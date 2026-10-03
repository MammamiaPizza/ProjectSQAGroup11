The generated nested-list XML represented an empty *outer* unwrapped list, not an outer list containing an empty nested list. The corrected tests add the required nested `<entries>` element so the deserializer must retain one outer-list entry.

The parser API tests use the actual token sequence exposed by `FromXmlParser`: the synthetic root `START_OBJECT`, followed by the XML root field name and its value/object tokens. No unsupported APIs or dependencies are used.

```java
package com.fasterxml.jackson.dataformat.xml.deser;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

public class FromXmlParserTest
{
    /**
     * Model used to exercise nested unwrapped-list behavior from
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
        try {
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
        } finally {
            parser.close();
        }
    }

    @Test
    public void testNextTextValueReturnsLeafTextAndConsumesMatchingEndElement() throws Exception {
        FromXmlParser parser = parserFor("<root>leaf text</root>");
        try {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());

            assertNull(parser.nextTextValue());
            assertEquals(JsonToken.FIELD_NAME, parser.getCurrentToken());
            assertEquals("root", parser.getCurrentName());

            assertEquals("leaf text", parser.nextTextValue());
            assertEquals(JsonToken.VALUE_STRING, parser.getCurrentToken());
            assertEquals("leaf text", parser.getText());

            assertNull(parser.nextToken());
        } finally {
            parser.close();
        }
    }

    @Test
    public void testNextTextValueRepresentsEmptyLeafAsEmptyString() throws Exception {
        FromXmlParser parser = parserFor("<root/>");
        try {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());

            assertNull(parser.nextTextValue());
            assertEquals(JsonToken.FIELD_NAME, parser.getCurrentToken());
            assertEquals("root", parser.getCurrentName());

            assertEquals("", parser.nextTextValue());
            assertEquals(JsonToken.VALUE_STRING, parser.getCurrentToken());
            assertEquals("", parser.getText());
        } finally {
            parser.close();
        }
    }

    @Test
    public void testBinaryValueIsDecodedAndCachedForCurrentStringToken() throws Exception {
        FromXmlParser parser = parserFor("<root>SGk=</root>");
        try {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());

            byte[] first = parser.getBinaryValue(Base64Variants.getDefaultVariant());
            byte[] second = parser.getBinaryValue(Base64Variants.getDefaultVariant());

            assertArrayEquals(new byte[] { 'H', 'i' }, first);
            assertSame("Decoded data should be cached for the current token", first, second);
        } finally {
            parser.close();
        }
    }

    @Test
    public void testBinaryAccessOnNonStringTokenReportsParseError() throws Exception {
        FromXmlParser parser = parserFor("<root>value</root>");
        try {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());

            try {
                parser.getBinaryValue(Base64Variants.getDefaultVariant());
                fail("Binary access must fail when current token is not a string token");
            } catch (JsonParseException e) {
                assertTrue(e.getMessage().contains("not VALUE_STRING"));
            }
        } finally {
            parser.close();
        }
    }

    @Test
    public void testCodecFormatFeaturesAndStaxReaderAccessors() throws Exception {
        FromXmlParser parser = parserFor("<root/>");
        try {
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
        } finally {
            parser.close();
        }

        assertTrue(parser.isClosed());

        parser.close();
        assertTrue(parser.isClosed());
    }

    @Test
    public void testCurrentNameCannotBeReadForInitialSyntheticStartObject() throws Exception {
        FromXmlParser parser = parserFor("<root/>");
        try {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());

            try {
                parser.getCurrentName();
                fail("The initial synthetic START_OBJECT has no XML element name");
            } catch (IllegalStateException e) {
                assertTrue(e.getMessage().contains("Missing name"));
            }
        } finally {
            parser.close();
        }
    }

    @Test
    public void testNestedUnwrappedListRetainsEmptyNestedElement() throws Exception {
        XmlMapper mapper = new XmlMapper();

        /*
         * The outer <entries> is the unwrapped outer-list item. Its nested
         * empty <entries> element represents the empty inner List<String>.
         */
        NestedLists result = mapper.readValue(
                "<NestedLists><entries><entries/></entries></NestedLists>",
                NestedLists.class);

        assertNotNull(result);
        assertNotNull("An empty nested unwrapped element must create an outer list entry",
                result.entries);
        assertEquals("The empty nested list must not be swallowed", 1, result.entries.size());
        assertNotNull(result.entries.get(0));
    }

    @Test
    public void testNestedUnwrappedListRetainsWhitespaceOnlyNestedElement() throws Exception {
        XmlMapper mapper = new XmlMapper();

        /*
         * Whitespace-only text in the nested list element is semantically
         * empty, but must still retain the enclosing outer-list entry.
         */
        NestedLists result = mapper.readValue(
                "<NestedLists><entries><entries>   </entries></entries></NestedLists>",
                NestedLists.class);

        assertNotNull(result);
        assertNotNull(result.entries);
        assertEquals("Whitespace-only empty nested list content must not be swallowed",
                1, result.entries.size());
        assertNotNull(result.entries.get(0));
    }
}
```