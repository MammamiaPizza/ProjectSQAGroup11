@Test
public void outerHtmlIncludesNamePublicAndSystemIdentifiers() {
    DocumentType type = new DocumentType("html", "-//W3C//DTD HTML 4.01//EN", "http://example.com/html.dtd", "");

    assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://example.com/html.dtd\">",
            renderDocumentTypeForOuterHtmlTest(type));
}

@Test
public void outerHtmlOmitsBlankDoctypeComponents() {
    DocumentType type = new DocumentType("", "", "", "");

    assertEquals("<!DOCTYPE>", renderDocumentTypeForOuterHtmlTest(type));
}

private String renderDocumentTypeForOuterHtmlTest(DocumentType type) {
    StringBuilder accum = new StringBuilder();
    org.jsoup.nodes.Document.OutputSettings settings = new org.jsoup.nodes.Document.OutputSettings();
    type.outerHtmlHead(accum, 0, settings);
    type.outerHtmlTail(accum, 0, settings);
    return accum.toString();
}