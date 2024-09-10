package com.alsheuski.reflection.result.config;

import java.util.function.Predicate;

public class ClassVisitorConfig {

  private final String sourceRootPath;
  private final Predicate<Integer> accessIdentifierFilter;

  public ClassVisitorConfig(String sourceRootPath, Predicate<Integer> accessIdentifierFilter) {
    this.sourceRootPath = sourceRootPath;
    this.accessIdentifierFilter = accessIdentifierFilter;
  }

  public String getSourceRootPath() {
    return sourceRootPath;
  }

  public Predicate<Integer> getAccessIdentifierFilter() {
    return accessIdentifierFilter;
  }
}
