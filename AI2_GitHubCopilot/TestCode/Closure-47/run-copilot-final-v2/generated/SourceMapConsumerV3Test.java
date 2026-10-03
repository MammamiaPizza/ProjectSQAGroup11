package com.google.debugging.sourcemap;

 import static org.junit.Assert.*;

 import com.google.debugging.sourcemap.proto.Mapping.OriginalMapping;
 import org.json.JSONArray;
 import org.json.JSONException;
 import org.json.JSONObject;
 import org.junit.Test;

 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.List;

 /**
  * Tests for SourceMapConsumerV3 and SourceMap to expose Closure bug 47
  * (off-by-one mapping count, section merge issues).
  */
 public class SourceMapConsumerV3Test {

   // --- Helpers ---

   private String minimalValidSourceMapJson(String mappings, int lineCount) {
     return "{" +
         "\"version\":3," +
         "\"file\":\"test.js\"," +
         "\"lineCount\":" + lineCount + "," +
         "\"mappings\":\"" + mappings + "\"," +
         "\"sources\":[\"test.js\"]," +
         "\"names\":[]" +
         "}";
   }

   private SourceMapConsumerV3 parseJson(String json) throws SourceMapParseException {
     SourceMapConsumerV3 consumer = new SourceMapConsumerV3();
     consumer.parse(json, (SourceMapSupplier) null);
     return consumer;
   }

   // --- SourceMapConsumerV3.getMappingForLine ---

   @Test
   public void testGetMappingForLineLastLineNotLost() throws Exception {
     // Two lines, each one entry; the last line must be stored.
     String json = minimalValidSourceMapJson("AAAAA;AAAAA", 2);
     SourceMapConsumerV3 consumer = parseJson(json);

     OriginalMapping m0 = consumer.getMappingForLine(0, 0);
     OriginalMapping m1 = consumer.getMappingForLine(1, 0);
     assertNotNull("First line mapping missing", m0);
     assertNotNull("Last line mapping missing – off-by-one bug", m1);
   }

   @Test
   public void testGetMappingForLineUnmappedReturnsNull) throws Exception {
     // A line with no entries (null line) should return null.
     String json = minimalValidSourceMapJson("AAAAA;", 2); // second line empty
     SourceMapConsumerV3 consumer = parseJson(json);

     OriginalMapping m0 = consumer.getMappingForLine(0, 0);
     OriginalMapping m1 = consumer.getMappingForLine(1, 0);
     assertNotNull(m0);
     assertNull("Empty line should have no mapping", m1);
   }

   @Test
   public void testGetMappingForLineOutOfBounds) throws Exception {
     String json = minimalValidSourceMapJson("AAAAA", 1);
     SourceMapConsumerV3 consumer = parseJson(json);
     assertNull(consumer.getMappingForLine(1, 0));
     assertNull(consumer.getMappingForLine(-1, 0));
   }

   @Test
   public void testGetMappingForColumnOutOfRange) throws Exception {
     String json = minimalValidSourceMapJson("AAAAA", 1);
     SourceMapConsumerV3 consumer = parseJson(json);
     // Entry at column 0, so column 1 should be unmapped.
     assertNotNull(consumer.getMappingForLine(0, 0));
     assertNull("Column beyond mapped range should return null", consumer.getMappingForLine(0, 1));
   }

   // --- SourceMapConsumerV3.sections / meta-map ---

   @Test
   public void testSectionMergingTotalLineCount) throws Exception {
     String indexJson = "{\" +
         "\"version\":3," +
         "\"file\":\"merged.js\"," +
         "\"lineCount\":2," +
         "\"sections\":[" +
         "{\"url\":\"s1\",\"map\":{" +
         "\"version\":3,\"file\":\"a.js\",\"lineCount\":1," +
         "\"mappings\":\"AAAAA\"," +
         "\"sources\":[\"a.js\"],\"names\":[]}}," +
         "{\"url\":\"s2\",\"map\":{" +
         "\"version\":3,\"file\":\"b.js\",\"lineCount\":1," +
         "\"mappings\":\"AAAAA\"," +
         "\"sources\":[\"b.js\"],\"names\":[]}}," +
         "]}";

     SourceMapConsumerV3 consumer = new SourceMapConsumerV3();
     consumer.parse(indexJson, new SourceMapSupplier() {
       @Override
       public String getSourceMap(String url) {
         if ("s1".equals(url)) {
           return "{\"version\":3,\"file\":\"a.js\",\"lineCount\":1,"+
                  "\"mappings\":\"AAAAA\",\"sources\":[\"a.js\"],\"names\":[]}";
         } else if ("s2".equals(url))) {
           return "{\"version\":3,\"file\":\"b.js\",\"lineCount\":1,"+
                  "\"mappings\":\"AAAAA\",\"sources\":[\"b.js\"],\"names\":[]}";
         }
         return null;
       }
     });

     // Both sections should produce exactly 2 mapped lines.
     assertNotNull(consumer.getMappingForLine(0, 0));
     assertNotNull(consumer.getMappingForLine(1, 0));
     assertNull(consumer.getMappingForLine(2, 0));
   }

   @Test
   public void testMergedMappingsCount) throws Exception {
     // First section provides 2 lines, second provides 1 line → total 3.
     String indexJson = "{\"version\":3,\"file\":\"merged.js\"," +
         "\"sections\":[" +
         "{\"url\":\"s1\",\"map\":{" +
         "\"version\":3,\"file\":\"a.js\",\"lineCount\":2," +
         "\"mappings\":\"AAAAA;AAAAA\"," +
         "\"sources\":[\"a.js\"],\"names\":[]}}," +
         "{\"url\":\"s2\",\"map\":{" +
         "\"version\":3,\"file\":\"b.js\",\"lineCount\":1," +
         "\"mappings\":\"AAAAA\"," +
         "\"sources\":[\"b.js\"],\"names\":[]}}," +
         "]}";

     SourceMapConsumerV3 consumer = new SourceMapConsumerV3();
     final int[] callCount = {0};
     consumer.parse(indexJson, new SourceMapSupplier() {
       @Override
       public String getSourceMap(String url) {
         if ("s1".equals(url)) {
           return "{\"version\":3,\"file\":\"a.js\",\"lineCount\":2," +
                  "\"mappings\":\"AAAAA;AAAAA\",\"sources\":[\"a.js\"],\"names\":[]}";
         } else if ("s2".equals(url)) {
           return "{\"version\":3,\"file\":\"b.js\",\"lineCount\":1," +
                  "\"mappings\":\"AAAAA\",\"sources\":[\"b.js\"],\"names\":[]}";
         }
         return null;
       }
     });

     // The bug could drop the last line of the last section (count=2 instead of 3).
     assertNotNull(consumer.getMappingForLine(0, 0));
     assertNotNull(consumer.getMappingForLine(1, 0));
     assertNotNull("Third line of merged map should exist", consumer.getMappingForLine(2, 0));
     assertNull(consumer.getMappingForLine(3, 0));
   }

   // --- visitMappings ---

   @Test
   public void testVisitMappingsTotalCount) throws Exception {
     String json = minimalValidSourceMapJson("AAAAA;AAAAA;AAAAA", 3);
     SourceMapConsumerV3 consumer = parseJson(json);

     final int[] count = {0};
     consumer.visitMappings(new SourceMapConsumerV3.EntryVisitor() {
       @Override
        public void notice(SourceMapConsumerV3.Entry entry) {
         count[0]++;
       }
     });

     assertEquals("All 3 entries should be visited", 3, count[0]);
   }

   // --- reverse mapping ---

   @Test
   public void testReverseMappingBasic) throws Exception {
     String json = "{\"version\":3,\"file\":\"test.js\",\"lineCount\":1," +
                   "\"mappings\":\"AAAAA\"," +
                   "\"sources\":[\"src.js\"],\"names\":[]}";
     SourceMapConsumerV3 consumer = parseJson(json);

     Collection<OriginalMapping> mappings =
         consumer.getReverseMapping("src.js", 0, 0);
     assertNotNull("Reverse mapping should not be null", mappings);
     assertFalse("Should have at least one reverse mapping", mappings.isEmpty());
   }

   // --- parse errors ---

   @Test(expected = SourceMapParseException.class)
   public void testParseInvalidJsonThrows) throws Exception {
     parseJson("not json");
   }

   @Test(expected = SourceMapParseException.class)
   public void testParseWrongVersionThrows) throws Exception {
     String json = "{\"version\":4,\"file\":\"test.js\",\"lineCount\":1," +
                   "\"mappings\":\"AAAAA\",\"sources\":[\"test.js\"],\"names\":[]}";
     parseJson(json);
   }

   @Test(expected = SourceMapParseException.class)
   public void testParseMissingFileThrows) throws Exception {
     String json = "{\"version\":3,\"file\":\"\",\"lineCount\":1," +
                   "\"mappings\":\"AAAAA\",\"sources\":[\"test.js\"],\"names\":[]}";
     parseJson(json);
   }

   // --- SourceMap appendTo / reset sanity ---

   @Test
   public void testSourceMapAppendToDoesNotThrow) throws Exception {
     // Just verify that a default SourceMap can be serialised without errors.
     SourceMap sm = SourceMap.Format.DEFAULT.getInstance();
     StringBuilder sb = new StringBuilder();
     sm.appendTo(sb, "test");
     assertTrue("Serialised output should contain version", sb.toString().contains("\"version\":"));
     sb.setLength(0);
     sm.reset();
     sm.appendTo(sb, "test2");
     assertTrue("Should produce valid map after reset", sb.toString().contains("\"version\":"));
   }
 }
