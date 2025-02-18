package com.alsheuski.arrays.islands_count;

public class Main {

  static String[][] islands = {
    {"1", "1", "0", "1", "0"},
    {"1", "1", "1", "1", "0"},
    {"0", "0", "0", "0", "1"},
    {"1", "1", "1", "1", "1"},
  };

  public static void main(String[] args) {

    int islandsCunt = 0;

    for (int i = 0; i < islands.length; i++) {

      for (int j = 0; j < islands[i].length; j++) {

        var segment = islands[i][j];

        if (segment.equals("1")) {
          islandsCunt = islandsCunt + 1;
          changeToZero(i, j);
        }
      }
    }
    System.err.println(islandsCunt);
  }

  private static void changeToZero(int i, int j) {
    if (i < 0
        || j < 0
        || j >= islands[0].length
        || i >= islands.length
        || islands[i][j].equals("0")) {
      return;
    }

    islands[i][j] = "0";
    changeToZero(i, j - 1); // left
    changeToZero(i, j + 1); // right
    changeToZero(j, i - 1); // upper
    changeToZero(j, i + 1); // down
  }
}
