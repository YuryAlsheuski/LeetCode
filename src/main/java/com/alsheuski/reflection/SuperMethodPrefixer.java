package com.alsheuski.reflection;

import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.*;
import java.util.*;

public class SuperMethodPrefixer {

  public static void main(String[] args) {
    String source =
        "package com.alsheuski.reflection;\n"
            + "\n"
            + "\n"
            + "public class Common extends ParentForCommonClass {\n"
            + "\n"
            + "  public void neone(){\n"
            + "    test();\n"
            + "  }\n"
            + "\n"
            + "  @Override\n"
            + "  protected void test() {\n"
            + "    test2();\n"
            + "  }\n"
            + "}";

    ASTParser parser = ASTParser.newParser(AST.JLS21);
    parser.setSource(source.toCharArray());
    parser.setKind(ASTParser.K_COMPILATION_UNIT);

    // Enable resolving bindings
    parser.setResolveBindings(true);

    // Set up classpath and sourcepath (required for resolving bindings)
    Map<String, String> options = JavaCore.getOptions();
    parser.setCompilerOptions(options);

    // Assuming you have classpath to resolve dependencies (e.g., for SuperClass)
    String[] classpathEntries = { "/Users/Yury_Alsheuski/Desktop/myProjects/LeetCode/target/classes" };  // Set proper classpath
    String[] sourcepathEntries = { "/Users/Yury_Alsheuski/Desktop/myProjects/LeetCode/src/main/java/com/alsheuski/reflection" };  // Set proper source path

    parser.setEnvironment(classpathEntries, sourcepathEntries, new String[] { "UTF-8" }, true);
    parser.setUnitName("Common.java");  // Set unit name, required for resolving bindings

    // Create AST
    CompilationUnit cu = (CompilationUnit) parser.createAST(null);

    // Visit method invocations
    cu.accept(new ASTVisitor() {
      @Override
      public boolean visit(MethodInvocation node) {
        IMethodBinding methodBinding = node.resolveMethodBinding();
        if (methodBinding != null) {
          ITypeBinding declaringClass = methodBinding.getDeclaringClass();
          String methodName = methodBinding.getName();

          // Assuming we have the SuperClass name here
          if (declaringClass != null && declaringClass.getName().equals("ParentForCommonClass")) {
            System.out.println("Method '" + methodName + "' belongs to the superclass");

            // Check if this method is overridden in the current class
            ITypeBinding currentClass = methodBinding.getDeclaringClass();
            if (!isMethodOverridden(currentClass, methodBinding)) {
              System.out.println("Method '" + methodName + "' is not overridden, prefixing with 'super.'");
              // Apply 'super.' prefix logic
            }
          }
        } else {
          System.out.println("Could not resolve method binding for: " + node.getName());
        }
        return super.visit(node);
      }
    });
  }

  // Utility method to check if a method is overridden
  private static boolean isMethodOverridden(ITypeBinding subclass, IMethodBinding methodBinding) {
    IMethodBinding[] subclassMethods = subclass.getDeclaredMethods();
    for (IMethodBinding subclassMethod : subclassMethods) {
      if (subclassMethod.overrides(methodBinding)) {
        return true;
      }
    }
    return false;
  }
}

