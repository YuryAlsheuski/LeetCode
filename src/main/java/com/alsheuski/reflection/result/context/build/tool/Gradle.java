package com.alsheuski.reflection.result.context.build.tool;


import com.alsheuski.reflection.result.context.GlobalContext;

import java.nio.file.Path;

public class Gradle extends AppBuildTool {

   Gradle(Path projectRootDir) {
    super(projectRootDir);
  }

  @Override
  protected String resolve(GlobalContext context) {
    return "";
  }
}
