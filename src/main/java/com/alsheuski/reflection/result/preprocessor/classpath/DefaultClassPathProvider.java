package com.alsheuski.reflection.result.preprocessor.classpath;

public class DefaultClassPathProvider implements ClasspathProvider {
  @Override
  public String getClassPath() {
    return System.getProperty("java.class.path");
  }
}
