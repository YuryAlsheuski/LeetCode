package com.alsheuski.reflection;

import org.objectweb.asm.*;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

public class SignatureParser {

  public static void main(String[] args) {
    String signature = "<N::Ljava/util/Queue<Ljava/util/List<Ljava/lang/String;>;>;>(Ljava/lang/String;Ljava/util/List<Ljava/util/Map<Ljava/lang/String;Ljava/lang/Integer;>;>;)Ljava/lang/Object;";

    // Create a SignatureReader to read the signature string
    SignatureReader signatureReader = new SignatureReader(signature);

    // Create a SignatureVisitor to visit the formal type parameter
    FormalTypeParameterVisitor visitor = new FormalTypeParameterVisitor();

    // Accept the signature reader with the visitor
    signatureReader.accept(visitor);

    // Get the parsed formal type parameter
    String formalTypeParameter = visitor.getFormalTypeParameter();

    // Print the result
    System.out.println("Output: " + formalTypeParameter);
  }

  static class FormalTypeParameterVisitor extends SignatureVisitor {
    private StringBuilder formalTypeParameterBuilder = new StringBuilder();

    public FormalTypeParameterVisitor() {
      super(Opcodes.ASM9);
    }

    @Override
    public void visitFormalTypeParameter(String name) {
      formalTypeParameterBuilder.append(name).append("::");
    }

    @Override
    public SignatureVisitor visitClassBound() {
      return new TypeSignatureVisitor();
    }

    @Override
    public SignatureVisitor visitInterfaceBound() {
      return new TypeSignatureVisitor();
    }

    public String getFormalTypeParameter() {
      return formalTypeParameterBuilder.toString();
    }

    class TypeSignatureVisitor extends SignatureVisitor {
      public TypeSignatureVisitor() {
        super(Opcodes.ASM9);
      }

      @Override
      public void visitClassType(String name) {
        formalTypeParameterBuilder.append(name);
      }

      @Override
      public void visitTypeArgument() {
        formalTypeParameterBuilder.append('<');
      }

      @Override
      public SignatureVisitor visitTypeArgument(char wildcard) {
        formalTypeParameterBuilder.append(wildcard);
        return this;
      }

      @Override
      public void visitEnd() {
        formalTypeParameterBuilder.append(';');
      }
    }
  }
}
