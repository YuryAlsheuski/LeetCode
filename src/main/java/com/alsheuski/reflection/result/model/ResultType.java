package com.alsheuski.reflection.result.model;

import java.util.function.Function;
import org.objectweb.asm.Type;

public class ResultType {

  private Type type;
  private String genericType;

  public ResultType(Type type) {
    if (type == null) {
      throw new IllegalArgumentException("Type cannot be null");
    }
    this.type = type;
  }

  public ResultType(String genericType) {
    if (genericType == null || genericType.isEmpty()) {
      throw new IllegalArgumentException("genericType is null or empty");
    }
    this.genericType = genericType.replace(";", "");
  }

  public String printClassName(Function<String, String> printer) {
    if (genericType != null) {
      return genericType;
    }
    return printer.apply(type.getClassName());
  }
}
