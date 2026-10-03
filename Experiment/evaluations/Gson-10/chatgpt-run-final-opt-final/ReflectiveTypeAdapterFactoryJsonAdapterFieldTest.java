package com.google.gson.functional;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import junit.framework.TestCase;

public class ReflectiveTypeAdapterFactoryJsonAdapterFieldTest extends TestCase {

  public static class StringIntegerAdapter extends TypeAdapter<Integer> {
    @Override
    public void write(JsonWriter out, Integer value) throws IOException {
      out.value(String.valueOf(value));
    }

    @Override
    public Integer read(JsonReader in) throws IOException {
      return Integer.valueOf(in.nextString());
    }
  }

  private static class AnnotatedPrimitiveField {
    @JsonAdapter(StringIntegerAdapter.class)
    int part;

    AnnotatedPrimitiveField(int part) {
      this.part = part;
    }
  }

  private static class PlainPrimitiveField {
    int part;

    PlainPrimitiveField(int part) {
      this.part = part;
    }
  }

  public void testPrimitiveFieldJsonAdapterTakesPrecedenceDuringSerialization() {
    Gson gson = new Gson();

    assertEquals("{\"part\":\"42\"}", gson.toJson(new AnnotatedPrimitiveField(42)));
  }

  public void testPrimitiveFieldWithoutJsonAdapterUsesDefaultNumericSerialization() {
    Gson gson = new Gson();

    assertEquals("{\"part\":42}", gson.toJson(new PlainPrimitiveField(42)));
  }

  public void testPrimitiveFieldJsonAdapterIsUsedDuringDeserialization() {
    Gson gson = new Gson();

    AnnotatedPrimitiveField value =
        gson.fromJson("{\"part\":\"42\"}", AnnotatedPrimitiveField.class);

    assertEquals(42, value.part);
  }
}
