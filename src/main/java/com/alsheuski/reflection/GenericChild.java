package com.alsheuski.reflection;

import java.util.List;
import java.util.Map;

public class GenericChild extends GenericParent<List<String>, String, Map<String,Integer>> {

  public void printSize() {
    //get(null).length();
  }
}
