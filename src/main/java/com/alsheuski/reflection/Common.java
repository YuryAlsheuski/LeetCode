package com.alsheuski.reflection;

import com.alsheuski.reflection.result.Anonimus2;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Common extends ParentForCommonClass<String, String> implements ParentCommonInterface {
  public Common(String test) {
    String[] arr = new String[1];
    var ppa = new GenericChild();
    List<String> ff = new ArrayList<>();
    test();
    test2();
    //test3();
    System.err.println(ppa);
    System.err.println(ff);
    GenericParent<?, ?, ?> parent = new GenericChild();
    parent.get(null);
    parent.getParentGenericArr(null, null);

    test.length();
    var something = new GenericParent<List<String>, ClassA, Map<String, Double>>().get(null);
    var some = new GenericParent<List<String>, ClassA, Map<String, Double>>().getX(null);

    System.err.println(something);
    System.err.println(some);

/*    var result =
        List.of(1, 2, 3, 4, 5).stream()
            .filter(
                i -> {
                  int rest = i % 2;
                  return rest == 0;
                })
            .collect(Collectors.toList());*/

  /*  for (int loopVar : result) {
      System.err.println(loopVar);
    }

    for (int loopVar2 = 0; loopVar2 <= result.size(); loopVar2++) {
      System.err.println(loopVar2);
    }*/

    new GenericParent<List<String>, ClassA, Map<String, Double>>().superParentGet(null);
    new GenericParent<List<String>, ClassA, Map<String, Double>>().get(null);
    new GenericParent<List<String>, ClassA, Map<String, Double>>().getX(null);

    new GenericParent<List<String>, ClassA, Map<String, Double>>().new InnerGetter();

    new GenericChild().superParentGet(null);
    new GenericChild().get(null);
    new GenericChild().getX(null);
    new GenericChild().getParentSimpleArr(null);
    new GenericParent<List<String>, ClassA, Map<String, Double>>().getParentSimpleArr(null);

    new GenericChild().getParentGenericArr(null, null);
    new GenericParent<List<String>, ClassA, Map<String, Double>>().getParentGenericArr(null, null);
    var primitive = new GenericParent<List<String>, ClassA, Map<String, Double>>().getPrimitive();

    Supplier<String> supp = new CommonInsider().getStr();
    System.err.println(supp.get().length());
    ClassA.testStatic();
    GenericParent<?, ?, ?> pp = new GenericChild();
    var b = 0;
    var s = 0;
    pp.primitivesTest(null);
    GenericParent zz = new GenericChild();
    System.err.println(zz);
    Object fff = zz.getParccdcdcentGenericArr(null);
    System.err.println(fff);
  }

  public String getALabel(List<ClassA> aas, List<String> second) {
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

  @Override
  public void test() {
    var obj =
        new Anonimus() {
          @Override
          public Object get() {
            return new Anonimus2();
          }
        };
    System.err.println(obj);
  }

  public class CommonInsider {
    public <T> Supplier<T> getStr() {
      return () -> (T) "fdsfsd";
    }
  }

  private class ParentGetter {

    public GenericParent<?, ?, ?> get() {
      var test = new GenericChild();
      return new GenericChild();
    }
  }
}
