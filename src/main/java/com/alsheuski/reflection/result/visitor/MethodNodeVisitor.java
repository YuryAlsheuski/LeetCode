package com.alsheuski.reflection.result.visitor;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;

// todo try to migrate all other places to this one class. We need to process whole method once and
// then parse results
public class MethodNodeVisitor extends MethodNode {

  private static final MultiKeyMap<String, String> rowNumberToType = new MultiKeyMap<>();

  public MethodNodeVisitor(
      int access, String name, String desc, String signature, String[] exceptions) {
    super(access, name, desc, signature, exceptions);
  }

  @Override
  public void visitLocalVariable(
      String name, String desc, String signature, Label start, Label end, int index) {

    if ("this".equals(name)) {
      return;
    }

    var node = (LabelNode) start.info;
    var rowNumber = getRowNumber(node);

    rowNumberToType.put(String.valueOf(rowNumber), name, signature != null ? signature : desc);
  }

  // for local variables definition in one row like: var a = 1;var b = 2;
  private int getRowNumber(AbstractInsnNode node) {
    var lineNode = node.getNext();
    if (lineNode instanceof LineNumberNode) {
      return ((LineNumberNode) lineNode).line - 1;
    }
    return getRowNumber(lineNode);
  }

  public static MultiKeyMap<String, String> getRowNumberToType() {
    return rowNumberToType;
  }
}
