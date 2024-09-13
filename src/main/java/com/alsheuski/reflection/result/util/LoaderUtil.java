package com.alsheuski.reflection.result.util;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.objectweb.asm.ClassReader.EXPAND_FRAMES;

import com.alsheuski.reflection.result.model.MetaClass;
import com.alsheuski.reflection.result.model.Method;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

public class LoaderUtil {
  private LoaderUtil() {}

  public static boolean isConstructor(String methodName) {
    return "<init>".equals(methodName);
  }

  public static String getClassName(String classFullName) {
    if ("*".equals(classFullName)) {
      return "?";
    }
    if (classFullName.contains("*") && !classFullName.contains("*;")) {
      classFullName = classFullName.replace("*", "*;");
    }
    return parseClassName(classFullName);
  }

  private static String parseClassName(String classFullName) {
    boolean isGenericType = classFullName.contains("<") && classFullName.contains(">");
    var result = new ArrayList<String>();
    if (classFullName.startsWith("[")) { // arrays
      classFullName = classFullName.substring(1) + "[]";
    }
    if (isGenericType) {
      var startIndex = classFullName.indexOf("<");
      var endIndex = classFullName.lastIndexOf(">");
      var outsideClass = classFullName.substring(0, startIndex);
      var genericClasses = classFullName.substring(startIndex + 1, endIndex);

      result.add(parseSimpleClassName(outsideClass));
      result.add("<");
      result.add(parseGenericClasses(genericClasses));
      result.add(">");
      if (classFullName.endsWith("[]")) {
        result.add("[]"); // for arrays [] brackets
      }
    } else {
      result.add(parseSimpleClassName(classFullName));
    }

    return String.join("", result);
  }

  private static String parseGenericClasses(String genericClasses) {
    var genericClassNames = splitGenericClasses(genericClasses); // todo upgrade class printing
    return genericClassNames.stream()
        .map(LoaderUtil::getClassName)
        .collect(Collectors.joining(", "));
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
      if (c == ';' && level == 0 && lastSplit != i) {
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
    var cn = classFullName.replace(";", "");
    if (cn.contains(delimiter1)) {
      resultDelimiter = delimiter1;
    } else if (cn.contains(delimiter2)) {
      resultDelimiter = "\\" + delimiter2;
    } else {
      return cn;
    }

    var nameParts = cn.split(resultDelimiter);
    return nameParts[nameParts.length - 1];
  }

  public static List<String> parseGenericTypes(String signature) {
    List<String> result = new ArrayList<>();
    int len = signature.length();
    int index = 0;

    while (index < len) {
      if (signature.charAt(index) == '<') {
        int endIndex = findMatchingBracket(signature, index);
        if (endIndex != -1) {
          String genericContent = signature.substring(index + 1, endIndex);
          extractGenericTypes(genericContent, result);
          index = endIndex;
        }
      }
      index++;
    }

    return result;
  }

  private static int findMatchingBracket(String input, int startIndex) {
    int len = input.length();
    int count = 0;

    for (int i = startIndex; i < len; i++) {
      if (input.charAt(i) == '<') {
        count++;
      } else if (input.charAt(i) == '>') {
        count--;
        if (count == 0) {
          return i;
        }
      }
    }

    return -1; // No matching bracket found
  }

  private static void extractGenericTypes(String input, List<String> result) {
    int len = input.length();
    int index = 0;
    int start = 0;
    int nestedLevel = 0;

    while (index < len) {
      char c = input.charAt(index);
      if (c == '<') {
        nestedLevel++;
      } else if (c == '>') {
        nestedLevel--;
      } else if (c == ';' && nestedLevel == 0) {
        String signature = input.substring(start, index + 1);
        result.add(clearFormalTypes(signature));
        start = index + 1;
      }
      index++;
    }

    if (start < len) {
      String signature = input.substring(start);
      result.add(clearFormalTypes(signature));
    }
  }

  private static String clearFormalTypes(
      String signature) { // todo check is it problem for other cases
    if (!signature.contains(":")) {
      return signature;
    }
    int index = signature.lastIndexOf(":") + 1;
    return signature.substring(index);
  }

  public static String print(List<MetaClass> classes) {

    var sb = new StringBuffer();

    for (MetaClass mc : classes) {
      var signature = getClassSignature(mc);
      sb.append(
          String.format("public class %s%s {\n", mc, signature)); // todo add interface support
      for (var method : mc.getMethods()) {
        printMethod(sb, method);
      }
      sb.append("}\n");
    }
    return sb.toString();
  }

  private static void printMethod(StringBuffer sb, Method method) {
    var argsStr =
        method.getArgs().stream().map(arg -> arg.getType() + " " + arg).collect(joining(", "));
    var returnTypeStr = method.isConstructor() ? "" : " " + method.getReturnType();
    var staticPrefix = method.isStatic() ? " static" : "";
    sb.append(
        String.format(
            "  public%s%s %s(%s);",
            staticPrefix,
            returnTypeStr,
            method.getName().replace("$", "."),
            argsStr)); // todo if needs add real access descriptor e.g.
    // public/protected/package - currently public stub only
    sb.append("\n");
  }

  public static String buildClassesMetadata(
      String classFullName,
      List<MetaClass> classes) { // todo refactor this class - it is pretty complicated

    var sb = new StringBuffer();
    for (MetaClass mc : classes) {
      var linkedMethods =
          mc.getMethods().stream()
              .filter(
                  method ->
                      method.getCalls().stream()
                          .anyMatch(
                              className ->
                                  className.equals(classFullName)
                                      || className.contains(classFullName + "$")))
              .collect(toList());
      if (linkedMethods.isEmpty()) {
        continue;
      }
      var signature = getClassSignature(mc);
      sb.append(
          String.format("public class %s%s {\n", mc, signature)); // todo add interface support
      for (var linkedMethod : linkedMethods) {
        printMethod(sb, linkedMethod);
      }
      sb.append("}\n");
    }
    return sb.toString();
  }

  private static String getClassSignature(MetaClass mc) {
    var signature = mc.getSignature();
    if (signature == null) {
      return "";
    }
    var methodGenericArgs = parseFormalTypeParameters(signature);
    return parseGenericMethodPrefix(methodGenericArgs).map(prefix -> prefix + " ").orElse("");
  }

  public static void loadClass(String classPath, ClassVisitor visitor) {
    try {
      var classBytes = Files.readAllBytes(Paths.get(classPath));
      var classReader = new ClassReader(classBytes);
      classReader.accept(visitor, EXPAND_FRAMES);
    } catch (IOException ex) {
      throw new RuntimeException("Build project or correct root path!", ex);
    }
  }

  public static Map<String, String> parseFormalTypeParameters(String signature) {
    Map<String, String> formalTypeParameters = new LinkedHashMap<>();

    int startIndex = signature.indexOf('<');

    if (startIndex == -1) {
      return Map.of();
    }

    int endIndex = findMatchingAngleBracket(signature, startIndex);

    String paramsString = signature.substring(startIndex + 1, endIndex);
    parseParameters(paramsString, formalTypeParameters);

    return formalTypeParameters;
  }

  private static int findMatchingAngleBracket(String signature, int startIndex) {
    int depth = 0;
    for (int i = startIndex; i < signature.length(); i++) {
      char c = signature.charAt(i);
      if (c == '<') {
        depth++;
      } else if (c == '>') {
        depth--;
        if (depth == 0) {
          return i;
        }
      }
    }
    return -1; // No matching closing bracket found
  }

  private static void parseParameters(
      String paramsString, Map<String, String> formalTypeParameters) {
    int length = paramsString.length();
    int i = 0;
    while (i < length) {
      int colonIndex = paramsString.indexOf(':', i);
      if (colonIndex == -1) {
        break;
      }
      String paramName = paramsString.substring(i, colonIndex);
      i = colonIndex + 1;
      if (paramsString.charAt(i) == ':') {
        i++; // Skip the second ':'
      }
      int typeStart = i;
      int depth = 0;
      while (i < length && (paramsString.charAt(i) != ';' || depth > 0)) {
        char c = paramsString.charAt(i);
        if (c == '<') {
          depth++;
        } else if (c == '>') {
          depth--;
        }
        i++;
      }
      // Include the ';' in the type if it exists
      if (i < length && paramsString.charAt(i) == ';') {
        i++;
      }
      String paramType = paramsString.substring(typeStart, i);
      formalTypeParameters.put(paramName, paramType);
    }
  }

  public static Optional<String> parseGenericMethodPrefix(
      Map<String, String> formalToTypeParameters) {
    if (formalToTypeParameters.isEmpty()) {
      return Optional.empty();
    }
    String resultSignature =
        formalToTypeParameters.entrySet().stream()
            .map(
                entry -> {
                  if ("Ljava/lang/Object;".equals(entry.getValue())) {
                    return entry.getKey();
                  }
                  return String.format(
                      "%s extends %s", entry.getKey(), getClassName(entry.getValue()));
                })
            .collect(joining(", "));
    return Optional.of(String.format("<%s>", resultSignature));
  }

  public static String parseGenericMethodReturnType(String signature) {
    var index = signature.lastIndexOf(')');
    var type = signature.substring(index + 1, signature.length() - 1);
    return getClassName(type);
  }

  public static String removeGenericClassPrefix(String signature) {
    return signature.replaceFirst("^<(([^<>]*)|(<([^<>]*)>))*>", "");
  }
}
