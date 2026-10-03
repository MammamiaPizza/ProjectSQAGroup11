package org.jsoup.nodes;

 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.junit.Test;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;

 /**
  * Tests for Node attribute handling with "abs:" prefix.
  * Covers the defect where hasAttr does not delegate for "abs:" and where attr might not
  * correctly resolve absolute URLs when an exact "abs:" attribute exists.
  */
 public class NodeAbsPrefixTest {

     @Test
     public void testAbsPrefixAttrWithHref() {
         Document doc = Jsoup.parse("<a href='/foo'>link</a>", "http://example.com/");
         Element a = doc.select("a").first();
         assertEquals("http://example.com/foo", a.attr("abs:href"));
     }

     @Test
     public void testAbsPrefixAttrWithoutHref() {
         Document doc = Jsoup.parse("<a>link</a>", "http://example.com/");
         Element a = doc.select("a").first();
         assertEquals("", a.attr("abs:href"));
     }

     @Test
     public void testHasAttrAbsPrefix() {
         Document doc = Jsoup.parse("<a href='/foo'>link</a>", "http://example.com/");
         Element a = doc.select("a").first();
         assertTrue(a.hasAttr("abs:href"));
     }

     @Test
     public void testHasAttrAbsPrefixMissing() {
         Document doc = Jsoup.parse("<a>link</a>", "http://example.com/");
         Element a = doc.select("a").first();
         assertFalse(a.hasAttr("abs:href"));
     }

     @Test
     public void testAttrAbsPrefixOverridesExactKey() {
         Document doc = Jsoup.parse("<a href='/foo'>link</a>", "http://example.com/");
         Element a = doc.select("a").first();
         a.attr("abs:href", "literal");
         assertEquals("literal", a.attr("abs:href"));
         assertEquals("http://example.com/foo", a.absUrl("href"));
     }

     @Test
     public void testAbsUrlDirect() {
         Document doc = Jsoup.parse("<a href='/foo'>link</a>", "http://example.com/");
         Element a = doc.select("a").first();
         assertEquals("http://example.com/foo", a.absUrl("href"));
     }

     @Test
     public void testAbsUrlMissingAttribute() {
         Document doc = Jsoup.parse("<a>link</a>", "http://example.com/");
         Element a = doc.select("a").first();
         assertEquals("", a.absUrl("href"));
     }

     @Test
     public void testAttrNonAbsKey() {
         Document doc = Jsoup.parse("<a href='/foo'>link</a>", "http://example.com/");
         Element a = doc.select("a").first();
         assertEquals("/foo", a.attr("href"));
     }

     @Test
     public void testHasAttrNonAbsKey() {
         Document doc = Jsoup.parse("<a href='/foo'>link</a>", "http://example.com/");
         Element a = doc.select("a").first();
         assertTrue(a.hasAttr("href"));
     }

     @Test
     public void testAbsPrefixAttrWithSrc() {
         Document doc = Jsoup.parse("<img src='/img.png'>", "http://example.com/");
         Element img = doc.select("img").first();
         assertEquals("http://example.com/img.png", img.attr("abs:src"));
     }

     @Test
     public void testHasAttrAbsPrefixWithSrc() {
         Document doc = Jsoup.parse("<img src='/img.png'>", "http://example.com/");
         Element img = doc.select("img").first();
         assertTrue(img.hasAttr("abs:src"));
     }

     @Test
     public void testAbsPrefixAttrWithNoBaseUri() {
         Document doc = Jsoup.parse("<a href='/foo'>link</a>");
         Element a = doc.select("a").first();
         assertEquals("", a.attr("abs:href"));
     }
 }
