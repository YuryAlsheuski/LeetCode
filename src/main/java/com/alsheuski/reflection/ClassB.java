package com.alsheuski.reflection;


import java.util.List;

public class ClassB {

  private final String name;

  public ClassB(String name) {
    this.name = name;
  }

  protected ClassB(String name, String newone) {
    this.name = name;
  }

  ClassB(String name, String newone, String threenewaone) {
    this.name = name;
  }

  private ClassB() {
    name = "";
  }

  public List<String> getName(int test, List<ClassC> cLink) {
    return  List.of(test + new ClassC().getCClassLabel());
  }

  private String donotprint() {
    return "not print";
  }
}
