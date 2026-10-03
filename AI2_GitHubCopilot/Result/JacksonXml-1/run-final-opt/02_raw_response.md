/**

 - JUnit test class for FromXmlParser, targeting the bug described in JacksonXml bug 1 (issue #180):
 - nested unwrapped lists with empty elements lose an expected list entry.
 -
 - The buggy behavior (returning 0 instead of 1 for nested empty unwrapped list elements) is
 - verified via the parser's public API (token stream, context, value extraction) without
 - modifying production classes.
  */

package com.fasterxml.jackson.dataformat.xml.deser;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.Collections;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.util.BufferRecycler;

public class FromXmlParserBug180Test {

 private final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
 private IOContext dummyIOContext;

 @Before
 public void setUp() {
     // Minimal IOContext needed by the parser constructor
     dummyIOContext = new IOContext(new BufferRecycler(), "test", false);
 }

 // ----------------------------------------------------------------------
 // Helper methods
 // ----------------------------------------------------------------------

 private XMLStreamReader staxReaderFor(String xml) throws Exception {
     return xmlInputFactory.createXMLStreamReader(new StringReader(xml));
 }

 /**
  * Creates a parser with no wrapping; inner element will be treated
  * according to default behavior (no virtual wrapping).
  */
 private FromXmlParser parserFor(String xml) throws Exception {
     return new FromXmlParser(dummyIOContext,
             Base64Variants.getDefaultVariant().getParserFeatures(),
             FromXmlParser.Feature.collectDefaults(),
             null, staxReaderFor(xml));
 }

 /**
  * Creates a parser with virtual wrapping enabled for a given set
  * of element names.
  */
 private FromXmlParser wrappedParserFor(String xml, Set<String> wrapNames) throws Exception {
     FromXmlParser parser = parserFor(xml);
     if (wrapNames != null && !wrapNames.isEmpty()) {
         parser.addVirtualWrapping(wrapNames);
     }
     return parser;
 }

 /** Advance past the initial START_OBJECT and return the next token. */
 private JsonToken advanceToFirstContent(FromXmlParser parser) throws Exception {
     JsonToken first = parser.nextToken();
     if (first == JsonToken.START_OBJECT) {
         return parser.nextToken();
     }
     return first;
 }

 /** Consume the full token stream and return the count of tokens of a given type. */
 private int countTokens(FromXmlParser parser, JsonToken type) throws Exception {
     int count = 0;
     while (parser.nextToken() != null) {
         if (parser.getCurrentToken() == type) {
             count++;
         }
     }
     return count;
 }

 // ----------------------------------------------------------------------
 // Tests targeting the buggy condition: empty inner element inside
 // nested unwrapped list loses an entry.
 // ----------------------------------------------------------------------

 /**
  * The core regression test for issue 180 / trigger
  * NestedUnwrappedListsTest.testNestedWithEmpty.
  *
  * XML: wrapper containing a list with one empty inner element.
  * Expected: the list should have 1 entry (empty string or null),
  * but the buggy code drops it, yielding 0.
  */
 @Test
 public void testNestedUnwrappedSingleEmptyChild() throws Exception {
     String xml = "<root><item></item></root>";

     // Without wrapping, normal parsing; empty elements are observed
     FromXmlParser parser = parserFor(xml);
     JsonToken t = advanceToFirstContent(parser);
     assertEquals("Expected FIELD_NAME for 'item'", JsonToken.FIELD_NAME, t);
     // The child element is empty; the parser should report a VALUE_STRING or null
     assertTrue("After FIELD_NAME, next token should be VALUE_STRING or VALUE_NULL for empty
element",
             parser.nextToken() == JsonToken.VALUE_STRING
             || parser.nextToken() == JsonToken.VALUE_NULL);
     // Token for ending the surrounding wrapper
     parser.close();
 }

 /**
  * Second regression trigger: testNestedWithEmpty2 — same concept
  * but with virtual wrapping (namesToWrap) enabled.
  */
 @Test
 public void testNestedUnwrappedEmptyWithVirtualWrapping() throws Exception {
     String xml = "<root><item><sub /></item></root>";
     Set<String> wraps = Collections.singleton("sub");

     FromXmlParser parser = wrappedParserFor(xml, wraps);
     JsonToken t = advanceToFirstContent(parser);
     // Should see FIELD_NAME for the outer 'item'
     assertEquals(JsonToken.FIELD_NAME, t);
     assertEquals("item", parser.getCurrentName());

     // Inside 'item', 'sub' is a wrapped repeated element (list-like)
     JsonToken inner = parser.nextToken();
     // Might be START_OBJECT or START_ARRAY depending on wrapping context
     assertNotNull("Expected a token for the wrapped 'sub'", inner);

     // Regardless, we should not silently lose the entry
     int entries = 0;
     while (parser.getCurrentToken() != null && parser.getCurrentToken() != JsonToken.END_ARRAY) {
         if (parser.getCurrentToken() == JsonToken.VALUE_STRING
                 || parser.getCurrentToken() == JsonToken.VALUE_NULL
                 || parser.getCurrentToken() == JsonToken.START_OBJECT) {
             entries++;
         }
         parser.nextToken();
     }
     // The fix ensures we have at least one entry for the empty wrapped element
     assertTrue("Expected at least 1 entry for empty wrapped element; bug would yield 0", entries >
0);
     parser.close();
 }

 /**
  * The NestedUnwrappedLists180Test trigger: JSON-like nested unwrapped list.
  * Verifies that an empty inner list element still yields a list entry.
  */
 @Test
 public void testNestedUnwrapped180Trigger() throws Exception {
     String xml = "<root><wrapper><list><item /><item>value</item></list></wrapper></root>";
     // 'list' is essentially an array-like element; add wrapping for 'item'
     Set<String> wraps = Collections.singleton("item");
     FromXmlParser parser = wrappedParserFor(xml, wraps);

     // Advance into structure: root -> wrapper -> list
     parser.nextToken(); // skip initial START_OBJECT
     // We'll count VALUE_STRING tokens under the list
     int itemCount = countTokens(parser, JsonToken.VALUE_STRING);

     // The bug manifested as missing the empty <item /> entry
     assertEquals("Expected 2 string tokens: empty first item and 'value' second item",
             2, itemCount);
     parser.close();
 }

 // ----------------------------------------------------------------------
 // isExpectedStartArrayToken behaviour
 // ----------------------------------------------------------------------

 @Test
 public void testIsExpectedStartArrayTokenOnStartObject() throws Exception {
     // With a root element that acts as an array
     String xml = "<list><entry>a</entry><entry>b</entry></list>";
     FromXmlParser parser = parserFor(xml);

     // First token is START_OBJECT
     parser.nextToken();
     assertEquals("Initial token should be START_OBJECT", JsonToken.START_OBJECT,
parser.getCurrentToken());

     // Calling isExpectedStartArrayToken should toggle to START_ARRAY
     assertTrue("isExpectedStartArrayToken should return true", parser.isExpectedStartArrayToken());
     assertEquals("Current token should now be START_ARRAY",
             JsonToken.START_ARRAY, parser.getCurrentToken());
     parser.close();
 }

 @Test
 public void testIsExpectedStartArrayTokenAlreadyArray() throws Exception {
     // When already an array, returns true without changing state
     String xml = "<root />";
     FromXmlParser parser = parserFor(xml);
     parser.nextToken(); // START_OBJECT
     parser.isExpectedStartArrayToken(); // convert to array
     assertTrue("Second call should still return true on START_ARRAY",
             parser.isExpectedStartArrayToken());
     parser.close();
 }

 // ----------------------------------------------------------------------
 // addVirtualWrapping boundaries + _mayBeLeaf interaction
 // ----------------------------------------------------------------------

 @Test
 public void testVirtualWrappingWithEmptyLeafElement() throws Exception {
     // Empty wrapped element should not cause _mayBeLeaf to drop the token
     String xml = "<root><items><entry></entry></items></root>";
     Set<String> wraps = Collections.singleton("entry");
     FromXmlParser parser = wrappedParserFor(xml, wraps);

     parser.nextToken(); // START_OBJECT (root)
     JsonToken peek = parser.nextToken(); // -> should be FIELD_NAME for 'items'
     assertEquals(JsonToken.FIELD_NAME, peek);
     assertEquals("items", parser.getCurrentName());

     // Next we expect START_OBJECT for the 'items' wrapper
     assertEquals(JsonToken.START_OBJECT, parser.nextToken());

     // Inside items, the context should be the wrapper for repeated 'entry'
     // Now call isExpectedStartArrayToken
     assertTrue(parser.isExpectedStartArrayToken());
     assertEquals(JsonToken.START_ARRAY, parser.getCurrentToken());

     // The empty <entry /> should be visible
     JsonToken entryToken = parser.nextToken();
     assertNotNull("empty <entry /> should produce a token", entryToken);
     // Could be VALUE_NULL, VALUE_NULL, or a FIELD_NAME for a contained branch
     // We just verify that the token stream is not prematurely empty
     parser.close();
 }

 // ----------------------------------------------------------------------
 // getCurrentName / getParsingContext consistency
 // ----------------------------------------------------------------------

 @Test
 public void testCurrentNameAfterEmptyElement() throws Exception {
     String xml = "<root><name />value</root>";
     FromXmlParser parser = parserFor(xml);

     parser.nextToken(); // START_OBJECT
     assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
     assertEquals("name", parser.getCurrentName());

     // The empty element token
     JsonToken inner = parser.nextToken();
     assertNotNull(inner);

     // Context depth should account for the empty element
     assertNotNull("Parsing context should not be null after empty element",
             parser.getParsingContext());
     parser.close();
 }

 // ----------------------------------------------------------------------
 // nextTextValue handling around empty elements
 // ----------------------------------------------------------------------

 @Test
 public void testNextTextValueAcrossEmptyWrappedElement() throws Exception {
     String xml = "<root><items><item /><item>hello</item></items></root>";
     Set<String> wraps = Collections.singleton("item");
     FromXmlParser parser = wrappedParserFor(xml, wraps);

     parser.nextToken(); // skip START_OBJECT
     // Navigate to the array content
     while (parser.getCurrentToken() != JsonToken.START_ARRAY && parser.getCurrentToken() != null) {
         parser.nextToken();
     }

     // Use nextTextValue to extract string values
     // Bug would skip the first empty item; fixed version should include it
     String v1 = parser.nextTextValue();
     String v2 = parser.nextTextValue();

     // The empty item -> v1 may be null (or empty, depending on impl);
     // the second should be "hello"
     // The critical assertion: v1 must not be null-in-sense-of-"never emitted"
     // (In practice, nextTextValue returns null when no text is available,
     // but the item itself must be enumerated.)
     // We cannot differentiate between "null because empty" and "null because missing",
     // so we instead verify that subsequent text is correct and the overall count is 2.

     assertNotNull("nextTextValue for empty element must not be null (value present)", v1);
     assertEquals("hello", parser.nextTextValue());
     parser.close();
 }

 // ----------------------------------------------------------------------
 // Boundary: missing end tag
 // ----------------------------------------------------------------------

 @Test
 public void testMissingEndElementThrows() throws Exception {
     // XML with missing closing tag for root
     String xml = "<root><item></item>";
     FromXmlParser parser = parserFor(xml);
     parser.nextToken();

     boolean threw = false;
     try {
         while (parser.nextToken() != null) {
             // exhaust tokens
         }
     } catch (Exception e) {
         threw = true; // expected some parse error
         assertTrue("Exception should contain location info or parse error",
                 e.getMessage().contains("location") || e.getMessage().contains("close")
                 || e.getMessage().contains("EOF"));
     }
     assertTrue("Missing end tag should produce a parse exception", threw);
     parser.close();
 }

 // ----------------------------------------------------------------------
 // Boundary: empty document (no content)
 // ----------------------------------------------------------------------

 @Test
 public void testEmptyDocument() throws Exception {
     String xml = "<root />";
     FromXmlParser parser = parserFor(xml);

     parser.nextToken(); // START_OBJECT
     // No children for root
     JsonToken t = parser.nextToken();
     // The root's end is the document end
     assertTrue("After empty root, token should be null or END_OBJECT; got " + t,
             t == null || t == JsonToken.END_OBJECT);
     parser.close();
 }

 // ----------------------------------------------------------------------
 // Multiple empty elements with virtual wrapping
 // ----------------------------------------------------------------------

 @Test
 public void testMultipleEmptyWrappedElements() throws Exception {
     String xml = "<root><items><item /><item /><item>last</item></items></root>";
     Set<String> wraps = Collections.singleton("item");
     FromXmlParser parser = wrappedParserFor(xml, wraps);

     int entryCount = 0;
     parser.nextToken(); // root start
     while (parser.nextToken() != null) {
         if (parser.getCurrentToken() == JsonToken.VALUE_STRING
                 || parser.getCurrentToken() == JsonToken.VALUE_NULL) {
             entryCount++;
         }
     }
     // Three <item> entries: two empty, one "last"
     assertEquals(3, entryCount);
     parser.close();
 }

}