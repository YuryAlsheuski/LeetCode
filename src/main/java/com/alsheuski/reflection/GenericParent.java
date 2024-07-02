package com.alsheuski.reflection;

import java.util.List;
import java.util.Map;

public abstract class GenericParent<TO extends List<?>> {

  public abstract List<TO> get(TO object);
  public abstract Map<Map<String,List<String>>,Map<String,List<String>>> superGeneric();

}
