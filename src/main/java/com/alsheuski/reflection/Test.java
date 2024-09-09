package com.alsheuski.reflection;

import java.util.List;

public class Test {
  public String globalField = "";

  public void method() {

    String pp="9";
    int ww=9;
    List<Integer> n = List.of(0);
    var test = new ClassA().getLabel();

    System.err.println(ww);
    System.err.println(test);
    System.err.println("trst");
    System.err.println(pp);
    Integer ZZ =
        List.of(9, 4, 3).stream()
            .filter(
                r -> {
                  int X = 4;
                  return r == X;
                })
            .findFirst()
            .get();

    for(int lo=0; lo<n.size(); lo++){
      System.err.println(lo);
    }

    System.err.println(ZZ + n.size());
  }
}

//58,54, null
