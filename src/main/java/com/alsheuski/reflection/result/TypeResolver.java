package com.alsheuski.reflection.result;

import static com.alsheuski.reflection.result.util.LoaderUtil.parseGenericTypes;
import static org.objectweb.asm.Opcodes.ASM9;

import com.alsheuski.reflection.result.context.ClassLoadingContext;
import com.alsheuski.reflection.result.visitor.GenericArgsVisitor;
import java.util.Map;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

public class TypeResolver {

  private Map<String, String> formalToConcreteSignature;

  public Type getType(ClassLoadingContext context, String descriptor, String signature) {
    var classSignature = context.getCurrentClass().getSignature();

    if (classSignature != null && context.hasChild() && formalToConcreteSignature == null) {
      var childClassSignature = context.getChildClassContext().getCurrentClass().getSignature();
      var childClassSignatures = parseGenericTypes(childClassSignature);
      formalToConcreteSignature =
          new GenericArgsVisitor(childClassSignatures, classSignature).load();
    }

    if (signature == null) {
      return Type.getType(descriptor);
    }

    var resolver = new Resolver(signature);
    new SignatureReader(signature).accept(resolver);

    return Type.getType(resolver.getSignature());
  }

  private class Resolver extends SignatureVisitor {

    private String signature;

    public Resolver(String signature) {
      super(ASM9);
      this.signature = signature;
    }

    @Override
    public void visitTypeVariable(String name) {
      signature = signature.replace("T" + name, getSignature(name));
    }

    @Override
    public SignatureVisitor visitArrayType() {
      return new ArgumentVisitor();
    }

    @Override
    public SignatureVisitor visitTypeArgument(char wildcard) {
      return new ArgumentVisitor();
    }

    public String getSignature() {
      return signature;
    }

    private class ArgumentVisitor extends SignatureVisitor {

      public ArgumentVisitor() {
        super(ASM9);
      }

      @Override
      public void visitTypeVariable(String name) {
        signature = signature.replace("T" + name, getSignature(name));
      }

      @Override
      public SignatureVisitor visitArrayType() {
        return this;
      }

      @Override
      public SignatureVisitor visitTypeArgument(char wildcard) {
        return new ArgumentVisitor();
      }
    }

    private String getSignature(String name) {
      var typeParamSignature = formalToConcreteSignature.get(name);
      return typeParamSignature.substring(0, typeParamSignature.length() - 1);
    }
  }
}
