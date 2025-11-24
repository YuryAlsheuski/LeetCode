package com.alsheuski.arrays.bubble_sort;

import java.util.Arrays;

public class Main {
  public static void main(String[] args) {
    int[] arr = {3, 1, 5, 0, 7, 9, 2};

    for (int j = 1; j < arr.length; j++) {

      for (int i = 0; i < arr.length - j; i++) {
        int current = arr[i];
        int next = arr[i + 1];
        if (current > next) {
          arr[i + 1] = current;
          arr[i] = next;
        }
      }
    }

    System.out.println(Arrays.toString(arr));
  }
}
