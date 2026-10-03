package com.google.gson.stream;

 import static org.junit.Assert.*;

 import java.io.IOException;
 import java.io.StringWriter;

 import org.junit.Test;

 /**
  * Tests for {@link JsonWriter} focusing on non-finite numeric values
  * (NaN, +Infinity, -Infinity) with and without lenient mode.
  */
 public class JsonWriterTest {

     // -----------------------------------------------------------------
     // Normal finite values
     // -----------------------------------------------------------------

     @Test
     public void testNormalDoubleValues() throws IOException {
         StringWriter sw = new StringWriter();
         JsonWriter writer = new JsonWriter(sw);
         writer.setLenient(false);

         writer.value(0.0);
         writer.value(1.0);
         writer.value(-1.0);
         writer.value(Double.MAX_VALUE);
         writer.value(Double.MIN_VALUE);
         writer.value(-0.0);
         writer.close();

         String json = sw.toString();
         assertTrue("0.0 missing", json.contains("0.0"));
         assertTrue("1.0 missing", json.contains("1.0"));
         assertTrue("-1.0 missing", json.contains("-1.0"));
         assertTrue("MAX_VALUE missing", json.contains(String.valueOf(Double.MAX_VALUE)));
         assertTrue("MIN_VALUE missing", json.contains(String.valueOf(Double.MIN_VALUE)));
         assertTrue("-0.0 missing", json.contains("-0.0"));
     }

     // -----------------------------------------------------------------
     // Non-finite values in strict mode – must throw
     // -----------------------------------------------------------------

     @Test(expected = IllegalArgumentException.class)
     public void testValueDoubleNaNNonLenientThrows() throws IOException {
         JsonWriter writer = new JsonWriter(new StringWriter());
         writer.setLenient(false);
         try {
             writer.value(Double.NaN);
         } finally {
             writer.close();
         }
     }

     @Test(expected = IllegalArgumentException.class)
     public void testValueDoublePositiveInfinityNonLenientThrows() throws IOException {
         JsonWriter writer = new JsonWriter(new StringWriter());
         writer.setLenient(false);
         try {
             writer.value(Double.POSITIVE_INFINITY);
         } finally {
             writer.close();
         }
     }

     @Test(expected = IllegalArgumentException.class)
     public void testValueDoubleNegativeInfinityNonLenientThrows() throws IOException {
         JsonWriter writer = new JsonWriter(new StringWriter());
         writer.setLenient(false);
         try {
             writer.value(Double.NEGATIVE_INFINITY);
         } finally {
             writer.close();
         }
     }

     @Test(expected = IllegalArgumentException.class)
     public void testValueNumberNaNNonLenientThrows() throws IOException {
         JsonWriter writer = new JsonWriter(new StringWriter());
         writer.setLenient(false);
         try {
             writer.value(Double.valueOf(Double.NaN));
         } finally {
             writer.close();
         }
     }

     @Test(expected = IllegalArgumentException.class)
     public void testValueNumberInfinityNonLenientThrows() throws IOException {
         JsonWriter writer = new JsonWriter(new StringWriter());
         writer.setLenient(false);
         try {
             writer.value(Double.valueOf(Double.POSITIVE_INFINITY));
         } finally {
             writer.close();
         }
     }

     // -----------------------------------------------------------------
     // Non-finite values in lenient mode – must NOT throw
     // -----------------------------------------------------------------

     @Test
     public void testNonFiniteDoublesWhenLenient() throws IOException {
         StringWriter sw = new StringWriter();
         JsonWriter writer = new JsonWriter(sw);
         writer.setLenient(true);

         writer.value(Double.NaN);
         writer.value(Double.POSITIVE_INFINITY);
         writer.value(Double.NEGATIVE_INFINITY);
         writer.close();

         String json = sw.toString();
         assertFalse("JSON output should not be empty", json.isEmpty());
         // At least one of the expected tokens should appear
         assertTrue(json.contains("NaN") || json.contains("Infinity") ||
json.contains("-Infinity"));
     }

     @Test
     public void testValueNumberNaNWhenLenient() throws IOException {
         StringWriter sw = new StringWriter();
         JsonWriter writer = new JsonWriter(sw);
         writer.setLenient(true);

         writer.value(Double.valueOf(Double.NaN));
         writer.close();

         String json = sw.toString();
         assertTrue("NaN expected in output", json.contains("NaN"));
     }

     @Test
     public void testValueNumberPositiveInfinityWhenLenient() throws IOException {
         StringWriter sw = new StringWriter();
         JsonWriter writer = new JsonWriter(sw);
         writer.setLenient(true);

         writer.value(Double.valueOf(Double.POSITIVE_INFINITY));
         writer.close();

         String json = sw.toString();
         assertTrue("Infinity expected in output", json.contains("Infinity"));
     }

     @Test
     public void testValueNumberNegativeInfinityWhenLenient() throws IOException {
         StringWriter sw = new StringWriter();
         JsonWriter writer = new JsonWriter(sw);
         writer.setLenient(true);

         writer.value(Double.valueOf(Double.NEGATIVE_INFINITY));
         writer.close();

         String json = sw.toString();
         assertTrue("-Infinity expected in output", json.contains("-Infinity"));
     }

     // -----------------------------------------------------------------
     // Mixed sequences with lenient toggle
     // -----------------------------------------------------------------

     @Test
     public void testMixedFiniteAndNonFiniteLenient() throws IOException {
         StringWriter sw = new StringWriter();
         JsonWriter writer = new JsonWriter(sw);
         writer.setLenient(true);

         writer.value(42.5);
         writer.value(Double.NaN);
         writer.value(-3.14);
         writer.close();

         String json = sw.toString();
         assertTrue("42.5 missing", json.contains("42.5"));
         assertTrue("NaN missing", json.contains("NaN"));
         assertTrue("-3.14 missing", json.contains("-3.14"));
     }

     @Test
     public void testLenientToggleWithinDocument() throws IOException {
         StringWriter sw = new StringWriter();
         JsonWriter writer = new JsonWriter(sw);

         writer.setLenient(true);
         writer.value(1.0);
         writer.value(Double.NaN);
         writer.setLenient(false);
         // after writing NaN in lenient mode, writer state should be intact;
         // writing further finite value must succeed because NaN already accepted.
         writer.value(2.0);
         writer.close();

         String json = sw.toString();
         assertTrue("First value missing", json.contains("1.0"));
         assertTrue("NaN missing", json.contains("NaN"));
         assertTrue("Last value missing", json.contains("2.0"));
     }

     // -----------------------------------------------------------------
     // Object and array with non-finite values
     // -----------------------------------------------------------------

     @Test
     public void testObjectWithNonFiniteValue() throws IOException {
         StringWriter sw = new StringWriter();
         JsonWriter writer = new JsonWriter(sw);
         writer.setLenient(true);

         writer.beginObject();
         writer.name("key");
         writer.value(Double.NaN);
         writer.endObject();
         writer.close();

         String json = sw.toString();
         assertTrue("NaN expected inside object", json.contains("\"key\"") && json.contains("NaN"));
     }

     // -----------------------------------------------------------------
     // Flush/close after non-finite write (no NPE)
     // -----------------------------------------------------------------

     @Test
     public void testFlushAfterNonFiniteWrite() throws IOException {
         StringWriter sw = new StringWriter();
         JsonWriter writer = new JsonWriter(sw);
         writer.setLenient(true);

         writer.value(Double.NaN);
         writer.flush();
         writer.close();

         // If we reach here without exception, flush/close are safe
         assertFalse("Flush must produce output", sw.toString().isEmpty());
     }
 }