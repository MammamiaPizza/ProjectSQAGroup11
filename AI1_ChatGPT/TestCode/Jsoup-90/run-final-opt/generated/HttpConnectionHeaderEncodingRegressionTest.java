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
}
