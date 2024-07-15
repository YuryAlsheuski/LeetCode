package com.alsheuski.reflection;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.*;

public class VarReplacer extends ClassVisitor {
  private final Map<Integer, String> localVariableTypes = new HashMap<>();

  public VarReplacer(ClassVisitor classVisitor) {
    super(Opcodes.ASM9, classVisitor);
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
    MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
    return new MethodVisitor(Opcodes.ASM9, mv) {
      @Override
      public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        localVariableTypes.put(index, descriptor);
        super.visitLocalVariable(name, descriptor, signature, start, end, index);
      }

      @Override
      public void visitVarInsn(int opcode, int var) {
        if (opcode == Opcodes.ASTORE && localVariableTypes.containsKey(var)) {
          String type = localVariableTypes.get(var);
          if (type.equals("Ljava/lang/Object;")) {
            // Replace 'var' with the actual type descriptor
            localVariableTypes.put(var, "Ljava/lang/String;");
          }
        }
        super.visitVarInsn(opcode, var);
      }
    };
  }

  public static void main(String[] args) throws Exception {
    // Read the Main class
    ClassReader cr = new ClassReader("com/alsheuski/reflection/Main");
    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    VarReplacer vr = new VarReplacer(cw);

    cr.accept(vr, 0);

    // Get the modified bytecode
    byte[] bytecode = cw.toByteArray();

    // Write the modified bytecode to a .class file
    try (FileOutputStream fos = new FileOutputStream("Main.class")) {
      fos.write(bytecode);
    }

    // Use CFR decompiler to convert the bytecode back to Java source code
  // decompileClass("Main.class");
  }

  public static void decompileClass(String classFilePath) throws Exception {
    // Use CFR decompiler to convert bytecode to Java source code
    String[] cfrArgs = {classFilePath};
    org.benf.cfr.reader.Main.main(cfrArgs);

    // Read and print the decompiled Java source code
    try (BufferedReader reader = new BufferedReader(new FileReader(classFilePath.replace(".class", ".java")))) {
      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }
    }
  }
}

