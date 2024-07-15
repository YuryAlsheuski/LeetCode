package com.alsheuski.reflection.result.config;

import java.util.function.Predicate;

public class ClassVisitorConfig {

  private final String className;
  private final Predicate<Integer> accessIdentifierFilter;

  public ClassVisitorConfig(String className, Predicate<Integer> accessIdentifierFilter) {
    this.className = className;
    this.accessIdentifierFilter = accessIdentifierFilter;
  }

  public String getClassName() {
    return className;
  }

  public Predicate<Integer> getAccessIdentifierFilter() {
    return accessIdentifierFilter;
  }
}
