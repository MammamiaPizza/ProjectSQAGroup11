package com.fasterxml.jackson.dataformat.xml.deser;

 import static org.junit.Assert.*;

 import java.io.StringReader;
 import org.junit.Test;

 import com.fasterxml.jackson.core.JsonToken;
 import com.fasterxml.jackson.dataformat.xml.XmlFactory;

 public class FromXmlParserNextTextValueTest {

     private FromXmlParser createParser(String xml) throws Exception {
         return (FromXmlParser) new XmlFactory().createParser(new StringReader(xml));
     }

     private void assertToken(JsonToken expected, JsonToken actual) {
         assertEquals("Expected token " + expected + " but was " + actual, expected, actual);
     }

     @Test
     public void testNextTextValueAfterAttribute() throws Exception {
         String xml = "<root><child attr='val'>7</child></root>";
         FromXmlParser p = createParser(xml);
         assertToken(JsonToken.START_OBJECT, p.nextToken());
         assertToken(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals("child", p.getCurrentName());
         assertToken(JsonToken.START_OBJECT, p.nextToken());
         assertToken(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals("attr", p.getCurrentName());
         assertToken(JsonToken.VALUE_STRING, p.nextToken());
         assertEquals("val", p.getText());
         String text = p.nextTextValue();
         assertEquals("7", text);
         assertEquals(JsonToken.VALUE_STRING, p.getCurrentToken());
         assertEquals("7", p.getText());
     }

     @Test
     public void testNextTextValueMultipleAttributes() throws Exception {
         String xml = "<root><child attr1='a' attr2='b'>7</child></root>";
         FromXmlParser p = createParser(xml);
         p.nextToken(); // START_OBJECT
         p.nextToken(); // FIELD_NAME child
         p.nextToken(); // START_OBJECT
         p.nextToken(); // FIELD_NAME attr1
         p.nextToken(); // VALUE_STRING a
         p.nextToken(); // FIELD_NAME attr2
         p.nextToken(); // VALUE_STRING b
         assertEquals("7", p.nextTextValue());
     }

     @Test
     public void testNextTextValueNoAttribute() throws Exception {
         String xml = "<root><child>7</child></root>";
         FromXmlParser p = createParser(xml);
         p.nextToken(); // START_OBJECT
         p.nextToken(); // FIELD_NAME child
         p.nextToken(); // START_OBJECT
         assertEquals("7", p.nextTextValue());
         assertEquals(JsonToken.VALUE_STRING, p.getCurrentToken());
     }

     @Test
     public void testNextTextValueEmptyElement() throws Exception {
         String xml = "<root><child attr='val'></child></root>";
         FromXmlParser p = createParser(xml);
         p.nextToken(); // START_OBJECT
         p.nextToken(); // FIELD_NAME child
         p.nextToken(); // START_OBJECT
         p.nextToken(); // FIELD_NAME attr
         p.nextToken(); // VALUE_STRING val
         assertNull(p.nextTextValue());
         assertEquals(JsonToken.END_OBJECT, p.getCurrentToken());
     }

     @Test
     public void testNextTextValueCData() throws Exception {
         String xml = "<root><child attr='val'><![CDATA[7]]></child></root>";
         FromXmlParser p = createParser(xml);
         p.nextToken(); // START_OBJECT
         p.nextToken(); // FIELD_NAME child
         p.nextToken(); // START_OBJECT
         p.nextToken(); // FIELD_NAME attr
         p.nextToken(); // VALUE_STRING val
         assertEquals("7", p.nextTextValue());
     }

     @Test
     public void testNextTextValueLargeText() throws Exception {
         StringBuilder sb = new StringBuilder();
         for (int i = 0; i < 1000; i++) sb.append("a");
         String large = sb.toString();
         String xml = "<root><child>" + large + "</child></root>";
         FromXmlParser p = createParser(xml);
         p.nextToken(); p.nextToken(); p.nextToken(); // to START_OBJECT of child
         assertEquals(large, p.nextTextValue());
     }

     @Test
     public void testNextTextValueAfterFieldName() throws Exception {
         String xml = "<root><child>7</child></root>";
         FromXmlParser p = createParser(xml);
         p.nextToken(); // START_OBJECT
         p.nextToken(); // FIELD_NAME child
         p.nextToken(); // START_OBJECT
         assertToken(JsonToken.FIELD_NAME, p.nextToken());
         assertEquals("7", p.nextTextValue());
     }

     @Test
     public void testNextTextValueOnNonTextToken() throws Exception {
         String xml = "<root><child attr='val'>7</child></root>";
         FromXmlParser p = createParser(xml);
         // at START_OBJECT (initial token after construction is not guaranteed to be start? we
advance a bit)
         p.nextToken(); // START_OBJECT (root)
         assertNull(p.nextTextValue()); // current=root START_OBJECT, not text
         // now navigate through, then call on non-text
         while (p.nextToken() != null) {}
         assertNull(p.nextTextValue()); // at end of stream
     }

     @Test
     public void testNextTextValueWithSpecialChars() throws Exception {
         String xml = "<root><child attr='val'>&lt;7&gt;</child></root>";
         FromXmlParser p = createParser(xml);
         p.nextToken(); p.nextToken(); p.nextToken(); // root FIELD_NAME START_OBJECT child
         p.nextToken(); p.nextToken(); // attr pair
         assertEquals("<7>", p.nextTextValue());
     }
 }