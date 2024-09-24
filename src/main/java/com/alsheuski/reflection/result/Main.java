package com.alsheuski.reflection.result;

import static com.alsheuski.reflection.result.util.LoaderUtil.buildClassesMetadata;

import com.alsheuski.reflection.result.config.ClassVisitorConfig;
import com.alsheuski.reflection.result.config.ConfigManager;
import com.alsheuski.reflection.result.context.ClassLoadingContext;
import com.alsheuski.reflection.result.context.GlobalContext;
import com.alsheuski.reflection.result.util.CompilerUtil;
import com.alsheuski.reflection.result.util.JavaFileUtil;
import com.alsheuski.reflection.result.visitor.ClassDepsVisitor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Predicate;

public class Main {
  public static void main(String[] args) throws IOException {

    var pathToJavaFile =
        "/Users/Yury_Alsheuski/Desktop/myProjects/LeetCode/src/main/java/com/alsheuski/reflection/Common.java";
    var workingDir = "/Users/Yury_Alsheuski/Desktop/myProjects/LeetCode/work";
    var gContext = new GlobalContext(pathToJavaFile, workingDir);
    // todo rewrite in the future JavaFileUtil content modifer methods like stream API with terminal
    // operations compile()/get()
    var newJavaFile = JavaFileUtil.simplifyJavaFileTypes(gContext).toString();
    var newClassFilePath = CompilerUtil.compile(newJavaFile, gContext);
    JavaFileUtil.removeVarTypes(newJavaFile, newClassFilePath.toString());
    var finalContent = JavaFileUtil.setSuperPrefix(newJavaFile, gContext);

    System.err.println(finalContent);

    var className = gContext.getSourceRootFilePath().toString();
    Predicate<String> allowedClassPaths = path -> path.startsWith("com/alsheuski");
    var configManager =
        new ConfigManager(allowedClassPaths)
            .addConfig(new ClassVisitorConfig(className, i -> true));

    var result =
        new ClassDepsVisitor(gContext.getWorkDirectory().toString(), configManager, 2)
            .getAllDeps(new ClassLoadingContext(className, false));

    System.err.println(buildClassesMetadata(className, new ArrayList<>(result.values())));
  }
  // todo check methods overriding
}
