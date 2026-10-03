```java
package com.google.gson.internal.bind;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collections;
import junit.framework.TestCase;

public class ReflectiveTypeAdapterFactoryTest extends TestCase {

  public void testJsonAdapterOnPrimitiveFieldControlsSerializationAndDeserialization() {
    Gson gson = new Gson();

    PrimitiveJsonAdapterHolder holder = new PrimitiveJsonAdapterHolder();
    holder.part = 42;

    assertEquals("{\"part\":\"42\"}", gson.toJson(holder));

    PrimitiveJsonAdapterHolder read =
        gson.fromJson("{\"part\":\"7\"}", PrimitiveJsonAdapterHolder.class);
    assertEquals(7, read.part);
  }

  public void testSerializedNameAlternateIsAcceptedForReadingButNotUsedForWriting() {
    Gson gson = new Gson();

    AliasedFieldHolder holder =
        gson.fromJson("{\"legacy_count\":3}", AliasedFieldHolder.class);

    assertEquals(3, holder.count);
    assertEquals("{\"count\":3}", gson.toJson(holder));
  }

  public void testNullForPrimitiveFieldDoesNotOverwriteInitializedValueAndUnknownFieldIsSkipped() {
    Gson gson = new Gson();

    PrimitiveDefaultHolder holder = gson.fromJson(
        "{\"number\":null,\"unknown\":{\"nested\":true}}",
        PrimitiveDefaultHolder.class);

    assertEquals(9, holder.number);
    assertEquals("initial", holder.text);
  }

  public void testNullObjectIsSerializedAndDeserializedAsNull() {
    Gson gson = new Gson();

    assertEquals("null", gson.toJson(null, PrimitiveDefaultHolder.class));
    assertNull(gson.fromJson("null", PrimitiveDefaultHolder.class));
  }

  public void testFieldNamingStrategyIsUsedWhenSerializedNameIsAbsent() {
    Gson gson = new GsonBuilder()
        .setFieldNamingStrategy(new FieldNamingStrategy() {
          @Override
          public String translateName(Field field) {
            return "wire_" + field.getName();
          }
        })
        .create();

    FieldNamingHolder holder = new FieldNamingHolder();
    holder.amount = 4;

    assertEquals("{\"wire_amount\":4}", gson.toJson(holder));

    FieldNamingHolder read =
        gson.fromJson("{\"wire_amount\":8}", FieldNamingHolder.class);
    assertEquals(8, read.amount);
  }

  public void testDuplicateJsonFieldNamesAreRejected() {
    Gson gson = new Gson();

    try {
      gson.toJson(new DuplicateJsonNameHolder());
      fail("Expected duplicate JSON field names to be rejected");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("multiple JSON fields named shared"));
    }
  }

  public void testObjectAdapterRejectsArrayInputAsJsonSyntaxException() {
    Gson gson = new Gson();

    try {
      gson.fromJson("[]", PrimitiveDefaultHolder.class);
      fail("Expected object adapter to reject an array");
    } catch (JsonSyntaxException expected) {
      assertNotNull(expected.getCause());
    }
  }

  public void testFactoryDoesNotCreateAdapterForPrimitiveButCreatesOneForObject() {
    ReflectiveTypeAdapterFactory factory = new ReflectiveTypeAdapterFactory(
        new ConstructorConstructor(
            Collections.<Type, InstanceCreator<?>>emptyMap()),
        FieldNamingPolicy.IDENTITY,
        Excluder.DEFAULT);

    assertNull(factory.create(new Gson(), TypeToken.get(Integer.TYPE)));
    assertNotNull(factory.create(new Gson(), TypeToken.get(PrimitiveDefaultHolder.class)));
  }

  public static final class PrimitiveJsonAdapterHolder {
    @JsonAdapter(DecimalStringIntegerAdapter.class)
    int part;
  }

  public static final class DecimalStringIntegerAdapter extends TypeAdapter<Integer> {
    @Override
    public void write(JsonWriter out, Integer value) throws IOException {
      if (value == null) {
        out.nullValue();
      } else {
        out.value(String.valueOf(value));
      }
    }

    @Override
    public Integer read(JsonReader in) throws IOException {
      return Integer.valueOf(in.nextString());
    }
  }

  public static final class AliasedFieldHolder {
    @SerializedName(value = "count", alternate = {"legacy_count", "old_count"})
    int count;
  }

  public static final class PrimitiveDefaultHolder {
    int number = 9;
    String text = "initial";
  }

  public static final class FieldNamingHolder {
    int amount;
  }

  public static final class DuplicateJsonNameHolder {
    @SerializedName("shared")
    int first;

    @SerializedName("shared")
    int second;
  }
}
```

Test coverage summary:

- `testJsonAdapterOnPrimitiveFieldControlsSerializationAndDeserialization` targets Gson-10 directly. It verifies that a field-level `@JsonAdapter` for a primitive `int` controls serialization, producing a JSON string (`"42"`) rather than allowing runtime-type adapter selection to produce a JSON number (`42`). It also verifies annotated deserialization.
- `testSerializedNameAlternateIsAcceptedForReadingButNotUsedForWriting` covers primary and alternate `@SerializedName` handling: alternate names may be read, but only the primary name is emitted.
- `testNullForPrimitiveFieldDoesNotOverwriteInitializedValueAndUnknownFieldIsSkipped` covers primitive-null protection and the unknown-field skip branch.
- `testNullObjectIsSerializedAndDeserializedAsNull` covers the adapter null write and null read branches.
- `testFieldNamingStrategyIsUsedWhenSerializedNameIsAbsent` covers field-name generation through `FieldNamingStrategy` when no `@SerializedName` annotation exists.
- `testDuplicateJsonFieldNamesAreRejected` covers duplicate bound-field detection and its `IllegalArgumentException`.
- `testObjectAdapterRejectsArrayInputAsJsonSyntaxException` covers conversion of invalid object input state into `JsonSyntaxException`.
- `testFactoryDoesNotCreateAdapterForPrimitiveButCreatesOneForObject` covers the `create` primitive-return-null branch and normal object-adapter creation branch.