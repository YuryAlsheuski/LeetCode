package com.alsheuski.reflection;

public class ClassB {

  private final String name;

  public ClassB(String name) {
    this.name = name;
  }

  public String getName(int test) {
    return String.valueOf(test);
  }
}
