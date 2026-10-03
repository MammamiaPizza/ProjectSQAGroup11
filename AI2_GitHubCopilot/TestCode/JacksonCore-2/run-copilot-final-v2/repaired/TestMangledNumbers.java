package com.fasterxml.jackson.core.json;

import static org.junit.Assert.*;

import java.io.*;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class TestMangledNumbers {

 private static JsonFactory newFactory() {
     return new JsonFactory();
 }

 private JsonParser readerParser(String json) throws IOException {
     return newFactory().createJsonParser(new StringReader(json));
 }

 private JsonParser utf8Parser(String json) throws IOException {
     return newFactory().createJsonParser(new ByteArrayInputStream(json.getBytes("UTF-8")));
 }

 private void assertMangledThrowsReader(String json) throws IOException {
     JsonParser p = readerParser(json);
     p.nextToken(); // should throw
     p.close();
 }

 private void assertMangledThrowsUTF8(String json) throws IOException {
     JsonParser p = utf8Parser(json);
     p.nextToken(); // should throw
     p.close();
 }

 // --------------- normal numbers must parse correctly ---------------
 @Test
 public void testNormalZero() throws IOException {
     JsonParser pr = readerParser("0");
     assertEquals(JsonToken.VALUE_NUMBER_INT, pr.nextToken());
     assertEquals("0", pr.getText());

     JsonParser pu = utf8Parser("0");
     assertEquals(JsonToken.VALUE_NUMBER_INT, pu.nextToken());
     assertEquals("0", pu.getText());
 }

 @Test
 public void testNegativeZero() throws IOException {
     JsonParser pr = readerParser("-0");
     assertEquals(JsonToken.VALUE_NUMBER_INT, pr.nextToken());
     assertEquals("-0", pr.getText());

     JsonParser pu = utf8Parser("-0");
     assertEquals(JsonToken.VALUE_NUMBER_INT, pu.nextToken());
     assertEquals("-0", pu.getText());
 }

 @Test
 public void testNormalInteger() throws IOException {
     JsonParser pr = readerParser("123");
     assertEquals(JsonToken.VALUE_NUMBER_INT, pr.nextToken());
     assertEquals("123", pr.getText());

     JsonParser pu = utf8Parser("123");
     assertEquals(JsonToken.VALUE_NUMBER_INT, pu.nextToken());
     assertEquals("123", pu.getText());
 }

 @Test
 public void testNormalFloatWithExponent() throws IOException {
     JsonParser pr = readerParser("1.5e2");
     assertEquals(JsonToken.VALUE_NUMBER_FLOAT, pr.nextToken());
     assertTrue(pr.getText().startsWith("1.5"));

     JsonParser pu = utf8Parser("1.5e2");
     assertEquals(JsonToken.VALUE_NUMBER_FLOAT, pu.nextToken());
     assertTrue(pu.getText().startsWith("1.5"));
 }

 @Test
 public void testNormalNegativeFloat() throws IOException {
     JsonParser pr = readerParser("-12.5");
     assertEquals(JsonToken.VALUE_NUMBER_FLOAT, pr.nextToken());

     JsonParser pu = utf8Parser("-12.5");
     assertEquals(JsonToken.VALUE_NUMBER_FLOAT, pu.nextToken());
 }

 // --------------- mangled numbers (leading zero) ---------------
 @Test
 public void testMangledLeadingZeroReader() throws IOException {
     try {
         assertMangledThrowsReader("01");
         fail("Should have thrown JsonParseException for '01'");
     } catch (JsonParseException ex) {
         // expected
     }
 }

 @Test
 public void testMangledLeadingZeroUTF8() throws IOException {
     try {
         assertMangledThrowsUTF8("01");
         fail("Should have thrown JsonParseException for '01'");
     } catch (JsonParseException ex) {
         // expected
     }
 }

 @Test
 public void testMangledDoubleZeroReader() throws IOException {
     try {
         assertMangledThrowsReader("00");
         fail("Should have thrown JsonParseException for '00'");
     } catch (JsonParseException ex) {
         // expected
     }
 }

 @Test
 public void testMangledDoubleZeroUTF8() throws IOException {
     try {
         assertMangledThrowsUTF8("00");
         fail("Should have thrown JsonParseException for '00'");
     } catch (JsonParseException ex) {
         // expected
     }
 }

 // --------------- mangled numbers (leading plus) ---------------
 @Test
 public void testMangledLeadingPlusReader() throws IOException {
     try {
         assertMangledThrowsReader("+1");
         fail("Should have thrown JsonParseException for '+1'");
     } catch (JsonParseException ex) {
         // expected
     }
 }

 @Test
 public void testMangledLeadingPlusUTF8() throws IOException {
     try {
         assertMangledThrowsUTF8("+1");
         fail("Should have thrown JsonParseException for '+1'");
     } catch (JsonParseException ex) {
         // expected
     }
 }

 // --------------- mangled numbers (trailing dot) ---------------
 @Test
 public void testMangledTrailingDotReader() throws IOException {
     try {
         assertMangledThrowsReader("1.");
         fail("Should have thrown JsonParseException for '1.'");
     } catch (JsonParseException ex) {
         // expected
     }
 }

 @Test
 public void testMangledTrailingDotUTF8() throws IOException {
     try {
         assertMangledThrowsUTF8("1.");
         fail("Should have thrown JsonParseException for '1.'");
     } catch (JsonParseException ex) {
         // expected
     }
 }

 // --------------- mangled numbers (bad exponent) ---------------
 @Test
 public void testMangledExponentNoDigitsReader() throws IOException {
     try {
         assertMangledThrowsReader("1e");
         fail("Should have thrown JsonParseException for '1e'");
     } catch (JsonParseException ex) {
         // expected
     }
 }

 @Test
 public void testMangledExponentNoDigitsUTF8() throws IOException {
     try {
         assertMangledThrowsUTF8("1e");
         fail("Should have thrown JsonParseException for '1e'");
     } catch (JsonParseException ex) {
         // expected
     }
 }

 @Test
 public void testMangledExponentPlusNoDigitsReader() throws IOException {
     try {
         assertMangledThrowsReader("1e+");
         fail("Should have thrown JsonParseException for '1e+'");
     } catch (JsonParseException ex) {
         // expected
     }
 }

 @Test
 public void testMangledExponentPlusNoDigitsUTF8() throws IOException {
     try {
         assertMangledThrowsUTF8("1e+");
         fail("Should have thrown JsonParseException for '1e+'");
     } catch (JsonParseException ex) {
         // expected
     }
 }

 @Test
 public void testMangledExponentMinusNoDigitsReader() throws IOException {
     try {
         assertMangledThrowsReader("1e-");
         fail("Should have thrown JsonParseException for '1e-'");
     } catch (JsonParseException ex) {
         // expected
     }
 }

 @Test
 public void testMangledExponentMinusNoDigitsUTF8() throws IOException {
     try {
         assertMangledThrowsUTF8("1e-");
         fail("Should have thrown JsonParseException for '1e-'");
     } catch (JsonParseException ex) {
         // expected
     }
 }

 // --------------- mangled numbers (multiple dots) ---------------
 @Test
 public void testMangledMultipleDotsReader() throws IOException {
     try {
         assertMangledThrowsReader("1.2.3");
         fail("Should have thrown JsonParseException for '1.2.3'");
     } catch (JsonParseException ex) {
         // expected
     }
 }

 @Test
 public void testMangledMultipleDotsUTF8() throws IOException {
     try {
         assertMangledThrowsUTF8("1.2.3");
         fail("Should have thrown JsonParseException for '1.2.3'");
     } catch (JsonParseException ex) {
         // expected
     }
 }

 // --------------- mangled numbers (dash-dot) ---------------
 @Test
 public void testMangledDashDotReader() throws IOException {
     try {
         assertMangledThrowsReader("-.5");
         fail("Should have thrown JsonParseException for '-.5'");
     } catch (JsonParseException ex) {
         // expected
     }
 }

 @Test
 public void testMangledDashDotUTF8() throws IOException {
     try {
         assertMangledThrowsUTF8("-.5");
         fail("Should have thrown JsonParseException for '-.5'");
     } catch (JsonParseException ex) {
         // expected
     }
 }

}
