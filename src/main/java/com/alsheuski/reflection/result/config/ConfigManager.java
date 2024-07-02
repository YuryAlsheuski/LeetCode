package com.alsheuski.reflection.result.config;

import static org.objectweb.asm.Opcodes.ACC_PRIVATE;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class ConfigManager {

  private final Map<String, ClassVisitorConfig> classNameToConfig;
  private final ClassVisitorConfig defaultConfig;

  public ConfigManager() {
    classNameToConfig = new HashMap<>();
    defaultConfig = new ClassVisitorConfig("def", accessCode -> (accessCode & ACC_PRIVATE) == 0);
  }

  public void addConfig(ClassVisitorConfig config) {
    classNameToConfig.put(config.getClassName(), config);
  }

  public Predicate<Integer> getAccessFilter(String className) {
    return classNameToConfig.getOrDefault(className, defaultConfig).getAccessFilter();
  }
}
