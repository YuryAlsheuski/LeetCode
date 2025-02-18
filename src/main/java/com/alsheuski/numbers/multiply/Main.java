package com.alsheuski.numbers.multiply;




// https://www.codewars.com/kata/55bf01e5a717a0d57e0000ec/train/java
public class Main {
  public static void main(String[] args) {

    for (int r = 102974; r != 0; r =r/ 10)
      System.err.println( r % 10);
    //System.err.println(Persist.persistence(39));
  }

  static class Persist {

    public static int persistence(long n) {
      return multiply(0, n);
    }

    private static int multiply(int multiplications, long n) {

      if (((n / 10)) == 0) {
        return multiplications;
      }

      var multipliedNumber = 1L;
      var restNumber = n;

      while ((restNumber / 10) != 0) {
        var dumpNumber = restNumber;
        restNumber = restNumber / 10;
        multipliedNumber = multipliedNumber * (dumpNumber - restNumber * 10);
      }
      multipliedNumber = multipliedNumber * restNumber;
      multiplications = multiplications + 1;
      return multiply(multiplications, multipliedNumber);
    }
  }
}
