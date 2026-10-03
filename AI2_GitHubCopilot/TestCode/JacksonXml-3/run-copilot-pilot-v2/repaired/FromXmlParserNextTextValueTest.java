package com.fasterxml.jackson.dataformat.xml.deser;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;

 import java.io.StringReader;

 import javax.xml.stream.XMLInputFactory;
 import javax.xml.stream.XMLStreamReader;

 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;

 import com.fasterxml.jackson.core.JsonToken;
 import com.fasterxml.jackson.core.io.IOContext;
 import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;

 /**
  * Tests for {@link FromXmlParser#nextTextValue()} focusing on the bug where
  * nextTextValue() returns null instead of the text content when an XML element
  * has attributes followed by text.
  *
  * Bug: nextTextValue() must return the text "7" for an XML element with
  * attributes and text content. The bug is in the token-state transition after
  * attribute-only events not advancing to the text token.
  */
 public class FromXmlParserNextTextValueTest {

     private XMLInputFactory xmlInputFactory;
     private IOContext ioContext;
     private int formatFeatures;

     @Before
     public void setUp() throws Exception {
         xmlInputFactory = XMLInputFactory.newInstance();
         ioContext = new IOContext(null, null, false);
         formatFeatures = FromXmlParser.Feature.collectDefaults();
     }

     @After
     public void tearDown() throws Exception {
         // nothing to tear down for simple tests
     }

     /**
      * Core bug reproduction: XML element with multiple attributes followed
      * by a text child. After advancing past START_OBJECT, nextTextValue()
      * should return the text content "7", not null.
      */
     @Test
     public void testXmlAttributesWithNextTextValue() throws Exception {
         String xml = "<root><element attr1='x' attr2='y'>7</element></root>";
         XMLStreamReader xmlReader = xmlInputFactory.createXMLStreamReader(new StringReader(xml));
         FromXmlParser parser = new FromXmlParser(ioContext, 0, formatFeatures, null, xmlReader);

         // Advance past START_OBJECT (root) and FIELD_NAME (element)
         JsonToken token = parser.nextToken(); // START_OBJECT
         assertEquals(JsonToken.START_OBJECT, token);
         token = parser.nextToken(); // FIELD_NAME
         assertEquals(JsonToken.FIELD_NAME, token);
         assertEquals("element", parser.getCurrentName());

         // Now we are at START_OBJECT for the element with attributes
         token = parser.nextToken();
         assertEquals(JsonToken.START_OBJECT, token);

         // nextTextValue() should return the text content "7"
         String text = parser.nextTextValue();
         assertEquals("7", text);

         parser.close();
     }

     /**
      * Element with a single attribute and text content.
      */
     @Test
     public void testSingleAttributeWithTextValue() throws Exception {
         String xml = "<root><item id='1'>hello</item></root>";
         XMLStreamReader xmlReader = xmlInputFactory.createXMLStreamReader(new StringReader(xml));
         FromXmlParser parser = new FromXmlParser(ioContext, 0, formatFeatures, null, xmlReader);

         assertEquals(JsonToken.START_OBJECT, parser.nextToken());
         assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
         assertEquals("item", parser.getCurrentName());
         assertEquals(JsonToken.START_OBJECT, parser.nextToken());

         String text = parser.nextTextValue();
         assertEquals("hello", text);

         parser.close();
     }

     /**
      * Element with attributes but no text child. nextTextValue() should
      * return null (or empty) since there is no text content.
      */
     @Test
     public void testAttributesNoTextChild() throws Exception {
         String xml = "<root><empty attr1='a' attr2='b'/></root>";
         XMLStreamReader xmlReader = xmlInputFactory.createXMLStreamReader(new StringReader(xml));
         FromXmlParser parser = new FromXmlParser(ioContext, 0, formatFeatures, null, xmlReader);

         assertEquals(JsonToken.START_OBJECT, parser.nextToken());
         assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
         assertEquals("empty", parser.getCurrentName());
         assertEquals(JsonToken.START_OBJECT, parser.nextToken());

         String text = parser.nextTextValue();
         assertNull(text);

         parser.close();
     }

     /**
      * Element with attributes and an empty text node ("").
      * nextTextValue() should return empty string.
      */
     @Test
     public void testAttributesWithEmptyText() throws Exception {
         String xml = "<root><tag attr='v'></tag></root>";
         XMLStreamReader xmlReader = xmlInputFactory.createXMLStreamReader(new StringReader(xml));
         FromXmlParser parser = new FromXmlParser(ioContext, 0, formatFeatures, null, xmlReader);

         assertEquals(JsonToken.START_OBJECT, parser.nextToken());
         assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
         assertEquals("tag", parser.getCurrentName());
         assertEquals(JsonToken.START_OBJECT, parser.nextToken());

         String text = parser.nextTextValue();
         // Empty element with attributes: text should be empty string
         assertEquals("", text);

         parser.close();
     }

     /**
      * Element with text content but no attributes.
      * nextTextValue() should still return the text.
      */
     @Test
     public void testTextOnlyNoAttributes() throws Exception {
         String xml = "<root><value>42</value></root>";
         XMLStreamReader xmlReader = xmlInputFactory.createXMLStreamReader(new StringReader(xml));
         FromXmlParser parser = new FromXmlParser(ioContext, 0, formatFeatures, null, xmlReader);

         assertEquals(JsonToken.START_OBJECT, parser.nextToken());
         assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
         assertEquals("value", parser.getCurrentName());
         assertEquals(JsonToken.START_OBJECT, parser.nextToken());

         String text = parser.nextTextValue();
         assertEquals("42", text);

         parser.close();
     }

     /**
      * Multiple child elements each with attributes and text.
      * Verify nextTextValue() returns correct text for each.
      */
     @Test
     public void testMultipleElementsWithAttributesAndText() throws Exception {
         String xml = "<root><a attr='x'>1</a><b attr='y'>2</b></root>";
         XMLStreamReader xmlReader = xmlInputFactory.createXMLStreamReader(new StringReader(xml));
         FromXmlParser parser = new FromXmlParser(ioContext, 0, formatFeatures, null, xmlReader);

         // root START_OBJECT
         assertEquals(JsonToken.START_OBJECT, parser.nextToken());

         // first child: "a"
         assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
         assertEquals("a", parser.getCurrentName());
         assertEquals(JsonToken.START_OBJECT, parser.nextToken());
         assertEquals("1", parser.nextTextValue());

         // consume END_OBJECT for "a"
         assertEquals(JsonToken.END_OBJECT, parser.nextToken());

         // second child: "b"
         assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
         assertEquals("b", parser.getCurrentName());
         assertEquals(JsonToken.START_OBJECT, parser.nextToken());
         assertEquals("2", parser.nextTextValue());

         parser.close();
     }

     /**
      * Nested elements where inner element has attributes and text.
      * After finishing the inner element's text, verify we can still
      * navigate correctly.
      */
     @Test
     public void testNestedElementWithAttributesAndText() throws Exception {
         String xml = "<root><outer><inner attr='z'>nested-text</inner></outer></root>";
         XMLStreamReader xmlReader = xmlInputFactory.createXMLStreamReader(new StringReader(xml));
         FromXmlParser parser = new FromXmlParser(ioContext, 0, formatFeatures, null, xmlReader);

         // root START_OBJECT
         assertEquals(JsonToken.START_OBJECT, parser.nextToken());

         // outer FIELD_NAME
         assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
         assertEquals("outer", parser.getCurrentName());
         assertEquals(JsonToken.START_OBJECT, parser.nextToken());

         // inner FIELD_NAME
         assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
         assertEquals("inner", parser.getCurrentName());
         assertEquals(JsonToken.START_OBJECT, parser.nextToken());

         // nextTextValue() should return text from inner element
         String text = parser.nextTextValue();
         assertEquals("nested-text", text);

         parser.close();
     }

     /**
      * Whitespace-only text content with attributes.
      * nextTextValue() should return the whitespace text.
      */
     @Test
     public void testAttributesWithWhitespaceText() throws Exception {
         String xml = "<root><space attr='x'>   </space></root>";
         XMLStreamReader xmlReader = xmlInputFactory.createXMLStreamReader(new StringReader(xml));
         FromXmlParser parser = new FromXmlParser(ioContext, 0, formatFeatures, null, xmlReader);

         assertEquals(JsonToken.START_OBJECT, parser.nextToken());
         assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
         assertEquals("space", parser.getCurrentName());
         assertEquals(JsonToken.START_OBJECT, parser.nextToken());

         String text = parser.nextTextValue();
         assertNotNull(text);
         assertEquals("   ", text);

         parser.close();
     }

     /**
      * Element with numeric text content and attributes.
      * Ensures numeric strings are returned as-is.
      */
     @Test
     public void testAttributesWithNumericText() throws Exception {
         String xml = "<root><num attr='a'>-123.45</num></root>";
         XMLStreamReader xmlReader = xmlInputFactory.createXMLStreamReader(new StringReader(xml));
         FromXmlParser parser = new FromXmlParser(ioContext, 0, formatFeatures, null, xmlReader);

         assertEquals(JsonToken.START_OBJECT, parser.nextToken());
         assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
         assertEquals("num", parser.getCurrentName());
         assertEquals(JsonToken.START_OBJECT, parser.nextToken());

         String text = parser.nextTextValue();
         assertEquals("-123.45", text);

         parser.close();
     }

     /**
      * Mixed content: sibling elements, one with attributes+text and one without text.
      * Verify nextTextValue() returns correct values for both.
      */
     @Test
     public void testMixedSiblingsWithAndWithoutText() throws Exception {
         String xml = "<root><hasText attr='a'>data</hasText><noText attr='b'/></root>";
         XMLStreamReader xmlReader = xmlInputFactory.createXMLStreamReader(new StringReader(xml));
         FromXmlParser parser = new FromXmlParser(ioContext, 0, formatFeatures, null, xmlReader);

         assertEquals(JsonToken.START_OBJECT, parser.nextToken());

         // hasText
         assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
         assertEquals("hasText", parser.getCurrentName());
         assertEquals(JsonToken.START_OBJECT, parser.nextToken());
         assertEquals("data", parser.nextTextValue());

         // consume END_OBJECT for hasText
         assertEquals(JsonToken.END_OBJECT, parser.nextToken());

         // noText
         assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
         assertEquals("noText", parser.getCurrentName());
         assertEquals(JsonToken.START_OBJECT, parser.nextToken());
         assertNull(parser.nextTextValue());

         parser.close();
     }

     /**
      * Multiple attributes (more than 2) with text content.
      * Tests that attribute processing does not interfere with text extraction.
      */
     @Test
     public void testManyAttributesWithText() throws Exception {
         String xml = "<root><many a='1' b='2' c='3' d='4'>text-here</many></root>";
         XMLStreamReader xmlReader = xmlInputFactory.createXMLStreamReader(new StringReader(xml));
         FromXmlParser parser = new FromXmlParser(ioContext, 0, formatFeatures, null, xmlReader);

         assertEquals(JsonToken.START_OBJECT, parser.nextToken());
         assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
         assertEquals("many", parser.getCurrentName());
         assertEquals(JsonToken.START_OBJECT, parser.nextToken());

         String text = parser.nextTextValue();
         assertEquals("text-here", text);

         parser.close();
     }
 }
