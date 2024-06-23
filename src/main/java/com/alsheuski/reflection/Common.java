package com.alsheuski.reflection;

import org.w3c.dom.ls.LSOutput;

import java.util.List;
import java.util.stream.Collectors;

public class Common {
  private final ClassB childB;

  public Common() {
    this.childB = new ClassB("TEST_NAME");
  }

  public String getALabel(List<ClassA> aas) {
    return aas.stream().map(ClassA::getLabel).collect(Collectors.joining());
  }

  public void printClassBName(){
    System.out.println(childB.getName(666));
  }

  public void commonInsider(CommonInsider insider){
    System.err.println(insider.getStr());
  }

  public class CommonInsider {
    public String getStr(){
      return "fdsfsd";
    }
  }
}
