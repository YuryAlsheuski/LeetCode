package com.alsheuski.reflection.result.preprocessor.replacer;

import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

public abstract class CompilationUnitVisitor extends ASTVisitor {

  protected final CompilationUnit cu;
  protected ASTRewrite rewriter;

  public CompilationUnitVisitor(ASTParser parser) {
    cu = (CompilationUnit) parser.createAST(null);
  }

  public CompilationUnit getCompilationUnit() {
    return cu;
  }

  public ASTRewrite getRewriter() {
    return rewriter;
  }
}
