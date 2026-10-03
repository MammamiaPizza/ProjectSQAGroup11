The generated suite made several low-level token-stream assumptions that are not part of the reported regression and did not hold on the fixed revision. In particular, tests for direct token sequencing, `nextTextValue()`, field-name replacement, attributes/text token layout, and invalid Base64 decoding failed on the fixed version, so they cannot be retained as valid regression assertions for this bug.

The corrected suite preserves the meaningful parser configuration and Base64-decoding coverage that passed on the fixed revision, and uses data binding to test the reported behavior: an unwrapped nested list must retain an empty nested list for both self-closing and whitespace-only XML elements.

```java
package com.fasterxml.jackson.dataformat.xml.deser;

import java.nio.charset.Charset;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class FromXmlParserTest
{
    private FromXmlParser parserFor(String xml) throws Exception {
        XmlMapper mapper = new XmlMapper();
        return (FromXmlParser) mapper.getFactory().createParser(xml);
    }

    @Test
    public void testBinaryValueIsDecodedAndCachedForCurrentStringToken() throws Exception {
        FromXmlParser parser = parserFor("<root>SGVsbG8=</root>");
        try {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());

            byte[] first = parser.getBinaryValue(Base64Variants.getDefaultVariant());
            byte[] second = parser.getBinaryValue(Base64Variants.getDefaultVariant());

            assertArrayEquals("Hello".getBytes(Charset.forName("UTF-8")), first);
            assertSame(first, second);
        } finally {
            parser.close();
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
```