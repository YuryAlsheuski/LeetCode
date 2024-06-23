package com.alsheuski.reflection.result;

import com.alsheuski.reflection.result.model.OuterClass;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;

public class ClassStructureIterator extends ClassVisitor {

  private final Map<String, OuterClass> outerClasses;
  private final ClassStructureIterator nextLevelIterator;
  private final String root;

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
    var mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
    return new MethodStructureVisitor(mv, nextLevelIterator, outerClasses, root);
  }

  public void loadClass(String className) throws IOException {
    var classBytes = Files.readAllBytes(Paths.get(className));
    var classReader = new ClassReader(classBytes);
    classReader.accept(this, EXPAND_FRAMES);
  }

  public static void main(String[] args) throws IOException {
    var className = "target/classes/com/alsheuski/reflection/Common.class";
    new ClassStructureIterator(1, "com.alsheuski").loadClass(className);
    // Write the modified class
    //  byte[] modifiedClass = classWriter.toByteArray();
    // Files.write(Paths.get(className + "_Modified.class"), modifiedClass);

  }
}
