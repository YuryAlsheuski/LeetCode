package com.alsheuski.reflection.result.visitor;

import static com.alsheuski.reflection.result.util.LoaderUtil.isConstructor;
import static com.alsheuski.reflection.result.util.LoaderUtil.loadClass;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.Opcodes.ASM9;

import com.alsheuski.reflection.result.config.ClassVisitorConfigManager;
import com.alsheuski.reflection.result.context.ClassLoadingContext;
import com.alsheuski.reflection.result.model.MetaClass;
import com.alsheuski.reflection.result.model.Method;
import com.alsheuski.reflection.result.resolver.ClassTypeResolver;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class ClassDepsVisitor {

  private final Map<String, MetaClass> classNameToMetaClass;
  private final Set<String> currentLevelClasses;
  private final ClassVisitorConfigManager configManager;
  private final Map<String, List<Consumer<MetaClass>>> nextLevelQueue;
  private int deep;
  private ClassLoadingContext rootClassLoadingContext;

  public ClassDepsVisitor(ClassVisitorConfigManager configManager, int deep) {
    this.configManager = configManager;
    this.deep = deep;
    classNameToMetaClass = new HashMap<>();
    currentLevelClasses = new HashSet<>();
    nextLevelQueue = new HashMap<>();
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

    var maybeClassPath = configManager.getClassPath(className);
    if (maybeClassPath.isEmpty()) {
      return null;
    }

    if (context.addToResults() && !classNameToMetaClass.containsKey(className)) {
      classNameToMetaClass.put(className, targetClass);
    }

    loadClass(maybeClassPath.get(), getInternalVisitor(context));

    if (currentLevelClasses.isEmpty()) { // level completed
      deep = deep - 1;
      if (deep > 0 && !nextLevelQueue.isEmpty()) {
        visitNextLevel();
      }
    }
    return targetClass;
  }

  private void visitNextLevel() {
    currentLevelClasses.addAll(nextLevelQueue.keySet());

    var entrySet = new HashSet<>(nextLevelQueue.entrySet());
    nextLevelQueue.clear();

    for (var entry : entrySet) {
      var nextClassName = entry.getKey();

      if (!configManager.isAllowedClass(nextClassName)) {
        continue;
      }

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

      var nexLoaders = entry.getValue();
      nexLoaders.forEach(nextLoader -> nextLoader.accept(nextClass));
    }
  }

  private ClassVisitor getInternalVisitor(ClassLoadingContext context) {
    // todo try to migrate to ClassNode after migration to any custom MethodNode. We need to process
    // whole class once and
    // then parse results
    return new ClassVisitor(ASM9) {

      private final ClassTypeResolver typeResolver = new ClassTypeResolver();

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

        if (context.equals(rootClassLoadingContext)) {
          return;
        }

        var hasValidParent = configManager.isAllowedClass(superName);
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
        context.getCurrentClass().addMethod(method);
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
        return currentClass
            .findMethod(descriptor, name)
            .map(
                existed -> {
                  existed.setReturnType(type);
                  return existed;
                })
            .orElse(new Method(access, descriptor, type, methodName, isConstructor));
      }
    };
  }
}
