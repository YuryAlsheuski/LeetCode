package com.alsheuski.reflection;

import java.util.List;
import java.util.Map;

public class IncomingParameterClass<K extends List<? extends List<? extends List<?>>>, V> {

  public class Inner<K extends Map<?, ?>, V> {}
}
