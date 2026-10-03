package com.google.gson.stream;

 import junit.framework.TestCase;
 import java.io.StringReader;

 public class JsonReaderBug7Test extends TestCase {

     private JsonReader newReader(String json) {
         JsonReader reader = new JsonReader(new StringReader(json));
         reader.setLenient(true);
         return reader;
     }

     public void testUnquotedIntegerKeyAsName() throws Exception {
         try {
             JsonReader reader = newReader("{42:1}");
             reader.beginObject();
             assertTrue(reader.hasNext());
             assertEquals("42", reader.nextName());
             assertEquals(1, reader.nextInt());
             reader.endObject();
             assertFalse(reader.hasNext());
             fail("Expected IllegalStateException due to bug");
         } catch (IllegalStateException e) {
             assertTrue(e.getMessage().contains("Expected an int but was STRING"));
         }
     }

     public void testUnquotedLongKeyAsName() throws Exception {
         JsonReader reader = newReader("{123L:2}");
         reader.beginObject();
         assertTrue(reader.hasNext());
         assertEquals("123L", reader.nextName());
         assertEquals(2, reader.nextInt());
         reader.endObject();
     }

     public void testUnquotedStringPrefixedWithInteger() throws Exception {
         try {
             JsonReader reader = newReader("[123abc]");
             reader.beginArray();
             assertTrue(reader.hasNext());
             assertEquals(JsonToken.STRING, reader.peek());
             assertEquals("123abc", reader.nextString());
             reader.endArray();
             assertFalse(reader.hasNext());
             fail("Expected IllegalStateException due to bug");
         } catch (IllegalStateException e) {
             assertTrue(e.getMessage().contains("Expected an int but was STRING"));
         }
     }

     public void testNextIntUnquotedNumber() throws Exception {
         assertEquals(42, newReader("42").nextInt());
     }

     public void testNextLongUnquotedNumber() throws Exception {
         assertEquals(42L, newReader("42").nextLong());
     }

     public void testNextIntPrecisionLoss() throws Exception {
         JsonReader reader = newReader("2147483648");
         try {
             reader.nextInt();
             fail("Expected NumberFormatException");
         } catch (NumberFormatException e) {
             assertTrue(e.getMessage().contains("Expected an int but was 2147483648"));
         }
     }

     public void testNextLongPrecisionLoss() throws Exception {
         JsonReader reader = newReader("9223372036854775808");
         try {
             reader.nextLong();
             fail("Expected IllegalStateException");
         } catch (IllegalStateException e) {
             assertTrue(e.getMessage().contains("Expected a long but was STRING"));
         }
     }

     public void testNextIntInvalidString() throws Exception {
         JsonReader reader = newReader("hello");
         try {
             reader.nextInt();
             fail("Expected exception");
         } catch (RuntimeException e) {
             assertTrue(e.toString().contains("Expected an int but was STRING"));
         }
     }

     public void testNextLongInvalidString() throws Exception {
         JsonReader reader = newReader("hello");
         try {
             reader.nextLong();
             fail("Expected exception");
         } catch (RuntimeException e) {
             assertTrue(e.toString().contains("Expected a long but was STRING"));
         }
     }

     public void testBoundaryLongMaxValue() throws Exception {
         assertEquals(Long.MAX_VALUE, newReader("9223372036854775807").nextLong());
     }

     public void testBoundaryLongMinValue() throws Exception {
         assertEquals(Long.MIN_VALUE, newReader("-9223372036854775808").nextLong());
     }

     public void testQuotedNumberAsLong() throws Exception {
         assertEquals(123L, newReader("\"123\"").nextLong());
     }
 }