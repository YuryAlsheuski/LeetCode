package com.alsheuski.reflection.result.util;


public class LoaderUtil {
  private LoaderUtil() {}

public static String getClassPath(String className){
    return String.format("target/classes/%s.class",className);
}
}
