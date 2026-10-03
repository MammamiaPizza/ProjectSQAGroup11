package org.jsoup.nodes;

import org.jsoup.parser.Tag;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NodeAbsUrlTest {
    private Element linkWithBase(String baseUri) {
        return new Element(Tag.valueOf("a"), baseUri);
    }

    @Test
    public void resolvesQueryOnlyUrlAgainstFileBase() {
        Element link = linkWithBase("http://jsoup.org/path/file");
        link.attr("href", "?foo");

        assertEquals("http://jsoup.org/path/file?foo", link.absUrl("href"));
    }

    @Test
    public void resolvesQueryOnlyUrlAgainstDirectoryBase() {
        Element link = linkWithBase("http://jsoup.org/path/");
        link.attr("href", "?foo=bar");

        assertEquals("http://jsoup.org/path/?foo=bar", link.absUrl("href"));
    }

    @Test
    public void queryOnlyUrlReplacesExistingBaseQueryWhileKeepingPath() {
        Element link = linkWithBase("http://jsoup.org/path/file?old=value");
        link.attr("href", "?new=value");

        assertEquals("http://jsoup.org/path/file?new=value", link.absUrl("href"));
    }

    @Test
    public void resolvesRelativePathAgainstFileBase() {
        Element link = linkWithBase("http://jsoup.org/path/file");
        link.attr("href", "other.html");

        assertEquals("http://jsoup.org/path/other.html", link.absUrl("href"));
    }

    @Test
    public void preservesAbsoluteUrlRegardlessOfBaseUri() {
        Element link = linkWithBase("http://jsoup.org/path/file");
        link.attr("href", "https://example.com/resource?key=value");

        assertEquals("https://example.com/resource?key=value", link.absUrl("href"));
    }

    @Test
    public void returnsEmptyStringForMissingAttribute() {
        Element link = linkWithBase("http://jsoup.org/path/file");

        assertEquals("", link.absUrl("href"));
    }

    @Test
    public void resolvesEmptyPresentAttributeToBaseUrl() {
        Element link = linkWithBase("http://jsoup.org/path/file");
        link.attr("href", "");

        assertEquals("http://jsoup.org/path/file", link.absUrl("href"));
    }

@Test
public void attrWithAbsPrefixResolvesRelativeUrl() {
    org.jsoup.nodes.Node link = nodeWithBaseUri("http://jsoup.org/path/file");
    link.attr("href", "other.html");

    org.junit.Assert.assertEquals("http://jsoup.org/path/other.html", link.attr("abs:href"));
}

@Test
public void resolvesAbsoluteUrlWhenBaseUriIsMalformed() {
    org.jsoup.nodes.Node link = nodeWithBaseUri("not a valid base uri");
    link.attr("href", "https://example.com/resource?key=value");

    org.junit.Assert.assertEquals("https://example.com/resource?key=value", link.absUrl("href"));
}

@Test
public void returnsEmptyStringWhenRelativeUrlIsMalformed() {
    org.jsoup.nodes.Node link = nodeWithBaseUri("http://jsoup.org/path/file");
    link.attr("href", "http://[");

    org.junit.Assert.assertEquals("", link.absUrl("href"));
}

private org.jsoup.nodes.Node nodeWithBaseUri(String baseUri) {
    return new org.jsoup.nodes.Node(baseUri) {
        public String nodeName() {
            return "#test";
        }

        void outerHtmlHead(StringBuilder accum, int depth, org.jsoup.nodes.Document.OutputSettings out) {
        }

        void outerHtmlTail(StringBuilder accum, int depth, org.jsoup.nodes.Document.OutputSettings out) {
        }
    };
}
}
