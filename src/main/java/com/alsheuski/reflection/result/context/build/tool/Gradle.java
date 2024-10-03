package com.alsheuski.reflection.result.context.build.tool;

import static java.util.stream.Collectors.toSet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Gradle extends AppBuildTool {

  private static final String DELIMITER = File.pathSeparator;
  private final File scriptFile;
  private final File gradlewFile;

  Gradle(Path projectRootDir) {
    super(projectRootDir);
    scriptFile = createTaskScript();
    gradlewFile = projectRootDir.resolve("gradlew.bat").toFile();
  }

  // todo support not wrapper only in the future
  @Override
  public String getProjectClassPath() {
    try (var reader =
        runCommands(gradlewFile.toString(), "-I", scriptFile.toString(), "getClasspath")) {
      return String.join(
          DELIMITER,
          reader
              .lines()
              .filter(line -> line.contains(DELIMITER))
              .flatMap(line -> Arrays.stream(line.split(DELIMITER)))
              .filter(path -> !isClassPath(path))
              .collect(toSet()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getEncoding() {
    try (var reader =
        runCommands(gradlewFile.toString(), "-I", scriptFile.toString(), "getEncoding")) {
      return reader
          .lines()
          .filter(line -> line.startsWith("#"))
          .map(line -> line.replace("#", ""))
          .findFirst()
          .orElse(null);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // todo here and for other places needs to handle errors correctly
  // todo copy file with content from resources in the future
  // todo chrck other encoding cases. Here it is only compileJavaTask
  private File createTaskScript() {
    try {
      var tempDir = Files.createTempDirectory("gradle");
      var scriptFile = tempDir.resolve("init.gradle").toFile();
      try (var myWriter = new FileWriter(scriptFile)) {
        scriptFile.createNewFile();
        myWriter.write(
            "allprojects {\n"
                + "    task getClasspath {\n"
                + "        doLast {\n"
                + "            println sourceSets.main.runtimeClasspath.asPath\n"
                + "        }\n"
                + "    }\n"
                + "\n"
                + "    task getEncoding {\n"
                + " doLast {\n"
                + "        def compileJavaTask = tasks.findByName('compileJava')\n"
                + "        if (compileJavaTask) {\n"
                + "            println \"#${compileJavaTask.options.encoding}\"\n"
                + "        }\n"
                + "    }\n"
                + "    }\n"
                + "}");
        return scriptFile;
      }
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }
  }
}
