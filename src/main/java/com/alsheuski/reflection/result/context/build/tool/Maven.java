package com.alsheuski.reflection.result.context.build.tool;

import com.alsheuski.reflection.result.context.GlobalContext;
import java.io.IOException;
import java.nio.file.Path;

public class Maven extends AppBuildTool {

  Maven(Path projectRootDir) {
    super(projectRootDir);
  }

  @Override
  protected String resolve(GlobalContext context) throws IOException {
    var path = context.getBuildToolHome();
    try (var reader =
        runCommands(
            path.resolve("mvn").toString(),
            "dependency:build-classpath",
            "-DincludeScope=runtime")) {

      return reader
          .lines()
          .filter(line -> !line.trim().isEmpty())
          .filter(line -> !line.startsWith("["))
          .findFirst()
          .orElse(null);
    }
  }
}
