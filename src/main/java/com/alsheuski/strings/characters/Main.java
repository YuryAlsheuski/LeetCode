package com.alsheuski.strings.characters;

public class Main {

  public static void main(String[] args) {
    encode("");
  }
  public static String longest(String s1, String s2) {
    //https://www.codewars.com/kata/5656b6906de340bd1b0000ac/solutions/java
    StringBuilder sb = new StringBuilder();
    (s1 + s2).chars().distinct().sorted().forEach(c -> sb.append((char) c));
    return sb.toString();
  }

  static String encode(String word){

    word = word.toLowerCase();

    StringBuilder sb = new StringBuilder();

    for(int i = 0; i<word.length();i++){

      String value = String.valueOf(word.charAt(i));

      boolean hasDuplicates = word.indexOf(value) != word.lastIndexOf(value);

      if(hasDuplicates){
        sb.append(")");
      } else {
        sb.append("(");
      }
    }
    return sb.toString();
  }
}
