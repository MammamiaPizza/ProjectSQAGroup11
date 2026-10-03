// Target: com.fasterxml.jackson.core.filter.FilteringParserDelegate (JacksonCore bug 21 / #330)
// Bug: nextToken() returns null after last matched scalar instead of emitting parent END_OBJECT.
// This test class verifies that nextToken() correctly emits structural closing tokens (END_OBJECT,
// END_ARRAY) after a path-matched value, and does not return null prematurely.

package com.fasterxml.jackson.core.filter;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.*;

public class FilteringParserDelegateBug330Test
{
    // Helper to build a parser over given JSON string and create the filter delegate
    private FilteringParserDelegate createDelegate(String json, TokenFilter filter,
                                                    boolean includePath, boolean allowMultiple)
throws IOException
    {
        JsonFactory f = new JsonFactory();
        JsonParser p = f.createParser(json);
        return new FilteringParserDelegate(p, filter, includePath, allowMultiple);
    }

 // ------------------------------------------------------------------------
 // Test 1: Single match in flat object - END_OBJECT must follow value, not null
 // ------------------------------------------------------------------------
 @Test
 public void testEndObjectAfterSingleMatch() throws IOException
 {
     final String JSON = "{\"a\":1,\"b\":2}";
     FilteringParserDelegate parser = createDelegate(JSON, new NameMatchFilter("b"),
             true /* includePath */, false /* single match */);

     // following tokens expected: START_OBJECT, FIELD_NAME("b"), VALUE_NUMBER_INT(2), END_OBJECT
     assertToken(JsonToken.START_OBJECT, parser.nextToken());
     assertToken(JsonToken.FIELD_NAME, parser.nextToken());
     assertEquals("b", parser.getCurrentName());
     assertToken(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
     assertEquals(2, parser.getIntValue());

     // This is the bug trigger: must be END_OBJECT, not null
     JsonToken closing = parser.nextToken();
     assertEquals("Expected END_OBJECT after last matched value, but got null (bug #330 repro)",
             JsonToken.END_OBJECT, closing);

     assertNull("No more tokens expected", parser.nextToken());
 }

 // ------------------------------------------------------------------------
 // Test 2: Last field of an object, single match
 // ------------------------------------------------------------------------
 @Test
 public void testEndObjectWhenMatchIsLastField() throws IOException
 {
     final String JSON = "{\"first\":1,\"target\":42}";
     FilteringParserDelegate parser = createDelegate(JSON, new NameMatchFilter("target"),
             true /* includePath */, false /* single match */);

     assertToken(JsonToken.START_OBJECT, parser.nextToken());
     assertToken(JsonToken.FIELD_NAME, parser.nextToken());
     assertEquals("target", parser.getCurrentName());
     assertToken(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
     assertEquals(42, parser.getIntValue());

     // Closing END_OBJECT is required; null is a bug
     assertEquals("Last field match must still emit END_OBJECT (bug #330)",
             JsonToken.END_OBJECT, parser.nextToken());

     assertNull(parser.nextToken());
 }

 // ------------------------------------------------------------------------
 // Test 3: Single match nested in object -> END_OBJECT x2 expected
 // ------------------------------------------------------------------------
 @Test
 public void testNestedObjectEndTokenSequence() throws IOException
 {
     final String JSON = "{\"inner\":{\"x\":9}}";
     // match field "x"
     FilteringParserDelegate parser = createDelegate(JSON, new NameMatchFilter("x"),
             true /* includePath */, false /* single match */);

     assertToken(JsonToken.START_OBJECT, parser.nextToken());      // root {
     assertToken(JsonToken.FIELD_NAME, parser.nextToken());        // "inner"
     assertEquals("inner", parser.getCurrentName());
     assertToken(JsonToken.START_OBJECT, parser.nextToken());      // {
     assertToken(JsonToken.FIELD_NAME, parser.nextToken());        // "x"
     assertEquals("x", parser.getCurrentName());
     assertToken(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
     assertEquals(9, parser.getIntValue());

     // inner object closing
     assertEquals("Nested END_OBJECT after match required (bug #330)",
             JsonToken.END_OBJECT, parser.nextToken());
     // outer object closing
     assertEquals("Outer END_OBJECT required (bug #330)",
             JsonToken.END_OBJECT, parser.nextToken());

     assertNull(parser.nextToken());
 }

 // ------------------------------------------------------------------------
 // Test 4: Array path match must emit END_ARRAY + END_OBJECT
 // ------------------------------------------------------------------------
 @Test
 public void testEndArrayAfterMatchedScalarInArray() throws IOException
 {
     final String JSON = "{\"list\":[10,20,30]}";
     // match only the value 20 inside array
     FilteringParserDelegate parser = createDelegate(JSON,
             new IndexMatchFilter(1), // match second element (0-based)
             true /* includePath */, false /* single match */);

     assertToken(JsonToken.START_OBJECT, parser.nextToken());
     assertToken(JsonToken.FIELD_NAME, parser.nextToken());
     assertEquals("list", parser.getCurrentName());
     assertToken(JsonToken.START_ARRAY, parser.nextToken());
     assertToken(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
     assertEquals(20, parser.getIntValue());

     // After the matched scalar inside array we must see END_ARRAY, then END_OBJECT
     assertEquals("END_ARRAY required after matched array element (bug #330)",
             JsonToken.END_ARRAY, parser.nextToken());
     assertEquals("END_OBJECT required after END_ARRAY (bug #330)",
             JsonToken.END_OBJECT, parser.nextToken());

     assertNull(parser.nextToken());
 }

 // ------------------------------------------------------------------------
 // Test 5: Mixed structure (object in array in object) → verify all end-tokens
 // ------------------------------------------------------------------------
 @Test
 public void testMixedStructureClosingTokens() throws IOException
 {
     final String JSON = "{\"root\":[{\"id\":\"a\"},{\"id\":\"z\"}]}";
     // match first element's "id" field; path: root → [0] → id
     FirstElementFilter firstElFilter = new FirstElementFilter();
     FilteringParserDelegate parser = createDelegate(JSON, firstElFilter,
             true /* includePath */, false /* single match */);

     assertToken(JsonToken.START_OBJECT, parser.nextToken());       // root {
     assertToken(JsonToken.FIELD_NAME, parser.nextToken());        // "root"
     assertEquals("root", parser.getCurrentName());
     assertToken(JsonToken.START_ARRAY, parser.nextToken());       // [
     assertToken(JsonToken.START_OBJECT, parser.nextToken());      // {
     assertToken(JsonToken.FIELD_NAME, parser.nextToken());        // "id"
     assertEquals("id", parser.getCurrentName());
     assertToken(JsonToken.VALUE_STRING, parser.nextToken());
     assertEquals("a", parser.getText());

     // now expect END_OBJECT (inner), END_ARRAY, END_OBJECT (outer)
     assertEquals("Missing inner END_OBJECT (bug #330)",
             JsonToken.END_OBJECT, parser.nextToken());           // }
     assertEquals("Missing END_ARRAY (bug #330)",
             JsonToken.END_ARRAY, parser.nextToken());            // ]
     assertEquals("Missing outer END_OBJECT (bug #330)",
             JsonToken.END_OBJECT, parser.nextToken());           // }

     assertNull(parser.nextToken());
 }

 // ------------------------------------------------------------------------
 // Test 6: allowMultipleMatches = false: after single scalar match, stream ends (no null)
 // ------------------------------------------------------------------------
 @Test
 public void testExactTokenSequenceForSingleMatchWithPath() throws IOException
 {
     // Replicates key assertion from the trigger test
     final String JSON = "{\"ob\":{\"value\":3}}";
     FilteringParserDelegate parser = createDelegate(JSON, new NameMatchFilter("value"),
             true /* includePath */, false /* single match */);

     assertToken(JsonToken.START_OBJECT, parser.nextToken());
     assertToken(JsonToken.FIELD_NAME, parser.nextToken());
     assertEquals("ob", parser.getCurrentName());
     assertToken(JsonToken.START_OBJECT, parser.nextToken());
     assertToken(JsonToken.FIELD_NAME, parser.nextToken());
     assertEquals("value", parser.getCurrentName());
     assertToken(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
     assertEquals(3, parser.getIntValue());

     // These two END_OBJECT tokens are mandatory; null indicates the bug
     assertEquals("Inner END_OBJECT not emitted (bug #330)",
             JsonToken.END_OBJECT, parser.nextToken());
     assertEquals("Outer END_OBJECT not emitted (bug #330)",
             JsonToken.END_OBJECT, parser.nextToken());

     assertNull(parser.nextToken());
 }

 // ------------------------------------------------------------------------
 // Test 7: skipChildren() must not corrupt END_OBJECT emission afterwards
 // ------------------------------------------------------------------------
 @Test
 public void testSkipChildrenDoesNotSkipEndObject() throws IOException
 {
     // "a" matched, "b" will be skipped; ensure END_OBJECT still emitted.
     final String JSON = "{\"a\":1,\"b\":{\"deep\":true}}";
     FilteringParserDelegate parser = createDelegate(JSON, new NameMatchFilter("a"),
             true /* includePath */, false /* single match */);

     assertToken(JsonToken.START_OBJECT, parser.nextToken());
     assertToken(JsonToken.FIELD_NAME, parser.nextToken());
     assertEquals("a", parser.getCurrentName());
     assertToken(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
     assertEquals(1, parser.getIntValue());

     // skip remaining unmatched content (should internally skip "b"'s object)
     parser.skipChildren();

     assertEquals("END_OBJECT must appear after skipChildren even when only one match (bug #330)",
             JsonToken.END_OBJECT, parser.nextToken());

     assertNull(parser.nextToken());
 }

 // ------------------------------------------------------------------------
 // Test 8: Only root-level array with a single match → END_ARRAY not null
 // ------------------------------------------------------------------------
 @Test
 public void testEndArrayAfterSingleMatchInRootArray() throws IOException
 {
     final String JSON = "[1,2,3]";
     FilteringParserDelegate parser = createDelegate(JSON, new IndexMatchFilter(2),
             true /* includePath */, false /* single match */);

     assertToken(JsonToken.START_ARRAY, parser.nextToken());
     assertToken(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
     assertEquals(3, parser.getIntValue());

     // need END_ARRAY to terminate
     assertEquals("Root array END_ARRAY must be emitted (bug #330)",
             JsonToken.END_ARRAY, parser.nextToken());
     assertNull(parser.nextToken());
 }

 // ------------------------------------------------------------------------
 // Test 9: Token sequence for empty match — still produce closing tokens
 // ------------------------------------------------------------------------
 @Test
 public void testNoValueMatchStillEmitsClosingTokens() throws IOException
 {
     // Filter that never matches, but includePath forces structural tokens through.
     final String JSON = "{\"key\":{\"inner\":7}}";
     FilteringParserDelegate parser = createDelegate(JSON, TokenFilter.INCLUDE_ALL,
             true /* includePath */, false /* single match */);

     // With INCLUDE_ALL and includePath=true, everything should be visible including all END
tokens.
     assertToken(JsonToken.START_OBJECT, parser.nextToken());
     assertToken(JsonToken.FIELD_NAME, parser.nextToken());
     assertToken(JsonToken.START_OBJECT, parser.nextToken());
     assertToken(JsonToken.FIELD_NAME, parser.nextToken());
     assertToken(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
     assertToken(JsonToken.END_OBJECT, parser.nextToken());
     assertToken(JsonToken.END_OBJECT, parser.nextToken());
     assertNull(parser.nextToken());
 }

 // ------------------------------------------------------------------------
 // Test 10: A filter that matches nothing — includePath=false, no tokens at all
 // ------------------------------------------------------------------------
 @Test
 public void testNoPathNoMatchProducesNothing() throws IOException
 {
     // Filter that rejects everything; includePath=false means no structural tokens either.
     final String JSON = "{\"x\":1}";
     FilteringParserDelegate parser = createDelegate(JSON, new RejectAllFilter(),
             false /* includePath */, false /* single match */);

     assertNull("No tokens should be visible for reject-all filter without path inclusion",
             parser.nextToken());
 }

 // ------------------------------------------------------------------------
 // Test 11: Complex nested combination verifying complete brace parity
 // ------------------------------------------------------------------------
 @Test
 public void testNestedArrayInObjectClosingParity() throws IOException
 {
     final String JSON = "{\"data\":[[1],[2,{\"hit\":\"yes\"},3]]}";
     // match the "hit" field value "yes"
     FilteringParserDelegate parser = createDelegate(JSON, new NameMatchFilter("hit"),
             true /* includePath */, false /* single match */);

     assertToken(JsonToken.START_OBJECT, parser.nextToken());      // root {
     assertToken(JsonToken.FIELD_NAME, parser.nextToken());        // "data"
     assertToken(JsonToken.START_ARRAY, parser.nextToken());       // outer [
     assertToken(JsonToken.START_ARRAY, parser.nextToken());       // inner [  (skipped element)
     assertToken(JsonToken.END_ARRAY, parser.nextToken());         // ]
     assertToken(JsonToken.START_ARRAY, parser.nextToken());       // [
     assertToken(JsonToken.START_OBJECT, parser.nextToken());      // {
     assertToken(JsonToken.FIELD_NAME, parser.nextToken());        // "hit"
     assertToken(JsonToken.VALUE_STRING, parser.nextToken());      // "yes"
     assertToken(JsonToken.END_OBJECT, parser.nextToken());        // }
     assertToken(JsonToken.END_ARRAY, parser.nextToken());         // ]
     assertToken(JsonToken.END_ARRAY, parser.nextToken());         // outer ]
     assertToken(JsonToken.END_OBJECT, parser.nextToken());        // root }

     assertNull(parser.nextToken());
 }

 // ------------------------------------------------------------------------
 // Test 12: Repeated call pattern stress — token stream must remain deterministic
 // ------------------------------------------------------------------------
 @Test
 public void testRepeatedNextTokenDoesNotIntroduceNull() throws IOException
 {
     final String JSON = "{\"p\":{\"q\":88}}";
     FilteringParserDelegate parser = createDelegate(JSON, new NameMatchFilter("q"),
             true /* includePath */, false /* single match */);

     // Walk tokens; important assertion is that final token after value is END_OBJECT not null.
     parser.nextToken(); // START_OBJECT
     parser.nextToken(); // FIELD_NAME p
     parser.nextToken(); // START_OBJECT
     parser.nextToken(); // FIELD_NAME q
     parser.nextToken(); // VALUE 88

     JsonToken t = parser.nextToken();
     assertNotNull("First closing token must not be null (bug #330)", t);
     assertEquals(JsonToken.END_OBJECT, t);

     t = parser.nextToken();
     assertNotNull("Second closing token must not be null (bug #330)", t);
     assertEquals(JsonToken.END_OBJECT, t);

     assertNull(parser.nextToken());
 }

 // ------------------------------------------------------------------------
 // Helper assertion & filter implementations
 // ------------------------------------------------------------------------
 private static void assertToken(JsonToken expected, JsonToken actual)
 {
     assertEquals("Unexpected token in token stream", expected, actual);
 }

 /**
  * Selects a single named field anywhere in the tree.
  */
 private static class NameMatchFilter extends TokenFilter
 {
     private final String targetName;

     NameMatchFilter(String target) {
         targetName = target;
     }

     @Override
     public TokenFilter includeProperty(String name) {
         if (targetName.equals(name)) {
             return TokenFilter.INCLUDE_ALL; // match value
         }
         return null; // reject
     }
 }

 /**
  * Matches only the n-th element (0-based) inside a single array level;
  * rejects everything else (other levels, other indices).
  */
 private static class IndexMatchFilter extends TokenFilter
 {
     private final int targetIndex;
     private int currentIndex;

     IndexMatchFilter(int idx) {
         targetIndex = idx;
         currentIndex = 0;
     }

     @Override
     public TokenFilter filterStartArray() {
         // reset for each new array (this simplistic filter resets index once)
         currentIndex = 0;
         return this;
     }

     @Override
     public TokenFilter includeProperty(String name) {
         // not matching by property name
         return null;
     }

     @Override
     public boolean includeValue(JsonParser p) throws IOException {
         // include if we are at target index
         return (currentIndex++ == targetIndex);
     }
 }

 /**
  * Special filter to match only the first element of an array inside
  * the field "root", and only the "id" property of that element.
  */
 private static class FirstElementFilter extends TokenFilter
 {
     private boolean rootSeen = false;
     private boolean firstElement = false;

     @Override
     public TokenFilter includeProperty(String name) {
         if ("root".equals(name)) {
             rootSeen = true;
             return this; // descend into "root"
         }
         if (firstElement && "id".equals(name)) {
             return TokenFilter.INCLUDE_ALL; // include the "id" value
         }
         return null; // reject others
     }

     @Override
     public TokenFilter filterStartArray() {
         if (rootSeen) {
             // This is the array value of "root"; include only first element.
             firstElement = true;
             return new FirstElementOnlyFilter();
         }
         return null;
     }
 }

 /**
  * Allows only the first element of an array (rejects others via skip).
  */
 private static class FirstElementOnlyFilter extends TokenFilter
 {
     private int count = 0;

     @Override
     public boolean includeValue(JsonParser p) {
         return (count++ == 0);
     }

     @Override
     public TokenFilter includeProperty(String name) {
         // within the first element, include all properties
         return (count == 1) ? TokenFilter.INCLUDE_ALL : null;
     }
 }

 /**
  * Filter that unconditionally rejects every value and property.
  */
 private static class RejectAllFilter extends TokenFilter
 {
     @Override
     public TokenFilter includeProperty(String name) {
         return null;
     }

     @Override
     public boolean includeValue(JsonParser p) {
         return false;
     }
 }

}