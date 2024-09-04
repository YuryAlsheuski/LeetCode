package com.alsheuski.reflection.result.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.tools.ToolProvider;

public class CompilerUtil {

  public static Path compile(String sourceFilePath, String outputDir, String classpath)
      throws IOException {

    var success = doCompile(sourceFilePath, outputDir, classpath);
    if (!success) {
      return null;
    }

    var classPath = getClassName(sourceFilePath);

    return Paths.get(outputDir, classPath + ".class");
  }

  private static boolean doCompile(String sourceFilePath, String outputDir, String classpath) {
    var compiler = ToolProvider.getSystemJavaCompiler();

    if (compiler == null) {
      System.err.println("No Java compiler available. Make sure you're using a JDK, not a JRE.");
      return false;
    }

    var options = new String[] {"-d", outputDir, "-classpath", classpath, "-g", sourceFilePath};

    var compilationResult = compiler.run(null, null, null, options);

    return compilationResult == 0;
  }

  private static String getClassName(String sourceFilePath) throws IOException {
    try (var reader = new BufferedReader(new FileReader(sourceFilePath))) {
      var line = "";
      var packageName = "";
      var className = "";

      while ((line = reader.readLine()) != null) {
        line = line.trim();

        if (line.startsWith("package ")) {
          packageName = line.substring(8, line.indexOf(';')).trim();
        } else if (line.startsWith("public class ") || line.startsWith("class ")) {
          className = line.split("\\s+")[2];
          break;
        }
      }

      if (className.isEmpty()) {
        throw new IllegalArgumentException("No class found in the provided source file.");
      }

      var packagePath = getPackagePath(packageName.split("\\."));
      return packagePath == null ? className : packagePath.resolve(className).toString();
    }
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
