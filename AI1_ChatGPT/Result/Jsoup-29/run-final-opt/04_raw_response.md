@org.junit.Test
public void createShellProvidesHeadAndBodyElements() {
    org.jsoup.nodes.Document document = org.jsoup.nodes.Document.createShell("http://example.com/");

    org.junit.Assert.assertNotNull(document.head());
    org.junit.Assert.assertNotNull(document.body());
}

@org.junit.Test
public void normaliseCreatesMissingStructureAndIsIdempotent() {
    org.jsoup.nodes.Document document = new org.jsoup.nodes.Document("");

    org.junit.Assert.assertSame(document, document.normalise());
    org.junit.Assert.assertNotNull(document.head());
    org.junit.Assert.assertNotNull(document.body());

    String normalisedHtml = document.outerHtml();
    document.normalise();
    org.junit.Assert.assertEquals(normalisedHtml, document.outerHtml());
}

@org.junit.Test
public void createElementUsesRequestedTagAndDocumentBaseUri() {
    org.jsoup.nodes.Document document = new org.jsoup.nodes.Document("http://example.com/base/");
    org.jsoup.nodes.Element element = document.createElement("section");

    org.junit.Assert.assertEquals("section", element.tagName());
    org.junit.Assert.assertEquals("http://example.com/base/", element.baseUri());
}

@org.junit.Test
public void cloneCopiesOutputSettingsIndependently() {
    org.jsoup.nodes.Document document = new org.jsoup.nodes.Document("");
    document.outputSettings().prettyPrint(false).indentAmount(3).charset("ISO-8859-1");

    org.jsoup.nodes.Document clone = document.clone();

    org.junit.Assert.assertNotSame(document.outputSettings(), clone.outputSettings());
    org.junit.Assert.assertFalse(clone.outputSettings().prettyPrint());
    org.junit.Assert.assertEquals(3, clone.outputSettings().indentAmount());
    org.junit.Assert.assertEquals("ISO-8859-1", clone.outputSettings().charset().name());

    clone.outputSettings().prettyPrint(true).indentAmount(1).charset("UTF-8");

    org.junit.Assert.assertFalse(document.outputSettings().prettyPrint());
    org.junit.Assert.assertEquals(3, document.outputSettings().indentAmount());
    org.junit.Assert.assertEquals("ISO-8859-1", document.outputSettings().charset().name());
}