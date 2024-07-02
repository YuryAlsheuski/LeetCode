package com.alsheuski.reflection.result;

import static com.alsheuski.reflection.result.util.LoaderUtil.getType;
import static com.alsheuski.reflection.result.util.LoaderUtil.isConstructor;
import static org.objectweb.asm.ClassReader.EXPAND_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;

import com.alsheuski.reflection.result.config.ConfigManager;
import com.alsheuski.reflection.result.context.ClassLoadingContext;
import com.alsheuski.reflection.result.context.ClassLoadingQueue;
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

public class ClassStructureVisitor {

  private final Predicate<String> classPathFilter;
  private final Map<String, MetaClass> classNameToMetaClass;
  private final Set<String> currentLevelClasses;
  private final ConfigManager configManager;
  private final String rootClassPath;
  private int deep;
  private ClassLoadingQueue nextLevelQueue;
  private ClassLoadingContext rootClassLoadingContext;

  public ClassStructureVisitor(
      String rootClassPath,
      ConfigManager configManager,
      Predicate<String> classPathFilter,
      int deep) {

    this.rootClassPath = rootClassPath;
    this.configManager = configManager;
    this.classPathFilter = classPathFilter;
    this.deep = deep;

    classNameToMetaClass = new HashMap<>();
    currentLevelClasses = new HashSet<>();
    nextLevelQueue = new ClassLoadingQueue(classPathFilter);
  }

  public Map<String, MetaClass> printAllDeps(ClassLoadingContext context) {
    rootClassLoadingContext = context;
    currentLevelClasses.add(context.getClassFullName());
    visit(context);
    return classNameToMetaClass;
  }

  private MetaClass visit(ClassLoadingContext context) {
    try {
      currentLevelClasses.remove(context.getClassFullName());
      var targetClass = context.getCurrentClass();
      var className = targetClass.getFullName();
      var classPath = rootClassPath + className + ".class";
      var classBytes = Files.readAllBytes(Paths.get(classPath));
      var classReader = new ClassReader(classBytes);

      if (context.addToResults()
          && !classNameToMetaClass.containsKey(className)
          && classPathFilter.test(className)) {

        classNameToMetaClass.put(className, targetClass);
      }
      classReader.accept(getInternalVisitor(context), EXPAND_FRAMES);

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

      var nextClassContext =
          rootClassLoadingContext.getClassFullName().equals(nextClassName)
              ? rootClassLoadingContext
              : new ClassLoadingContext(nextClassName);

      var nextClass =
          classNameToMetaClass.containsKey(nextClassName)
              ? classNameToMetaClass.get(nextClassName)
              : visit(nextClassContext);

      if (nextClass == null) {
        continue;
      }

      var nexLoaders = entry.getValue();
      nexLoaders.forEach(nextLoader -> nextLoader.accept(nextClass));
    }
  }

  private ClassVisitor getInternalVisitor(ClassLoadingContext context) {
    return new ClassVisitor(Opcodes.ASM9) {

      @Override
      public void visit(
          int version,
          int access,
          String name,
          String signature,
          String superName,
          String[] interfaces) {

        if (classPathFilter.test(superName)) {
          ClassStructureVisitor.this.visit(new ClassLoadingContext(superName, context, true));
        }
      }

      @Override
      public ClassVisitor getDelegate() {
        return new ClassWriter(COMPUTE_FRAMES);
      }

      @Override
      public MethodVisitor visitMethod(
          int access, String name, String descriptor, String signature, String[] exceptions) {

        if (!configManager.getAccessFilter(context.getClassFullName()).test(access)) {
          return null;
        }
        var method = getMethod(access, name, descriptor, signature);
        return new MethodStructureVisitor(nextLevelQueue, context, method);
      }

      @Override
      public void visitEnd() {
        if (context.hasChild()) {
          var superClassMethods = context.getCurrentClass().getMethods();
          context.getChildClassContext().getCurrentClass().addMethods(superClassMethods);
        }
      }

      private Method getMethod(int access, String name, String descriptor, String signature) {
        var currentClass = context.getCurrentClass();
        var constructor = isConstructor(name);
        var methodName = constructor ? currentClass.getName() : name;
        var type = getType(descriptor, signature).getReturnType();
        var method =
            currentClass
                .findMethod(descriptor, name)
                .map(
                    existed -> {
                      existed.setReturnType(type);
                      return existed;
                    })
                .orElse(new Method(access, descriptor, type, methodName, constructor));
        currentClass.addMethod(method);

        return method;
      }
    };
  }
}
