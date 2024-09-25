package com.alsheuski.reflection.result.config;

import static org.objectweb.asm.Opcodes.ACC_PRIVATE;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class ClassVisitorConfigManager {

  private final Map<String, ClassVisitorConfig> classNameToConfig;
  private final ClassVisitorConfig defaultConfig;
  private final Path rootClassPath;

  public ClassVisitorConfigManager(Path rootClassPath) {
    this.rootClassPath = rootClassPath;
    classNameToConfig = new HashMap<>();
    defaultConfig = new ClassVisitorConfig("def", accessCode -> (accessCode & ACC_PRIVATE) == 0);
  }

  public ClassVisitorConfigManager addConfig(ClassVisitorConfig config) {
    classNameToConfig.put(config.getSourceRootPath(), config);
    return this;
  }

  public Predicate<Integer> getAccessFilter(String className) {
    return classNameToConfig.getOrDefault(className, defaultConfig).getAccessIdentifierFilter();
  }

  public Optional<Path> getClassPath(String className) {
    var classPath = rootClassPath.resolve(className + ".class");
    return classPath.toFile().exists() ? Optional.of(classPath) : Optional.empty();
  }

  public boolean isAllowedClass(String className) {
    return getClassPath(className).isPresent();
  }
}
