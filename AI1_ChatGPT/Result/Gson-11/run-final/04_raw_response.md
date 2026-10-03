```java
package com.google.gson.internal.bind;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.BitSet;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class TypeAdaptersTest {

  private static JsonReader reader(String json) {
    return new JsonReader(new StringReader(json));
  }

  private static <T> String write(TypeAdapter<T> adapter, T value) throws IOException {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);
    adapter.write(writer, value);
    writer.flush();
    return output.toString();
  }

  private enum NamedValue {
    @SerializedName(value = "wire-name", alternate = { "legacy-name" })
    VALUE,

    PLAIN
  }

  @Test
  public void numberAdapterReadsNumberTokenAndNull() throws IOException {
    Number number = TypeAdapters.NUMBER.read(reader("12345678901234567890"));
    assertEquals("12345678901234567890", number.toString());

    assertNull(TypeAdapters.NUMBER.read(reader("null")));
  }

  @Test
  public void numberAdapterAcceptsQuotedNumericStringForBackwardCompatibleDeserialization()
      throws IOException {
    Number number = new Gson().fromJson("\"42.75\"", Number.class);

    assertNotNull(number);
    assertEquals(42.75d, number.doubleValue(), 0.0d);
  }

  @Test(expected = JsonSyntaxException.class)
  public void numberAdapterRejectsNonScalarJsonValues() throws IOException {
    TypeAdapters.NUMBER.read(reader("[1]"));
  }

  @Test
  public void bitSetAdapterReadsNumbersBooleansAndNumericStringsAndWritesSetBits()
      throws IOException {
    BitSet bitSet = TypeAdapters.BIT_SET.read(reader("[1, false, \"2\", 0, true]"));

    assertTrue(bitSet.get(0));
    assertFalse(bitSet.get(1));
    assertTrue(bitSet.get(2));
    assertFalse(bitSet.get(3));
    assertTrue(bitSet.get(4));
    assertEquals("[1,0,1,0,1]", write(TypeAdapters.BIT_SET, bitSet));
  }

  @Test(expected = JsonSyntaxException.class)
  public void bitSetAdapterRejectsNonNumericStringValues() throws IOException {
    TypeAdapters.BIT_SET.read(reader("[\"not-a-bit\"]"));
  }

  @Test
  public void booleanAndStringAdaptersHandleCompatibilityCoercionsAndNulls()
      throws IOException {
    assertEquals(Boolean.TRUE, TypeAdapters.BOOLEAN.read(reader("\"true\"")));
    assertEquals(Boolean.FALSE, TypeAdapters.BOOLEAN.read(reader("\"anything-else\"")));
    assertEquals("true", TypeAdapters.STRING.read(reader("true")));
    assertNull(TypeAdapters.BOOLEAN.read(reader("null")));
    assertEquals("\"null\"", write(TypeAdapters.BOOLEAN_AS_STRING, null));
  }

  @Test
  public void characterAdapterReadsSingleCharacterAndWritesNull() throws IOException {
    assertEquals(Character.valueOf('x'), TypeAdapters.CHARACTER.read(reader("\"x\"")));
    assertNull(TypeAdapters.CHARACTER.read(reader("null")));
    assertEquals("null", write(TypeAdapters.CHARACTER, null));
  }

  @Test(expected = JsonSyntaxException.class)
  public void characterAdapterRejectsStringsWithMoreThanOneCharacter() throws IOException {
    TypeAdapters.CHARACTER.read(reader("\"xy\""));
  }

  @Test
  public void calendarAdapterRoundTripsAllSupportedCalendarFields() throws IOException {
    Calendar calendar = new GregorianCalendar(2016, Calendar.NOVEMBER, 25, 23, 40, 14);

    String json = write(TypeAdapters.CALENDAR, calendar);
    Calendar restored = TypeAdapters.CALENDAR.read(reader(json));

    assertEquals(2016, restored.get(Calendar.YEAR));
    assertEquals(Calendar.NOVEMBER, restored.get(Calendar.MONTH));
    assertEquals(25, restored.get(Calendar.DAY_OF_MONTH));
    assertEquals(23, restored.get(Calendar.HOUR_OF_DAY));
    assertEquals(40, restored.get(Calendar.MINUTE));
    assertEquals(14, restored.get(Calendar.SECOND));
  }

  @Test
  public void jsonElementAdapterReadsAndWritesNestedJsonStructures() throws IOException {
    String json = "{\"text\":\"value\",\"number\":2,\"array\":[true,null]}";

    JsonElement element = TypeAdapters.JSON_ELEMENT.read(reader(json));

    assertTrue(element.isJsonObject());
    assertEquals("value", element.getAsJsonObject().get("text").getAsString());
    assertEquals(2, element.getAsJsonObject().get("number").getAsInt());
    assertTrue(element.getAsJsonObject().get("array").getAsJsonArray().get(1).isJsonNull());
    assertEquals(json, write(TypeAdapters.JSON_ELEMENT, element));
  }

  @Test
  public void enumFactoryUsesSerializedNameAndAlternateNames() throws IOException {
    TypeToken<NamedValue> type = TypeToken.get(NamedValue.class);
    TypeAdapter<NamedValue> adapter = TypeAdapters.ENUM_FACTORY.create(new Gson(), type);

    assertNotNull(adapter);
    assertEquals(NamedValue.VALUE, adapter.read(reader("\"wire-name\"")));
    assertEquals(NamedValue.VALUE, adapter.read(reader("\"legacy-name\"")));
    assertEquals(NamedValue.PLAIN, adapter.read(reader("\"PLAIN\"")));
    assertNull(adapter.read(reader("\"unknown\"")));
    assertEquals("\"wire-name\"", write(adapter, NamedValue.VALUE));
  }

  @Test
  public void exactTypeFactoryMatchesOnlyTheConfiguredRawType() {
    TypeAdapterFactory factory = TypeAdapters.newFactory(String.class, TypeAdapters.STRING);
    TypeToken<String> stringType = TypeToken.get(String.class);
    TypeToken<StringBuilder> builderType = TypeToken.get(StringBuilder.class);

    assertSame(TypeAdapters.STRING, factory.create(new Gson(), stringType));
    assertNull(factory.create(new Gson(), builderType));
  }

  @Test
  public void primitiveAndBoxedFactoryMatchesBothConfiguredTypes() {
    TypeAdapterFactory factory =
        TypeAdapters.newFactory(boolean.class, Boolean.class, TypeAdapters.BOOLEAN);
    TypeToken<Boolean> boxedType = TypeToken.get(Boolean.class);
    TypeToken<Boolean> primitiveType = TypeToken.get(boolean.class);
    TypeToken<String> otherType = TypeToken.get(String.class);

    assertSame(TypeAdapters.BOOLEAN, factory.create(new Gson(), boxedType));
    assertSame(TypeAdapters.BOOLEAN, factory.create(new Gson(), primitiveType));
    assertNull(factory.create(new Gson(), otherType));
  }

  @Test(expected = JsonSyntaxException.class)
  public void typeHierarchyFactoryRejectsResultOutsideRequestedSubtype() throws IOException {
    TypeAdapter<Number> integerProducingAdapter = new TypeAdapter<Number>() {
      @Override
      public Number read(JsonReader in) throws IOException {
        in.nextInt();
        return Integer.valueOf(1);
      }

      @Override
      public void write(JsonWriter out, Number value) throws IOException {
        out.value(value);
      }
    };

    TypeAdapterFactory factory =
        TypeAdapters.newTypeHierarchyFactory(Number.class, integerProducingAdapter);
    TypeAdapter<Double> doubleAdapter =
        factory.create(new Gson(), TypeToken.get(Double.class));

    assertNotNull(doubleAdapter);
    doubleAdapter.read(reader("1"));
  }

  @Test
  public void typeHierarchyFactoryDelegatesCompatibleReadsWritesAndNulls() throws IOException {
    TypeAdapter<Number> integerAdapter = new TypeAdapter<Number>() {
      @Override
      public Number read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
          in.nextNull();
          return null;
        }
        return Integer.valueOf(in.nextInt());
      }

      @Override
      public void write(JsonWriter out, Number value) throws IOException {
        out.value(value);
      }
    };

    TypeAdapterFactory factory =
        TypeAdapters.newTypeHierarchyFactory(Number.class, integerAdapter);
    TypeAdapter<Integer> integerTypeAdapter =
        factory.create(new Gson(), TypeToken.get(Integer.class));

    assertNotNull(integerTypeAdapter);
    assertEquals(Integer.valueOf(7), integerTypeAdapter.read(reader("7")));
    assertNull(integerTypeAdapter.read(reader("null")));
    assertEquals("8", write(integerTypeAdapter, Integer.valueOf(8)));
    assertNull(factory.create(new Gson(), TypeToken.get(String.class)));
  }

  @Test
  public void classAdapterAcceptsJsonNull() throws IOException {
    assertNull(TypeAdapters.CLASS.read(reader("null")));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void classAdapterRejectsSerializationOfClassObjects() throws IOException {
    TypeAdapters.CLASS.write(new JsonWriter(new StringWriter()), String.class);
  }
}
```

### Added test coverage

- **`typeHierarchyFactoryDelegatesCompatibleReadsWritesAndNulls`**
  - Covers the successful branch of `newTypeHierarchyFactory(...).create(...)` for a requested subtype (`Integer` under `Number`).
  - Covers the unmatched-type branch where the requested type (`String`) is not assignable from the configured hierarchy root (`Number`), causing `create(...)` to return `null`.
  - Covers the generated hierarchy adapter’s successful `read(...)` path where the returned value is an instance of the requested type.
  - Covers the generated hierarchy adapter’s `null` result path, including the short-circuit behavior of:
    ```java
    result != null && !requestedType.isInstance(result)
    ```
  - Covers the generated hierarchy adapter’s previously uncovered `write(...)` delegation:
    ```java
    typeAdapter.write(out, value);
    ```
  - Complements, rather than duplicates, the existing test which only verifies that a hierarchy adapter rejects a result outside the requested subtype.

The existing `numberAdapterAcceptsQuotedNumericStringForBackwardCompatibleDeserialization` test already targets Gson-11’s reported defect: deserializing a quoted numeric JSON value into `Number` must succeed rather than throw `JsonSyntaxException` with “Expecting number, got: STRING”.