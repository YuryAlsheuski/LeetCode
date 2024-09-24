package com.alsheuski.reflection;

import static org.objectweb.asm.Opcodes.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import org.objectweb.asm.*;

public class SuperMethodPrefixerASM extends ClassVisitor {

  private String currentClassName;
  private String superClassName;
  private Set<String> superClassMethods = new HashSet<>();
  private Set<String> overriddenMethods = new HashSet<>();

  public SuperMethodPrefixerASM(ClassVisitor classVisitor) {
    super(ASM9, classVisitor);
  }

  @Override
  public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
    this.currentClassName = name;
    this.superClassName = superName;

    // Use ClassReader to collect all methods from the superclass
    try {
      ClassReader superClassReader = new ClassReader(superName);  // Load the superclass
      superClassReader.accept(new ClassVisitor(ASM9) {
        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
          // Collect all methods from the superclass (name and descriptor for signature)
          superClassMethods.add(name + descriptor);
          return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
      }, 0);
    } catch (IOException e) {
      e.printStackTrace();
    }

    super.visit(version, access, name, signature, superName, interfaces);
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
    // Store overridden methods in TestClass (i.e., methods defined in TestClass)
    overriddenMethods.add(name + descriptor);

    MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

      return new MethodVisitor(ASM9, mv) {

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
          String methodSignature = name + descriptor;

          // Check if the method is from the superclass and not overridden
          if (opcode == INVOKEVIRTUAL && owner.equals(currentClassName) && superClassMethods.contains(methodSignature) && !overriddenMethods.contains(methodSignature)) {
            // This method belongs to the superclass and is not overridden in the current class
            System.out.println("Method call to non-overridden superclass method: " + name);

            // Modify the method call to invoke the method with 'super.'
            super.visitMethodInsn(INVOKESPECIAL, superClassName, name, descriptor, isInterface);
          } else {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
          }
        }
      };


  }
  public static void main(String[] args) throws IOException {
    var classBytes = Files.readAllBytes(Paths.get("/Users/Yury_Alsheuski/Desktop/myProjects/LeetCode/target/classes/com/alsheuski/reflection/Common.class"));

    ClassReader classReader = new ClassReader(classBytes);

    ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    ClassVisitor classVisitor = new SuperMethodPrefixerASM(classWriter);

    classReader.accept(classVisitor, 0);

    // The modified bytecode is now available in classWriter. You can write it to a file, load it, etc.
    byte[] modifiedClass = classWriter.toByteArray();

    // Output the modified bytecode (or write it to a file or load it dynamically)
    System.out.println("Class modified with 'super.' prefix on non-overridden superclass methods.");
  }
}
