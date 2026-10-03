@Test public void attrWithAbsPrefixResolvesAbsoluteUrl() {
    org.jsoup.nodes.Element el = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("a"),
"]8;id=md-1trkven;http://example.com/path/http://example.com/path/]8;;]8;;");]8;;
    el.attr("href", "]8;id=md-1ukaspy;http://other.com/pagehttp://other.com/page]8;;]8;;");]8;;
    assertEquals("]8;id=md-1ukaspy;http://other.com/pagehttp://other.com/page]8;;]8;;", el.attr("abs:href"));]8;;
}

@Test public void absUrl_invalidBaseWithAbsoluteUrlAttribute() {
    org.jsoup.nodes.Element el = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("a"),
"://invalid");
    el.attr("href", "]8;id=md-1qqly8w;http://example.com/absolutehttp://example.com/absolute]8;;]8;;");]8;;
    assertEquals("]8;id=md-1qqly8w;http://example.com/absolutehttp://example.com/absolute]8;;]8;;", el.absUrl("href"));]8;;
}

@Test public void absUrl_invalidBaseWithRelativeUrlAttribute() {
    org.jsoup.nodes.Element el = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("a"),
"://invalid");
    el.attr("href", "/relative/path");
    assertEquals("", el.absUrl("href"));
}

@Test public void removeNodeWithoutParent() {
    org.jsoup.nodes.Element el = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("div"),
"");
    el.remove();
    assertNull(el.parent());
}