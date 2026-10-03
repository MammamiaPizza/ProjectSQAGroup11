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
}
