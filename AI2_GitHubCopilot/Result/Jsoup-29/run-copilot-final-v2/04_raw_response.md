@Test
public void testNormaliseMovesContentFromDuplicateHeadToMaster() {
    Document doc = Document.createShell("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
    Element html = doc.child(0); // the <html> element
    Element dupHead = doc.createElement("head");
    html.appendChild(dupHead);
    Element title = doc.createElement("title");
    title.text("Extra title");
    dupHead.appendChild(title);
    doc.normalise();
    Element head = doc.head();
    assertEquals(1, head.children().size());
    assertEquals("Extra title", head.child(0).text());
}

@Test
public void testClonePreservesIndependentOutputSettings() {
    Document doc = new Document("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
    Document.OutputSettings settings = new Document.OutputSettings();
    settings.prettyPrint(false);
    doc.outputSettings(settings);
    Document clone = doc.clone();
    assertTrue(doc.outputSettings() != clone.outputSettings());
    assertEquals(false, clone.outputSettings().pretyPrint());
    clone.outputSettings().pretyPrint(true);
    assertEquals(false, doc.outputSettings().pretyPrint());
    assertEquals(true, clone.outputSettings().pretyPrint());
}

@Test
public void testDocumentTextMethodSetsBodyText() {
    Document doc = Document.createShell("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
    doc.text("Hello world");
    assertEquals("Hello world", doc.body().text());
}

@Test
public void testTitleNormalizesNewlinesToSpaces() {
    Document doc = Document.createShell("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
    doc.title("Hello\nthere");
    assertEquals("Hello there", doc.title());
}