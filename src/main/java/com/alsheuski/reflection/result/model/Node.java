package com.alsheuski.reflection.result.model;

public class Node {
  private MetaClass previousClass;
  private MetaClass currentClass;
  private Method currentMethod;

  public MetaClass getPreviousClass() {
    return previousClass;
  }

  public void setPreviousClass(MetaClass previousClass) {
    this.previousClass = previousClass;
  }

  public MetaClass getCurrentClass() {
    return currentClass;
  }

  public void setCurrentClass(MetaClass currentClass) {
    this.previousClass =  this.currentClass;
    this.currentClass = currentClass;
  }

  public Method getCurrentMethod() {
    return currentMethod;
  }

  public void setCurrentMethod(Method currentMethod) {
    this.currentMethod = currentMethod;
  }
}
