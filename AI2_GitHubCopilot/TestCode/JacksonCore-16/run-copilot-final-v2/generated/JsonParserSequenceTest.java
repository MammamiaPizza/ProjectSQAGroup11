package com.fasterxml.jackson.core.util;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;

 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.List;

 import org.junit.Test;

 import com.fasterxml.jackson.core.JsonFactory;
 import com.fasterxml.jackson.core.JsonParser;
 import com.fasterxml.jackson.core.JsonToken;

 public class JsonParserSequenceTest {

     private final JsonFactory FACTORY = new JsonFactory();

     private JsonParser parser(String json) throws IOException {
         return FACTORY.createParser(json);
     }

     // --- core bug: count should match flattened leaf count -----------------

     @Test
     public void testFlattenedContainedParsersCount() throws IOException {
         JsonParser p1 = parser("1");
         JsonParser p2 = parser("2");
         JsonParserSequence seq = JsonParserSequence.createFlattened(p1, p2);
         // Bug triggers: expected 2, actual 3
         assertEquals(2, seq.containedParsersCount());
         seq.close();
     }

     // --- token ordering ----------------------------------------------------

     @Test
     public void testFlattenedTokenOrder() throws IOException {
         JsonParser p1 = parser("[1,2]");   // tokens: [ , 1 , 2 , ]
         JsonParser p2 = parser("true");   // token: true
         JsonParserSequence seq = JsonParserSequence.createFlattened(p1, p2);

         assertEquals(JsonToken.START_ARRAY, seq.nextToken());   // from p1
         assertEquals(JsonToken.VALUE_NUMBER_INT, seq.nextToken()); // 1
         assertEquals(JsonToken.VALUE_NUMBER_INT, seq.nextToken()); // 2
         assertEquals(JsonToken.END_ARRAY, seq.nextToken());       // ]
         assertEquals(JsonToken.VALUE_TRUE, seq.nextToken());      // from p2
         assertNull(seq.nextToken());
         seq.close();
     }

     // --- flattening with first argument being a sequence -------------------

     @Test
     public void testFlattenedWithFirstNested() throws IOException {
         JsonParser p1 = parser("1");
         JsonParser p2 = parser("2");
         JsonParser p3 = parser("3");
         JsonParserSequence inner = JsonParserSequence.createFlattened(p1, p2);
         JsonParserSequence outer = JsonParserSequence.createFlattened(inner, p3);

         assertEquals(3, outer.containedParsersCount());

         JsonToken[] tokens = { JsonToken.VALUE_NUMBER_INT,
                                JsonToken.VALUE_NUMBER_INT,
                                JsonToken.VALUE_NUMBER_INT };
         for (JsonToken expected : tokens) {
             assertEquals(expected, outer.nextToken());
         }
         assertNull(outer.nextToken());
         outer.close();
     }

     // --- flattening with second argument being a sequence ------------------

     @Test
     public void testFlattenedWithSecondNested() throws IOException {
         JsonParser p1 = parser("1");
         JsonParser p2 = parser("2");
         JsonParser p3 = parser("3");
         JsonParserSequence inner = JsonParserSequence.createFlattened(p2, p3);
         JsonParserSequence outer = JsonParserSequence.createFlattened(p1, inner);

         assertEquals(3, outer.containedParsersCount());

         JsonToken[] tokens = { JsonToken.VALUE_NUMBER_INT,
                                JsonToken.VALUE_NUMBER_INT,
                                JsonToken.VALUE_NUMBER_INT };
         for (JsonToken expected : tokens) {
             assertEquals(expected, outer.nextToken());
         }
         assertNull(outer.nextToken());
         outer.close();
     }

     // --- both arguments sequences ------------------------------------------

     @Test
     public void testFlattenedWithBothNested() throws IOException {
         JsonParser p1 = parser("1"), p2 = parser("2");
         JsonParser p3 = parser("3"), p4 = parser("4");
         JsonParserSequence seq1 = JsonParserSequence.createFlattened(p1, p2);
         JsonParserSequence seq2 = JsonParserSequence.createFlattened(p3, p4);
         JsonParserSequence seq = JsonParserSequence.createFlattened(seq1, seq2);

         assertEquals(4, seq.containedParsersCount());

         for (int i = 0; i < 4; i++) {
             assertEquals(JsonToken.VALUE_NUMBER_INT, seq.nextToken());
         }
         assertNull(seq.nextToken());
         seq.close();
     }

     // --- deep nesting ------------------------------------------------------

     @Test
     public void testFlattenedDeepNesting() throws IOException {
         JsonParser a = parser("1"), b = parser("2"), c = parser("3"), d = parser("4");
         JsonParserSequence level1 = JsonParserSequence.createFlattened(a, b);
         JsonParserSequence level2 = JsonParserSequence.createFlattened(level1, c);
         JsonParserSequence root = JsonParserSequence.createFlattened(level2, d);

         assertEquals(4, root.containedParsersCount());

         for (int i = 0; i < 4; i++) {
             assertEquals(JsonToken.VALUE_NUMBER_INT, root.nextToken());
         }
         assertNull(root.nextToken());
         root.close();
     }

     // --- addFlattenedActiveParsers direct test -----------------------------

     @Test
     public void testAddFlattenedActiveParsersOnPlainSequence() throws IOException {
         JsonParser p1 = parser("1");
         JsonParser p2 = parser("2");
         // construct via createFlattened (non-nested case) to get a sequence
         JsonParserSequence seq = JsonParserSequence.createFlattened(p1, p2);

         List<JsonParser> collected = new ArrayList<>();
         seq.addFlattenedActiveParsers(collected);
         assertEquals(2, collected.size());
         seq.close();
     }

     @Test
     public void testAddFlattenedActiveParsersAfterNesting() throws IOException {
         JsonParser a = parser("a"), b = parser("b"), c = parser("c");
         JsonParserSequence inner = JsonParserSequence.createFlattened(a, b);
         JsonParserSequence outer = JsonParserSequence.createFlattened(inner, c);

         List<JsonParser> collected = new ArrayList<>();
         outer.addFlattenedActiveParsers(collected);
         assertEquals(3, collected.size());
         outer.close();
     }

     // --- exhaustion --------------------------------------------------------

     @Test
     public void testNextTokenReturnsNullAfterExhaustion() throws IOException {
         JsonParser p1 = parser("1");
         JsonParserSequence seq = JsonParserSequence.createFlattened(p1, parser("2"));
         assertNotNull(seq.nextToken()); // 1
         assertNotNull(seq.nextToken()); // 2
         assertNull(seq.nextToken());
         assertNull(seq.nextToken()); // still null
         seq.close();
     }

     // --- close delegates ---------------------------------------------------

     @Test
     public void testCloseClosesAllDelegates() throws IOException {
         // use custom parsers that track close call
         CloseTrackingParser p1 = new CloseTrackingParser(parser("1"));
         CloseTrackingParser p2 = new CloseTrackingParser(parser("2"));
         JsonParserSequence seq = JsonParserSequence.createFlattened(p1, p2);

         assertFalse(p1.closed);
         assertFalse(p2.closed);
         seq.close();
         assertTrue(p1.closed);
         assertTrue(p2.closed);
     }

     // --- switchToNext boundary ---------------------------------------------

     @Test
     public void testSwitchToNextReturnsFalseWhenNoMoreParsers() throws IOException {
         JsonParser p1 = parser("1");
         JsonParserSequence seq = JsonParserSequence.createFlattened(p1, parser("2"));

         assertTrue(seq.switchToNext()); // moves to second
         assertTrue(seq.switchToNext()); // moves beyond? actually there are only 2 parsers,
delegate becomes second, then switchToNext moves to third? But there is no third, so returns false.
         // adjust: after first switch, delegate=second. second switch should fail.
         // So we need to verify: first call succeeds, second call fails.
         assertFalse(seq.switchToNext());
         seq.close();
     }

     // --- empty / no-token parser -------------------------------------------

     @Test
     public void testFlattenedWithEmptyParser() throws IOException {
         JsonParser empty = parser(""); // no tokens
         JsonParser p = parser("1");
         JsonParserSequence seq = JsonParserSequence.createFlattened(empty, p);

         assertEquals(JsonToken.VALUE_NUMBER_INT, seq.nextToken());
         assertNull(seq.nextToken());
         seq.close();
     }

     // --- helper -----------------------------------------------------------------

     static class CloseTrackingParser extends JsonParserDelegate {
         boolean closed;
         CloseTrackingParser(JsonParser delegate) { super(delegate); }
         @Override public void close() throws IOException {
             super.close();
             closed = true;
         }
     }
 }
