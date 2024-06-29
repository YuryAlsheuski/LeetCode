package com.alsheuski.reflection.result;

import static com.alsheuski.reflection.result.util.LoaderUtil.getType;
import static com.alsheuski.reflection.result.util.LoaderUtil.isConstructor;

import com.alsheuski.reflection.result.model.Argument;
import com.alsheuski.reflection.result.model.ClassLoadingQueue;
import com.alsheuski.reflection.result.model.MetaClass;
import com.alsheuski.reflection.result.model.Method;
import java.util.Arrays;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodStructureVisitor extends MethodVisitor {

  private final ClassLoadingQueue nextLevelQueue;
  private final MetaClass currentClass;
  private final Method currentMethod;

  public MethodStructureVisitor(
      ClassLoadingQueue nextLevelQueue, MetaClass currentClass, Method currentMethod) {

    super(Opcodes.ASM9);
    this.nextLevelQueue = nextLevelQueue;
    this.currentClass = currentClass;
    this.currentMethod = currentMethod;
  }

  @Override
  public void visitInvokeDynamicInsn(
      String name,
      String descriptor,
      Handle bootstrapMethodHandle,
      Object... bootstrapMethodArguments) {

    var maybeHandle =
        Arrays.stream(bootstrapMethodArguments)
            .filter(args -> args instanceof Handle)
            .map(handle -> (Handle) handle)
            .findFirst();

    maybeHandle.ifPresent(
        handle ->
            visitMethodInsn(
                handle.getTag(),
                handle.getOwner(),
                handle.getName(),
                handle.getDesc(),
                handle.isInterface())); // todo need to check if int handle.getTag()==int opcode for
    // next method
  }

  @Override
  public void visitMethodInsn(
      int opcode, String owner, String name, String descriptor, boolean isInterface) {

    nextLevelQueue.add(
        owner,
        nextClazz -> {
          var methodName = isConstructor(name) ? nextClazz.getName() : name;
          var maybeMethod = nextClazz.findMethod(descriptor, methodName);

          maybeMethod.ifPresent(method -> method.addCallFromClass(currentClass.getFullName()));
        });
  }

  @Override
  public void visitLocalVariable(
      String name, String descriptor, String signature, Label start, Label end, int index) {
    if (currentMethod == null || name.equals("this")) {
      return;
    }
    var arg = new Argument(getType(descriptor, signature), name);
    currentMethod.addArgument(arg);
  }
}
