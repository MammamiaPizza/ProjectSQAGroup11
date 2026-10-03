package com.google.gson.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;

import org.junit.Test;

public class GsonTypesRecursiveResolveTest {

  static class A<T> {
    B<T> context;
    List<T> list;
    List<? extends T> upperBoundedList;
    T[] array;
  }

  static class B<U> extends A<U> {
  }

  static class ConcreteA extends A<String> {
  }

  private static Type fieldType(String name) throws Exception {
    Field field = A.class.getDeclaredField(name);
    return field.getGenericType();
  }

  private static Type cyclicContext() throws Exception {
    return fieldType("context");
  }

  private static TypeVariable<?> aTypeVariable() {
    return A.class.getTypeParameters()[0];
  }

  @Test
  public void resolveMutuallyRecursiveTypeVariablesTerminatesAtOriginalVariable() throws Exception {
    Type resolved = $Gson$Types.resolve(cyclicContext(), B.class, aTypeVariable());

    assertEquals(aTypeVariable(), resolved);
  }

  @Test
  public void resolveParameterizedTypeContainingMutuallyRecursiveVariableTerminates()
      throws Exception {
    Type resolved = $Gson$Types.resolve(cyclicContext(), B.class, fieldType("list"));

    assertTrue(resolved instanceof ParameterizedType);
    ParameterizedType parameterizedType = (ParameterizedType) resolved;
    assertEquals(List.class, parameterizedType.getRawType());
    assertEquals(aTypeVariable(), parameterizedType.getActualTypeArguments()[0]);
  }

  @Test
  public void resolveWildcardContainingMutuallyRecursiveVariableTerminates() throws Exception {
    Type resolved =
        $Gson$Types.resolve(cyclicContext(), B.class, fieldType("upperBoundedList"));

    assertTrue(resolved instanceof ParameterizedType);
    Type argument = ((ParameterizedType) resolved).getActualTypeArguments()[0];
    assertTrue(argument instanceof java.lang.reflect.WildcardType);
    java.lang.reflect.WildcardType wildcard = (java.lang.reflect.WildcardType) argument;
    assertEquals(aTypeVariable(), wildcard.getUpperBounds()[0]);
  }

  @Test
  public void resolveGenericArrayContainingMutuallyRecursiveVariableTerminates() throws Exception {
    Type resolved = $Gson$Types.resolve(cyclicContext(), B.class, fieldType("array"));

    assertTrue(resolved instanceof GenericArrayType);
    assertEquals(aTypeVariable(), ((GenericArrayType) resolved).getGenericComponentType());
  }

  @Test
  public void resolveConcreteSubclassTypeVariableToConcreteType() {
    Type resolved = $Gson$Types.resolve(ConcreteA.class, ConcreteA.class, aTypeVariable());

    assertEquals(String.class, resolved);
  }

  @Test
  public void resolveConcreteSubclassParameterizedFieldSubstitutesTypeArgument() throws Exception {
    Type resolved = $Gson$Types.resolve(ConcreteA.class, ConcreteA.class, fieldType("list"));

    assertTrue(resolved instanceof ParameterizedType);
    ParameterizedType parameterizedType = (ParameterizedType) resolved;
    assertEquals(List.class, parameterizedType.getRawType());
    assertEquals(String.class, parameterizedType.getActualTypeArguments()[0]);
  }
}
