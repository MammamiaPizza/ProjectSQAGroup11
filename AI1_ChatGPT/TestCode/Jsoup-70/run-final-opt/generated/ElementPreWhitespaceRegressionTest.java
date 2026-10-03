package org.jsoup.nodes;

import org.jsoup.Jsoup;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ElementPreWhitespaceRegressionTest {

    @Test
    public void keepsCodeTextAdjacentToPreStartTag() {
        Element pre = new Element("pre");
        pre.appendElement("code").appendText("code[\n  first line\n  second line\n]");

        assertEquals("<pre><code>code[\n  first line\n  second line\n]</code></pre>", pre.outerHtml());
    }

    @Test
    public void keepsLeadingNewlineAndIndentationInsideNestedCode() {
        Element pre = new Element("pre");
        pre.appendElement("code").appendText("\n    code[\n        indented\n    ]");

        assertEquals("<pre><code>\n    code[\n        indented\n    ]</code></pre>", pre.toString());
    }

    @Test
    public void doesNotIndentCodeInsidePreWhenPreIsNestedAtDepth() {
        Element container = new Element("div");
        Element pre = container.appendElement("pre");
        pre.appendElement("code").appendText("code[\n  value\n]");

        String html = container.outerHtml();

        assertTrue(html, html.contains("<pre><code>code[\n  value\n]</code></pre>"));
    }

    @Test
    public void parsedPreCodeSerializationPreservesItsTextWhitespace() {
        Document document = Jsoup.parse("<div><pre><code>code[\n  parsed line\n]</code></pre></div>");
        Element pre = document.select("pre").first();

        assertEquals("<pre><code>code[\n  parsed line\n]</code></pre>", pre.outerHtml());
    }

    @Test
    public void preservesPlainPreformattedTextWithoutAddingIndentation() {
        Element pre = new Element("pre");
        pre.appendText("code[\n  plain text\n]");

        assertEquals("<pre>code[\n  plain text\n]</pre>", pre.outerHtml());
    }

    @Test
    public void serializesEmptyPreNormally() {
        assertEquals("<pre></pre>", new Element("pre").outerHtml());
    }

    @Test(expected = IllegalArgumentException.class)
    public void appendTextRejectsNullText() {
        new Element("pre").appendText(null);
    }
}
