package com.alsheuski.reflection.result.model;

public class Signature {

  private final String signature;
  private boolean hasFormalArgs;

  public Signature(String signature) {
    this.signature = signature;
  }

  public Signature(String signature, boolean hasFormalArgs) {//todo remove class
    this.signature = signature;
    this.hasFormalArgs = hasFormalArgs;
  }

  public String getValue() {
    return signature;
  }

  public boolean hasFormalArgs() {
    return hasFormalArgs;
  }

  public void setHasFormalArgs(boolean hasFormalArgs) {
    this.hasFormalArgs = hasFormalArgs;
  }
}
