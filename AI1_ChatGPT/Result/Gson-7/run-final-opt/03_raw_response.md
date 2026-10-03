package com.google.gson.stream;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Map;
import junit.framework.TestCase;

public class JsonReaderBug7Test extends TestCase {

  public void testUnquotedIntegerObjectKeyDeserializesAsInteger() {
    Type type = new TypeToken<Map<Integer, String>>() {}.getType();

    Map<Integer, String> result = new Gson().fromJson("{123:unquotedValue}", type);

    assertEquals(1, result.size());
    assertEquals("unquotedValue", result.get(Integer.valueOf(123)));
  }

  public void testUnquotedLongObjectKeyDeserializesAsLong() {
    Type type = new TypeToken<Map<Long, String>>() {}.getType();

    Map<Long, String> result =
        new Gson().fromJson("{9223372036854775807:value}", type);

    assertEquals(1, result.size());
    assertEquals("value", result.get(Long.valueOf(Long.MAX_VALUE)));
  }

  public void testUnquotedIntegersCanBePeekedAndReadInLenientArray() throws Exception {
    JsonReader reader = new JsonReader(new StringReader("[0,-1,2147483647]"));
    reader.setLenient(true);

    reader.beginArray();
    assertEquals(JsonToken.NUMBER, reader.peek());
    assertEquals(0, reader.nextInt());
    assertEquals(JsonToken.NUMBER, reader.peek());
    assertEquals(-1, reader.nextInt());
    assertEquals(JsonToken.NUMBER, reader.peek());
    assertEquals(Integer.MAX_VALUE, reader.nextInt());
    assertFalse(reader.hasNext());
    reader.endArray();
  }

  public void testUnquotedLongCanBePeekedAndReadInLenientArray() throws Exception {
    JsonReader reader = new JsonReader(
        new StringReader("[9223372036854775807,-9223372036854775808]"));
    reader.setLenient(true);

    reader.beginArray();
    assertEquals(JsonToken.NUMBER, reader.peek());
    assertEquals(Long.MAX_VALUE, reader.nextLong());
    assertEquals(JsonToken.NUMBER, reader.peek());
    assertEquals(Long.MIN_VALUE, reader.nextLong());
    reader.endArray();
  }

  public void testIntegerPrefixedUnquotedLiteralsRemainStrings() throws Exception {
    JsonReader reader = new JsonReader(
        new StringReader("[123abc,-45suffix,12e3tail,7.5words]"));
    reader.setLenient(true);

    reader.beginArray();

    assertEquals(JsonToken.STRING, reader.peek());
    assertEquals("123abc", reader.nextString());

    assertEquals(JsonToken.STRING, reader.peek());
    assertEquals("-45suffix", reader.nextString());

    assertEquals(JsonToken.STRING, reader.peek());
    assertEquals("12e3tail", reader.nextString());

    assertEquals(JsonToken.STRING, reader.peek());
    assertEquals("7.5words", reader.nextString());

    assertFalse(reader.hasNext());
    reader.endArray();
  }

  public void testNextIntRejectsUnquotedNonNumericLiteral() throws Exception {
    JsonReader reader = new JsonReader(new StringReader("[notANumber]"));
    reader.setLenient(true);

    reader.beginArray();
    assertEquals(JsonToken.STRING, reader.peek());
    try {
      reader.nextInt();
      fail("Expected nextInt to reject a nonnumeric unquoted literal");
    } catch (NumberFormatException expected) {
      assertTrue(expected.getMessage().indexOf("notANumber") >= 0);
    }
  }

  public void testNextIntRejectsIntegerOverflow() throws Exception {
    JsonReader reader = new JsonReader(new StringReader("[2147483648]"));
    reader.setLenient(true);

    reader.beginArray();
    assertEquals(JsonToken.NUMBER, reader.peek());
    try {
      reader.nextInt();
      fail("Expected nextInt to reject an out-of-range integer");
    } catch (NumberFormatException expected) {
      assertTrue(expected.getMessage().indexOf("Expected an int") >= 0);
    }
  }
}