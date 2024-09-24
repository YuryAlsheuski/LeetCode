package com.alsheuski.bracketsvalidator;

import static java.util.stream.Collectors.toUnmodifiableSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Stack;

public class Main {

  public static void main(String[] args) {

    Collection<Wrapper> wrappers = new ArrayList<>();
    wrappers.add(new Wrapper(null));
    wrappers.add(null);
    wrappers.add(new Wrapper(""));

    var rr =
        wrappers.stream().map(i -> i.name)
                .filter(Objects::nonNull)
                .collect(toUnmodifiableSet());

    System.err.println(rr);

    // System.err.println(isValid("{[]}}"));
  }

  static class Wrapper {
    public String name;

    public Wrapper(String name) {
      this.name = name;
    }
  }

  public static boolean isValid(String brackets) {
    var stack = new Stack<Character>();
    for (var c : brackets.toCharArray()) {
      if (c == '{' || c == '[' || c == '(') {
        stack.push(c);
        continue;
      }
      if (c == '}' || c == ']' || c == ')') {
        if (stack.isEmpty()) {
          return false;
        }
        var cur = stack.pop();
        return cur == '{' && c == '}' || cur == '[' && c == ']' || cur == '(' && c == ')';
      }
    }
    return true;
  }
}
