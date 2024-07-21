package com.alsheuski.reflection.result.context.build.tool;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.alsheuski.reflection.result.context.GlobalContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class AppBuildTool {

  private static AppBuildTool INSTANCE;
  private final Path projectRootDir;

  protected AppBuildTool(Path projectRootDir) {
    this.projectRootDir = projectRootDir;
  }

  protected BufferedReader runCommands(String... commands) {
    try {
      var processBuilder = new ProcessBuilder(commands);
      processBuilder.directory(projectRootDir.toFile());
      processBuilder.redirectErrorStream(true);

      var process = processBuilder.start();

      return new BufferedReader(new InputStreamReader(process.getInputStream()));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public String resolveClasspath(GlobalContext context) {
    try {
      return context.getRootClassPath() + File.pathSeparator + resolve(context);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public Path getProjectRootDir() {
    return projectRootDir;
  }

  protected abstract String resolve(GlobalContext context) throws IOException;

  public static AppBuildTool getInstance(Path rootClassPath) {
    if (INSTANCE == null) {
      synchronized (AppBuildTool.class) {
        if (INSTANCE == null) {
          INSTANCE = resolveToolType(rootClassPath);
        }
      }
    }
    return INSTANCE;
  }

  private static AppBuildTool resolveToolType(Path path) {
    var folder = path.toFile();
    if (!folder.exists() || folder.isFile()) {
      return null;
    }
    var configFile =
        folder.listFiles(f -> BuildToolType.getConfigFileNames().contains(f.getName()));
    if (configFile != null && configFile.length != 0) {
      return BuildToolType.get(configFile[0].getName()).getInstance(path);
    }
    return resolveToolType(path.getParent());
  }

  private enum BuildToolType {
    MAVEN("pom.xml", Maven::new),
    GRADLE("build.gradle", Gradle::new);

    private static final Map<String, BuildToolType> TYPES =
        Arrays.stream(BuildToolType.values()).collect(toMap(k -> k.configFileName, v -> v));
    private static final List<String> configFileNames =
        Arrays.stream(BuildToolType.values()).map(tool -> tool.configFileName).collect(toList());

    private final String configFileName;
    private final Function<Path, AppBuildTool> factory;

    BuildToolType(String configFileName, Function<Path, AppBuildTool> factory) {
      this.configFileName = configFileName;
      this.factory = factory;
    }

    static List<String> getConfigFileNames() {
      return configFileNames;
    }

    static BuildToolType get(String configFileName) {
      return TYPES.get(configFileName);
    }

    AppBuildTool getInstance(Path projectRootDir) {
      return factory.apply(projectRootDir);
    }
  }
}
