package com.alsheuski.reflection.result.preprocessor.replacer;

import static java.util.stream.Collectors.toList;

import com.alsheuski.reflection.result.context.GlobalContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

public class SuperclassMethodPrefixVisitorProvider implements ASTVisitorProvider {

  private final GlobalContext context;
  private List<String> parentTypes;

  public SuperclassMethodPrefixVisitorProvider(GlobalContext context) {
    this.context = context;
  }

  @Override
  public CompilationUnitVisitor get(ASTParser parser) {

    parser.setResolveBindings(true);
    parser.setCompilerOptions(JavaCore.getOptions());

    var classpathEntries = new String[] {context.getWorkDirectory().toString()};
    var sourcepathEntries = new String[] {context.getWorkDirectory().toString()};

    parser.setEnvironment(classpathEntries, sourcepathEntries, new String[] {"UTF-8"}, true);
    parser.setUnitName(context.getFilePath().getFileName().toString());

    return getVisitor(parser);
  }

  private CompilationUnitVisitor getVisitor(ASTParser parser) {
    return new CompilationUnitVisitor(parser) {

      {
        rewriter = ASTRewrite.create(cu.getAST());
      }

      @Override
      public boolean visit(TypeDeclaration node) {
        var typeCollection = new ArrayList<Type>();
        typeCollection.add(node.getSuperclassType());
        typeCollection.addAll(node.superInterfaceTypes());

        parentTypes =
            typeCollection.stream()
                .filter(Objects::nonNull)
                .map(ASTNode::toString)
                .collect(toList());

        return super.visit(node);
      }

      @Override
      public boolean visit(MethodInvocation node) {
        var methodBinding = node.resolveMethodBinding();
        if (methodBinding != null) {
          var declaringClass = methodBinding.getDeclaringClass();
          if (parentTypes.contains(declaringClass.getName())) {
            var currentClass = methodBinding.getDeclaringClass();
            if (!isMethodOverridden(currentClass, methodBinding)) {
              var ast = node.getAST();
              var methodName = methodBinding.getName();

              var superMethodInvocation = ast.newSuperMethodInvocation();
              superMethodInvocation.setName(ast.newSimpleName(methodName));

              rewriter.replace(node, superMethodInvocation, null);
            }
          }
        }
        return super.visit(node);
      }

      private boolean isMethodOverridden(ITypeBinding subclass, IMethodBinding methodBinding) {
        var subclassMethods = subclass.getDeclaredMethods();
        for (var subclassMethod : subclassMethods) {
          if (subclassMethod.overrides(methodBinding)) {
            return true;
          }
        }
        return false;
      }
    };
  }
}
