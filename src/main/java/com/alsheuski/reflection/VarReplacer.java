package com.alsheuski.reflection;

import org.eclipse.jdt.core.dom.*;

public class VarReplacer {
  public static void main(String[] args) {
    // Java code that you want to analyze and possibly replace with var import java.util.List;
    String code =
        "package com.alsheuski.reflection;\n"
            + "\n"
            + "        import java.util.function.Supplier;\n"
                + "        import java.util.List;\n"
            + "\n"
            + "        public class TestClass {\n"
            + "\n"
            + "          public void test() {\n"
            + "            int i = 0;\n"
            + "            Supplier<String> supp = getGeneric();\n"
            + "            List<String> list = new ArrayList<>();\n"
            + "            System.err.println(i);\n"
            + "            System.err.println(supp.get().length());\n"
            + "          }\n"
            + "\n"
            + "          public <T> Supplier<T> getGeneric(){\n"
            + "            return ()-> (T) \"fdsfsd\";\n"
            + "          }\n"
            + "        }";

    ASTParser parser = ASTParser.newParser(AST.JLS17); // Use the Java version you're targeting
    parser.setSource(code.toCharArray());
    parser.setKind(ASTParser.K_COMPILATION_UNIT);

    CompilationUnit cu = (CompilationUnit) parser.createAST(null);

    cu.accept(new ASTVisitor() {
      @Override
      public boolean visit(VariableDeclarationStatement node) {
        Type type = node.getType();
        if (type.isSimpleType()) {
          SimpleType simpleType = (SimpleType) type;
          // Check if the type is generic
          if (isGenericType(simpleType)) {
            System.out.println("Generic type, skipping 'var' for: " + node);
          } else {
            System.out.println("Can replace with 'var': " + node);
          }
        } else if (type.isPrimitiveType()) {
          // Primitive types can be safely replaced with 'var'
          System.out.println("Can replace with 'var': " + node);
        }
        return super.visit(node);
      }

      private boolean isGenericType(SimpleType type) {
        if (type.getName() instanceof QualifiedName) {
          QualifiedName qualifiedName = (QualifiedName) type.getName();
          // In real scenarios, you can also resolve the bindings and check for generics
          // Here we are just checking if the type name is parameterized
          return qualifiedName.getFullyQualifiedName().contains("<");
        }
        return false;
      }
    });
  }
}