package org.jsoup.nodes;

 import static org.junit.Assert.*;
 import org.junit.Test;

 import org.jsoup.parser.Tag;

 /**
  * Tests for Node behavior when the node has no parent (detached),
  * focusing on the NullPointerException bug in outerHtml()/toString()
  * when ownerDocument() returns null.
  */
 public class NodeBug8Test {

     @Test
     public void testParentlessElementToStringDoesNotThrow() {
         // The trigger case: a completely detached Element must serialize without NPE.
         Element e = new Element(Tag.valueOf("p"), "");
         try {
             String html = e.toString();
             assertNotNull("toString() should never return null", html);
         } catch (NullPointerException npe) {
             fail("parentless toString() must not throw NullPointerException");
         }
     }

     @Test
     public void testParentlessElementOuterHtmlDoesNotThrow() {
         Element e = new Element(Tag.valueOf("div"), "");
         try {
             String html = e.outerHtml();
             assertNotNull("outerHtml() should never return null", html);
         } catch (NullPointerException npe) {
             fail("parentless outerHtml() must not throw NullPointerException");
         }
     }

     @Test
     public void testParentlessElementWithChildrenToString() {
         Element parent = new Element(Tag.valueOf("ul"), "");
         Element child = new Element(Tag.valueOf("li"), "");
         parent.appendChild(child);
         try {
             String html = parent.toString();
             assertNotNull("toString() with children should not return null", html);
             assertTrue("should contain child tag", html.contains("<li>"));
         } catch (NullPointerException npe) {
             fail("parentless toString() with children must not throw NullPointerException");
         }
     }

     @Test
     public void testParentlessElementWithAttributesToString() {
         Element e = new Element(Tag.valueOf("a"), "");
         e.attr("href", "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         try {
             String html = e.toString();
             assertNotNull(html);
             assertTrue("should contain href attribute", html.contains("href"));
         } catch (NullPointerException npe) {
             fail("parentless toString() with attributes must not throw NullPointerException");
         }
     }

     @Test
     public void testParentlessElementAfterRemoveToString() {
         Document doc = Document.createShell("");
         Element e = new Element(Tag.valueOf("span"), "");
         doc.body().appendChild(e);
         e.remove(); // now detached
         try {
             String html = e.toString();
             assertNotNull(html);
         } catch (NullPointerException npe) {
             fail("toString() after removal must not throw NullPointerException");
         }
     }

     @Test
     public void testParentlessTextNodeToString() {
         TextNode t = new TextNode("hello", "");
         try {
             String html = t.toString();
             assertNotNull(html);
         } catch (NullPointerException npe) {
             fail("parentless TextNode toString() must not throw NullPointerException");
         }
     }

     @Test
     public void testNodeWithParentNotInDocumentToString() {
         // A node with a parent that itself has no document ancestor – ownerDocument() returns
null.
         Element parent = new Element(Tag.valueOf("section"), "");
         Element child = new Element(Tag.valueOf("p"), "");
         parent.appendChild(child);
         try {
             String html = child.toString();
             assertNotNull(html);
         } catch (NullPointerException npe) {
             fail("toString() with non-document parent must not throw NullPointerException");
         }
     }

     @Test
     public void testNodeInDocumentToStringReturnsExpectedHtml() {
         Document doc = Document.createShell("");
         Element el = new Element(Tag.valueOf("h1"), "");
         el.appendChild(new TextNode("Title", ""));
         doc.body().appendChild(el);
         String html = el.toString();
         assertTrue("should contain h1 tag", html.startsWith("<h1>"));
         assertTrue("should contain Title text", html.contains("Title"));
     }

     @Test
     public void testOwnerDocumentForDocument() {
         Document doc = Document.createShell("");
         assertSame("Document should own itself", doc, doc.ownerDocument());
     }

     @Test
     public void testOwnerDocumentForDetachedNode() {
         Element e = new Element(Tag.valueOf("div"), "");
         assertNull("detached node has no owner document", e.ownerDocument());
     }

     @Test
     public void testToStringEqualsOuterHtmlForParentless() {
         Element e = new Element(Tag.valueOf("img"), "");
         try {
             String viaToString = e.toString();
             String viaOuter = e.outerHtml();
             assertEquals("toString() must equal outerHtml()", viaOuter, viaToString);
         } catch (NullPointerException npe) {
             fail("parentless serialization methods must not throw NullPointerException");
         }
     }

     @Test
     public void testOuterHtmlVisitorDoesNotThrowOnParentless() {
         // Exercises the OuterHtmlVisitor path explicitly.
         Element e = new Element(Tag.valueOf("br"), "");
         try {
             StringBuilder accum = new StringBuilder();
             new NodeTraversor(
                 new OuterHtmlVisitor(accum, e.ownerDocument() != null ?
e.ownerDocument().outputSettings() : new Document.OutputSettings())
             ).traverse(e);
             assertNotNull(accum.toString());
         } catch (NullPointerException npe) {
             fail("OuterHtmlVisitor should handle null OutputSettings gracefully");
         }
     }
 }
