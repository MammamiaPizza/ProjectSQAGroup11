@Test
public void parentsReturnsAncestorsNearestFirstAndExcludesDocumentRoot() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<div><span>text</span></div>");
    Element span = document.selectFirst("span");

    assertEquals(3, span.parents().size());
    assertEquals("div", span.parents().get(0).tagName());
    assertEquals("body", span.parents().get(1).tagName());
    assertEquals("html", span.parents().get(2).tagName());
}

@Test
public void appendParsesAndAddsAllFragmentNodes() {
    Element container = new Element("div");

    assertSame(container, container.append("<span>one</span><span>two</span>"));
    assertEquals(2, container.childNodeSize());
    assertEquals("span", container.child(0).tagName());
    assertEquals("one", container.child(0).text());
    assertEquals("two", container.child(1).text());
}

@Test
public void afterSupportsHtmlAndNodeInsertion() {
    Element container = new Element("div");
    Element first = new Element("p").appendText("one");
    container.appendChild(first);

    assertSame(first, first.after("<span>two</span>"));
    Element span = container.child(1);
    Element em = new Element("em").appendText("three");
    assertSame(span, span.after(em));

    assertEquals(3, container.children().size());
    assertEquals("p", container.child(0).tagName());
    assertEquals("span", container.child(1).tagName());
    assertEquals("em", container.child(2).tagName());
}

@Test
public void textPreservesWhitespaceInsidePreformattedElements() {
    Element pre = new Element("pre").appendText("  one\n two  ");

    assertEquals("  one\n two  ", pre.text());
}