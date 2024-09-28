package com.alsheuski.reflection.result.util;

import com.alsheuski.reflection.result.context.GlobalContext;
import java.nio.file.Path;
import javax.tools.ToolProvider;

public class CompilerUtil {

  public static Path compile(Path sourceFilePath, GlobalContext context) {
    var outputDir = context.getWorkDirectory();
    doCompile(sourceFilePath, context);
    return outputDir.resolve(context.getSourceRootFilePath() + ".class");
  }

  private static void doCompile(Path sourceFilePath, GlobalContext context) {
    var compiler = ToolProvider.getSystemJavaCompiler();

    if (compiler == null) {
      throw new RuntimeException(
          "No Java compiler available. Make sure you're using a JDK, not a JRE.");
    }

    var options =
        new String[] {
          "-sourcepath",
          context.getProjectSourceFilesPath(),
          "-classpath",
          context.getProjectClassPath(),
          "-Xprefer:source",
          "-d",
          context.getWorkDirectory().toString(),
          "-g",
          sourceFilePath.toString()
        };

    var compilationResult = compiler.run(null, null, null, options);
    if (compilationResult != 0) {
      throw new RuntimeException("Compilation failed.");
    }
  }
}
