package com.alsheuski.reflection.result.util;

public class LoaderUtil {
  private LoaderUtil() {}

  public static String getClassPath(String className) {
    return String.format("target/classes/%s.class", className);
  }

  public static boolean isConstructor(String methodName){
    return"<init>".equals(methodName);
  }

  public static String getClassName(String classFullName){
    var parts = classFullName.split("/");
    return  parts[parts.length - 1];
  }

}
