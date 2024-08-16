package com.alsheuski.reflection.result.config;

import static org.objectweb.asm.Opcodes.ACC_PRIVATE;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class ConfigManager {

  private final Predicate<String> allowedClassPaths;
  private final Map<String, ClassVisitorConfig> classNameToConfig;
  private final ClassVisitorConfig defaultConfig;

  public ConfigManager(Predicate<String> allowedClassPaths) {
    this.allowedClassPaths = allowedClassPaths;
    classNameToConfig = new HashMap<>();
    defaultConfig = new ClassVisitorConfig("def", accessCode -> (accessCode & ACC_PRIVATE) == 0);
  }

  public ConfigManager addConfig(ClassVisitorConfig config) {
    classNameToConfig.put(config.getClassName(), config);
    return this;
  }

  public Predicate<Integer> getAccessFilter(String className) {
    return classNameToConfig.getOrDefault(className, defaultConfig).getAccessIdentifierFilter();
  }

  public Predicate<String> getAllowedClassPaths() {
    return allowedClassPaths;
  }
}
