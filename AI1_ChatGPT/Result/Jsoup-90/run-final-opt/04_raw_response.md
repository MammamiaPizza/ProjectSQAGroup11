@org.junit.Test
public void connectsUsingProvidedUrlObject() throws Exception {
    java.net.URL url = new java.net.URL("http://example.com/path?q=value");
    org.jsoup.Connection connection = HttpConnection.connect(url);

    org.junit.Assert.assertEquals(url, connection.request().url());
}

@org.junit.Test
public void addsAndFindsDataValuesAndStreams() {
    HttpConnection connection = new HttpConnection();
    java.io.ByteArrayInputStream stream = new java.io.ByteArrayInputStream(new byte[] { 1, 2, 3 });
    java.io.ByteArrayInputStream typedStream = new java.io.ByteArrayInputStream(new byte[] { 4, 5, 6 });

    org.junit.Assert.assertSame(connection, connection.data("plain", "value"));
    org.junit.Assert.assertSame(connection, connection.data("upload", "file.txt", stream));
    org.junit.Assert.assertSame(connection, connection.data("typed", "typed.txt", typedStream, "text/plain"));

    org.junit.Assert.assertEquals("value", connection.data("plain").value());
    org.junit.Assert.assertSame(stream, connection.data("upload").inputStream());
    org.junit.Assert.assertSame(typedStream, connection.data("typed").inputStream());
    org.junit.Assert.assertNull(connection.data("missing"));
}

@org.junit.Test
public void addsDataCollectionWithoutReplacingKeyVals() {
    HttpConnection connection = new HttpConnection();
    org.jsoup.Connection.KeyVal first = HttpConnection.KeyVal.create("first", "one");
    org.jsoup.Connection.KeyVal second = HttpConnection.KeyVal.create("second", "two");
    java.util.Collection<org.jsoup.Connection.KeyVal> values =
        new java.util.ArrayList<org.jsoup.Connection.KeyVal>();
    values.add(first);
    values.add(second);

    org.junit.Assert.assertSame(connection, connection.data(values));
    org.junit.Assert.assertSame(first, connection.data("first"));
    org.junit.Assert.assertSame(second, connection.data("second"));
}

@org.junit.Test
public void addsIndividualAndMappedCookies() {
    HttpConnection connection = new HttpConnection();
    java.util.Map<String, String> cookies = new java.util.LinkedHashMap<String, String>();
    cookies.put("session", "abc");
    cookies.put("theme", "dark");

    org.junit.Assert.assertSame(connection, connection.cookie("single", "value"));
    org.junit.Assert.assertSame(connection, connection.cookies(cookies));
}