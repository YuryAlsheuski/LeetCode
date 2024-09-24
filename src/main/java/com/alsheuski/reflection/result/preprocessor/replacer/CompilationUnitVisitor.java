package com.alsheuski.reflection.result.preprocessor.replacer;

import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

public abstract class CompilationUnitVisitor extends ASTVisitor {

  public CompilationUnit getCompilationUnit(ASTParser parser) {
    return (CompilationUnit) parser.createAST(null);
  }
}
