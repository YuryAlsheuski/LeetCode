package com.alsheuski.reflection;

import java.util.List;
import java.util.stream.Collectors;

public class Common {
  private ClassB childB;

  public Common() {
    this.childB = new ClassB("TEST_NAME");
  }

  /*public Common(int one, Long two) {
    childB = new ClassB("TEST_NAME", "", "");
  }

  private Common(String three) {

  }

  protected Common(Byte testProtectedConstr) {
    childB = new ClassB("TEST_NAME", "protected constr");
  }*/

  public String getALabel(List<ClassA> aas, List<String> second) {
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
  }
}
