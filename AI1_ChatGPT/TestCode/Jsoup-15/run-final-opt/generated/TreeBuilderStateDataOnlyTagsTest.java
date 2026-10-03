package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TreeBuilderStateDataOnlyTagsTest {

    @Test
    public void scriptContentContainingMarkupLikeJavaScriptDoesNotBecomeDocumentText() {
        Document document = Jsoup.parse("<p>Hello <script>document.write('<p>'); i++;</script> There</p>");

        assertEquals("Hello There", document.text());
        assertEquals(1, document.select("p").size());
    }

    @Test
    public void scriptContentWithNonMatchingEndTagDoesNotExitScriptData() {
        Document document = Jsoup.parse(
                "<p>Before <script>var s = '</not-script>'; var markup = '<em>'; </script> After</p>");

        assertEquals("Before After", document.text());
    }

    @Test
    public void unclosedScriptContentDoesNotLeakIntoVisibleTextAtEof() {
        Document document = Jsoup.parse("<p>Hello <script>var value = '<p>'; value++;</script>");

        assertEquals("Hello", document.text());
    }

    @Test
    public void styleRawtextDoesNotBecomeVisibleDocumentText() {
        Document document = Jsoup.parse(
                "<p>Before <style>.notice { content: '<b>'; }</style> After</p>");

        assertEquals("Before After", document.text());
    }

    @Test
    public void titleRcdataPreservesMarkupLikeCharactersAndReturnsToNormalParsing() {
        Document document = Jsoup.parse(
                "<html><head><title>A <b>title</b> &amp; more</title></head><body>After</body></html>");

        assertEquals("A <b>title</b> & more", document.title());
        assertEquals("After", document.body().text());
    }
}
