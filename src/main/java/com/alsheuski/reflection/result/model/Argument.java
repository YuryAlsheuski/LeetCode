package com.alsheuski.reflection.result.model;

import java.util.Objects;

public class Argument {
  private final ResultType type;
  private final String name;

  public Argument(ResultType type, String name) {
    this.type = type;
    this.name = name;
  }

  public ResultType getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name.replace("$", "."); // replacing here for inner classes
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
