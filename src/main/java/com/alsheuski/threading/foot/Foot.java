package com.alsheuski.threading.foot;

public class Foot extends Thread {

  private final String name;
  private final String lock = "lock";
  private static volatile int current = 0;

  public Foot(String name) {
    this.name = name;
  }

  @Override
  public void run() {
    while (true) {
      step();
    }
  }

  private void step() {
    while (true) {
      try {
        synchronized (lock) {
          if (current == 0 && name.equals("LEFT") || current == 1 && name.equals("RIGHT")) {
            System.err.println(name);
            current = (current + 1) % 2;
            lock.notify();
            return;
          } else {
            lock.wait();
            System.out.println(name); //try to comment lock.notify() and lock.wait() and see the difference
          }
        }
      } catch (Exception e) {
        System.out.println(e);
      }
    }
  }

  public static void main(String[] args) {
    Foot left = new Foot("LEFT");
    left.start();
    Foot right = new Foot("RIGHT");
    right.start();
  }
}
