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
import java.util.function.BiFunction;
import java.util.function.Predicate;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class ClassStructureVisitor {

  private final Predicate<String> classPathFilter;
  private final Predicate<Integer> accessFilter;
  private final Map<String, MetaClass> classNameToMetaClass;
  private final int deep;

  public ClassStructureVisitor(
      int deep, Predicate<String> classPathFilter, Predicate<Integer> accessFilter) {
    this(deep, new HashMap<>(), classPathFilter, accessFilter);
  }

  ClassStructureVisitor(
      int deep,
      Map<String, MetaClass> classNameToMetaClass,
      Predicate<String> classPathFilter,
      Predicate<Integer> accessFilter) {

    this.classNameToMetaClass = classNameToMetaClass;
    this.classPathFilter = classPathFilter;
    this.accessFilter = accessFilter;
    this.deep = deep;
  }

  public Map<String, MetaClass> visitAll(String rootClass) {
    visit(rootClass, rootClass);
    return classNameToMetaClass;
  }

  private MetaClass visit(String current, String next) {
    try {
      var targetClass = new MetaClass(next);
      var className = targetClass.getFullName();
      var classPath = getClassPath(className);
      var classBytes = Files.readAllBytes(Paths.get(classPath));
      var classReader = new ClassReader(classBytes);

      if (!classNameToMetaClass.containsKey(className) && classPathFilter.test(className)) {
        classNameToMetaClass.put(className, targetClass);
      }
      classReader.accept(getInternalVisitor(targetClass), EXPAND_FRAMES);
      return targetClass;
    } catch (IOException ex) {
      System.err.println(ex.getMessage());
    }
    return null;
  }

  private ClassVisitor getInternalVisitor(MetaClass currentClass) {
    return new ClassVisitor(Opcodes.ASM9) {

      @Override
      public ClassVisitor getDelegate() {
        return new ClassWriter(COMPUTE_FRAMES);
      }

      @Override
      public MethodVisitor visitMethod(
          int access, String name, String descriptor, String signature, String[] exceptions) {

        if (!accessFilter.test(access)) {
          return null;
        }
        var method = getMethod(name, descriptor);
        return new MethodStructureVisitor(getNextVisitor(), currentClass, method);
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

      private BiFunction<String, String, Optional<MetaClass>> getNextVisitor() {
        return (current, next) -> {
          if (!classPathFilter.test(next)) {
            return Optional.empty();
          }
          var clazz = classNameToMetaClass.get(next);
          if (clazz != null) {
            return Optional.of(clazz);
          }
          return Optional.ofNullable(ClassStructureVisitor.this.visit(current, next));
        };
      }
    };
  }

  public static void main(String[] args) {
    var className = "com/alsheuski/reflection/Common";
    Predicate<String> allowedClassPaths =
        path -> path.startsWith("com/alsheuski") && !path.startsWith(className);
    Predicate<Integer> accessFilter = accessCode -> accessCode != ACC_PRIVATE;
    var result = new ClassStructureVisitor(1, allowedClassPaths, accessFilter).visitAll(className);
    System.err.println(result);
  }
}
