package com.alsheuski.reflection;

import java.util.List;
import java.util.Map;

public class Common {

  public Common() {
    var something = new GenericParent<List<String>, ClassA, Map<String, Double>>().get(null);
    var some= new GenericParent<List<String>, ClassA, Map<String, Double>>().getX(null);
    new GenericChild().get(null);
    new GenericChild().getX(null);
    // GenericParent<List<String>,Integer, Map<String,Double>>.get();
  }
  /* public String getALabel(List<ClassA> aas, List<String> second) {
    return aas.stream().map(ClassA::getLabel).collect(Collectors.joining());
  }

  protected void testProtected() {}

  private void testPrivate() {
    new PrivateCallClass();
  }

  public void printClassBName() {
    new ClassC().getCClassLabel();
    System.out.println(new ClassB("d").getName(666, null, null));
  }

  public void commonInsider(CommonInsider insider) {
    System.err.println(insider.getStr());
  }

  public class CommonInsider {
    public String getStr() {
      return "fdsfsd";
    }
  }*/
}
