package com.alsheuski.reflection.result;

import static com.alsheuski.reflection.result.util.LoaderUtil.buildClassesMetadata;

import com.alsheuski.reflection.result.config.ClassVisitorConfig;
import com.alsheuski.reflection.result.config.ConfigManager;
import com.alsheuski.reflection.result.context.ClassLoadingContext;
import com.alsheuski.reflection.result.context.GlobalContext;
import com.alsheuski.reflection.result.preprocessor.SourceClassPreprocessor;
import com.alsheuski.reflection.result.visitor.ClassDepsVisitor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Predicate;

public class Main {
  public static void main(String[] args) throws IOException {
    String workingDir = "/Users/Yury_Alsheuski/Desktop/myProjects/LeetCode/work";
    String rootClassPath = "/Users/Yury_Alsheuski/Desktop/myProjects/LeetCode/target/classes";
    String buildToolHome = "/Applications/IntelliJ IDEA.app/Contents/plugins/maven/lib/maven3/bin";
    GlobalContext gContext = new GlobalContext(workingDir, rootClassPath, buildToolHome);
    SourceClassPreprocessor.simplifyTypes(
        "/Users/Yury_Alsheuski/Desktop/myProjects/LeetCode/src/main/java/com/alsheuski/reflection/Common.java",
        gContext);
    var root = "/Users/Yury_Alsheuski/Desktop/myProjects/LeetCode/target/classes/";
    var className = "com/alsheuski/reflection/Common";
    Predicate<String> allowedClassPaths = path -> path.startsWith("com/alsheuski");

    var configManager =
        new ConfigManager(allowedClassPaths)
            .addConfig(new ClassVisitorConfig(className, i -> true));

    var result =
        new ClassDepsVisitor(root, configManager, 2)
            .getAllDeps(new ClassLoadingContext(className, false));

    System.err.println(buildClassesMetadata(className, new ArrayList<>(result.values())));
  }
  // todo check methods overriding
}
