package com.alsheuski.reflection.result.preprocessor.replacer;

import org.eclipse.jdt.core.dom.ASTParser;

public interface ASTVisitorProvider {
  CompilationUnitVisitor get(ASTParser parser);
}
