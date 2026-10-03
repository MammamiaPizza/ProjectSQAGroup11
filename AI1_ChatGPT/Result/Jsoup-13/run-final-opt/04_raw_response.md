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