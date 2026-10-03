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

        assertEquals("http://example.com/dir/one.html", links.attr("abs:href"));
        assertEquals("http://example.com/dir/one.html", links.get(0).attr("abs:href"));
        assertEquals("http://example.com/two.html", links.get(1).attr("abs:href"));
        assertTrue(links.hasAttr("abs:href"));
    }

    @Test
    public void elementsHasAbsAttrIsFalseWhenNoElementHasResolvableAttribute() {
        Document document = Jsoup.parse("<a>one</a><a>two</a>", "http://example.com/");
        Elements links = document.select("a");

        assertEquals("", links.attr("abs:href"));
        assertFalse(links.hasAttr("abs:href"));
    }

@org.junit.Test
public void malformedRelativeUrlIsNotExposedAsAbsoluteAttribute() {
    org.jsoup.nodes.Node node = newTestNode("http://example.com/base/");
    node.attr("href", "http://[invalid]");

    org.junit.Assert.assertEquals("", node.attr("abs:href"));
    org.junit.Assert.assertFalse(node.hasAttr("abs:href"));
}

@org.junit.Test
public void absoluteAttributeResolutionUsesUpdatedBaseUri() {
    org.jsoup.nodes.Node node = newTestNode("http://first.example/one/");
    node.attr("href", "page.html");

    org.junit.Assert.assertEquals("http://first.example/one/page.html", node.attr("abs:href"));

    node.setBaseUri("https://second.example/two/");
    org.junit.Assert.assertEquals("https://second.example/two/page.html", node.attr("abs:href"));
    org.junit.Assert.assertTrue(node.hasAttr("abs:href"));
}

private org.jsoup.nodes.Node newTestNode(String baseUri) {
    return new org.jsoup.nodes.Node(baseUri) {
        public String nodeName() {
            return "test";
        }

        void outerHtmlHead(StringBuilder accum, int depth, org.jsoup.nodes.Document.OutputSettings out) {
        }

        void outerHtmlTail(StringBuilder accum, int depth, org.jsoup.nodes.Document.OutputSettings out) {
        }
    };
}
}
