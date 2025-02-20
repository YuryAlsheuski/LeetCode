package com.alsheuski.numbers.multiply.reverse;

public class MAin {
  public static void main(String[] args) {
    int n = -10;
    int result = 0;
    int factor = n < 0 ? -1 : 1;

    for (int i = n * factor; i >= 1; i = i / 10) {
      var number = n % 10;
      n = n / 10;
      result = result * 10 + number;
    }

    System.err.println(result );
  }
}
