package com.alsheuski.reflection.result.util;

import com.alsheuski.reflection.result.context.GlobalContext;
import java.nio.file.Path;
import javax.tools.ToolProvider;

public class CompilerUtil {

  public static Path compile(String sourceFilePath, GlobalContext context) {
    var outputDir = context.getWorkDirectory();
    doCompile(sourceFilePath, context);
    return outputDir.resolve(context.getSourceRootFilePath() + ".class");
  }

  private static void doCompile(String sourceFilePath, GlobalContext context) {
    var compiler = ToolProvider.getSystemJavaCompiler();

    if (compiler == null) {
      throw new RuntimeException(
          "No Java compiler available. Make sure you're using a JDK, not a JRE.");
    }

    var options =
        new String[] {
          "-sourcepath",
          context.getProjectSourcesDir().toString(),
          "-classpath",
          context.getProjectClassPath(),
          "-Xprefer:source",
          "-d",
          context.getWorkDirectory().toString(),
          "-g",
          sourceFilePath
        };

    var compilationResult = compiler.run(null, null, null, options);
    if (compilationResult != 0) {
      throw new RuntimeException("Compilation failed.");
    }
  }
}
