package com.alsheuski.arrays.routes_count;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {


  public static void main(String[] args) {

    Map<Integer, List<Integer>> map = new HashMap<>();

    int[][] arr =
        new int[][] {
          {1, 2, 3},
          {4, 5, 6},
          {7, 8, 9}
        };
    int pin = 2334;

    for (int i = 0; i < arr.length; i++) {
      var row = arr[i];
      for (int j = 0; j < row.length; j++) {
        var value = row[j];
        var currentNeighbors = new ArrayList<Integer>();
        if (!map.containsKey(value)) {
          map.put(value, currentNeighbors);
        }
        var previousValIndex = j - 1;
        if (previousValIndex >= 0) {
          var previousVal = row[previousValIndex];
          var prevNeighbors = map.get(previousVal);
          prevNeighbors.add(value);
          currentNeighbors.add(previousVal);
        }

        int previousRowIndex = i - 1;
        if (previousRowIndex >= 0) {
          var prevRow = arr[previousRowIndex];
          var startIndex = Math.max(j - 1, 0);
          var endIndex = Math.min(j + 1, prevRow.length - 1);
          for (int k = startIndex; k <= endIndex; k++) {
            var previousRowVal = prevRow[k];
            var prevNeighbors = map.get(previousRowVal);
            prevNeighbors.add(value);
            currentNeighbors.add(previousRowVal);
          }
        }
      }
    }

  }

  private void test(){
    List<Integer> list = new ArrayList<>();
    list.sort((a, b) -> Integer.compare(b, a));

  }
}
