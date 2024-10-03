package com.alsheuski.reflection.result.context.build.tool;

import static java.util.stream.Collectors.joining;

import com.alsheuski.reflection.result.util.FileUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public class Maven extends AppBuildTool {
  // todo - hardcode! check dynamically in the future
  private static final Path HOME =
      FileUtil.resolvePath("/Applications/IntelliJ IDEA.app/Contents/plugins/maven/lib/maven3/bin");

  Maven(Path projectRootDir) {
    super(projectRootDir);
  }

  @Override
  public String getProjectClassPath() {
    try (var reader =
        runCommands(
            HOME.resolve("mvn").toString(),
            "dependency:build-classpath",
            "-DincludeScope=runtime")) {

      var classPathString =
          reader
              .lines()
              .filter(line -> !line.trim().isEmpty())
              .filter(line -> !line.startsWith("["))
              .findFirst()
              .orElse(null);

      if (classPathString == null) {
        return null;
      }

      return Arrays.stream(classPathString.split(File.pathSeparator))
          .filter(path -> !isClassPath(path))
          .collect(joining(File.pathSeparator));

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getEncoding() {
    return null;
  }
}
