package com.alsheuski.reflection.result.resolver;

import static com.alsheuski.reflection.result.resolver.classpath.GradleClasspathProvider.GRADLE_CONFIG_NAME;
import static com.alsheuski.reflection.result.resolver.classpath.MavenClasspathProvider.MAVEN_CONFIG_NAME;

import com.alsheuski.reflection.result.resolver.classpath.GradleClasspathProvider;
import com.alsheuski.reflection.result.resolver.classpath.MavenClasspathProvider;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class PathResolver {
  private PathResolver() {}

  public static Path resolvePath(String path, String... parts) {
    var systemIndependentPath = path.replace("\\", File.separator).replace("/", File.separator);
    return Paths.get(systemIndependentPath, parts).toAbsolutePath().normalize();
  }

  public static String resolveClassPath(String rootClassPath, String buildToolHome) {
    var supportedConfigs = List.of(GRADLE_CONFIG_NAME, MAVEN_CONFIG_NAME);
    var root = resolvePath(rootClassPath);
    var buildToolHomePath = resolvePath(buildToolHome);
    var configToPath = findConfigFilePath(root, supportedConfigs);
    var configFileName = configToPath.getLeft();
    var projectDir = configToPath.getRight();
    if (GRADLE_CONFIG_NAME.equals(configFileName)) {
      return new GradleClasspathProvider().getClassPath();
    }
    var externalDepsClasspath =
        new MavenClasspathProvider(buildToolHomePath, projectDir).getClassPath();
    return rootClassPath + ":" + externalDepsClasspath;
  }

  private static Pair<String, Path> findConfigFilePath(Path root, List<String> configFileNames) {
    var folder = root.toFile();
    if (!folder.exists() || folder.isFile()) {
      return null;
    }
    var configFile = folder.listFiles(f -> configFileNames.contains(f.getName()));
    if (configFile != null && configFile.length != 0) {

      return new ImmutablePair<>(configFile[0].getName(), root);
    }
    return findConfigFilePath(root.getParent(), configFileNames);
  }
}
