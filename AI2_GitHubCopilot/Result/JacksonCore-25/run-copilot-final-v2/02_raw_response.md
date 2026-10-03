package com.fasterxml.jackson.core.json;

 import static org.junit.Assert.*;

 import java.io.IOException;
 import java.io.StringReader;

 import org.junit.Test;

 import com.fasterxml.jackson.core.JsonFactory;
 import com.fasterxml.jackson.core.JsonParseException;
 import com.fasterxml.jackson.core.JsonParser;
 import com.fasterxml.jackson.core.JsonToken;

 /**
  * Tests for ReaderBasedJsonParser handling of unquoted field names with characters
  * beyond the Latin-1 range (code points > 255), which triggered ArrayIndexOutOfBoundsException
  * in _icLatin1 lookup (bug #510 / issue 25).
  */
 public class ReaderBasedJsonParserNonLatin1UnquotedNamesTest {

     private JsonParser createParser(String json) throws IOException {
         JsonFactory f = new JsonFactory();
         f.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
         return f.createParser(new StringReader(json));
     }

     @Test
     public void testUnquotedNameWithChar256() throws IOException {
         // Code point 256 (U+0100) – triggers the off-by-one bug
         String json = "{\u0100: \"value\"}";
         JsonParser p = createParser(json);
         try {
             assertToken(JsonToken.START_OBJECT, p.nextToken());
             // Either a FIELD_NAME token or a parse exception is acceptable;
             // the critical requirement is no ArrayIndexOutOfBoundsException.
             JsonToken tok = p.nextToken();
             if (tok == JsonToken.FIELD_NAME) {
                 String name = p.getCurrentName();
                 assertNotNull(name);
                 // Name may or may not preserve the exact char depending on parser behavior,
                 // but it should be non-null and non-empty.
                 assertFalse(name.isEmpty());
             }
             // If parsing succeeded, continue consuming tokens to ensure validity.
             // If it threw, that's ok – it must not be AIOOBE.
         } catch (JsonParseException e) {
             // Graceful rejection is acceptable.
             assertNotNull(e.getMessage());
         } finally {
             p.close();
         }
     }

     @Test
     public void testUnquotedNameMaxLatin1Char255() throws IOException {
         // Code point 255 (U+00FF) – maximum valid Latin-1 index
         String json = "{\u00FF: \"value\"}";
         JsonParser p = createParser(json);
         assertToken(JsonToken.START_OBJECT, p.nextToken());
         assertToken(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals("\u00FF", p.getCurrentName());
         assertToken(JsonToken.VALUE_STRING, p.nextToken());
         assertEquals("value", p.getText());
         assertToken(JsonToken.END_OBJECT, p.nextToken());
         p.close();
     }

     @Test
     public void testUnquotedNameChar0() throws IOException {
         // Null character is special but within Latin-1; should produce an error or be handled.
         // Key point: no AIOOBE.
         String json = "{\u0000: \"value\"}";
         JsonParser p = createParser(json);
         try {
             assertToken(JsonToken.START_OBJECT, p.nextToken());
             p.nextToken();
             fail("Expected parse exception for null character in unquoted name");
         } catch (JsonParseException e) {
             assertNotNull(e.getMessage());
         } finally {
             p.close();
         }
     }

     @Test
     public void testUnquotedNameChar0x7F() throws IOException {
         // DEL character (0x7F) – within Latin-1
         String json = "{\u007F: \"value\"}";
         JsonParser p = createParser(json);
         assertToken(JsonToken.START_OBJECT, p.nextToken());
         assertToken(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals("\u007F", p.getCurrentName());
         assertToken(JsonToken.VALUE_STRING, p.nextToken());
         assertToken(JsonToken.END_OBJECT, p.nextToken());
         p.close();
     }

     @Test
     public void testUnquotedNameChar0x80() throws IOException {
         // Code point 0x80 – first extended ASCII within Latin-1
         String json = "{\u0080: \"value\"}";
         JsonParser p = createParser(json);
         assertToken(JsonToken.START_OBJECT, p.nextToken());
         assertToken(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals("\u0080", p.getCurrentName());
         assertToken(JsonToken.VALUE_STRING, p.nextToken());
         assertToken(JsonToken.END_OBJECT, p.nextToken());
         p.close();
     }

     @Test
     public void testUnquotedNameMixedAsciiNonLatin1() throws IOException {
         // Mixed ASCII and non-Latin1: "a\u0100b" – chars 97, 256, 98
         String json = "{a\u0100b: \"value\"}";
         JsonParser p = createParser(json);
         try {
             assertToken(JsonToken.START_OBJECT, p.nextToken());
             JsonToken tok = p.nextToken();
             if (tok == JsonToken.FIELD_NAME) {
                 String name = p.getCurrentName();
                 assertNotNull(name);
                 assertFalse(name.isEmpty());
             }
         } catch (JsonParseException e) {
             assertNotNull(e.getMessage());
         } finally {
             p.close();
         }
     }

     @Test
     public void testUnquotedNameMaxBMP() throws IOException {
         // U+FFFF – maximum BMP character
         String json = "{\uFFFF: \"value\"}";
         JsonParser p = createParser(json);
         try {
             assertToken(JsonToken.START_OBJECT, p.nextToken());
             JsonToken tok = p.nextToken();
             if (tok == JsonToken.FIELD_NAME) {
                 String name = p.getCurrentName();
                 assertNotNull(name);
                 assertFalse(name.isEmpty());
             }
         } catch (JsonParseException e) {
             assertNotNull(e.getMessage());
         } finally {
             p.close();
         }
     }

     @Test
     public void testUnquotedNameSingleChar() throws IOException {
         // Single ASCII letter
         String json = "{a: \"value\"}";
         JsonParser p = createParser(json);
         assertToken(JsonToken.START_OBJECT, p.nextToken());
         assertToken(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals("a", p.getCurrentName());
         assertToken(JsonToken.VALUE_STRING, p.nextToken());
         assertToken(JsonToken.END_OBJECT, p.nextToken());
         p.close();
     }

     @Test
     public void testUnquotedNameLongAscii() throws IOException {
         // Long ASCII name – regression safety
         StringBuilder sb = new StringBuilder();
         for (int i = 0; i < 100; i++) {
             sb.append((char) ('a' + (i % 26)));
         }
         String longName = sb.toString();
         String json = "{" + longName + ": \"value\"}";
         JsonParser p = createParser(json);
         assertToken(JsonToken.START_OBJECT, p.nextToken());
         assertToken(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals(longName, p.getCurrentName());
         assertToken(JsonToken.VALUE_STRING, p.nextToken());
         assertToken(JsonToken.END_OBJECT, p.nextToken());
         p.close();
     }

     @Test
     public void testUnquotedNameWithSpaces() throws IOException {
         // Space is not allowed in unquoted names normally; should produce error
         String json = "{a b: \"value\"}";
         JsonParser p = createParser(json);
         try {
             assertToken(JsonToken.START_OBJECT, p.nextToken());
             p.nextToken();
             fail("Expected parse exception for space in unquoted name");
         } catch (JsonParseException e) {
             assertNotNull(e.getMessage());
         } finally {
             p.close();
         }
     }

     @Test
     public void testUnquotedNameMultipleFieldsWithNonLatin1() throws IOException {
         // Object with multiple fields, one containing non-Latin1 char
         String json = "{\u0100: \"first\", b: \"second\", c\u0100d: \"third\"}";
         JsonParser p = createParser(json);
         try {
             assertToken(JsonToken.START_OBJECT, p.nextToken());
             // Iterate through all fields without AIOOBE
             int fieldCount = 0;
             while (p.nextToken() != JsonToken.END_OBJECT) {
                 if (p.getCurrentToken() == JsonToken.FIELD_NAME) {
                     assertNotNull(p.getCurrentName());
                     p.nextToken();
                     assertNotNull(p.getText());
                     fieldCount++;
                 }
             }
             assertEquals(3, fieldCount);
         } catch (JsonParseException e) {
             assertNotNull(e.getMessage());
         } finally {
             p.close();
         }
     }

     @Test
     public void testUnquotedNameReplicateIssue510() throws IOException {
         // Direct replication of the bug trigger scenario from testUnquotedIssue510
         String json = "{\u0100:true}";
         JsonParser p = createParser(json);
         try {
             assertToken(JsonToken.START_OBJECT, p.nextToken());
             JsonToken tok = p.nextToken();
             assertNotNull(tok);
             if (tok == JsonToken.FIELD_NAME) {
                 String name = p.getCurrentName();
                 assertNotNull(name);
                 assertFalse(name.isEmpty());
                 assertToken(JsonToken.VALUE_TRUE, p.nextToken());
                 assertTrue(p.getBooleanValue());
             }
             assertToken(JsonToken.END_OBJECT, p.nextToken());
         } catch (JsonParseException e) {
             // If the parser rejects non-Latin1 unquoted names with a proper exception,
             // that is also a valid fix for the AIOOBE bug.
             assertNotNull(e.getMessage());
         } finally {
             p.close();
         }
     }

     private void assertToken(JsonToken exp, JsonToken actual) {
         assertEquals("Expected token " + exp + " but got " + actual, exp, actual);
     }
 }