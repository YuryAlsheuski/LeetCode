package com.alsheuski.reflection.result.preprocessor.replacer;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class VarTypeVisitorProvider implements ASTVisitorProvider {

  @Override
  public CompilationUnitVisitor get(ASTParser parser) {

    parser.setResolveBindings(true);
    parser.setCompilerOptions(JavaCore.getOptions());

    return getVisitor(parser);
  }

  public CompilationUnitVisitor getVisitor(ASTParser parser) {
    return new CompilationUnitVisitor(parser) {
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
        if (canBeReplacedWithVar(expression.getType(), fragment.getInitializer())) {
          var ast = node.getAST();
          expression.setType(ast.newSimpleType(ast.newSimpleName("var")));
        }

        return super.visit(node);
      }

      @Override
      public boolean visit(EnhancedForStatement node) {
        var loopField = node.getParameter();
        var ast = node.getAST();
        loopField.setType(ast.newSimpleType(ast.newSimpleName("var")));

        return super.visit(node);
      }

      @Override
      public boolean visit(VariableDeclarationStatement node) {
        var fragment = (VariableDeclarationFragment) node.fragments().get(0);

        if (canBeReplacedWithVar(node.getType(), fragment.getInitializer())) {
          var ast = node.getAST();
          node.setType(ast.newSimpleType(ast.newSimpleName("var")));
        }
        return super.visit(node);
      }

      private boolean canBeReplacedWithVar(Type linkType, ASTNode initializer) {
        return !linkType.isParameterizedType()
            && initializer != null
            && !(initializer instanceof NullLiteral);
      }
    };
  }
}
