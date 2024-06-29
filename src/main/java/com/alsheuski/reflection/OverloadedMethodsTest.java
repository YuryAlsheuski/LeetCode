package com.alsheuski.reflection;

import java.io.IOException;
import org.objectweb.asm.*;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.tree.*;

public class OverloadedMethodsTest {
  public static void main(String[] args) throws IOException {
    ClassReader classReader = new ClassReader("TargetClass");
    ClassNode classNode = new ClassNode();
    classReader.accept(classNode, 0);

    for (MethodNode method : classNode.methods) {
      if (method.name.equals("testExtractionFromOtherClass")) {
        method.accept(new MethodStructureVisitor());
      }
    }
  }

  static class MethodStructureVisitor extends MethodVisitor {
    public MethodStructureVisitor() {
      super(Opcodes.ASM9);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
      System.out.println("Method call: " + owner + "." + name + descriptor);

      // Resolve the actual argument types passed to the method call
      Type methodType = Type.getMethodType(descriptor);
      Type[] argumentTypes = methodType.getArgumentTypes();
      for (Type argType : argumentTypes) {
        System.out.println("Arg type: " + argType);
        if (argType.getSort() == Type.OBJECT) {
          System.out.println("Class name: " + argType.getClassName());
        }
      }

      // Attempt to resolve the method in TestClass with the correct generic types
      resolveMethodSignature(owner, name, descriptor);
    }

    private void resolveMethodSignature(String owner, String name, String descriptor) {
      try {
        ClassReader classReader = new ClassReader(owner.replace('/', '.'));
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        for (MethodNode method : classNode.methods) {
          if (method.name.equals(name) && method.desc.equals(descriptor)) {
            String methodSignature = method.signature;
            if (methodSignature != null) {
              System.out.println("Found method: " + method.name + method.desc);
              System.out.println("Method signature: " + methodSignature);
              printGenericTypes(methodSignature);
            } else {
              System.out.println("No generic type information available.");
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    private void printGenericTypes(String methodSignature) {
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
            public SignatureVisitor visitTypeArgument(char wildcard) {
              return new SignatureVisitor(Opcodes.ASM9) {
                @Override
                public void visitClassType(String name) {
                  System.out.print("<L" + name + ";>");
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
