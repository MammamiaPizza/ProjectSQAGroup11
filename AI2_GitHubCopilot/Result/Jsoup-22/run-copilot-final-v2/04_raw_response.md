@Test
public void testParentsTraversal() {
    org.jsoup.nodes.Document doc =
org.jsoup.Jsoup.parse("<html><head></head><body><div><p>text</p></div></body></html>");
    org.jsoup.nodes.Element p = doc.select("p").first();
    org.jsoup.select.Elements parents = p.parents();
    assertEquals(3, parents.size());
    assertEquals("div", parents.get(0).tagName());
    assertEquals("body", parents.get(1).tagName());
    assertEquals("html", parents.get(2).tagName());
}

@Test
public void testRemoveClass() {
    org.jsoup.nodes.Element el = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("div"),
"");
    el.attr("class", "foo bar baz");
    el.removeClass("bar");
    assertEquals("foo baz", el.attr("class"));
}

@Test
public void testAfterHtmlReturnsElement() {
    org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>Hello</p></div>");
    org.jsoup.nodes.Element p = doc.select("p").first();
    org.jsoup.nodes.Element result = p.after("<span>after</span>");
    assertSame(p, result);
}

@Test
public void testBeforeHtmlOnElements() {
    org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse("<div><p>one</p><p>two</p></div>");
    org.jsoup.select.Elements ps = doc.select("p");
    ps.before("<b>before</b>");
    String html = doc.body().html();
    assertTrue(html.contains("<b>before</b><p>one</p>"));
    assertTrue(html.contains("<b>before</b><p>two</p>"));
}