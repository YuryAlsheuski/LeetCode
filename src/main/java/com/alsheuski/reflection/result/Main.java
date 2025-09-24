package com.alsheuski.reflection.result;

import java.io.IOException;

public class Main {
  public static void main(String[] args) throws IOException {

    var pathToJavaFile =
        "/Users/Yury_Alsheuski/Desktop/myProjects/LeetCode/src/main/java/com/alsheuski/reflection/Common.java";
    var workingDir = "/Users/Yury_Alsheuski/Desktop/myProjects/LeetCode/work";

    var testManager = new JunitTestManager(pathToJavaFile, workingDir);
    System.err.println(testManager.getJUnitTest());
  }
  // todo check methods overriding
}
