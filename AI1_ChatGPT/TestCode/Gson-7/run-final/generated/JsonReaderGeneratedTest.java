package com.google.gson.stream;

import com.google.gson.internal.JsonReaderInternalAccess;
import java.io.StringReader;
import junit.framework.TestCase;

/**
 * Tests JsonReader parsing behavior, numeric conversions, lenient syntax,
 * recursive skipping, and unquoted object names promoted to values.
 */
public class JsonReaderGeneratedTest extends TestCase {

  public void testConstructorRejectsNullReader() {
    try {
      new JsonReader(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
      assertEquals("in == null", expected.getMessage());
    }
  }

  public void testReadObjectArrayAndPrimitiveTokens() throws Exception {
    JsonReader reader = new JsonReader(new StringReader(
        "{\"name\":\"Ada\",\"values\":[1,true,null,2.5]}"));

    assertEquals(JsonToken.BEGIN_OBJECT, reader.peek());
    reader.beginObject();

    assertTrue(reader.hasNext());
    assertEquals("name", reader.nextName());
    assertEquals(JsonToken.STRING, reader.peek());
    assertEquals("Ada", reader.nextString());

    assertTrue(reader.hasNext());
    assertEquals("values", reader.nextName());
    assertEquals(JsonToken.BEGIN_ARRAY, reader.peek());
    reader.beginArray();

    assertEquals(JsonToken.NUMBER, reader.peek());
    assertEquals(1, reader.nextInt());
    assertEquals(JsonToken.BOOLEAN, reader.peek());
    assertTrue(reader.nextBoolean());
    assertEquals(JsonToken.NULL, reader.peek());
    reader.nextNull();
    assertEquals(2.5d, reader.nextDouble(), 0.0d);

    assertFalse(reader.hasNext());
    reader.endArray();
    assertFalse(reader.hasNext());
    reader.endObject();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testNextStringAcceptsNumericValuesAndUnescapesQuotedStrings() throws Exception {
    JsonReader reader = new JsonReader(new StringReader(
        "[12,-3.5,\"line\\n\\u0041\"]"));

    reader.beginArray();
    assertEquals("12", reader.nextString());
    assertEquals("-3.5", reader.nextString());
    assertEquals("line\nA", reader.nextString());
    reader.endArray();

    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testNumericConversionsAndRangeValidation() throws Exception {
    JsonReader reader = new JsonReader(new StringReader(
        "[\"42\",1.0,9223372036854775807,-2147483648,2147483648]"));

    reader.beginArray();
    assertEquals(42, reader.nextInt());
    assertEquals(1L, reader.nextLong());
    assertEquals(Long.MAX_VALUE, reader.nextLong());
    assertEquals(Integer.MIN_VALUE, reader.nextInt());

    try {
      reader.nextInt();
      fail("Expected NumberFormatException for an out-of-range integer");
    } catch (NumberFormatException expected) {
      assertTrue(expected.getMessage().indexOf("Expected an int") != -1);
    }
  }

  public void testLenientUnquotedValuePrefixedWithDigitsIsAString() throws Exception {
    JsonReader reader = new JsonReader(new StringReader("[123abc]"));
    reader.setLenient(true);

    reader.beginArray();
    assertEquals(JsonToken.STRING, reader.peek());
    assertEquals("123abc", reader.nextString());
    reader.endArray();
  }

  public void testNextIntForNonNumericUnquotedStringThrowsNumberFormatException() throws Exception {
    JsonReader reader = new JsonReader(new StringReader("[123abc]"));
    reader.setLenient(true);
    reader.beginArray();

    assertEquals(JsonToken.STRING, reader.peek());
    try {
      reader.nextInt();
      fail("Expected NumberFormatException when a string cannot be parsed as an int");
    } catch (NumberFormatException expected) {
      assertNotNull(expected);
    }
  }

  public void testPromotedUnquotedIntegerAndLongObjectNamesCanBeReadAsNumbers() throws Exception {
    JsonReader reader = new JsonReader(new StringReader(
        "{12:\"integer\",3000000000:\"long\"}"));
    reader.setLenient(true);

    reader.beginObject();

    JsonReaderInternalAccess.INSTANCE.promoteNameToValue(reader);
    assertEquals(12, reader.nextInt());
    assertEquals("integer", reader.nextString());

    JsonReaderInternalAccess.INSTANCE.promoteNameToValue(reader);
    assertEquals(3000000000L, reader.nextLong());
    assertEquals("long", reader.nextString());

    assertFalse(reader.hasNext());
    reader.endObject();
  }

  public void testSkipValueSkipsNestedObjectAndArray() throws Exception {
    JsonReader reader = new JsonReader(new StringReader(
        "{\"known\":1,\"ignored\":{\"nested\":[true,null,{\"x\":\"y\"}]},\"after\":\"ok\"}"));

    reader.beginObject();

    assertEquals("known", reader.nextName());
    assertEquals(1, reader.nextInt());

    assertEquals("ignored", reader.nextName());
    reader.skipValue();

    assertEquals("after", reader.nextName());
    assertEquals("ok", reader.nextString());

    assertFalse(reader.hasNext());
    reader.endObject();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testStrictModeRejectsUnquotedObjectName() throws Exception {
    JsonReader reader = new JsonReader(new StringReader("{unquoted:1}"));
    reader.beginObject();

    try {
      reader.nextName();
      fail("Expected malformed JSON in strict mode");
    } catch (MalformedJsonException expected) {
      assertTrue(expected.getMessage().indexOf("setLenient(true)") != -1);
    }
  }

  public void testLenientModeAcceptsCommentsSingleQuotesAndAlternateSeparators() throws Exception {
    JsonReader reader = new JsonReader(new StringReader(
        "/* header */ {'first'=1;'second':'value'}"));
    reader.setLenient(true);

    assertTrue(reader.isLenient());
    reader.beginObject();

    assertEquals("first", reader.nextName());
    assertEquals(1, reader.nextInt());

    assertEquals("second", reader.nextName());
    assertEquals("value", reader.nextString());

    assertFalse(reader.hasNext());
    reader.endObject();
  }

  public void testCloseMakesReaderUnavailable() throws Exception {
    JsonReader reader = new JsonReader(new StringReader("[]"));
    reader.close();

    try {
      reader.peek();
      fail("Expected IllegalStateException after close");
    } catch (IllegalStateException expected) {
      assertTrue(expected.getMessage().indexOf("closed") != -1);
    }
  }

  public void testWrongStructuralMethodReportsIllegalState() throws Exception {
    JsonReader reader = new JsonReader(new StringReader("[]"));

    try {
      reader.beginObject();
      fail("Expected IllegalStateException when reading an array as an object");
    } catch (IllegalStateException expected) {
      assertTrue(expected.getMessage().indexOf("Expected BEGIN_OBJECT") != -1);
    }
  }
}
