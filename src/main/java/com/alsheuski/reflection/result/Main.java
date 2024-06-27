package com.alsheuski.reflection.result;



import static com.alsheuski.reflection.result.util.LoaderUtil.printLinkedWith;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;

import java.util.ArrayList;
import java.util.function.Predicate;

public class Main {
  public static void main(String[] args) {

    var className = "com/alsheuski/reflection/Common";
    Predicate<String> allowedClassPaths =
        path -> path.startsWith("com/alsheuski") && !path.startsWith(className);
    Predicate<Integer> accessFilter = accessCode -> accessCode != ACC_PRIVATE;
    var result = new ClassStructureVisitor(3, allowedClassPaths, accessFilter).visitAll(className);

    System.err.println(printLinkedWith(className,  new ArrayList<>(result.values())));

  }
}