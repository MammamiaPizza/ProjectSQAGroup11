package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class HtmlTreeBuilderStateRegressionTest {

    @Test
    public void emptyStyleAllowsFollowingMetaToRemainInHead() {
        Document document = Jsoup.parse(
                "<html><head><style></style><meta name=foo></head><body>One</body></html>");

        Element head = document.head();
        assertEquals(1, head.getElementsByTag("style").size());
        assertEquals(1, head.getElementsByTag("meta").size());
        assertEquals("foo", head.getElementsByTag("meta").first().attr("name"));
        assertEquals(0, document.body().getElementsByTag("meta").size());
        assertEquals("One", document.body().text());
        assertFalse(document.body().html().contains("&lt;"));
    }

    @Test
    public void emptyNoFramesAllowsFollowingMetaToRemainInHead() {
        Document document = Jsoup.parse(
                "<html><head><noframes></noframes><meta name=foo></head><body>One</body></html>");

        Element head = document.head();
        assertEquals(1, head.getElementsByTag("noframes").size());
        assertEquals(1, head.getElementsByTag("meta").size());
        assertEquals("foo", head.getElementsByTag("meta").first().attr("name"));
        assertEquals(0, document.body().getElementsByTag("meta").size());
        assertEquals("One", document.body().text());
        assertFalse(document.body().html().contains("&lt;"));
    }
}
