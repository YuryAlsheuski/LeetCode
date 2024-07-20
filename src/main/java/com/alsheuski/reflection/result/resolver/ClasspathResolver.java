package com.alsheuski.reflection.result.resolver;

import static com.alsheuski.reflection.result.resolver.PathResolver.resolvePath;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class ClasspathResolver {

  private ClasspathResolver() {}

  public static String resolve(String rootClassPath, String buildToolHome) {

    var root = resolvePath(rootClassPath);
    var buildToolHomePath = resolvePath(buildToolHome);
    var projectConfig = getBuildingTool(root, BuildingTool.configFileNames);
    var tool = projectConfig.getLeft();
    var projectFolder = projectConfig.getRight();

    try {
      ProcessBuilder processBuilder = new ProcessBuilder(tool.getCommands(buildToolHomePath));
      processBuilder.directory(projectFolder.toFile());
      processBuilder.redirectErrorStream(true);

      Process process = processBuilder.start();

      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;

      while ((line = reader.readLine()) != null) {
        if (line.startsWith("[")) {
          continue;
        }
        return rootClassPath + ":" + line.trim();
      }
      reader.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private static Pair<BuildingTool, Path> getBuildingTool(Path root, List<String> configFileNames) {
    var folder = root.toFile();
    if (!folder.exists() || folder.isFile()) {
      return null;
    }
    var configFile = folder.listFiles(f -> configFileNames.contains(f.getName()));
    if (configFile != null && configFile.length != 0) {

      return new ImmutablePair<>(BuildingTool.getBuildingTool(configFile[0].getName()), root);
    }
    return getBuildingTool(root.getParent(), configFileNames);
  }

  private enum BuildingTool {
    MAVEN(
        "pom.xml",
        path ->
            new String[] {
              path.resolve("mvn").toString(), "dependency:build-classpath", "-DincludeScope=runtime"
            }),
    GRADLE(
        "build.gradle", path -> new String[] {path.resolve("gradle").toString(), "printClasspath"});

    private static final Map<String, BuildingTool> BUILDING_TOOLS =
        Arrays.stream(BuildingTool.values()).collect(toMap(k -> k.configFileName, v -> v));

    private static final List<String> configFileNames =
        Arrays.stream(BuildingTool.values()).map(tool -> tool.configFileName).collect(toList());

    private final String configFileName;
    private final Function<Path, String[]> commandsProvider;

    BuildingTool(String configFileName, Function<Path, String[]> commandsProvider) {
      this.configFileName = configFileName;
      this.commandsProvider = commandsProvider;
    }

    public String getConfigFileName() {
      return configFileName;
    }

    public String[] getCommands(Path path) {
      return commandsProvider.apply(path);
    }

    public static BuildingTool getBuildingTool(String configFileName) {
      return BUILDING_TOOLS.get(configFileName);
    }

    public List<String> getConfigFileNames() {
      return configFileNames;
    }
  }
}
