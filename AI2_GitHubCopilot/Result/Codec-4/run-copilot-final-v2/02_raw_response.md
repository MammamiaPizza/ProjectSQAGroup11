package org.apache.commons.codec.binary;

 import static org.junit.Assert.*;

 import java.math.BigInteger;
 import java.util.Arrays;

 import org.junit.Test;

 public class Base64Test {

     // --- Constructor and isUrlSafe tests ---

     @Test
     public void testDefaultConstructorIsNotUrlSafe() {
         Base64 b64 = new Base64();
         assertFalse("Default constructor should create non-url-safe instance", b64.isUrlSafe());
     }

     @Test
     public void testUrlSafeConstructorTrue() {
         Base64 b64 = new Base64(true);
         assertTrue("Constructor with true should set url-safe", b64.isUrlSafe());
     }

     @Test
     public void testUrlSafeConstructorFalse() {
         Base64 b64 = new Base64(false);
         assertFalse("Constructor with false should be non-url-safe", b64.isUrlSafe());
     }

     @Test
     public void testConstructorWithLineLengthAndSeparatorUrlSafe() {
         Base64 b64 = new Base64(76, new byte[]{'\r', '\n'}, true);
         assertTrue("Should be url-safe", b64.isUrlSafe());
     }

     // --- Encode/decode roundtrip: standard alphabet ---

     @Test
     public void testRoundtripEmptyArray() {
         Base64 b64 = new Base64();
         byte[] input = new byte[0];
         byte[] encoded = b64.encode(input);
         byte[] decoded = b64.decode(encoded);
         assertArrayEquals("Empty input roundtrip", input, decoded);
     }

     @Test
     public void testRoundtripSingleByte() {
         Base64 b64 = new Base64();
         byte[] input = new byte[]{0x41};
         byte[] encoded = b64.encode(input);
         byte[] decoded = b64.decode(encoded);
         assertArrayEquals("Single byte roundtrip", input, decoded);
     }

     @Test
     public void testRoundtripTwoBytes() {
         Base64 b64 = new Base64();
         byte[] input = new byte[]{0x41, 0x42};
         byte[] encoded = b64.encode(input);
         byte[] decoded = b64.decode(encoded);
         assertArrayEquals("Two byte roundtrip", input, decoded);
     }

     @Test
     public void testRoundtripThreeBytes() {
         Base64 b64 = new Base64();
         byte[] input = new byte[]{0x41, 0x42, 0x43};
         byte[] encoded = b64.encode(input);
         byte[] decoded = b64.decode(encoded);
         assertArrayEquals("Three byte roundtrip", input, decoded);
     }

     @Test
     public void testRoundtripBinaryDataWithZerosAndFF() {
         Base64 b64 = new Base64();
         byte[] input = new byte[]{0x00, (byte) 0xFF, 0x00, (byte) 0xFF, 0x01};
         byte[] encoded = b64.encode(input);
         byte[] decoded = b64.decode(encoded);
         assertArrayEquals("Binary data with 0x00/0xFF roundtrip", input, decoded);
     }

     // --- URL-safe alphabet produces no '+'/'/' characters ---

     @Test
     public void testUrlSafeEncodingUsesNoPlusOrSlash() {
         // Input that would normally produce '+' and '/' in standard encoding
         byte[] input = new byte[]{(byte) 0xFC, (byte) 0xFB, (byte) 0xFA};
         Base64 urlSafeB64 = new Base64(true);
         byte[] encoded = urlSafeB64.encode(input);
         String encodedStr = new String(encoded);
         assertFalse("URL-safe encoding should not contain '+'", encodedStr.contains("+"));
         assertFalse("URL-safe encoding should not contain '/'", encodedStr.contains("/"));
         // Verify roundtrip still works
         byte[] decoded = urlSafeB64.decode(encoded);
         assertArrayEquals("URL-safe roundtrip", input, decoded);
     }

     // --- Static encodeBase64 and decodeBase64 roundtrip ---

     @Test
     public void testStaticEncodeDecodeRoundtrip() {
         byte[] input = "Hello World".getBytes();
         byte[] encoded = Base64.encodeBase64(input);
         byte[] decoded = Base64.decodeBase64(encoded);
         assertArrayEquals("Static encode/decode roundtrip", input, decoded);
     }

     // --- encodeInteger/decodeInteger roundtrip ---

     @Test
     public void testIntegerEncodeDecodeRoundtrip() {
         BigInteger value = new BigInteger("12345678901234567890");
         byte[] encoded = Base64.encodeInteger(value);
         BigInteger decoded = Base64.decodeInteger(encoded);
         assertEquals("Integer encode/decode roundtrip", value, decoded);
     }
 }