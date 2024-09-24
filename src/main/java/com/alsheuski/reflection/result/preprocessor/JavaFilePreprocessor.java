package com.alsheuski.reflection.result.preprocessor;

import static com.alsheuski.reflection.result.util.LoaderUtil.loadClass;

import com.alsheuski.reflection.result.context.GlobalContext;
import com.alsheuski.reflection.result.preprocessor.replacer.JavaFileContentReplacer;
import com.alsheuski.reflection.result.preprocessor.replacer.RealTypeVisitorProvider;
import com.alsheuski.reflection.result.preprocessor.replacer.VarTypeVisitorProvider;
import com.alsheuski.reflection.result.resolver.PathResolver;
import com.alsheuski.reflection.result.visitor.FieldTypeClassVisitor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class JavaFilePreprocessor {

  private JavaFilePreprocessor() {}

  // todo CHECK IF POSSIBLE- for future will be better to process class once. The main idea to have
  // one class loader which can load all necessary class data once
  public static String removeVarTypes(String pathToJavaFile, String pathToCompiledClass)
      throws IOException {

    var asmVisitor = new FieldTypeClassVisitor(pathToCompiledClass);
    loadClass(pathToCompiledClass, asmVisitor);

    var jdtVisitorProvider = new RealTypeVisitorProvider(asmVisitor.getRowNumbersMap());
    return JavaFileContentReplacer.replace(pathToJavaFile, jdtVisitorProvider);
  }

  public static Path simplifyJavaFileTypes(String pathToJavaFile, GlobalContext context)
      throws IOException {

    var javaFilePath = PathResolver.resolve(pathToJavaFile);
    var content =
        JavaFileContentReplacer.replace(javaFilePath.toString(), new VarTypeVisitorProvider());
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
