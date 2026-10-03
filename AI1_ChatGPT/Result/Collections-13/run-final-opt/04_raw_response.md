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