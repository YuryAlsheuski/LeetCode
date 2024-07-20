package com.alsheuski.reflection.result.preprocessor.classpath;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class MavenClasspathProvider implements ClasspathProvider {

  private final String buildToolHome;

  public MavenClasspathProvider(String buildToolHome) {
    this.buildToolHome = buildToolHome;
  }

  @Override
  public String getClassPath() {
    Path path = PathResolver.resolve(buildToolHome, "mvn");
    try {
      ProcessBuilder processBuilder =
          new ProcessBuilder(
              path.toString(), "dependency:build-classpath", "-DincludeScope=runtime");
      processBuilder.redirectErrorStream(true);

      Process process = processBuilder.start();

      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;

      while ((line = reader.readLine()) != null) {
        if (line.startsWith("[")) {
          continue;
        }
        return line.trim();
      }
      reader.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
