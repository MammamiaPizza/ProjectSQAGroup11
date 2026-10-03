@org.junit.Test
public void testFindDeserializerForAllSupportedTypesAndUnsupportedType() {
    java.lang.Class<?>[] supportedTypes = new java.lang.Class<?>[] {
            java.io.File.class,
            java.net.URL.class,
            java.net.URI.class,
            java.lang.Class.class,
            com.fasterxml.jackson.databind.JavaType.class,
            java.util.Currency.class,
            java.util.regex.Pattern.class,
            java.util.Locale.class,
            java.nio.charset.Charset.class,
            java.util.TimeZone.class,
            java.net.InetAddress.class,
            java.net.InetSocketAddress.class,
            java.lang.StringBuilder.class
    };

    org.junit.Assert.assertEquals(supportedTypes.length,
            com.fasterxml.jackson.databind.deser.std.FromStringDeserializer.types().length);
    for (java.lang.Class<?> supportedType : supportedTypes) {
        org.junit.Assert.assertNotNull(
                com.fasterxml.jackson.databind.deser.std.FromStringDeserializer.findDeserializer(supportedType));
    }
    org.junit.Assert.assertNull(
            com.fasterxml.jackson.databind.deser.std.FromStringDeserializer.findDeserializer(java.util.UUID.class));
}

@org.junit.Test
public void testStandardStringDeserializersForCommonScalarTypes() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    org.junit.Assert.assertEquals(new java.io.File("target/from-string-value"),
            mapper.readValue("\"target/from-string-value\"", java.io.File.class));
    org.junit.Assert.assertEquals("http://example.com/resource",
            mapper.readValue("\"http://example.com/resource\"", java.net.URL.class).toExternalForm());
    org.junit.Assert.assertEquals(java.lang.String.class,
            mapper.readValue("\"java.lang.String\"", java.lang.Class.class));
    org.junit.Assert.assertEquals(java.util.Currency.getInstance("USD"),
            mapper.readValue("\"USD\"", java.util.Currency.class));
    org.junit.Assert.assertEquals("a+b",
            mapper.readValue("\"a+b\"", java.util.regex.Pattern.class).pattern());
    org.junit.Assert.assertEquals("UTF-8",
            mapper.readValue("\"UTF-8\"", java.nio.charset.Charset.class).name());
    org.junit.Assert.assertEquals("GMT",
            mapper.readValue("\"GMT\"", java.util.TimeZone.class).getID());
    org.junit.Assert.assertEquals("text",
            mapper.readValue("\"text\"", java.lang.StringBuilder.class).toString());
}

@org.junit.Test
public void testInetAddressAndSocketAddressDeserialization() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    java.net.InetAddress address = mapper.readValue("\"127.0.0.1\"", java.net.InetAddress.class);
    org.junit.Assert.assertEquals("127.0.0.1", address.getHostAddress());

    java.net.InetSocketAddress socket =
            mapper.readValue("\"127.0.0.1:8080\"", java.net.InetSocketAddress.class);
    org.junit.Assert.assertEquals(8080, socket.getPort());
    org.junit.Assert.assertNotNull(socket.getAddress());
    org.junit.Assert.assertEquals("127.0.0.1", socket.getAddress().getHostAddress());
}