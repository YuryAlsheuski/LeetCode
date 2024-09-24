package com.alsheuski.reflection.result.preprocessor.modifier;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class TypeSimplifier extends JavaCodeModifier {

  public TypeSimplifier(String code) {
    super(code);
    parser.setResolveBindings(true);
    parser.setCompilerOptions(JavaCore.getOptions());
  }

  @Override
  public boolean visit(ForStatement node) {
    var expression =
        (VariableDeclarationExpression)
            node.initializers().stream()
                .filter(i -> i instanceof VariableDeclarationExpression)
                .findFirst()
                .get();

    var fragment = (VariableDeclarationFragment) expression.fragments().stream().findFirst().get();
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
}
