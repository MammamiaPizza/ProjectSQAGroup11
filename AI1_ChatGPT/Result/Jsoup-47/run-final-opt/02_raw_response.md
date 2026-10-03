package org.jsoup.nodes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EntitiesXmlAttributeEscapeTest {
    @Test
    public void escapesBothAngleBracketsInXmlAttributeValues() {
        Document document = Document.createShell("");
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        Element link = document.body().appendElement("a");
        link.attr("title", "<p>One</p>");
        link.text("One");

        assertEquals("<a title=\"&lt;p&gt;One&lt;/p&gt;\">One</a>", link.outerHtml());
    }

    @Test
    public void escapesLessThanButNotGreaterThanInHtmlAttributeValues() {
        Document document = Document.createShell("");
        document.outputSettings().syntax(Document.OutputSettings.Syntax.html);
        Element link = document.body().appendElement("a");
        link.attr("title", "<p>One</p>");
        link.text("One");

        assertEquals("<a title=\"&lt;p>One&lt;/p>\">One</a>", link.outerHtml());
    }
}