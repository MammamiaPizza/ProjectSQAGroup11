package org.jsoup.nodes;

import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class ElementDuplicateSiblingTest {

    private Element newRoot() {
        return new Element(Tag.valueOf("div"), "");
    }

    @Test
    public void elementSiblingIndexUsesTreePositionForEqualContentSiblings() {
        Element root = newRoot();
        Element first = root.appendElement("p").text("same");
        Element second = root.appendElement("p").text("same");
        Element third = root.appendElement("p").text("same");

        assertEquals(Integer.valueOf(0), first.elementSiblingIndex());
        assertEquals(Integer.valueOf(1), second.elementSiblingIndex());
        assertEquals(Integer.valueOf(2), third.elementSiblingIndex());
    }

    @Test
    public void siblingElementsExcludesOnlyReceivingEqualContentElement() {
        Element root = newRoot();
        Element first = root.appendElement("p").text("same");
        Element second = root.appendElement("p").text("same");
        Element third = root.appendElement("p").text("same");

        Elements siblings = second.siblingElements();

        assertEquals(2, siblings.size());
        assertSame(first, siblings.get(0));
        assertSame(third, siblings.get(1));
    }

    @Test
    public void siblingNavigationUsesPositionWhenAdjacentElementsHaveEqualContent() {
        Element root = newRoot();
        Element first = root.appendElement("li").text("item");
        Element second = root.appendElement("li").text("item");
        Element third = root.appendElement("li").text("item");

        assertSame(first, second.previousElementSibling());
        assertSame(third, second.nextElementSibling());
        assertSame(first, second.firstElementSibling());
        assertSame(third, second.lastElementSibling());

        assertNull(first.previousElementSibling());
        assertNull(third.nextElementSibling());
    }

    @Test
    public void elementSiblingOperationsHandleOnlyChildAndDetachedElement() {
        Element root = newRoot();
        Element only = root.appendElement("span").text("only");
        Element detached = new Element(Tag.valueOf("span"), "").text("detached");

        assertEquals(Integer.valueOf(0), only.elementSiblingIndex());
        assertEquals(0, only.siblingElements().size());
        assertNull(only.previousElementSibling());
        assertNull(only.nextElementSibling());
        assertNull(only.firstElementSibling());
        assertNull(only.lastElementSibling());

        assertEquals(Integer.valueOf(0), detached.elementSiblingIndex());
        assertEquals(0, detached.siblingElements().size());
        assertNull(detached.previousElementSibling());
        assertNull(detached.nextElementSibling());
    }
}
