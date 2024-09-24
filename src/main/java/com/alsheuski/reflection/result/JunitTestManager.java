package com.alsheuski.reflection.result;

import static com.alsheuski.reflection.result.util.LoaderUtil.buildClassesMetadata;
import static com.alsheuski.reflection.result.util.LoaderUtil.loadClass;

import com.alsheuski.reflection.result.config.ClassVisitorConfig;
import com.alsheuski.reflection.result.config.ConfigManager;
import com.alsheuski.reflection.result.context.ClassLoadingContext;
import com.alsheuski.reflection.result.context.GlobalContext;
import com.alsheuski.reflection.result.preprocessor.modifier.JavaCodeModifier;
import com.alsheuski.reflection.result.util.CompilerUtil;
import com.alsheuski.reflection.result.util.FileUtil;
import com.alsheuski.reflection.result.visitor.ClassDepsVisitor;
import com.alsheuski.reflection.result.visitor.FieldTypeClassVisitor;
import java.util.ArrayList;
import java.util.function.Predicate;

public class JunitTestManager {

  private final GlobalContext context;

  public JunitTestManager(String pathToJavaFile, String workingDir) {
    context = new GlobalContext(pathToJavaFile, workingDir);
  }

  public String getTestClassCode() {
    return getJavaFileContent()
        + "\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n"
        + generateDepsData();
  }

  private String getJavaFileContent() {
    try {
      var code = FileUtil.readFileToString(context.getFilePath());
      var newCode = JavaCodeModifier.chain(code).simplifyTypes().modify();
      var javaFileCopy = context.getWorkDirectory().resolve(context.getFilePath().getFileName());

      FileUtil.writeToFile(javaFileCopy, newCode);

      var classFile = CompilerUtil.compile(javaFileCopy, context);

      var asmVisitor = new FieldTypeClassVisitor(classFile);
      loadClass(classFile, asmVisitor);

      return JavaCodeModifier.chain(newCode)
          .concretizeTypes(asmVisitor.getRowNumbersMap())
          .setSuperPrefix(context)
          .modify();

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private String generateDepsData() {
    var className = context.getSourceRootFilePath().toString();
    var allowedClassPaths =
        (Predicate<String>) path -> path.startsWith("com/alsheuski"); // todo make generic
    var configManager =
        new ConfigManager(allowedClassPaths)
            .addConfig(new ClassVisitorConfig(className, i -> true));

    var result =
        new ClassDepsVisitor(context.getWorkDirectory().toString(), configManager, 2)
            .getAllDeps(new ClassLoadingContext(className, false));

    return buildClassesMetadata(className, new ArrayList<>(result.values()));
  }
}
