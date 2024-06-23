package com.alsheuski.reflection.result;

import com.alsheuski.reflection.result.model.OuterClass;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.util.Map;

public class MethodStructureVisitor extends MethodVisitor {

  private final Map<String, OuterClass> outerClasses;
  private final ClassStructureIterator nextLevelIterator;
  private final String root;

  public MethodStructureVisitor(MethodVisitor mv, ClassStructureIterator nextLevelIterator, Map<String, OuterClass> outerClasses, String root) {
    super(Opcodes.ASM9, mv);
    this.nextLevelIterator = nextLevelIterator;
    this.outerClasses = outerClasses;
    this.root = root;
  }

  @Override
  public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
    System.err.println();
    super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
  }

  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
    System.err.println();
    var path = prepareClassPath(owner);
    if(!isProjectClass(path)){
      return;
    }

    /*if(!outerClasses.containsKey(path) && nextLevelIterator!=null){
      var classPath = "target/classes/"+owner+".class";
      try {
        nextLevelIterator.loadClass(classPath);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }*/


    //Type.getMethodType(descriptor).getArgumentTypes()[0].getClassName();
    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
  }

  @Override
  public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
    super.visitLocalVariable(name, descriptor, signature, start, end, index);
  }

  private String prepareClassPath(String path) {
    return path.replace('/', '.');
  }

  private boolean isProjectClass(String className) {
    return className.startsWith(root);
  }
}
