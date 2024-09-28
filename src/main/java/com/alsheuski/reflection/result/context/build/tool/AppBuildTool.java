package com.alsheuski.reflection.result.context.build.tool;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.alsheuski.reflection.result.util.FileUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public abstract class AppBuildTool {

  private static AppBuildTool instance;
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

  public String getProjectSourceFilesPath() {
    return new SourceFolderResolver(projectRootDir.toFile()).getProjectSourceFilesPath();
  }

  public Path getProjectRootDir() {
    return projectRootDir;
  }

  public String getProjectEncoding() {
    var encoding = getEncoding();
    if (encoding != null) {
      return encoding;
    }
    return "UTF-8";
  }

  public abstract String getProjectClassPath();

  protected abstract String getEncoding();

  public static AppBuildTool getInstance(Path path) {
    if (instance == null) {
      synchronized (AppBuildTool.class) {
        if (instance == null) {
          instance = new BuildToolResolver().resolveToolType(path);
        }
      }
    }
    return instance;
  }

  private static class SourceFolderResolver {

    private final Set<String> folders = new HashSet<>();
    private String currentFolder = "-1";

    SourceFolderResolver(File folder) {
      findSourceFolders(folder);
    }

    public void findSourceFolders(File folder) {

      var files = folder.listFiles();
      if (files != null) {
        for (var file : files) {
          if (file.getAbsolutePath().startsWith(currentFolder)) {
            break;
          }
          if (file.isDirectory()) {
            findSourceFolders(file);
            continue;
          }
          var fileName = file.getName();
          if (!fileName.endsWith(".java")) {
            continue;
          }
          var filePath = file.toPath();
          var sourceRootFilePath = FileUtil.getSourceRootFilePath(filePath);
          if (sourceRootFilePath.isEmpty()) {
            continue;
          }
          var index = filePath.toString().indexOf(sourceRootFilePath.get().toString());
          if (index == -1) { // for cases when some .java files exists out of the source path
            continue;
          }
          var sourceFolderPath = Path.of(filePath.toString().substring(0, index));
          folders.add(sourceFolderPath.toString());
          currentFolder = sourceFolderPath.toString();
        }
      }
    }

    String getProjectSourceFilesPath() {
      return String.join(File.pathSeparator, folders);
    }
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
    private static final List<String> CONFIG_FILE_NAMES =
        Arrays.stream(BuildToolType.values()).map(tool -> tool.configFileName).collect(toList());

    private final String configFileName;
    private final Function<Path, AppBuildTool> factory;

    BuildToolType(String configFileName, Function<Path, AppBuildTool> factory) {
      this.configFileName = configFileName;
      this.factory = factory;
    }

    static List<String> getConfigFileNames() {
      return CONFIG_FILE_NAMES;
    }

    static BuildToolType get(String configFileName) {
      return TYPES.get(configFileName);
    }

    AppBuildTool getInstance(Path projectRootDir) {
      return factory.apply(projectRootDir);
    }
  }
}
