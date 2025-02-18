package com.alsheuski.threading.printer;

public class Printer2 {

  private int currentOrder = 1;

  public void first(Runnable runnable) {
    process(1, runnable);
  }

  public void second(Runnable runnable) {
    process(2, runnable);
  }

  public void third(Runnable runnable) {
    process(3, runnable);
  }

  private void process(int order, Runnable runnable) {
    try {
      synchronized (this) {
        while (true) {
          if (currentOrder == order) {
            runnable.run();
            incrementCurrentOrder();
            this.notifyAll();
            return;
          } else {
            this.wait();
          }
        }
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    Printer2 pp = new Printer2();
    Thread t1 =
        new Thread(
            () -> {
              while (true) {
                pp.first(
                    () -> System.out.println(1));
              }
            });

    Thread t2 =
            new Thread(
                    () -> {
                      while (true) {
                        pp.second(
                                () -> System.out.println(2));
                      }
                    });

    Thread t3 =
            new Thread(
                    () -> {
                      while (true) {
                        pp.third(
                                () -> System.out.println(3));
                      }
                    });

    t1.start();
    t2.start();
    t3.start();
  }

  private void incrementCurrentOrder() {
    if (currentOrder == 3) {
      currentOrder = 1;
    } else {
      currentOrder = currentOrder + 1;
    }
  }
}
