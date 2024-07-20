package com.alsheuski.reflection.result.resolver.classpath;

//Works only for project which contains such class
public class DefaultClassPathProvider implements ClasspathProvider {
  @Override
  public String getClassPath() {
    return System.getProperty("java.class.path");
  }
}
