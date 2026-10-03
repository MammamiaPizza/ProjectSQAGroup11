package org.apache.commons.collections;

import java.io.ByteArrayInputStream;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ExtendedPropertiesCollections271Test {

    private ExtendedProperties load(String text) throws Exception {
        ExtendedProperties properties = new ExtendedProperties();
        properties.load(new ByteArrayInputStream(text.getBytes("ISO-8859-1")));
        return properties;
    }

    @Test
    public void testEvenNumberOfBackslashesBeforeCommaDoesNotEscapeDelimiter() throws Exception {
        ExtendedProperties properties = load("values=first\\\\,second\n");

        assertArrayEquals(new String[] { "first\\", "second" },
                properties.getStringArray("values"));
    }

    @Test
    public void testSingleBackslashEscapesCommaWithinOneCollectionValue() throws Exception {
        ExtendedProperties properties = load("values=first\\,second,third\n");

        assertArrayEquals(new String[] { "first,second", "third" },
                properties.getStringArray("values"));
    }

    @Test
    public void testLoadedCommaSeparatedPropertyIsAvailableAsCollection() throws Exception {
        ExtendedProperties properties = load("values=red,green,blue\n");

        assertArrayEquals(new String[] { "red", "green", "blue" },
                properties.getStringArray("values"));
        assertEquals("red", properties.getString("values"));
    }

    @Test
    public void testLoadedSingleValueIsAvailableAsSingleElementCollection() throws Exception {
        ExtendedProperties properties = load("value=plain\n");

        assertArrayEquals(new String[] { "plain" }, properties.getStringArray("value"));
        assertEquals("plain", properties.getString("value"));
    }
}
