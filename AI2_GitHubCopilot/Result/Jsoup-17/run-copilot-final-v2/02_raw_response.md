package org.jsoup.parser;

 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Defects4J Jsoup 17b: tests for correct handling of the NULL character (U+0000)
  * in character tokens. The buggy version may drop or mishandle the NUL character
  * instead of preserving it as text. These tests expect the NUL to appear in
  * parsed text output and will fail on the buggy implementation.
  */
 public class NullCharacterHandlingTest {

     // The NULL character as a string for convenience
     private static final String NUL = "\0";
     private static final String NUL_DOUBLE = "\0\0";
     private static final String NUL_WITH_FOO = "\0foo";
     private static final String FOO_WITH_NUL = "foo\0";

     // --- cases that exercise NUL in regular text ---

     @Test
     public void testNullCharacterInParagraph() {
         Document doc = Jsoup.parse("<p>" + NUL + "</p>");
         Element p = doc.select("p").first();
         assertNotNull("Paragraph should exist", p);
         assertEquals("Paragraph text should contain the null character", NUL, p.text());
     }

     @Test
     public void testNullCharacterInBodyText() {
         // NUL directly inside body (no wrapping element)
         Document doc = Jsoup.parse(NUL);
         String bodyText = doc.body().text();
         assertTrue("Body text should contain the null character",
                    bodyText.contains(NUL) && bodyText.length() > 0);
     }

     @Test
     public void testMultipleNullCharacters() {
         Document doc = Jsoup.parse("<p>" + NUL_DOUBLE + "</p>");
         assertEquals("Two null characters should be preserved",
                      NUL_DOUBLE, doc.select("p").text());
     }

     @Test
     public void testNullCharacterAtStartOfText() {
         Document doc = Jsoup.parse("<p>" + NUL_WITH_FOO + "</p>");
         assertEquals("Null at start should be kept",
                      NUL_WITH_FOO, doc.select("p").text());
     }

     @Test
     public void testNullCharacterAtEndOfText() {
         Document doc = Jsoup.parse("<p>" + FOO_WITH_NUL + "</p>");
         assertEquals("Null at end should be kept",
                      FOO_WITH_NUL, doc.select("p").text());
     }

     @Test
     public void testOnlyNullCharacter() {
         Document doc = Jsoup.parse("<div>" + NUL + "</div>");
         assertEquals("Sole null character should appear as text",
                      NUL, doc.select("div").text());
     }

     // --- cases that trigger table-cell state (InCell) ---

     @Test
     public void testNullCharacterInTableCell() {
         String html = "<table><tr><td>" + NUL + "</td></tr></table>";
         Document doc = Jsoup.parse(html);
         Element td = doc.select("td").first();
         assertNotNull(td);
         assertEquals("Null inside <td> should be kept",
                      NUL, td.text());
     }

     @Test
     public void testMultipleNullsInTableCell() {
         String html = "<table><tr><td>" + NUL_DOUBLE + "</td></tr></table>";
         Document doc = Jsoup.parse(html);
         assertEquals(NUL_DOUBLE, doc.select("td").text());
     }

     // --- boundary: null together with normal characters in cell ---

     @Test
     public void testNullMixedWithTextInTableCell() {
         String html = "<table><tr><td>a" + NUL + "b</td></tr></table>";
         Document doc = Jsoup.parse(html);
         assertEquals("a" + NUL + "b", doc.select("td").text());
     }

     // --- verify that normal text is unaffected (sanity) ---

     @Test
     public void testOrdinaryTextStillWorks() {
         Document doc = Jsoup.parse("<p>hello</p>");
         assertEquals("hello", doc.select("p").text());
     }

     @Test
     public void testOrdinaryTableCellStillWorks() {
         Document doc = Jsoup.parse("<table><tr><td>data</td></tr></table>");
         assertEquals("data", doc.select("td").text());
     }

     // --- null character via numeric character reference &#0; ---

     @Test
     public void testNullCharacterAsNumericCharacterReference() {
         Document doc = Jsoup.parse("<p>&#0;</p>");
         // The numeric reference should produce the null character;
         // we expect the text node not to be empty and to contain the NUL char.
         String text = doc.select("p").text();
         assertFalse("Null character via &#0; should not be swallowed",
                     text.isEmpty());
         assertEquals(1, text.length());
         assertEquals(NUL.charAt(0), text.charAt(0));
     }
 }