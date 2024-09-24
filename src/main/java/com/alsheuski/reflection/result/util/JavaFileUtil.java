package com.alsheuski.reflection.result.util;

import static com.alsheuski.reflection.result.util.LoaderUtil.loadClass;

import com.alsheuski.reflection.result.context.GlobalContext;
import com.alsheuski.reflection.result.preprocessor.replacer.JavaFileContentReplacer;
import com.alsheuski.reflection.result.preprocessor.replacer.RealTypeVisitorProvider;
import com.alsheuski.reflection.result.preprocessor.replacer.SuperclassMethodPrefixVisitorProvider;
import com.alsheuski.reflection.result.preprocessor.replacer.VarTypeVisitorProvider;
import com.alsheuski.reflection.result.visitor.FieldTypeClassVisitor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class JavaFileUtil {

  private static final Pattern CLASS_NAME_PATTERN =
      Pattern.compile("(?:public\\s+)?(?:abstract\\s+)?class\\s+(\\w+)");

  private JavaFileUtil() {}

  public static Path getSourceRootFilePath(Path sourceFilePath) {
    try (var reader = new BufferedReader(new FileReader(sourceFilePath.toFile()))) {
      var line = "";
      var packageName = "";
      var className = "";

      while ((line = reader.readLine()) != null) {
        line = line.trim();

        if (line.startsWith("package ")) {
          packageName = line.substring(8, line.indexOf(';')).trim();
          continue;
        }
        if (line.startsWith("class ") || line.contains(" class ")) {
          var matcher = CLASS_NAME_PATTERN.matcher(line);
          if (matcher.find()) {
            className = matcher.group(1);
          }
          break;
        }
      }

      if (className.isEmpty()) {
        throw new IllegalArgumentException("No class found in the provided source file.");
      }

      var packagePath = getPackagePath(packageName.split("\\."));
      return packagePath == null ? Path.of(className) : packagePath.resolve(className);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // todo CHECK IF POSSIBLE- for future will be better to process class once.
  public static String removeVarTypes(String pathToJavaFile, String pathToCompiledClass)
      throws IOException {

    var asmVisitor = new FieldTypeClassVisitor(pathToCompiledClass);
    loadClass(pathToCompiledClass, asmVisitor);

    var jdtVisitorProvider = new RealTypeVisitorProvider(asmVisitor.getRowNumbersMap());
    var content = JavaFileContentReplacer.replace(pathToJavaFile, jdtVisitorProvider);

    writeToFile(Path.of(pathToJavaFile).toFile(), content);

    return content;
  }

  public static String setSuperPrefix(String pathToJavaFile, GlobalContext context)
      throws IOException {

    var jdtVisitorProvider = new SuperclassMethodPrefixVisitorProvider(context);
    var content = JavaFileContentReplacer.replace(pathToJavaFile, jdtVisitorProvider);
    writeToFile(Path.of(pathToJavaFile).toFile(), content);

    return content;
  }

  public static Path simplifyJavaFileTypes(GlobalContext context) throws IOException {

    var pathToJavaFile = context.getFilePath();
    var content =
        JavaFileContentReplacer.replace(pathToJavaFile.toString(), new VarTypeVisitorProvider());
    var newJavaFilePath = context.getWorkDirectory().resolve(pathToJavaFile.getFileName());

    writeToFile(newJavaFilePath.toFile(), content);

    return newJavaFilePath;
  }

  private static void writeToFile(File file, String content) throws IOException {
    if (!file.exists()) {
      file.createNewFile();
    }
    var writer = new FileWriter(file);
    writer.write(content);
    writer.close();
  }

  private static Path getPackagePath(String[] packageFolders) {
    if (packageFolders == null || packageFolders.length == 0) {
      return null;
    }

    var path = Paths.get(packageFolders[0]);
    for (int i = 1; i < packageFolders.length; i++) {
      path = path.resolve(packageFolders[i]);
    }
    return path;
  }
}
