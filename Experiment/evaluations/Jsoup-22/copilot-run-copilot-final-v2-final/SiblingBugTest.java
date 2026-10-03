package org.jsoup.nodes;

import static org.junit.Assert.*;
import java.util.List;
import org.junit.Test;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.jsoup.parser.Tag;

public class SiblingBugTest {

 private static Element createElement(String tagName) {
     return new Element(Tag.valueOf(tagName), "");
 }

 @Test
 public void testElementSiblingElementsExcludesSelf() {
     Element parent = createElement("div");
     Element childA = createElement("p");
     Element childB = createElement("span");
     Element childC = createElement("a");
     parent.appendChild(childA);
     parent.appendChild(childB);
     parent.appendChild(childC);

     Elements siblings = childB.siblingElements();
     assertEquals("sibling count should exclude self", 2, siblings.size());
     assertFalse("siblings should not contain self", siblings.contains(childB));
     assertTrue("siblings should contain other children", siblings.contains(childA));
     assertTrue("siblings should contain other children", siblings.contains(childC));
 }

 @Test
 public void testNodeSiblingNodesExcludesSelf() {
     Element parent = createElement("div");
     Element childA = createElement("p");
     Element childB = createElement("span");
     Element childC = createElement("a");
     parent.appendChild(childA);
     parent.appendChild(childB);
     parent.appendChild(childC);

     Node node = childB;
     List<Node> siblings = node.siblingNodes();
     assertEquals("sibling count should exclude self", 2, siblings.size());
     assertFalse("siblings should not contain self", siblings.contains(node));
     assertTrue("siblings should contain other child nodes", siblings.contains(childA));
     assertTrue("siblings should contain other child nodes", siblings.contains(childC));
 }

 @Test
 public void testOrphanElementSiblingElementsNoException() {
     Element orphan = createElement("div");
     try {
         Elements siblings = orphan.siblingElements();
         assertNotNull("orphan siblingElements should not return null", siblings);
         assertTrue("orphan siblingElements should be empty", siblings.isEmpty());
     } catch (NullPointerException e) {
         fail("siblingElements() should not throw NullPointerException on orphan node");
     }
 }

 @Test
 public void testOrphanNodeSiblingNodesReturnsEmpty() {
     Element orphan = createElement("div");
     Node node = orphan;
     try {
         List<Node> siblings = node.siblingNodes();
         assertNotNull("orphan siblingNodes should not return null", siblings);
         assertTrue("orphan siblingNodes should be empty", siblings.isEmpty());
     } catch (NullPointerException e) {
         fail("siblingNodes() should not throw NullPointerException on orphan node");
     }
 }

 @Test
 public void testOnlyChildElementHasEmptySiblingElements() {
     Element parent = createElement("div");
     Element child = createElement("p");
     parent.appendChild(child);
     Elements siblings = child.siblingElements();
     assertTrue("only child should have no sibling elements", siblings.isEmpty());
 }

 @Test
 public void testOnlyChildNodeHasEmptySiblingNodes() {
     Element parent = createElement("div");
     Element child = createElement("p");
     parent.appendChild(child);
     List<Node> siblings = child.siblingNodes();
     assertTrue("only child should have no sibling nodes", siblings.isEmpty());
 }

 @Test
 public void testSiblingElementsDoesNotContainParent() {
     Element parent = createElement("div");
     Element child = createElement("p");
     parent.appendChild(child);
     Elements siblings = child.siblingElements();
     assertFalse("sibling elements should not contain the parent", siblings.contains(parent));
 }

 @Test
 public void testSiblingElementsMaintainsOrder() {
     Element parent = createElement("div");
     Element first = createElement("p");
     Element middle = createElement("span");
     Element last = createElement("a");
     parent.appendChild(first);
     parent.appendChild(middle);
     parent.appendChild(last);

     Elements siblings = middle.siblingElements();
     assertEquals("should have two siblings", 2, siblings.size());
     assertEquals("first sibling should be the first child", first, siblings.get(0));
     assertEquals("second sibling should be the last child", last, siblings.get(1));
 }

 @Test
 public void testSiblingNodesMaintainsOrder() {
     Element parent = createElement("div");
     Element first = createElement("p");
     Element middle = createElement("span");
     Element last = createElement("a");
     parent.appendChild(first);
     parent.appendChild(middle);
     parent.appendChild(last);

     List<Node> siblings = middle.siblingNodes();
     assertEquals("should have two siblings", 2, siblings.size());
     assertEquals("first sibling should be the first child", first, siblings.get(0));
     assertEquals("second sibling should be the last child", last, siblings.get(1));
 }

 @Test
 public void testSiblingElementsReturnsElementsType() {
     Element parent = createElement("div");
     Element child = createElement("p");
     parent.appendChild(child);
     Elements siblings = child.siblingElements();
     assertNotNull("returned object should not be null", siblings);
     assertTrue("returned object should be an Elements instance", siblings instanceof Elements);
 }

}
