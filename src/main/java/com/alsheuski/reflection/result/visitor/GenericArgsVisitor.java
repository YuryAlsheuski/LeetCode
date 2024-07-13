package com.alsheuski.reflection.result.visitor;

import static com.alsheuski.reflection.result.util.LoaderUtil.parseGenericTypes;
import static org.objectweb.asm.Opcodes.ASM9;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

public class GenericArgsVisitor {

  private final Map<String, String> formalToConcreteSignature;
  private final String parentSignature;
  private final List<String> genericSignatures;

  public GenericArgsVisitor(String childSignature, String parentSignature) {
    this.parentSignature = parentSignature;
    this.genericSignatures = parseGenericTypes(childSignature);
    formalToConcreteSignature = new HashMap<>(genericSignatures.size());
  }

  public Map<String, String> load() {
    var argSignaturesIterator = genericSignatures.iterator();
    new SignatureReader(parentSignature)
        .accept(
            new SignatureVisitor(ASM9) {

              @Override
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
