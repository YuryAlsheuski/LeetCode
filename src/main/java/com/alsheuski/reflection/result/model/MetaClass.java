package com.alsheuski.reflection.result.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MetaClass {

  private final List<Method> methods;
  private final String name;
  private final String fullName;

  public String getName() {
    return name;
  }

  public MetaClass(String fullName) {
    this(fullName, new ArrayList<>());
  }

  public MetaClass(String fullName, List<Method> methods) {
    this.fullName = fullName;
    var parts = fullName.split("/");
    this.name = parts[parts.length - 1];
    this.methods = methods;
  }

  public List<Method> getMethods() {
    return methods;
  }

  public String getFullName() {
    return fullName;
  }

  public void addMethod(Method method) {
    methods.add(method);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MetaClass that = (MetaClass) o;
    return Objects.equals(fullName, that.fullName);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(fullName);
  }
}
