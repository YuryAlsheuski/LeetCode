package com.alsheuski.reflection;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;

public class DisassembleClass {
  public static void main(String[] args) {

    String inputClassFile = "Main.class";
    String outputTextFile = "output.txt";

    try (FileInputStream fis = new FileInputStream(inputClassFile);
         FileWriter fw = new FileWriter(outputTextFile);
         PrintWriter pw = new PrintWriter(fw)) {

      ClassReader classReader = new ClassReader(fis);
      TraceClassVisitor traceClassVisitor = new TraceClassVisitor(pw);
      classReader.accept(traceClassVisitor, 0);

      System.out.println("Disassembled class saved to " + outputTextFile);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
