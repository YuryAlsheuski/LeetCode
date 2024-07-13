package com.alsheuski.reflection;

import java.util.List;
import java.util.Map;

public class GenericSuperParent<G extends List<?>> {
  public Map<G, String> superParentGet(ClassA obj) {
    return null;
  }
}
