package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.junit.Assert.*;

public class CharacterReaderTokeniserStateTest {

 private static final String ROUGH_ATTRS_INPUT = "<p =a>One<a[ <p>Something</a></p><a <p]>Else</a>";
 private static final String ROUGH_ATTRS_EXPECTED = "<p
=a>One<a[></a></p><p><a>Something</a></p><a]>Else</a>";

 @Test
 public void testParsesQuiteRoughAttributes() {
     Document doc = Jsoup.parse(ROUGH_ATTRS_INPUT);
     String bodyHtml = doc.body().html();
     assertEquals(ROUGH_ATTRS_EXPECTED, bodyHtml);
 }

 private static final String LESS_IN_TAG_INPUT = "<p <<p <div id='one'><span>Two</span>";
 private static final String LESS_IN_TAG_EXPECTED = "<p></p><p></p><div
id=\"one\">\n<span>Two</span>\n</div>";

 @Test
 public void testHandlesLessInTagThanAsNewTag() {
     Document doc = Jsoup.parse(LESS_IN_TAG_INPUT);
     String bodyHtml = doc.body().html();
     assertEquals(LESS_IN_TAG_EXPECTED, bodyHtml);
 }

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

 @Test
 public void testLessThanInAttributeValueSingleQuoted() {
     Document doc = Jsoup.parse("<a href='<'>link</a>");
     Element a = doc.selectFirst("a");
     assertEquals("<", a.attr("href"));
     assertEquals("link", a.text());
 }

 @Test
 public void testMultipleLessThanInAttributes() {
     Document doc = Jsoup.parse("<p a<<b>text<c>");
     Element p = doc.selectFirst("p");
     assertNotNull(p);
     assertTrue(p.hasAttr("a"));
     assertEquals(1, p.attributes().size());
     for (org.jsoup.nodes.Attribute attr : p.attributes()) {
         assertFalse(attr.getKey().contains("<"));
     }
 }

 @Test
 public void testLessThanInsideTagName() {
     Document doc = Jsoup.parse("<a<b>");
     Element body = doc.body();
     assertEquals(2, body.childrenSize());
     assertEquals("a", body.child(0).tagName());
     assertEquals("b", body.child(1).tagName());
 }

 @Test
 public void testEmptyInput() {
     Document doc = Jsoup.parse("");
     assertTrue(doc.body().html().isEmpty());
 }

 @Test
 public void testSimpleValidHtml() {
     Document doc = Jsoup.parse("<p>Hello</p>");
     assertEquals("Hello", doc.selectFirst("p").text());
 }

 @Test
 public void testSelfClosingTagFollowedByLessThan() {
     Document doc = Jsoup.parse("<br/><a>");
     Element body = doc.body();
     assertTrue(body.childrenSize() >= 1);
     assertNotNull(doc.selectFirst("a"));
 }

}