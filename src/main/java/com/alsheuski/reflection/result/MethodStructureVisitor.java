package com.alsheuski.reflection.result;

import com.alsheuski.reflection.result.model.Argument;
import com.alsheuski.reflection.result.model.MetaClass;
import com.alsheuski.reflection.result.model.Method;
import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class MethodStructureVisitor extends MethodVisitor {

  private final Map<String, MetaClass> outerClasses;
  private final ClassStructureVisitor nextLevelIterator;
  private final Predicate<String> classPathFilter;
  private final Method currentMethod;

  public MethodStructureVisitor(ClassStructureVisitor nextLevelIterator, Map<String, MetaClass> outerClasses, Method method, Predicate<String> classPathFilter) {
    super(Opcodes.ASM9);
    this.nextLevelIterator = nextLevelIterator;
    this.outerClasses = outerClasses;
    this.classPathFilter = classPathFilter;
    currentMethod = method;
  }

  @Override
  public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
    System.err.println();
    super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
  }

  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {

    if (!classPathFilter.test(owner)) {
      return;
    }

    if(!outerClasses.containsKey(owner) && nextLevelIterator!=null){
      try {
        nextLevelIterator.visitClass(owner);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    //Type.getMethodType(descriptor).getArgumentTypes()[0].getClassName();
    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
  }

  @Override
  public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
    if (currentMethod==null || name.equals("this")) {//todo think about constructors
      return;
    }
    var meta = signature == null ? descriptor : signature;
    var arg = new Argument(Type.getType(meta), name);
    currentMethod.addArgument(arg);
  }


}
