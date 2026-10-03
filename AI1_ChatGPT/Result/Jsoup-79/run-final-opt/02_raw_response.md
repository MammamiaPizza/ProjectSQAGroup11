package org.jsoup.nodes;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LeafNodeChildAccessTest {

    @Test
    public void textNodeReportsNoChildren() {
        TextNode textNode = new TextNode("text", "");

        assertEquals(0, textNode.childNodeSize());
    }

    @Test
    public void textNodeChildNodesIsEmptyAndCanBeReadRepeatedly() {
        TextNode textNode = new TextNode("text", "");

        List<Node> firstAccess = textNode.childNodes();
        List<Node> secondAccess = textNode.childNodes();

        assertTrue(firstAccess.isEmpty());
        assertTrue(secondAccess.isEmpty());
        assertEquals(0, textNode.childNodeSize());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void accessingMissingTextNodeChildThrowsIndexOutOfBoundsException() {
        TextNode textNode = new TextNode("text", "");

        textNode.childNode(0);
    }
}