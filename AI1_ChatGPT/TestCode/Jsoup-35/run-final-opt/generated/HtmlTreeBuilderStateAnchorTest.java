package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class HtmlTreeBuilderStateAnchorTest {

    @Test
    public void closesAnUnclosedAnchorBeforeStartingAnotherAnchor() {
        Document document = Jsoup.parse(
                "<a href='http://example.com/'>Link<a href='http://example.com/'>Link</a>");

        assertEquals(
                "<a href=\"http://example.com/\">Link</a><a href=\"http://example.com/\">Link</a>",
                document.body().html());
    }

    @Test
    public void keepsNestedAnchorStartTagsAsSiblingLinksWithTheirOwnText() {
        Document document = Jsoup.parse(
                "<a href='http://one.example/'>first<a href='http://two.example/'>second</a>");

        Elements links = document.body().getElementsByTag("a");
        assertEquals(2, links.size());
        assertEquals("first", links.get(0).text());
        assertEquals("second", links.get(1).text());
        assertEquals("http://one.example/", links.get(0).attr("href"));
        assertEquals("http://two.example/", links.get(1).attr("href"));
        assertSame(document.body(), links.get(0).parent());
        assertSame(document.body(), links.get(1).parent());
    }

    @Test
    public void preservesExplicitlyClosedAdjacentAnchors() {
        Document document = Jsoup.parse(
                "<a href='http://one.example/'>one</a><a href='http://two.example/'>two</a>");

        Elements links = document.body().getElementsByTag("a");
        assertEquals(2, links.size());
        assertEquals("one", links.get(0).text());
        assertEquals("two", links.get(1).text());
        assertEquals(
                "<a href=\"http://one.example/\">one</a><a href=\"http://two.example/\">two</a>",
                document.body().html());
    }

    @Test
    public void ignoresStrayAnchorEndTagAfterAnchorWasClosed() {
        Document document = Jsoup.parse("<a href='http://example.com/'>Link</a></a>tail");

        Elements links = document.body().getElementsByTag("a");
        assertEquals(1, links.size());
        assertEquals("Link", links.get(0).text());
        assertEquals("http://example.com/", links.get(0).attr("href"));
        assertTrue(document.body().html().contains("tail"));
    }
}
