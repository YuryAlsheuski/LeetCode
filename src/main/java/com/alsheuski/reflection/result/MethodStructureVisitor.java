package com.alsheuski.reflection.result;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodStructureVisitor extends MethodVisitor {
  private final int deep;
  public MethodStructureVisitor(MethodVisitor mv,int deep) {
    super(Opcodes.ASM9, mv);
    this.deep=deep;
  }

  @Override
  public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
    System.err.println();
    super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
  }

  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
    System.err.println();
    //Type.getMethodType(descriptor).getArgumentTypes()[0].getClassName();
    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
  }

  @Override
  public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
    super.visitLocalVariable(name, descriptor, signature, start, end, index);
  }
}
