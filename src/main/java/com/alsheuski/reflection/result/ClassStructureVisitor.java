package com.alsheuski.reflection.result;

import static com.alsheuski.reflection.result.util.LoaderUtil.getClassPath;
import static com.alsheuski.reflection.result.util.LoaderUtil.isConstructor;
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
import java.util.Optional;
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
    this(deep, new HashMap<>(), classPathFilter, accessFilter, null);
  }

  ClassStructureVisitor(
      int deep,
      Map<String, MetaClass> classNameToMetaClass,
      Predicate<String> classPathFilter,
      Predicate<Integer> accessFilter,
      MetaClass currentClass) {

    super(Opcodes.ASM9);

    this.classNameToMetaClass = classNameToMetaClass;
    this.classPathFilter = classPathFilter;
    this.accessFilter = accessFilter;
    this.deep = deep;
    this. currentClass =  currentClass;
    cv = new ClassWriter(COMPUTE_FRAMES);
  }

  @Override
  public MethodVisitor visitMethod(
      int access, String name, String descriptor, String signature, String[] exceptions) {

    if (!accessFilter.test(access)) {
      return null;
    }
    var method = getMethod(name, descriptor);
    return new MethodStructureVisitor(this, method);
  }

  public Map<String, MetaClass> visitAll(String rootClass) {
    var currentClass = new MetaClass(rootClass);
    this.currentClass=currentClass;
    visit(currentClass);
    return getClassNameToMetaClass();
  }

  Map<String, MetaClass> getClassNameToMetaClass() {
    return classNameToMetaClass;
  }

  Predicate<String> getClassPathFilter() {
    return classPathFilter;
  }

  Predicate<Integer> getAccessFilter() {
    return accessFilter;
  }
  
  MetaClass getCurrentClass() {
    return currentClass;
  }

  Optional<MetaClass> visitNext(String targetClassName) {
    var nextLevelDeep = deep - 1;
    if (nextLevelDeep < 0) {
      return Optional.empty();
    }

    var targetClass = new MetaClass(targetClassName);

    var nextLevelVisitor =
        new ClassStructureVisitor(
            nextLevelDeep, classNameToMetaClass, classPathFilter, accessFilter, targetClass);

    nextLevelVisitor.visit(targetClass);

    return Optional.of(targetClass);
  }

  void visit(MetaClass targetClass) {
    try {
      var className = targetClass.getFullName();
      var classPath = getClassPath(className);
      var classBytes = Files.readAllBytes(Paths.get(classPath));
      var classReader = new ClassReader(classBytes);

      if (!classNameToMetaClass.containsKey(className) && classPathFilter.test(className)) {
        classNameToMetaClass.put(className, targetClass);
      }
      classReader.accept(this, EXPAND_FRAMES);
    } catch (IOException ex) {
      System.err.println(ex.getMessage());
    }
  }

  private Method getMethod(String name, String descriptor) {
    if (!classNameToMetaClass.containsKey(currentClass.getFullName())) {
      return null;
    }
    var constructor = isConstructor(name);
    var methodName = constructor ? currentClass.getName() : name;
    var method =
        new Method(Type.getMethodType(descriptor).getReturnType(), methodName, constructor);
    currentClass.addMethod(method);
    return method;
  }

  public static void main(String[] args) {
    var className = "com/alsheuski/reflection/Common";
    Predicate<String> allowedClassPaths =
        path -> path.startsWith("com/alsheuski") && !path.startsWith(className);
    Predicate<Integer> accessFilter = accessCode -> accessCode != ACC_PRIVATE;
    var result = new ClassStructureVisitor(2, allowedClassPaths, accessFilter).visitAll(className);
    System.err.println(result);
  }
}
