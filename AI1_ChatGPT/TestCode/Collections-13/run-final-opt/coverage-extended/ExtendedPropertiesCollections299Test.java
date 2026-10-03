package org.apache.commons.collections;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ExtendedPropertiesCollections299Test {

    @Test
    public void addPropertyAccumulatesRepeatedValuesForAllCollectionAccessors() {
        ExtendedProperties properties = new ExtendedProperties();

        properties.addProperty("resource.loader", "file");
        properties.addProperty("resource.loader", "classpath");
        properties.addProperty("resource.loader", "url");

        assertArrayEquals(
                new String[] { "file", "classpath", "url" },
                properties.getStringArray("resource.loader"));

        Vector values = properties.getVector("resource.loader");
        assertNotNull(values);
        assertEquals(3, values.size());
        assertEquals("file", values.get(0));
        assertEquals("classpath", values.get(1));
        assertEquals("url", values.get(2));

        List list = properties.getList("resource.loader");
        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals("file", list.get(0));
        assertEquals("classpath", list.get(1));
        assertEquals("url", list.get(2));
    }

    @Test
    public void loadAccumulatesRepeatedPropertyLines() throws IOException {
        String contents =
                "database.host=primary\n"
                + "database.host=secondary\n"
                + "database.host=replica\n";
        ExtendedProperties properties = new ExtendedProperties();

        properties.load(new ByteArrayInputStream(contents.getBytes()));

        assertArrayEquals(
                new String[] { "primary", "secondary", "replica" },
                properties.getStringArray("database.host"));

        Vector values = properties.getVector("database.host");
        assertNotNull(values);
        assertEquals(3, values.size());
        assertEquals("primary", values.get(0));
        assertEquals("secondary", values.get(1));
        assertEquals("replica", values.get(2));

        List list = properties.getList("database.host");
        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals("primary", list.get(0));
        assertEquals("secondary", list.get(1));
        assertEquals("replica", list.get(2));
    }

    @Test
    public void addingASecondValueKeepsTheOriginalSingleValueAccessible() {
        ExtendedProperties properties = new ExtendedProperties();

        properties.addProperty("mode", "initial");
        assertArrayEquals(new String[] { "initial" }, properties.getStringArray("mode"));

        properties.addProperty("mode", "additional");

        assertArrayEquals(
                new String[] { "initial", "additional" },
                properties.getStringArray("mode"));
        assertEquals("initial", properties.getVector("mode").get(0));
        assertEquals("additional", properties.getList("mode").get(1));
    }

@org.junit.Test
public void addPropertySplitsCommaDelimitedStringValues() {
    ExtendedProperties properties = new ExtendedProperties();

    properties.addProperty("colors", "red,green,blue");

    org.junit.Assert.assertArrayEquals(
            new String[] { "red", "green", "blue" },
            properties.getStringArray("colors"));
}

@org.junit.Test
public void addPropertyRetainsNonStringValues() {
    ExtendedProperties properties = new ExtendedProperties();
    Object value = new Object();

    properties.addProperty("object", value);

    org.junit.Assert.assertSame(value, properties.getProperty("object"));
}

@org.junit.Test
public void fileConstructorLoadsPrimaryAndDefaultProperties() throws Exception {
    java.io.File primary = java.io.File.createTempFile("extended-properties-primary", ".properties");
    java.io.File defaults = java.io.File.createTempFile("extended-properties-default", ".properties");
    java.io.FileOutputStream primaryOut = null;
    java.io.FileOutputStream defaultsOut = null;
    try {
        defaultsOut = new java.io.FileOutputStream(defaults);
        defaultsOut.write("fromDefault=default\nshared=default\n".getBytes("ISO-8859-1"));
        defaultsOut.close();
        defaultsOut = null;

        primaryOut = new java.io.FileOutputStream(primary);
        primaryOut.write("fromPrimary=primary\nshared=primary\n".getBytes("ISO-8859-1"));
        primaryOut.close();
        primaryOut = null;

        ExtendedProperties properties =
                new ExtendedProperties(primary.getAbsolutePath(), defaults.getAbsolutePath());

        org.junit.Assert.assertEquals("primary", properties.getProperty("fromPrimary"));
        org.junit.Assert.assertEquals("default", properties.getProperty("fromDefault"));
        org.junit.Assert.assertEquals("primary", properties.getProperty("shared"));
    } finally {
        if (primaryOut != null) {
            primaryOut.close();
        }
        if (defaultsOut != null) {
            defaultsOut.close();
        }
        primary.delete();
        defaults.delete();
    }
}
}
