package com.fasterxml.jackson.core.json;

 import static org.junit.Assert.*;
 import java.io.*;
 import org.junit.*;

 import com.fasterxml.jackson.core.*;
 import com.fasterxml.jackson.core.JsonFactory;
 import com.fasterxml.jackson.core.JsonGenerator;
 import com.fasterxml.jackson.core.JsonGenerationException;

 /**
  * Tests for surrogate handling in UTF8JsonGenerator.writeRaw methods.
  * Targets the bug where a segment boundary splits a surrogate pair
  * and causes a false JsonGenerationException.
  */
 public class UTF8JsonGeneratorWriteRawSurrogateTest {
     private static JsonFactory newFactory() {
         return new JsonFactory();
     }
     private JsonGenerator createGenerator(OutputStream out) throws IOException {
         return newFactory().createGenerator(out);
     }
     // ----------------------------------------------------------------
     // Valid surrogate pair, single segment (no internal splitting)
     // ----------------------------------------------------------------
     @Test
     public void testWriteRawStringWithValidSurrogatePair() throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         try (JsonGenerator gen = createGenerator(baos)) {
             gen.writeRaw("abc\uD800\uDC00xyz");
             gen.flush();
         }
         byte[] output = baos.toByteArray();
         byte[] head = "abc".getBytes("UTF-8");
         byte[] pair = new byte[] { (byte)0xF0, (byte)0x90, (byte)0x80, (byte)0x80 };
         byte[] tail = "xyz".getBytes("UTF-8");
         byte[] expected = concat(head, pair, tail);
         assertArrayEquals(expected, output);
     }
     // ----------------------------------------------------------------
     // Valid surrogate pair spanning the internal char-buffer boundary
     // (high surrogate at last position of the first segment)
     // ----------------------------------------------------------------
     @Test
     public void testWriteRawStringSegmentBoundarySurrogate() throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         JsonGenerator baseGen = createGenerator(baos);
         UTF8JsonGenerator gen = (UTF8JsonGenerator) baseGen;
         int bufLen = gen._charBuffer.length;
         // Build string that forces segmentation:
         //   first segment: bufLen-1 'a', then HIGH surrogate at index bufLen-1
         //   second segment starts with LOW surrogate, then 50 'b'
         StringBuilder sb = new StringBuilder(bufLen + 60);
         for (int i = 0; i < bufLen - 1; i++) {
             sb.append('a');
         }
         sb.append('\uD800'); // high surrogate, last char of first segment
         sb.append('\uDC00'); // low surrogate, first char of next segment
         for (int i = 0; i < 50; i++) {
             sb.append('b');
         }
         try {
             gen.writeRaw(sb.toString());
             gen.flush();
         } finally {
             gen.close();
         }
         byte[] output = baos.toByteArray();
         // Verify the UTF-8 encoding of the supplementary character at the correct position.
         int pairOffset = bufLen - 1; // number of 'a' bytes before the pair
         assertEquals("Byte before pair should be 'a'", (byte)'a', output[pairOffset - 1]);
         assertEquals("First byte of pair", (byte)0xF0, output[pairOffset]);
         assertEquals("Second byte of pair", (byte)0x90, output[pairOffset + 1]);
         assertEquals("Third byte of pair", (byte)0x80, output[pairOffset + 2]);
         assertEquals("Fourth byte of pair", (byte)0x80, output[pairOffset + 3]);
         assertEquals("Byte after pair should be 'b'", (byte)'b', output[pairOffset + 4]);
         int expectedLength = (bufLen - 1) + 4 + 50;
         assertEquals("Total output length", expectedLength, output.length);
     }
     // ----------------------------------------------------------------
     // Lone high surrogate at the very end of the whole input -> must throw
     // ----------------------------------------------------------------
     @Test(expected = JsonGenerationException.class)
     public void testWriteRawStringLoneHighSurrogateEnd() throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         try (JsonGenerator gen = createGenerator(baos)) {
             gen.writeRaw("test\uD800");
             gen.flush();
         }
     }
     // ----------------------------------------------------------------
     // Lone low surrogate at the very end of the whole input -> must throw
     // ----------------------------------------------------------------
     @Test(expected = JsonGenerationException.class)
     public void testWriteRawStringLoneLowSurrogateEnd() throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         try (JsonGenerator gen = createGenerator(baos)) {
             gen.writeRaw("test\uDC00");
             gen.flush();
         }
     }
     // ----------------------------------------------------------------
     // Lone high surrogate at the end of a writeRaw(String,offset,len) chunk
     // ----------------------------------------------------------------
     @Test(expected = JsonGenerationException.class)
     public void testWriteRawStringOffsetLoneHighSurrogateEnd() throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         try (JsonGenerator gen = createGenerator(baos)) {
             // text has high surrogate followed by 'b'; we only write the first 2 chars.
             gen.writeRaw("a\uD800b", 0, 2);
             gen.flush();
         }
     }
     // ----------------------------------------------------------------
     // Lone low surrogate at the start of a writeRaw(String,offset,len) chunk
     // ----------------------------------------------------------------
     @Test(expected = JsonGenerationException.class)
     public void testWriteRawStringOffsetLoneLowSurrogateStart() throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         try (JsonGenerator gen = createGenerator(baos)) {
             gen.writeRaw("a\uDC00b", 1, 2); // starts at low surrogate, includes following 'b'
             gen.flush();
         }
     }
     // ----------------------------------------------------------------
     // Empty raw string
     // ----------------------------------------------------------------
     @Test
     public void testWriteRawEmptyString() throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         try (JsonGenerator gen = createGenerator(baos)) {
             gen.writeRaw("");
             gen.flush();
         }
         assertEquals(0, baos.size());
     }
     // ----------------------------------------------------------------
     // writeRaw(char[], ...) with valid surrogate pair
     // ----------------------------------------------------------------
     @Test
     public void testWriteRawCharArrayWithValidSurrogatePair() throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         try (JsonGenerator gen = createGenerator(baos)) {
             char[] input = new char[] { 'a', '\uD800', '\uDC00', 'b' };
             gen.writeRaw(input, 0, 4);
             gen.flush();
         }
         byte[] output = baos.toByteArray();
         byte[] expected = new byte[] {
             'a', (byte)0xF0, (byte)0x90, (byte)0x80, (byte)0x80, 'b'
         };
         assertArrayEquals(expected, output);
     }
     // ----------------------------------------------------------------
     // writeRaw(char[], ...) with lone high surrogate at the end
     // ----------------------------------------------------------------
     @Test(expected = JsonGenerationException.class)
     public void testWriteRawCharArrayLoneHighSurrogateEnd() throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         try (JsonGenerator gen = createGenerator(baos)) {
             char[] input = new char[] { 'a', '\uD800' };
             gen.writeRaw(input, 0, 2);
             gen.flush();
         }
     }
     // ----------------------------------------------------------------
     // writeRaw(char[], ...) with lone low surrogate at the start
     // ----------------------------------------------------------------
     @Test(expected = JsonGenerationException.class)
     public void testWriteRawCharArrayLoneLowSurrogateStart() throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         try (JsonGenerator gen = createGenerator(baos)) {
             char[] input = new char[] { '\uDC00', 'a' };
             gen.writeRaw(input, 0, 2);
             gen.flush();
         }
     }
     // ----------------------------------------------------------------
     // writeRaw(char) with high surrogate -> must throw
     // ----------------------------------------------------------------
     @Test(expected = JsonGenerationException.class)
     public void testWriteRawCharHighSurrogate() throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         try (JsonGenerator gen = createGenerator(baos)) {
             gen.writeRaw('\uD800');
             gen.flush();
         }
     }
     // ----------------------------------------------------------------
     // writeRaw(char) with low surrogate -> must throw
     // ----------------------------------------------------------------
     @Test(expected = JsonGenerationException.class)
     public void testWriteRawCharLowSurrogate() throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         try (JsonGenerator gen = createGenerator(baos)) {
             gen.writeRaw('\uDC00');
             gen.flush();
         }
     }
     // ----------------------------------------------------------------
     // Helper utilities
     // ----------------------------------------------------------------
     private static byte[] concat(byte[] first, byte[] second, byte[] third) {
         byte[] result = new byte[first.length + second.length + third.length];
         System.arraycopy(first, 0, result, 0, first.length);
         System.arraycopy(second, 0, result, first.length, second.length);
         System.arraycopy(third, 0, result, first.length + second.length, third.length);
         return result;
     }
     private static byte[] concat(byte[] first, byte[] second) {
         byte[] result = new byte[first.length + second.length];
         System.arraycopy(first, 0, result, 0, first.length);
         System.arraycopy(second, 0, result, first.length, second.length);
         return result;
     }
 }