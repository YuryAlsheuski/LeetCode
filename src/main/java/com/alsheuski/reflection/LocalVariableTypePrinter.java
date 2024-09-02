package com.alsheuski.reflection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

public class LocalVariableTypePrinter {
  private static final MultiKeyMap<String, String> rowNumberToType = new MultiKeyMap<>();

  public static void main(String[] args) throws IOException {
    var classBytes =
        Files.readAllBytes(
            Paths.get(
                "/Users/Yury_Alsheuski/Desktop/myProjects/LeetCode/work/com/alsheuski/reflection/Common.class"));
    var classReader = new ClassReader(classBytes);
    // Accept the class and visit its contents with our custom visitor
    classReader.accept(
        new ClassVisitor(Opcodes.ASM9) {

          @Override
          public MethodVisitor visitMethod(
              int access, String name, String descriptor, String signature, String[] exceptions) {
            // Only visit the method we're interested in
            return new MethodNode(Opcodes.ASM9, access, name, descriptor, signature, exceptions) {
              @Override
              public void visitLocalVariable(
                  String name, String desc, String signature, Label start, Label end, int index) {

                if ("this".equals(name)) {
                  return;
                }

                var node = (LabelNode) start.info;
                var rowNumber = getRowNumber(node);

                rowNumberToType.put(
                    String.valueOf(rowNumber), name, signature != null ? signature : desc);
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
        },
        0);

    System.err.println(rowNumberToType);
  }
}
