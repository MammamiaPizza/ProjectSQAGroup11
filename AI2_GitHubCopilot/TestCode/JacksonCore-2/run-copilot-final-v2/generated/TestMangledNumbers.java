package com.fasterxml.jackson.core.json;

 import static org.junit.Assert.*;

 import java.io.ByteArrayInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.Reader;
 import java.io.StringReader;

 import org.junit.Test;

 import com.fasterxml.jackson.core.Base64Variant;
 import com.fasterxml.jackson.core.Base64Variants;
 import com.fasterxml.jackson.core.JsonFactory;
 import com.fasterxml.jackson.core.JsonParseException;
 import com.fasterxml.jackson.core.JsonParser;
 import com.fasterxml.jackson.core.JsonToken;
 import com.fasterxml.jackson.core.ObjectCodec;
 import com.fasterxml.jackson.core.io.IOContext;
 import com.fasterxml.jackson.core.sym.BytesToNameCanonicalizer;
 import com.fasterxml.jackson.core.sym.CharsToNameCanonicalizer;
 import com.fasterxml.jackson.core.util.BufferRecycler;

 public class TestMangledNumbers {

     private static JsonFactory newFactory() {
         return new JsonFactory();
     }

     // --- helper to create ReaderBasedJsonParser ---
     private ReaderBasedJsonParser readerParser(String json) throws IOException {
         JsonFactory f = newFactory();
         IOContext ioCtxt = new IOContext(new BufferRecycler(), json, false);
         Reader r = new StringReader(json);
         CharsToNameCanonicalizer sym = CharsToNameCanonicalizer.createRoot(0);
         return new ReaderBasedJsonParser(ioCtxt, f.getParserFeatures(), r, null, sym);
     }

     // --- helper to create UTF8StreamJsonParser ---
     private UTF8StreamJsonParser utf8Parser(String json) throws IOException {
         JsonFactory f = newFactory();
         byte[] bytes = json.getBytes("UTF-8");
         IOContext ioCtxt = new IOContext(new BufferRecycler(), json, false);
         InputStream in = new ByteArrayInputStream(bytes);
         BytesToNameCanonicalizer sym = BytesToNameCanonicalizer.createRoot(0);
         byte[] buf = new byte[bytes.length];
         System.arraycopy(bytes, 0, buf, 0, bytes.length);
         return new UTF8StreamJsonParser(ioCtxt, f.getParserFeatures(), in, null, sym, buf, 0,
bytes.length, false);
     }

     // advance to first VALUE_NUMBER_INT token, expect exception
     private void assertMangledThrowsReader(String json) throws IOException {
         ReaderBasedJsonParser p = readerParser(json);
         p.nextToken(); // should throw
     }

     private void assertMangledThrowsUTF8(String json) throws IOException {
         UTF8StreamJsonParser p = utf8Parser(json);
         p.nextToken(); // should throw
     }

     // --------------- normal numbers must parse correctly ---------------
     @Test
     public void testNormalZero() throws IOException {
         ReaderBasedJsonParser pr = readerParser("0");
         assertEquals(JsonToken.VALUE_NUMBER_INT, pr.nextToken());
         assertEquals("0", pr.getText());

         UTF8StreamJsonParser pu = utf8Parser("0");
         assertEquals(JsonToken.VALUE_NUMBER_INT, pu.nextToken());
         assertEquals("0", pu.getText());
     }

     @Test
     public void testNegativeZero() throws IOException {
         ReaderBasedJsonParser pr = readerParser("-0");
         assertEquals(JsonToken.VALUE_NUMBER_INT, pr.nextToken());
         assertEquals("-0", pr.getText());

         UTF8StreamJsonParser pu = utf8Parser("-0");
         assertEquals(JsonToken.VALUE_NUMBER_INT, pu.nextToken());
         assertEquals("-0", pu.getText());
     }

     @Test
     public void testNormalInteger() throws IOException {
         ReaderBasedJsonParser pr = readerParser("123");
         assertEquals(JsonToken.VALUE_NUMBER_INT, pr.nextToken());
         assertEquals("123", pr.getText());

         UTF8StreamJsonParser pu = utf8Parser("123");
         assertEquals(JsonToken.VALUE_NUMBER_INT, pu.nextToken());
         assertEquals("123", pu.getText());
     }

     @Test
     public void testNormalFloatWithExponent() throws IOException {
         ReaderBasedJsonParser pr = readerParser("1.5e2");
         assertEquals(JsonToken.VALUE_NUMBER_FLOAT, pr.nextToken());
         assertTrue(pr.getText().startsWith("1.5"));

         UTF8StreamJsonParser pu = utf8Parser("1.5e2");
         assertEquals(JsonToken.VALUE_NUMBER_FLOAT, pu.nextToken());
         assertTrue(pu.getText().startsWith("1.5"));
     }

     @Test
     public void testNormalNegativeFloat() throws IOException {
         ReaderBasedJsonParser pr = readerParser("-12.5");
         assertEquals(JsonToken.VALUE_NUMBER_FLOAT, pr.nextToken());

         UTF8StreamJsonParser pu = utf8Parser("-12.5");
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
