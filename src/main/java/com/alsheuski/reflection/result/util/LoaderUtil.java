package com.alsheuski.reflection.result.util;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

import com.alsheuski.reflection.result.model.MetaClass;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.objectweb.asm.Type;

public class LoaderUtil {
  private LoaderUtil() {}

  public static boolean isConstructor(String methodName) {
    return "<init>".equals(methodName);
  }

  public static String getClassName(String classFullName) {
    var raw = classFullName.replace(";", "");
    var isGenericType = raw.contains("<") && raw.contains(">");
    var result = new ArrayList<String>();
    var parts = new ArrayList<String>();
    if (isGenericType) {
      var outsideClass = raw.substring(0, raw.indexOf("<"));
      parts.add(outsideClass);
      parts.add("<");
      var genericClass = raw.substring(raw.indexOf("<") + 1, raw.indexOf(">"));
      parts.add(genericClass);
      parts.add(">");
    } else {
      parts.add(raw);
    }

    for (var part : parts) {
      if (part.equals("<") || part.equals(">")) {
        result.add(part);
        continue;
      }
      var delimeter1 = "/";
      var delimeter2 = ".";
      var resultDelimeter = "";
      if (part.contains(delimeter1)) {
        resultDelimeter = delimeter1;
      } else if (part.contains(delimeter2)) {
        resultDelimeter = "\\" + delimeter2;
      } else {
        result.add(part);
        continue;
      }
      var nameParts = part.split(resultDelimeter);
      result.add(nameParts[nameParts.length - 1]);
    }
    return String.join("", result);
  }

  public static Type getType(String descriptor, String signature) {
    try {
      return Type.getType(signature);
    } catch (Exception exception) {
      return Type.getType(descriptor);
    }
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
