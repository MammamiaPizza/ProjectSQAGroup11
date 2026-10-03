@org.junit.Test
public void handlesMalformedXmlDeclarationBeforeRoot() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(
        "<?xml version=\"1.0\"><root/>", "", org.jsoup.parser.Parser.xmlParser());

    org.junit.Assert.assertEquals("root", document.child(0).tagName());
}

@org.junit.Test
public void preservesDoctypeAsDocumentTypeInXmlMode() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(
        "<!DOCTYPE note SYSTEM \"note.dtd\"><note/>", "", org.jsoup.parser.Parser.xmlParser());

    org.junit.Assert.assertTrue(document.childNodes().get(0) instanceof org.jsoup.nodes.DocumentType);
    org.junit.Assert.assertEquals("note", document.child(0).tagName());
}

@org.junit.Test
public void preservesCdataNodesInXmlMode() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(
        "<root><![CDATA[a < b]]></root>", "", org.jsoup.parser.Parser.xmlParser());

    org.junit.Assert.assertTrue(document.child(0).childNode(0) instanceof org.jsoup.nodes.CDataNode);
}

@org.junit.Test
public void closesOpenDescendantsWhenMatchingAncestorEndTagIsSeen() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(
        "<root><child></root><after/>", "", org.jsoup.parser.Parser.xmlParser());

    org.junit.Assert.assertEquals(2, document.children().size());
    org.junit.Assert.assertEquals("root", document.child(0).tagName());
    org.junit.Assert.assertEquals("after", document.child(1).tagName());
}