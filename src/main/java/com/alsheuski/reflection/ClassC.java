package com.alsheuski.reflection;

public class ClassC {
  public String getCClassLabel() {
    return "from C class";
  }

  protected void doNotCall() {
    new Common();
    new ClassB("");
    new ClassA().getLabel();
  }
}
