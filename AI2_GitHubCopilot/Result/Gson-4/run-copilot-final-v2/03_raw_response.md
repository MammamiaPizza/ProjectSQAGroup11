package com.google.gson.stream;

 import java.io.IOException;
 import java.io.StringReader;
 import java.io.StringWriter;

 import junit.framework.TestCase;

 public class TopLevelValueTest extends TestCase {

     public void testReaderTopLevelValueTypes() throws IOException {
         JsonReader reader = new JsonReader(new StringReader("\"a string\""));
         assertEquals(JsonToken.STRING, reader.peek());
         assertEquals("a string", reader.nextString());

         reader = new JsonReader(new StringReader("true"));
         assertEquals(JsonToken.BOOLEAN, reader.peek());
         assertTrue(reader.nextBoolean());

         reader = new JsonReader(new StringReader("false"));
         assertFalse(reader.nextBoolean());

         reader = new JsonReader(new StringReader("null"));
         assertEquals(JsonToken.NULL, reader.peek());
         reader.nextNull();

         reader = new JsonReader(new StringReader("123"));
         assertEquals(JsonToken.NUMBER, reader.peek());
         assertEquals(123, reader.nextInt());

         reader = new JsonReader(new StringReader("123.4"));
         assertEquals(123.4, reader.nextDouble(), 0.0000001);
     }

     public void testReaderTopLevelValueWithSkipValue() throws IOException {
         JsonReader reader = new JsonReader(new StringReader("\"a string\""));
         reader.setLenient(true);
         assertEquals(JsonToken.STRING, reader.peek());
         reader.skipValue();
         assertFalse(reader.hasNext());
     }

     public void testReaderTopLevelEmptyString() throws IOException {
         JsonReader reader = new JsonReader(new StringReader("\"\""));
         assertEquals("", reader.nextString());
     }

     public void testReaderTopLevelNumberBoundaries() throws IOException {
         JsonReader reader = new JsonReader(new StringReader(String.valueOf(Long.MAX_VALUE)));
         assertEquals(Long.MAX_VALUE, reader.nextLong());

         reader = new JsonReader(new StringReader(String.valueOf(Long.MIN_VALUE)));
         assertEquals(Long.MIN_VALUE, reader.nextLong());

         reader = new JsonReader(new StringReader("1e2"));
         assertEquals(100.0, reader.nextDouble(), 0.0);

         reader = new JsonReader(new StringReader("-1.5"));
         assertEquals(-1.5, reader.nextDouble(), 0.0000001);
     }

     public void testReaderTopLevelUnquotedStringRequiresLenient() throws IOException {
         JsonReader strict = new JsonReader(new StringReader("hello"));
         try {
             strict.nextString();
             fail("Expected MalformedJsonException because unquoted strings require lenient mode");
         } catch (MalformedJsonException expected) {
             // expected
         }

         JsonReader lenient = new JsonReader(new StringReader("hello"));
         lenient.setLenient(true);
         assertEquals("hello", lenient.nextString());
     }

     public void testReaderMultipleTopLevelValuesStrictThrows() throws IOException {
         JsonReader reader = new JsonReader(new StringReader("\"a\"\"b\""));
         assertEquals("a", reader.nextString());
         try {
             reader.peek();
             fail("Expected MalformedJsonException for multiple top-level values in strict mode");
         } catch (MalformedJsonException expected) {
             // expected
         }
     }

     public void testWriterTopLevelValueTypes() throws IOException {
         StringWriter out = new StringWriter();
         new JsonWriter(out).value("a string").close();
         assertEquals("\"a string\"", out.toString());

         out = new StringWriter();
         new JsonWriter(out).value(true).close();
         assertEquals("true", out.toString());

         out = new StringWriter();
         new JsonWriter(out).value(123).close();
         assertEquals("123", out.toString());

         out = new StringWriter();
         new JsonWriter(out).value(456.7).close();
         assertEquals("456.7", out.toString());

         out = new StringWriter();
         new JsonWriter(out).nullValue().close();
         assertEquals("null", out.toString());
     }

     public void testWriterMultipleTopLevelValuesStrictThrows() throws IOException {
         StringWriter out = new StringWriter();
         JsonWriter writer = new JsonWriter(out);
         writer.value(1);
         try {
             writer.value(2);
             fail("Expected IllegalStateException for multiple top-level values");
         } catch (IllegalStateException expected) {
             assertEquals("JSON must have only one top-level value.", expected.getMessage());
         }
         writer.close();
         assertEquals("1", out.toString());
     }

     public void testWriterNestedArrayAndObject() throws IOException {
         StringWriter out = new StringWriter();
         JsonWriter writer = new JsonWriter(out);
         writer.beginArray();
         writer.value(1);
         writer.value(2);
         writer.endArray();
         writer.close();
         assertEquals("[1,2]", out.toString());

         out = new StringWriter();
         writer = new JsonWriter(out);
         writer.beginObject();
         writer.name("a").value(true);
         writer.endObject();
         writer.close();
         assertEquals("{\"a\":true}", out.toString());
     }

     public void testWriterTopLevelEmptyString() throws IOException {
         StringWriter out = new StringWriter();
         new JsonWriter(out).value("").close();
         assertEquals("\"\"", out.toString());
     }

     public void testWriterLenientAllowsMultipleTopLevelValues() throws IOException {
         StringWriter out = new StringWriter();
         JsonWriter writer = new JsonWriter(out);
         writer.setLenient(true);
         writer.value("a");
         writer.value("b");
         writer.close();
         assertEquals("\"a\"\"b\"", out.toString());
     }
 }