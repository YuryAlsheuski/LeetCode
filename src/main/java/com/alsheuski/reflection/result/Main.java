package com.alsheuski.reflection.result;

import com.alsheuski.reflection.result.preprocessor.TestClassPreprocessor;
import com.alsheuski.reflection.result.preprocessor.classpath.ClasspathProvider;
import com.alsheuski.reflection.result.preprocessor.classpath.DefaultClassPathProvider;
import java.io.IOException;

public class Main {
  public static void main(String[] args) throws IOException {
    String workingDir = "/Users/Yury_Alsheuski/Desktop/myProjects/LeetCode";
    ClasspathProvider provider = new DefaultClassPathProvider();
    TestClassPreprocessor.simplifyTypes(
        "/Users/Yury_Alsheuski/Desktop/myProjects/LeetCode/src/main/java/com/alsheuski/reflection/Common.java",
        workingDir,
        provider);

    /*    var root = "/Users/Yury_Alsheuski/Desktop/myProjects/LeetCode/target/classes/";
    var className = "com/alsheuski/reflection/Common";
    Predicate<String> allowedClassPaths = path -> path.startsWith("com/alsheuski");

    var configManager = new ConfigManager();
    configManager.addConfig(new ClassVisitorConfig(className, i -> true));

    var result =
        new ClassStructureVisitor(root, configManager, allowedClassPaths, 2)
            .getAllDeps(new ClassLoadingContext(className, false));

    System.err.println(printLinkedWith(className, new ArrayList<>(result.values())));*/
  }
  // todo check methods overriding
}
