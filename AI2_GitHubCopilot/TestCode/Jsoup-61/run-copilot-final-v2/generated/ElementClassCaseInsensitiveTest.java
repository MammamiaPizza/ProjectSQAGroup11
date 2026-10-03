package org.jsoup.nodes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class ElementClassCaseInsensitiveTest {

 @Test
 public void testHasClassExactMatch() {
     Element el = new Element("div").attr("class", "foo");
     assertTrue(el.hasClass("foo"));
 }

 @Test
 public void testHasClassCaseInsensitive() {
     Element el = new Element("div").attr("class", "FOO");
     assertTrue("hasClass should be case-insensitive", el.hasClass("foo"));

     el = new Element("div").attr("class", "Foo");
     assertTrue("hasClass should be case-insensitive", el.hasClass("FOO"));
 }

 @Test
 public void testHasClassMixedCaseMismatch() {
     Element el = new Element("div").attr("class", "MyClass");
     assertTrue("hasClass should be case-insensitive", el.hasClass("myclass"));
     assertTrue("hasClass should be case-insensitive", el.hasClass("MYCLASS"));
 }

 @Test
 public void testHasClassWithMultipleClasses() {
     Element el = new Element("div").attr("class", "foo bar");

     assertTrue("should match token 'foo'", el.hasClass("foo"));
     assertTrue("should match token 'bar'", el.hasClass("bar"));
     assertFalse("should not match substring 'fo'", el.hasClass("fo"));
     assertFalse("should not match phrase with space", el.hasClass("foo bar"));
     assertFalse("should not match non-existing token", el.hasClass("baz"));
 }

 @Test
 public void testHasClassEmptyString() {
     Element el = new Element("div").attr("class", "");
     assertFalse(el.hasClass(""));
     assertFalse(el.hasClass("nonexistent"));
 }

 @Test(expected = NullPointerException.class)
 public void testHasClassNull() {
     Element el = new Element("div").attr("class", "foo");
     el.hasClass(null);
 }

 @Test
 public void testHasClassWithLeadingTrailingSpaces() {
     Element el = new Element("div").attr("class", "  foo  ");
     assertTrue("should match token 'foo' regardless of surrounding whitespace",
el.hasClass("foo"));
     assertFalse("should not match partial token inside whitespace", el.hasClass("  "));
 }

 @Test
 public void testGetElementsByClassCaseInsensitive() {
     Document doc = Jsoup.parse("<div><p class='Foo'>A</p><span class='foo'>B</span><a
class='FOO'>C</a></div>");
     Element div = doc.select("div").first();

     Elements els = div.getElementsByClass("foo");
     assertEquals("case-insensitive getElementsByClass should match all variants", 3, els.size());

     els = div.getElementsByClass("Foo");
     assertEquals("exact-case should also match all variants", 3, els.size());
 }

 @Test
 public void testSelectByClassCaseInsensitive() {
     Document doc = Jsoup.parse("<div><p class='Test'>1</p><span class='test'>2</span><a
class='TEST'>3</a></div>");

     Elements els = doc.select(".test");
     assertEquals("CSS class selector should be case-insensitive in HTML", 3, els.size());

     els = doc.select(".Test");
     assertEquals(3, els.size());

     els = doc.select(".nonexistent");
     assertTrue(els.isEmpty());
 }

 @Test
 public void testSelectByClassCaseSensitiveInXml() {
     Document xmlDoc = Jsoup.parse("<root><div class='FOO'>text</div></root>", "",
Parser.xmlParser());

     Elements els = xmlDoc.select(".foo");
     assertTrue("XML class selectors must be case-sensitive, lowercase should not match uppercase
class", els.isEmpty());
     els = xmlDoc.select(".FOO");
     assertEquals(1, els.size());
 }

 @Test
 public void testClassNamesPreservesOriginalCase() {
     Element el = new Element("div").attr("class", "Foo Bar");
     assertTrue(el.hasClass("foo"));

     Set<String> classNames = el.classNames();
     assertTrue("classNames set must contain original-case name", classNames.contains("Foo"));
     assertTrue(classNames.contains("Bar"));
     assertFalse("classNames set should NOT contain lowercased variant",
classNames.contains("foo"));
 }

 @Test
 public void testSelectByClassWithMultipleClasses() {
     Document doc = Jsoup.parse("<div class='foo bar'>text</div><span class='baz
foo'>other</span>");
     Elements els = doc.select(".foo");
     assertEquals("should match elements where 'foo' appears among other classes", 2, els.size());

     els = doc.select(".bar");
     assertEquals(1, els.size());

     els = doc.select(".none");
     assertTrue(els.isEmpty());
 }

}
