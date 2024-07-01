package com.alsheuski.reflection.result.context;

import com.alsheuski.reflection.result.model.MetaClass;

public class ClassLoadingContext {

  private final MetaClass currentClass;
  private final ClassLoadingContext childClassContext;
  private final boolean addToResults;

  public ClassLoadingContext(String currentClass, boolean addToResults) {
    this(currentClass, null, addToResults);
  }

  public ClassLoadingContext(String currentClass) {
    this(currentClass, null, true);
  }

  public ClassLoadingContext(
      String currentClass, ClassLoadingContext childClassContext, boolean addToResults) {

    this.currentClass = new MetaClass(currentClass);
    this.childClassContext = childClassContext;
    this.addToResults = childClassContext == null && addToResults;
  }

  public MetaClass getCurrentClass() {
    return currentClass;
  }

  public String getClassFullName() {
    return currentClass.getFullName();
  }

  public boolean hasChild() {
    return childClassContext != null;
  }

  public ClassLoadingContext getChildClassContext() {
    return childClassContext;
  }

  public boolean addToResults() {
    return addToResults;
  }
}
