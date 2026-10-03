package org.jsoup.nodes;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NodeAbsPrefixRegressionTest {

    @Test
    public void attrWithAbsPrefixResolvesRelativeUrlAgainstBaseUri() {
        Document document = Jsoup.parse("<a href=\"page.html\">link</a>", "http://example.com/dir/");
        Element link = document.select("a").first();

        assertEquals("http://example.com/dir/page.html", link.attr("abs:href"));
        assertEquals(link.absUrl("href"), link.attr("abs:href"));
    }

    @Test
    public void hasAttrWithAbsPrefixIsTrueForResolvableRelativeAttribute() {
        Document document = Jsoup.parse("<a href=\"../target\">link</a>", "http://example.com/dir/page.html");
        Element link = document.select("a").first();

        assertTrue(link.hasAttr("href"));
        assertTrue(link.hasAttr("abs:href"));
    }

    @Test
    public void absPrefixIsCaseInsensitive() {
        Document document = Jsoup.parse("<a href=\"page.html\">link</a>", "http://example.com/dir/");
        Element link = document.select("a").first();

        assertEquals("http://example.com/dir/page.html", link.attr("ABS:href"));
        assertTrue(link.hasAttr("ABS:href"));
    }

    @Test
    public void absoluteAttributeIsAvailableThroughAbsPrefixEvenWithInvalidBaseUri() {
        Document document = Jsoup.parse("<a href=\"https://other.example/path\">link</a>", "not a valid base uri");
        Element link = document.select("a").first();

        assertEquals("https://other.example/path", link.attr("abs:href"));
        assertTrue(link.hasAttr("abs:href"));
    }

    @Test
    public void missingUnderlyingAttributeDoesNotExistAsAbsoluteAttribute() {
        Document document = Jsoup.parse("<a>link</a>", "http://example.com/");
        Element link = document.select("a").first();

        assertEquals("", link.attr("abs:href"));
        assertFalse(link.hasAttr("abs:href"));
    }

    @Test
    public void elementsAbsAttrAndHasAbsAttrExposeResolvedAttributes() {
        Document document = Jsoup.parse(
                "<a href=\"one.html\">one</a><a href=\"../two.html\">two</a>",
                "http://example.com/dir/");
        Elements links = document.select("a");

        assertEquals(2, links.absAttr("href").size());
        assertEquals("http://example.com/dir/one.html", links.absAttr("href").get(0));
        assertEquals("http://example.com/two.html", links.absAttr("href").get(1));
        assertTrue(links.hasAbsAttr("href"));
    }

    @Test
    public void elementsHasAbsAttrIsFalseWhenNoElementHasResolvableAttribute() {
        Document document = Jsoup.parse("<a>one</a><a>two</a>", "http://example.com/");
        Elements links = document.select("a");

        assertTrue(links.absAttr("href").isEmpty());
        assertFalse(links.hasAbsAttr("href"));
    }
}
