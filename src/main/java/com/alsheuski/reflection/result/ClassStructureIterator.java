package com.alsheuski.reflection.result;

import com.alsheuski.reflection.result.model.Method;
import com.alsheuski.reflection.result.model.OuterClass;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static com.alsheuski.reflection.result.util.LoaderUtil.prepareClassPath;
import static org.objectweb.asm.ClassReader.EXPAND_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;

public class ClassStructureIterator extends ClassVisitor {

  private final Map<String, OuterClass> outerClasses;
  private final ClassStructureIterator nextLevelIterator;
  private final String root;
  private OuterClass currentClass;

  public ClassStructureIterator(int deep, String root) throws IOException {
    this(deep, new HashMap<>(), root);
  }

  ClassStructureIterator(int deep, Map<String, OuterClass> outerClasses, String root) throws IOException {
    super(Opcodes.ASM9);
    int nextLevelDeep = deep - 1;
    nextLevelIterator = nextLevelDeep < 0 ? null : new ClassStructureIterator(nextLevelDeep, outerClasses, root);
    this.outerClasses = outerClasses;
    cv = new ClassWriter(COMPUTE_FRAMES);
    this.root = root;
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
    var method = new Method(Type.getMethodType(descriptor).getReturnType(), name);
    currentClass.addMethod(method);
    return new MethodStructureVisitor(nextLevelIterator, outerClasses, method, root);
  }

  public void loadClass(String className) throws IOException {
    var currentClassName = prepareClassPath(className);
    currentClass = outerClasses.get(currentClassName);
    if (currentClass == null) {
      currentClass = new OuterClass(currentClassName);
    }
    outerClasses.put(currentClassName, currentClass);

    var classPath = "target/classes/" + className + ".class";
    var classBytes = Files.readAllBytes(Paths.get(classPath));
    var classReader = new ClassReader(classBytes);

    classReader.accept(this, EXPAND_FRAMES);

    System.err.println(outerClasses);
  }

  public static void main(String[] args) throws IOException {
    var className = "com/alsheuski/reflection/Common";
    new ClassStructureIterator(1, "com.alsheuski").loadClass(className);
    // Write the modified class
    //  byte[] modifiedClass = classWriter.toByteArray();
    // Files.write(Paths.get(className + "_Modified.class"), modifiedClass);

  }
}
