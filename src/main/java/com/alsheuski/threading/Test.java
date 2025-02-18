package com.alsheuski.threading;

public class Test {
  private static boolean bool;
  public static void main(String[] args) {
    Thread th = new Thread(() -> {
      while (bool) {
        
      }
    });
  }
}
