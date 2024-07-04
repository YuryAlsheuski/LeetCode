package com.alsheuski.reflection;

import java.util.List;
import java.util.Map;

public class GenericParent<TTT extends List<?>,K,P extends Map<String,?>> {
  public K[]  get(K object, List<P> properties){
    return null;
  }

  public static <X> X getX(){
    return null;

  }
}
