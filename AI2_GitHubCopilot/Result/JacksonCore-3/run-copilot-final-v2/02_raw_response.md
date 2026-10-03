package com.fasterxml.jackson.core.json;

 import static org.junit.Assert.*;

 import java.io.ByteArrayInputStream;
 import java.io.IOException;

 import org.junit.Before;
 import org.junit.Test;

 import com.fasterxml.jackson.core.*;
 import com.fasterxml.jackson.core.io.IOContext;
 import com.fasterxml.jackson.core.sym.BytesToNameCanonicalizer;
 import com.fasterxml.jackson.core.util.BufferRecycler;

 public class UTF8StreamJsonParserOffsetBugTest extends com.fasterxml.jackson.core.BaseTest {

     private byte[] buf;
     private int bufLen;
     private IOContext ioContext;
     private BytesToNameCanonicalizer symbols;

     @Before
     public void setUp() {
         buf = new byte[128];
         bufLen = 0;
         ioContext = new IOContext(new BufferRecycler(), null, false);
         symbols = BytesToNameCanonicalizer.createRoot();
     }

     private void writeBytes(String str) throws IOException {
         byte[] b = str.getBytes("UTF-8");
         System.arraycopy(b, 0, buf, bufLen, b.length);
         bufLen += b.length;
     }

     private UTF8StreamJsonParser createParser(int offset) {
         // offset: how many leading padding bytes before the actual JSON content
         return new UTF8StreamJsonParser(ioContext, 0,
                 new ByteArrayInputStream(buf, offset, bufLen - offset),
                 null, symbols.makeChild(JsonFactory.Feature.collectDefaults()),
                 buf, offset, bufLen, false);
     }

     @Test
     public void testFirstTokenOffsetZeroWithImplicitInputOffset() throws Exception {
         writeBytes("42");
         UTF8StreamJsonParser p = createParser(0); // input starts at buffer byte 0
         assertNull(p.nextToken()); // should be VALUE_NUMBER_INT, but check via nextToken
         JsonLocation loc = p.getCurrentLocation();
         assertEquals("Token byte offset with zero input offset", 0, loc.getByteOffset());
         p.close();
     }

     @Test
     public void testFirstTokenOffsetWithExplicitInputOffset() throws Exception {
         writeBytes("   true");
         UTF8StreamJsonParser p = createParser(3); // input starts at buffer byte 3, JSON is "true"
         // This is the scenario from TestLocation.testOffsetWithInputOffset
         assertNull(p.nextToken());
         JsonLocation loc = p.getCurrentLocation();
         assertEquals("Token byte offset with 3-byte input offset", 0, loc.getByteOffset());
         p.close();
     }

     @Test
     public void testTokenOffsetAfterLeadingWhitespace() throws Exception {
         writeBytes("\n\n  [1]");
         UTF8StreamJsonParser p = createParser(0);
         assertNull(p.nextToken()); // START_ARRAY
         JsonLocation loc = p.getCurrentLocation();
         assertEquals("Token byte offset after newlines and spaces", 4, loc.getByteOffset());
         p.close();
     }

     @Test
     public void testTokenOffsetAfterLeadingWhitespaceWithInputOffset() throws Exception {
         writeBytes("  \n  [1]");
         UTF8StreamJsonParser p = createParser(1); // skip the first byte
         assertNull(p.nextToken()); // START_ARRAY
         JsonLocation loc = p.getCurrentLocation();
         // input offset = 1, leading bytes seen = 3 (space, newline, space, space)
         assertEquals("Token byte offset after leading WS with input offset", 3,
loc.getByteOffset());
         p.close();
     }

     @Test
     public void testTokenLocationContentAfterInputOffset() throws Exception {
         writeBytes("null");
         UTF8StreamJsonParser p = createParser(0);
         assertNull(p.nextToken());
         JsonLocation tokenLoc = p.getTokenLocation();
         assertEquals("getTokenLocation byte offset for first token", 0, tokenLoc.getByteOffset());
         p.close();
     }

     @Test
     public void testCurrentLocationBeforeAnyToken() throws Exception {
         writeBytes("42");
         UTF8StreamJsonParser p = createParser(0);
         JsonLocation loc = p.getCurrentLocation();
         // Before nextToken() the parser is at start; byte offset should be 0
         assertEquals("Pre-token current location byte offset", 0, loc.getByteOffset());
         p.close();
     }

     @Test
     public void testGetTextOffsetMatchesTokenStart() throws Exception {
         writeBytes("true");
         UTF8StreamJsonParser p = createParser(0);
         assertNull(p.nextToken());
         assertEquals("getTextOffset == getTokenLocation byte offset",
p.getTokenLocation().getByteOffset(), (long) p.getTextOffset());
         p.close();
     }

     @Test
     public void testGetTextOffsetMatchesTokenStartWithInputOffset() throws Exception {
         writeBytes("-123");
         UTF8StreamJsonParser p = createParser(2);
         assertNull(p.nextToken());
         assertEquals("getTextOffset == getTokenLocation byte offset with input offset",
                 p.getTokenLocation().getByteOffset(), (long) p.getTextOffset());
         p.close();
     }

     @Test
     public void testMultipleTokensOffsetsProgression() throws Exception {
         writeBytes("{\"a\":1}");
         UTF8StreamJsonParser p = createParser(0);
         // START_OBJECT
         assertNull(p.nextToken());
         assertEquals("START_OBJECT byte offset", 0, p.getCurrentLocation().getByteOffset());
         // FIELD_NAME
         assertNull(p.nextToken());
         assertEquals("FIELD_NAME byte offset", 1, p.getCurrentLocation().getByteOffset());
         // VALUE_NUMBER_INT
         assertNull(p.nextToken());
         assertEquals("VALUE_NUMBER_INT byte offset", 5, p.getCurrentLocation().getByteOffset());
         // END_OBJECT
         assertNull(p.nextToken());
         assertEquals("END_OBJECT byte offset", 6, p.getCurrentLocation().getByteOffset());
         p.close();
     }

     @Test
     public void testMultipleTokensOffsetsProgressionWithInputOffset() throws Exception {
         writeBytes("{\"b\":false}");
         UTF8StreamJsonParser p = createParser(2);
         // START_OBJECT
         assertNull(p.nextToken());
         assertEquals("START_OBJECT byte offset with offset", 0,
p.getCurrentLocation().getByteOffset());
         // FIELD_NAME
         assertNull(p.nextToken());
         assertEquals("FIELD_NAME byte offset with offset", 1,
p.getCurrentLocation().getByteOffset());
         // VALUE_FALSE
         assertNull(p.nextToken());
         assertEquals("VALUE_FALSE byte offset with offset", 5,
p.getCurrentLocation().getByteOffset());
         // END_OBJECT
         assertNull(p.nextToken());
         assertEquals("END_OBJECT byte offset with offset", 10,
p.getCurrentLocation().getByteOffset());
         p.close();
     }

     @Test
     public void testOffsetAtBufferBoundaryWithInputOffset() throws Exception {
         // Construct a buffer where JSON starts exactly at the specified offset
         int startOffset = 5;
         byte[] data = new byte[startOffset + 10];
         String json = "[42]";
         byte[] jsonBytes = json.getBytes("UTF-8");
         System.arraycopy(jsonBytes, 0, data, startOffset, jsonBytes.length);
         // Use buffer from 'startOffset' to end
         UTF8StreamJsonParser p = new UTF8StreamJsonParser(
                 new IOContext(new BufferRecycler(), null, false), 0,
                 new ByteArrayInputStream(data, startOffset, jsonBytes.length),
                 null, symbols.makeChild(JsonFactory.Feature.collectDefaults()),
                 data, startOffset, startOffset + jsonBytes.length, false);
         assertNull(p.nextToken()); // START_ARRAY
         assertEquals(0, p.getCurrentLocation().getByteOffset());
         p.close();
     }
 }