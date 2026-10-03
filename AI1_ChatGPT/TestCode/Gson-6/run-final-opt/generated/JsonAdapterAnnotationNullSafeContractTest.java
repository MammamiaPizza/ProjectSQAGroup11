package com.google.gson.regression;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import junit.framework.TestCase;

import java.io.IOException;

public class JsonAdapterAnnotationNullSafeContractTest extends TestCase {

  public void testTypeAdapterWithNullSafeFalseDeserializesNullThroughAdapter() {
    DirectValue value = new Gson().fromJson("null", DirectValue.class);

    assertNotNull(value);
    assertEquals("direct-null-read", value.value);
  }

  public void testTypeAdapterWithNullSafeFalseSerializesNullThroughAdapter() {
    String json = new Gson().toJson(null, DirectValue.class);

    assertEquals("\"direct-null-written\"", json);
  }

  public void testTypeAdapterWithNullSafeFalseHandlesNormalValues() {
    Gson gson = new Gson();

    assertEquals("\"ordinary\"", gson.toJson(new DirectValue("ordinary"), DirectValue.class));
    assertEquals("ordinary", gson.fromJson("\"ordinary\"", DirectValue.class).value);
  }

  public void testTypeAdapterFactoryWithNullSafeFalseDeserializesNullThroughAdapter() {
    FactoryValue value = new Gson().fromJson("null", FactoryValue.class);

    assertNotNull(value);
    assertEquals("factory-null-read", value.value);
  }

  public void testTypeAdapterFactoryWithNullSafeFalseSerializesNullThroughAdapter() {
    String json = new Gson().toJson(null, FactoryValue.class);

    assertEquals("\"factory-null-written\"", json);
  }

  public void testDefaultNullSafeSettingPreventsAdapterFromReceivingNullOnDeserialize() {
    DefaultNullSafeValue value = new Gson().fromJson("null", DefaultNullSafeValue.class);

    assertNull(value);
  }

  public void testDefaultNullSafeSettingPreventsAdapterFromReceivingNullOnSerialize() {
    String json = new Gson().toJson(null, DefaultNullSafeValue.class);

    assertEquals("null", json);
  }

  public void testInvalidJsonAdapterValueIsRejected() {
    try {
      new Gson().getAdapter(InvalidAdapterValue.class);
      fail("Expected an invalid @JsonAdapter value to be rejected");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("@JsonAdapter value must be TypeAdapter or TypeAdapterFactory"));
    }
  }

  @JsonAdapter(value = DirectAdapter.class, nullSafe = false)
  private static final class DirectValue {
    final String value;

    DirectValue(String value) {
      this.value = value;
    }
  }

  public static final class DirectAdapter extends TypeAdapter<DirectValue> {
    @Override
    public void write(JsonWriter out, DirectValue value) throws IOException {
      out.value(value == null ? "direct-null-written" : value.value);
    }

    @Override
    public DirectValue read(JsonReader in) throws IOException {
      if (in.peek() == JsonToken.NULL) {
        in.nextNull();
        return new DirectValue("direct-null-read");
      }
      return new DirectValue(in.nextString());
    }
  }

  @JsonAdapter(value = FactoryAdapterFactory.class, nullSafe = false)
  private static final class FactoryValue {
    final String value;

    FactoryValue(String value) {
      this.value = value;
    }
  }

  public static final class FactoryAdapterFactory implements TypeAdapterFactory {
    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
      if (type.getRawType() == FactoryValue.class) {
        return (TypeAdapter<T>) new FactoryAdapter();
      }
      return null;
    }
  }

  public static final class FactoryAdapter extends TypeAdapter<FactoryValue> {
    @Override
    public void write(JsonWriter out, FactoryValue value) throws IOException {
      out.value(value == null ? "factory-null-written" : value.value);
    }

    @Override
    public FactoryValue read(JsonReader in) throws IOException {
      if (in.peek() == JsonToken.NULL) {
        in.nextNull();
        return new FactoryValue("factory-null-read");
      }
      return new FactoryValue(in.nextString());
    }
  }

  @JsonAdapter(DefaultNullSafeAdapter.class)
  private static final class DefaultNullSafeValue {
    final String value;

    DefaultNullSafeValue(String value) {
      this.value = value;
    }
  }

  public static final class DefaultNullSafeAdapter extends TypeAdapter<DefaultNullSafeValue> {
    @Override
    public void write(JsonWriter out, DefaultNullSafeValue value) throws IOException {
      out.value(value == null ? "adapter-received-null" : value.value);
    }

    @Override
    public DefaultNullSafeValue read(JsonReader in) throws IOException {
      if (in.peek() == JsonToken.NULL) {
        in.nextNull();
        return new DefaultNullSafeValue("adapter-received-null");
      }
      return new DefaultNullSafeValue(in.nextString());
    }
  }

  @JsonAdapter(NotAnAdapter.class)
  private static final class InvalidAdapterValue {
  }

  public static final class NotAnAdapter {
  }
}
