package com.alsheuski.reflection.result.preprocessor;

import com.alsheuski.reflection.result.context.GlobalContext;
import com.alsheuski.reflection.result.resolver.PathResolver;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class SourceClassPreprocessor {

  private SourceClassPreprocessor() {}

  // todo for future will be better to process class once
  public static String removeVarTypes(String pathToJavaFile) {
    return "";
  }

  public static Path simplifyJavaFileTypes(String pathToJavaFile, GlobalContext context)
      throws IOException {

    var javaFilePath = PathResolver.resolve(pathToJavaFile);
    var content = new VarReplacer().replaceTypesToVar(javaFilePath.toString());
    var newJavaFilePath = context.getWorkDirectory().resolve(javaFilePath.getFileName());
    writeToFile(newJavaFilePath.toFile(), content);

    return newJavaFilePath;
  }

  private static void writeToFile(File file, String content) throws IOException {
    if (!file.exists()) {
      file.createNewFile();
    }
    var writer = new FileWriter(file);
    writer.write(content);
    writer.close();
  }
}
