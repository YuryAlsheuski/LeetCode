package com.alsheuski.reflection;

import java.util.*;

public class GenericTypeParser {

  public static void main(String[] args) {
    String input = "Lcom/alsheuski/reflection/GenericParent<Ljava/util/List<Ljava/lang/String;>;Ljava/lang/String;Ljava/util/Map<Ljava/lang/String;Ljava/lang/Integer;>;>.InnerClass<Ljava/util/Map<Ljava/lang/String;Ljava/lang/Integer;>;>..InnerClass<Ljava/util/Map<Ljava/lang/String;Ljava/lang/666;>;>;";
    List<String> result = parseGenericTypes(input);
    System.out.println(result);
  }

  public static List<String> parseGenericTypes(String input) {
    List<String> result = new ArrayList<>();
    int len = input.length();
    int index = 0;

    while (index < len) {
      if (input.charAt(index) == '<') {
        int endIndex = findMatchingBracket(input, index);
        if (endIndex != -1) {
          String genericContent = input.substring(index + 1, endIndex);
          extractGenericTypes(genericContent, result);
          index = endIndex;
        }
      }
      index++;
    }

    return result;
  }

  private static int findMatchingBracket(String input, int startIndex) {
    int len = input.length();
    int count = 0;

    for (int i = startIndex; i < len; i++) {
      if (input.charAt(i) == '<') {
        count++;
      } else if (input.charAt(i) == '>') {
        count--;
        if (count == 0) {
          return i;
        }
      }
    }

    return -1; // No matching bracket found
  }

  private static void extractGenericTypes(String input, List<String> result) {
    int len = input.length();
    int index = 0;
    int start = 0;
    int nestedLevel = 0;

    while (index < len) {
      char c = input.charAt(index);
      if (c == '<') {
        nestedLevel++;
      } else if (c == '>') {
        nestedLevel--;
      } else if (c == ';' && nestedLevel == 0) {
        result.add(input.substring(start, index + 1));
        start = index + 1;
      }
      index++;
    }

    if (start < len) {
      result.add(input.substring(start));
    }
  }
}


