package com.alsheuski.reflection.result.util;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

import com.alsheuski.reflection.result.model.MetaClass;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.objectweb.asm.Type;

public class LoaderUtil {
  private LoaderUtil() {}

  public static boolean isConstructor(String methodName) {
    return "<init>".equals(methodName);
  }

  public static String getClassName(String classFullName) {
    if ("*".equals(classFullName)) {
      return "?";
    }
    return parseClassName(classFullName.replace("*", "*;"));
  }

  private static String parseClassName(String classFullName) {
    boolean isGenericType = classFullName.contains("<") && classFullName.contains(">");
    var result = new ArrayList<String>();

    if (isGenericType) {
      var startIndex = classFullName.indexOf("<");
      var endIndex = classFullName.lastIndexOf(">");
      var outsideClass = classFullName.substring(0, startIndex);
      var genericClasses = classFullName.substring(startIndex + 1, endIndex);

      result.add(parseSimpleClassName(outsideClass));
      result.add("<");
      result.add(parseGenericClasses(genericClasses));
      result.add(">");
    } else {
      result.add(parseSimpleClassName(classFullName));
    }

    return String.join("", result);
  }

  private static String parseGenericClasses(String genericClasses) {
    var genericClassNames = splitGenericClasses(genericClasses); // todo upgrade class printing
    return genericClassNames.stream()
        .map(LoaderUtil::getClassName)
        .collect(Collectors.joining(","));
  }

  private static List<String> splitGenericClasses(String genericClasses) {
    var result = new ArrayList<String>();
    var level = 0;
    var lastSplit = 0;

    for (var i = 0; i < genericClasses.length(); i++) {
      var c = genericClasses.charAt(i);
      if (c == '<') {
        level++;
        continue;
      }
      if (c == '>') {
        level--;
        continue;
      }
      if (c == ';' && level == 0) {
        result.add(genericClasses.substring(lastSplit, i).trim());
        lastSplit = i + 1;
      }
    }
    return result;
  }

  private static String parseSimpleClassName(String classFullName) {
    var delimiter1 = "/";
    var delimiter2 = ".";
    var resultDelimiter = "";

    if (classFullName.contains(delimiter1)) {
      resultDelimiter = delimiter1;
    } else if (classFullName.contains(delimiter2)) {
      resultDelimiter = "\\" + delimiter2;
    } else {
      return classFullName;
    }

    var nameParts = classFullName.split(resultDelimiter);
    return nameParts[nameParts.length - 1];
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
        var staticPrefix = linkedMethod.isStatic() ? " static" : "";
        sb.append(
            String.format(
                "  public%s%s %s(%s);",
                staticPrefix,
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
