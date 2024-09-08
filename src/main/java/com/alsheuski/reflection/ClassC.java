package com.alsheuski.reflection;

public class ClassC {
  public String getCClassLabel() {
    return "from C class";
  }

  protected void doNotCall() {
    new Common("drerf");
    new ClassB("");
    new ClassA().getLabel();
  }
}
