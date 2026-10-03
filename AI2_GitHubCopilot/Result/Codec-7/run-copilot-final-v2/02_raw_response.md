package org.apache.commons.codec.binary;

 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for Base64 encoding, focusing on the bug CODEC-99 where
  * non-chunked static encode methods incorrectly append a trailing
  * line separator (CRLF or LF) to the output.
  */
 public class Base64Codec99Test {

     // Known-good test vectors
     private static final byte[] HELLO_WORLD = "Hello World".getBytes();
     private static final String HELLO_WORLD_B64 = "SGVsbG8gV29ybGQ=";

     private static final byte[] SINGLE_BYTE_F = "f".getBytes();
     private static final String SINGLE_BYTE_F_B64 = "Zg==";

     // RFC 4648 Section 10 test vectors
     private static final byte[] EMPTY = new byte[0];
     private static final String EMPTY_B64 = "";

     private static final byte[] RFC_4648_10_F = "f".getBytes();
     private static final String RFC_4648_10_F_B64 = "Zg==";

     private static final byte[] RFC_4648_10_FO = "fo".getBytes();
     private static final String RFC_4648_10_FO_B64 = "Zm8=";

     private static final byte[] RFC_4648_10_FOO = "foo".getBytes();
     private static final String RFC_4648_10_FOO_B64 = "Zm9v";

     private static final byte[] RFC_4648_10_FOOB = "foob".getBytes();
     private static final String RFC_4648_10_FOOB_B64 = "Zm9vYg==";

     private static final byte[] RFC_4648_10_FOOBA = "fooba".getBytes();
     private static final String RFC_4648_10_FOOBA_B64 = "Zm9vYmE=";

     private static final byte[] RFC_4648_10_FOOBAR = "foobar".getBytes();
     private static final String RFC_4648_10_FOOBAR_B64 = "Zm9vYmFy";

     // ================================================================
     // Non-chunked static methods must NOT have trailing line separator
     // ================================================================

     @Test
     public void testEncodeBase64HelloWorldNoTrailingSeparator() {
         // The bug: encodeBase64() was adding a trailing line separator
         byte[] encoded = Base64.encodeBase64(HELLO_WORLD);
         String result = new String(encoded);
         assertEquals("encodeBase64 should not have trailing line separator",
                 HELLO_WORLD_B64, result);
     }

     @Test
     public void testEncodeBase64SingleByteNoTrailingSeparator() {
         byte[] encoded = Base64.encodeBase64(SINGLE_BYTE_F);
         String result = new String(encoded);
         assertEquals("encodeBase64 single byte should not have trailing line separator",
                 SINGLE_BYTE_F_B64, result);
     }

     @Test
     public void testEncodeBase64StringNoTrailingSeparator() {
         // encodeBase64String incorrectly called encodeBase64(binaryData, true) making it chunked
         String result = Base64.encodeBase64String(HELLO_WORLD);
         assertEquals("encodeBase64String should return clean Base64 without trailing separator",
                 HELLO_WORLD_B64, result);
     }

     @Test
     public void testEncodeBase64URLSafeNoTrailingSeparator() {
         byte[] encoded = Base64.encodeBase64URLSafe(HELLO_WORLD);
         // URL-safe encoding of "Hello World" should not have '+' or '/'
         String result = new String(encoded);
         assertFalse("URL-safe output should not contain '+'", result.contains("+"));
         assertFalse("URL-safe output should not contain '/'", result.contains("/"));
         assertFalse("URL-safe output should not end with line separator",
                 result.endsWith("\r\n") || result.endsWith("\n"));
     }

     @Test
     public void testEncodeBase64URLSafeStringNoTrailingSeparator() {
         String result = Base64.encodeBase64URLSafeString(SINGLE_BYTE_F);
         // "f" in URL-safe Base64 is "Zg==" (same as standard for this input since no + or /)
         assertFalse("URL-safe string output should not end with line separator",
                 result.endsWith("\r\n") || result.endsWith("\n"));
         // Roundtrip: decode should give back original
         byte[] decoded = Base64.decodeBase64(result);
         assertArrayEquals("URL-safe encode/decode roundtrip should preserve data",
                 SINGLE_BYTE_F, decoded);
     }

     // ================================================================
     // RFC 4648 Section 10 vectors (non-chunked)
     // ================================================================

     @Test
     public void testRfc4648Section10EncodeEmpty() {
         byte[] encoded = Base64.encodeBase64(EMPTY);
         assertEquals("Empty input should produce empty output", EMPTY_B64, new String(encoded));
     }

     @Test
     public void testRfc4648Section10EncodeF() {
         byte[] encoded = Base64.encodeBase64(RFC_4648_10_F);
         assertEquals("RFC 4648 §10 'f'", RFC_4648_10_F_B64, new String(encoded));
     }

     @Test
     public void testRfc4648Section10EncodeFo() {
         byte[] encoded = Base64.encodeBase64(RFC_4648_10_FO);
         assertEquals("RFC 4648 §10 'fo'", RFC_4648_10_FO_B64, new String(encoded));
     }

     @Test
     public void testRfc4648Section10EncodeFoo() {
         byte[] encoded = Base64.encodeBase64(RFC_4648_10_FOO);
         assertEquals("RFC 4648 §10 'foo'", RFC_4648_10_FOO_B64, new String(encoded));
     }

     @Test
     public void testRfc4648Section10EncodeFoob() {
         byte[] encoded = Base64.encodeBase64(RFC_4648_10_FOOB);
         assertEquals("RFC 4648 §10 'foob'", RFC_4648_10_FOOB_B64, new String(encoded));
     }

     @Test
     public void testRfc4648Section10EncodeFooba() {
         byte[] encoded = Base64.encodeBase64(RFC_4648_10_FOOBA);
         assertEquals("RFC 4648 §10 'fooba'", RFC_4648_10_FOOBA_B64, new String(encoded));
     }

     @Test
     public void testRfc4648Section10EncodeFoobar() {
         byte[] encoded = Base64.encodeBase64(RFC_4648_10_FOOBAR);
         assertEquals("RFC 4648 §10 'foobar'", RFC_4648_10_FOOBAR_B64, new String(encoded));
     }

     // ================================================================
     // Chunked encoding must have correct line separator
     // ================================================================

     @Test
     public void testChunkedEncodeHasCorrectSeparator() {
         // Build input that spans multiple chunks (MIME_CHUNK_SIZE = 76)
         byte[] largeInput = new byte[200];
         for (int i = 0; i < largeInput.length; i++) {
             largeInput[i] = (byte) (i % 256);
         }
         byte[] chunked = Base64.encodeBase64Chunked(largeInput);
         String chunkedStr = new String(chunked);
         // Chunked output should contain CRLF line separators
         assertTrue("Chunked output should contain CRLF separators",
                 chunkedStr.contains("\r\n"));
     }

     @Test
     public void testNonChunkedLargeInputNoSeparator() {
         byte[] largeInput = new byte[200];
         for (int i = 0; i < largeInput.length; i++) {
             largeInput[i] = (byte) (i % 256);
         }
         byte[] nonChunked = Base64.encodeBase64(largeInput);
         String result = new String(nonChunked);
         // Non-chunked output must not have any line separator
         assertFalse("Non-chunked output should not contain CRLF", result.contains("\r\n"));
         assertFalse("Non-chunked output should not contain LF", result.contains("\n"));
     }

     @Test
     public void testEncodeToStringMatchesEncode() {
         byte[] encoded = Base64.encodeBase64(HELLO_WORLD);
         String encodedStr = new String(encoded);
         assertEquals("encodeToString should match encode on instance",
                 encodedStr, new Base64().encodeToString(HELLO_WORLD));
     }

     // ================================================================
     // Boundary: length 76 (MIME_CHUNK_SIZE boundary)
     // ================================================================

     @Test
     public void testLength76NonChunkedNoSeparator() {
         // 57 bytes encodes to exactly 76 Base64 characters (no padding)
         byte[] input = new byte[57];
         for (int i = 0; i < input.length; i++) {
             input[i] = (byte) ('A' + (i % 26));
         }
         byte[] encoded = Base64.encodeBase64(input);
         String result = new String(encoded);
         assertEquals("57 bytes should encode to 76 chars", 76, result.length());
         assertFalse("Length-76 non-chunked output should not have separator",
                 result.endsWith("\r\n") || result.endsWith("\n"));
     }

     @Test
     public void testAllByteValuesRoundtrip() {
         // Test all 256 byte values for encode/decode roundtrip
         byte[] allBytes = new byte[256];
         for (int i = 0; i < 256; i++) {
             allBytes[i] = (byte) i;
         }
         byte[] encoded = Base64.encodeBase64(allBytes);
         String encodedStr = new String(encoded);
         byte[] decoded = Base64.decodeBase64(encodedStr);
         assertArrayEquals("All byte values should survive encode/decode roundtrip",
                 allBytes, decoded);
     }

     @Test
     public void testDecodeOfEncodedOutputNonChunked() {
         // Verify that the encoded output from encodeBase64 is directly decodable
         byte[] encoded = Base64.encodeBase64(HELLO_WORLD);
         byte[] decoded = Base64.decodeBase64(encoded);
         assertArrayEquals("Decoded non-chunked output should match original",
                 HELLO_WORLD, decoded);
     }

     @Test
     public void testEncodeBase64WithNullReturnsNull() {
         assertNull("encodeBase64(null) should return null",
                 Base64.encodeBase64(null));
     }

     @Test
     public void testEncodeBase64WithEmptyReturnsEmpty() {
         byte[] result = Base64.encodeBase64(new byte[0]);
         assertNotNull("encodeBase64(empty) should not return null", result);
         assertEquals("encodeBase64(empty) should return empty array", 0, result.length);
     }
 }