package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.XmlDeclaration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class XmlTreeBuilderAdditionalTest {

    @Test
    public void parsesValidXmlDeclarationAndPreservesItsAttributes() {
        Document document = Jsoup.parse(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Root id=\"r\"/>",
                "",
                Parser.xmlParser());

        Node declaration = document.childNode(0);
        assertTrue(declaration instanceof XmlDeclaration);
        assertEquals("1.0", declaration.attr("version"));
        assertEquals("UTF-8", declaration.attr("encoding"));

        Element root = document.child(0);
        assertEquals("Root", root.tagName());
        assertEquals("r", root.attr("id"));
        assertEquals(Document.OutputSettings.Syntax.xml, document.outputSettings().syntax());
    }

    @Test
    public void handlesEmptyProcessingInstructionLikeDeclarationBeforeRoot() {
        Document document = Jsoup.parse(
                "<?xml?><root><child/></root>",
                "",
                Parser.xmlParser());

        Element root = document.child(0);
        assertEquals("root", root.tagName());
        assertEquals(1, root.children().size());
        assertEquals("child", root.child(0).tagName());
    }

    @Test
    public void handlesEmptyBangDeclarationLikeCommentBeforeRoot() {
        Document document = Jsoup.parse(
                "<!?><root attribute=\"value\"/>",
                "",
                Parser.xmlParser());

        Element root = document.child(0);
        assertEquals("root", root.tagName());
        assertEquals("value", root.attr("attribute"));
    }

    @Test
    public void preservesCaseTextAndSelfClosingElementsInXmlMode() {
        Document document = Jsoup.parse(
                "<Root id=\"1\">text<Empty /></Root>",
                "",
                Parser.xmlParser());

        Element root = document.child(0);
        assertEquals("Root", root.tagName());
        assertEquals("1", root.attr("id"));
        assertEquals("text", root.text());
        assertEquals(1, root.children().size());
        assertEquals("Empty", root.child(0).tagName());
    }

    @Test
    public void ignoresUnmatchedEndTagsAndContinuesAtDocumentLevel() {
        Document document = Jsoup.parse(
                "<root>one</root></orphan><after/>",
                "",
                Parser.xmlParser());

        assertEquals(2, document.children().size());
        assertEquals("root", document.child(0).tagName());
        assertEquals("after", document.child(1).tagName());
    }

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
}
