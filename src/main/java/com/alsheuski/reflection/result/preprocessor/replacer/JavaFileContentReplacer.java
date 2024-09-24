package com.alsheuski.reflection.result.preprocessor.replacer;

import static org.eclipse.jdt.core.dom.AST.JLS21;
import static org.eclipse.jdt.core.dom.ASTParser.K_COMPILATION_UNIT;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jface.text.Document;

public class JavaFileContentReplacer {

  private JavaFileContentReplacer() {}

  public static String replace(String pathToJavaFile, ASTVisitorProvider provider)
      throws IOException {

    var sourceCode = readFileToString(pathToJavaFile);
    var parser = ASTParser.newParser(JLS21);
    parser.setSource(sourceCode.toCharArray());
    parser.setKind(K_COMPILATION_UNIT);

    var visitor = provider.get(parser);

    var cu = visitor.getCompilationUnit();
    cu.recordModifications();
    cu.accept(visitor);

    var rewriter = visitor.getRewriter();

    var document = new Document(sourceCode);
    var edits = rewriter != null ? rewriter.rewriteAST(document, null) : cu.rewrite(document, null);
    try {
      edits.apply(document);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return document.get();
  }

  private static String readFileToString(String filePath) throws IOException {
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
