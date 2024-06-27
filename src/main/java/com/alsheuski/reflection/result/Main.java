package com.alsheuski.reflection.result;

import java.util.function.Predicate;

import static org.objectweb.asm.Opcodes.ACC_PRIVATE;

public class Main {
  public static void main(String[] args) {

    var className = "com/alsheuski/reflection/Common";
    Predicate<String> allowedClassPaths =
        path -> path.startsWith("com/alsheuski") && !path.startsWith(className);
    Predicate<Integer> accessFilter = accessCode -> accessCode != ACC_PRIVATE;
    var result = new ClassStructureVisitor(3, allowedClassPaths, accessFilter).visitAll(className);
    System.err.println(result);

  }
}
