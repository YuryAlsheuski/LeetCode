package com.alsheuski.reflection.result.model;

import org.objectweb.asm.Type;

import java.util.List;
import java.util.Objects;

public class Method {

  private final List<Argument> args;
  private final Type returnType;
  private final String name;

  public Method(Type returnType, String name, List<Argument> args) {
    this.returnType = returnType;
    this.name = name;
    this.args = args;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Method method = (Method) o;
    return Objects.equals(args, method.args) && Objects.equals(returnType, method.returnType) && Objects.equals(name, method.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(args, returnType, name);
  }
}
