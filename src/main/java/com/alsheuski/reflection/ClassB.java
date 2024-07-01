package com.alsheuski.reflection;

import java.util.List;
import java.util.Map;

public class ClassB extends SuperClassTest {

  private final String name;

  public ClassB(String name) {
    super(name);
    this.name = name;
  }

  protected ClassB(String name, String newone) {
    super(name);
    this.name = name;
  }

  ClassB(String name, String newone, String threenewaone) {
    super(name);
    this.name = name;
  }

  private ClassB() {
    super("");
    name = "";
  }

  public List<String> getName(int test, Map<ClassC, ClassA> cLink, Map<ClassC, ?> llInk) {
    return List.of(test + new ClassC().getCClassLabel());
  }

  private String donotprint() {
    return "not print";
  }
}
