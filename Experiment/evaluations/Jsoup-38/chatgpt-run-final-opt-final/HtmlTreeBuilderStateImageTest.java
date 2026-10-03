package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HtmlTreeBuilderStateImageTest {

    @Test
    public void convertsImageStartTagToImgElement() {
        Document document = Jsoup.parse("<image>");
        assertEquals("<img />", document.body().html());
    }

    @Test
    public void convertsImageStartTagAndPreservesAttributes() {
        Document document = Jsoup.parse("<image src='photo.png' alt='A photo'>");
        assertEquals("<img src=\"photo.png\" alt=\"A photo\" />", document.body().html());
    }

    @Test
    public void handlesNormalImgStartTagAsVoidElement() {
        Document document = Jsoup.parse("<img src='photo.png'>");
        assertEquals("<img src=\"photo.png\" />", document.body().html());
    }

    @Test
    public void convertsMixedCaseImageStartTagToImgElement() {
        Document document = Jsoup.parse("<IMAGE>");
        assertEquals("<img />", document.body().html());
    }

@Test
public void parsesTitleContentsAsRcdata() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<title>One <b>Two</b> &amp; Three</title>");

    assertEquals("One <b>Two</b> & Three", document.head().getElementsByTag("title").first().text());
    assertEquals(0, document.head().getElementsByTag("b").size());
}

@Test
public void parsesStyleContentsAsRawtext() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<style>p < b { color: red; }</style>");

    assertEquals("p < b { color: red; }", document.head().getElementsByTag("style").first().data());
    assertEquals(0, document.head().getElementsByTag("b").size());
}

@Test
public void handlesLeadingWhitespaceAndTextBeforeBodyContent() {
    org.jsoup.nodes.Document whitespaceDocument = org.jsoup.Jsoup.parse("\t \r\n<p>text</p>");
    assertEquals("text", whitespaceDocument.body().getElementsByTag("p").first().text());

    org.jsoup.nodes.Document textDocument = org.jsoup.Jsoup.parse("x<p>text</p>");
    assertEquals(true, textDocument.body().text().contains("x"));
}
}
