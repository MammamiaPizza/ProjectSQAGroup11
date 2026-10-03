package com.google.gson;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import junit.framework.TestCase;

/**
 * Tests for {@link TypeInfoFactory}.
 */
public class TypeInfoFactoryTest extends TestCase {

  private static class GenericHolder<T> {
    public T value;
    public T[] array;
    public List<T> values;
    public List<? extends T> boundedValues;
  }

  private static class StringHolder extends GenericHolder<String> {
  }

  private static class TypeReferences {
    @SuppressWarnings("unused")
    GenericHolder<String> stringHolder;
  }

  public void testGetTypeInfoForArrayAcceptsClassArrayType() {
    TypeInfoArray typeInfo = TypeInfoFactory.getTypeInfoForArray(String[].class);

    assertEquals(String[].class, typeInfo.getType());
    assertEquals(String[].class, typeInfo.getRawClass());
  }

  public void testGetTypeInfoForArrayRejectsNonArrayType() {
    try {
      TypeInfoFactory.getTypeInfoForArray(String.class);
      fail("A non-array type must be rejected");
    } catch (IllegalArgumentException expected) {
      assertNotNull(expected);
    }
  }

  public void testGetTypeInfoForFieldReturnsConcreteNonGenericFieldType()
      throws NoSuchFieldException {
    Field field = String.class.getDeclaredField("value");

    TypeInfo typeInfo = TypeInfoFactory.getTypeInfoForField(field, String.class);

    assertEquals(char[].class, typeInfo.getType());
  }

  public void testGetTypeInfoForFieldResolvesTypeVariableFromParameterizedParent()
      throws NoSuchFieldException {
    Field valueField = GenericHolder.class.getField("value");
    Type parameterizedHolderType = getStringHolderType();

    TypeInfo typeInfo =
        TypeInfoFactory.getTypeInfoForField(valueField, parameterizedHolderType);

    assertEquals(String.class, typeInfo.getType());
  }

  public void testGetTypeInfoForFieldResolvesGenericArrayComponentType()
      throws NoSuchFieldException {
    Field arrayField = GenericHolder.class.getField("array");
    Type parameterizedHolderType = getStringHolderType();

    TypeInfo typeInfo =
        TypeInfoFactory.getTypeInfoForField(arrayField, parameterizedHolderType);

    assertEquals(String[].class, typeInfo.getType());
    assertEquals(String[].class, typeInfo.getRawClass());
  }

  public void testGetTypeInfoForFieldResolvesTypeVariableInsideParameterizedField()
      throws NoSuchFieldException {
    Field valuesField = GenericHolder.class.getField("values");
    Type parameterizedHolderType = getStringHolderType();

    TypeInfo typeInfo =
        TypeInfoFactory.getTypeInfoForField(valuesField, parameterizedHolderType);

    assertEquals(List.class, typeInfo.getRawClass());
    assertParameterizedTypeArgument(typeInfo.getType(), List.class, String.class);
  }

  public void testGetTypeInfoForFieldResolvesWildcardUpperBoundContainingTypeVariable()
      throws NoSuchFieldException {
    Field boundedValuesField = GenericHolder.class.getField("boundedValues");
    Type parameterizedHolderType = getStringHolderType();

    TypeInfo typeInfo =
        TypeInfoFactory.getTypeInfoForField(boundedValuesField, parameterizedHolderType);

    assertEquals(List.class, typeInfo.getRawClass());
    assertParameterizedTypeArgument(typeInfo.getType(), List.class, String.class);
  }

  public void testGetTypeInfoForFieldResolvesInheritedGenericFieldUsingConcreteSubclass()
      throws NoSuchFieldException {
    Field inheritedValueField = StringHolder.class.getField("value");

    TypeInfo typeInfo =
        TypeInfoFactory.getTypeInfoForField(inheritedValueField, StringHolder.class);

    assertEquals(
        "A field inherited from GenericHolder<String> must resolve T to String "
            + "when the supplied containing type is the concrete subclass.",
        String.class,
        typeInfo.getType());
  }

  public void testGetTypeInfoForFieldRejectsUnresolvedRawGenericType()
      throws NoSuchFieldException {
    Field valueField = GenericHolder.class.getField("value");

    try {
      TypeInfoFactory.getTypeInfoForField(valueField, GenericHolder.class);
      fail("A raw generic type does not provide an actual type for T");
    } catch (UnsupportedOperationException expected) {
      assertTrue(expected.getMessage().indexOf("Expecting parameterized type") >= 0);
    }
  }

  private Type getStringHolderType() throws NoSuchFieldException {
    return TypeReferences.class.getDeclaredField("stringHolder").getGenericType();
  }

  private void assertParameterizedTypeArgument(
      Type type, Class<?> expectedRawType, Type expectedArgument) {
    assertTrue("Expected a ParameterizedType but was: " + type,
        type instanceof ParameterizedType);

    ParameterizedType parameterizedType = (ParameterizedType) type;
    assertEquals(expectedRawType, parameterizedType.getRawType());
    assertEquals(1, parameterizedType.getActualTypeArguments().length);
    assertEquals(expectedArgument, parameterizedType.getActualTypeArguments()[0]);
  }
}
