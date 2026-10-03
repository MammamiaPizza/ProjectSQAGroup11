package org.jsoup.nodes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class DocumentNormaliseRegressionTest {

    @Test
    public void normaliseMovesBodiesOutOfNoscriptAndMergesTheirContents() {
        Document document = new Document("");
        document.outputSettings().prettyPrint(false);

        Element html = document.appendElement("html");
        Element head = html.appendElement("head");
        head.appendElement("script").text("one");
        Element noscript = head.appendElement("noscript");

        Element firstBody = noscript.appendElement("body");
        firstBody.appendElement("p").text("two");
        firstBody.appendElement("body").appendElement("p").text("three");

        assertSame(document, document.normalise());

        assertEquals("<html><head><script>one</script><noscript></noscript></head><body><p>two</p><p>three</p></body></html>",
                document.outerHtml());
        assertEquals(1, document.getElementsByTag("body").size());
        assertEquals(0, document.head().getElementsByTag("noscript").first().getElementsByTag("body").size());
    }

    @Test
    public void normaliseMergesSeparateBodyElementsInDocumentOrder() {
        Document document = Document.createShell("");
        document.outputSettings().prettyPrint(false);

        document.body().appendElement("p").text("one");
        document.getElementsByTag("html").first().appendElement("body").appendElement("p").text("two");

        document.normalise();

        assertEquals("<html><head></head><body><p>one</p><p>two</p></body></html>", document.outerHtml());
        assertEquals(1, document.getElementsByTag("body").size());
    }

    @Test
    public void normaliseMergesDuplicateHeadsWithoutLosingHeadContent() {
        Document document = Document.createShell("");
        document.outputSettings().prettyPrint(false);

        document.head().appendElement("title").text("first");
        Element html = document.getElementsByTag("html").first();
        html.prependElement("head").appendElement("meta").attr("name", "description");

        document.normalise();

        assertEquals(1, document.getElementsByTag("head").size());
        assertEquals("first", document.title());
        assertEquals(1, document.head().getElementsByTag("meta").size());
        assertEquals("description", document.head().getElementsByTag("meta").first().attr("name"));
    }

    @Test
    public void normaliseKeepsAnAlreadyNormalShellIntact() {
        Document document = Document.createShell("");
        document.outputSettings().prettyPrint(false);

        document.normalise();

        assertEquals("<html><head></head><body></body></html>", document.outerHtml());
        assertEquals(1, document.getElementsByTag("html").size());
        assertEquals(1, document.getElementsByTag("head").size());
        assertEquals(1, document.getElementsByTag("body").size());
    }
}