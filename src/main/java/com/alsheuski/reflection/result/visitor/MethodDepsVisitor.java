package com.alsheuski.reflection.result.visitor;

import static org.objectweb.asm.Opcodes.ASM9;

import com.alsheuski.reflection.result.model.Argument;
import com.alsheuski.reflection.result.model.Method;
import com.alsheuski.reflection.result.resolver.ClassTypeResolver;
import java.util.Arrays;
import java.util.function.Consumer;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

// todo IMPORTANT works fine but try to migrate to MethodNode
public class MethodDepsVisitor extends MethodVisitor {

  private final Method currentMethod;
  private final ClassTypeResolver typeResolver;
  private final Consumer<MethodMetadata> loaderAction;

  public MethodDepsVisitor(
      ClassTypeResolver typeResolver, Method currentMethod, Consumer<MethodMetadata> loaderAction) {

    super(ASM9);
    this.currentMethod = currentMethod;
    this.loaderAction = loaderAction;
    this.typeResolver = typeResolver;
  }

  @Override
  public void visitInvokeDynamicInsn(
      String name,
      String descriptor,
      Handle bootstrapMethodHandle,
      Object... bootstrapMethodArguments) {

    var maybeHandle =
        Arrays.stream(bootstrapMethodArguments)
            .filter(Handle.class::isInstance)
            .map(Handle.class::cast)
            .findFirst();

    maybeHandle.ifPresent(
        handle ->
            visitMethodInsn(
                handle.getTag(),
                handle.getOwner(),
                handle.getName(),
                handle.getDesc(),
                handle.isInterface())); // todo need to check if int handle.getTag()==int opcode for
  }

  @Override
  public void visitMethodInsn(
      int opcode, String owner, String name, String descriptor, boolean isInterface) {

    loaderAction.accept(new MethodMetadata(owner, name, descriptor));
  }

  @Override
  public void visitLocalVariable(
      String name, String descriptor, String signature, Label start, Label end, int index) {

    if (name.equals("this") || !currentMethod.isArgumentDescriptor(descriptor)) {
      return;
    }
    var type = typeResolver.getType(descriptor, signature);
    var arg = new Argument(type, name);
    currentMethod.addArgument(arg);
  }

  public class MethodMetadata {
    private final String owner;
    private final String name;
    private final String descriptor;

    public MethodMetadata(String owner, String name, String descriptor) {
      this.owner = owner;
      this.name = name;
      this.descriptor = descriptor;
    }

    public String getOwner() {
      return owner;
    }

    public String getName() {
      return name;
    }

    public String getDescriptor() {
      return descriptor;
    }
  }
}
