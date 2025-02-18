package com.alsheuski.strings.characters;

import java.util.ArrayList;
import java.util.List;

public class IPAdress {

  public static void main(String[] args) {

    List<String> results = new ArrayList<>();
    // List
    String ipAdress = "00000";

    for (int i = 0; i <= 2; i++) {

      String firstGroup = ipAdress.substring(0, i+1);
      if (!isValidGroup(firstGroup)) {
        break;
      }
 double dd = 3.444000000;

      for (int j = ipAdress.length() - 1; j >= ipAdress.length() - 3; j--) {

        String lastGroup = ipAdress.substring(j);
        if (!isValidGroup(lastGroup)) {
          continue;
        }

        String otherGroups = ipAdress.substring(i+1, j);
        if (otherGroups.length() > 6 || otherGroups.length() == 1) {
          continue;
        }

        int kStart = Math.max(1, (otherGroups.length() / 2) - 1);
        int kEnd = Math.min(2, otherGroups.length() - 1);

        for (int k = kStart; k <= kEnd; k++) {
          String secondGroup = otherGroups.substring(0, k+1);
          if (!isValidGroup(secondGroup)) {
            continue;
          }
          String thirdGroup = otherGroups.substring(k+1);
          if (!isValidGroup(thirdGroup)) {
            continue;
          }

          results.add(String.format("%s.%s.%s.%s", firstGroup, secondGroup, thirdGroup, lastGroup));
        }
      }
    }

    System.err.println(results);
  }

  private static boolean isValidGroup(String group) {
    var number = Integer.parseInt(group);

    if (number > 255) {
      return false;
    }
    if (number > 0 && group.startsWith("0")) {
      return false;
    }

    return true;
  }
}
