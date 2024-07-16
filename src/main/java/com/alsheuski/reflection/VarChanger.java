package com.alsheuski.reflection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
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
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class VarChanger {
  public static void main(String[] args) throws IOException {
    String filePath =
        "/Users/Yury_Alsheuski/Desktop/myProjects/LeetCode/src/main/java/com/alsheuski/reflection/VarClass.java";
    String source = readFileToString(filePath);

    ASTParser parser = ASTParser.newParser(AST.JLS21);
    parser.setSource(source.toCharArray());
    parser.setKind(ASTParser.K_COMPILATION_UNIT);
    parser.setResolveBindings(true);

    Map<String, String> options = JavaCore.getOptions();
    parser.setCompilerOptions(options);

    CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
    compilationUnit.recordModifications();

    compilationUnit.accept(
        new ASTVisitor() {
          @Override
          public boolean visit(VariableDeclarationStatement node) {
            if (canBeReplacedWithVar(node)) {
              AST ast = node.getAST();
              VariableDeclarationFragment fragment =
                  (VariableDeclarationFragment) node.fragments().get(0);
              node.setType(ast.newSimpleType(ast.newSimpleName("var")));
            }
            return super.visit(node);
          }

          private boolean canBeReplacedWithVar(VariableDeclarationStatement node) {
            // Check if the variable declaration can be replaced with 'var'
            if (node.fragments().size() != 1) {
              return false; // multiple variables declared in one statement
            }

            VariableDeclarationFragment fragment =
                (VariableDeclarationFragment) node.fragments().get(0);
            Expression initializer = fragment.getInitializer();

            // Do not replace if the initializer is null
            if (initializer == null
                || initializer instanceof NullLiteral
                || initializer instanceof LambdaExpression
                || ((initializer instanceof ClassInstanceCreation)
                    && (((ClassInstanceCreation) initializer).getType()
                        instanceof ParameterizedType)
                    && ((ParameterizedType) ((ClassInstanceCreation) initializer).getType())
                        .typeArguments()
                        .isEmpty())) {
              return false;
            }

            // Replace if the type is explicit and not inferred from null
            return true;
          }
        });

    String modifiedSource = compilationUnit.toString();
    writeFileFromString(filePath, modifiedSource);
  }

  private static String readFileToString(String filePath) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(filePath));
    StringBuilder stringBuilder = new StringBuilder();
    String line = null;
    while ((line = reader.readLine()) != null) {
      stringBuilder.append(line).append("\n");
    }
    reader.close();
    return stringBuilder.toString();
  }

  private static void writeFileFromString(String filePath, String content) throws IOException {
    FileWriter writer = new FileWriter(filePath);
    writer.write(content);
    writer.close();
  }
}
