package com.alsheuski.reflection.result.preprocessor.classpath;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathResolver {
  private PathResolver() {}

  public static Path resolve(String path, String... parts) {
    String systemIndependentPath = path.replace("\\", File.separator).replace("/", File.separator);
    return Paths.get(systemIndependentPath, parts).toAbsolutePath().normalize();
  }
}
