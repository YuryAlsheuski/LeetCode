package com.alsheuski.reflection;

import java.util.List;
import java.util.Map;

public class GenericParent<TTT extends List<?>,K,P extends Map<String,?>> {
  public <ZZ> ZZ get(K object, List<P> properties){
    return null;
  }
}
