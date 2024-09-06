package com.alsheuski.reflection;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import java.util.Map;

public class TypeReplacer {

  public static void main(String[] args) {
    String sourceCode =
        " package com.alsheuski;\n"
            + "            class TestClass {\n"
            + "                TestClass(){\n"
            + "                    var i = 10;\n"
            + "                }\n"
            + "                public void testMethod(){\n"
            + "                    var k = \"\";\n"
            + "                }\n"
            + "            }";

    Map<Integer, String> rowNumberToRealType = Map.of(4, "int", 7, "String");

    String modifiedSource = replaceVarTypes(sourceCode, rowNumberToRealType);
    System.out.println(modifiedSource);
  }

  public static String replaceVarTypes(String sourceCode, Map<Integer, String> rowNumberToRealType) {
    ASTParser parser = ASTParser.newParser(AST.JLS21);
    parser.setSource(sourceCode.toCharArray());
    parser.setKind(ASTParser.K_COMPILATION_UNIT);

    CompilationUnit cu = (CompilationUnit) parser.createAST(null);

    cu.recordModifications();
    AST ast = cu.getAST();

    cu.accept(
        new ASTVisitor() {
          @Override
          public boolean visit(VariableDeclarationStatement node) {
            int lineNumber = cu.getLineNumber(node.getStartPosition());

            if (rowNumberToRealType.containsKey(lineNumber)) {
              Type realType;
              var fieldName =
                  node.fragments().stream()
                      .findFirst()
                      .map(
                          fragment ->
                              ((VariableDeclarationFragment) fragment).getName().getIdentifier()).get();

              var type = rowNumberToRealType.get(lineNumber);
              var primitiveTypeCode = PrimitiveType.toCode(type);
              if (primitiveTypeCode == null) {
                realType = ast.newSimpleType(ast.newName(type));
              } else {
                realType = ast.newPrimitiveType(primitiveTypeCode);
              }

              node.setType(realType);
            }
            return super.visit(node);
          }
        });

    // Use a document to apply the text edits
    Document document = new Document(sourceCode);
    TextEdit edits = cu.rewrite(document, null);
    try {
      edits.apply(document);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return document.get();
  }
}

