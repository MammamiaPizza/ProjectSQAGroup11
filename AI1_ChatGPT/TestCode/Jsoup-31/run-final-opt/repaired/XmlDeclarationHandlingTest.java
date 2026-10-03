package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class XmlDeclarationHandlingTest {

    @Test
    public void parsesXmlDeclarationAsDeclarationBeforeBody() {
        Document document = Jsoup.parse("<?xml encoding='UTF-8' ?><body>One</body>", "", Parser.xmlParser());

        assertEquals("<?xml encoding='UTF-8' ?> <body> One </body>",
                document.outerHtml().replaceAll("\\s+", " ").trim());
    }

    @Test
    public void preservesDeclarationClosedWithoutWhitespaceBeforeQuestionMark() {
        Document document = Jsoup.parse("<?xml version='1.0'?><root/>", "", Parser.xmlParser());

        String declaration = document.childNode(0).outerHtml();
        assertTrue(declaration.startsWith("<?xml"));
        assertTrue(declaration.contains("version='1.0'"));
        assertTrue(declaration.endsWith("?>"));
        assertFalse(declaration.startsWith("<!--"));
    }

    @Test
    public void preservesSpacingAndAttributesInXmlDeclaration() {
        Document document = Jsoup.parse("<?xml   version='1.0' encoding='UTF-8'   ?><root/>", "", Parser.xmlParser());

        String declaration = document.childNode(0).outerHtml();
        assertTrue(declaration.startsWith("<?xml"));
        assertTrue(declaration.contains("version='1.0'"));
        assertTrue(declaration.contains("encoding='UTF-8'"));
        assertTrue(declaration.endsWith("?>"));
        assertFalse(declaration.contains("<!--"));
    }

    @Test
    public void keepsOrdinaryXmlCommentsAsComments() {
        Document document = Jsoup.parse("<!--ordinary comment--><root/>", "", Parser.xmlParser());

        assertEquals("<!--ordinary comment-->", document.childNode(0).outerHtml());
        assertTrue(document.childNode(0).outerHtml().startsWith("<!--"));
    }
}
