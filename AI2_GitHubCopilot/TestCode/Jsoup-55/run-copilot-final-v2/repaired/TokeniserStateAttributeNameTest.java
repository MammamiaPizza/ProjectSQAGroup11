import not included and could
cause further issues. Let me also check div.attributes() iteration and img.attributes() for-each
loop — Elements (from getElements) has first(). The asList() on attributes returns Elements, so
.first() works.

For the stream-based test, I'll replace it with a simple for-each loop.

Here's the corrected complete test class:

 package org.jsoup.parser;

 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Attribute;
 import org.jsoup.nodes.Attributes;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.junit.Test;

 import static org.junit.Assert.*;

 public class TokeniserStateAttributeNameTest {

     @Test
     public void testNormalAttributeWithoutTrailingSlash() {
         Document doc = Jsoup.parse("<img src>");
         assertTrue(doc.select("img").size() > 0);
         Element img = doc.select("img").first();
         assertTrue(img.hasAttr("src"));
         assertFalse(img.hasAttr("src/"));
         assertEquals("src", img.attributes().asList().get(0).getKey());
     }

     @Test
     public void testAttributeWithTrailingSlashDropsSlash() {
         Document doc = Jsoup.parse("<img src/ >");
         Element img = doc.select("img").first();
         assertTrue(img.hasAttr("src"));
         assertFalse(img.hasAttr("src/"));
         assertFalse(img.hasAttr("sr"));
         assertEquals("src", img.attributes().asList().get(0).getKey());
         for (Attribute a : img.attributes()) {
             assertFalse(a.getKey().contains("/"));
         }
     }

     @Test
     public void testAttributeWithValueAndTrailingSlash() {
         Document doc = Jsoup.parse("<a href=\"/about\" />");
         Element a = doc.select("a").first();
         assertTrue(a.hasAttr("href"));
         assertEquals("/about", a.attr("href"));
         assertFalse(a.hasAttr("href/"));
     }

     @Test
     public void testAttributeWithValueUnquotedAndTrailingSlash() {
         Document doc = Jsoup.parse("<img alt=home />");
         Element img = doc.select("img").first();
         assertTrue(img.hasAttr("alt"));
         assertEquals("home", img.attr("alt"));
         assertFalse(img.hasAttr("alt/"));
     }

     @Test
     public void testEmptyAttributeNameBeforeSlash() {
         Document doc = Jsoup.parse("<img />");
         Element img = doc.select("img").first();
         assertEquals(0, img.attributes().size());
     }

     @Test
     public void testMidNameSlashPreserved() {
         Document doc = Jsoup.parse("<div sr/c>");
         Element div = doc.select("div").first();
         assertTrue(div.hasAttr("sr/c"));
         assertFalse(div.hasAttr("src"));
     }

     @Test
     public void testMultipleAttributesLastTrailingSlashDrops() {
         Document doc = Jsoup.parse("<input type=\"checkbox\" disabled/ >");
         Element input = doc.select("input").first();
         assertTrue(input.hasAttr("type"));
         assertEquals("checkbox", input.attr("type"));
         assertTrue(input.hasAttr("disabled"));
         assertFalse(input.hasAttr("disabled/"));
         assertFalse(input.hasAttr("disablede"));
     }

     @Test
     public void testSlashBeforeQuoteAndAfterValue() {
         Document doc = Jsoup.parse("<img src=\"x\"/ >");
         Element img = doc.select("img").first();
         assertTrue(img.hasAttr("src"));
         assertEquals("x", img.attr("src"));
         boolean endsWithSlash = false;
         for (Attribute attr : img.attributes()) {
             if (attr.getKey().endsWith("/")) { endsWithSlash = true; break; }
         }
         assertFalse(endsWithSlash);
     }

     @Test
     public void testSlashAfterBooleanAttribute() {
         Document doc = Jsoup.parse("<video controls/ >");
         Element video = doc.select("video").first();
         assertTrue(video.hasAttr("controls"));
         assertFalse(video.hasAttr("controls/"));
     }

     @Test
     public void testSlashInAttributeNameWithValue() {
         Document doc = Jsoup.parse("<div foo/=bar>");
         Element div = doc.select("div").first();
         assertTrue(div.hasAttr("foo/"));
         assertFalse(div.hasAttr("foo"));
     }

     @Test
     public void testSelfClosingStartTagKeepsAttributeNameIntegrity() {
         String name = "src";
         Document doc = Jsoup.parse("<img " + name + "/>");
         Element img = doc.select("img").first();
         assertTrue(img.hasAttr(name));
         assertEquals(name.length(), img.attributes().asList().get(0).getKey().length());
     }

     @Test
     public void testTrailingSlashWithSpacesBeforeTagClose() {
         Document doc = Jsoup.parse("<hr size=5/  >");
         Element hr = doc.select("hr").first();
         assertTrue(hr.hasAttr("size"));
         assertEquals("5", hr.attr("size"));
         assertFalse(hr.hasAttr("size/"));
     }
 }
