package com.alsheuski.reflection.result.context;

import com.alsheuski.reflection.result.context.build.tool.AppBuildTool;
import com.alsheuski.reflection.result.util.FileUtil;
import java.nio.file.Path;

public class GlobalContext {

  private final AppBuildTool buildTool;
  private final Path projectRootDir;
  private final Path sourceRootFilePath;
  private final Path
      workDirectory; // todo set it automatically like temp folder hardcode and remove like auto
  // closeable;
  private final Path filePath;

  public GlobalContext(String pathToJavaFile, String workDirectory) {
    this.workDirectory = FileUtil.resolvePath(workDirectory);
    filePath = FileUtil.resolvePath(pathToJavaFile);
    sourceRootFilePath = FileUtil.getSourceRootFilePath(filePath);
    buildTool = AppBuildTool.getInstance(filePath.getParent());
    projectRootDir = buildTool.getProjectRootDir();
    createDirs();
  }

  public Path getProjectRootDir() {
    return projectRootDir;
  }

  public Path getWorkDirectory() {
    return workDirectory;
  }

  public String getProjectClassPath() {
    return buildTool.resolve(this);
  }

  private void createDirs() {
    var workDirFolder = workDirectory.toFile();
    if (!workDirFolder.exists()) {
      workDirFolder.mkdir();
    }
  }

  public String getProjectSourceFilesPath() {
    return buildTool.getProjectSourceFilesPath();
  }

  public Path getSourceRootFilePath() {
    return sourceRootFilePath;
  }

  public Path getFilePath() {
    return filePath;
  }
}
