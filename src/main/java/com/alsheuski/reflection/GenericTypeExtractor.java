package com.alsheuski.reflection;


import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GenericTypeExtractor {

  public static void main(String[] args) throws Exception {
    printClassMethods(Class.forName("com.alsheuski.reflection.GenericChild"));
  }

  public static void printClassMethods(Class<?> clazz) throws Exception {
    Map<String, String> typeMapping = resolveTypeMapping(clazz);
    Method[] methods = clazz.getMethods();

    System.out.println("public class " + clazz.getSimpleName() + "{");
    for (Method method : methods) {
      if (method.getDeclaringClass() != Object.class) {
        String methodSignature = getMethodSignature(method, typeMapping);
        System.out.println(methodSignature);
      }
    }
    System.out.println("}");
  }

  private static Map<String, String> resolveTypeMapping(Class<?> clazz) {
    Map<String, String> typeMapping = new HashMap<>();
    while (clazz != null && clazz != Object.class) {
      if (clazz.getGenericSuperclass() instanceof ParameterizedType) {
        ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
        Class<?> superClass = (Class<?>) parameterizedType.getRawType();
        TypeVariable<?>[] typeParameters = superClass.getTypeParameters();
        java.lang.reflect.Type[] actualTypes = parameterizedType.getActualTypeArguments();

        for (int i = 0; i < typeParameters.length; i++) {
          typeMapping.put(typeParameters[i].getTypeName(), actualTypes[i].getTypeName());
        }
      }
      clazz = clazz.getSuperclass();
    }
    return typeMapping;
  }

  private static String getMethodSignature(Method method, Map<String, String> typeMapping) throws Exception {
    Class<?> returnType = method.getReturnType();
    java.lang.reflect.Type genericReturnType = method.getGenericReturnType();

    String returnTypeString = resolveGenericType(genericReturnType, typeMapping);
    String methodName = method.getName();
    String parametersString = Arrays.stream(method.getGenericParameterTypes())
            .map(paramType -> resolveGenericType(paramType, typeMapping))
            .reduce((a, b) -> a + ", " + b)
            .orElse("");

    return "public " + returnTypeString + " " + methodName + "(" + parametersString + ");";
  }

  private static String resolveGenericType(java.lang.reflect.Type type, Map<String, String> typeMapping) {
    if (type instanceof TypeVariable) {
      TypeVariable<?> typeVariable = (TypeVariable<?>) type;
      return typeMapping.getOrDefault(typeVariable.getTypeName(), typeVariable.getTypeName());
    } else if (type instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) type;
      Class<?> rawType = (Class<?>) parameterizedType.getRawType();
      java.lang.reflect.Type[] actualTypes = parameterizedType.getActualTypeArguments();

      String actualTypesString = Arrays.stream(actualTypes)
              .map(t -> resolveGenericType(t, typeMapping))
              .reduce((a, b) -> a + ", " + b)
              .orElse("");

      return rawType.getSimpleName() + "<" + actualTypesString + ">";
    } else {
      return ((Class<?>) type).getSimpleName();
    }
  }
}


