package com.alsheuski.reflection;



public class Common {

  public Common() {
    /*var something = new GenericParent<List<String>, ClassA, Map<String, Double>>().get(null);
     var some= new GenericParent<List<String>, ClassA, Map<String, Double>>().getX(null);

   new GenericParent<List<String>, ClassA, Map<String, Double>>().superParentGet(null);
     new GenericParent<List<String>, ClassA, Map<String, Double>>().get(null);
     new GenericParent<List<String>, ClassA, Map<String, Double>>().getX(null);

     new GenericChild().superParentGet(null);
     new GenericChild().get(null);
     new GenericChild().getX(null);
    new GenericChild().getParentSimpleArr(null);
    new GenericParent<List<String>, ClassA, Map<String, Double>>().getParentSimpleArr(null);

    new GenericChild().getParentGenericArr(null);
    new GenericParent<List<String>, ClassA, Map<String, Double>>().getParentGenericArr(null);*/
    GenericParent<?,String,?> pp = new GenericChild();
    String instance = pp.get(null);
  }
   /*public String getALabel(List<ClassA> aas, List<String> second) {
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

  class ParentGetter{

    public GenericParent<?,?,?> get(){
      return new GenericChild();
    }
  }
}
