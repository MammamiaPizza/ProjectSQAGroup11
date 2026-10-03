package org.jsoup.helper;

import org.jsoup.Connection;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HttpConnectionHeaderCombinationTest {

    @Test
    public void repeatedHeaderNamesCombineValuesWithCommaAndSpace() {
        Connection.Request request = HttpConnection.connect("http://example.com/").request();

        request.header("Cache-Control", "no-cache");
        request.header("Cache-Control", "no-store");

        assertEquals("no-cache, no-store", request.header("Cache-Control"));
    }

    @Test
    public void repeatedHeaderNamesWithDifferentCaseCombineValues() {
        Connection.Request request = HttpConnection.connect("http://example.com/").request();

        request.header("Cache-Control", "no-cache");
        request.header("cache-control", "no-store");

        assertEquals("no-cache, no-store", request.header("Cache-Control"));
    }

    @Test
    public void combinedHeaderCanBeRetrievedCaseInsensitively() {
        Connection.Request request = HttpConnection.connect("http://example.com/").request();

        request.header("X-Test-Header", "first");
        request.header("x-test-header", "second");

        assertEquals("first, second", request.header("X-TEST-HEADER"));
    }

    @Test
    public void multipleRepeatedHeaderValuesRemainInInsertionOrder() {
        Connection.Request request = HttpConnection.connect("http://example.com/").request();

        request.header("Accept", "text/html");
        request.header("ACCEPT", "application/xhtml+xml");
        request.header("accept", "application/xml");

        assertEquals("text/html, application/xhtml+xml, application/xml", request.header("Accept"));
    }
}
