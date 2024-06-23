package com.alsheuski.reflection.result.model;

import java.util.List;
import java.util.Objects;

public class Constructor {
  private final List<Argument> args;
  private boolean tagged;

  public Constructor(List<Argument> args) {
    this.args = args;
  }

  public List<Argument> getArgs() {
    return args;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Constructor that = (Constructor) o;
    return Objects.equals(args, that.args);
  }

  public boolean isTagged() {
    return tagged;
  }

  public void setTagged(boolean tagged) {
    this.tagged = tagged;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(args);
  }
}
