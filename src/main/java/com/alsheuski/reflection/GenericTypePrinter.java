package com.alsheuski.reflection;

import org.objectweb.asm.*;

public class GenericTypePrinter {

  public static void main(String[] args) {
    String commonClassName = "com/alsheuski/reflection/Common";
    String genericParentClassName = "GenericParent";

    try {
      // Load the bytecode of the Common class
      ClassReader commonClassReader = new ClassReader(commonClassName);

      // Parse the class to find the concrete type
      ConcreteTypeFinder concreteTypeFinder = new ConcreteTypeFinder();
      commonClassReader.accept(concreteTypeFinder, 0);

      String concreteType = concreteTypeFinder.getConcreteType();
      if (concreteType != null) {
        // Print the modified method signature
        printModifiedGenericParent(concreteType);
      } else {
        System.out.println("Concrete type not found.");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void printModifiedGenericParent(String concreteType) {
    System.out.println("public class GenericParent {");
    System.out.println("  public " + concreteType + " get(){");
    System.out.println("    return null;");
    System.out.println("  }");
    System.out.println("}");
  }

  static class ConcreteTypeFinder extends ClassVisitor {
    private String concreteType;

    public ConcreteTypeFinder() {
      super(Opcodes.ASM9);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
      if ("<init>".equals(name)) {
        return new MethodVisitor(Opcodes.ASM9) {
          @Override
          public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            if ("get".equals(name) && "GenericChild".equals(owner)) {
              concreteType = extractConcreteType(descriptor);
            }
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
          }
        };
      }
      return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    public String getConcreteType() {
      return concreteType;
    }

    private String extractConcreteType(String descriptor) {
      int start = descriptor.indexOf('<') + 1;
      int end = descriptor.lastIndexOf('>');
      if (start > 0 && end > start) {
        String type = descriptor.substring(start, end).replace('/', '.');
        return type.replaceAll(";>", ";");
      }
      return null;
    }
  }
}

