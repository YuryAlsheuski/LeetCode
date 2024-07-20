package com.alsheuski.reflection.result;

import com.alsheuski.reflection.result.preprocessor.SourceClassPreprocessor;
import com.alsheuski.reflection.result.resolver.PathResolver;
import java.io.IOException;

public class Main {
  public static void main(String[] args) throws IOException {
    String workingDir = "/Users/Yury_Alsheuski/Desktop/myProjects/LeetCode";
    String rootClassPath = "/Users/Yury_Alsheuski/Desktop/myProjects/LeetCode/target/classes";
    String buildToolHome = "/Applications/IntelliJ IDEA.app/Contents/plugins/maven/lib/maven3/bin";
    String classpath = PathResolver.resolveClassPath(rootClassPath, buildToolHome);
    SourceClassPreprocessor.simplifyTypes(
        "/Users/Yury_Alsheuski/Desktop/myProjects/LeetCode/src/main/java/com/alsheuski/reflection/Common.java",
        workingDir,
        classpath);
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
