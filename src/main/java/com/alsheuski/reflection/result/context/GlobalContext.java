package com.alsheuski.reflection.result.context;

import com.alsheuski.reflection.result.context.build.tool.AppBuildTool;
import com.alsheuski.reflection.result.resolver.PathResolver;
import java.nio.file.Path;

public class GlobalContext {

  private final Path rootClassPath;
  private final AppBuildTool buildTool;
  private final Path projectRootDir;
  private final Path buildToolHome; // todo detect automatically in the future
  private final Path
      workDirectory; // todo set it automatically like temp folder hardcode and remove like auto
                     // closeable;

  public GlobalContext(String workDirectory, String rootClassPath, String buildToolHome) {
    this.rootClassPath = PathResolver.resolve(rootClassPath);
    this.buildToolHome = PathResolver.resolve(buildToolHome);
    this.workDirectory = PathResolver.resolve(workDirectory);
    buildTool = AppBuildTool.getInstance(this.rootClassPath);
    projectRootDir = buildTool.getProjectRootDir();
  }

  public Path getRootClassPath() {
    return rootClassPath;
  }

  public Path getProjectRootDir() {
    return projectRootDir;
  }

  public AppBuildTool getBuildTool() {
    return buildTool;
  }

  public Path getBuildToolHome() {
    return buildToolHome;
  }

  public Path getWorkDirectory() {
    return workDirectory;
  }

  public String getProjectClassPath() {
    return buildTool.resolveClasspath(this);
  }
}
