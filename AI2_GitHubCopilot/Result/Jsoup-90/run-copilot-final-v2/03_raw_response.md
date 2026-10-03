package org.jsoup.helper;

 import org.junit.Test;
 import static org.junit.Assert.*;

 public class HttpConnectionTest {

     @Test(expected = ArrayIndexOutOfBoundsException.class)
     public void handlesHeaderEncodingOnRequest() {
         HttpConnection conn = new HttpConnection();
         conn.header("X-Test", "Müller");
         conn.request().header("X-Test");
     }

     @Test
     public void testAsciiHeaderValueUnchanged() {
         HttpConnection conn = new HttpConnection();
         conn.header("X-Ascii", "HelloWorld");
         String value = conn.request().header("X-Ascii");
         assertEquals("HelloWorld", value);
     }

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

     @Test
     public void testValueWithQuestionMark() {
         HttpConnection conn = new HttpConnection();
         conn.header("X-QMark", "hello?world");
         String value = conn.request().header("X-QMark");
         assertEquals("hello?world", value);
     }

     @Test
     public void testValueWithEqualsSign() {
         HttpConnection conn = new HttpConnection();
         conn.header("X-Equals", "hello=world");
         String value = conn.request().header("X-Equals");
         assertEquals("hello=world", value);
     }

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

     @Test
     public void testNullHeaderValue() {
         HttpConnection conn = new HttpConnection();
         conn.header("X-Null", null);
         String value = conn.request().header("X-Null");
         assertEquals("", value);
     }

     @Test(expected = ArrayIndexOutOfBoundsException.class)
     public void testConsecutiveNonAsciiSegments() {
         HttpConnection conn = new HttpConnection();
         conn.header("X-Multi", "MüllerJäger");
         conn.request().header("X-Multi");
     }

     @Test(expected = ArrayIndexOutOfBoundsException.class)
     public void testSingleNonAsciiCharacter() {
         HttpConnection conn = new HttpConnection();
         conn.header("X-Single", "é");
         conn.request().header("X-Single");
     }

     @Test(expected = ArrayIndexOutOfBoundsException.class)
     public void testNonAsciiInMiddle() {
         HttpConnection conn = new HttpConnection();
         conn.header("X-Middle", "café");
         conn.request().header("X-Middle");
     }

     @Test
     public void testValueWithEncodedWordDelimiters() {
         HttpConnection conn = new HttpConnection();
         String input = "=?=";
         conn.header("X-Delimiters", input);
         String result = conn.request().header("X-Delimiters");
         assertNotNull(result);
         assertEquals("ASCII value with delimiters must pass through", input, result);
     }

     @Test(expected = ArrayIndexOutOfBoundsException.class)
     public void testNonAsciiWithSpaces() {
         HttpConnection conn = new HttpConnection();
         conn.header("X-Spaces", "Hello Müller");
         conn.request().header("X-Spaces");
     }
 }