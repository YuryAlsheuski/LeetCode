package com.alsheuski.reflection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

public class LocalVariableTypePrinter {

  public static void main(String[] args) throws IOException {
    var classBytes =
        Files.readAllBytes(
            Paths.get(
                "/Users/Yury_Alsheuski/Desktop/myProjects/LeetCode/work/com/alsheuski/reflection/Common.class"));
    var classReader = new ClassReader(classBytes);
    // Accept the class and visit its contents with our custom visitor
    classReader.accept(new ClassVisitor(Opcodes.ASM9) {

      @Override
      public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        // Only visit the method we're interested in
          return new MethodNode(Opcodes.ASM9, access, name, descriptor, signature, exceptions) {
            @Override
            public void visitEnd() {
              super.visitEnd();

              // Iterate over the local variables
              List<LocalVariableNode> localVariables = this.localVariables;
              for (LocalVariableNode localVar : localVariables) {
                if (localVar.index != 0) { // Exclude 'this' reference for non-static methods
                  String typeDesc = localVar.desc;
                  String type = Type.getType(typeDesc).getClassName();
                  System.out.println(type);
                }
              }
            }
          };
        }

    }, 0);
  }
}