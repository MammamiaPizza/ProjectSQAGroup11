package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HtmlTreeBuilderDeepStackTest {

    @Test
    public void parsesShallowNestedElementsIntoExpectedHierarchy() {
        Document document = Jsoup.parse("<div><div><div>text</div></div></div>");

        Element outer = document.body().child(0);
        Element middle = outer.child(0);
        Element inner = middle.child(0);

        assertEquals("div", outer.tagName());
        assertEquals("div", middle.tagName());
        assertEquals("div", inner.tagName());
        assertEquals("text", inner.text());
    }

    @Test
    public void handlesVeryDeepBalancedElementStack() {
        final int depth = 12000;
        StringBuilder html = new StringBuilder(depth * 11);

        for (int i = 0; i < depth; i++) {
            html.append("<div>");
        }
        html.append("deep");
        for (int i = 0; i < depth; i++) {
            html.append("</div>");
        }

        Document document = Jsoup.parse(html.toString());
        Element current = document.body().child(0);

        for (int i = 0; i < depth; i++) {
            assertNotNull(current);
            assertEquals("div", current.tagName());
            if (i < depth - 1) {
                assertEquals(1, current.children().size());
                current = current.child(0);
            }
        }

        assertEquals("deep", current.text());
    }

    @Test
    public void retainsDeepOpenElementStackAtEndOfInput() {
        final int depth = 12000;
        StringBuilder html = new StringBuilder(depth * 5 + 4);

        for (int i = 0; i < depth; i++) {
            html.append("<div>");
        }
        html.append("end");

        Document document = Jsoup.parse(html.toString());
        Element current = document.body().child(0);

        for (int i = 0; i < depth; i++) {
            assertNotNull(current);
            assertEquals("div", current.tagName());
            if (i < depth - 1) {
                assertEquals(1, current.children().size());
                current = current.child(0);
            }
        }

        assertEquals("end", current.text());
    }
}