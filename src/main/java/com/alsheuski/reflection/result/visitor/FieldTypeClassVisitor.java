package com.alsheuski.reflection.result.visitor;

import static com.alsheuski.reflection.result.util.LoaderUtil.loadClass;
import static org.objectweb.asm.Opcodes.ASM9;

import com.alsheuski.reflection.result.util.LoaderUtil;
import java.nio.file.Path;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;

public class FieldTypeClassVisitor extends ClassVisitor {

  private final String currentClassPath;
  private final MultiKeyMap<String, String> rowNumberAndNameToType;

  public FieldTypeClassVisitor(String currentClassPath) {
    this(currentClassPath, new MultiKeyMap<>());
  }

  private FieldTypeClassVisitor(
      String currentClassPath, MultiKeyMap<String, String> rowNumberAndNameToType) {
    super(ASM9);
    this.currentClassPath = currentClassPath;
    this.rowNumberAndNameToType = rowNumberAndNameToType;
  }

  @Override
  public void visitInnerClass(String name, String outerName, String innerName, int access) {
    if (access == 25) {
      return;
    }
    var rootFolder = Path.of(currentClassPath).getParent();
    var innerClassPath = rootFolder.resolve(LoaderUtil.getClassName(name) + ".class").toString();
    if (currentClassPath.equals(innerClassPath)) {
      return;
    }
    var visitor = new FieldTypeClassVisitor(innerClassPath, rowNumberAndNameToType);

    loadClass(innerClassPath, visitor);
  }

  @Override
  public MethodVisitor visitMethod(
      int access, String name, String descriptor, String signature, String[] exceptions) {

    return new MethodNode(ASM9, access, name, descriptor, signature, exceptions) {
      @Override
      public void visitLocalVariable(
          String name, String desc, String signature, Label start, Label end, int index) {

        if ("this".equals(name)) {
          return;
        }

        var node = (LabelNode) start.info;
        var rowNumber = getRowNumber(node);
        if (rowNumber == -1) {
          return;
        }
        var typeDescriptor = signature != null ? signature : desc;
        var type = Type.getType(typeDescriptor);

        rowNumberAndNameToType.put(
            String.valueOf(rowNumber), name, LoaderUtil.getClassName(type.getClassName()));
      }

      // for local variables definition in one row like: var a = 1;var b = 2;
      private int getRowNumber(AbstractInsnNode node) {
        if (node == null) {
          return -1; // case when field is used without type link: field.toString();
        }
        var lineNode = node.getPrevious();
        if (lineNode instanceof LineNumberNode) {
          return ((LineNumberNode) lineNode).line;
        }
        return getRowNumber(lineNode);
      }
    };
  }

  public MultiKeyMap<String, String> getRowNumbersMap() {
    return rowNumberAndNameToType;
  }
}
