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

@Test
public void textNodeCoreValueCanBeReadAndUpdatedThroughItsNodeNameAttribute() {
    TextNode textNode = new TextNode("original", "");

    assertEquals("original", textNode.attr("#text"));
    assertTrue(textNode == textNode.attr("#text", "updated"));
    assertEquals("updated", textNode.attr("#text"));
    assertTrue(textNode.hasAttr("#text"));
}

@Test
public void textNodeAttributesPreserveCoreValueAndCanBeRemoved() {
    TextNode textNode = new TextNode("content", "");

    textNode.attributes();
    assertEquals("content", textNode.attr("#text"));

    textNode.attr("class", "lead");
    assertEquals("content", textNode.attr("#text"));
    assertEquals("lead", textNode.attr("class"));
    assertTrue(textNode.hasAttr("class"));

    textNode.removeAttr("class");
    assertTrue(!textNode.hasAttr("class"));
    assertEquals("content", textNode.attr("#text"));
}

@Test
public void detachedTextNodeHasNoBaseUri() {
    TextNode textNode = new TextNode("content", "http://example.com/base/");

    assertEquals("", textNode.baseUri());
}
}
