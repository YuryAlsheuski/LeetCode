package com.alsheuski.reflection.result.preprocessor.classpath;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

//NOT WORKS - NEEDS REFACTORING AND TESTING LIKE FOR MavenClasspathProvider
//USE DefaultClasspathProvider if it possible

public class GradleClasspathProvider implements ClasspathProvider{

  @Override
  public String getClassPath() {
    try {
      // Command to get the classpath using Gradle
      ProcessBuilder processBuilder = new ProcessBuilder("gradle", "printClasspath");
      processBuilder.redirectErrorStream(true);

      // Start the process
      Process process = processBuilder.start();

      // Capture the output
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;
      List<String> classpathEntries = new ArrayList<>();

      while ((line = reader.readLine()) != null) {
        if (line.contains("Classpath:")) {
          classpathEntries.add(line.substring(line.indexOf("Classpath:") + 10).trim());
        }
      }
      reader.close();

      // Combine the classpath entries into a single string
      String classpath = String.join(File.pathSeparator, classpathEntries);
      System.out.println("Classpath: " + classpath);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }
}
