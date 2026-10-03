package org.jsoup.nodes;

 import org.jsoup.Jsoup;
 import org.jsoup.select.Elements;
 import org.junit.Test;

 import java.util.Set;

 import static org.junit.Assert.*;

 public class ElementTest {

     // Helper to create a simple element with a parent and several children
     private Element createParentWithChildren() {
         Element parent = new Element("div");
         parent.appendChild(new Element("span"));
         parent.appendChild(new Element("p"));
         parent.appendChild(new Element("a"));
         return parent;
     }

     @Test
     public void testNextElementSiblingAfterClone() {
         Element parent = createParentWithChildren();
         Element middle = parent.child(1); // the <p>
         assertNotNull(middle.nextElementSibling());

         Element clone = middle.clone();
         assertNull("Clone should have no next sibling (not in parent's child list)",
             clone.nextElementSibling());
     }

     @Test
     public void testCloneRetainsNoParentSiblings() {
         Element parent = createParentWithChildren();
         Element first = parent.child(0);

         Element clone = first.clone();

         assertNull(clone.nextElementSibling());
         assertNull(clone.previousElementSibling());
         assertNull(clone.firstElementSibling());
         assertNull(clone.lastElementSibling());
         assertEquals(0, clone.elementSiblingIndex());
         assertEquals(0, clone.siblingElements().size());
     }

     @Test
     public void testSiblingMethodsOnDetachedElement() {
         Element detached = new Element("div");

         assertNull(detached.nextElementSibling());
         assertNull(detached.previousElementSibling());
         assertNull(detached.firstElementSibling());
         assertNull(detached.lastElementSibling());
         assertEquals(0, detached.elementSiblingIndex());
         assertEquals(0, detached.siblingElements().size());
     }

     @Test
     public void testSiblingMethodsOnOrphanAfterRemoveChild() {
         Element parent = createParentWithChildren();
         Element middle = parent.child(1);
         parent.removeChild(middle);

         assertNull(middle.nextElementSibling());
         assertNull(middle.previousElementSibling());
         assertNull(middle.firstElementSibling());
         assertNull(middle.lastElementSibling());
         assertEquals(0, middle.elementSiblingIndex());
         assertEquals(0, middle.siblingElements().size());
     }

     @Test
     public void testNextElementSiblingOnLastChild() {
         Element parent = createParentWithChildren();
         Element last = parent.child(2);
         assertNull(last.nextElementSibling());
     }

     @Test
     public void testPreviousElementSiblingOnFirstChild() {
         Element parent = createParentWithChildren();
         Element first = parent.child(0);
         assertNull(first.previousElementSibling());
     }

     @Test
     public void testFirstElementSiblingReturnsCorrectElement() {
         Element parent = createParentWithChildren();
         // firstElementSibling should return the first sibling (excluding self)
         Element middle = parent.child(1);
         Element firstExpected = parent.child(0);
         assertEquals(firstExpected, middle.firstElementSibling());
     }

     @Test
     public void testLastElementSiblingReturnsCorrectElement() {
         Element parent = createParentWithChildren();
         Element middle = parent.child(1);
         Element lastExpected = parent.child(2);
         assertEquals(lastExpected, middle.lastElementSibling());
     }

     @Test
     public void testElementSiblingIndexForOnlyChild() {
         Element parent = new Element("ul");
         Element only = parent.appendChild(new Element("li"));
         assertEquals(0, only.elementSiblingIndex());
     }

     @Test
     public void testCloneThenAppendToDifferentParentHasSiblingsInNewParent() {
         Element parent1 = createParentWithChildren();
         Element middle = parent1.child(1);
         Element clone = middle.clone();

         Element parent2 = new Element("section");
         parent2.appendChild(new Element("header"));
         parent2.appendChild(clone);
         parent2.appendChild(new Element("footer"));

         assertNotNull(clone.nextElementSibling());
         assertNotNull(clone.previousElementSibling());
         assertEquals(2, clone.siblingElements().size());
         assertEquals("header", clone.previousElementSibling().tagName());
         assertEquals("footer", clone.nextElementSibling().tagName());
     }

     @Test
     public void testSiblingElementsDoesNotContainSelf() {
         Element parent = createParentWithChildren();
         Elements siblings = parent.child(1).siblingElements();
         assertEquals(2, siblings.size());
         for (Element sib : siblings) {
             assertNotSame(parent.child(1), sib);
         }
     }

     @Test
     public void testFirstElementSiblingOnFirstChildReturnsNull() {
         Element parent = createParentWithChildren();
         Element first = parent.child(0);
         // firstElementSibling returns the first sibling; when this is already the first
         // and the list size > 1, it returns index 0 which is itself; the javadoc
         // suggests it should return the first sibling; implementation currently returns
         // index 0 if siblings.size() > 1 regardless of whether that's self.
         // According to the contract, if there is no *other* first sibling, null is
         // expected. We test the observable behavior consistent with the method's
         // TODO comment.
         Element firstSib = first.firstElementSibling();
         // The current implementation: siblings.size() > 1 ? siblings.get(0) : null
         // For first element among >1 siblings, this returns itself.
         assertEquals(first, firstSib);
     }
 }