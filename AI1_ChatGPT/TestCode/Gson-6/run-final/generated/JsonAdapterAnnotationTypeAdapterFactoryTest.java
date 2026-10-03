package com.google.gson.internal.bind;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;

import com.google.gson.Gson;
import com.google.gson.InstanceCreator;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import junit.framework.TestCase;

public class JsonAdapterAnnotationTypeAdapterFactoryTest extends TestCase {

  private JsonAdapterAnnotationTypeAdapterFactory newFactory() {
    return new JsonAdapterAnnotationTypeAdapterFactory(
        new ConstructorConstructor(Collections.<Type, InstanceCreator<?>>emptyMap()));
  }

  public void testCreateReturnsNullForTypeWithoutJsonAdapterAnnotation() {
    TypeAdapter<UnannotatedType> adapter = newFactory().create(
        new Gson(), TypeToken.get(UnannotatedType.class));

    assertNull(adapter);
  }

  public void testAnnotatedTypeAdapterIsUsedForSerializationAndDeserialization() {
    ValueTypeAdapter.writeCalls = 0;
    ValueTypeAdapter.readCalls = 0;

    Gson gson = new Gson();

    assertEquals("\"adapter-value\"", gson.toJson(new AdapterAnnotatedType()));
    assertEquals(1, ValueTypeAdapter.writeCalls);

    AdapterAnnotatedType result = gson.fromJson("\"input\"", AdapterAnnotatedType.class);
    assertNotNull(result);
    assertEquals(17, result.value);
    assertEquals(1, ValueTypeAdapter.readCalls);
  }

  public void testAnnotatedTypeAdapterIsNullSafe() {
    ValueTypeAdapter.writeCalls = 0;
    ValueTypeAdapter.readCalls = 0;

    Gson gson = new Gson();

    assertEquals("null", gson.toJson(null, AdapterAnnotatedType.class));
    assertNull(gson.fromJson("null", AdapterAnnotatedType.class));

    assertEquals(0, ValueTypeAdapter.writeCalls);
    assertEquals(0, ValueTypeAdapter.readCalls);
  }

  public void testAnnotatedTypeAdapterFactoryIsUsedForSerializationAndDeserialization() {
    ProducingAdapterFactory.createCalls = 0;
    FactoryValueTypeAdapter.writeCalls = 0;
    FactoryValueTypeAdapter.readCalls = 0;

    Gson gson = new Gson();

    assertEquals("\"factory-value\"", gson.toJson(new FactoryAnnotatedType()));
    assertEquals(1, ProducingAdapterFactory.createCalls);
    assertEquals(1, FactoryValueTypeAdapter.writeCalls);

    FactoryAnnotatedType result = gson.fromJson("\"input\"", FactoryAnnotatedType.class);
    assertNotNull(result);
    assertEquals(23, result.value);
    assertEquals(1, FactoryValueTypeAdapter.readCalls);
  }

  public void testInvalidJsonAdapterValueThrowsIllegalArgumentException() {
    try {
      newFactory().create(new Gson(), TypeToken.get(InvalidAnnotatedType.class));
      fail("A @JsonAdapter value which is neither a TypeAdapter nor a TypeAdapterFactory must fail");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains(
          "@JsonAdapter value must be TypeAdapter or TypeAdapterFactory reference."));
    }
  }

  public void testNullReturningAdapterFactoryAllowsDefaultSerialization() {
    NullReturningAdapterFactory.createCalls = 0;

    Gson gson = new Gson();
    NullFactoryAnnotatedType value = new NullFactoryAnnotatedType();
    value.value = 7;

    assertEquals("{\"value\":7}", gson.toJson(value));
    assertTrue(NullReturningAdapterFactory.createCalls > 0);
  }

  public void testNullReturningAdapterFactoryAllowsDefaultDeserialization() {
    NullReturningAdapterFactory.createCalls = 0;

    NullFactoryAnnotatedType result =
        new Gson().fromJson("{\"value\":9}", NullFactoryAnnotatedType.class);

    assertNotNull(result);
    assertEquals(9, result.value);
    assertTrue(NullReturningAdapterFactory.createCalls > 0);
  }

  public static final class UnannotatedType {
    int value;
  }

  @JsonAdapter(ValueTypeAdapter.class)
  public static final class AdapterAnnotatedType {
    int value;
  }

  public static final class ValueTypeAdapter extends TypeAdapter<AdapterAnnotatedType> {
    static int writeCalls;
    static int readCalls;

    @Override
    public void write(JsonWriter out, AdapterAnnotatedType value) throws IOException {
      writeCalls++;
      out.value("adapter-value");
    }

    @Override
    public AdapterAnnotatedType read(JsonReader in) throws IOException {
      readCalls++;
      in.nextString();

      AdapterAnnotatedType result = new AdapterAnnotatedType();
      result.value = 17;
      return result;
    }
  }

  @JsonAdapter(ProducingAdapterFactory.class)
  public static final class FactoryAnnotatedType {
    int value;
  }

  public static final class ProducingAdapterFactory implements TypeAdapterFactory {
    static int createCalls;

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
      createCalls++;
      if (type.getRawType() == FactoryAnnotatedType.class) {
        @SuppressWarnings("unchecked")
        TypeAdapter<T> adapter = (TypeAdapter<T>) new FactoryValueTypeAdapter();
        return adapter;
      }
      return null;
    }
  }

  public static final class FactoryValueTypeAdapter extends TypeAdapter<FactoryAnnotatedType> {
    static int writeCalls;
    static int readCalls;

    @Override
    public void write(JsonWriter out, FactoryAnnotatedType value) throws IOException {
      writeCalls++;
      out.value("factory-value");
    }

    @Override
    public FactoryAnnotatedType read(JsonReader in) throws IOException {
      readCalls++;
      in.nextString();

      FactoryAnnotatedType result = new FactoryAnnotatedType();
      result.value = 23;
      return result;
    }
  }

  @JsonAdapter(String.class)
  public static final class InvalidAnnotatedType {
  }

  @JsonAdapter(NullReturningAdapterFactory.class)
  public static final class NullFactoryAnnotatedType {
    public int value;
  }

  public static final class NullReturningAdapterFactory implements TypeAdapterFactory {
    static int createCalls;

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
      createCalls++;
      return null;
    }
  }
}
