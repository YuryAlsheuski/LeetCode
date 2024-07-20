package com.alsheuski.reflection.result.resolver;


import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathResolver {
  private PathResolver() {}

  public static Path resolvePath(String path, String... parts) {
    var systemIndependentPath = path.replace("\\", File.separator).replace("/", File.separator);
    return Paths.get(systemIndependentPath, parts).toAbsolutePath().normalize();
  }
}
