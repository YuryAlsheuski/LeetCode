package com.alsheuski.reflection.result.context;

import com.alsheuski.reflection.result.model.MetaClass;
import java.util.Objects;

public class ClassLoadingContext {

  private final MetaClass currentClass;
  private final ClassLoadingContext childClassContext;
  private final boolean addToResults;

  private String loadingContextSignature;

  public ClassLoadingContext(String currentClass, boolean addToResults) {
    this(currentClass, null, addToResults);
  }

  public ClassLoadingContext(String currentClass) {
    this(currentClass, null, true);
  }

  public ClassLoadingContext(String currentClass, ClassLoadingContext childClassContext) {
    this(currentClass, childClassContext, false);
  }

  public ClassLoadingContext(
      String currentClass, ClassLoadingContext childClassContext, boolean addToResults) {

    this.currentClass = new MetaClass(currentClass);
    this.childClassContext = childClassContext;
    this.addToResults = addToResults;
  }

  public MetaClass getCurrentClass() {
    return currentClass;
  }

  public String getClassFullName() {
    return currentClass.getFullName();
  }

  public boolean hasChild() {
    return childClassContext != null;
  }

  public ClassLoadingContext getChildClassContext() {
    return childClassContext;
  }

  public boolean addToResults() {
    return addToResults;
  }

  public String getLoadingContextSignature() {
    return loadingContextSignature;
  }

  public void setLoadingContextSignature(String loadingContextSignature) {
    this.loadingContextSignature = loadingContextSignature;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClassLoadingContext that = (ClassLoadingContext) o;
    return addToResults == that.addToResults
        && Objects.equals(currentClass, that.currentClass)
        && Objects.equals(childClassContext, that.childClassContext)
        && Objects.equals(loadingContextSignature, that.loadingContextSignature);
  }

  @Override
  public int hashCode() {
    return Objects.hash(currentClass, childClassContext, addToResults, loadingContextSignature);
  }
}
