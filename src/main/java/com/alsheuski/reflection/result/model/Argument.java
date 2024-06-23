package com.alsheuski.reflection.result.model;

import org.objectweb.asm.Type;

import java.util.Objects;

public class Argument {
  private final Type type;
  private final String name;

  public Argument(Type type, String name) {
    this.type = type;
    this.name = name;
  }

  public Type getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Argument argument = (Argument) o;
    return Objects.equals(type, argument.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type);
  }
}
