package com.alsheuski.reflection;

import java.util.List;
import java.util.Map;

public class GenericParent<TTT extends List<?>, K, P extends Map<String, ?>>
    extends GenericSuperParent<List<TTT[]>> {

  public K get(List<P> properties) {
    return null;
  }

  public <X, Y extends List<P>, K> Y getX(List<X> properties) {
    return null;
  }

  public String[] getParentSimpleArr(byte[] incoming) {
    return null;
  }

  public P getParentGenericArr(TTT incoming, K out) {
    return null;
  }

  public int getPrimitive(){
    return 0;
  }

  public List<P> getParccdcdcentGenericArr(TTT incoming) {
    return null;
  }

  public void getVoidArr(TTT incoming) {}

  public List<P> getVoidArr() {
    return null;
  }

  public int primitivesTest(List<P> s){
    return 0;
  }

  public class InnerGetter {

    public GenericParent<?, ?, ?> get() {
      return new GenericChild();
    }
  }
}
