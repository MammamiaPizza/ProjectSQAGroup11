package com.google.gson;

 import static org.junit.Assert.*;
 import org.junit.Test;
 import com.google.gson.stream.JsonReader;
 import com.google.gson.stream.JsonToken;
 import java.io.IOException;
 import java.io.StringReader;
 import java.sql.Timestamp;
 import java.text.DateFormat;
 import java.text.SimpleDateFormat;
 import java.util.Date;
 import java.util.Locale;
 import java.util.TimeZone;

 public class DefaultDateTypeAdapterTest {

     private static DateFormat newUtcFormat(String pattern) {
         SimpleDateFormat f = new SimpleDateFormat(pattern, Locale.US);
         f.setTimeZone(TimeZone.getTimeZone("UTC"));
         return f;
     }

     private DefaultDateTypeAdapter adapter(Date dateType, String pattern) {
         return new DefaultDateTypeAdapter(dateType, pattern);
     }

     private JsonReader reader(String json) {
         return new JsonReader(new StringReader(json));
     }

     @Test
     public void testNullValue() throws IOException {
         DefaultDateTypeAdapter adapter = adapter(Date.class, "yyyy-MM-dd");
         JsonReader in = reader("null");
         assertNull(adapter.read(in));
     }

     @Test
     public void testUnexpectedToken() throws IOException {
         DefaultDateTypeAdapter adapter = adapter(Date.class, "yyyy-MM-dd");
         for (String token : new String[]{"1", "1.5", "true", "{}", "[]"}) {
             JsonReader in = reader(token);
             try {
                 adapter.read(in);
                 fail("Expected JsonParseException for token: " + token);
             } catch (JsonParseException expected) {
                 assertEquals("The date should be a string value", expected.getMessage());
             }
         }
     }

     @Test
     public void testValidDateString() throws IOException {
         DefaultDateTypeAdapter adapter = adapter(Date.class, "yyyy-MM-dd");
         JsonReader in = reader("\"2024-01-15\"");
         Date date = adapter.read(in);
         DateFormat fmt = newUtcFormat("yyyy-MM-dd");
         assertEquals("2024-01-15", fmt.format(date));
     }

     @Test
     public void testValidDateTimeString() throws IOException {
         DefaultDateTypeAdapter adapter = new DefaultDateTypeAdapter(Date.class,
                 DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US),
                 DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US));
         JsonReader in = reader("\"Jan 15, 2024 10:30:00 AM\"");
         Date date = adapter.read(in);
         assertNotNull(date);
         assertTrue(date instanceof Date);
     }

     @Test
     public void testTimestampRead() throws IOException {
         DefaultDateTypeAdapter adapter = new DefaultDateTypeAdapter(Timestamp.class, "yyyy-MM-dd
HH:mm:ss");
         JsonReader in = reader("\"2024-01-15 10:30:00\"");
         Date date = adapter.read(in);
         assertTrue(date instanceof Timestamp);
         assertEquals(Timestamp.class, date.getClass());
     }

     @Test
     public void testSqlDateRead() throws IOException {
         DefaultDateTypeAdapter adapter = new DefaultDateTypeAdapter(java.sql.Date.class,
"yyyy-MM-dd");
         JsonReader in = reader("\"2024-01-15\"");
         Date date = adapter.read(in);
         assertTrue(date instanceof java.sql.Date);
         assertEquals(java.sql.Date.class, date.getClass());
     }

     @Test(expected = JsonSyntaxException.class)
     public void testMalformedDateString() throws IOException {
         DefaultDateTypeAdapter adapter = adapter(Date.class, "yyyy-MM-dd");
         JsonReader in = reader("\"not-a-date\"");
         adapter.read(in);
     }

     @Test
     public void testEmptyDateString() throws IOException {
         DefaultDateTypeAdapter adapter = adapter(Date.class, "yyyy-MM-dd");
         JsonReader in = reader("\"\"");
         try {
             adapter.read(in);
             fail("Expected exception for empty date string");
         } catch (JsonSyntaxException expected) {
             assertNotNull(expected.getMessage());
         }
     }

     @Test
     public void testWhitespaceOnlyDateString() throws IOException {
         DefaultDateTypeAdapter adapter = new DefaultDateTypeAdapter(Date.class,
                 DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US),
                 DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US));
         JsonReader in = reader("\"   \"");
         try {
             adapter.read(in);
             fail("Expected exception for whitespace-only date string");
         } catch (JsonSyntaxException expected) {
             assertNotNull(expected.getMessage());
         }
     }

     @Test
     public void testWriteNull() throws IOException {
         DefaultDateTypeAdapter adapter = adapter(Date.class, "yyyy-MM-dd");
         StringBuilder sb = new StringBuilder();
         com.google.gson.stream.JsonWriter writer = new com.google.gson.stream.JsonWriter(new
java.io.StringWriter() {
             @Override
             public String toString() { return sb.toString(); }
             @Override
             public void write(String s) { sb.append(s); }
         });
         adapter.write(writer, null);
         assertTrue(sb.toString().contains("null"));
     }

     @Test
     public void testWriteNonNull() throws IOException {
         DefaultDateTypeAdapter adapter = new DefaultDateTypeAdapter(Date.class,
                 DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US),
                 DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US));
         Date date = new Date(1705312200000L); // 2024-01-15T10:30:00 UTC
         final StringBuilder sb = new StringBuilder();
         com.google.gson.stream.JsonWriter writer = new com.google.gson.stream.JsonWriter(new
java.io.Writer() {
             @Override
             public void write(char[] cbuf, int off, int len) { sb.append(cbuf, off, len); }
             @Override
             public void flush() {}
             @Override
             public void close() {}
         });
         adapter.write(writer, date);
         writer.flush();
         String result = sb.toString();
         assertTrue(result.length() > 2);
         assertTrue(result.startsWith("\""));
         assertTrue(result.endsWith("\""));
     }

     @Test
     public void testConstructorGuardsDateType() {
         try {
             new DefaultDateTypeAdapter(String.class, "yyyy-MM-dd");
             fail("Expected IllegalArgumentException for invalid date type");
         } catch (IllegalArgumentException expected) {
             assertTrue(expected.getMessage().contains("Date type must be one of"));
         }
     }
 }