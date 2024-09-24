package com.alsheuski.reflection.result.preprocessor.replacer;

import java.util.ArrayList;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class RealTypeVisitorProvider implements ASTVisitorProvider {

  private final MultiKeyMap<String, String> rowNumberAndNameToType;

  public RealTypeVisitorProvider(MultiKeyMap<String, String> rowNumberAndNameToType) {
    this.rowNumberAndNameToType = rowNumberAndNameToType;
  }

  @Override
  public CompilationUnitVisitor get(ASTParser parser) {
    return getVisitor((CompilationUnit) parser.createAST(null));
  }

  private CompilationUnitVisitor getVisitor(CompilationUnit cu) {
    return new CompilationUnitVisitor() {

      @Override
      public CompilationUnit getCompilationUnit(ASTParser parser) {
        return cu;
      }

      @Override
      public boolean visit(ForStatement node) {
        var expression =
            (VariableDeclarationExpression)
                node.initializers().stream()
                    .filter(i -> i instanceof VariableDeclarationExpression)
                    .findFirst()
                    .get();

        var fragment =
            (VariableDeclarationFragment) expression.fragments().stream().findFirst().get();

        var type = getFieldType(fragment);
        if (type != null) {
          expression.setType(type);
        }

        return super.visit(node);
      }

      @Override
      public boolean visit(EnhancedForStatement node) {

        var loopField = node.getParameter();

        var type = getFieldType(loopField);
        if (type != null) {
          loopField.setType(type);
        }

        return super.visit(node);
      }

      @Override
      public boolean visit(VariableDeclarationStatement node) {

        var fragment = (VariableDeclarationFragment) node.fragments().stream().findFirst().get();

        var type = getFieldType(fragment);
        if (type != null) {
          node.setType(type);
        }

        return super.visit(node);
      }

      private Type getFieldType(VariableDeclaration node) {
        // we need to check offset for cases when we have field initialization with several rows
        var offset = node.getStartPosition() + node.getLength();
        var lineNumber = cu.getLineNumber(offset);
        var fieldName = node.getName().getIdentifier();
        var ast = node.getAST();

        var type = rowNumberAndNameToType.get(String.valueOf(lineNumber), fieldName);
        if (type == null) {
          return null;
        }
        var primitiveTypeCode = PrimitiveType.toCode(type);
        if (primitiveTypeCode == null) {
          if (type.contains("<")) {
            return createParameterizedType(ast, type);
          }
          if (type.contains("[")) {
            return parseArrayType(type, ast);
          }

          return ast.newSimpleType(ast.newName(type));
        }
        return ast.newPrimitiveType(primitiveTypeCode);
      }

      private Type parseArrayType(String typeString, AST ast) {

        typeString = typeString.trim();

        if (!typeString.matches("^[a-zA-Z_][a-zA-Z0-9_]*\\s*(\\[\\s*\\])*")) {
          throw new IllegalArgumentException("Invalid array type string: " + typeString);
        }

        var baseTypeName = typeString.replaceAll("\\[.*", "").trim();
        int dimensions = typeString.length() - typeString.replace("[", "").length();

        var baseType = ast.newSimpleType(ast.newSimpleName(baseTypeName));

        var arrayType = ast.newArrayType(baseType);

        var dim = arrayType.dimensions();

        for (int i = 0; i < dimensions - 1; i++) {
          dim.add(ast.newDimension());
        }
        return arrayType;
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
            var argType =
                "?".equals(arg)
                    ? ast.newWildcardType()
                    : ast.newSimpleType(ast.newName(arg.trim()));
            parameterizedType.typeArguments().add(argType);
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
    };
  }
}
