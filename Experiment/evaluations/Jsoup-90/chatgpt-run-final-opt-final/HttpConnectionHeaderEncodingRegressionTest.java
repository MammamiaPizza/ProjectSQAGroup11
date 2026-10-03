import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.jsoup.Connection;
import org.jsoup.helper.HttpConnection;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HttpConnectionHeaderEncodingRegressionTest {
    @Test
    public void sendsOrdinaryAsciiHeaderValue() throws IOException {
        AtomicReference<String> received = new AtomicReference<String>();
        HttpServer server = startServer("X-Ascii", received);
        try {
            Connection.Response response = HttpConnection.connect(url(server))
                    .header("X-Ascii", "ordinary-value")
                    .execute();

            assertEquals(200, response.statusCode());
            assertEquals("ordinary-value", received.get());
        } finally {
            server.stop(0);
        }
    }

    @Test
    public void executesRequestWithShortNonAsciiHeaderValue() throws IOException {
        AtomicReference<String> received = new AtomicReference<String>();
        HttpServer server = startServer("X-Encoded", received);
        try {
            Connection.Response response = HttpConnection.connect(url(server))
                    .header("X-Encoded", "\u00e9")
                    .execute();

            assertEquals(200, response.statusCode());
            assertNotNull(received.get());
        } finally {
            server.stop(0);
        }
    }

    @Test
    public void headersMapSupportsAsciiAndNonAsciiValuesDuringExecution() throws IOException {
        AtomicReference<String> received = new AtomicReference<String>();
        HttpServer server = startServer("X-Map-Ascii", received);
        try {
            Map<String, String> headers = new LinkedHashMap<String, String>();
            headers.put("X-Map-Ascii", "map-value");
            headers.put("X-Map-Encoded", "Caf\u00e9");

            Connection.Response response = HttpConnection.connect(url(server))
                    .headers(headers)
                    .execute();

            assertEquals(200, response.statusCode());
            assertEquals("map-value", received.get());
        } finally {
            server.stop(0);
        }
    }

    private static HttpServer startServer(final String headerName, final AtomicReference<String> received)
            throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                received.set(exchange.getRequestHeaders().getFirst(headerName));
                exchange.sendResponseHeaders(200, 0);
                exchange.close();
            }
        });
        server.start();
        return server;
    }

    private static String url(HttpServer server) {
        return "http://localhost:" + server.getAddress().getPort() + "/";
    }

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
}
