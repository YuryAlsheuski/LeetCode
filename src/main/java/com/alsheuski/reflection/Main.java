package com.alsheuski.reflection;
import java.io.IOException;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

public class Main {
  public static void main(String[] args) throws Exception {
    analyzeClass(Common.class);
  }

  public static void analyzeClass(Class<?> clazz) throws IOException {
    ClassReader classReader = new ClassReader(clazz.getName());
    ClassNode classNode = new ClassNode();
    classReader.accept(classNode, 0);

    for (MethodNode method : classNode.methods) {
      analyzeMethod(clazz, method);
    }

  /*  // Analyze synthetic methods
    for (MethodNode method : classNode.methods) {
      if ((method.access & Opcodes.ACC_SYNTHETIC) != 0) {
        analyzeMethod(clazz, method);
      }
    }*/
  }

  public static void analyzeMethod(Class<?> clazz, MethodNode method) {
    InsnList instructions = method.instructions;
    int currentLine = -1;
    for (AbstractInsnNode instruction : instructions) {
      if (instruction instanceof LineNumberNode) {
        LineNumberNode lineNumberNode = (LineNumberNode) instruction;
        currentLine = lineNumberNode.line;
      } else if (instruction instanceof MethodInsnNode) {
        MethodInsnNode methodInsn = (MethodInsnNode) instruction;
        printMethodCall(methodInsn, currentLine);
      } else if (instruction instanceof TypeInsnNode) {
        TypeInsnNode typeInsn = (TypeInsnNode) instruction;
        if (typeInsn.getOpcode() == Opcodes.NEW) {
          System.out.println("Line " + currentLine + ": Called constructor: " + typeInsn.desc.replace('/', '.'));
        }
      }
    }
  }

  public static void printMethodCall(MethodInsnNode methodInsn, int currentLine) {
    String owner = methodInsn.owner.replace('/', '.');
    String name = methodInsn.name;
    String desc = methodInsn.desc;

    if (name.equals("<init>")) {
      System.out.println("public class " + owner + " {");
      System.out.println("    public " + owner.substring(owner.lastIndexOf('.') + 1) + "(" + desc + ") {");
      System.out.println("        // some content here");
      System.out.println("    }");
      System.out.println("}");
    } else {
      System.out.println("Line " + currentLine + ": Called method: " + owner + "." + name + "()");
      System.out.println("public class " + owner + " {");
      System.out.println("    public " + name + "();");
      System.out.println("}");
    }
  }
}


