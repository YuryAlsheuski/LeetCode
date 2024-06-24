package com.alsheuski.reflection.result.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.objectweb.asm.Type;

public class Method {

  private final List<Argument> args;
  private final Type returnType;
  private final String name;
  private final boolean isConstructor;
  private final List<String> calledFrom;

  public Method(Type returnType, String name, boolean isConstructor) {
    this.returnType = returnType;
    this.name = name;
    this.isConstructor = isConstructor;
    args = new ArrayList<>();
    calledFrom = new ArrayList<>();
  }

  public List<Argument> getArgs() {
    return args;
  }

  public Type getReturnType() {
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

  public boolean getIsConstructor() {
    return isConstructor;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Method method = (Method) o;
    return isConstructor == method.isConstructor
        && Objects.equals(args, method.args)
        && Objects.equals(returnType, method.returnType)
        && Objects.equals(name, method.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(args, returnType, name, isConstructor);
  }
}
