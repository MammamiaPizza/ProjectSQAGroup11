@Test
public void titleCreatesThenUpdatesSingleTitleElement() {
    Document document = Document.createShell("");

    document.title("First");
    document.title("Second");

    assertEquals("Second", document.title());
    assertEquals(1, document.head().getElementsByTag("title").size());
    assertEquals("<html><head><title>Second</title></head><body></body></html>", document.outerHtml());
}

@Test
public void createElementCreatesDetachedElement() {
    Document document = Document.createShell("");

    org.jsoup.nodes.Element element = document.createElement("p");

    assertEquals("<p></p>", element.outerHtml());
    assertEquals("<html><head></head><body></body></html>", document.outerHtml());
}

@Test
public void normaliseCreatesMissingHtmlHeadAndBodyElements() {
    Document document = new Document("");

    assertSame(document, document.normalise());
    assertEquals("<html><head></head><body></body></html>", document.outerHtml());
}

@Test
public void normaliseMovesRootAndHeadTextIntoBodyInDocumentOrder() {
    Document document = Document.createShell("");
    document.prependChild(new org.jsoup.nodes.TextNode("root", ""));
    document.head().prependChild(new org.jsoup.nodes.TextNode("head", ""));

    document.normalise();

    assertEquals("<html><head></head><body>head root </body></html>", document.outerHtml());
}