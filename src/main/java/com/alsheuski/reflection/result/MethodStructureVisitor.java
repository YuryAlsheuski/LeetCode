package com.alsheuski.reflection.result;

import static com.alsheuski.reflection.result.util.LoaderUtil.getClassName;
import static com.alsheuski.reflection.result.util.LoaderUtil.isConstructor;
import static java.util.stream.Collectors.toList;

import com.alsheuski.reflection.result.model.Argument;
import com.alsheuski.reflection.result.model.Method;
import java.util.Arrays;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class MethodStructureVisitor extends MethodVisitor {

  private final ClassStructureVisitor classVisitor;
  private final Method currentMethod;

  public MethodStructureVisitor(ClassStructureVisitor classVisitor, Method method) {
    super(Opcodes.ASM9);
    this.classVisitor = classVisitor;
    currentMethod = method;
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

    if (!classVisitor.getClassPathFilter().test(owner)) {
      return;
    }
    var clazz = classVisitor.getClassNameToMetaClass().get(owner);
    if (clazz == null) {
      var maybeNextClass = classVisitor.visitNext(owner);
      if (maybeNextClass.isPresent()) {
        clazz = maybeNextClass.get();
      } else {
        System.err.println("Couldn't find class: " + owner);
        return;
      }
    }
    var type = Type.getMethodType(descriptor);
    var args =
        Arrays.stream(type.getArgumentTypes())
            .map(argType -> new Argument(argType, "stub"))
            .collect(toList());
    var methodName = isConstructor(name) ? getClassName(owner) : name;
    var maybeMethod = clazz.findMethod(type.getReturnType(), methodName, args);
    var callee = classVisitor.getCurrentClass();
    maybeMethod.ifPresent(method -> method.addCallFromClass(callee.getFullName()));

    // Type.getMethodType(descriptor).getArgumentTypes()[0].getClassName();
    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
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
