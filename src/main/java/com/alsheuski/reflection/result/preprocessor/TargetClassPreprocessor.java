package com.alsheuski.reflection.result.preprocessor;

import java.io.FileWriter;
import java.io.IOException;

public class TargetClassPreprocessor {
  private final VarReplacer replacer;

  public TargetClassPreprocessor() {
    this.replacer = new VarReplacer();
  }

  public void process(String pathToJavaFile) throws IOException {
    String output = replacer.replaceTypesToVar(pathToJavaFile);
    writeToFile(pathToJavaFile, output);
  }

  private void writeToFile(String pathToJavaFile, String content) throws IOException {
    FileWriter writer = new FileWriter(pathToJavaFile);
    writer.write(content);
    writer.close();
  }
}
