package com.alsheuski.reflection.result.preprocessor;

import static com.alsheuski.reflection.result.util.LoaderUtil.loadClass;

import com.alsheuski.reflection.result.context.GlobalContext;
import com.alsheuski.reflection.result.resolver.PathResolver;
import com.alsheuski.reflection.result.visitor.FieldTypeClassVisitor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class JavaFilePreprocessor {

  private JavaFilePreprocessor() {}

  // todo CHECK IF POSSIBLE- for future will be better to process class once. The main idea to have
  // one class loader which can load all necessary class data once
  public static String removeVarTypes(String pathToJavaFile, String pathToCompiledClass)
      throws IOException {

    var visitor = new FieldTypeClassVisitor(pathToCompiledClass);
    loadClass(pathToCompiledClass, visitor);

    return new JavaFileTypeReplacer().replaceVarTypes(pathToJavaFile, visitor.getRowNumbersMap());
  }

  public static Path simplifyJavaFileTypes(String pathToJavaFile, GlobalContext context)
      throws IOException {

    var javaFilePath = PathResolver.resolve(pathToJavaFile);
    var content = new JavaFileTypeReplacer().replaceTypesToVar(javaFilePath.toString());
    var newJavaFilePath = context.getWorkDirectory().resolve(javaFilePath.getFileName());
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
}
