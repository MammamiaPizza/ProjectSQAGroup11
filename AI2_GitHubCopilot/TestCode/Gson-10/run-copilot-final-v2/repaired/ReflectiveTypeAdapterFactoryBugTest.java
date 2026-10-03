package com.google.gson.functional;

 import com.google.gson.Gson;
 import com.google.gson.GsonBuilder;
 import com.google.gson.TypeAdapter;
 import com.google.gson.annotations.Expose;
 import com.google.gson.annotations.JsonAdapter;
 import com.google.gson.annotations.SerializedName;
 import com.google.gson.stream.JsonReader;
 import com.google.gson.stream.JsonWriter;
 import java.io.IOException;
 import junit.framework.TestCase;

 /**

 - Tests that {@link JsonAdapter} on primitive fields is honoured.
 - The buggy version ignores the annotation when the field type is primitive.
   */
  public class ReflectiveTypeAdapterFactoryBugTest extends TestCase {
  // ------------------------------------------------------------------------------------------
  // ADAPTERS
  // ------------------------------------------------------------------------------------------
  /** Serializes an Integer as an array containing its string form.
  */
  static final class IntArrayStringAdapter extends TypeAdapter<Integer> {
  @Override public void write(JsonWriter out, Integer value) throws IOException {
      if (value == null) {
          out.nullValue();
          return;
      }
      out.beginArray();
      out.value(value.toString());
      out.endArray();
  }
  @Override public Integer read(JsonReader in) throws IOException {
      if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
          in.nextNull();
          return null;
      }
      in.beginArray();
      Integer parsed = Integer.parseInt(in.nextString());
      in.endArray();
      return parsed;
  }
  }
  /** Serializes a Boolean as its upper-case string.
  */
  static final class BooleanStringAdapter extends TypeAdapter<Boolean> {
  @Override public void write(JsonWriter out, Boolean value) throws IOException {
      if (value == null) out.nullValue();
      else out.value(value ? "TRUE" : "FALSE");
  }
  @Override public Boolean read(JsonReader in) throws IOException {
      if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
          in.nextNull();
          return null;
      }
      return "TRUE".equals(in.nextString());
  }
  }
  /** Serializes a Long as a string prefixed with "L".
  */
  static final class LongPrefixAdapter extends TypeAdapter<Long> {
  @Override public void write(JsonWriter out, Long value) throws IOException {
      if (value == null) out.nullValue();
      else out.value("L" + value);
  }
  @Override public Long read(JsonReader in) throws IOException {
      if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
          in.nextNull();
          return null;
      }
      return Long.parseLong(in.nextString().substring(1));
  }
  }
  /** Reverses the string.
  */
  static final class ReverseStringAdapter extends TypeAdapter<String> {
  @Override public void write(JsonWriter out, String value) throws IOException {
      if (value == null) out.nullValue();
      else out.value(new StringBuilder(value).reverse().toString());
  }
  @Override public String read(JsonReader in) throws IOException {
      if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
          in.nextNull();
          return null;
      }
      return new StringBuilder(in.nextString()).reverse().toString();
  }
  }
  // ------------------------------------------------------------------------------------------
  // TEST BEANS
  // ------------------------------------------------------------------------------------------
  static class PrimitiveIntField {
  @JsonAdapter(IntArrayStringAdapter.class) int part = 42;
  }
  static class PrimitiveIntFieldNull {
  @JsonAdapter(IntArrayStringAdapter.class) Integer part = null; // bug: primitive field cannot be
null
  }
  static class PrimitiveBooleanField {
  @JsonAdapter(BooleanStringAdapter.class) boolean flag = true;
  }
  static class PrimitiveLongField {
  @JsonAdapter(LongPrefixAdapter.class) long value = 100L;
  }
  @JsonAdapter(ReverseStringAdapter.class)
  static class ClassLevelAnnotation {
  String text = "abc";
  }
  static class FieldOverridesClassAnnotation {
  @JsonAdapter(ReverseStringAdapter.class) String text = "xyz";
  }
  static class NonPrimitiveField {
  @JsonAdapter(ReverseStringAdapter.class) String name = "hello";
  }
  static class MultipleFields {
  @JsonAdapter(IntArrayStringAdapter.class) int count = 3;
  @JsonAdapter(BooleanStringAdapter.class) boolean active = false;
  String label = "test";
  }
  static class ContainerWithExpose {
  @Expose @JsonAdapter(IntArrayStringAdapter.class) int id = 7;
  String ignored = "secret";
  }
  // ------------------------------------------------------------------------------------------
  // TESTS
  // ------------------------------------------------------------------------------------------
  private Gson gson;
  @Override protected void setUp() {
  gson = new Gson();
  }
  /** Bug: @JsonAdapter on a primitive int field is ignored during serialization.
  */
  public void testPrimitiveIntFieldSerialization() {
  String json = gson.toJson(new PrimitiveIntField());
  assertEquals("{"part":["42"]}", json);
  }
  /** Bug: @JsonAdapter on a primitive int field is ignored during deserialization.
  */
  public void testPrimitiveIntFieldDeserialization() throws Exception {
  PrimitiveIntField obj = gson.fromJson("{"part":["99"]}", PrimitiveIntField.class);
  assertNotNull(obj);
  assertEquals(99, obj.part);
  }
  /** @JsonAdapter on a boolean primitive should work.
  */
  public void testPrimitiveBooleanField() {
  PrimitiveBooleanField bean = new PrimitiveBooleanField();
  bean.flag = true;
  String json = gson.toJson(bean);
  assertEquals("{"flag":"TRUE"}", json);
  PrimitiveBooleanField fromJson = gson.fromJson("{"flag":"FALSE"}",

PrimitiveBooleanField.class);
         assertEquals(false, fromJson.flag);
     }

  /** @JsonAdapter on a long primitive should work. */
  public void testPrimitiveLongField() {
      PrimitiveLongField bean = new PrimitiveLongField();
      bean.value = 1234L;
      String json = gson.toJson(bean);
      assertEquals("{\"value\":\"L1234\"}", json);

      PrimitiveLongField fromJson = gson.fromJson("{\"value\":\"L5678\"}",

PrimitiveLongField.class);
         assertEquals(5678L, fromJson.value);
     }

  /** @JsonAdapter on a non-primitive field works (regression guard). */
  public void testNonPrimitiveFieldAdapterWorks() {
      NonPrimitiveField bean = new NonPrimitiveField();
      bean.name = "abc";
      String json = gson.toJson(bean);
      assertEquals("{\"name\":\"cba\"}", json);
  }

  /** Class-level @JsonAdapter should be respected when no field-level override exists. */
  public void testClassLevelJsonAdapterWorks() {
      String json = gson.toJson(new ClassLevelAnnotation());
      assertEquals("{\"text\":\"cba\"}", json);
  }

  /** Field-level @JsonAdapter takes precedence over class-level annotation. */
  public void testFieldOverridesClassAnnotation() {
      FieldOverridesClassAnnotation bean = new FieldOverridesClassAnnotation();
      bean.text = "abc";
      String json = gson.toJson(bean);
      assertEquals("{\"text\":\"cba\"}", json);
  }

  /** Multiple fields with different adapters all work. */
  public void testMultipleFieldsWithAdapters() {
      MultipleFields bean = new MultipleFields();
      bean.count = 8;
      bean.active = true;
      bean.label = "ok";
      String json = gson.toJson(bean);
      assertTrue(json.contains("\"count\":[\"8\"]"));
      assertTrue(json.contains("\"active\":\"TRUE\""));
      assertTrue(json.contains("\"label\":\"ok\""));
  }

  /** @Expose combined with @JsonAdapter on a primitive field. */
  public void testExposeWithAdapterOnPrimitiveField() {
      Gson exposedGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
      String json = exposedGson.toJson(new ContainerWithExpose());
      assertEquals("{\"id\":[\"7\"]}", json);
  }

  /**
   * Null primitive-relaxed adapters: read null -> null; write null -> null.
   * We use an Integer field here because primitive int cannot hold null.
   */
  public void testNullValueWithAdapter() {
      String json = gson.toJson(new PrimitiveIntFieldNull());
      assertEquals("{\"part\":null}", json);
  }

  /** Round-trip: serialization + deserialization of a primitive field with adapter. */
  public void testRoundTripPrimitiveField() {
      PrimitiveIntField original = new PrimitiveIntField();
      original.part = 123;
      String json = gson.toJson(original);
      PrimitiveIntField restored = gson.fromJson(json, PrimitiveIntField.class);
      assertEquals(123, restored.part);
  }

 }
