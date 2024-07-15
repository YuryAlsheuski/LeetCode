package com.alsheuski.reflection;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.util.InstructionFinder;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InstructionHandle;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;

public class BCELDisassembleClass {
  public static void main(String[] args) {

    String inputClassFile = "Main.class";
    String outputTextFile = "output.txt";

    try (FileInputStream fis = new FileInputStream(inputClassFile);
         FileWriter fw = new FileWriter(outputTextFile);
         PrintWriter pw = new PrintWriter(fw)) {

      ClassParser parser = new ClassParser(fis, inputClassFile);
      JavaClass javaClass = parser.parse();
      ClassGen classGen = new ClassGen(javaClass);

      pw.println("Class: " + classGen.getClassName());
      pw.println("Superclass: " + classGen.getSuperclassName());
      pw.println("Methods:");

      for (Method method : javaClass.getMethods()) {
        MethodGen methodGen = new MethodGen(method, classGen.getClassName(), classGen.getConstantPool());
        InstructionList instructionList = methodGen.getInstructionList();

        if (instructionList != null) {
          pw.println(" " + method.getName() + " " + method.getSignature());
          InstructionFinder finder = new InstructionFinder(instructionList);

          for (InstructionHandle handle : instructionList.getInstructionHandles()) {
            pw.println("  " + handle.toString());
          }
        }
      }

      System.out.println("Disassembled class saved to " + outputTextFile);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

