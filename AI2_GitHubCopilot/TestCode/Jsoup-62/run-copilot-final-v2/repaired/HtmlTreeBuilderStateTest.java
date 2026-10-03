package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import static org.junit.Assert.*;

public class HtmlTreeBuilderStateTest {

 @Test
 public void caseSensitiveEndTagClosesMatchingStartTag() {
     Document doc = Jsoup.parse("<r><X>A</X><y>B</y></r>");
     Element r = doc.select("r").first();
     assertNotNull("root <r> must exist", r);
     Elements kids = r.children();
     assertEquals("r has one child element", 1, kids.size());
     Element x = kids.get(0);
     assertEquals("X", x.tagName());
     assertEquals("A", x.ownText());
     Element y = x.child(0);
     assertEquals("y", y.tagName());
     assertEquals("B", y.ownText());
 }

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

 @Test
 public void upperCaseEndTagClosesLowerCaeStartTag() {
     Document doc = Jsoup.parse("<r><x>A</X><y>B</y></r>");
     Element r = doc.select("r").first();
     assertNotNull(r);
     Elements kids = r.children();
     assertEquals(1, kids.size());
     Element x = kids.get(0);
     assertEquals("x", x.tagName());
     assertEquals("A", x.ownText());
     Element y = x.child(0);
     assertEquals("y", y.tagName());
     assertEquals("B", y.ownText());
 }

 @Test
 public void caseDifferingUnknownTagsAreDistinctSiblings() {
     Document doc = Jsoup.parse("<r><X>A</X><x>B</x></r>");
     Element r = doc.select("r").first();
     Elements kids = r.children();
     assertEquals(1, kids.size());
     Element outer = kids.get(0);
     assertEquals("X", outer.tagName());
     assertEquals("A", outer.ownText());
     Element nested = outer.child(0);
     assertEquals("x", nested.tagName());
     assertEquals("B", nested.ownText());
 }

 @Test
 public void nestedMixedCaseUnknownTagsUnwindCorrectly() {
     Document doc = Jsoup.parse("<r><Foo><c>D</c></Foo></r>");
     Element r = doc.select("r").first();
     assertNotNull(r);
     assertEquals("r has one child", 1, r.children().size());
     Element foo = r.child(0);
     assertEquals("Foo", foo.tagName());
     assertEquals("foo has one child", 1, foo.children().size());
     Element c = foo.child(0);
     assertEquals("c", c.tagName());
     assertEquals("D", c.ownText());
 }

 @Test
 public void multipleSameNameUnknownTags() {
     Document doc = Jsoup.parse("<r><x>A</x><x>B</x></r>");
     Element r = doc.select("r").first();
     Elements kids = r.children();
     assertEquals(2, kids.size());
     assertEquals("A", kids.get(0).ownText());
     assertEquals("B", kids.get(1).ownText());
 }

 @Test
 public void endTagWithoutMachingStartTagIsIgnored() {
     Document doc = Jsoup.parse("<r></X><y>B</y></r>");
     Element r = doc.select("r").first();
     Elements kids = r.children();
     assertFalse("r should have at least one child", kids.isEmpty());
     assertEquals("B", kids.get(0).ownText());
 }

 @Test
 public void knownHtmlTagsNormalieToLowerCae() {
     Document doc = Jsoup.parse("<DIV><P>text</P></DIV>");
     Element div = doc.select("div").first();
     assertNotNull("DIV normalised to div", div);
     Element p = div.select("p").first();
     assertNotNull("P normalised to p", p);
     assertEquals("text", p.ownText());
 }

 @Test
 public void whitespaceAroundMixedCaeTags() {
     Document doc = Jsoup.parse("<r> <X>A </X> <y> B </y> </r>");
     Element r = doc.select("r").first();
     Elements kids = r.children();
     assertEquals(1, kids.size());
     Element x = kids.get(0);
     assertTrue(x.ownText().contains("A"));
     Element y = x.child(0);
     assertEquals("y", y.tagName());
     assertTrue(y.ownText().contains("B"));
 }

 @Test
 public void eofWithUnmatchedOpenTag() {
     Document doc = Jsoup.parse("<r><X>A");
     Element r = doc.select("r").first();
     assertNotNull(r);
     Element x = r.child(0);
     assertNotNull("<X> should be present even at EOF", x);
     assertEquals("A", x.ownText());
 }

 @Test
 public void balancedUnknownTagWithInnerText() {
     Document doc = Jsoup.parse("<custom>hello world</custom>");
     Element custom = doc.select("custom").first();
     assertNotNull(custom);
     assertEquals("hello world", custom.ownText());
 }

}
