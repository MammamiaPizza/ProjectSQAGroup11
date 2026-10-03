package com.fasterxml.jackson.dataformat.xml.deser;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class FromXmlParserTest
{
    private FromXmlParser parserFor(String xml) throws IOException {
        return (FromXmlParser) new XmlFactory().createParser(xml);
    }

    @Test
    public void nextTextValueReadsAttributeValueAfterAttributeFieldName() throws Exception {
        FromXmlParser parser = parserFor("<root number=\"7\"/>");
        try {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals("root", parser.getCurrentName());

            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals("number", parser.getCurrentName());

            assertEquals("7", parser.nextTextValue());
            assertEquals(JsonToken.VALUE_STRING, parser.getCurrentToken());
            assertEquals("7", parser.getText());

            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        } finally {
            parser.close();
        }
    }

    @Test
    public void configuredTextElementNameIsUsedForTextFollowingAttributes() throws Exception {
        FromXmlParser parser = parserFor("<root code=\"A\">contents</root>");
        parser.setXMLTextElementName("value");
        try {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals("root", parser.getCurrentName());

            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals("code", parser.getCurrentName());
            assertEquals("A", parser.nextTextValue());

            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals("value", parser.getCurrentName());
            assertEquals("value", parser.getText());

            assertEquals("contents", parser.nextTextValue());
            assertEquals(8, parser.getTextLength());
            assertEquals(0, parser.getTextOffset());
            assertFalse(parser.hasTextCharacters());
            assertEquals("contents", new String(parser.getTextCharacters()));

            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        } finally {
            parser.close();
        }
    }

    @Test
    public void nextTextValueReturnsEmptyStringForEmptyLeafElement() throws Exception {
        FromXmlParser parser = parserFor("<root/>");
        try {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals("root", parser.getCurrentName());

            assertEquals("", parser.nextTextValue());
            assertEquals(JsonToken.VALUE_STRING, parser.getCurrentToken());
            assertEquals("", parser.getValueAsString("default"));

            assertNull(parser.nextTextValue());
            assertNull(parser.getCurrentToken());

            assertNull(parser.nextTextValue());
            assertNull(parser.getCurrentToken());
        } finally {
            parser.close();
        }
    }

    @Test
    public void binaryValueDecodesStringAndRejectsInvalidBase64() throws Exception {
        FromXmlParser validParser = parserFor("<root>SGVsbG8=</root>");
        try {
            assertEquals(JsonToken.START_OBJECT, validParser.nextToken());
            assertEquals(JsonToken.FIELD_NAME, validParser.nextToken());
            assertEquals(JsonToken.VALUE_STRING, validParser.nextToken());

            byte[] expected = new byte[] { 'H', 'e', 'l', 'l', 'o' };
            assertArrayEquals(expected,
                    validParser.getBinaryValue(Base64Variants.getDefaultVariant()));
            assertArrayEquals(expected,
                    validParser.getBinaryValue(Base64Variants.getDefaultVariant()));
        } finally {
            validParser.close();
        }

        FromXmlParser invalidParser = parserFor("<root>%%%not-base64%%%</root>");
        try {
            assertEquals(JsonToken.START_OBJECT, invalidParser.nextToken());
            assertEquals(JsonToken.FIELD_NAME, invalidParser.nextToken());
            assertEquals(JsonToken.VALUE_STRING, invalidParser.nextToken());

            try {
                invalidParser.getBinaryValue(Base64Variants.getDefaultVariant());
                fail("Invalid Base64 text must be reported as a parsing error");
            } catch (JsonParseException expected) {
                assertTrue(expected.getMessage().contains("base64"));
            }
        } finally {
            invalidParser.close();
        }
    }

    @Test
    public void parserExposesCustomCodecSupportArrayConversionAndCloseState() throws Exception {
        FromXmlParser parser = parserFor("<root><item>value</item></root>");
        try {
            XmlMapper mapper = new XmlMapper();
            parser.setCodec(mapper);

            assertSame(mapper, parser.getCodec());
            assertTrue(parser.requiresCustomCodec());
            assertFalse(parser.isClosed());

            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertTrue(parser.isExpectedStartArrayToken());
            assertEquals(JsonToken.START_ARRAY, parser.getCurrentToken());
            assertTrue(parser.isExpectedStartArrayToken());
        } finally {
            parser.close();
        }

        assertTrue(parser.isClosed());

        parser.close();
        assertTrue(parser.isClosed());
    }
}
