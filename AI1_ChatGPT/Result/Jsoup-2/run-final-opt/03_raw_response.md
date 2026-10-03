package org.jsoup.parser;

import org.jsoup.nodes.Document;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParserScriptDataTest {

    @Test
    public void parsesTextAfterScriptAsBodyTextInFullDocument() {
        Document document = Parser.parse(
                "<html><head></head><body>pre <script>inner</script> aft</body></html>",
                "http://example.com/");

        assertEquals("<html><head></head><body>pre <script>inner aft</script></body></html>",
                document.html());
    }

    @Test
    public void parsesTextAfterScriptAsBodyTextInBodyFragment() {
        Document document = Parser.parseBodyFragment(
                "pre <script>inner</script> aft",
                "http://example.com/");

        assertEquals("pre <script>inner aft</script>", document.body().html());
    }

    @Test
    public void keepsImmediatelyFollowingTextOutsideScript() {
        Document document = Parser.parseBodyFragment(
                "<script>inner</script>aft",
                "http://example.com/");

        assertEquals("<script>inner</script>aft", document.body().html());
    }
}