package com.alsheuski.reflection.result;

import static com.alsheuski.reflection.result.util.LoaderUtil.isConstructor;
import static org.objectweb.asm.ClassReader.EXPAND_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;

import com.alsheuski.reflection.result.config.ConfigManager;
import com.alsheuski.reflection.result.model.ClassLoadingQueue;
import com.alsheuski.reflection.result.model.MetaClass;
import com.alsheuski.reflection.result.model.Method;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class ClassStructureVisitor {

  private final Predicate<String> classPathFilter;
  private final Map<String, MetaClass> classNameToMetaClass;
  private final Set<String> currentLevelClasses;
  private final ConfigManager configManager;
  private final String root;
  private int deep;
  private ClassLoadingQueue nextLevelQueue;

  public ClassStructureVisitor(
      String root, ConfigManager configManager, Predicate<String> classPathFilter, int deep) {

    this.root = root;
    this.configManager = configManager;
    this.classPathFilter = classPathFilter;
    this.deep = deep;

    classNameToMetaClass = new HashMap<>();
    currentLevelClasses = new HashSet<>();
    nextLevelQueue = new ClassLoadingQueue(classPathFilter);
  }

  public Map<String, MetaClass> visitAll(String rootClass) {
    currentLevelClasses.add(rootClass);
    visit(rootClass);
    return classNameToMetaClass;
  }

  private MetaClass visit(String next) {
    try {
      currentLevelClasses.remove(next);
      var targetClass = new MetaClass(next);
      var className = targetClass.getFullName();
      var classPath = root + className + ".class";
      var classBytes = Files.readAllBytes(Paths.get(classPath));
      var classReader = new ClassReader(classBytes);

      if (!classNameToMetaClass.containsKey(className) && classPathFilter.test(className)) {
        classNameToMetaClass.put(className, targetClass);
      }
      classReader.accept(getInternalVisitor(targetClass), EXPAND_FRAMES);

      if (currentLevelClasses.isEmpty()) { // level completed
        deep = deep - 1;
        if (deep <= 0 || nextLevelQueue.isEmpty()) {
          return targetClass;
        }
        visitNextLevel();
      }
      return targetClass;
    } catch (IOException ex) {
      throw new RuntimeException("Build project or correct root path!", ex);
    }
  }

  private void visitNextLevel() {
    currentLevelClasses.addAll(nextLevelQueue.getClasses());

    var entrySet = nextLevelQueue.getEntries();
    nextLevelQueue = new ClassLoadingQueue(classPathFilter);

    for (var entry : entrySet) {
      var nextClassName = entry.getKey();
      var nextClass =
          classNameToMetaClass.containsKey(nextClassName)
              ? classNameToMetaClass.get(nextClassName)
              : visit(nextClassName);

      if (nextClass == null) {
        continue;
      }

      var nexLoaders = entry.getValue();
      nexLoaders.forEach(nextLoader -> nextLoader.accept(nextClass));
    }
  }

  private ClassVisitor getInternalVisitor(MetaClass targetClass) {
    return new ClassVisitor(Opcodes.ASM9) {

      @Override
      public ClassVisitor getDelegate() {
        return new ClassWriter(COMPUTE_FRAMES);
      }

      @Override
      public MethodVisitor visitMethod(
          int access, String name, String descriptor, String signature, String[] exceptions) {

        if (!configManager.getAccessFilter(targetClass.getFullName()).test(access)) {
          return null;
        }
        var method = getMethod(name, descriptor);
        return new MethodStructureVisitor(nextLevelQueue, targetClass, method);
      }

      private Method getMethod(String name, String descriptor) {
        if (!classNameToMetaClass.containsKey(targetClass.getFullName())) {
          return null;
        }
        var constructor = isConstructor(name);
        var methodName = constructor ? targetClass.getName() : name;
        var method =
            new Method(Type.getMethodType(descriptor).getReturnType(), methodName, constructor);
        targetClass.addMethod(method);
        return method;
      }
    };
  }
}
