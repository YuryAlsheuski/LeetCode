package com.alsheuski.reflection.result.util;

import com.alsheuski.reflection.result.model.OuterClass;

public class LoaderUtil {
  private LoaderUtil() {}

  public static void loadMethods(OuterClass clazz,int access, String name, String descriptor, String signature, String[] exceptions){
    //var methodsArgs =
  }

  public static String prepareClassPath(String path) {
    return path.replace('/', '.');
  }
}
