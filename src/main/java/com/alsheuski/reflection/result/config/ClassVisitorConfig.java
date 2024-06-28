package com.alsheuski.reflection.result.config;

import java.util.function.Predicate;

public class ClassVisitorConfig {
  private final String className;
  private final Predicate<Integer> accessFilter;

  public ClassVisitorConfig(String className, Predicate<Integer> accessFilter) {
    this.className = className;
    this.accessFilter = accessFilter;
  }

  public String getClassName() {
    return className;
  }

  public Predicate<Integer> getAccessFilter() {
    return accessFilter;
  }
}
