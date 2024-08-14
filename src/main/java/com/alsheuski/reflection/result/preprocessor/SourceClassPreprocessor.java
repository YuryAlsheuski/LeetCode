package com.alsheuski.reflection.result.preprocessor;

import com.alsheuski.reflection.result.context.GlobalContext;
import com.alsheuski.reflection.result.resolver.PathResolver;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.tools.ToolProvider;

public class SourceClassPreprocessor {

  private SourceClassPreprocessor() {}

  public static String removerVarTypes(String pathToJavaFile) {
    return "";
  }

  public static void simplifyTypes(String pathToJavaFile, GlobalContext context)
      throws IOException {

    var javaFilePath = PathResolver.resolve(pathToJavaFile);
    var content = new VarReplacer().replaceTypesToVar(javaFilePath.toString());
    var newJavaFilePath = context.getWorkDirectory().resolve(javaFilePath.getFileName());

    writeToFile(newJavaFilePath.toFile(), content);
    recompile(
        newJavaFilePath.toString(),
        context.getWorkDirectory().toString(),
        context.getProjectClassPath());
  }

  private static void writeToFile(File file, String content) throws IOException {
    if (!file.exists()) {
      file.createNewFile();
    }
    var writer = new FileWriter(file);
    writer.write(content);
    writer.close();
  }

  private static boolean recompile(String sourceFilePath, String outputDir, String classpath) {
    var compiler = ToolProvider.getSystemJavaCompiler();

    if (compiler == null) {
      System.err.println("No Java compiler available. Make sure you're using a JDK, not a JRE.");
      return false;
    }

    // Create the compilation options and source files list
    var options = new String[] {"-d", outputDir, "-classpath", classpath, "-g", sourceFilePath};

    // Compile the file
    var compilationResult = compiler.run(null, null, null, options);

    return compilationResult == 0;
  }
}
