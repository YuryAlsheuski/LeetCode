package com.alsheuski.reflection.result;

import static com.alsheuski.reflection.result.util.LoaderUtil.parseFormalTypeParameters;
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

    var resolver = new Resolver(formalToConcreteSignature, Map.of(), signature);
    new SignatureReader(signature).accept(resolver);
    var solvedSignature = resolver.getSignature();

    if (resolver.hasFormalArgs) { // for methods like: public <N> N get(){}
      var methodGenericArgs = parseFormalTypeParameters(solvedSignature);
      resolver = new Resolver(formalToConcreteSignature, methodGenericArgs, solvedSignature);
      new SignatureReader(solvedSignature).accept(resolver);
      solvedSignature = resolver.getSignature();
    }

    try {
      return Type.getType(solvedSignature);
    } catch (Exception ex) {
      // ignore
    }
    return Type.getMethodType(solvedSignature);
  }

  private class Resolver extends SignatureVisitor {

    private final Map<String, String> classGenericArgs;
    private final Map<String, String> methodGenericArgs;

    private String signature;
    private boolean hasFormalArgs;

    public Resolver(
        Map<String, String> classGenericArgs,
        Map<String, String> methodGenericArgs,
        String signature) {

      super(ASM9);
      this.classGenericArgs = classGenericArgs;
      this.methodGenericArgs = methodGenericArgs;
      this.signature = signature;
    }

    @Override
    public void visitFormalTypeParameter(String name) {
      hasFormalArgs = true;
    }

    @Override
    public void visitTypeVariable(String name) {
      var sign = getSignature(name);
      if (sign != null) {
        signature = signature.replace("T" + name, sign);
      }
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

    public boolean hasFormalArgs() {
      return hasFormalArgs;
    }

    private class ArgumentVisitor extends SignatureVisitor {

      public ArgumentVisitor() {
        super(ASM9);
      }

      @Override
      public void visitTypeVariable(String name) {
        var sign = getSignature(name);
        if (sign != null) {
          signature = signature.replace("T" + name, sign);
        }
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
      var typeParamSignature = classGenericArgs.get(name);
      if (typeParamSignature == null) {
        typeParamSignature = methodGenericArgs.get(name);
        if (typeParamSignature == null) {
          return null; // default for unsuported cases. One known case for methods
          // like: public <N> N get(){}
        }
      }
      return typeParamSignature.substring(0, typeParamSignature.length() - 1);
    }
  }
}
