@Test
public void addPropertySplitsCommaSeparatedStringValues() {
    ExtendedProperties properties = new ExtendedProperties();

    properties.addProperty("colors", "red,green,blue");

    String[] colors = properties.getStringArray("colors");
    assertEquals(3, colors.length);
    assertEquals("red", colors[0]);
    assertEquals("green", colors[1]);
    assertEquals("blue", colors[2]);
}

@Test
public void loadWithExplicitEncodingPreservesNonAsciiValues() throws Exception {
    ExtendedProperties properties = new ExtendedProperties();
    byte[] content = "greeting=caf\u00e9\n".getBytes("UTF-8");

    properties.load(new java.io.ByteArrayInputStream(content), "UTF-8");

    assertEquals("caf\u00e9", properties.getString("greeting"));
}

@Test
public void fileConstructorLoadsPrimaryAndDefaultProperties() throws Exception {
    java.io.File primary = java.io.File.createTempFile("extended-properties-primary", ".properties");
    java.io.File defaults = java.io.File.createTempFile("extended-properties-default", ".properties");
    try {
        java.io.FileOutputStream primaryOutput = new java.io.FileOutputStream(primary);
        try {
            primaryOutput.write("primary=value\n".getBytes("ISO-8859-1"));
        } finally {
            primaryOutput.close();
        }

        java.io.FileOutputStream defaultsOutput = new java.io.FileOutputStream(defaults);
        try {
            defaultsOutput.write("fallback=defaultValue\n".getBytes("ISO-8859-1"));
        } finally {
            defaultsOutput.close();
        }

        ExtendedProperties properties = new ExtendedProperties(
                primary.getAbsolutePath(), defaults.getAbsolutePath());

        assertTrue(properties.isInitialized());
        assertEquals("value", properties.getString("primary"));
        assertEquals("defaultValue", properties.getString("fallback"));
    } finally {
        primary.delete();
        defaults.delete();
    }
}