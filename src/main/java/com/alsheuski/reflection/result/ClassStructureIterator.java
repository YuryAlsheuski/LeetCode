package com.alsheuski.reflection.result;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClassStructureIterator extends ClassVisitor {

  private final int deep;

  public ClassStructureIterator(ClassVisitor cv, int deep) {
    super(Opcodes.ASM9, cv);
    this.deep = deep;
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
    MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
    return new MethodStructureVisitor(mv);
  }


  public static void main(String[] args) throws IOException {
    String className = "target/classes/com/alsheuski/reflection/Common"; // Replace with your actual class name and path
    byte[] classBytes = Files.readAllBytes(Paths.get(className + ".class"));

    // Read the class
    ClassReader classReader = new ClassReader(classBytes);
    ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    ClassVisitor classVisitor = new ClassStructureIterator(classWriter);

    classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);

    // Write the modified class
    //  byte[] modifiedClass = classWriter.toByteArray();
    // Files.write(Paths.get(className + "_Modified.class"), modifiedClass);

  }
}
