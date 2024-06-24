package com.alsheuski.reflection;

public class ClassB {

  private final String name;

  public ClassB(String name) {
    this.name = name;
  }

  protected ClassB(String name,String newone) {
    this.name = name;
  }

  ClassB(String name,String newone,String threenewaone) {
    this.name = name;
  }

  private ClassB(){
   name="";
  }

  public String getName(int test) {
    return String.valueOf(test);
  }

  private String donotprint(){
    return "not print";
  }
}
