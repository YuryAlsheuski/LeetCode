package com.alsheuski.reflection;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;

public class ClassNameExtractor {
  public static void main(String[] args) throws IOException {
    // Path to the .class file (replace with actual path)
    String classFilePath = "com/alsheuski/reflection/Common";

    // Create a ClassReader to read the class file
    ClassReader classReader = new ClassReader(classFilePath);

    // Create a custom visitor to extract class name and package
    classReader.accept(new ClassVisitor(Opcodes.ASM9) {
      @Override
      public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        // Convert the internal class name (with slashes) to a standard package name (with dots)
        String fullyQualifiedName = name.replace('/', '.');
        System.out.println("Fully Qualified Class Name: " + name);
      }
    }, 0);
  }
}
