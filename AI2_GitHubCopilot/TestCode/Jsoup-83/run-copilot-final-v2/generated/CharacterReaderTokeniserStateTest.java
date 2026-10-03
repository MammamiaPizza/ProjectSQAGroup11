package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.junit.Assert.*;

public class CharacterReaderTokeniserStateTest {

 // The rough attributes input and its expected output as given in the bug report
 private static final String ROUGH_ATTRS_INPUT = "<p =a>One<a[ <p>Something</a></p><a <p]>Else</a>";
 private static final String ROUGH_ATTRS_EXPECTED = "<p
=a>One<a[></a></p><p><a>Something</a></p><a]>Else</a>";

 @Test
 public void testParsesQuiteRoughAttributes() {
     Document doc = Jsoup.parse(ROUGH_ATTRS_INPUT);
     String bodyHtml = doc.body().html();
     assertEquals(ROUGH_ATTRS_EXPECTED, bodyHtml);
 }

 // handlesLessInTagThanAsNewTag: input reconstructed from error messages
 // Expected body: <p></p><p></p><div id="one"><span>Two</span></div>
 private static final String LESS_IN_TAG_INPUT = "<p <<p <div id='one'><span>Two</span>";
 private static final String LESS_IN_TAG_EXPECTED = "<p></p><p></p><div id=\"one\">\n
<span>Two</span>\n</div>";

 @Test
 public void testHandlesLessInTagThanAsNewTag() {
     Document doc = Jsoup.parse(LESS_IN_TAG_INPUT);
     String bodyHtml = doc.body().html();
     assertEquals(LESS_IN_TAG_EXPECTED, bodyHtml);
 }

 // BeforeAttributeName: '<' should close tag and start a new one
 @Test
 public void testLessThanInBeforeAttributeName() {
     Document doc = Jsoup.parse("<p a <b>");
     Element body = doc.body();
     assertEquals(2, body.childrenSize());
     Element firstChild = body.child(0);
     assertEquals("p", firstChild.tagName());
     assertTrue(firstChild.hasAttr("a"));
     Element secondChild = body.child(1);
     assertEquals("b", secondChild.tagName());
     assertTrue(secondChild.hasAttr(""));
 }

 // AttributeName: '<' after attribute name character (no space) closes tag
 @Test
 public void testLessThanDirectlyAfterAttributeName() {
     Document doc = Jsoup.parse("<p a<b>");
     Element body = doc.body();
     assertEquals(2, body.childrenSize());
     Element p = body.child(0);
     assertEquals("p", p.tagName());
     assertTrue(p.hasAttr("a"));
     Element b = body.child(1);
     assertEquals("b", b.tagName());
 }

 // AfterAttributeName: '<' after attribute name and whitespace closes tag
 @Test
 public void testLessThanInAfterAttributeName() {
     Document doc = Jsoup.parse("<p a = <b>");
     Element body = doc.body();
     assertEquals(2, body.childrenSize());
     Element p = body.child(0);
     assertEquals("p", p.tagName());
     assertTrue(p.hasAttr("a"));
     Element b = body.child(1);
     assertEquals("b", b.tagName());
 }

 // Attribute name containing '<' should not be treated as part of the name
 @Test
 public void testLessThanNotInAttributeName() {
     Document doc = Jsoup.parse("<p a<b>test</b>");
     Element p = doc.selectFirst("p");
     assertNotNull(p);
     assertTrue(p.hasAttr("a"));
     assertFalse(p.hasAttr("<"));
     assertFalse(p.hasAttr("a<"));
     Element b = doc.selectFirst("b");
     assertNotNull(b);
     assertEquals("test", b.text());
 }

 // Attribute value containing '<' should be preserved
 @Test
 public void testLessThanInAttributeValueSingleQuoted() {
     Document doc = Jsoup.parse("<a href='<'>link</a>");
     Element a = doc.selectFirst("a");
     assertEquals("<", a.attr("href"));
     assertEquals("link", a.text());
 }

 // Multiple '<' occurrences in quick succession
 @Test
 public void testMultipleLessThanInAttributes() {
     Document doc = Jsoup.parse("<p a<<b>text<c>");
     // After fix: first <p a> closes before the first '<',
     // so we get empty <p a> then possibly malformed from remaining?
     // Expect at least a <p> with attr "a" and no other attributes
     Element p = doc.selectFirst("p");
     assertNotNull(p);
     assertTrue(p.hasAttr("a"));
     assertEquals(1, p.attributes().size());
     // Verify that no '<' appears as part of any attribute name
     for (org.jsoup.nodes.Attribute attr : p.attributes()) {
         assertFalse(attr.getKey().contains("<"));
     }
 }

 // Tag name interrupted by '<' (consumeTagName edge)
 @Test
 public void testLessThanInsideTagName() {
     Document doc = Jsoup.parse("<a<b>");
     // The '<' should close the tag <a> and start a new one; so there should be two tags
     Element body = doc.body();
     assertEquals(2, body.childrenSize());
     assertEquals("a", body.child(0).tagName());
     assertEquals("b", body.child(1).tagName());
 }

 // Empty input stability
 @Test
 public void testEmptyInput() {
     Document doc = Jsoup.parse("");
     assertTrue(doc.body().html().isEmpty());
 }

 // Simple valid HTML to ensure no regression
 @Test
 public void testSimpleValidHtml() {
     Document doc = Jsoup.parse("<p>Hello</p>");
     assertEquals("Hello", doc.selectFirst("p").text());
 }

 // Boundary: tag with self-closing and less-than
 @Test
 public void testSelfClosingTagFollowedByLessThan() {
     Document doc = Jsoup.parse("<br/><a>");
     Element body = doc.body();
     assertTrue(body.childrenSize() >= 1);
     assertNotNull(doc.selectFirst("a"));
 }

}
