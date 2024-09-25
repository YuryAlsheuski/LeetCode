package com.alsheuski.reflection.result.visitor;

import com.alsheuski.reflection.result.context.ClassLoadingContext;
import com.alsheuski.reflection.result.model.Argument;
import com.alsheuski.reflection.result.model.MetaClass;
import com.alsheuski.reflection.result.model.Method;
import com.alsheuski.reflection.result.resolver.ClassTypeResolver;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.alsheuski.reflection.result.util.LoaderUtil.isConstructor;
import static org.objectweb.asm.Opcodes.ASM9;

// todo IMPORTANT works fine but try to migrate to MethodNode
public class MethodDepsVisitor extends MethodVisitor {

  private final Map<String, List<Consumer<MetaClass>>> nextLevelQueue;
  private final ClassLoadingContext context;
  private final Method currentMethod;
  private final ClassTypeResolver typeResolver;

  public MethodDepsVisitor(
      ClassTypeResolver typeResolver,
      Map<String, List<Consumer<MetaClass>>> nextLevelQueue,
      ClassLoadingContext context,
      Method currentMethod) {

    super(ASM9);
    this.nextLevelQueue = nextLevelQueue;
    this.context = context;
    this.currentMethod = currentMethod;
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

    var ownerName = Path.of(owner).toString();
    if (context.hasChild()) {
      return;
    }
    var actions = nextLevelQueue.computeIfAbsent(ownerName, k -> new ArrayList<>());
    actions.add(
        nextClazz -> {
          var methodName = isConstructor(name) ? nextClazz.getName() : name;
          var maybeMethod = nextClazz.findMethod(descriptor, methodName);

          maybeMethod.ifPresent(method -> method.addCallFromClass(context.getClassFullName()));
        });
    nextLevelQueue.put(ownerName, actions);
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
}
