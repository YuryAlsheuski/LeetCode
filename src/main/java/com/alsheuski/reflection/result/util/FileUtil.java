package com.alsheuski.reflection.result.util;

import static com.alsheuski.reflection.result.util.LoaderUtil.loadClass;
import static org.objectweb.asm.Opcodes.ASM9;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.objectweb.asm.ClassVisitor;

public class FileUtil {

  private static final Pattern CLASS_NAME_PATTERN =
      Pattern.compile(
          "(?:public\\s+)?(?:abstract\\s+)?(?:class|enum|interface|@interface)\\s+(\\w+)");

  private FileUtil() {}

  public static Path resolvePath(String path, String... parts) {
    var systemIndependentPath = path.replace("\\", File.separator).replace("/", File.separator);
    return Paths.get(systemIndependentPath, parts).toAbsolutePath().normalize();
  }

  public static Optional<Path> getSourceRootJavaFilePath(Path sourceFilePath) {
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
        if (line.startsWith("class ")
            || line.contains(" class ")
            || line.startsWith("enum ")
            || line.contains(" enum ")
            || line.startsWith("interface ")
            || line.contains(" interface ")
            || line.startsWith("@interface ")
            || line.contains(" @interface ")) {
          var matcher = CLASS_NAME_PATTERN.matcher(line);
          if (matcher.find()) {
            className = matcher.group(1);
          }
          break;
        }
      }

      if (className.isEmpty()) {
        System.err.println(
            "Wrong class content:\n" + reader.lines().collect(Collectors.joining("\n")));
        return Optional.empty();
      }

      var packagePath = getPackagePath(packageName.split("\\."));
      var result = packagePath == null ? Path.of(className) : packagePath.resolve(className);
      return Optional.of(result);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static Optional<Path> getSourceRootClassFilePath(Path path) {
    final Path[] result = {Path.of("")};
    var visitor =
        new ClassVisitor(ASM9) {
          @Override
          public void visit(
              int version,
              int access,
              String name,
              String signature,
              String superName,
              String[] interfaces) {
            result[0] = result[0].resolve(name + ".class");
          }
        };
    loadClass(path, visitor);
    return Optional.of(result[0]);
  }

  public static void writeToFile(Path filePath, String content) throws IOException {
    var file = filePath.toFile();
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

  public static String readFileToString(Path path) throws IOException {
    var reader = new BufferedReader(new FileReader(path.toFile()));
    var stringBuilder = new StringBuilder();
    var line = "";
    while ((line = reader.readLine()) != null) {
      stringBuilder.append(line).append("\n");
    }
    reader.close();
    return stringBuilder.toString();
  }
}
