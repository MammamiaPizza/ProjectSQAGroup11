package com.google.gson.stream;

import static org.junit.Assert.*;
import java.io.IOException;
import java.io.StringWriter;

import org.junit.Test;

public class JsonWriterTest {

 @Test
 public void testNormalDoubleValues() throws IOException {
     StringWriter sw = new StringWriter();
     JsonWriter writer = new JsonWriter(sw);
     writer.setLenient(false);

     writer.beginArray();
     writer.value(0.0);
     writer.value(1.0);
     writer.value(-1.0);
     writer.value(Double.MAX_VALUE);
     writer.value(Double.MIN_VALUE);
     writer.value(-0.0);
     writer.endArray();
     writer.close();

     String json = sw.toString();
     assertTrue("0.0 missing", json.contains("0.0"));
     assertTrue("1.0 missing", json.contains("1.0"));
     assertTrue("-1.0 missing", json.contains("-1.0"));
     assertTrue("MAX_VALUE missing", json.contains(String.valueOf(Double.MAX_VALUE)));
     assertTrue("MIN_VALUE missing", json.contains(String.valueOf(Double.MIN_VALUE)));
     assertTrue("-0.0 missing", json.contains("-0.0"));
 }

 @Test(expected = IllegalArgumentException.class)
 public void testValueDoubleNaNNonLenientThrows() throws IOException {
     JsonWriter writer = new JsonWriter(new StringWriter());
     writer.setLenient(false);
     writer.beginArray();
     writer.value(Double.NaN);
 }

 @Test(expected = IllegalArgumentException.class)
 public void testValueDoublePositiveInfinityNonLenientThrows() throws IOException {
     JsonWriter writer = new JsonWriter(new StringWriter());
     writer.setLenient(false);
     writer.beginArray();
     writer.value(Double.POSITIVE_INFINITY);
 }

 @Test(expected = IllegalArgumentException.class)
 public void testValueDoubleNegativeInfinityNonLenientThrows() throws IOException {
     JsonWriter writer = new JsonWriter(new StringWriter());
     writer.setLenient(false);
     writer.beginArray();
     writer.value(Double.NEGATIVE_INFINITY);
 }

 @Test(expected = IllegalArgumentException.class)
 public void testValueNumberNaNNonLenientThrows() throws IOException {
     JsonWriter writer = new JsonWriter(new StringWriter());
     writer.setLenient(false);
     writer.beginArray();
     writer.value(Double.valueOf(Double.NaN));
 }

 @Test(expected = IllegalArgumentException.class)
 public void testValueNumberInfinityNonLenientThrows() throws IOException {
     JsonWriter writer = new JsonWriter(new StringWriter());
     writer.setLenient(false);
     writer.beginArray();
     writer.value(Double.valueOf(Double.POSITIVE_INFINITY));
 }

 @Test
 public void testNonFiniteDoublesWhenLenient() throws IOException {
     StringWriter sw = new StringWriter();
     JsonWriter writer = new JsonWriter(sw);
     writer.setLenient(true);

     writer.beginArray();
     writer.value(Double.NaN);
     writer.value(Double.POSITIVE_INFINITY);
     writer.value(Double.NEGATIVE_INFINITY);
     writer.endArray();
     writer.close();

     String json = sw.toString();
     assertFalse("JSON output should not be empty", json.isEmpty());
     assertTrue(json.contains("NaN") || json.contains("Infinity") || json.contains("-Infinity"));
 }

 @Test
 public void testValueNumberNaNWhenLenient() throws IOException {
     StringWriter sw = new StringWriter();
     JsonWriter writer = new JsonWriter(sw);
     writer.setLenient(true);

     writer.beginArray();
     writer.value(Double.valueOf(Double.NaN));
     writer.endArray();
     writer.close();

     String json = sw.toString();
     assertTrue("NaN expected in output", json.contains("NaN"));
 }

 @Test
 public void testValueNumberPositiveInfinityWhenLenient() throws IOException {
     StringWriter sw = new StringWriter();
     JsonWriter writer = new JsonWriter(sw);
     writer.setLenient(true);

     writer.beginArray();
     writer.value(Double.valueOf(Double.POSITIVE_INFINITY));
     writer.endArray();
     writer.close();

     String json = sw.toString();
     assertTrue("Infinity expected in output", json.contains("Infinity"));
 }

 @Test
 public void testValueNumberNegativeInfinityWhenLenient() throws IOException {
     StringWriter sw = new StringWriter();
     JsonWriter writer = new JsonWriter(sw);
     writer.setLenient(true);

     writer.beginArray();
     writer.value(Double.valueOf(Double.NEGATIVE_INFINITY));
     writer.endArray();
     writer.close();

     String json = sw.toString();
     assertTrue("-Infinity expected in output", json.contains("-Infinity"));
 }

 @Test
 public void testMixedFiniteAndNonFiniteLenient() throws IOException {
     StringWriter sw = new StringWriter();
     JsonWriter writer = new JsonWriter(sw);
     writer.setLenient(true);

     writer.beginArray();
     writer.value(42.5);
     writer.value(Double.NaN);
     writer.value(-3.14);
     writer.endArray();
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
     writer.beginArray();
     writer.value(1.0);
     writer.value(Double.NaN);
     writer.setLenient(false);
     writer.value(2.0);
     writer.endArray();
     writer.close();

     String json = sw.toString();
     assertTrue("First value missing", json.contains("1.0"));
     assertTrue("NaN missing", json.contains("NaN"));
     assertTrue("Last value missing", json.contains("2.0"));
 }

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

 @Test
 public void testFlushAfterNonFiniteWrite() throws IOException {
     StringWriter sw = new StringWriter();
     JsonWriter writer = new JsonWriter(sw);
     writer.setLenient(true);

     writer.beginArray();
     writer.value(Double.NaN);
     writer.flush();
     writer.endArray();
     writer.close();

     assertFalse("Flush must produce output", sw.toString().isEmpty());
 }

}