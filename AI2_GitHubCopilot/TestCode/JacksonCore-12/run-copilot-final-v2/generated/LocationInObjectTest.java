package com.fasterxml.jackson.core.json;

 import com.fasterxml.jackson.core.*;
 import com.fasterxml.jackson.core.io.IOContext;
 import com.fasterxml.jackson.core.io.JsonEOFException;
 import com.fasterxml.jackson.core.sym.CharsToNameCanonicalizer;
 import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
 import com.fasterxml.jackson.core.util.BufferRecycler;
 import java.io.*;
 import org.junit.Test;
 import static org.junit.Assert.*;

 public class LocationInObjectTest {

     /* Utility to build a ReaderBasedJsonParser from a JSON string. */
     private ReaderBasedJsonParser _readerParser(String json) throws IOException {
         IOContext ioCtxt = new IOContext(new BufferRecycler(), json, false);
         return new ReaderBasedJsonParser(ioCtxt, 0, new StringReader(json),
                 null, CharsToNameCanonicalizer.createRoot(0));
     }

     /* Utility to build a UTF8StreamJsonParser from a JSON string. */
     private UTF8StreamJsonParser _utf8Parser(String json) throws IOException {
         IOContext ioCtxt = new IOContext(new BufferRecycler(), json, false);
         byte[] bytes = json.getBytes("UTF-8");
         return new UTF8StreamJsonParser(ioCtxt, 0, new ByteArrayInputStream(bytes),
                 null, ByteQuadsCanonicalizer.createRoot(0),
                 bytes, 0, bytes.length, false);
     }

     /* Consume all remaining tokens and return the char-offset of the last token. */
     private long _consumeAll(JsonParser p) throws IOException {
         long lastOff = -1L;
         while (p.nextToken() != null) {
             lastOff = p.getTokenLocation().getCharOffset();
         }
         return lastOff;
     }

     // ------------------------------------------------------------------------
     // ReaderBasedJsonParser tests
     // ------------------------------------------------------------------------

     @Test
     public void testOffsetWithObjectFieldsUsingReader() throws Exception {
         // This is the exact trigger case from the bug report.
         // JSON: {"a":1}   offsets: 0 1 2 3 4 5 6
         ReaderBasedJsonParser p = _readerParser("{\"a\":1}");
         assertToken(JsonToken.START_OBJECT, p.nextToken());
         assertEquals("START_OBJECT offset", 0L, p.getTokenLocation().getCharOffset());

         assertToken(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals("FIELD_NAME offset", 1L, p.getTokenLocation().getCharOffset());
         assertEquals("a", p.getText());

         assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
         assertEquals("VALUE_NUMBER_INT offset", 5L, p.getTokenLocation().getCharOffset());

         assertToken(JsonToken.END_OBJECT, p.nextToken());
         assertEquals("END_OBJECT offset", 6L, p.getTokenLocation().getCharOffset());

         assertNull(p.nextToken());
         p.close();
     }

     @Test
     public void testOffsetWithMultipleObjectFieldsReader() throws Exception {
         // {"x":1,"yz":2}
         // 0         1         2
         // 01234567890123456789012
         // {"x":1,"yz":2}
         ReaderBasedJsonParser p = _readerParser("{\"x\":1,\"yz\":2}");

         assertToken(JsonToken.START_OBJECT, p.nextToken());
         assertEquals(0L, p.getTokenLocation().getCharOffset());

         assertToken(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals(1L, p.getTokenLocation().getCharOffset());
         assertEquals("x", p.getText());

         assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
         assertEquals(5L, p.getTokenLocation().getCharOffset());

         assertToken(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals(7L, p.getTokenLocation().getCharOffset());
         assertEquals("yz", p.getText());

         assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
         assertEquals(12L, p.getTokenLocation().getCharOffset());

         assertToken(JsonToken.END_OBJECT, p.nextToken());
         assertEquals(13L, p.getTokenLocation().getCharOffset());

         assertNull(p.nextToken());
         p.close();
     }

     @Test
     public void testOffsetWithWhitespacePrefixReader() throws Exception {
         //    {"a" : 1}
         // 0         1
         // 012345678901
         ReaderBasedJsonParser p = _readerParser("  {\"a\" : 1}");

         assertToken(JsonToken.START_OBJECT, p.nextToken());
         assertEquals(2L, p.getTokenLocation().getCharOffset());

         assertToken(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals(3L, p.getTokenLocation().getCharOffset());
         assertEquals("a", p.getText());

         assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
         assertEquals(9L, p.getTokenLocation().getCharOffset());

         assertToken(JsonToken.END_OBJECT, p.nextToken());
         assertEquals(10L, p.getTokenLocation().getCharOffset());

         assertNull(p.nextToken());
         p.close();
     }

     @Test
     public void testOffsetWithStringValueReader() throws Exception {
         // {"key":"val"}
         // 0         1
         // 0123456789012
         ReaderBasedJsonParser p = _readerParser("{\"key\":\"val\"}");

         assertToken(JsonToken.START_OBJECT, p.nextToken());
         assertEquals(0L, p.getTokenLocation().getCharOffset());

         assertToken(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals(1L, p.getTokenLocation().getCharOffset());
         assertEquals("key", p.getText());

         assertToken(JsonToken.VALUE_STRING, p.nextToken());
         assertEquals(7L, p.getTokenLocation().getCharOffset());
         assertEquals("val", p.getText());

         assertToken(JsonToken.END_OBJECT, p.nextToken());
         assertEquals(12L, p.getTokenLocation().getCharOffset());

         assertNull(p.nextToken());
         p.close();
     }

     @Test
     public void testOffsetEmptyObjectReader() throws Exception {
         ReaderBasedJsonParser p = _readerParser("{}");

         assertToken(JsonToken.START_OBJECT, p.nextToken());
         assertEquals(0L, p.getTokenLocation().getCharOffset());

         assertToken(JsonToken.END_OBJECT, p.nextToken());
         assertEquals(1L, p.getTokenLocation().getCharOffset());

         assertNull(p.nextToken());
         p.close();
     }

     @Test
     public void testOffsetNestedObjectReader() throws Exception {
         // {"outer":{"inner":5}}
         // 0         1         2
         // 01234567890123456789012
         ReaderBasedJsonParser p = _readerParser("{\"outer\":{\"inner\":5}}");

         assertToken(JsonToken.START_OBJECT, p.nextToken());
         assertEquals(0L, p.getTokenLocation().getCharOffset());

         assertToken(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals(1L, p.getTokenLocation().getCharOffset());
         assertEquals("outer", p.getText());

         assertToken(JsonToken.START_OBJECT, p.nextToken());
         assertEquals(9L, p.getTokenLocation().getCharOffset());

         assertToken(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals(10L, p.getTokenLocation().getCharOffset());
         assertEquals("inner", p.getText());

         assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
         assertEquals(18L, p.getTokenLocation().getCharOffset());

         assertToken(JsonToken.END_OBJECT, p.nextToken());
         assertEquals(19L, p.getTokenLocation().getCharOffset());

         assertToken(JsonToken.END_OBJECT, p.nextToken());
         assertEquals(20L, p.getTokenLocation().getCharOffset());

         assertNull(p.nextToken());
         p.close();
     }

     // ------------------------------------------------------------------------
     // UTF8StreamJsonParser tests
     // ------------------------------------------------------------------------

     @Test
     public void testOffsetWithObjectFieldsUsingUTF8() throws Exception {
         UTF8StreamJsonParser p = _utf8Parser("{\"a\":1}");

         assertToken(JsonToken.START_OBJECT, p.nextToken());
         assertEquals(0L, p.getTokenLocation().getCharOffset());

         assertToken(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals(1L, p.getTokenLocation().getCharOffset());
         assertEquals("a", p.getText());

         assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
         assertEquals(5L, p.getTokenLocation().getCharOffset());

         assertToken(JsonToken.END_OBJECT, p.nextToken());
         assertEquals(6L, p.getTokenLocation().getCharOffset());

         assertNull(p.nextToken());
         p.close();
     }

     @Test
     public void testOffsetMultipleFieldsUTF8() throws Exception {
         UTF8StreamJsonParser p = _utf8Parser("{\"a\":10,\"bb\":20}");

         assertToken(JsonToken.START_OBJECT, p.nextToken());
         assertEquals(0L, p.getTokenLocation().getCharOffset());

         assertToken(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals(1L, p.getTokenLocation().getCharOffset());
         assertEquals("a", p.getText());

         assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
         assertEquals(5L, p.getTokenLocation().getCharOffset());

         assertToken(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals(8L, p.getTokenLocation().getCharOffset());
         assertEquals("bb", p.getText());

         assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
         assertEquals(13L, p.getTokenLocation().getCharOffset());

         assertToken(JsonToken.END_OBJECT, p.nextToken());
         assertEquals(15L, p.getTokenLocation().getCharOffset());

         assertNull(p.nextToken());
         p.close();
     }

     @Test
     public void testOffsetWithMultilineWhitespaceReader() throws Exception {
         // LF-separated object to exercise line/column reset
         // {\n"a":1}
         // 0         1
         // 01234567890
         ReaderBasedJsonParser p = _readerParser("{\n\"a\":1}");

         assertToken(JsonToken.START_OBJECT, p.nextToken());
         assertEquals(0L, p.getTokenLocation().getCharOffset());

         assertToken(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals(2L, p.getTokenLocation().getCharOffset());
         assertEquals("a", p.getText());
         assertEquals(2, p.getTokenLocation().getLineNr());

         assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
         assertEquals(6L, p.getTokenLocation().getCharOffset());

         assertToken(JsonToken.END_OBJECT, p.nextToken());
         assertEquals(7L, p.getTokenLocation().getCharOffset());

         assertNull(p.nextToken());
         p.close();
     }

     @Test
     public void testOffsetNumericEdgeValuesReader() throws Exception {
         // {"neg":-1,"zero":0,"big":999}
         // 0         1         2         3
         // 012345678901234567890123456789012
         ReaderBasedJsonParser p = _readerParser("{\"neg\":-1,\"zero\":0,\"big\":999}");

         assertToken(JsonToken.START_OBJECT, p.nextToken());

         assertToken(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals(1L, p.getTokenLocation().getCharOffset());
         assertEquals("neg", p.getText());

         assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
         assertEquals(7L, p.getTokenLocation().getCharOffset());

         assertToken(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals(10L, p.getTokenLocation().getCharOffset());
         assertEquals("zero", p.getText());

         assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
         assertEquals(17L, p.getTokenLocation().getCharOffset());

         assertToken(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals(19L, p.getTokenLocation().getCharOffset());
         assertEquals("big", p.getText());

         assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
         assertEquals(25L, p.getTokenLocation().getCharOffset());

         assertToken(JsonToken.END_OBJECT, p.nextToken());
         assertEquals(28L, p.getTokenLocation().getCharOffset());

         assertNull(p.nextToken());
         p.close();
     }

     @Test
     public void testGetCurrentLocationMatchesAfterClose() throws Exception {
         ReaderBasedJsonParser p = _readerParser("{\"a\":1}");
         _consumeAll(p);
         // getCurrentLocation reports the parser's current input pointer position
         JsonLocation loc = p.getCurrentLocation();
         assertNotNull(loc);
         assertTrue("Char offset after consuming all input should be > 0",
                 loc.getCharOffset() >= 6L);
         p.close();
     }

     // helper
     private static void assertToken(JsonToken exp, JsonToken act) {
         assertEquals("Unexpected token", exp, act);
     }
 }
