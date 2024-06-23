package com.alsheuski.reflection.result;

import com.alsheuski.reflection.result.model.Argument;
import com.alsheuski.reflection.result.model.Method;
import com.alsheuski.reflection.result.model.OuterClass;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Map;

import static com.alsheuski.reflection.result.util.LoaderUtil.isProjectClass;
import static com.alsheuski.reflection.result.util.LoaderUtil.prepareClassPath;

public class MethodStructureVisitor extends MethodVisitor {

  private final Map<String, OuterClass> outerClasses;
  private final ClassStructureIterator nextLevelIterator;
  private final String root;
  private final Method currentMethod;

  public MethodStructureVisitor(ClassStructureIterator nextLevelIterator, Map<String, OuterClass> outerClasses, Method method, String root) {
    super(Opcodes.ASM9);
    this.nextLevelIterator = nextLevelIterator;
    this.outerClasses = outerClasses;
    this.root = root;
    currentMethod = method;
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
    if (!isProjectClass(path, root)) {
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
    if (name.equals("this")) {//todo think about constructors
      return;
    }
    var meta = signature == null ? descriptor : signature;
    var arg = new Argument(Type.getType(meta), name);
    currentMethod.addArgument(arg);
  }


}
