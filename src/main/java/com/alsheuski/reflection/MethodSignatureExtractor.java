package com.alsheuski.reflection;

import org.objectweb.asm.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MethodSignatureExtractor extends ClassVisitor {
  private Set<String> methodSignatures = new HashSet<>();
  private String targetClassName = "target/classes/com/alsheuski/reflection/ClassB.class"; // Replace with the fully qualified name of your target class
  private Map<String, MethodData> methodDataMap = new HashMap<>();

  public MethodSignatureExtractor(ClassVisitor cv) {
    super(Opcodes.ASM9, cv);
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
    MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
    if ((access & Opcodes.ACC_STATIC) == 0 || (access & Opcodes.ACC_STATIC) == 1) {
      MethodData methodData = new MethodData(name, descriptor);
      methodDataMap.put(name + descriptor, methodData);
      return new LocalVariableTableVisitor(mv, methodData, (access & Opcodes.ACC_STATIC) != 0);
    }
    return mv;
  }

  private class MethodInvocationVisitor extends MethodVisitor {
    public MethodInvocationVisitor(MethodVisitor mv) {
      super(Opcodes.ASM9, mv);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
      if (owner.equals(targetClassName.replace('.', '/'))) {
        methodSignatures.add(getMethodSignature(owner, name, descriptor));
      }
      super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }
  }

  private class LocalVariableTableVisitor extends MethodVisitor {
    private final MethodData methodData;
    private final boolean isStatic;
    private final Set<String> localVars = new HashSet<>();

    public LocalVariableTableVisitor(MethodVisitor mv, MethodData methodData, boolean isStatic) {
      super(Opcodes.ASM9, mv);
      this.methodData = methodData;
      this.isStatic = isStatic;
    }

    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
      if ((isStatic && index < methodData.argumentTypes.length) ||
              (!isStatic && index <= methodData.argumentTypes.length && index > 0)) {
        methodData.argumentNames[index - (isStatic ? 0 : 1)] = name;
      }
      super.visitLocalVariable(name, descriptor, signature, start, end, index);
    }
  }

  private String getMethodSignature(String owner, String name, String descriptor) {
    MethodData methodData = methodDataMap.get(name + descriptor);
    if (methodData == null) {
      return null;
    }

    Type returnType = Type.getReturnType(descriptor);
    StringBuilder signatureBuilder = new StringBuilder();
    signatureBuilder.append("class ").append(owner.replace('/', '.')).append(" {\n");
    signatureBuilder.append("  public ").append(returnType.getClassName()).append(" ").append(name).append("(");
    for (int i = 0; i < methodData.argumentTypes.length; i++) {
      if (i > 0) {
        signatureBuilder.append(", ");
      }
      signatureBuilder.append(methodData.argumentTypes[i].getClassName()).append(" ").append(methodData.argumentNames[i]);
    }
    signatureBuilder.append(");\n");
    signatureBuilder.append("}");
    return signatureBuilder.toString();
  }

  public Set<String> getMethodSignatures() {
    return methodSignatures;
  }

  public static void main(String[] args) throws IOException {
    String className = "target/classes/com/alsheuski/reflection/ClassB"; // Replace with the fully qualified name of your Common class
    byte[] classBytes = Files.readAllBytes(Paths.get(className.replace('.', '/') + ".class"));

    ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    MethodSignatureExtractor extractor = new MethodSignatureExtractor(classWriter);

    ClassReader classReader = new ClassReader(classBytes);
    classReader.accept(extractor, ClassReader.EXPAND_FRAMES);

    Set<String> methodSignatures = extractor.getMethodSignatures();
    for (String signature : methodSignatures) {
      System.out.println(signature);
    }
  }

  private static class MethodData {
    String name;
    String descriptor;
    Type[] argumentTypes;
    String[] argumentNames;

    public MethodData(String name, String descriptor) {
      this.name = name;
      this.descriptor = descriptor;
      this.argumentTypes = Type.getArgumentTypes(descriptor);
      this.argumentNames = new String[argumentTypes.length];
    }
  }
}


