package com.alsheuski.threading.foot;

public class Robot {

  private final String lock = "lock";
  private volatile int currentFootIndex;
  private final RobotFoot leftFoot;
  private final RobotFoot rightFoot;

  public Robot(int currentFootIndex) {
    this.currentFootIndex = currentFootIndex;
    leftFoot = new RobotFoot(0, "left");
    rightFoot = new RobotFoot(1, "right");
  }

  public void startJourney() {
    leftFoot.start();
    rightFoot.start();
  }

  private int getNextFootIndex() {
    if (currentFootIndex == leftFoot.index) {
      return rightFoot.index;
    }
    return leftFoot.index;
  }

  public static void main(String[] args) {
    Robot robot = new Robot(0);
    robot.startJourney();
  }

  private class RobotFoot extends Thread {

    private final int index;
    private final String footName;

    public RobotFoot(int index, String footName) {
      this.index = index;
      this.footName = footName;
    }

    @Override
    public void run() {
      try {
        while (true) {
          synchronized (lock) {
            while (true) {
              if (currentFootIndex == index) {
                System.out.println("Foot: " + footName);
                currentFootIndex = getNextFootIndex();
                lock.notify();
              } else {
                lock.wait();
                System.err.println(footName + " loop iteration");
              }
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
