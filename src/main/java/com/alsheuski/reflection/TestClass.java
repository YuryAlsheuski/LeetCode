package com.alsheuski.reflection;

import java.util.function.Supplier;

public class TestClass {

  public void test() {
    Supplier<String> supp = getGeneric();
    System.err.println(supp.get().length());
  }

  public <T> Supplier<T> getGeneric(){
    return ()-> (T) "fdsfsd";
  }
}
