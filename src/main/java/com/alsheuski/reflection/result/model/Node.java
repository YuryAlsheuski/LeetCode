package com.alsheuski.reflection.result.model;

public class Node {
  private String previousClass;
  private String currentClass;
  private Method currentClassMethod;


  public String getPreviousClass() {
    return previousClass;
  }

  public void setPreviousClass(String previousClass) {
    this.previousClass = previousClass;
  }

  public String getCurrentClass() {
    return currentClass;
  }

  public void setCurrentClass(String currentClass) {
    this.currentClass = currentClass;
  }

  public Method getCurrentClassMethod() {
    return currentClassMethod;
  }

  public void setCurrentClassMethod(Method currentClassMethod) {
    this.currentClassMethod = currentClassMethod;
  }
}
