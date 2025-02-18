package com.alsheuski.threading.printer;

public class Printer {

  private int currentOrder;

  public Printer(int currentOrder) {
    this.currentOrder = currentOrder;
  }

  public void first(Runnable thread) {
    thread.run();
  }

  public void second(Runnable thread) {
    thread.run();
  }

  public void third(Runnable thread) {
    thread.run();
  }

  public void print() {
    first(getRunnable(1));
    second(getRunnable(2));
    third(getRunnable(3));
  }

  private Runnable getRunnable(int startOrder) {
    return () -> {
      var thread =
          new Thread(
              () -> {
                try {
                  while (true) {
                    synchronized (this) {
                      while (true) {
                        if (currentOrder == startOrder) {
                          System.out.println(currentOrder);
                          currentOrder = getCurrentOrder();
                          this.notifyAll();
                        } else {
                          this.wait();
                        }
                      }
                    }
                  }
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
              });
      thread.start();
    };
  }

  private int getCurrentOrder() {
    if (currentOrder == 3) {
      return 1;
    }
    return currentOrder + 1;
  }

  public static void main(String[] args) {
    Printer printer = new Printer(1);
    printer.print();
  }
}
