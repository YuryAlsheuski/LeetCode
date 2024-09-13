package com.alsheuski.reflection.result.model;

import static com.alsheuski.reflection.result.util.LoaderUtil.getClassName;
import static java.util.stream.Collectors.toList;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class MetaClass {

  private final Set<Method> methods;
  private final String name;
  private final String fullName;
  private String signature;

  public MetaClass(String fullName) {
    this(fullName, new HashSet<>());
  }

  public MetaClass(String fullName, Set<Method> methods) {
    this.fullName = fullName;
    this.name = getClassName(fullName).replace("$", ".");
    this.methods = methods;
  }

  public Set<Method> getMethods() {
    return methods;
  }

  public String getFullName() {
    return fullName;
  }

  public void addMethod(Method method) {
    methods.add(method);
  }

  public void addMethods(Set<Method> methods) {
    this.methods.addAll(methods);
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

  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public String getName() {
    return name;
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
