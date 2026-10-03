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
                "<??><root><child/></root>",
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
}