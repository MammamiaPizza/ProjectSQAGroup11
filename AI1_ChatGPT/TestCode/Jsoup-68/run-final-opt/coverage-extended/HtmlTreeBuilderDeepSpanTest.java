package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HtmlTreeBuilderDeepSpanTest {

    @Test
    public void parsesNormallyNestedSpans() {
        Document document = Jsoup.parse(nestedSpans(3, true, "normal"));

        assertEquals("normal", document.text());
        assertEquals(3, document.getElementsByTag("span").size());
    }

    @Test
    public void parsesSpansAroundMaximumScopeSearchDepth() {
        int belowLimit = HtmlTreeBuilder.MaxScopeSearchDepth - 1;
        int aboveLimit = HtmlTreeBuilder.MaxScopeSearchDepth + 1;

        Document belowDocument = Jsoup.parse(nestedSpans(belowLimit, true, "below"));
        Document aboveDocument = Jsoup.parse(nestedSpans(aboveLimit, true, "above"));

        assertEquals("below", belowDocument.text());
        assertEquals(belowLimit, belowDocument.getElementsByTag("span").size());
        assertEquals("above", aboveDocument.text());
        assertEquals(aboveLimit, aboveDocument.getElementsByTag("span").size());
    }

    @Test
    public void parsesVeryDeepUnclosedSpansWithoutStackOverflow() {
        int depth = 1000;
        Document document = Jsoup.parse(nestedSpans(depth, false, "deep"));

        assertEquals("deep", document.text());
        assertEquals(depth, document.getElementsByTag("span").size());
    }

    @Test
    public void parsesVeryDeepBalancedSpansWithoutStackOverflow() {
        int depth = 1000;
        Document document = Jsoup.parse(nestedSpans(depth, true, "balanced"));

        assertEquals("balanced", document.text());
        assertEquals(depth, document.getElementsByTag("span").size());
    }

    private String nestedSpans(int depth, boolean closeTags, String text) {
        StringBuilder html = new StringBuilder(depth * 13 + text.length());
        for (int i = 0; i < depth; i++) {
            html.append("<span>");
        }
        html.append(text);
        if (closeTags) {
            for (int i = 0; i < depth; i++) {
                html.append("</span>");
            }
        }
        return html.toString();
    }
}
