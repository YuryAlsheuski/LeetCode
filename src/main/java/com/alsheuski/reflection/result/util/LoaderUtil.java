package com.alsheuski.reflection.result.util;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

import com.alsheuski.reflection.result.model.MetaClass;
import java.util.List;
import java.util.Set;

public class LoaderUtil {
  private LoaderUtil() {}

  public static boolean isConstructor(String methodName) {
    return "<init>".equals(methodName);
  }

  public static String getClassName(String classFullName) {
    var delimeter1 = "/";
    var delimeter2 = ".";
    var resultDelimeter = "";
    if (classFullName.contains(delimeter1)) {
      resultDelimeter = delimeter1;
    } else if (classFullName.contains(delimeter2)) {
      resultDelimeter = "\\" + delimeter2;
    } else {
      return classFullName;
    }
    var parts = classFullName.split(resultDelimeter);
    return parts[parts.length - 1];
  }

  public static Set<MetaClass> getLinkedWith(String classFullName, List<MetaClass> classes) {
    return classes.stream()
        .filter(clazz -> !clazz.getCalledWith(classFullName).isEmpty())
        .collect(toSet());
  }

  public static String printLinkedWith(String classFullName, List<MetaClass> classes) {
    var sb = new StringBuffer();
    for (MetaClass mc : classes) {
      var linkedMethods = mc.getCalledWith(classFullName);
      if (linkedMethods.isEmpty()) {
        continue;
      }
      sb.append(String.format("public class %s {\n", mc.getName())); // todo add interface support
      for (var linkedMethod : linkedMethods) {
        var argsStr =
            linkedMethod.getArgs().stream()
                .map(arg -> getClassName(arg.getType().getClassName()) + " " + arg.getName())
                .collect(joining(", "));
        var returnTypeStr =
            linkedMethod.isConstructor()
                ? ""
                : " " + getClassName(linkedMethod.getReturnType().getClassName());
        sb.append(
            String.format(
                "  public%s %s(%s);",
                returnTypeStr,
                linkedMethod.getName(),
                argsStr)); // todo if needs add real access descriptor e.g.
        // public/protected/package - currently public stub only
        sb.append("\n");
      }
      sb.append("}\n");
    }
    return sb.toString();
  }
}
