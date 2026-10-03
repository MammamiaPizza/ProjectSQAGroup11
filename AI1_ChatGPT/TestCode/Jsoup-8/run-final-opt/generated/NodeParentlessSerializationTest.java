package org.jsoup.nodes;

import org.jsoup.parser.Tag;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class NodeParentlessSerializationTest {

    @Test
    public void parentlessElementToStringSerializesWithoutDocument() {
        Element element = new Element(Tag.valueOf("p"), "http://example.com/");

        assertNull(element.parent());
        assertNull(element.ownerDocument());
        assertEquals("<p></p>", element.toString());
    }

    @Test
    public void parentlessElementOuterHtmlSerializesWithoutDocument() {
        Element element = new Element(Tag.valueOf("div"), "http://example.com/");
        element.attr("class", "message");

        assertNull(element.ownerDocument());
        assertEquals("<div class=\"message\"></div>", element.outerHtml());
    }

    @Test
    public void attachedElementUsesOwnerDocumentOutputSettings() {
        Document document = new Document("http://example.com/");
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        Element element = new Element(Tag.valueOf("br"), "http://example.com/");
        document.appendChild(element);

        assertSame(document, element.ownerDocument());
        assertEquals("<br />", element.outerHtml());
    }
}
