package com.alsheuski.reflection.result.preprocessor;

import static org.eclipse.jdt.core.dom.AST.JLS21;
import static org.eclipse.jdt.core.dom.ASTParser.K_COMPILATION_UNIT;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jface.text.Document;

// todo refactor this class!
public class JavaFileTypeReplacer {

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
            var fragment =
                (VariableDeclarationFragment) node.fragments().stream().findFirst().get();
            // we need to check offset for cases when we have field initialization with several rows
            var offset =
                fragment.getInitializer().getStartPosition()
                    + fragment.getInitializer().getLength();
            var lineNumber = cu.getLineNumber(offset);
            var fieldName = fragment.getName().getIdentifier();

            var type = rowNumberAndNameToType.get(String.valueOf(lineNumber), fieldName);
            if (type != null) {
              Type realType;
              var primitiveTypeCode = PrimitiveType.toCode(type);
              if (primitiveTypeCode == null) {
                if (type.contains("<")) {
                  realType = createParameterizedType(ast, type);
                } else {
                  realType = ast.newSimpleType(ast.newName(type));
                }
              } else {
                realType = ast.newPrimitiveType(primitiveTypeCode);
              }

              node.setType(realType);
            }
            return super.visit(node);
          }

          private ParameterizedType createParameterizedType(AST ast, String type) {
            var indexOfGenerics = type.indexOf("<");
            var rawTypeName = type.substring(0, indexOfGenerics).trim();
            var genericArgs = type.substring(indexOfGenerics + 1, type.lastIndexOf(">")).trim();

            var rawType = ast.newSimpleType(ast.newName(rawTypeName));

            var parameterizedType = ast.newParameterizedType(rawType);

            var typeArguments = splitTypeArguments(genericArgs);

            for (String arg : typeArguments) {
              if (arg.contains("<")) {
                parameterizedType.typeArguments().add(createParameterizedType(ast, arg));
              } else {
                parameterizedType.typeArguments().add(ast.newSimpleType(ast.newName(arg.trim())));
              }
            }

            return parameterizedType;
          }

          private String[] splitTypeArguments(String typeArguments) {
            var result = new ArrayList<String>();
            var balance = 0;
            var currentArgument = new StringBuilder();

            for (char ch : typeArguments.toCharArray()) {
              if (ch == '<') {
                balance++;
              } else if (ch == '>') {
                balance--;
              } else if (ch == ',' && balance == 0) {
                result.add(currentArgument.toString().trim());
                currentArgument.setLength(0);
                continue;
              }
              currentArgument.append(ch);
            }

            result.add(currentArgument.toString().trim());

            return result.toArray(new String[0]);
          }
        });
    var document = new Document(sourceCode);
    var edits = cu.rewrite(document, null);
    try {
      edits.apply(document);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return document.get();
  }

  public String replaceTypesToVar(String pathToJavaFile) throws IOException {
    var source = readFileToString(pathToJavaFile);

    var parser = ASTParser.newParser(JLS21);
    parser.setSource(source.toCharArray());
    parser.setKind(K_COMPILATION_UNIT);
    parser.setResolveBindings(true);

    var options = JavaCore.getOptions();
    parser.setCompilerOptions(options);

    var compilationUnit = (CompilationUnit) parser.createAST(null);
    compilationUnit.recordModifications();

    compilationUnit.accept(getVisitor());

    var document = new Document(source);
    var edits = compilationUnit.rewrite(document, options);
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
          var ast = node.getAST();
          node.setType(ast.newSimpleType(ast.newSimpleName("var")));
        }
        return super.visit(node);
      }

      private boolean canBeReplacedWithVar(VariableDeclarationStatement node) {
        if (node.fragments().size() != 1) {
          return false;
        }
        var fragment = (VariableDeclarationFragment) node.fragments().get(0);
        var initializer = fragment.getInitializer();

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
    var reader = new BufferedReader(new FileReader(filePath));
    var stringBuilder = new StringBuilder();
    var line = "";
    while ((line = reader.readLine()) != null) {
      stringBuilder.append(line).append("\n");
    }
    reader.close();
    return stringBuilder.toString();
  }
}
