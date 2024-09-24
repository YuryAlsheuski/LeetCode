package com.alsheuski.reflection.result.preprocessor.modifier;

import static org.eclipse.jdt.core.dom.AST.JLS21;
import static org.eclipse.jdt.core.dom.ASTParser.K_COMPILATION_UNIT;

import com.alsheuski.reflection.result.context.GlobalContext;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

public abstract class JavaCodeModifier extends ASTVisitor {

  protected final String code;
  protected final ASTParser parser;
  protected CompilationUnit cu;

  protected JavaCodeModifier(String code) {
    this.code = code;
    parser = ASTParser.newParser(JLS21);
    parser.setSource(code.toCharArray());
    parser.setKind(K_COMPILATION_UNIT);
  }

  public String modify() {
    cu = (CompilationUnit) parser.createAST(null);
    cu.recordModifications();
    cu.accept(this);
    var document = new Document(code);
    var edits = modify(document);
    try {
      edits.apply(document);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return document.get();
  }

  protected TextEdit modify(Document doc) {
    return cu.rewrite(doc, null);
  }

  public static Chain chain(String code) {
    return new Chain(code);
  }

  public static class Chain {
    private List<Function<String, JavaCodeModifier>> modifiers;
    private String code;

    public Chain(String code) {
      this.modifiers = new ArrayList<>(3);
      this.code = code;
    }

    public Chain concretizeTypes(MultiKeyMap<String, String> rowNumberAndNameToType) {
      modifiers.add(code -> new TypeConcretizer(code, rowNumberAndNameToType));
      return this;
    }

    public Chain setSuperPrefix(GlobalContext context) {
      modifiers.add(code -> new SuperMethodPrefixSetter(code, context));
      return this;
    }

    public Chain simplifyTypes() {
      modifiers.add(TypeSimplifier::new);
      return this;
    }

    public String modify() {
      for (var modifier : modifiers) {
        code = modifier.apply(code).modify();
      }
      return code;
    }
  }
}
