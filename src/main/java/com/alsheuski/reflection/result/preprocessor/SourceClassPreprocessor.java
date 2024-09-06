package com.alsheuski.reflection.result.preprocessor;

import static com.alsheuski.reflection.result.util.LoaderUtil.loadClass;

import com.alsheuski.reflection.result.context.GlobalContext;
import com.alsheuski.reflection.result.resolver.PathResolver;
import com.alsheuski.reflection.result.util.LoaderUtil;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;

public class SourceClassPreprocessor {

  private SourceClassPreprocessor() {}

  // todo for future will be better to process class once
  public static String removeVarTypes(String pathToJavaFile, String pathToCompiledClass)
      throws IOException {
    var rowNumberAndNameToType = new MultiKeyMap<String, String>();
    var visitor =
        new ClassVisitor(Opcodes.ASM9) {
          @Override
          public MethodVisitor visitMethod(
              int access, String name, String descriptor, String signature, String[] exceptions) {

            return new MethodNode(Opcodes.ASM9, access, name, descriptor, signature, exceptions) {
              @Override
              public void visitLocalVariable(
                  String name, String desc, String signature, Label start, Label end, int index) {

                if ("this".equals(name)) {
                  return;
                }

                var node = (LabelNode) start.info;
                var rowNumber = getRowNumber(node);
                var type = signature != null ? signature : desc;

                rowNumberAndNameToType.put(
                    String.valueOf(rowNumber), name, LoaderUtil.getClassName(type));
              }

              // for local variables definition in one row like: var a = 1;var b = 2;
              private int getRowNumber(AbstractInsnNode node) {
                var lineNode = node.getNext();
                if (lineNode instanceof LineNumberNode) {
                  return ((LineNumberNode) lineNode).line - 1;
                }
                return getRowNumber(lineNode);
              }
            };
          }
        };

    loadClass(pathToCompiledClass, visitor);

    return new TypeReplacer().replaceVarTypes(pathToJavaFile, rowNumberAndNameToType);
  }

  public static Path simplifyJavaFileTypes(String pathToJavaFile, GlobalContext context)
      throws IOException {

    var javaFilePath = PathResolver.resolve(pathToJavaFile);
    var content = new TypeReplacer().replaceTypesToVar(javaFilePath.toString());
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
