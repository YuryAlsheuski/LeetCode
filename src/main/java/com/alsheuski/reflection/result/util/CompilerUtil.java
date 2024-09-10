package com.alsheuski.reflection.result.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.tools.ToolProvider;

public class CompilerUtil {

  public static CompiledClassPath compile(String sourceFilePath, String outputDir, String classpath)
      throws IOException {

    doCompile(sourceFilePath, outputDir, classpath);

    var sourceRootPath = sourceRootPath(sourceFilePath);
    var absolutePath = Paths.get(outputDir, sourceRootPath + ".class");

    return new CompiledClassPath(absolutePath, sourceRootPath);
  }

  private static void doCompile(String sourceFilePath, String outputDir, String classpath) {
    var compiler = ToolProvider.getSystemJavaCompiler();

    if (compiler == null) {
      throw new RuntimeException(
          "No Java compiler available. Make sure you're using a JDK, not a JRE.");
    }

    var options = new String[] {"-d", outputDir, "-classpath", classpath, "-g", sourceFilePath};

    var compilationResult = compiler.run(null, null, null, options);
    if (compilationResult != 0) {
      throw new RuntimeException("Compilation failed.");
    }
  }

  private static Path sourceRootPath(String sourceFilePath) throws IOException {
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
      return packagePath == null ? Path.of(className) : packagePath.resolve(className);
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

  public static class CompiledClassPath {

    private final Path absolutePath;
    private final Path sourceRootPath;

    public CompiledClassPath(Path absolutePath, Path sourceRootPath) {
      this.absolutePath = absolutePath;
      this.sourceRootPath = sourceRootPath;
    }

    public Path getAbsolutePath() {
      return absolutePath;
    }

    public Path getSourceRootPath() {
      return sourceRootPath;
    }
  }
}
