package org.jsoup.helper;

import org.jsoup.Connection;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class HttpConnectionHeaderCombinationTest {

    @Test
    public void repeatedHeaderNamesCombineValuesWithCommaAndSpace() throws Exception {
        Connection.Response response = responseWithHeaders("Cache-Control", "no-cache", "no-store");

        assertEquals("no-cache, no-store", response.header("Cache-Control"));
    }

    @Test
    public void repeatedHeaderNamesWithDifferentCaseCombineValues() throws Exception {
        Connection.Response response = responseWithHeaders("cache-control", "no-cache", "no-store");

        assertEquals("no-cache, no-store", response.header("Cache-Control"));
    }

    @Test
    public void combinedHeaderCanBeRetrievedCaseInsensitively() throws Exception {
        Connection.Response response = responseWithHeaders("x-test-header", "first", "second");

        assertEquals("first, second", response.header("X-TEST-HEADER"));
    }

    @Test
    public void multipleRepeatedHeaderValuesRemainInInsertionOrder() throws Exception {
        Connection.Response response = responseWithHeaders(
            "Accept", "text/html", "application/xhtml+xml", "application/xml");

        assertEquals("text/html, application/xhtml+xml, application/xml", response.header("Accept"));
    }

    private static Connection.Response responseWithHeaders(String name, String... values) throws Exception {
        final Map<String, List<String>> headers = new LinkedHashMap<String, List<String>>();
        headers.put(name, Arrays.asList(values));
        headers.put("Content-Type", Collections.singletonList("text/html"));

        URL url = new URL(null, "http://example.com/", new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL url) {
                return new HeaderConnection(url, headers);
            }
        });

        return HttpConnection.connect(url).execute();
    }

    private static class HeaderConnection extends HttpURLConnection {
        private final Map<String, List<String>> headers;

        HeaderConnection(URL url, Map<String, List<String>> headers) {
            super(url);
            this.headers = headers;
        }

        @Override
        public void disconnect() {
        }

        @Override
        public boolean usingProxy() {
            return false;
        }

        @Override
        public void connect() {
        }

        @Override
        public int getResponseCode() {
            return HTTP_OK;
        }

        @Override
        public String getResponseMessage() {
            return "OK";
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(new byte[0]);
        }

        @Override
        public String getContentType() {
            return "text/html";
        }

        @Override
        public String getHeaderField(String name) {
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(name) && !entry.getValue().isEmpty())
                    return entry.getValue().get(0);
            }
            return null;
        }

        @Override
        public Map<String, List<String>> getHeaderFields() {
            return headers;
        }
    }
}
