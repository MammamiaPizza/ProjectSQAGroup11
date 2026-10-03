package com.fasterxml.jackson.dataformat.xml.stream;

import static org.junit.Assert.*;

import java.io.StringReader;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class FromXmlParserAttributeTest {

  @Test
  public void testNextTextValueReturnsAttributeValue() throws Exception {
      String xml = "<root a='7'/>";
      XmlMapper mapper = new XmlMapper();
      JsonParser parser = mapper.getFactory().createParser(new StringReader(xml));
      assertEquals(JsonToken.START_OBJECT, parser.nextToken());
      assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
      assertEquals("a", parser.getCurrentName());
      String value = parser.nextTextValue();
      assertEquals("7", value);
      assertEquals(JsonToken.VALUE_STRING, parser.currentToken());
      assertEquals("7", parser.getText());
  }

  @Test
  public void testNextTextValueReturnsNullBeforeFieldName() throws Exception {
      String xml = "<root a='7'/>";
      XmlMapper mapper = new XmlMapper();
      JsonParser parser = mapper.getFactory().createParser(new StringReader(xml));
      assertEquals(JsonToken.START_OBJECT, parser.nextToken());
      String value = parser.nextTextValue();
      assertNull(value);
      assertEquals(JsonToken.FIELD_NAME, parser.currentToken());
  }

  @Test
  public void testGetTextReturnsAttributeValue() throws Exception {
      String xml = "<root a='77'/>";
      XmlMapper mapper = new XmlMapper();
      JsonParser parser = mapper.getFactory().createParser(new StringReader(xml));
      assertEquals(JsonToken.START_OBJECT, parser.nextToken());
      assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
      assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
      assertEquals("77", parser.getText());
  }

  @Test
  public void testGetCurrentNameReturnsAttributeName() throws Exception {
      String xml = "<root a='7'/>";
      XmlMapper mapper = new XmlMapper();
      JsonParser parser = mapper.getFactory().createParser(new StringReader(xml));
      assertEquals(JsonToken.START_OBJECT, parser.nextToken());
      assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
      assertEquals("a", parser.getCurrentName());
  }

  @Test
  public void testEmptyAttributeValue() throws Exception {
      String xml = "<root a=''/>";
      XmlMapper mapper = new XmlMapper();
      JsonParser parser = mapper.getFactory().createParser(new StringReader(xml));
      assertEquals(JsonToken.START_OBJECT, parser.nextToken());
      assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
      String value = parser.nextTextValue();
      assertEquals("", value);
  }

  @Test
  public void testAttributeWithSpaces() throws Exception {
      String xml = "<root a=' hello '  b=' world ' />";
      XmlMapper mapper = new XmlMapper();
      JsonParser parser = mapper.getFactory().createParser(new StringReader(xml));
      assertEquals(JsonToken.START_OBJECT, parser.nextToken());
      // first attribute
      assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
      assertEquals("a", parser.getCurrentName());
      assertEquals(" hello ", parser.nextTextValue());
      // second attribute
      assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
      assertEquals("b", parser.getCurrentName());
      assertEquals(" world ", parser.nextTextValue());
  }

  @Test
  public void testMultipleAttributesSequentialNextTextValue() throws Exception {
      String xml = "<root a='1' b='2'/>";
      XmlMapper mapper = new XmlMapper();
      JsonParser parser = mapper.getFactory().createParser(new StringReader(xml));
      assertEquals(JsonToken.START_OBJECT, parser.nextToken());

      // consume first attribute name and value
      assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
      assertEquals("a", parser.getCurrentName());
      assertEquals("1", parser.nextTextValue());

      // second attribute
      assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
      assertEquals("b", parser.getCurrentName());
      assertEquals("2", parser.nextTextValue());
  }

  @Test
  public void testNextTextValueForTextElement() throws Exception {
      String xml = "<root>xyz</root>";
      XmlMapper mapper = new XmlMapper();
      JsonParser parser = mapper.getFactory().createParser(new StringReader(xml));
      assertEquals(JsonToken.START_OBJECT, parser.nextToken());
      assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
      // Next token should be VALUE_STRING containing "xyz"
      assertNotNull(parser.nextTextValue());
      assertEquals("xyz", parser.getText());
  }

  @Test
  public void testNextTextValueReturnsNullAfterTextElementConsumed() throws Exception {
      String xml = "<root>xyz</root>";
      XmlMapper mapper = new XmlMapper();
      JsonParser parser = mapper.getFactory().createParser(new StringReader(xml));
      assertEquals(JsonToken.START_OBJECT, parser.nextToken());
      // consume property name and string value
      assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
      assertEquals("xyz", parser.nextTextValue());
      // now no more VALUE_STRING ahead
      assertNull(parser.nextTextValue());
  }

  @Test
  public void testNextTextValueReturnsNullAtEndObject() throws Exception {
      String xml = "<root/>";
      XmlMapper mapper = new XmlMapper();
      JsonParser parser = mapper.getFactory().createParser(new StringReader(xml));
      assertNotNull(parser.nextToken()); // START_OBJECT
      assertNotNull(parser.nextToken()); // END_OBJECT
      assertNull(parser.nextTextValue());
  }

  @Test
  public void testNextTextValueWhenNextTokenIsFieldName() throws Exception {
      // after consuming a value, next token is FIELD_NAME of another attribute
      String xml = "<root a='7' b='8'/>";
      XmlMapper mapper = new XmlMapper();
      JsonParser parser = mapper.getFactory().createParser(new StringReader(xml));
      assertEquals(JsonToken.START_OBJECT, parser.nextToken());
      // consume first attribute value
      assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
      assertEquals("7", parser.nextTextValue());
      // now current token is VALUE_STRING; nextTextValue() will advance to FIELD_NAME and return
null
      assertNull(parser.nextTextValue());
      assertEquals(JsonToken.FIELD_NAME, parser.currentToken());
      assertEquals("b", parser.getCurrentName());
  }

  @Test
  public void testNextTextValueReturnsValueForAttributeWithoutExplicitNameAdvance() throws Exception
{
      // variation: after FIELD_NAME, nextTextValue() should directly give the value
      String xml = "<root a='9'/>";
      XmlMapper mapper = new XmlMapper();
      JsonParser parser = mapper.getFactory().createParser(new StringReader(xml));
      assertEquals(JsonToken.START_OBJECT, parser.nextToken());
      assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
      assertEquals("a", parser.getCurrentName());
      String v = parser.nextTextValue(); // should be "9"
      assertEquals("9", v);
      assertEquals(JsonToken.VALUE_STRING, parser.currentToken());
  }

 }
