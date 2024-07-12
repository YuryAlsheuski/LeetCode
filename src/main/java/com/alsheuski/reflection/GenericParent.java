package com.alsheuski.reflection;

import java.util.List;
import java.util.Map;

public class GenericParent<TTT extends List<?>, K, P extends Map<String, ?>> {
  public K get(List<P> properties) {
    return null;
  }

  public <X, Y extends List<P>, K> Y getX(List<X> properties) {
    return null;
  }
}
