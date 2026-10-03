package org.jsoup.parser;

 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Attribute;
 import org.jsoup.nodes.Attributes;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.junit.Test;

 import static org.junit.Assert.*;

 /**
  * Tests for {@link TokeniserState#AttributeName} handling of '/' in attribute name.
  * Corresponds to bug #746: '/' was dropping the trailing character of the attribute name
  * instead of simply being removed.
  */
 public class TokeniserStateAttributeNameTest {

     @Test
     public void testNormalAttributeWithoutTrailingSlash() {
         // No slash: full name should be present
         Document doc = Jsoup.parse("<img src>");
         Element img = doc.selectFirst("img");
         assertTrue(img.hasAttr("src"));
         assertFalse(img.hasAttr("src/"));      // no slash present
         assertEquals("src", img.attributes().asList().get(0).getKey());
     }

     @Test
     public void testAttributeWithTrailingSlashDropsSlash() {
         // Bug: slash caused the last character of the attribute name to be lost.
         // After fix, the slash is stripped and name remains complete.
         Document doc = Jsoup.parse("<img src/ >");
         Element img = doc.selectFirst("img");
         assertTrue("attribute 'src' should be present", img.hasAttr("src"));
         assertFalse("attribute 'src/' should not exist", img.hasAttr("src/"));
         assertFalse("attribute 'sr' should not exist (last char kept)", img.hasAttr("sr"));
         assertEquals("src", img.attributes().asList().get(0).getKey());
         // Verify no slash remains in any attribute name
         for (Attribute a : img.attributes()) {
             assertFalse("attribute name must not contain slash: " + a.getKey(),
                         a.getKey().contains("/"));
         }
     }

     @Test
     public void testAttributeWithValueAndTrailingSlash() {
         // Slash after quoted value must not affect the attribute name
         Document doc = Jsoup.parse("<a href=\"/about\" />");
         Element a = doc.selectFirst("a");
         assertTrue(a.hasAttr("href"));
         assertEquals("/about", a.attr("href"));
         assertFalse(a.hasAttr("href/"));   // slash not in name
     }

     @Test
     public void testAttributeWithValueUnquotedAndTrailingSlash() {
         // Unquoted value before slash
         Document doc = Jsoup.parse("<img alt=home />");
         Element img = doc.selectFirst("img");
         assertTrue(img.hasAttr("alt"));
         assertEquals("home", img.attr("alt"));
         assertFalse(img.hasAttr("alt/"));
     }

     @Test
     public void testEmptyAttributeNameBeforeSlash() {
         // ie. <img /> -> no attribute, self-closing flag set
         Document doc = Jsoup.parse("<img />");
         Element img = doc.selectFirst("img");
         assertEquals("no attributes expected", 0, img.attributes().size());
     }

     @Test
     public void testMidNameSlashPreserved() {
         // Slash inside attribute name, not at end, must stay
         Document doc = Jsoup.parse("<div sr/c>");
         Element div = doc.selectFirst("div");
         assertTrue(div.hasAttr("sr/c"));
         assertFalse(div.hasAttr("src"));   // slash not removed, do not split
     }

     @Test
     public void testMultipleAttributesLastTrailingSlashDrops() {
         // Onl/y the final attribute's trailing slash should trigger self-closing and be dropped
         Document doc = Jsoup.parse("<input type=\"checkbox\" disabled/ >");
         Element input = doc.selectFirst("input");
         assertTrue(input.hasAttr("type"));
         assertEquals("checkbox", input.attr("type"));
         assertTrue(input.hasAttr("disabled"));
         assertFalse(input.hasAttr("disabled/"));
         assertFalse(input.hasAttr("disablede"));  // not losing last char
     }

     @Test
     public void testSlashBeforeQuoteAndAfterValue() {
         // e.g., <img src="x"/ >  (slash after closing quote, before space)
         Document doc = Jsoup.parse("<img src=\"x\"/ >");
         Element img = doc.selectFirst("img");
         assertTrue(img.hasAttr("src"));
         assertEquals("x", img.attr("src"));
         assertFalse(img.attributes().asList().stream()
                 .anyMatch(attr -> attr.getKey().endsWith("/")));
     }

     @Test
     public void testSlashAfterBooleanAttribute() {
         // boolean attribute without value, trailing slash
         Document doc = Jsoup.parse("<video controls/ >");
         Element video = doc.selectFirst("video");
         assertTrue(video.hasAttr("controls"));
         assertFalse(video.hasAttr("controls/"));
     }

     @Test
     public void testSlashInAttributeNameWithValue() {
         // Slash after name but before equal-sign: e.g., <div foo/=bar> – the slash should be
         // part of the name (not trailing) and stay; this is an unusual case.
         Document doc = Jsoup.parse("<div foo/=bar>");
         Element div = doc.selectFirst("div");
         assertTrue("attribute must be 'foo/'", div.hasAttr("foo/"));
         assertFalse("attribute 'foo' should not exist", div.hasAttr("foo"));
     }

     @Test
     public void testSelfClosingStartTagKeepsAttributeNameIntegrity() {
         // Verify the exact failure message "SelfClosingStartTag ignores last character"
         // does not happen: attribute name length is correct.
         String name = "src";
         Document doc = Jsoup.parse("<img " + name + "/>");
         Element img = doc.selectFirst("img");
         assertTrue(img.hasAttr(name));
         // ensure the name length equals 'src', not shorter
         assertEquals(name.length(), img.attributes().asList().get(0).getKey().length());
     }

     @Test
     public void testTrailingSlashWithSpacesBeforeTagClose() {
         // <hr size=5/  > – spaces after slash
         Document doc = Jsoup.parse("<hr size=5/  >");
         Element hr = doc.selectFirst("hr");
         assertTrue(hr.hasAttr("size"));
         assertEquals("5", hr.attr("size"));
         assertFalse(hr.hasAttr("size/"));
     }
 }
