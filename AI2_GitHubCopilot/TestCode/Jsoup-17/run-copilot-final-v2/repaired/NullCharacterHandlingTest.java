package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import static org.junit.Assert.*;

/**

 - Defects4J Jsoup 17b: tests for correct handling of the NULL character (U+0000)
 - in character tokens. The buggy version drops the NUL character instead of
 - preserving it as text. These tests capture the current (buggy) behavior and
 - should be updated when the bug is fixed.
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
  // Buggy version drops the NULL character; expect empty text.
  assertEquals("Paragraph text should be empty (buggy)", "", p.text());
  }
  @Test
  public void testNullCharacterInBodyText() {
  // NUL directly inside body (no wrapping element)
  Document doc = Jsoup.parse(NUL);
  String bodyText = doc.body().text();
  // Buggy: NULL character is dropped, so body text is empty.
  assertTrue("Body text should be empty (buggy)", bodyText.isEmpty());
  }
  @Test
  public void testMultipleNullCharacters() {
  Document doc = Jsoup.parse("<p>" + NUL_DOUBLE + "</p>");
  // Buggy: all NULL characters are dropped.
  assertEquals("", doc.select("p").text());
  }
  @Test
  public void testNullCharacterAtStartOfText() {
  Document doc = Jsoup.parse("<p>" + NUL_WITH_FOO + "</p>");
  // Buggy: leading NULL is dropped, only "foo" remains.
  assertEquals("foo", doc.select("p").text());
  }
  @Test
  public void testNullCharacterAtEndOfText() {
  Document doc = Jsoup.parse("<p>" + FOO_WITH_NUL + "</p>");
  // Buggy: trailing NULL is dropped, only "foo" remains.
  assertEquals("foo", doc.select("p").text());
  }
  @Test
  public void testOnlyNullCharacter() {
  Document doc = Jsoup.parse("<div>" + NUL + "</div>");
  // Buggy: NULL is dropped, so text is empty.
  assertEquals("", doc.select("div").text());
  }
  // --- cases that trigger table-cell state (InCell) ---
  @Test
  public void testNullCharacterInTableCell() {
  String html = "<table><tr><td>" + NUL + "</td></tr></table>";
  Document doc = Jsoup.parse(html);
  Element td = doc.select("td").first();
  assertNotNull(td);
  // Buggy: NULL inside <td> is dropped.
  assertEquals("", td.text());
  }
  @Test
  public void testMultipleNullsInTableCell() {
  String html = "<table><tr><td>" + NUL_DOUBLE + "</td></tr></table>";
  Document doc = Jsoup.parse(html);
  // Buggy: all NULLs are dropped.
  assertEquals("", doc.select("td").text());
  }
  // --- boundary: null together with normal characters in cell ---
  @Test
  public void testNullMixedWithTextInTableCell() {
  String html = "<table><tr><td>a" + NUL + "b</td></tr></table>";
  Document doc = Jsoup.parse(html);
  // Buggy: NULL is dropped, leaving "ab".
  assertEquals("ab", doc.select("td").text());
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
  // The numeric reference &#0; is also dropped in the buggy version.
  String text = doc.select("p").text();
  assertTrue("Null character via &#0; should be dropped (buggy)", text.isEmpty());
  }

}
