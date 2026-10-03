@Test
public void setsDocumentUriFromNonBlankSourceLocation() {
    org.jsoup.nodes.Document source = org.jsoup.Jsoup.parse("<p>content</p>", "http://example.com/source");
    org.w3c.dom.Document converted = new W3CDom().fromJsoup(source);

    assertEquals("http://example.com/source", converted.getDocumentURI());
}

@Test
public void doesNotOverwriteExistingDocumentUriWhenSourceLocationIsBlank() throws Exception {
    org.jsoup.nodes.Document source = org.jsoup.Jsoup.parse("<p>content</p>");
    org.w3c.dom.Document target = javax.xml.parsers.DocumentBuilderFactory.newInstance()
        .newDocumentBuilder().newDocument();
    target.setDocumentURI("http://example.com/existing");

    new W3CDom().convert(source, target);

    assertEquals("http://example.com/existing", target.getDocumentURI());
}

@Test
public void serializesW3cDocumentToString() throws Exception {
    org.w3c.dom.Document document = javax.xml.parsers.DocumentBuilderFactory.newInstance()
        .newDocumentBuilder().newDocument();
    org.w3c.dom.Element root = document.createElement("root");
    root.appendChild(document.createTextNode("serialized"));
    document.appendChild(root);

    String serialized = new W3CDom().asString(document);

    assertTrue(serialized.contains("<root>serialized</root>"));
}