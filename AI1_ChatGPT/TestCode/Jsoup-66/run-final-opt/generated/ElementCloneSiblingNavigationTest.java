package org.jsoup.nodes;

import org.jsoup.select.Elements;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class ElementCloneSiblingNavigationTest {

    @Test
    public void cloneOfElementCanFindFollowingElementSibling() {
        Element parent = new Element("div");
        Element first = parent.appendElement("p").attr("id", "first");
        Element second = parent.appendElement("p").attr("id", "second");

        Element clone = first.clone();

        assertSame(second, clone.nextElementSibling());
    }

    @Test
    public void cloneOfLastElementHasNoNextElementSibling() {
        Element parent = new Element("div");
        parent.appendElement("p").attr("id", "first");
        Element last = parent.appendElement("p").attr("id", "last");

        Element clone = last.clone();

        assertNull(clone.nextElementSibling());
    }

    @Test
    public void cloneFindsNextElementSiblingWhenTextNodesArePresent() {
        Element parent = new Element("div");
        parent.appendText("before");
        Element first = parent.appendElement("p").attr("id", "first");
        parent.appendText("between");
        Element second = parent.appendElement("p").attr("id", "second");
        parent.appendText("after");

        Element clone = first.clone();

        assertSame(second, clone.nextElementSibling());
    }

    @Test
    public void clonedChildrenNavigateAmongTheirClonedSiblings() {
        Element parent = new Element("div");
        Element container = parent.appendElement("section");
        container.appendElement("p").attr("id", "first");
        container.appendText("separator");
        container.appendElement("p").attr("id", "second");

        Element clone = container.clone();
        Element clonedFirst = clone.child(0);
        Element clonedSecond = clone.child(1);

        assertSame(clonedSecond, clonedFirst.nextElementSibling());

        Elements siblings = clonedFirst.siblingElements();
        assertEquals(1, siblings.size());
        assertSame(clonedSecond, siblings.get(0));
    }
}
