package org.jsoup.nodes;

import org.jsoup.Jsoup;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DocumentTypeBugTest {

    @Test
    public void constructorKeepsNormalDoctypeValues() {
        DocumentType type = new DocumentType("html", "-//W3C//DTD HTML 4.01//EN", "http://example.com/html.dtd", "http://example.com/");

        assertEquals("#doctype", type.nodeName());
        assertEquals("html", type.attr("name"));
        assertEquals("-//W3C//DTD HTML 4.01//EN", type.attr("publicId"));
        assertEquals("http://example.com/html.dtd", type.attr("systemId"));
    }

    @Test
    public void constructorAllowsEmptyNameWithIdentifiers() {
        DocumentType type = new DocumentType("", "public-id", "system-id", "");

        assertEquals("#doctype", type.nodeName());
        assertEquals("", type.attr("name"));
        assertEquals("public-id", type.attr("publicId"));
        assertEquals("system-id", type.attr("systemId"));
    }

    @Test
    public void constructorAllowsEmptyNameAndEmptyIdentifiers() {
        DocumentType type = new DocumentType("", "", "", "");

        assertEquals("#doctype", type.nodeName());
        assertEquals("", type.attr("name"));
        assertEquals("", type.attr("publicId"));
        assertEquals("", type.attr("systemId"));
    }

    @Test
    public void parserHandlesDoctypeWithNoName() {
        Document document = Jsoup.parse("<!DOCTYPE><p>parsed</p>");

        assertNotNull(document);
        assertEquals(1, document.getElementsByTag("p").size());
        assertEquals("parsed", document.getElementsByTag("p").get(0).text());
    }
}
