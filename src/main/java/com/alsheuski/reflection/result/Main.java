package com.alsheuski.reflection.result;

import static com.alsheuski.reflection.result.util.LoaderUtil.printLinkedWith;

import com.alsheuski.reflection.result.config.ClassVisitorConfig;
import com.alsheuski.reflection.result.config.ConfigManager;
import com.alsheuski.reflection.result.context.ClassLoadingContext;
import com.alsheuski.reflection.result.visitor.ClassStructureVisitor;
import java.util.ArrayList;
import java.util.function.Predicate;

public class Main {
  public static void main(String[] args) {

    var root = "/Users/Yury_Alsheuski/Desktop/myProjects/LeetCode/target/classes/";
    var className = "com/alsheuski/reflection/Common";
    Predicate<String> allowedClassPaths =
        path -> path.startsWith("com/alsheuski");

    var configManager = new ConfigManager();
    configManager.addConfig(new ClassVisitorConfig(className, i -> true));

    var result =
        new ClassStructureVisitor(root, configManager, allowedClassPaths, 2).getAllDeps(new ClassLoadingContext(className,false));

    System.err.println(printLinkedWith(className, new ArrayList<>(result.values())));
  }
  //todo check array support type and as generic type too
}
