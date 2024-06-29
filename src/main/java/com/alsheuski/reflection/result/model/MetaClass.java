package com.alsheuski.reflection.result.model;

import static com.alsheuski.reflection.result.util.LoaderUtil.getClassName;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    this.name = getClassName(fullName);
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

  public Optional<Method> findMethod(String descriptor, String methodName) {
    return methods.stream()
        .filter(
            method ->
                method.getDescriptor().equals(descriptor) && method.getName().equals(methodName))
        .findFirst();
  }

  public List<Method> getCalledWith(String className) {
    return methods.stream().filter(method -> method.isCalledFrom(className)).collect(toList());
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
