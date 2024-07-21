package com.alsheuski.reflection.result.context.build.tool;

import com.alsheuski.reflection.result.context.GlobalContext;
import java.io.IOException;
import java.nio.file.Path;

public class Maven extends AppBuildTool {

  Maven(Path projectRootDir) {
    super(projectRootDir);
  }

  @Override
  protected String resolve(GlobalContext context) {
    var path = context.getBuildToolHome();
    try (var reader =
        runCommands(
            path.resolve("mvn").toString(),
            "dependency:build-classpath",
            "-DincludeScope=runtime")) {

      String line;

      while ((line = reader.readLine()) != null) {
        if (line.startsWith("[")) {
          continue;
        }
        return line.trim();
      }
    } catch (IOException e) {

    }
    return null;
  }
}
