package com.alsheuski.reflection.result.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class FileUtil {

  private static final Pattern CLASS_NAME_PATTERN =
      Pattern.compile(
          "(?:public\\s+)?(?:abstract\\s+)?(?:class|enum|interface|@interface)\\s+(\\w+)");

  private FileUtil() {}

  public static Path resolvePath(String path, String... parts) {
    var systemIndependentPath = path.replace("\\", File.separator).replace("/", File.separator);
    return Paths.get(systemIndependentPath, parts).toAbsolutePath().normalize();
  }

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
        throw new IllegalArgumentException("No class found in the provided source file.");
      }

      var packagePath = getPackagePath(packageName.split("\\."));
      return packagePath == null ? Path.of(className) : packagePath.resolve(className);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
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
