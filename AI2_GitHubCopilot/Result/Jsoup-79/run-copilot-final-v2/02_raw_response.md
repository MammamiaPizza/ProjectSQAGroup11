package org.jsoup.nodes;

 import org.junit.Test;
 import static org.junit.Assert.*;

 import java.util.Arrays;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.List;

 /**
  * Tests for {@link LeafNode} behavior, focusing on child-related operations.
  * Leaf nodes must not have children; childNodes()/childNodeSize() must return
  * empty/zero and mutation methods must throw UnsupportedOperationException.
  */
 public class LeafNodeTest {

     // --- child enumeration (target of the reported bug) ---

     @Test
     public void testChildNodesReturnsEmptyList() {
         LeafNode leaf = new TextNode("text");
         List<Node> children = leaf.childNodes();
         assertNotNull("childNodes() must never return null", children);
         assertTrue("leaf nodes must have no children", children.isEmpty());
     }

     @Test
     public void testChildNodeSizeIsZero() {
         LeafNode leaf = new TextNode("text");
         assertEquals("childNodeSize() must be 0 for leaf nodes", 0, leaf.childNodeSize());
     }

     @Test
     public void testChildNodesSizeConsistency() {
         LeafNode leaf = new TextNode("text");
         assertEquals("childNodes().size() must match childNodeSize()",
                      leaf.childNodeSize(), leaf.childNodes().size());
     }

     @Test
     public void testChildNodesAfterAttributeManipulation() {
         LeafNode leaf = new TextNode("text");
         // turn value into an Attributes map (multiple attributes)
         leaf.attr("href", "/page");
         leaf.attr("class", "foo");
         assertTrue("childNodes() must still be empty after attribute changes",
                    leaf.childNodes().isEmpty());
         assertEquals("childNodeSize() must still be 0", 0, leaf.childNodeSize());
     }

     // --- child mutation (must unconditionally throw) ---

     @Test(expected = UnsupportedOperationException.class)
     public void testAppendChildThrows() {
         new TextNode("text").appendChild(new TextNode("child"));
     }

     @Test
     public void testAppendChildExceptionMessage() {
         try {
             new TextNode("text").appendChild(new TextNode("child"));
             fail("Expected UnsupportedOperationException");
         } catch (UnsupportedOperationException e) {
             assertEquals("Leaf Nodes do not have child nodes.", e.getMessage());
         }
     }

     @Test(expected = UnsupportedOperationException.class)
     public void testInsertChildrenThrows() {
         Collection<Node> children = Arrays.asList(new TextNode("a"), new TextNode("b"));
         new TextNode("text").insertChildren(0, children);
     }

     @Test(expected = UnsupportedOperationException.class)
     public void testRemoveChildThrows() {
         // cannot remove because there are never children, but the call must still throw
         new TextNode("text").removeChild(new TextNode("nonexistent"));
     }

     @Test(expected = UnsupportedOperationException.class)
     public void testReplaceChildThrows() {
         new TextNode("text").replaceChild(new TextNode("old"), new TextNode("new"));
     }

     // --- additional boundary / corner cases ---

     @Test
     public void testChildNodesIsEmptyListNotShared() {
         LeafNode leaf1 = new TextNode("a");
         LeafNode leaf2 = new TextNode("b");
         List<Node> c1 = leaf1.childNodes();
         List<Node> c2 = leaf2.childNodes();
         assertNotSame("each leaf node must own its child list", c1, c2);
     }

     @Test
     public void testChildNodesOnNodeWithOnlyCoreValue() {
         // coreValue() path: value is a plain String, not Attributes
         LeafNode leaf = new TextNode("core");
         assertTrue(leaf.childNodes().isEmpty());
         assertEquals(0, leaf.childNodeSize());
     }
 }