package com.alsheuski.reflection.result.model;

public class Node {

  private MetaClass previousClass;
  private MetaClass currentClass;

  public Node() {}

  public Node(MetaClass previousClass, MetaClass currentClass) {
    this.previousClass = previousClass;
    this.currentClass = currentClass;
  }

  public MetaClass getPreviousClass() {
    return previousClass;
  }

  public MetaClass getCurrentClass() {
    return currentClass;
  }

  public void setPreviousClass(MetaClass previousClass) {
    this.previousClass = previousClass;
  }

  public void setCurrentClass(MetaClass currentClass) {
    this.currentClass = currentClass;
  }
}
