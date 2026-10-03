package org.jsoup.helper;

 import org.junit.Test;
 import static org.junit.Assert.*;

 import java.util.Base64;
 import java.nio.charset.StandardCharsets;

 /**
  * Tests for HttpConnection header encoding (RFC 2047), targeting the
  * encodeMimeName ArrayIndexOutOfBoundsException bug (#1172).
  */
 public class HttpConnectionTest {

     /**
      * Computes the expected RFC 2047 encoded-word: "=?UTF-8?B?<base64>?="
      * for inputs containing non-ASCII; returns ASCII inputs unchanged.
      */
     private static String expectedMimeEncode(String input) {
         if (input == null || input.isEmpty()) {
             return "";
         }
         boolean hasNonAscii = false;
         for (int i = 0; i < input.length(); i++) {
             if (input.charAt(i) > 127) {
                 hasNonAscii = true;
                 break;
             }
         }
         if (!hasNonAscii) {
             return input;
         }
         byte[] utf8Bytes = input.getBytes(StandardCharsets.UTF_8);
         String base64 = Base64.getEncoder().encodeToString(utf8Bytes);
         return "=?UTF-8?B?" + base64 + "?=";
     }

     /**
      * Trigger test from the bug report: non-ASCII value must be RFC 2047
      * encoded without throwing ArrayIndexOutOfBoundsException.
      */
     @Test
     public void handlesHeaderEncodingOnRequest() {
         HttpConnection conn = new HttpConnection();
         conn.header("X-Test", "Müller");
         String value = conn.request().header("X-Test");
         assertNotNull("Header value should not be null", value);
         String expected = expectedMimeEncode("Müller");
         assertEquals("Non-ASCII header must be RFC 2047 encoded", expected, value);
         assertTrue("Must start with =?UTF-8?B?", value.startsWith("=?UTF-8?B?"));
         assertTrue("Must end with ?=", value.endsWith("?="));
     }

     /**
      * ASCII header values must pass through unchanged.
      */
     @Test
     public void testAsciiHeaderValueUnchanged() {
         HttpConnection conn = new HttpConnection();
         conn.header("X-Ascii", "HelloWorld");
         String value = conn.request().header("X-Ascii");
         assertEquals("ASCII value must remain unchanged", "HelloWorld", value);
     }

     /**
      * Empty header value must not cause ArrayIndexOutOfBoundsException
      * and should be stored as the empty string.
      */
     @Test
     public void testEmptyHeaderValue() {
         HttpConnection conn = new HttpConnection();
         try {
             conn.header("X-Empty", "");
             String value = conn.request().header("X-Empty");
             assertNotNull(value);
             assertEquals("", value);
         } catch (ArrayIndexOutOfBoundsException e) {
             fail("Empty value must not throw ArrayIndexOutOfBoundsException");
         }
     }

     /**
      * Value containing a question mark; must not confuse the split logic
      * inside encodeMimeName and trigger ArrayIndexOutOfBoundsException.
      */
     @Test
     public void testValueWithQuestionMark() {
         HttpConnection conn = new HttpConnection();
         conn.header("X-QMark", "hello?world");
         String value = conn.request().header("X-QMark");
         assertEquals("hello?world", value);
     }

     /**
      * Value containing an equals sign; must not confuse delimiter detection
      * and must not trigger ArrayIndexOutOfBoundsException.
      */
     @Test
     public void testValueWithEqualsSign() {
         HttpConnection conn = new HttpConnection();
         conn.header("X-Equals", "hello=world");
         String value = conn.request().header("X-Equals");
         assertEquals("hello=world", value);
     }

     /**
      * Already RFC-2047-encoded value must not be double-encoded.
      * The already-encoded string is all ASCII and should survive unchanged.
      */
     @Test
     public void testAlreadyEncodedValue() {
         HttpConnection conn = new HttpConnection();
         String alreadyEncoded = "=?UTF-8?B?dGVzdA==?=";
         conn.header("X-AlradyEncoded", alreadyEncoded);
         String result = conn.request().header("X-AlradyEncoded");
         assertNotNull(result);
         assertTrue("Already-encoded value must not be double-encoded",
                    result.contains("UTF-8?B?"));
     }

     /**
      * Null value must be treated as an empty string (per addHeader contract)
      * and must not throw any exception.
      */
     @Test
     public void testNullHeaderValue() {
         HttpConnection conn = new HttpConnection();
         conn.header("X-Null", null);
         String value = conn.request().header("X-Null");
         assertEquals("", value);
     }

     /**
      * Multiple consecutive non-ASCII characters must be fully encoded
      * into a single encoded-word.
      */
     @Test
     public void testConsecutiveNonAsciiSegments() {
         HttpConnection conn = new HttpConnection();
         String input = "MüllerJäger";
         conn.header("X-Multi", input);
         String result = conn.request().header("X-Multi");
         String expected = expectedMimeEncode(input);
         assertEquals("Consecutive non-ASCII must be one encoded-word", expected, result);
     }

     /**
      * Single-character non-ASCII value must be correctly encoded.
      */
     @Test
     public void testSingleNonAsciiCharacter() {
         HttpConnection conn = new HttpConnection();
         String input = "é";
         conn.header("X-Single", input);
         String result = conn.request().header("X-Single");
         assertEquals(expectedMimeEncode(input), result);
     }

     /**
      * ASCII prefix/suffix with a non-ASCII character in the middle
      * must be encoded as a single encoded-word.
      */
     @Test
     public void testNonAsciiInMiddle() {
         HttpConnection conn = new HttpConnection();
         String input = "café";
         conn.header("X-Middle", input);
         String result = conn.request().header("X-Middle");
         assertEquals(expectedMimeEncode(input), result);
     }

     /**
      * Value containing "=?=" which mimics encoded-word delimiters.
      * Must not cause ArrayIndexOutOfBoundsException.
      */
     @Test
     public void testValueWithEncodedWordDelimiters() {
         HttpConnection conn = new HttpConnection();
         String input = "=?=";
         conn.header("X-Delimiters", input);
         String result = conn.request().header("X-Delimiters");
         assertNotNull("Result must not be null", result);
         assertEquals("ASCII value with delimiters must pass through", input, result);
     }

     /**
      * Non-ASCII characters together with spaces must be fully encoded
      * without splitting issues.
      */
     @Test
     public void testNonAsciiWithSpaces() {
         HttpConnection conn = new HttpConnection();
         String input = "Hello Müller";
         conn.header("X-Spaces", input);
         String result = conn.request().header("X-Spaces");
         String expected = expectedMimeEncode(input);
         assertEquals("Non-ASCII with spaces must be fully encoded", expected, result);
     }
 }
