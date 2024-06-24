package com.alsheuski.reflection.result;

import static com.alsheuski.reflection.result.util.LoaderUtil.getClassPath;
import static org.objectweb.asm.ClassReader.EXPAND_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;

import com.alsheuski.reflection.result.model.MetaClass;
import com.alsheuski.reflection.result.model.Method;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class ClassStructureVisitor extends ClassVisitor {

  private final Predicate<String> classPathFilter;
  private final Predicate<Integer> accessFilter;
  private final Map<String, MetaClass> classNameToMetaClass;
  private final int deep;
  private MetaClass currentClass;

  public ClassStructureVisitor(
      int deep, Predicate<String> classPathFilter, Predicate<Integer> accessFilter) {
    this(deep, new HashMap<>(), classPathFilter, accessFilter);
  }

  ClassStructureVisitor(
      int deep,
      Map<String, MetaClass> classNameToMetaClass,
      Predicate<String> classPathFilter,
      Predicate<Integer> accessFilter) {

    super(Opcodes.ASM9);

    this.classNameToMetaClass = classNameToMetaClass;
    this.classPathFilter = classPathFilter;
    this.accessFilter = accessFilter;
    this.deep = deep;
    cv = new ClassWriter(COMPUTE_FRAMES);
  }

  @Override
  public MethodVisitor visitMethod(
      int access, String name, String descriptor, String signature, String[] exceptions) {

    if (!accessFilter.test(access)) {
      return null;
    }
    return getMethodVisitorProvider().apply(getMethod(name, descriptor));
  }

  public void visitClass(String className) throws IOException {

    if (!classNameToMetaClass.containsKey(className) && classPathFilter.test(className)) {
      currentClass = new MetaClass(className);
      classNameToMetaClass.put(className, currentClass);
    }

    var classPath = getClassPath(className);
    var classBytes = Files.readAllBytes(Paths.get(classPath));
    var classReader = new ClassReader(classBytes);

    classReader.accept(this, EXPAND_FRAMES);

    System.err.println(classNameToMetaClass);
  }

  private Method getMethod(String name, String descriptor) {
    if (currentClass == null) {
      return null;
    }
    var method = new Method(Type.getMethodType(descriptor).getReturnType(), name);
    currentClass.addMethod(method);
    return method;
  }

  private Function<Method, MethodStructureVisitor> getMethodVisitorProvider() {

    int nextLevelDeep = deep - 1;
    var nextLevelVisitor =
        nextLevelDeep < 0
            ? null
            : new ClassStructureVisitor(
                nextLevelDeep, classNameToMetaClass, classPathFilter, accessFilter);

    return method ->
        new MethodStructureVisitor(nextLevelVisitor, classNameToMetaClass, method, classPathFilter);
  }

  public static void main(String[] args) throws IOException {
    var className = "com/alsheuski/reflection/Common";
    Predicate<String> allowedClassPaths =
        path -> path.startsWith("com/alsheuski") && !path.startsWith(className);
    Predicate<Integer> accessFilter = accessCode -> accessCode != ACC_PRIVATE;
    new ClassStructureVisitor(1, allowedClassPaths, accessFilter).visitClass(className);
  }
}
