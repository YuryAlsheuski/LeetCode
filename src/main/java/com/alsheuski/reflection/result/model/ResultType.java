package com.alsheuski.reflection.result.model;

import com.alsheuski.reflection.result.util.LoaderUtil;
import org.objectweb.asm.Type;

public class ResultType {

  private final String type;
  private boolean isFormalGenericPrm;

  public ResultType(Type type) {
    if (type == null) {
      throw new IllegalArgumentException("Type cannot be null");
    }
    this.type = type.getClassName();
  }

  public ResultType(String formalGenericType) {
    if (formalGenericType == null || formalGenericType.isEmpty()) {
      throw new IllegalArgumentException("Type is null or empty");
    }
    this.type = formalGenericType;
    isFormalGenericPrm = true;
  }

  @Override
  public String toString() {
    if (isFormalGenericPrm) {
      return type;
    }
    return LoaderUtil.getClassName(type).replace("$", ".");
  }
}
