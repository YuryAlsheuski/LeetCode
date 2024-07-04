package com.alsheuski.reflection;

import java.util.HashMap;
import java.util.Map;

public class FormalTypeParameterParser {

  public static void main(String[] args) {
    String signature = "<N::Ljava/util/Queue<Ljava/util/List<Ljava/lang/String;>;>;L:Ljava/lang/Object;M::Ljava/util/Map<Ljava/lang/String;Ljava/util/Map<Ljava/lang/String;Ljava/lang/Integer;>;>;>(Ljava/util/Map<Ljava/lang/String;Ljava/util/Map<Ljava/lang/String;Ljava/lang/666;>;>;)TN;";

    Map<String, String> formalTypeParameters = parseFormalTypeParameters(signature);

    for (Map.Entry<String, String> entry : formalTypeParameters.entrySet()) {
      System.out.println(entry.getKey() + " -> " + entry.getValue());
    }
  }

  public static Map<String, String> parseFormalTypeParameters(String signature) {
    Map<String, String> formalTypeParameters = new HashMap<>();

    int startIndex = signature.indexOf('<');
    int endIndex = findMatchingAngleBracket(signature, startIndex);

    if (startIndex == -1 || endIndex == -1 || endIndex <= startIndex) {
      throw new IllegalArgumentException("Invalid signature format: " + signature);
    }

    String paramsString = signature.substring(startIndex + 1, endIndex);
    parseParameters(paramsString, formalTypeParameters);

    return formalTypeParameters;
  }

  private static int findMatchingAngleBracket(String signature, int startIndex) {
    int depth = 0;
    for (int i = startIndex; i < signature.length(); i++) {
      char c = signature.charAt(i);
      if (c == '<') {
        depth++;
      } else if (c == '>') {
        depth--;
        if (depth == 0) {
          return i;
        }
      }
    }
    return -1;  // No matching closing bracket found
  }

  private static void parseParameters(String paramsString, Map<String, String> formalTypeParameters) {
    int length = paramsString.length();
    int i = 0;
    while (i < length) {
      int colonIndex = paramsString.indexOf(':', i);
      if (colonIndex == -1) {
        break;
      }
      String paramName = paramsString.substring(i, colonIndex);
      i = colonIndex + 1;
      if (paramsString.charAt(i) == ':') {
        i++;  // Skip the second ':'
      }
      int typeStart = i;
      int depth = 0;
      while (i < length && (paramsString.charAt(i) != ';' || depth > 0)) {
        char c = paramsString.charAt(i);
        if (c == '<') {
          depth++;
        } else if (c == '>') {
          depth--;
        }
        i++;
      }
      // Include the ';' in the type if it exists
      if (i < length && paramsString.charAt(i) == ';') {
        i++;
      }
      String paramType = paramsString.substring(typeStart, i);
      formalTypeParameters.put(paramName, paramType);
    }
  }
}

