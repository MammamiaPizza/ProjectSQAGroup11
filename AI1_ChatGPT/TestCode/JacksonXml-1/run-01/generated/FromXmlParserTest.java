package com.fasterxml.jackson.dataformat.xml.deser;

import java.nio.charset.Charset;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import static org.junit.Assert.*;

public class FromXmlParserTest
{
    private FromXmlParser parserFor(String xml) throws Exception {
        XmlMapper mapper = new XmlMapper();
        return (FromXmlParser) mapper.getFactory().createParser(xml);
    }

    @Test
    public void testBasicEmptyElementTokenSequence() throws Exception {
        FromXmlParser parser = parserFor("<root/>");

        assertNull(parser.getText());
        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
        assertEquals("root", parser.getCurrentName());
        assertEquals("root", parser.getText());

        assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
        assertEquals(JsonToken.VALUE_NULL.asString(), parser.getText());
        assertNull(parser.nextToken());
    }

    @Test
    public void testNextTextValueReturnsEmptyStringForEmptyElement() throws Exception {
        FromXmlParser parser = parserFor("<root/>");

        assertNull(parser.nextTextValue());
        assertEquals(JsonToken.START_OBJECT, parser.getCurrentToken());

        assertNull(parser.nextTextValue());
        assertEquals(JsonToken.FIELD_NAME, parser.getCurrentToken());
        assertEquals("root", parser.getCurrentName());

        assertEquals("", parser.nextTextValue());
        assertEquals(JsonToken.VALUE_STRING, parser.getCurrentToken());
        assertEquals("", parser.getText());
        assertNull(parser.nextTextValue());
    }

    @Test
    public void testAttributesAndTextAreExposedAsObjectProperties() throws Exception {
        FromXmlParser parser = parserFor("<root attribute=\"value\">text</root>");
        parser.setXMLTextElementName("content");

        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
        assertEquals("root", parser.getCurrentName());

        assertEquals(JsonToken.START_OBJECT, parser.nextToken());

        assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
        assertEquals("attribute", parser.getCurrentName());
        assertEquals("attribute", parser.getText());

        assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        assertEquals("value", parser.getText());

        assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
        assertEquals("content", parser.getCurrentName());

        assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        assertEquals("text", parser.getText());
        assertEquals(4, parser.getTextLength());
        assertArrayEquals(new char[] { 't', 'e', 'x', 't' }, parser.getTextCharacters());
        assertEquals(0, parser.getTextOffset());
        assertFalse(parser.hasTextCharacters());

        assertEquals(JsonToken.END_OBJECT, parser.nextToken());
    }

    @Test
    public void testOverrideCurrentNameChangesCurrentFieldName() throws Exception {
        FromXmlParser parser = parserFor("<root>value</root>");

        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
        assertEquals("root", parser.getCurrentName());

        parser.overrideCurrentName("renamed");

        assertEquals("renamed", parser.getCurrentName());
        assertEquals("renamed", parser.getText());
    }

    @Test
    public void testBinaryValueIsDecodedAndCachedForCurrentStringToken() throws Exception {
        FromXmlParser parser = parserFor("<root>SGVsbG8=</root>");

        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
        assertEquals(JsonToken.VALUE_STRING, parser.nextToken());

        byte[] first = parser.getBinaryValue(Base64Variants.getDefaultVariant());
        byte[] second = parser.getBinaryValue(Base64Variants.getDefaultVariant());

        assertArrayEquals("Hello".getBytes(Charset.forName("UTF-8")), first);
        assertSame(first, second);
    }

    @Test
    public void testInvalidBinaryValueReportsParseException() throws Exception {
        FromXmlParser parser = parserFor("<root>%%%not-base64%%%</root>");

        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
        assertEquals(JsonToken.VALUE_STRING, parser.nextToken());

        try {
            parser.getBinaryValue(Base64Variants.getDefaultVariant());
            fail("Invalid Base64 content must not be accepted");
        } catch (JsonParseException e) {
            assertTrue(e.getMessage().contains("Failed to decode VALUE_STRING as base64"));
        }
    }

    @Test
    public void testCodecFormatAndCloseConfiguration() throws Exception {
        FromXmlParser parser = parserFor("<root/>");
        XmlMapper codec = new XmlMapper();

        assertTrue(parser.requiresCustomCodec());
        assertNotNull(parser.version());
        assertNotNull(parser.getStaxReader());
        assertEquals(0, parser.getFormatFeatures());

        parser.setCodec(codec);
        assertSame(codec, parser.getCodec());

        assertSame(parser, parser.overrideFormatFeatures(0, 0));
        assertEquals(0, parser.getFormatFeatures());

        assertFalse(parser.isClosed());
        parser.close();
        assertTrue(parser.isClosed());

        parser.close();
        assertTrue(parser.isClosed());
    }

    @Test
    public void testNestedUnwrappedListKeepsEmptyNestedList() throws Exception {
        XmlMapper mapper = new XmlMapper();

        NestedUnwrappedLists value = mapper.readValue(
                "<NestedUnwrappedLists><values/></NestedUnwrappedLists>",
                NestedUnwrappedLists.class);

        assertNotNull(value.values);
        assertEquals(1, value.values.size());
        assertNotNull(value.values.get(0));
        assertTrue(value.values.get(0).isEmpty());
    }

    @Test
    public void testNestedUnwrappedListKeepsWhitespaceOnlyNestedList() throws Exception {
        XmlMapper mapper = new XmlMapper();

        NestedUnwrappedLists value = mapper.readValue(
                "<NestedUnwrappedLists><values>   </values></NestedUnwrappedLists>",
                NestedUnwrappedLists.class);

        assertNotNull(value.values);
        assertEquals(1, value.values.size());
        assertNotNull(value.values.get(0));
        assertTrue(value.values.get(0).isEmpty());
    }

    public static class NestedUnwrappedLists
    {
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "values")
        public List<List<String>> values;
    }
}
