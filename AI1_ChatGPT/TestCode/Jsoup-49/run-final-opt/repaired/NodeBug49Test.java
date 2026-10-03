package org.jsoup.nodes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class NodeBug49Test {

    @Test
    public void appendChildrenMovesAllLiveChildrenFromAnotherParentInOrder() {
        Document document = Document.createShell("");
        Element body = document.body();
        Element destination = body.appendElement("destination");
        Element source = body.appendElement("source");

        Element div3 = destination.appendElement("div3").appendText("Check");
        Element div4 = source.appendElement("div4");
        Element div1 = source.appendElement("div1");
        Element div2 = source.appendElement("div2");

        destination.insertChildren(destination.childNodeSize(), source.childNodes());

        assertEquals(4, destination.childNodeSize());
        assertSame(div3, destination.childNode(0));
        assertSame(div4, destination.childNode(1));
        assertSame(div1, destination.childNode(2));
        assertSame(div2, destination.childNode(3));
        assertEquals("Check", div3.text());
        assertEquals(0, source.childNodeSize());
        assertSame(destination, div4.parent());
        assertSame(destination, div1.parent());
        assertSame(destination, div2.parent());
        assertEquals("<destination><div3>Check</div3><div4></div4><div1></div1><div2></div2></destination>",
                destination.outerHtml());
    }

    @Test
    public void insertChildrenAtBeginningMovesAllChildrenWithoutSkippingAny() {
        Document document = Document.createShell("");
        Element body = document.body();
        Element destination = body.appendElement("destination");
        Element source = body.appendElement("source");

        Element existing = destination.appendElement("existing");
        Element first = source.appendElement("first");
        Element second = source.appendElement("second");
        Element third = source.appendElement("third");

        destination.insertChildren(0, source.childNodes());

        assertEquals(4, destination.childNodeSize());
        assertSame(first, destination.childNode(0));
        assertSame(second, destination.childNode(1));
        assertSame(third, destination.childNode(2));
        assertSame(existing, destination.childNode(3));
        assertEquals(0, source.childNodeSize());
        assertSame(destination, first.parent());
        assertSame(destination, second.parent());
        assertSame(destination, third.parent());
        assertEquals(0, first.siblingIndex());
        assertEquals(1, second.siblingIndex());
        assertEquals(2, third.siblingIndex());
        assertEquals(3, existing.siblingIndex());
        assertSame(second, first.nextSibling());
        assertSame(second, third.previousSibling());
    }

    @Test
    public void appendChildReparentsSingleChildAndUpdatesSourceAndSiblingLinks() {
        Document document = Document.createShell("");
        Element body = document.body();
        Element source = body.appendElement("source");
        Element destination = body.appendElement("destination");

        Element moved = source.appendElement("moved");
        Element remaining = source.appendElement("remaining");
        Element existing = destination.appendElement("existing");

        destination.appendChild(moved);

        assertEquals(1, source.childNodeSize());
        assertSame(remaining, source.childNode(0));
        assertEquals(0, remaining.siblingIndex());
        assertEquals(2, destination.childNodeSize());
        assertSame(existing, destination.childNode(0));
        assertSame(moved, destination.childNode(1));
        assertSame(destination, moved.parent());
        assertSame(existing, moved.previousSibling());
        assertNull(moved.nextSibling());
    }
}
