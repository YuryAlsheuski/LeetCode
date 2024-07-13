package com.alsheuski.reflection.result;

import static com.alsheuski.reflection.result.util.LoaderUtil.parseFormalTypeParameters;
import static com.alsheuski.reflection.result.util.LoaderUtil.parseGenericMethodPrefix;
import static com.alsheuski.reflection.result.util.LoaderUtil.parseGenericMethodReturnType;
import static com.alsheuski.reflection.result.util.LoaderUtil.removeGenericClassPrefix;
import static org.objectweb.asm.Opcodes.ASM9;

import com.alsheuski.reflection.result.context.ClassLoadingContext;
import com.alsheuski.reflection.result.model.ResultType;
import com.alsheuski.reflection.result.model.Signature;
import com.alsheuski.reflection.result.visitor.GenericArgsVisitor;
import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

public class TypeResolver {

  private final Map<String, String> formalToConcreteSignature = new HashMap<>(3);

  public void resolveClassSignature(ClassLoadingContext context) {
    var classSignature = context.getLoadingContextSignature();
    if (classSignature == null) {
      return;
    }
    if (!context.hasChild()) {
      // var signature = getClassSignature(classSignature, classSignature);
      context.getCurrentClass().setSignature(new Signature(classSignature));
      return;
    }
    var childClassSignature = context.getChildClassContext().getCurrentClass().getSignature();
    var preparedChildSignature =
        getClassSignature(childClassSignature.getValue(), childClassSignature.getValue());
    var solvedSignature = getClassSignature(preparedChildSignature.getValue(), classSignature);
    context.getCurrentClass().setSignature(solvedSignature);
  }

  private Signature getClassSignature(String childSignature, String parentSignature) {
    var signDict = new GenericArgsVisitor(childSignature, parentSignature).load();
    formalToConcreteSignature.putAll(signDict);
    var resolver = getResolver(parentSignature);
    var solvedSignature = resolver.getSignature();
    if (solvedSignature.hasFormalArgs()) {
      String result = removeGenericClassPrefix(solvedSignature.getValue());
      return new Signature(result);
    }
    return solvedSignature;
  }

  public ResultType getType(String descriptor, String signature) {
    if (signature == null) {
      return new ResultType(Type.getType(descriptor));
    }
    var resolver = getResolver(signature);
    var solvedSignature = resolver.getSignature().getValue();
    return new ResultType(Type.getType(solvedSignature));
  }

  public ResultType getMethodReturnType(String descriptor, String signature) {
    if (signature == null) {
      return new ResultType(Type.getMethodType(descriptor).getReturnType());
    }
    var resolver = getResolver(signature);
    var solvedSignature = resolver.getSignature().getValue();

    if (resolver.hasFormalArgs) {
      var methodGenericArgs = parseFormalTypeParameters(solvedSignature);
      var genericPrefix =
          parseGenericMethodPrefix(methodGenericArgs).map(prefix -> prefix + " ").orElse("");
      var returnType = parseGenericMethodReturnType(solvedSignature);
      return new ResultType(String.format("%s%s", genericPrefix, returnType));
    }

    return new ResultType(Type.getMethodType(solvedSignature).getReturnType());
  }

  private Resolver getResolver(String signature) {
    var resolver = new Resolver(signature);
    new SignatureReader(signature).accept(resolver);
    return resolver;
  }

  private class Resolver extends SignatureVisitor {

    private String signature;
    private boolean hasFormalArgs;

    public Resolver(String signature) {

      super(ASM9);
      this.signature = signature;
    }

    @Override
    public void visitFormalTypeParameter(String name) {
      hasFormalArgs = true;
    }

    @Override
    public void visitTypeVariable(String name) {
      replace(name);
    }

    @Override
    public SignatureVisitor visitArrayType() {
      return new ArgumentVisitor();
    }

    @Override
    public SignatureVisitor visitTypeArgument(char wildcard) {
      return new ArgumentVisitor();
    }

    public Signature getSignature() {
      return new Signature(signature, hasFormalArgs);
    }

    private class ArgumentVisitor extends SignatureVisitor {

      public ArgumentVisitor() {
        super(ASM9);
      }

      @Override
      public void visitTypeVariable(String name) {
        replace(name);
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

    private void replace(String name) {
      var sign = getSignature(name);
      if (sign == null) {
        hasFormalArgs = true;
        sign = name;
      }
      signature = signature.replace("T" + name, sign);
    }

    private String getSignature(String name) {
      if (!formalToConcreteSignature.containsKey(name)) {
        return null; // default for unsuported cases. One known case for methods
        // like: public <N> N get(){}
      }
      var typeParamSignature = formalToConcreteSignature.get(name);
      return typeParamSignature.substring(0, typeParamSignature.length() - 1);
    }
  }
}
