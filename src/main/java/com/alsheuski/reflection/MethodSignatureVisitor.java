package com.alsheuski.reflection;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

public class MethodSignatureVisitor extends SignatureVisitor {

  public MethodSignatureVisitor() {
    super(Opcodes.ASM9);
  }

  @Override
  public void visitTypeVariable(String name) {
    System.err.println();
  }

  @Override
  public SignatureVisitor visitArrayType() {
    return new TypeArgumentVisitor();
  }

  @Override
  public SignatureVisitor visitTypeArgument(char wildcard) {
    return new TypeArgumentVisitor();
  }

  private static class TypeArgumentVisitor extends SignatureVisitor {

    public TypeArgumentVisitor() {
      super(Opcodes.ASM9);
    }
    @Override
    public void visitTypeVariable(String name) {
      System.err.println(); // typeParameters.add(currentType.toString());
    }

    @Override
    public SignatureVisitor visitArrayType() {
      return this;
    }

    @Override
    public SignatureVisitor visitTypeArgument(char wildcard) {
      return new TypeArgumentVisitor();
    }
  }

  public static void main(String[] args) {
   // testSignature("(TK;)TT;");
   // testSignature("(Ljava/util/String;)TT;");
    //testSignature("(Ljava/util/Map<TK;Ljava/util/String;>;)Ljava/util/Map<TOP;Ljava/util/String.Inner<TMM;TZS;>;>;");
//  testSignature("Ljava/util/List<TK;>;");
   testSignature("TK;");
  }

  public static void testSignature(String signature) {
    MethodSignatureVisitor extractor = new MethodSignatureVisitor();
    SignatureReader signatureReader = new SignatureReader(signature);
    signatureReader.accept(extractor);
  }
}


