package com.alsheuski.reflection.result.visitor;

import static com.alsheuski.reflection.result.util.LoaderUtil.isConstructor;
import static com.alsheuski.reflection.result.util.LoaderUtil.loadClass;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.Opcodes.ASM9;

import com.alsheuski.reflection.result.config.ConfigManager;
import com.alsheuski.reflection.result.context.ClassLoadingContext;
import com.alsheuski.reflection.result.context.ClassLoadingQueue;
import com.alsheuski.reflection.result.model.MetaClass;
import com.alsheuski.reflection.result.model.Method;
import com.alsheuski.reflection.result.resolver.TypeResolver;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class ClassDepsVisitor {

  private final Predicate<String> classPathFilter;
  private final Map<String, MetaClass> classNameToMetaClass;
  private final Set<String> currentLevelClasses;
  private final ConfigManager configManager;
  private final String rootClassPath;
  private int deep;
  private ClassLoadingQueue nextLevelQueue;
  private ClassLoadingContext rootClassLoadingContext;

  public ClassDepsVisitor(String rootClassPath, ConfigManager configManager, int deep) {

    this.rootClassPath = rootClassPath;
    this.configManager = configManager;
    this.deep = deep;
    classPathFilter = configManager.getAllowedClassPaths();
    classNameToMetaClass = new HashMap<>();
    currentLevelClasses = new HashSet<>();
    nextLevelQueue = new ClassLoadingQueue(classPathFilter);
  }

  public Map<String, MetaClass> getAllDeps(ClassLoadingContext context) {
    rootClassLoadingContext = context;
    currentLevelClasses.add(context.getClassFullName());
    visit(context);
    return classNameToMetaClass;
  }

  private MetaClass visit(ClassLoadingContext context) {

    currentLevelClasses.remove(context.getClassFullName());
    var targetClass = context.getCurrentClass();
    var className = targetClass.getFullName();

    if (context.addToResults()
        && !classNameToMetaClass.containsKey(className)
        && classPathFilter.test(className)) {

      classNameToMetaClass.put(className, targetClass);
    }
    var classPath = rootClassPath + className + ".class";

    loadClass(classPath, getInternalVisitor(context));

    if (currentLevelClasses.isEmpty()) { // level completed
      deep = deep - 1;
      if (deep <= 0 || nextLevelQueue.isEmpty()) {
        return targetClass;
      }
      visitNextLevel();
    }
    return targetClass;
  }

  private void visitNextLevel() {
    currentLevelClasses.addAll(nextLevelQueue.getClasses());

    var entrySet = nextLevelQueue.getEntries();
    nextLevelQueue = new ClassLoadingQueue(classPathFilter);

    for (var entry : entrySet) {
      var nextClassName = entry.getKey();

      var rootClass = rootClassLoadingContext.getClassFullName();
      var addToResults =
          !nextClassName.contains(rootClass + "$"); // for nested classes in root class

      var nextClassContext =
          rootClass.equals(nextClassName)
              ? rootClassLoadingContext
              : new ClassLoadingContext(nextClassName, addToResults);

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
    return new ClassVisitor(ASM9) {

      private final TypeResolver typeResolver = new TypeResolver();

      @Override
      public void visit(
          int version,
          int access,
          String name,
          String signature,
          String superName,
          String[] interfaces) {

        context.setLoadingContextSignature(signature);
        typeResolver.resolveClassSignature(context);

        var hasValidParent = classPathFilter.test(superName);
        if (hasValidParent) {
          ClassDepsVisitor.this.visit(new ClassLoadingContext(superName, context));
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
        return new MethodDepsVisitor(typeResolver, nextLevelQueue, context, method);
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
        var isConstructor = isConstructor(name);
        var methodName = isConstructor ? currentClass.getName() : name;
        var type = typeResolver.getMethodReturnType(descriptor, signature);
        var method =
            currentClass
                .findMethod(descriptor, name)
                .map(
                    existed -> {
                      existed.setReturnType(type);
                      return existed;
                    })
                .orElse(new Method(access, descriptor, type, methodName, isConstructor));
        currentClass.addMethod(method);

        return method;
      }
    };
  }
}
