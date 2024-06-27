package com.alsheuski.reflection.result;

import static com.alsheuski.reflection.result.util.LoaderUtil.isConstructor;
import static java.util.stream.Collectors.toList;

import com.alsheuski.reflection.result.model.Argument;
import com.alsheuski.reflection.result.model.ClassLoadingQueue;
import com.alsheuski.reflection.result.model.MetaClass;
import com.alsheuski.reflection.result.model.Method;
import java.util.Arrays;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

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

    System.err.println();
    super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
  }

  @Override
  public void visitMethodInsn(
      int opcode, String owner, String name, String descriptor, boolean isInterface) {

    nextLevelQueue.add(
        owner,
        nextClazz -> {
          var type = Type.getMethodType(descriptor);
          var args =
              Arrays.stream(type.getArgumentTypes())
                  .map(argType -> new Argument(argType, "stub"))
                  .collect(toList());
          var methodName = isConstructor(name) ? nextClazz.getName() : name;
          var maybeMethod = nextClazz.findMethod(type.getReturnType(), methodName, args);

          maybeMethod.ifPresent(method -> method.addCallFromClass(currentClass.getFullName()));

          // Type.getMethodType(descriptor).getArgumentTypes()[0].getClassName();
          // super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        });
  }

  @Override
  public void visitLocalVariable(
      String name, String descriptor, String signature, Label start, Label end, int index) {
    if (currentMethod == null || name.equals("this")) { // todo think about constructors
      return;
    }
    var meta = signature == null ? descriptor : signature;
    var arg = new Argument(Type.getType(meta), name);
    currentMethod.addArgument(arg);
  }
}
