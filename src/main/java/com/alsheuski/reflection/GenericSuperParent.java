package com.alsheuski.reflection;

import java.util.List;

public class GenericSuperParent <G extends List<?>> {
  public G superParentGet(G obj) {
    return obj;
  }
}
