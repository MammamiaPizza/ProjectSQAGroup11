package com.fasterxml.jackson.dataformat.xml.deser;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

public class FromXmlParserTest
{
    /**
     * Model used to exercise the nested unwrapped-list behavior from
     * dataformat-xml#180.
     */
    public static class NestedLists {
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<List<String>> entries;
    }

    private FromXmlParser parserFor(String xml) throws IOException {
        return (FromXmlParser) new XmlFactory().createParser(xml);
    }

    @Test
    public void testParsesAttributesAndTextUsingConfiguredTextPropertyName() throws Exception {
        FromXmlParser parser = parserFor("<root attr=\"attribute-value\">text-value</root>");
        parser.setXMLTextElementName("#text");

        assertEquals(JsonToken.START_OBJECT, parser.nextToken());

        assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
        assertEquals("root", parser.getCurrentName());
        assertEquals("root", parser.getText());

        assertEquals(JsonToken.START_OBJECT, parser.nextToken());

        assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
        assertEquals("attr", parser.getCurrentName());
        assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        assertEquals("attribute-value", parser.getText());

        assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
        assertEquals("#text", parser.getCurrentName());
        assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        assertEquals("text-value", parser.getValueAsString());
        assertArrayEquals("text-value".toCharArray(), parser.getTextCharacters());
        assertEquals("text-value".length(), parser.getTextLength());
        assertEquals(0, parser.getTextOffset());
        assertFalse(parser.hasTextCharacters());

        assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        assertNull(parser.nextToken());

        parser.close();
    }

    @Test
    public void testNextTextValueReturnsLeafTextAndConsumesMatchingEndElement() throws Exception {
        FromXmlParser parser = parserFor("<root>leaf text</root>");

        assertEquals(JsonToken.START_OBJECT, parser.nextToken());

        assertNull(parser.nextTextValue());
        assertEquals(JsonToken.FIELD_NAME, parser.getCurrentToken());
        assertEquals("root", parser.getCurrentName());

        assertEquals("leaf text", parser.nextTextValue());
        assertEquals(JsonToken.VALUE_STRING, parser.getCurrentToken());
        assertEquals("leaf text", parser.getText());

        assertNull(parser.nextToken());
        parser.close();
    }

    @Test
    public void testNextTextValueRepresentsEmptyLeafAsEmptyString() throws Exception {
        FromXmlParser parser = parserFor("<root/>");

        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        assertNull(parser.nextTextValue());
        assertEquals(JsonToken.FIELD_NAME, parser.getCurrentToken());

        assertEquals("", parser.nextTextValue());
        assertEquals(JsonToken.VALUE_STRING, parser.getCurrentToken());
        assertEquals("", parser.getText());

        parser.close();
    }

    @Test
    public void testBinaryValueIsDecodedAndCachedForCurrentStringToken() throws Exception {
        FromXmlParser parser = parserFor("<root>SGk=</root>");

        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
        assertEquals(JsonToken.VALUE_STRING, parser.nextToken());

        byte[] first = parser.getBinaryValue(Base64Variants.getDefaultVariant());
        byte[] second = parser.getBinaryValue(Base64Variants.getDefaultVariant());

        assertArrayEquals(new byte[] { 'H', 'i' }, first);
        assertSame("Decoded data should be cached for the current token", first, second);

        parser.close();
    }

    @Test
    public void testBinaryAccessOnNonStringTokenReportsParseError() throws Exception {
        FromXmlParser parser = parserFor("<root>value</root>");
        assertEquals(JsonToken.START_OBJECT, parser.nextToken());

        try {
            parser.getBinaryValue(Base64Variants.getDefaultVariant());
            fail("Binary access must fail when current token is not a string token");
        } catch (JsonParseException e) {
            assertTrue(e.getMessage().contains("not VALUE_STRING"));
        } finally {
            parser.close();
        }
    }

    @Test
    public void testCodecFormatFeaturesAndStaxReaderAccessors() throws Exception {
        FromXmlParser parser = parserFor("<root/>");
        XmlMapper codec = new XmlMapper();

        parser.setCodec(codec);
        assertSame(codec, parser.getCodec());
        assertTrue(parser.requiresCustomCodec());
        assertNotNull(parser.version());
        assertNotNull(parser.getStaxReader());
        assertNotNull(parser.getTokenLocation());
        assertNotNull(parser.getCurrentLocation());

        assertSame(parser, parser.overrideFormatFeatures(0x0A, 0x0F));
        assertEquals(0x0A, parser.getFormatFeatures() & 0x0F);

        parser.close();
        assertTrue(parser.isClosed());

        // Closing an already closed parser must be harmless.
        parser.close();
        assertTrue(parser.isClosed());
    }

    @Test
    public void testCurrentNameCannotBeReadForInitialSyntheticStartObject() throws Exception {
        FromXmlParser parser = parserFor("<root/>");

        assertEquals(JsonToken.START_OBJECT, parser.nextToken());

        try {
            parser.getCurrentName();
            fail("The initial synthetic START_OBJECT has no XML element name");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("Missing name"));
        } finally {
            parser.close();
        }
    }

    @Test
    public void testNestedUnwrappedListRetainsEmptyNestedElement() throws Exception {
        XmlMapper mapper = new XmlMapper();

        NestedLists result = mapper.readValue(
                "<NestedLists><entries/></NestedLists>",
                NestedLists.class);

        assertNotNull(result);
        assertNotNull("An empty nested unwrapped element must still create an outer list entry",
                result.entries);
        assertEquals("The empty nested list must not be swallowed", 1, result.entries.size());
    }

    @Test
    public void testNestedUnwrappedListRetainsWhitespaceOnlyNestedElement() throws Exception {
        XmlMapper mapper = new XmlMapper();

        NestedLists result = mapper.readValue(
                "<NestedLists><entries>   </entries></NestedLists>",
                NestedLists.class);

        assertNotNull(result);
        assertNotNull(result.entries);
        assertEquals("Whitespace-only empty nested list content must not be swallowed",
                1, result.entries.size());
    }
}
