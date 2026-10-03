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