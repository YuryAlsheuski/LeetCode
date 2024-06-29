package com.alsheuski.reflection;

import org.objectweb.asm.*;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

import java.io.IOException;

public class Test {
  public static void main(String[] args) throws IOException {
    ClassReader classReader = new ClassReader("TestClass");
    classReader.accept(new ClassVisitor(Opcodes.ASM9) {
      @Override
      public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return new MethodStructureVisitor(name, descriptor, signature);
      }
    }, 0);
  }

  static class MethodStructureVisitor extends MethodVisitor {
    private final String methodName;
    private final String methodDescriptor;
    private final String methodSignature;

    public MethodStructureVisitor(String methodName, String methodDescriptor, String methodSignature) {
      super(Opcodes.ASM9);
      this.methodName = methodName;
      this.methodDescriptor = methodDescriptor;
      this.methodSignature = methodSignature;
    }

    @Override
    public void visitEnd() {
      System.out.println("Method: " + methodName);
      System.out.println("Descriptor: " + methodDescriptor);
      System.out.println("Signature: " + methodSignature);

      if (methodSignature != null) {
        SignatureReader signatureReader = new SignatureReader(methodSignature);
        signatureReader.accept(new SignatureVisitor(Opcodes.ASM9) {
          @Override
          public SignatureVisitor visitParameterType() {
            return new SignatureVisitor(Opcodes.ASM9) {
              @Override
              public void visitClassType(String name) {
                System.out.print("L" + name + ";");
              }

              @Override
              public void visitTypeArgument() {
                System.out.print("<>");
              }

              @Override
              public SignatureVisitor visitTypeArgument(char wildcard) {
                return new SignatureVisitor(Opcodes.ASM9) {
                  @Override
                  public void visitClassType(String name) {
                    System.out.print("L" + name + ";");
                  }
                };
              }

              @Override
              public void visitEnd() {
                System.out.println();
              }
            };
          }
        });
      }
    }
  }
}

