package com.google.gson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class DefaultDateTypeAdapterBug17Test {

  private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

  @Test
  public void readNullConsumesNullAndReturnsNull() throws IOException {
    DefaultDateTypeAdapter adapter = new DefaultDateTypeAdapter(Date.class, PATTERN);

    Date result = adapter.read(new JsonReader(new StringReader("null")));

    assertNull(result);
  }

  @Test
  public void readNumberRejectsUnexpectedNonStringToken() throws IOException {
    DefaultDateTypeAdapter adapter = new DefaultDateTypeAdapter(Date.class, PATTERN);

    try {
      adapter.read(new JsonReader(new StringReader("123")));
      fail("A numeric JSON value is not a date string");
    } catch (JsonParseException expected) {
      assertTrue(expected.getMessage().contains("string"));
    }
  }

  @Test
  public void readMalformedStringThrowsJsonSyntaxException() throws IOException {
    DefaultDateTypeAdapter adapter = new DefaultDateTypeAdapter(Date.class, PATTERN);

    try {
      adapter.read(new JsonReader(new StringReader("\"not-a-date\"")));
      fail("An unparsable date string should be rejected");
    } catch (JsonSyntaxException expected) {
      assertEquals("not-a-date", expected.getMessage());
    }
  }

  @Test
  public void readIso8601StringProducesExpectedDate() throws IOException {
    DefaultDateTypeAdapter adapter = new DefaultDateTypeAdapter(Date.class, PATTERN);

    Date result = adapter.read(new JsonReader(new StringReader("\"1970-01-01T00:00:00Z\"")));

    assertEquals(0L, result.getTime());
  }

  @Test
  public void readTimestampAdapterReturnsTimestampWithParsedInstant() throws IOException {
    DefaultDateTypeAdapter adapter = new DefaultDateTypeAdapter(Timestamp.class, PATTERN);

    Date result = adapter.read(new JsonReader(new StringReader("\"1970-01-01T00:00:00Z\"")));

    assertTrue(result instanceof Timestamp);
    assertEquals(0L, result.getTime());
  }

  @Test
  public void readSqlDateAdapterReturnsSqlDateWithParsedInstant() throws IOException {
    DefaultDateTypeAdapter adapter = new DefaultDateTypeAdapter(java.sql.Date.class, PATTERN);

    Date result = adapter.read(new JsonReader(new StringReader("\"1970-01-01T00:00:00Z\"")));

    assertTrue(result instanceof java.sql.Date);
    assertEquals(0L, result.getTime());
  }

  @Test
  public void writeNullEmitsJsonNull() throws IOException {
    DefaultDateTypeAdapter adapter = new DefaultDateTypeAdapter(Date.class, PATTERN);
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);

    adapter.write(writer, null);
    writer.close();

    assertEquals("null", output.toString());
  }

  @Test
  public void writeDateEmitsConfiguredFormatAndCanBeReadBack() throws Exception {
    DefaultDateTypeAdapter adapter = new DefaultDateTypeAdapter(Date.class, PATTERN);
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);
    Date epoch = new Date(0L);

    adapter.write(writer, epoch);
    writer.close();

    String expectedText = new SimpleDateFormat(PATTERN, Locale.US).format(epoch);
    assertEquals("\"" + expectedText + "\"", output.toString());

    Date reread = adapter.read(new JsonReader(new StringReader(output.toString())));
    assertEquals(epoch.getTime(), reread.getTime());
  }

  @Test
  public void unsupportedDateSubclassIsRejectedByConstructor() {
    try {
      new DefaultDateTypeAdapter(UnsupportedDate.class, PATTERN);
      fail("Only Date, Timestamp, and java.sql.Date are supported");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("Date type must be one of"));
    }
  }

  private static final class UnsupportedDate extends Date {
    private static final long serialVersionUID = 1L;
  }
}