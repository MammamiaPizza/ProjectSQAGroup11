package org.jsoup.nodes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EntitiesXmlAttributeEscapeTest {
    @Test
    public void escapesLessThanButNotGreaterThanInXmlAttributeValues() {
        Document document = Document.createShell("");
        document.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        Element link = document.body().appendElement("a");
        link.attr("title", "<p>One</p>");
        link.text("One");

        assertEquals("<a title=\"&lt;p>One&lt;/p>\">One</a>", link.outerHtml());
    }

    @Test
    public void doesNotEscapeAngleBracketsInHtmlAttributeValues() {
        Document document = Document.createShell("");
        document.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.html);
        Element link = document.body().appendElement("a");
        link.attr("title", "<p>One</p>");
        link.text("One");

        assertEquals("<a title=\"<p>One</p>\">One</a>", link.outerHtml());
    }
}
