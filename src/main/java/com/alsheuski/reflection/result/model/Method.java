package com.alsheuski.reflection.result.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.objectweb.asm.Opcodes;

public class Method {

  private final int access;
  private final String descriptor;
  private final List<Argument> args;
  private final String name;
  private final boolean isConstructor;
  private  ResultType returnType;
  private List<String> calledFrom;

  public Method(
      int access,
      String descriptor,
      ResultType returnType,
      String name,
      boolean isConstructor) { // todo migrate to MethodNode

    this.access = access;
    this.descriptor = descriptor;
    this.returnType = returnType;
    this.name = name;
    this.isConstructor = isConstructor;
    args = new ArrayList<>();
    calledFrom = new ArrayList<>();
  }

  public List<Argument> getArgs() {
    return args;
  }

  public void setReturnType(ResultType returnType) {
    this.returnType = returnType;
  }

  public ResultType getReturnType() {
    return returnType;
  }

  public String getName() {
    return name;
  }

  public void addArgument(Argument argument) {
    args.add(argument);
  }

  public void addCallFromClass(String className) {
    calledFrom.add(className);
  }

  public boolean isCalledFrom(String className) {
    return calledFrom.contains(className);
  }

  public boolean isConstructor() {
    return isConstructor;
  }

  public String getDescriptor() {
    return descriptor;
  }

  public boolean isStatic() {
    return (access & Opcodes.ACC_STATIC) != 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Method method = (Method) o;
    return isConstructor == method.isConstructor
        && Objects.equals(descriptor, method.descriptor)
        && Objects.equals(name, method.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(descriptor, name, isConstructor);
  }
}
