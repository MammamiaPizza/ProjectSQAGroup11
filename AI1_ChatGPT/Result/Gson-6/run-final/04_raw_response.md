```java
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
import com.google.gson.stream.JsonToken;
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

  public void testAnnotatedTypeAdapterWithNullSafeFalseReceivesNullValues() {
    NullUnsafeTypeAdapter.writeCalls = 0;
    NullUnsafeTypeAdapter.readCalls = 0;

    Gson gson = new Gson();

    assertEquals("\"adapter-handled-null\"", gson.toJson(null, NullUnsafeAdapterAnnotatedType.class));
    assertEquals(1, NullUnsafeTypeAdapter.writeCalls);

    NullUnsafeAdapterAnnotatedType result =
        gson.fromJson("null", NullUnsafeAdapterAnnotatedType.class);
    assertNotNull(result);
    assertEquals(31, result.value);
    assertEquals(1, NullUnsafeTypeAdapter.readCalls);
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

  public void testAnnotatedTypeAdapterFactoryWithNullSafeFalseReceivesNullValues() {
    NullUnsafeProducingAdapterFactory.createCalls = 0;
    NullUnsafeFactoryTypeAdapter.writeCalls = 0;
    NullUnsafeFactoryTypeAdapter.readCalls = 0;

    Gson gson = new Gson();

    assertEquals("\"factory-handled-null\"",
        gson.toJson(null, NullUnsafeFactoryAnnotatedType.class));
    assertEquals(1, NullUnsafeProducingAdapterFactory.createCalls);
    assertEquals(1, NullUnsafeFactoryTypeAdapter.writeCalls);

    NullUnsafeFactoryAnnotatedType result =
        gson.fromJson("null", NullUnsafeFactoryAnnotatedType.class);
    assertNotNull(result);
    assertEquals(37, result.value);
    assertEquals(1, NullUnsafeFactoryTypeAdapter.readCalls);
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

  @JsonAdapter(value = NullUnsafeTypeAdapter.class, nullSafe = false)
  public static final class NullUnsafeAdapterAnnotatedType {
    int value;
  }

  public static final class NullUnsafeTypeAdapter
      extends TypeAdapter<NullUnsafeAdapterAnnotatedType> {
    static int writeCalls;
    static int readCalls;

    @Override
    public void write(JsonWriter out, NullUnsafeAdapterAnnotatedType value) throws IOException {
      writeCalls++;
      if (value == null) {
        out.value("adapter-handled-null");
      } else {
        out.value("adapter-value");
      }
    }

    @Override
    public NullUnsafeAdapterAnnotatedType read(JsonReader in) throws IOException {
      readCalls++;
      if (in.peek() == JsonToken.NULL) {
        in.nextNull();
        NullUnsafeAdapterAnnotatedType result = new NullUnsafeAdapterAnnotatedType();
        result.value = 31;
        return result;
      }

      in.nextString();
      NullUnsafeAdapterAnnotatedType result = new NullUnsafeAdapterAnnotatedType();
      result.value = 32;
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

  @JsonAdapter(value = NullUnsafeProducingAdapterFactory.class, nullSafe = false)
  public static final class NullUnsafeFactoryAnnotatedType {
    int value;
  }

  public static final class NullUnsafeProducingAdapterFactory implements TypeAdapterFactory {
    static int createCalls;

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
      createCalls++;
      if (type.getRawType() == NullUnsafeFactoryAnnotatedType.class) {
        @SuppressWarnings("unchecked")
        TypeAdapter<T> adapter = (TypeAdapter<T>) new NullUnsafeFactoryTypeAdapter();
        return adapter;
      }
      return null;
    }
  }

  public static final class NullUnsafeFactoryTypeAdapter
      extends TypeAdapter<NullUnsafeFactoryAnnotatedType> {
    static int writeCalls;
    static int readCalls;

    @Override
    public void write(JsonWriter out, NullUnsafeFactoryAnnotatedType value) throws IOException {
      writeCalls++;
      if (value == null) {
        out.value("factory-handled-null");
      } else {
        out.value("factory-value");
      }
    }

    @Override
    public NullUnsafeFactoryAnnotatedType read(JsonReader in) throws IOException {
      readCalls++;
      if (in.peek() == JsonToken.NULL) {
        in.nextNull();
        NullUnsafeFactoryAnnotatedType result = new NullUnsafeFactoryAnnotatedType();
        result.value = 37;
        return result;
      }

      in.nextString();
      NullUnsafeFactoryAnnotatedType result = new NullUnsafeFactoryAnnotatedType();
      result.value = 38;
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
```

New test coverage and bug-detection targets:

* `testAnnotatedTypeAdapterWithNullSafeFalseReceivesNullValues`
  * Targets the `@JsonAdapter(nullSafe = false)` API contract for a directly configured `TypeAdapter`.
  * Verifies both null serialization and null deserialization are delegated to the adapter rather than intercepted by `TypeAdapter.nullSafe()`.
  * Distinguishes the defective unconditional `typeAdapter.nullSafe()` behavior from the expected behavior: the defective implementation serializes as plain `null`, deserializes to Java `null`, and does not invoke the custom adapter.

* `testAnnotatedTypeAdapterFactoryWithNullSafeFalseReceivesNullValues`
  * Targets the same `nullSafe = false` contract when the annotation refers to a `TypeAdapterFactory`.
  * Exercises the `TypeAdapterFactory` path of `getTypeAdapter`, ensuring that an adapter returned by a factory is not wrapped when null safety is explicitly disabled.
  * Detects the same fault independently for factory-produced adapters, covering both possible annotation value categories.