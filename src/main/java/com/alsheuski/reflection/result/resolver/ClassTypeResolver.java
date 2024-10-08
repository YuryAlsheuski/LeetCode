package com.alsheuski.reflection.result.resolver;

import static com.alsheuski.reflection.result.util.LoaderUtil.getClassName;
import static com.alsheuski.reflection.result.util.LoaderUtil.parseFormalTypeParameters;
import static com.alsheuski.reflection.result.util.LoaderUtil.parseGenericMethodPrefix;
import static com.alsheuski.reflection.result.util.LoaderUtil.parseGenericMethodReturnType;
import static com.alsheuski.reflection.result.util.LoaderUtil.removeGenericClassPrefix;
import static org.objectweb.asm.Opcodes.ASM9;

import com.alsheuski.reflection.result.context.ClassLoadingContext;
import com.alsheuski.reflection.result.model.ResultType;
import com.alsheuski.reflection.result.visitor.GenericArgsVisitor;
import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

public class ClassTypeResolver {

  private final Map<String, String> formalToConcreteSignature = new HashMap<>(3);

  public void resolveClassSignature(ClassLoadingContext context) {
    var classSignature = context.getLoadingContextSignature();
    if (classSignature == null) {
      return;
    }
    if (!context.hasChild()) {
      context.getCurrentClass().setSignature(classSignature);
      return;
    }
    var childClassSignature = context.getChildClassContext().getCurrentClass().getSignature();
    var preparedChildSignature = solve(childClassSignature);

    var signDict = new GenericArgsVisitor(preparedChildSignature, classSignature).load();
    formalToConcreteSignature.putAll(signDict);

    var solvedSignature = solve(classSignature);
    context.getCurrentClass().setSignature(solvedSignature);
  }

  private String solve(String parentSignature) {
    var resolver = getResolver(parentSignature);
    var solvedSignature = resolver.getSignature();
    if (resolver.hasFormalArgs) {
      return removeGenericClassPrefix(solvedSignature);
    }
    return solvedSignature;
  }

  public ResultType getType(String descriptor, String signature) {
    if (signature == null) {
      return new ResultType(Type.getType(descriptor));
    }
    var resolver = getResolver(signature);
    var solvedSignature = resolver.getSignature();
    if (resolver.hasFormalArgs) {
      return new ResultType(getClassName(solvedSignature));
    }
    return new ResultType(Type.getType(solvedSignature));
  }

  public ResultType getMethodReturnType(String descriptor, String signature) {

    if (signature == null) {
      return new ResultType(Type.getMethodType(descriptor).getReturnType());
    }
    try {//try to get void type or all primitive types
      var type = Type.getMethodType(signature).getReturnType();
      var hasGenericParams = type.getClassName().contains("<");
      if (!hasGenericParams) {
        return new ResultType(type);
      }

    } catch (Exception ex) { // ignore
    }

    var resolver = getResolver(signature);
    var solvedSignature = resolver.getSignature();

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

    public String getSignature() {
      return signature;
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
