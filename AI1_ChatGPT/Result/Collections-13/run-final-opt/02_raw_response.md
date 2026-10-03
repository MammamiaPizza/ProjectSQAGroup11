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
}