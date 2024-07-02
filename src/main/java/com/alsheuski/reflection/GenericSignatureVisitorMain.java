package com.alsheuski.reflection;

import org.objectweb.asm.*;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenericSignatureVisitorMain {

  public static void main(String[] args) throws IOException {
    String className = "Common";
    String childClassName = "com/alsheuski/reflection/GenericChild";
    List<MethodNode> methods = getMethods(childClassName);
    printMethodsInClassScope(childClassName, methods);
  }

  public static List<MethodNode> getMethods(String className) throws IOException {
    List<MethodNode> methods = new ArrayList<>();
    String currentClassName = className;

    while (currentClassName != null && !currentClassName.equals("java/lang/Object")) {
      ClassReader classReader = new ClassReader(currentClassName);
      ClassNodeCollector classNodeCollector = new ClassNodeCollector();
      classReader.accept(classNodeCollector, 0);

      methods.addAll(classNodeCollector.getMethods());

      currentClassName = classNodeCollector.getSuperName();
    }

    return methods;
  }

  public static void printMethodsInClassScope(String className, List<MethodNode> methods) {
    System.out.println("public class " + className + " {");

    for (MethodNode method : methods) {
      if ((method.access & Opcodes.ACC_PUBLIC) != 0 || (method.access & Opcodes.ACC_PROTECTED) != 0) {
        System.out.println("    " + getMethodSignature(method) + ";");
      }
    }

    System.out.println("}");
  }

  public static String getMethodSignature(MethodNode method) {
    StringBuilder sb = new StringBuilder();

    if ((method.access & Opcodes.ACC_PUBLIC) != 0) {
      sb.append("public ");
    } else if ((method.access & Opcodes.ACC_PROTECTED) != 0) {
      sb.append("protected ");
    } else if ((method.access & Opcodes.ACC_PRIVATE) != 0) {
      sb.append("private ");
    }

    // Method return type and parameters with generics
    if (method.signature != null) {
      String methodSignature = getGenericMethodSignature(method.signature);
      sb.append(methodSignature);
    } else {
      Type methodType = Type.getMethodType(method.desc);
      sb.append(methodType.getReturnType().getClassName()).append(" ");
      sb.append(method.name).append("(");
      Type[] argumentTypes = methodType.getArgumentTypes();
      for (int i = 0; i < argumentTypes.length; i++) {
        if (i > 0) {
          sb.append(", ");
        }
        sb.append(argumentTypes[i].getClassName()).append(" arg").append(i);
      }
      sb.append(")");
    }

    return sb.toString();
  }

  public static String getGenericMethodSignature(String signature) {
    StringBuilder sb = new StringBuilder();
    SignatureReader signatureReader = new SignatureReader(signature);
    signatureReader.accept(new SignatureVisitor(Opcodes.ASM9) {
      @Override
      public SignatureVisitor visitReturnType() {
        sb.append(" ");
        return new SignatureVisitor(Opcodes.ASM9) {
          @Override
          public void visitClassType(String name) {
            sb.append(name.replace('/', '.'));
          }

          @Override
          public void visitTypeArgument() {
            sb.append("<");
          }

          @Override
          public SignatureVisitor visitTypeArgument(char wildcard) {
            return this;
          }

          @Override
          public void visitEnd() {
            sb.append(">");
          }
        };
      }

      @Override
      public SignatureVisitor visitParameterType() {
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '(') {
          sb.append(", ");
        }
        return new SignatureVisitor(Opcodes.ASM9) {
          @Override
          public void visitClassType(String name) {
            sb.append(name.replace('/', '.'));
          }

          @Override
          public void visitTypeArgument() {
            sb.append("<");
          }

          @Override
          public SignatureVisitor visitTypeArgument(char wildcard) {
            return this;
          }

          @Override
          public void visitEnd() {
            sb.append(">");
          }
        };
      }
    });
    return sb.toString();
  }

  static class ClassNodeCollector extends ClassVisitor {
    private String superName;
    private List<MethodNode> methods = new ArrayList<>();

    public ClassNodeCollector() {
      super(Opcodes.ASM9);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
      this.superName = superName;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
      MethodNode methodNode = new MethodNode(access, name, descriptor, signature, exceptions);
      methods.add(methodNode);
      return methodNode;
    }

    public String getSuperName() {
      return superName;
    }

    public List<MethodNode> getMethods() {
      return methods;
    }
  }
}
