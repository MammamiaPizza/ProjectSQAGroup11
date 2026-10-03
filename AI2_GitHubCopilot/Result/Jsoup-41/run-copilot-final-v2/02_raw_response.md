package org.jsoup.nodes;

 import static org.junit.Assert.*;

 import org.jsoup.parser.Tag;
 import org.junit.Test;

 public class ElementTest {

     /**
      * Two elements with same tag, attributes, and text must be equal.
      */
     @Test
     public void testEqualElements_SameTagAttributesText() {
         Element e1 = new Element(Tag.valueOf("p"), "");
         e1.attr("class", "one");
         e1.appendChild(new TextNode("One", ""));

         Element e2 = new Element(Tag.valueOf("p"), "");
         e2.attr("class", "one");
         e2.appendChild(new TextNode("One", ""));

         assertEquals(e1, e2);
         assertEquals(e1.hashCode(), e2.hashCode());
     }

     /**
      * A cloned element must be equal to the original.
      */
     @Test
     public void testEqualElements_Clone() {
         Element original = new Element(Tag.valueOf("p"), "");
         original.attr("class", "one");
         original.appendChild(new TextNode("One", ""));

         Element clone = original.clone();

         assertEquals(original, clone);
         assertEquals(original.hashCode(), clone.hashCode());
     }

     /**
      * Two empty elements with the same tag must be equal.
      */
     @Test
     public void testEqualElements_EmptyElement() {
         Element e1 = new Element(Tag.valueOf("div"), "");
         Element e2 = new Element(Tag.valueOf("div"), "");

         assertEquals(e1, e2);
         assertEquals(e1.hashCode(), e2.hashCode());
     }

     /**
      * Elements with different tags are not equal.
      */
     @Test
     public void testDifferentTags() {
         Element p = new Element(Tag.valueOf("p"), "");
         Element div = new Element(Tag.valueOf("div"), "");

         assertFalse(p.equals(div));
     }

     /**
      * Elements with different attributes are not equal.
      */
     @Test
     public void testDifferentAttributes() {
         Element e1 = new Element(Tag.valueOf("p"), "");
         e1.attr("class", "one");

         Element e2 = new Element(Tag.valueOf("p"), "");
         e2.attr("class", "two");

         assertFalse(e1.equals(e2));
     }

     /**
      * Elements with different text content are not equal.
      */
     @Test
     public void testDifferentText() {
         Element e1 = new Element(Tag.valueOf("p"), "");
         e1.appendChild(new TextNode("One", ""));

         Element e2 = new Element(Tag.valueOf("p"), "");
         e2.appendChild(new TextNode("Two", ""));

         assertFalse(e1.equals(e2));
     }

     /**
      * Elements with identical nested children are equal.
      */
     @Test
     public void testEqualElements_NestedChildren() {
         Element parent1 = new Element(Tag.valueOf("div"), "");
         Element child1 = new Element(Tag.valueOf("p"), "");
         child1.appendChild(new TextNode("Hello", ""));
         parent1.appendChild(child1);

         Element parent2 = new Element(Tag.valueOf("div"), "");
         Element child2 = new Element(Tag.valueOf("p"), "");
         child2.appendChild(new TextNode("Hello", ""));
         parent2.appendChild(child2);

         assertEquals(parent1, parent2);
         assertEquals(parent1.hashCode(), parent2.hashCode());
     }

     /**
      * Elements with whitespace-only text children are equal.
      */
     @Test
     public void testEqualElements_WhitespaceOnlyText() {
         Element e1 = new Element(Tag.valueOf("span"), "");
         e1.appendChild(new TextNode("   ", ""));

         Element e2 = new Element(Tag.valueOf("span"), "");
         e2.appendChild(new TextNode("   ", ""));

         assertEquals(e1, e2);
     }

     /**
      * Equality must not depend on parent context or sibling index.
      */
     @Test
     public void testEqualElements_IgnoreParentContext() {
         Element orphan = new Element(Tag.valueOf("p"), "");
         orphan.attr("class", "one");
         orphan.appendChild(new TextNode("One", ""));

         Element parented = new Element(Tag.valueOf("p"), "");
         parented.attr("class", "one");
         parented.appendChild(new TextNode("One", ""));

         // Attach parented to a parent so siblingIndex and parentNode are set.
         Element dummyParent = new Element(Tag.valueOf("div"), "");
         dummyParent.appendChild(parented);

         assertEquals(orphan, parented);
         assertEquals(orphan.hashCode(), parented.hashCode());
     }

     /**
      * Hash code must be consistent across calls on the same object.
      */
     @Test
     public void testHashCodeConsistent() {
         Element e = new Element(Tag.valueOf("p"), "");
         e.attr("class", "one");
         e.appendChild(new TextNode("One", ""));

         int hc1 = e.hashCode();
         int hc2 = e.hashCode();

         assertEquals(hc1, hc2);
     }

     /**
      * Element must not be equal to null.
      */
     @Test
     public void testEqualsWithNull() {
         Element e = new Element(Tag.valueOf("p"), "");
         assertFalse(e.equals(null));
     }

     /**
      * Element must not be equal to an object of a different type.
      */
     @Test
     public void testEqualsWithDifferentType() {
         Element e = new Element(Tag.valueOf("p"), "");
         assertFalse(e.equals("some string"));
     }
 }