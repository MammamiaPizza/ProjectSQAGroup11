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

@org.junit.Test
public void testLoadedValueWithMultipleEscapedCommasRetainsEachEscapedComma() throws Exception {
    ExtendedProperties properties = new ExtendedProperties();
    properties.load(new java.io.ByteArrayInputStream(
            "values=one\\,two\\,three,four\n".getBytes("ISO-8859-1")));

    assertArrayEquals(new String[] { "one,two,three", "four" },
            properties.getStringArray("values"));
}

@org.junit.Test
public void testConstructorLoadsPropertiesAndDefaultsFromFiles() throws Exception {
    java.io.File propertiesFile = createExtendedPropertiesFile("local=main\n");
    java.io.File defaultsFile = createExtendedPropertiesFile("fallback=default\n");

    try {
        ExtendedProperties properties = new ExtendedProperties(
                propertiesFile.getAbsolutePath(), defaultsFile.getAbsolutePath());

        assertEquals("main", properties.getString("local"));
        assertEquals("default", properties.getString("fallback"));
    } finally {
        propertiesFile.delete();
        defaultsFile.delete();
    }
}

@org.junit.Test
public void testClearPropertyRemovesOnlySpecifiedProperty() {
    ExtendedProperties properties = new ExtendedProperties();
    properties.addProperty("first", "one");
    properties.addProperty("second", "two");

    properties.clearProperty("first");

    assertEquals(null, properties.getProperty("first"));
    assertEquals("two", properties.getString("second"));

    properties.addProperty("first", "replacement");
    assertArrayEquals(new String[] { "replacement" },
            properties.getStringArray("first"));
}

private java.io.File createExtendedPropertiesFile(String contents) throws java.io.IOException {
    java.io.File file = java.io.File.createTempFile("extended-properties-", ".properties");
    java.io.FileOutputStream output = new java.io.FileOutputStream(file);
    try {
        output.write(contents.getBytes("ISO-8859-1"));
    } finally {
        output.close();
    }
    return file;
}
}
