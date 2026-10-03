package org.jsoup.nodes;

import java.util.List;

import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SiblingBehaviorTest {

    private Element element(String name) {
        return new Element(Tag.valueOf(name), "");
    }

    @Test
    public void elementSiblingElementsExcludesElementItself() {
        Element parent = element("parent");
        Element first = element("first");
        Element middle = element("middle");
        Element last = element("last");

        parent.appendChild(first);
        parent.appendChild(middle);
        parent.appendChild(last);

        Elements siblings = middle.siblingElements();

        assertEquals(2, siblings.size());
        assertTrue(siblings.contains(first));
        assertTrue(siblings.contains(last));
        assertFalse(siblings.contains(middle));
    }

    @Test
    public void nodeSiblingNodesExcludesNodeItself() {
        Element parent = element("parent");
        TextNode first = new TextNode("one", "");
        TextNode middle = new TextNode("two", "");
        TextNode last = new TextNode("three", "");

        parent.appendChild(first);
        parent.appendChild(middle);
        parent.appendChild(last);

        List<Node> siblings = middle.siblingNodes();

        assertEquals(2, siblings.size());
        assertTrue(siblings.contains(first));
        assertTrue(siblings.contains(last));
        assertFalse(siblings.contains(middle));
    }

    @Test
    public void orphanNodeSiblingElementsReturnsNull() {
        Element orphan = element("orphan");

        assertNull(orphan.siblingElements());
    }

    @Test
    public void orphanNodeSiblingNodesReturnsEmptyList() {
        TextNode orphan = new TextNode("orphan", "");

        List<Node> siblings = orphan.siblingNodes();

        assertTrue(siblings.isEmpty());
    }
}