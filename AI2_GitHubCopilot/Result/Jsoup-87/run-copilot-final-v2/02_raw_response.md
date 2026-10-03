package org.jsoup.parser;

 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.jsoup.nodes.Node;
 import org.jsoup.nodes.TextNode;
 import org.junit.Test;

 import java.util.List;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;

 /**
  * Verifies that <a> (anchor) elements are never nested – an open <a> must be implicitly
  * closed before a new <a> start tag, regardless of tag‑name case (bug #1149).
  */
 public class LinkNestingTest {

     private Document parse(String html) {
         return Jsoup.parse(html);
     }

     private Document parsePreserveCase(String html) {
         return Parser.htmlParser().settings(ParseSettings.preserveCase).parseInput(html, "");
     }

     private Element body(Document doc) {
         return doc.body();
     }

     // --------------------------------------------------------------------
     // Lowercase / uppercase / mixed case with normalised (lowercase) tags
     // --------------------------------------------------------------------

     @Test
     public void testLowerCaseLinkCantNest() {
         Document doc = parse("<a>one<a>two</a>three</a>");
         List<Node> children = body(doc).childNodes();
         assertEquals(3, children.size());
         Element first = (Element) children.get(0);
         assertEquals("a", first.tagName());
         assertEquals("one", first.text());
         Element second = (Element) children.get(1);
         assertEquals("a", second.tagName());
         assertEquals("two", second.text());
         assertTrue(children.get(2) instanceof TextNode);
         assertEquals("three", ((TextNode) children.get(2)).text());
     }

     @Test
     public void testUpperCaseLinkCantNest() {
         Document doc = parse("<A>one<A>two</A>three</A>");
         List<Node> children = body(doc).childNodes();
         assertEquals(3, children.size());
         assertEquals("one", ((Element) children.get(0)).text());
         assertEquals("two", ((Element) children.get(1)).text());
         // both are normalised to lowercase
         assertEquals("a", ((Element) children.get(0)).tagName());
         assertEquals("a", ((Element) children.get(1)).tagName());
     }

     @Test
     public void testMixedCaseLinkCantNest() {
         Document doc = parse("<A>one<a>two</a>three</A>");
         List<Node> children = body(doc).childNodes();
         assertEquals(3, children.size());
         assertEquals("one", ((Element) children.get(0)).text());
         assertEquals("two", ((Element) children.get(1)).text());
         assertEquals("a", ((Element) children.get(0)).tagName());
         assertEquals("a", ((Element) children.get(1)).tagName());
     }

     // --------------------------------------------------------------------
     // Preserved‑case settings – tag names keep original case
     // --------------------------------------------------------------------

     @Test
     public void testPreservedCaseLinksCantNest() {
         Document doc = parsePreserveCase("<A>one<A>two</A>three</A>");
         List<Node> children = body(doc).childNodes();
         assertEquals(3, children.size());
         Element first = (Element) children.get(0);
         assertEquals("A", first.tagName()); // case preserved
         assertEquals("one", first.text());
         Element second = (Element) children.get(1);
         assertEquals("A", second.tagName());
         assertEquals("two", second.text());
         TextNode text = (TextNode) children.get(2);
         assertEquals("three", text.text());
     }

     @Test
     public void testPreservedCaseMixedCantNest() {
         Document doc = parsePreserveCase("<A>one<a>two</a>three</A>");
         List<Node> children = body(doc).childNodes();
         assertEquals(3, children.size());
         Element first = (Element) children.get(0);
         assertEquals("A", first.tagName());
         assertEquals("one", first.text());
         Element second = (Element) children.get(1);
         assertEquals("a", second.tagName()); // inner keeps its original case
         assertEquals("two", second.text());
     }

     // --------------------------------------------------------------------
     // Edge cases
     // --------------------------------------------------------------------

     @Test
     public void testLinksWithAttributes() {
         Document doc = parse("<a href='one'><a href='two'>text</a></a>");
         Element body = body(doc);
         assertEquals(2, body.children().size());
         Element first = body.child(0);
         assertEquals("a", first.tagName());
         assertEquals("one", first.attr("href"));
         assertEquals("", first.text()); // outer <a> was implicitly closed, so it has no content
         Element second = body.child(1);
         assertEquals("a", second.tagName());
         assertEquals("two", second.attr("href"));
         assertEquals("text", second.text());
     }

     @Test
     public void testEmptyLinks() {
         Document doc = parse("<a></a><a></a>");
         Element body = body(doc);
         assertEquals(2, body.children().size());
         assertEquals("a", body.child(0).tagName());
         assertEquals("a", body.child(1).tagName());
     }

     @Test
     public void testDeepNesting() {
         Document doc = parse("<a><A><a>text</a></A></a>");
         Element body = body(doc);
         // At every <a>/<A> start the previous <a> is closed → three siblings
         assertEquals(3, body.children().size());
         assertEquals("a", body.child(0).tagName());
         assertTrue(body.child(0).text().isEmpty());
         assertEquals("a", body.child(1).tagName());
         assertTrue(body.child(1).text().isEmpty());
         assertEquals("a", body.child(2).tagName());
         assertEquals("text", body.child(2).text());
     }

     @Test
     public void testLinkNestingDoesNotBreakOtherInlineElements() {
         Document doc = parse("<a><b>text</b></a>");
         Element body = body(doc);
         assertEquals(1, body.children().size());
         Element a = body.child(0);
         assertEquals("a", a.tagName());
         assertEquals(1, a.children().size());
         assertEquals("b", a.child(0).tagName());
     }

     @Test
     public void testLinkInsideParagraphNesting() {
         Document doc = parse("<p><a>one<a>two</a></a></p>");
         Element p = body(doc).child(0);
         assertEquals("p", p.tagName());
         assertEquals(2, p.children().size());
         assertEquals("a", p.child(0).tagName());
         assertEquals("one", p.child(0).text());
         assertEquals("a", p.child(1).tagName());
         assertEquals("two", p.child(1).text());
     }

     @Test
     public void testMultipleLinksWithWhitespaceBetween() {
         Document doc = parse("<a>first</a> <a>second</a>");
         Element body = body(doc);
         assertEquals(3, body.childNodeSize());
         assertEquals("a", body.child(0).tagName());
         assertEquals("first", body.child(0).text());
         assertTrue(body.childNode(1) instanceof TextNode);
         assertEquals(" ", ((TextNode) body.childNode(1)).text());
         assertEquals("a", body.child(2).tagName());
     }

     @Test
     public void testNestedLinkWithTextBeforeInnerLink() {
         // outer <a> has text, then inner <a>. Outer must close before inner, so text stays in
outer.
         Document doc = parse("<a>before<a>inner</a>after</a>");
         List<Node> children = body(doc).childNodes();
         assertEquals(3, children.size());
         Element first = (Element) children.get(0);
         assertEquals("a", first.tagName());
         assertEquals("before", first.text());
         Element second = (Element) children.get(1);
         assertEquals("a", second.tagName());
         assertEquals("inner", second.text());
         assertEquals("after", ((TextNode) children.get(2)).text());
     }
 }