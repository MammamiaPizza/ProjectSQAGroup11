package org.jsoup.nodes;

import org.jsoup.Jsoup;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DocumentNormaliseRegressionTest {

    @Test
    public void bodySnippetPreservesTextOrderAroundNestedElement() {
        Document document = Jsoup.parseBodyFragment("foo <b>bar</b> baz");

        assertEquals("foo bar baz", document.body().text());

        String html = document.outerHtml();
        assertTrue(html.indexOf("foo") < html.indexOf("bar"));
        assertTrue(html.indexOf("bar") < html.indexOf("baz"));
    }

    @Test
    public void normaliseMovesHeadTextAfterExistingBodyText() {
        Document document = Document.createShell("");
        document.head().appendChild(new TextNode("head text", ""));
        document.body().appendChild(new TextNode("body text", ""));

        document.normalise();

        assertEquals("body text head text", document.body().text());
        assertTrue(document.outerHtml().indexOf("body text") < document.outerHtml().indexOf("head text"));
    }

    @Test
    public void shellProvidesHeadBodyAndEmptyTitle() {
        Document document = Document.createShell("http://example.com/");

        assertNotNull(document.head());
        assertNotNull(document.body());
        assertEquals("", document.title());
    }

    @Test
    public void titleIsTrimmedAndDocumentTextDoesNotRemoveHead() {
        Document document = Document.createShell("");
        document.title("  Example Title  ");

        document.text("Body content");

        assertEquals("Example Title", document.title());
        assertEquals("Body content", document.body().text());
        assertNotNull(document.head().getElementsByTag("title").first());
    }
}
