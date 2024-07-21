package com.alsheuski.reflection.result.context.build.tool;

import com.alsheuski.reflection.result.context.GlobalContext;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class Gradle extends AppBuildTool {

  Gradle(Path projectRootDir) {
    super(projectRootDir);
  }

  // todo support not wrapper only in the future
  @Override
  protected String resolve(GlobalContext context) throws IOException {
    var scriptFile = createClasspathTask(context.getWorkDirectory());
    try(var reader = runCommands("./gradlew", "-I", scriptFile.toString(), "printClasspath")){
      var lines = reader.lines().collect(Collectors.toList());
      for (String line : lines) {
        System.err.println(line);
      }

      return "";
    }
  }

  // todo here and for other places needs to handle errors correctly
  // todo copy file with content from resources in the future
  private File createClasspathTask(Path workDir) {
    var scriptFile = new File(workDir.toFile(), "init.gradle");
    try (var myWriter = new FileWriter(scriptFile)) {
      scriptFile.createNewFile();
      myWriter.write(
          "allprojects {\n"
              + "    task printClasspath {\n"
              + "        doLast {\n"
              + "            println sourceSets.main.runtimeClasspath.asPath\n"
              + "        }\n"
              + "    }\n"
              + "}");
      return scriptFile;
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }
  }
}
