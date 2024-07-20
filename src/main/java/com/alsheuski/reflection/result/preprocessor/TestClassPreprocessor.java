package com.alsheuski.reflection.result.preprocessor;

import com.alsheuski.reflection.result.preprocessor.classpath.ClasspathProvider;
import com.alsheuski.reflection.result.preprocessor.classpath.PathResolver;
import java.io.FileWriter;
import java.io.IOException;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class TestClassPreprocessor {
  
  private TestClassPreprocessor(){}

  public static String removerVarTypes(String pathToJavaFile) {
    return "";
  }

  public static void simplifyTypes(String pathToJavaFile, String outputDir, ClasspathProvider classpathProvider) throws IOException {
    pathToJavaFile = PathResolver.resolve(pathToJavaFile).toString();
    outputDir = PathResolver.resolve(outputDir).toString();

    String output = new VarReplacer().replaceTypesToVar(pathToJavaFile);
    writeToFile(pathToJavaFile, output);
    recompile(pathToJavaFile, outputDir, classpathProvider.getClassPath());
  }

  private static void writeToFile(String pathToJavaFile, String content) throws IOException {
    FileWriter writer = new FileWriter(pathToJavaFile);
    writer.write(content);
    writer.close();
  }

  private static boolean recompile(String sourceFilePath, String outputDir, String classpath) {
    // Get the system Java compiler
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    if (compiler == null) {
      System.err.println("No Java compiler available. Make sure you're using a JDK, not a JRE.");
      return false;
    }

    // Create the compilation options and source files list
    String[] options = {"-d", outputDir, "-classpath", classpath, sourceFilePath};

    // Compile the file
    int compilationResult = compiler.run(null, null, null, options);

    return compilationResult == 0;
  }
}
