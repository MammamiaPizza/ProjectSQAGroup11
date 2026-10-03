package com.fasterxml.jackson.databind.node;

 import static org.junit.Assert.*;

 import java.io.IOException;

 import org.junit.Test;

 public class TreeTraversingParser2096Test {

     // ---------- URL-safe variant (the original bug) ----------

     @Test
     public void testUrlSafeVariantSingleByte() throws IOException {
         byte[] data = new byte[]{0x00}; // single 0-byte
         String base64 = com.fasterxml.jackson.core.Base64Variant.MODIFIED_FOR_URL.encode(data);

         JsonNode textNode = JsonNodeFactory.instance.textNode(base64);
         TreeTraversingParser parser = new TreeTraversingParser(textNode);
         parser.nextToken(); // advance to VALUE_STRING

         byte[] result =
parser.getBinaryValue(com.fasterxml.jackson.core.Base64Variant.MODIFIED_FOR_URL);
         assertNotNull("getBinaryValue should not return null for valid URL-safe base64 TextNode",
result);
         assertArrayEquals("Decoded bytes must match original data", data, result);
     }

     @Test
     public void testUrlSafeVariantMultiByte() throws IOException {
         byte[] data = new byte[]{0x01, 0x02, 0x03, 0x04};
         String base64 = com.fasterxml.jackson.core.Base64Variant.MODIFIED_FOR_URL.encode(data);

         JsonNode textNode = JsonNodeFactory.instance.textNode(base64);
         TreeTraversingParser parser = new TreeTraversingParser(textNode);
         parser.nextToken();

         byte[] result =
parser.getBinaryValue(com.fasterxml.jackson.core.Base64Variant.MODIFIED_FOR_URL);
         assertNotNull(result);
         assertArrayEquals(data, result);
     }

     // ---------- Standard (MIME) variant ----------

     @Test
     public void testStandardVariantSingleByte() throws IOException {
         byte[] data = new byte[]{0x00};
         com.fasterxml.jackson.core.Base64Variant variant =
com.fasterxml.jackson.core.Base64Variant.MIME_NO_LINEFEEDS;
         String base64 = variant.encode(data);

         JsonNode textNode = JsonNodeFactory.instance.textNode(base64);
         TreeTraversingParser parser = new TreeTraversingParser(textNode);
         parser.nextToken();

         byte[] result = parser.getBinaryValue(variant);
         assertNotNull(result);
         assertArrayEquals(data, result);
     }

     @Test
     public void testStandardVariantMultiByte() throws IOException {
         byte[] data = new byte[]{0x7F, (byte) 0xFF, 0x00};
         com.fasterxml.jackson.core.Base64Variant variant =
com.fasterxml.jackson.core.Base64Variant.MIME_NO_LINEFEEDS;
         String base64 = variant.encode(data);

         JsonNode textNode = JsonNodeFactory.instance.textNode(base64);
         TreeTraversingParser parser = new TreeTraversingParser(textNode);
         parser.nextToken();

         byte[] result = parser.getBinaryValue(variant);
         assertNotNull(result);
         assertArrayEquals(data, result);
     }

     // ---------- Edge cases ----------

     @Test
     public void testEmptyTextNode() throws IOException {
         JsonNode textNode = JsonNodeFactory.instance.textNode("");
         TreeTraversingParser parser = new TreeTraversingParser(textNode);
         parser.nextToken();

         byte[] result = parser.getBinaryValue(com.fasterxml.jackson.core.Base64Variant.MIME);
         assertNotNull("Empty text should decode to empty byte array", result);
         assertEquals(0, result.length);
     }

     @Test
     public void testExtraPadding() throws Exception {
         // "AA===" is valid base64 content ("AA") followed by illegal third padding character
         JsonNode textNode = JsonNodeFactory.instance.textNode("AA===");
         TreeTraversingParser parser = new TreeTraversingParser(textNode);
         parser.nextToken();

         try {
             parser.getBinaryValue(com.fasterxml.jackson.core.Base64Variant.MIME);
             fail("Expected JsonParseException for extra padding");
         } catch (com.fasterxml.jackson.core.JsonParseException e) {
             // expected
         }
     }

     @Test(expected = com.fasterxml.jackson.core.JsonParseException.class)
     public void testInvalidBase64Character() throws IOException {
         // '!' is not a valid base64 character
         JsonNode textNode = JsonNodeFactory.instance.textNode("!nvalid===");
         TreeTraversingParser parser = new TreeTraversingParser(textNode);
         parser.nextToken();

         parser.getBinaryValue(com.fasterxml.jackson.core.Base64Variant.MIME);
     }

     @Test
     public void testNonTextNodeReturnsNull() throws IOException {
         JsonNode intNode = JsonNodeFactory.instance.numberNode(123);
         TreeTraversingParser parser = new TreeTraversingParser(intNode);
         parser.nextToken(); // VALUE_NUMBER_INT

         byte[] result = parser.getBinaryValue(com.fasterxml.jackson.core.Base64Variant.MIME);
         assertNull("Non-text / non-binary nodes should yield null", result);
     }

     @Test
     public void testClosedParserReturnsNull() throws IOException {
         JsonNode textNode = JsonNodeFactory.instance.textNode("AA==");
         TreeTraversingParser parser = new TreeTraversingParser(textNode);
         parser.nextToken();

         parser.close();
         byte[] result = parser.getBinaryValue(com.fasterxml.jackson.core.Base64Variant.MIME);
         assertNull(result);
     }

     @Test
     public void testTextNodeWithStandardPadding() throws IOException {
         // "AA==" encodes a single byte 0x00
         byte[] expected = new byte[]{0x00};
         JsonNode textNode = JsonNodeFactory.instance.textNode("AA==");
         TreeTraversingParser parser = new TreeTraversingParser(textNode);
         parser.nextToken();

         byte[] result = parser.getBinaryValue(com.fasterxml.jackson.core.Base64Variant.MIME);
         assertNotNull(result);
         assertArrayEquals(expected, result);
     }

     // ---------- Verify the variant is actually used ----------

     @Test
     public void testVariantDoesMatter() throws IOException {
         // A string that is valid in MODIFIED_FOR_URL but not in standard mode
         // URL-safe variant uses '-' and '_' instead of '+' and '/'
         // encode a byte that yields '-' or '_' with the URL-safe variant,
         // make sure standard variant fails on it
         byte[] data = new byte[]{(byte) 0xFC}; // 11111100 -> base64 uses all bits
         com.fasterxml.jackson.core.Base64Variant urlVariant =
com.fasterxml.jackson.core.Base64Variant.MODIFIED_FOR_URL;
         String urlBase64 = urlVariant.encode(data); // may contain '-' or '_'

         JsonNode textNode = JsonNodeFactory.instance.textNode(urlBase64);
         TreeTraversingParser parser = new TreeTraversingParser(textNode);
         parser.nextToken();

         // URL-safe variant must work
         byte[] urlResult = parser.getBinaryValue(urlVariant);
         assertNotNull(urlResult);
         assertArrayEquals(data, urlResult);

         // Standard variant must fail on the same text (contains invalid chars)
         // Close and recreate parser because it is stateful
         parser.close();

         parser = new TreeTraversingParser(textNode);
         parser.nextToken();
         try {
             parser.getBinaryValue(com.fasterxml.jackson.core.Base64Variant.MIME);
             // If we reach here without exception the bug is present or the variant doesn't differ;
             // we can still assert that the standard variant failed or returned null.
             // But in the buggy version getBinaryValue ignores the variant, so it might succeed
             // unintentionally. We can still assert: ensure it fails for standard by checking
exception.
             // Actually in the buggy version, binaryValue() tries to decode with default variant
(maybe MIME)
             // and will fail. So we expect exception. After fix, getBinaryValue uses provided
variant
             // and returns bytes, but when we call with MIME it should fail on URL char.
             // So either way this test is consistent with fixed behaviour.
             fail("Should have thrown JsonParseException for invalid MIME base64 content");
         } catch (com.fasterxml.jackson.core.JsonParseException e) {
             // expected: MIME variant does not accept URL-safe chars
         }
     }
 }
