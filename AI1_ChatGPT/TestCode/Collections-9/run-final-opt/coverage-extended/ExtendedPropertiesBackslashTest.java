package org.apache.commons.collections;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

public class ExtendedPropertiesBackslashTest extends TestCase {

    private ExtendedProperties load(String content) throws Exception {
        ExtendedProperties properties = new ExtendedProperties();
        properties.load(new ByteArrayInputStream(content.getBytes("ISO-8859-1")));
        return properties;
    }

    public void testLoadPreservesTwoLeadingBackslashes() throws Exception {
        ExtendedProperties properties = load(
                "server=\\\\\\\\192.168.1.91\\\\test\n");

        assertEquals("\\\\192.168.1.91\\test", properties.getString("server"));
    }

    public void testLoadPreservesSingleLeadingAndEmbeddedBackslashes() throws Exception {
        ExtendedProperties properties = load(
                "path=\\server\\share\\directory\n");

        assertEquals("\\server\\share\\directory", properties.getString("path"));
    }

    public void testLoadUnescapesEscapedBackslashesInsideValue() throws Exception {
        ExtendedProperties properties = load(
                "path=C:\\\\temp\\\\folder\n");

        assertEquals("C:\\temp\\folder", properties.getString("path"));
    }

    public void testLoadPreservesEscapedTrailingBackslash() throws Exception {
        ExtendedProperties properties = load(
                "path=directory\\\\\n");

        assertEquals("directory\\", properties.getString("path"));
    }

@org.junit.Test
public void testFileConstructorLoadsProperties() throws Exception {
    java.io.File file = java.io.File.createTempFile("extended-properties-", ".properties");
    try {
        writeExtendedPropertiesFile(file, "name=loaded\n");
        ExtendedProperties properties = new ExtendedProperties(file.getAbsolutePath());

        assertEquals("loaded", properties.getString("name"));
    } finally {
        file.delete();
    }
}

@org.junit.Test
public void testFileConstructorUsesDefaultProperties() throws Exception {
    java.io.File file = java.io.File.createTempFile("extended-properties-main-", ".properties");
    java.io.File defaultFile = java.io.File.createTempFile("extended-properties-default-", ".properties");
    try {
        writeExtendedPropertiesFile(file, "main=value\n");
        writeExtendedPropertiesFile(defaultFile, "fallback=defaultValue\n");

        ExtendedProperties properties = new ExtendedProperties(
                file.getAbsolutePath(), defaultFile.getAbsolutePath());

        assertEquals("value", properties.getString("main"));
        assertEquals("defaultValue", properties.getString("fallback"));
    } finally {
        file.delete();
        defaultFile.delete();
    }
}

@org.junit.Test
public void testLoadSplitsCommaSeparatedValuesAndPreservesEscapedCommas() throws Exception {
    ExtendedProperties properties = new ExtendedProperties();
    properties.load(new java.io.ByteArrayInputStream(
            "values=one\\,two,three\n".getBytes("ISO-8859-1")));

    String[] values = properties.getStringArray("values");
    assertEquals(2, values.length);
    assertEquals("one,two", values[0]);
    assertEquals("three", values[1]);
}

private static void writeExtendedPropertiesFile(java.io.File file, String contents)
        throws java.io.IOException {
    java.io.FileOutputStream output = new java.io.FileOutputStream(file);
    try {
        output.write(contents.getBytes("ISO-8859-1"));
    } finally {
        output.close();
    }
}
}
