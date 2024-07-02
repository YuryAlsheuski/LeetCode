package com.alsheuski.reflection;

import java.util.List;
import java.util.Map;

public class GenericChild extends GenericParent<List<String>> {


  @Override
  public List<List<String>> get(List<String> object) {
    return List.of();
  }

  @Override
  public Map<Map<String, List<String>>, Map<String, List<String>>> superGeneric() {
    return Map.of();
  }
}
