package com.alsheuski.reflection;

import com.alsheuski.sudoku.Solver;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class TypeEvaluator {
  public static void main(String[] args) throws IOException {
    String filePath = "/Users/Yury_Alsheuski/Desktop/myProjects/LeetCode/src/main/java/com/alsheuski/reflection/Test.java";
    String source = readFileToString(filePath);
    Common cc = new Common("");
    Solver solver = new Solver(null, List.of());
    System.out.println(solver);
    System.err.println(cc);
    ASTParser parser = ASTParser.newParser(AST.JLS21);
    parser.setSource(source.toCharArray());
    parser.setKind(ASTParser.K_COMPILATION_UNIT);
    parser.setResolveBindings(true);

    Map<String, String> options = JavaCore.getOptions();
    parser.setCompilerOptions(options);

    CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
    compilationUnit.recordModifications();

    compilationUnit.accept(new ASTVisitor() {
      @Override
      public boolean visit(VariableDeclarationStatement node) {
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) node.fragments().get(0);
        Expression initializer = fragment.getInitializer();

        if (initializer != null && initializer.resolveTypeBinding() != null) {
          String newTypeName = initializer.resolveTypeBinding().getName();
          AST ast = node.getAST();
          Type newType = ast.newSimpleType(ast.newName(newTypeName));
          node.setType(newType);
        }
        return super.visit(node);
      }
    });

    String modifiedSource = compilationUnit.toString();
    writeFileFromString(filePath, modifiedSource);

    // Compile the modified source file to a .class file
    compileJavaFile(filePath);
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

  private static void compileJavaFile(String filePath) throws IOException {
    ProcessBuilder processBuilder = new ProcessBuilder("javac", filePath);
    processBuilder.inheritIO();
    Process process = processBuilder.start();
    try {
      process.waitFor();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
