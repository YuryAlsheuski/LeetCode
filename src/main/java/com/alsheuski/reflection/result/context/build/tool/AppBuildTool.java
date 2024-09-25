package com.alsheuski.reflection.result.context.build.tool;

import com.alsheuski.reflection.result.context.GlobalContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

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

  public Path getProjectRootDir() {
    return projectRootDir;
  }

  public abstract String resolve(GlobalContext context);

  public static AppBuildTool getInstance(Path path) {
    if (INSTANCE == null) {
      synchronized (AppBuildTool.class) {
        if (INSTANCE == null) {
          INSTANCE = new BuildToolResolver().resolveToolType(path);
        }
      }
    }
    return INSTANCE;
  }

  private static class BuildToolResolver {
    private File configFile;

    private AppBuildTool resolveToolType(Path path) {
      resolveConfigFile(path);
      if (configFile == null) {
        return null;
      }

      return BuildToolType.get(configFile.getName()).getInstance(configFile.toPath().getParent());
    }

    private void resolveConfigFile(Path path) {
      if (path == null) {
        return;
      }
      var folder = path.toFile();
      if (!folder.exists() || folder.isFile()) {
        return;
      }
      var configFiles =
              folder.listFiles(f -> BuildToolType.getConfigFileNames().contains(f.getName()));
      if (configFiles != null && configFiles.length != 0) {
        configFile = configFiles[0];
      }
      resolveConfigFile(path.getParent());
    }
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
