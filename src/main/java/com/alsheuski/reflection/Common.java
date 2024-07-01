package com.alsheuski.reflection;


import java.util.Map;

public class Common {
  private ClassB childB;

  public Common() {
    this.childB = new ClassB("TEST_NAME");
    childB.setSomething(Map.of());
    childB.printSmth();
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
    System.out.println(childB.getName(666, null, null));
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
