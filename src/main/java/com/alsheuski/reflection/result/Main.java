package com.alsheuski.reflection.result;


import com.alsheuski.reflection.result.preprocessor.TargetClassPreprocessor;

public class Main {
  public static void main(String[] args) {

   /* var root = "/Users/Yury_Alsheuski/Desktop/myProjects/LeetCode/target/classes/";
    var className = "com/alsheuski/reflection/Common";
    Predicate<String> allowedClassPaths =
        path -> path.startsWith("com/alsheuski");

    var configManager = new ConfigManager();
    configManager.addConfig(new ClassVisitorConfig(className, i -> true));

    var result =
        new ClassStructureVisitor(root, configManager, allowedClassPaths, 2).getAllDeps(new ClassLoadingContext(className,false));

    System.err.println(printLinkedWith(className, new ArrayList<>(result.values())));*/
    TargetClassPreprocessor preprocessor = new TargetClassPreprocessor();
    preprocessor.process();
  }
  //todo check methods overriding
}
