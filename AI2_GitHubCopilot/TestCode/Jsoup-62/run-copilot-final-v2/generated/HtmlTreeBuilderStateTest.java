package org.jsoup.parser;

 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.jsoup.select.Elements;
 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for HtmlTreeBuilderState end‐tag matching with case‐preserved unknown tags (Bug 62).
  * When parsing custom (non‐standard) elements the parser must match end tags to their
  * corresponding start tags so that siblings are produced, not unintended nesting.
  */
 public class HtmlTreeBuilderStateTest {

     /**
      * Core regression: </X> must close <X> so that <y> becomes a sibling under <r>,
      * not a child of <X>.  Expected tree: <r><X>A</X><y>B</y></r>.
      */
     @Test
     public void caseSensitiveEndTagClosesMatchingStartTag() {
         Document doc = Jsoup.parse("<r><X>A</X><y>B</y></r>");
         Element r = doc.select("r").first();
         assertNotNull("root <r> must exist", r);
         Elements kids = r.children();
         assertEquals("r must have exactly 2 children", 2, kids.size());
         assertEquals("first child owns A", "A", kids.get(0).ownText());
         assertEquals("second child owns B", "B", kids.get(1).ownText());
         // verify <y> is a direct child of <r>, not nested inside <X>
         assertSame("y must be sibling of X", r, kids.get(1).parent());
         assertTrue("X must not contain y", kids.get(0).getElementsByTag("y").isEmpty());
     }

     /**
      * Lower-case end tag </x> must still close an upper-case start tag <X>.
      */
     @Test
     public void lowerCaseEndTagClosesUpperCaeStartTag() {
         Document doc = Jsoup.parse("<r><X>A</x><y>B</y></r>");
         Element r = doc.select("r").first();
         Elements kids = r.children();
         assertEquals(2, kids.size());
         assertEquals("A", kids.get(0).ownText());
         assertEquals("B", kids.get(1).ownText());
         assertSame(r, kids.get(1).parent());
     }

     /**
      * Upper-case end tag </X> must close a lower-case start tag <x>.
      */
     @Test
     public void upperCaseEndTagClosesLowerCaeStartTag() {
         Document doc = Jsoup.parse("<r><x>A</X><y>B</y></r>");
         Element r = doc.select("r").first();
         Elements kids = r.children();
         assertEquals(2, kids.size());
         assertEquals("A", kids.get(0).ownText());
         assertEquals("B", kids.get(1).ownText());
         assertSame(r, kids.get(1).parent());
     }

     /**
      * Two unknown sibling tags that differ only in case are treated as distinct elements.
      */
     @Test
     public void caseDifferingUnknownTagsAreDistinctSiblings() {
         Document doc = Jsoup.parse("<r><X>A</X><x>B</x></r>");
         Element r = doc.select("r").first();
         Elements kids = r.children();
         assertEquals("different case produces distinct siblings", 2, kids.size());
         assertEquals("A", kids.get(0).ownText());
         assertEquals("B", kids.get(1).ownText());
     }

     /**
      * Nested unknown tags with mixed case – each end tag must close its immediate open tag.
      */
     @Test
     public void nestedMixedCaseUnknownTagsUnwindCorrectly() {
         Document doc = Jsoup.parse("<a><B><c>D</c></B></a>");
         Element a = doc.select("a").first();
         assertNotNull(a);
         assertEquals("a has one child", 1, a.children().size());
         Element b = a.child(0);
         // verify case is preserved (B, not b)
         assertEquals("tag name preserves case", "B", b.tagName());
         assertEquals("B has one child", 1, b.children().size());
         Element c = b.child(0);
         assertEquals("c", c.tagName());
         assertEquals("D", c.ownText());
     }

     /**
      * Multiple same-case unknown tags in sequence.
      */
     @Test
     public void multipleSameNameUnknownTags() {
         Document doc = Jsoup.parse("<r><x>A</x><x>B</x></r>");
         Element r = doc.select("r").first();
         Elements kids = r.children();
         assertEquals(2, kids.size());
         assertEquals("A", kids.get(0).ownText());
         assertEquals("B", kids.get(1).ownText());
     }

     /**
      * An end tag with no matching open tag on the stack is ignored (no crash).
      */
     @Test
     public void endTagWithoutMachingStartTagIsIgnored() {
         Document doc = Jsoup.parse("<r></X><y>B</y></r>");
         Element r = doc.select("r").first();
         // <y> must be present despite stray </X>
         Elements kids = r.children();
         assertFalse("r should have at least one child", kids.isEmpty());
         assertEquals("B", kids.get(0).ownText());
     }

     /**
      * Known HTML tags (e.g. <P>, <DIV>) still normalise to lower case.
      */
     @Test
     public void knownHtmlTagsNormalieToLowerCae() {
         Document doc = Jsoup.parse("<DIV><P>text</P></DIV>");
         Element div = doc.select("div").first();
         assertNotNull("DIV normalised to div", div);
         Element p = div.select("p").first();
         assertNotNull("P normalised to p", p);
         assertEquals("text", p.ownText());
     }

     /**
      * Whitespace between mixed-case tags must not affect end-tag matching.
      */
     @Test
     public void whitespaceAroundMixedCaeTags() {
         Document doc = Jsoup.parse("<r> <X>A </X> <y> B </y> </r>");
         Element r = doc.select("r").first();
         Elements kids = r.children();
         assertEquals(2, kids.size());
         assertTrue(kids.get(0).ownText().contains("A"));
         assertTrue(kids.get(1).ownText().contains("B"));
     }

     /**
      * EOF with unmatched open unknown tag: element remains open gracefully.
      */
     @Test
     public void eofWithUnmatchedOpenTag() {
         Document doc = Jsoup.parse("<r><X>A");
         Element r = doc.select("r").first();
         assertNotNull(r);
         Element x = r.child(0);
         assertNotNull("<X> should be present even at EOF", x);
         assertEquals("A", x.ownText());
     }

     /**
      * Fully balanced unknown tags with inner text only.
      */
     @Test
     public void balancedUnknownTagWithInnerText() {
         Document doc = Jsoup.parse("<custom>hello world</custom>");
         Element custom = doc.select("custom").first();
         assertNotNull(custom);
         assertEquals("hello world", custom.ownText());
     }
 }
