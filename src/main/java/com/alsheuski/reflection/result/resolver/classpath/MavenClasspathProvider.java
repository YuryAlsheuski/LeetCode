package com.alsheuski.reflection.result.resolver.classpath;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class MavenClasspathProvider implements ClasspathProvider {

  public static final String MAVEN_CONFIG_NAME = "pom.xml";
  private final Path buildToolHome;
  private final Path projectRoot;

  public MavenClasspathProvider(Path buildToolHome, Path projectRoot) {
    this.buildToolHome = buildToolHome;
    this.projectRoot = projectRoot;
  }

  @Override
  public String getClassPath() {
    Path path = buildToolHome.resolve("mvn");
    try {
      ProcessBuilder processBuilder =
          new ProcessBuilder(
              path.toString(), "dependency:build-classpath", "-DincludeScope=runtime");

      processBuilder.directory(projectRoot.toFile());
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
