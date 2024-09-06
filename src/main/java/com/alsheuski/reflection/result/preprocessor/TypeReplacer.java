package com.alsheuski.reflection.result.preprocessor;

import static org.eclipse.jdt.core.dom.AST.JLS21;
import static org.eclipse.jdt.core.dom.ASTParser.K_COMPILATION_UNIT;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

// todo refactor this class!
public class TypeReplacer {

  public String replaceVarTypes(
      String pathToJavaFile, MultiKeyMap<String, String> rowNumberAndNameToType)
      throws IOException {

    String sourceCode = readFileToString(pathToJavaFile);
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
            var fieldName =
                node.fragments().stream()
                    .findFirst()
                    .map(
                        fragment ->
                            ((VariableDeclarationFragment) fragment).getName().getIdentifier())
                    .get();

            var type = rowNumberAndNameToType.get(String.valueOf(lineNumber), fieldName);
            if (type != null) {
              Type realType;
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

    Document document = new Document(sourceCode);
    TextEdit edits = cu.rewrite(document, null);
    try {
      edits.apply(document);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return document.get();
  }

  public String replaceTypesToVar(String pathToJavaFile) throws IOException {
    String source = readFileToString(pathToJavaFile);

    ASTParser parser = ASTParser.newParser(JLS21);
    parser.setSource(source.toCharArray());
    parser.setKind(K_COMPILATION_UNIT);
    parser.setResolveBindings(true);

    Map<String, String> options = JavaCore.getOptions();
    parser.setCompilerOptions(options);

    CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
    compilationUnit.recordModifications();

    compilationUnit.accept(getVisitor());

    Document document = new Document(source);
    TextEdit edits = compilationUnit.rewrite(document, options);
    try {
      edits.apply(document);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return document.get();
  }

  private ASTVisitor getVisitor() {
    return new ASTVisitor() {
      @Override
      public boolean visit(VariableDeclarationStatement node) {
        if (canBeReplacedWithVar(node)) {
          AST ast = node.getAST();
          node.setType(ast.newSimpleType(ast.newSimpleName("var")));
        }
        return super.visit(node);
      }

      private boolean canBeReplacedWithVar(VariableDeclarationStatement node) {
        if (node.fragments().size() != 1) {
          return false;
        }
        VariableDeclarationFragment fragment =
            (VariableDeclarationFragment) node.fragments().get(0);
        Expression initializer = fragment.getInitializer();

        if (initializer == null
            || initializer instanceof NullLiteral
            || initializer instanceof LambdaExpression
            || ((initializer instanceof ClassInstanceCreation)
                && (((ClassInstanceCreation) initializer).getType() instanceof ParameterizedType)
                && ((ParameterizedType) ((ClassInstanceCreation) initializer).getType())
                    .typeArguments()
                    .isEmpty())) {
          return false;
        }

        return true;
      }
    };
  }

  private String readFileToString(String filePath) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(filePath));
    StringBuilder stringBuilder = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      stringBuilder.append(line).append("\n");
    }
    reader.close();
    return stringBuilder.toString();
  }
}
