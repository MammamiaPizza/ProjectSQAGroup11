package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TokeniserUnterminatedTextTest {
    @Test
    public void unterminatedTextareaStopsTextAtFollowingMarkup() {
        Document document = Jsoup.parse("<textarea>one<p>two");

        assertEquals("one", document.select("textarea").get(0).text());
    }

    @Test
    public void unterminatedTitleStopsTextAtFollowingMarkup() {
        Document document = Jsoup.parse("<title>One<b>Two <p>Test</p>");

        assertEquals("One", document.title());
    }

    @Test
    public void closedTextareaRetainsTextBeforeItsClosingTag() {
        Document document = Jsoup.parse("<textarea>one</textarea><p>two</p>");
        Element textarea = document.select("textarea").get(0);

        assertEquals("one", textarea.text());
    }

    @Test
    public void closedTitleRetainsTextBeforeItsClosingTag() {
        Document document = Jsoup.parse("<title>One</title><p>Two</p>");

        assertEquals("One", document.title());
    }
}
