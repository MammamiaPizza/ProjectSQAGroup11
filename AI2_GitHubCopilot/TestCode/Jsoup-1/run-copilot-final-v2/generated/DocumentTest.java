package org.jsoup.nodes;

import org.junit.Test;
import static org.junit.Assert.*;

/**

 - Tests for {@link Document} targeting the text() order bug (issue 23).
 - Ensures that text concatenation preserves document order and that
 - normalisation does not reorder body children.
  */
 public class DocumentTest {
  // Bug-23 core: multiple elements in body should remain in source order
  @Test
  public void testTextMultipleDivsInDocumentOrder() {
  Document doc = Document.createShell("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
  doc.body().appendElement("div").text("foo");
  doc.body().appendElement("div").text("bar");
  doc.body().appendElement("div").text("baz");
  assertEquals("foo bar baz", doc.text());
  }
  // Setter/getter round-trip
  @Test
  public void testTextSetThenGet() {
  Document doc = Document.createShell("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
  doc.text("hello world");
  assertEquals("hello world", doc.text());
  }
  // Empty document yields empty text
  @Test
  public void testTextEmptyDocument() {
  Document doc = new Document("");
  assertEquals("", doc.text());
  }
  // Single element
  @Test
  public void testTextSingleElement() {
  Document doc = Document.createShell("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
  doc.body().text("hello");
  assertEquals("hello", doc.text());
  }
  // Body text excludes head content
  @Test
  public void testBodyTextExcludesHead() {
  Document doc = Document.createShell("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
  doc.title("head title");
  doc.body().appendElement("div").text("body text");
  assertEquals("body text", doc.body().text());
  }
  // Head text excludes body content
  @Test
  public void testHeadTextExcludesBody() {
  Document doc = Document.createShell("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
  doc.title("head title");
  doc.body().appendElement("div").text("body text");
  assertEquals("head title", doc.head().text().trim());
  }
  // normalise() must preserve body child ordering
  @Test
  public void testNormalisePreservesBodyOrder() {
  Document doc = Document.createShell("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
  doc.body().appendElement("div").text("a");
  doc.body().appendElement("div").text("b");
  doc.body().appendElement("div").text("c");
  doc.normalise();
  assertEquals("a b c", doc.body().text());
  }
  // After normalise, text() order still correct
  @Test
  public void testTextOrderAfterNormalise() {
  Document doc = Document.createShell("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
  doc.body().appendElement("span").text("x");
  doc.body().appendElement("span").text("y");
  doc.body().appendElement("span").text("z");
  doc.normalise();
  assertEquals("x y z", doc.text());
  }
  // Title setter/getter
  @Test
  public void testTitleSetAndGet() {
  Document doc = Document.createShell("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
  doc.title("Test Page");
  assertEquals("Test Page", doc.title());
  }
  // outerHtml() contains the html element
  @Test
  public void testOuterHtmlContainsHtmlTag() {
  Document doc = Document.createShell("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
  String html = doc.outerHtml();
  assertTrue(html.contains("<html"));
  }
  // createShell builds valid skeleton
  @Test
  public void testCreateShellStructure() {
  Document doc = Document.createShell("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
  assertNotNull(doc.head());
  assertNotNull(doc.body());
  assertFalse(doc.select("html").isEmpty());
  }
  // Null base URI is rejected (validates not-null contract)
  @Test(expected = IllegalArgumentException.class)
  public void testCreateShellNullBaseUriThrows() {
  Document.createShell(null);
  }

}
