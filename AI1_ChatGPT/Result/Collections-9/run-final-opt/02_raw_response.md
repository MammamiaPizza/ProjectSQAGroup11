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
}