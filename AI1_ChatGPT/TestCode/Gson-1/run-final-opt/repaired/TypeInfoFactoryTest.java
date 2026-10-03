package com.google.gson;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import junit.framework.TestCase;

public class TypeInfoFactoryTest extends TestCase {

  public static class GenericValue<T> {
    public T value;
  }

  public static class GenericArrayValue<T> {
    public T[] values;
  }

  public static class GenericListValue<T> {
    public List<T> values;
  }

  public void testRawGenericTypeVariableDeserializesUsingItsBound() {
    Gson gson = new Gson();

    GenericValue value = gson.fromJson("{\"value\":\"text\"}", GenericValue.class);

    assertEquals("text", value.value);
  }

  public void testRawGenericArrayTypeVariableDeserializes() {
    Gson gson = new Gson();

    GenericArrayValue value =
        gson.fromJson("{\"values\":[\"first\",\"second\"]}", GenericArrayValue.class);

    assertNotNull(value.values);
    assertEquals(2, value.values.length);
    assertEquals("first", value.values[0]);
    assertEquals("second", value.values[1]);
  }

  public void testRawGenericTypeVariableInsideParameterizedFieldDeserializes() {
    Gson gson = new Gson();

    GenericListValue value =
        gson.fromJson("{\"values\":[\"one\",\"two\"]}", GenericListValue.class);

    assertNotNull(value.values);
    assertEquals(2, value.values.size());
    assertEquals("one", value.values.get(0));
    assertEquals("two", value.values.get(1));
  }

  public void testParameterizedGenericFieldResolvesActualTypeArgument() {
    Gson gson = new Gson();

    GenericValue<Integer> value = (GenericValue<Integer>) gson.fromJson(
        "{\"value\":7}", new TypeToken<GenericValue<Integer>>() {}.getType());

    assertNotNull(value.value);
    assertEquals(Integer.class, value.value.getClass());
    assertEquals(7, value.value.intValue());
  }

  public void testParameterizedGenericArrayResolvesComponentType() {
    Gson gson = new Gson();

    GenericArrayValue<Integer> value = (GenericArrayValue<Integer>) gson.fromJson(
        "{\"values\":[1,2]}", new TypeToken<GenericArrayValue<Integer>>() {}.getType());

    assertNotNull(value.values);
    assertEquals(Integer[].class, value.values.getClass());
    assertEquals(Integer.valueOf(1), value.values[0]);
    assertEquals(Integer.valueOf(2), value.values[1]);
  }

  public void testTypeInfoFactoryAcceptsClassAndGenericArrayTypes() throws Exception {
    Type genericArrayType = new TypeToken<List<String>[]>() {}.getType();

    assertNotNull(TypeInfoFactory.getTypeInfoForArray(String[].class));
    assertNotNull(TypeInfoFactory.getTypeInfoForArray(genericArrayType));
  }

  public void testTypeInfoFactoryRejectsNonArrayType() {
    try {
      TypeInfoFactory.getTypeInfoForArray(String.class);
      fail("A non-array type must be rejected");
    } catch (IllegalArgumentException expected) {
      assertNotNull(expected);
    }
  }
}
