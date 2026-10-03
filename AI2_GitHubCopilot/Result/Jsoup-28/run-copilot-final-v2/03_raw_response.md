package org.jsoup.nodes;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;

 import org.jsoup.parser.Parser;
 import org.jsoup.nodes.Document;
 import org.junit.Test;

 public class EntitiesTest {

     @Test
     public void unescape() {
         // Extended entity &angst without semicolon must NOT decode
         String input = "Hello &<> ® Å &angst π π 新 there &! ¾ © ...";
         String result = Entities.unescape(input);
         assertEquals("Hello &<> ® Å &angst π π 新 there &! ¾ © ...", result);
     }

     @Test
     public void noSpuriousDecodes() {
         // Entity prefixes in URL query strings must not trigger partial decodes
         String input = "http://www.foo.com?a=1&num_rooms=1&children=0&int=VA&b=2";
         String result = Entities.unescape(input);
         assertEquals("http://www.foo.com?a=1&num_rooms=1&children=0&int=VA&b=2", result);
     }

     @Test
     public void strictRequiresSemicolonForExtendedEntities() {
         // Strict mode: all named entity references must end with semicolon
         assertEquals("&angst &mid &wr &num", Entities.unescape("&angst &mid &wr &num", true));
     }

     @Test
     public void numericEntitiesDecodeFlexibly() {
         // Numeric entities decode with or without semicolons
         assertEquals("A A A A", Entities.unescape("&#65; &#x41; &#65 &#x41"));
     }

     @Test
     public void baseEntitiesDecodeWithoutSemicolon() {
         // amp, lt, gt, quot are base entities that may omit the semicolon
         assertEquals("& < > \"", Entities.unescape("&amp &lt &gt &quot"));
     }

     @Test
     public void unknownEntityPreserved() {
         // Completely unknown entity names are left unchanged
         assertEquals("&nonexistent; &bogus", Entities.unescape("&nonexistent; &bogus"));
     }

     @Test
     public void entityAtEndOfStringWithSemicolon() {
         // Trailing entity with semicolon decodes correctly
         assertEquals("test &", Entities.unescape("test &amp;"));
     }

     @Test
     public void moreAttributeUnescapes() {
         // In attribute values, &wr without semicolon must not be decoded
         String html = "<a href=\"http://example.com?size=23&mid-size=true&ok=&wr\">link</a>";
         Document doc = Parser.htmlParser().parseInput(html, "");
         String href = doc.select("a").first().attr("href");
         assertEquals("http://example.com?size=23&mid-size=true&ok=&wr", href);
     }

     @Test
     public void strictAttributeUnescapes() {
         // Strict (XML) parser: &mid without semicolon must not be decoded in attributes
         String html = "<tag attr=\"foo=bar&mid&lt=true\" />";
         Document doc = Parser.xmlParser().parseInput(html, "");
         String attr = doc.select("tag").first().attr("attr");
         assertTrue("&mid should not be decoded", attr.contains("&mid"));
         assertFalse("mid entity should not appear", attr.contains("\u2223"));
     }

     @Test
     public void doesNotFindShortestMatchingEntity() {
         // Parser must not decode &clubsuit (extended entity) when no semicolon follows.
         // It should be treated as unknown and escaped as &amp;clubsuit.
         String html = "<p>One &clubsuit e; ♣</p>";
         Document doc = Parser.htmlParser().parseInput(html, "");
         String bodyHtml = doc.body().html();
         assertEquals("One &amp;clubsuit e; ♣", bodyHtml);
     }

     @Test
     public void relaxedBaseEntityMatchAndStrictExtendedMatch() {
         // Base entities match without semicolon; extended entities require semicolon.
         // Without semicolon, icy and hopf are left as-is by the tokeniser (as raw text)
         // and thus must be escaped as &amp;icy and &amp;hopf in the output.
         String html = "<p>&amp;icy &amp;hopf &icy; &hopf;</p>";
         Document doc = Parser.htmlParser().parseInput(html, "");
         String bodyHtml = doc.body().html();
         assertEquals("&amp;icy &amp;hopf &icy; &hopf;", bodyHtml);
     }

     @Test
     public void mixedBaseAndExtendedEntities() {
         // Combine base entities (no semicolon = decode), extended (no semicolon = preserve),
         // extended with semicolon = decode, numeric = always decode
         String input = "&amp &angst &amp; &angst; &#60";
         String result = Entities.unescape(input);
         assertEquals("& &angst & Å <", result);
     }
 }