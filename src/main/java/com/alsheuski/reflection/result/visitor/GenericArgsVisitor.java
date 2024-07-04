package com.alsheuski.reflection.result.visitor;

import static org.objectweb.asm.Opcodes.ASM9;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

public class GenericArgsVisitor {

  private final Map<String, String> formalToConcreteSignature;
  private final String parentSignature;
  private final List<String> argSignatures;

  public GenericArgsVisitor(List<String> argSignatures, String parentSignature) {
    formalToConcreteSignature = new HashMap<>(argSignatures.size());
    this.parentSignature = parentSignature;
    this.argSignatures = argSignatures;
  }

  public Map<String, String> load() {
    var argSignaturesIterator = argSignatures.iterator();
    new SignatureReader(parentSignature)
        .accept(
            new SignatureVisitor(ASM9) {

              public void visitFormalTypeParameter(String name) {
                if (argSignaturesIterator.hasNext()) {
                  formalToConcreteSignature.put(name, argSignaturesIterator.next());
                } else {
                  System.err.println(
                      parentSignature
                          + " class generic params does not equal with child class params");
                }
              }
            });
    return formalToConcreteSignature;
  }
}
