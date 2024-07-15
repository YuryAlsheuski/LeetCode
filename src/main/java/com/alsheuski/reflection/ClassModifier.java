package com.alsheuski.reflection;

import org.objectweb.asm.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.objectweb.asm.Opcodes.*;

public class ClassModifier {

  public static void main(String[] args) throws IOException {

    File inputFile = new File("path/to/Common.class");
    FileInputStream inputStream = new FileInputStream(inputFile);
    ClassReader classReader = new ClassReader(inputStream);

    ClassWriter classWriter = new ClassWriter(classReader, 0);
    ClassVisitor classVisitor = new ClassVisitor(ASM9, classWriter) {
      @Override
      public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new MethodVisitor(ASM9, mv) {
          @Override
          public void visitTypeInsn(int opcode, String type) {
            if (opcode == NEW && type.equals("Common$ParentGetter")) {
              super.visitTypeInsn(NEW, "Common$GenericChild");
            } else {
              super.visitTypeInsn(opcode, type);
            }
          }

          @Override
          public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            if (owner.equals("Common$ParentGetter") && name.equals("get")) {
              // Remove the existing NEW and INVOKESPECIAL for ParentGetter
              mv.visitInsn(POP); // remove 'new ParentGetter()'
              mv.visitInsn(POP); // remove '.get()'
              mv.visitTypeInsn(NEW, "Common$GenericChild");
              mv.visitInsn(DUP);
              mv.visitMethodInsn(INVOKESPECIAL, "Common$GenericChild", "<init>", "()V", false);
            } else {
              super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            }
          }
        };
      }
    };

    classReader.accept(classVisitor, 0);

    byte[] modifiedClass = classWriter.toByteArray();
    File outputFile = new File("path/to/ModifiedCommon.class");
    FileOutputStream outputStream = new FileOutputStream(outputFile);
    outputStream.write(modifiedClass);
    outputStream.close();
  }
}

