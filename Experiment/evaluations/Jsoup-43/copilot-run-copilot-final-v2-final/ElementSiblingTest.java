package org.jsoup.nodes;

 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.parser.Tag;
 import org.jsoup.select.Elements;
 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for Element sibling methods, focusing on position-based (identity) behaviour
  * rather than content-based comparison. Targets the bug where duplicate-content siblings
  * were incorrectly matched by equals/toString instead of by position.
  */
 public class ElementSiblingTest {

     // ---- duplicate content: index -------------------------------------------------
     @Test
     public void testElementSiblingIndexWithDuplicates() {
         Document doc = Jsoup.parse("<div><span>X</span><span>X</span></div>");
         Element div = doc.body().child(0);
         Elements children = div.children();
         Element first = children.get(0);
         Element second = children.get(1);

         assertEquals("First child index should be 0", 0, (int) first.elementSiblingIndex());
         assertEquals("Second child (duplicate) index should be 1", 1, (int)
second.elementSiblingIndex());
     }

     // ---- duplicate content: siblingElements contents -------------------------------
     @Test
     public void testSiblingElementsWithDuplicates() {
         Document doc = Jsoup.parse("<div><span>X</span><span>X</span><p>Y</p></div>");
         Element div = doc.body().child(0);
         Elements children = div.children();
         Element first = children.get(0);
         Element second = children.get(1);
         Element third = children.get(2);

         // first child siblings
         Elements s1 = first.siblingElements();
         assertEquals("First should have 2 siblings", 2, s1.size());
         assertSame("First sibling of first is second child", second, s1.get(0));
         assertSame("Second sibling of first is third child", third, s1.get(1));

         // second child siblings
         Elements s2 = second.siblingElements();
         assertEquals("Second should have 2 siblings", 2, s2.size());
         assertSame("First sibling of second is first child", first, s2.get(0));
         assertSame("Second sibling of second is third child", third, s2.get(1));

         // third child siblings
         Elements s3 = third.siblingElements();
         assertEquals("Third should have 2 siblings", 2, s3.size());
         assertSame("First sibling of third is first child", first, s3.get(0));
         assertSame("Second sibling of third is second child", second, s3.get(1));
     }

     // ---- duplicate content: nextElementSibling ------------------------------------
     @Test
     public void testNextElementSiblingWithDuplicates() {
         Document doc = Jsoup.parse("<div><span>X</span><span>X</span><p>Y</p></div>");
         Element div = doc.body().child(0);
         Elements children = div.children();
         Element first = children.get(0);
         Element second = children.get(1);
         Element third = children.get(2);

         assertSame("Next of first is second", second, first.nextElementSibling());
         assertSame("Next of second (duplicate) is third", third, second.nextElementSibling());
         assertNull("Next of third is null", third.nextElementSibling());
     }

     // ---- duplicate content: previousElementSibling ---------------------------------
     @Test
     public void testPreviousElementSiblingWithDuplicates() {
         Document doc = Jsoup.parse("<div><span>X</span><span>X</span><p>Y</p></div>");
         Element div = doc.body().child(0);
         Elements children = div.children();
         Element first = children.get(0);
         Element second = children.get(1);
         Element third = children.get(2);

         assertNull("Prev of first is null", first.previousElementSibling());
         assertSame("Prev of second (duplicate) is first", first, second.previousElementSibling());
         assertSame("Prev of third is second", second, third.previousElementSibling());
     }

     // ---- first/lastElementSibling (do not rely on indexInList; self-inclusive) ----
     @Test
     public void testFirstElementSiblingWithDuplicates() {
         Document doc = Jsoup.parse("<div><span>X</span><span>X</span><p>Y</p></div>");
         Element div = doc.body().child(0);
         Elements children = div.children();
         Element first = children.get(0);
         Element second = children.get(1);
         Element third = children.get(2);

         assertSame("First sibling of first is itself", first, first.firstElementSibling());
         assertSame("First sibling of second is first", first, second.firstElementSibling());
         assertSame("First sibling of third is first", first, third.firstElementSibling());
     }

     @Test
     public void testLastElementSiblingWithDuplicates() {
         Document doc = Jsoup.parse("<div><span>X</span><span>X</span><p>Y</p></div>");
         Element div = doc.body().child(0);
         Elements children = div.children();
         Element first = children.get(0);
         Element second = children.get(1);
         Element third = children.get(2);

         assertSame("Last sibling of first is third", third, first.lastElementSibling());
         assertSame("Last sibling of second is third", third, second.lastElementSibling());
         assertSame("Last sibling of third is itself", third, third.lastElementSibling());
     }

     // ---- no parent ----------------------------------------------------------------
     @Test
     public void testSiblingMethodsNoParent() {
         Element orphan = new Element(Tag.valueOf("div"), "");
         assertEquals("Orphan sibling elements empty", 0, orphan.siblingElements().size());
         assertEquals("Orphan sibling index is 0", 0, (int) orphan.elementSiblingIndex());
         assertNull("Orphan next sibling null", orphan.nextElementSibling());
         assertNull("Orphan previous sibling null", orphan.previousElementSibling());
         // first/last would NPE on null parent; excluded per implementation
     }

     // ---- only child ---------------------------------------------------------------
     @Test
     public void testSiblingMethodsOnlyChild() {
         Element parent = new Element(Tag.valueOf("div"), "");
         Element only = new Element(Tag.valueOf("span"), "");
         parent.appendChild(only);

         assertEquals("Only child has no siblings", 0, only.siblingElements().size());
         assertEquals("Only child index is 0", 0, (int) only.elementSiblingIndex());
         assertNull("Only child next null", only.nextElementSibling());
         assertNull("Only child previous null", only.previousElementSibling());
         assertNull("Only child first null", only.firstElementSibling());
         assertNull("Only child last null", only.lastElementSibling());
     }

     // ---- mixed distinct and duplicates --------------------------------------------
     @Test
     public void testMixedDistinctAndDuplicateSiblings() {
         Document doc = Jsoup.parse("<div><a>1</a><b>2</b><a>1</a><c>3</c></div>");
         Element div = doc.body().child(0);
         Elements children = div.children();
         Element a1 = children.get(0);
         Element b = children.get(1);
         Element a2 = children.get(2);
         Element c = children.get(3);

         // indices
         assertEquals(0, (int) a1.elementSiblingIndex());
         assertEquals(1, (int) b.elementSiblingIndex());
         assertEquals(2, (int) a2.elementSiblingIndex());
         assertEquals(3, (int) c.elementSiblingIndex());

         // next
         assertSame(b, a1.nextElementSibling());
         assertSame(a2, b.nextElementSibling());
         assertSame(c, a2.nextElementSibling());
         assertNull(c.nextElementSibling());

         // previous
         assertNull(a1.previousElementSibling());
         assertSame(a1, b.previousElementSibling());
         assertSame(b, a2.previousElementSibling());
         assertSame(a2, c.previousElementSibling());

         // siblingElements order
         Elements sA1 = a1.siblingElements();
         assertEquals(3, sA1.size());
         assertSame(b, sA1.get(0));
         assertSame(a2, sA1.get(1));
         assertSame(c, sA1.get(2));

         Elements sA2 = a2.siblingElements();
         assertEquals(3, sA2.size());
         assertSame(a1, sA2.get(0));
         assertSame(b, sA2.get(1));
         assertSame(c, sA2.get(2));
     }

     // ---- self must never appear in siblingElements ---------------------------------
     @Test
     public void testSiblingElementsDoesNotIncludeSelf() {
         Document doc = Jsoup.parse("<div><span>X</span><span>X</span><span>Y</span></div>");
         Element div = doc.body().child(0);
         Elements children = div.children();
         Element first = children.get(0);

         for (Element sib : first.siblingElements()) {
             assertNotSame("Sibling must not be the element itself", first, sib);
         }

         Element second = children.get(1);
         for (Element sib : second.siblingElements()) {
             assertNotSame("Sibling must not be the element itself", second, sib);
         }
     }

     // ---- non-element children are excluded -----------------------------------------
     @Test
     public void testSiblingElementsIgnoresNonElementSiblings() {
         Element parent = new Element(Tag.valueOf("div"), "");
         Element el = new Element(Tag.valueOf("span"), "");
         parent.appendChild(el);
         parent.appendChild(new TextNode("ignored", ""));   // text node
         parent.appendChild(new Element(Tag.valueOf("p"), ""));

         Elements siblings = el.siblingElements();
         assertEquals("Only the <p> should be a sibling", 1, siblings.size());
         assertEquals("p", siblings.get(0).tagName());
     }

     // ---- complex traversal with several duplicates ---------------------------------
     @Test
     public void testComplexSiblingTraversalWithManyDuplicates() {
         Document doc =
Jsoup.parse("<div><p>A</p><span>B</span><p>A</p><span>C</span><p>A</p></div>");
         Element div = doc.body().child(0);
         Elements children = div.children();
         Element p1 = children.get(0);
         Element s1 = children.get(1);
         Element p2 = children.get(2);
         Element s2 = children.get(3);
         Element p3 = children.get(4);

         assertEquals(0, (int) p1.elementSiblingIndex());
         assertEquals(1, (int) s1.elementSiblingIndex());
         assertEquals(2, (int) p2.elementSiblingIndex());
         assertEquals(3, (int) s2.elementSiblingIndex());
         assertEquals(4, (int) p3.elementSiblingIndex());

         // forward chain
         Element cur = p1;
         for (int i = 0; i < 5; i++) {
             assertEquals("index at step " + i, i, (int) cur.elementSiblingIndex());
             cur = cur.nextElementSibling();
         }
         assertNull("end of forward chain", cur);

         // backward chain
         cur = p3;
         for (int i = 4; i >= 0; i--) {
             assertEquals("index at step " + i, i, (int) cur.elementSiblingIndex());
             cur = cur.previousElementSibling();
         }
         assertNull("end of backward chain", cur);
     }

     // ---- elementSiblingIndex contract: never null ----------------------------------
     @Test
     public void testElementSiblingIndexNeverNull() {
         Document doc = Jsoup.parse("<div><span>X</span><span>X</span></div>");
         Element div = doc.body().child(0);
         Element child = div.child(0);
         assertNotNull("elementSiblingIndex must not be null", child.elementSiblingIndex());
     }
 }
