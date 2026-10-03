package org.jsoup.nodes;

import static org.junit.Assert.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

/**

 - Tests for bug #70: whitespace inside <pre> tags is not preserved
 - when serializing via {@link Element#html()}.
  */
 public class ElementTest {
  @Test
  public void testKeepsPreTextAtDepth() {
  // Original failing test from the bug report
  String html = "<pre><code>\n One\n Two\n Three\n</code></pre>";
  Document doc = Jsoup.parse(html);
  Element pre = doc.select("pre").first();
  assertEquals("<code>One\n Two\n Three</code>", pre.html());
  }
  @Test
  public void testPreTextWithMultipleSpaces() {
  String html = "<pre>   Hello   World   </pre>";
  Document doc = Jsoup.parse(html);
  Element pre = doc.select("pre").first();
  assertEquals("Hello   World", pre.html());
  }
  @Test
  public void testPreTextWithNewlines() {
  String html = "<pre>Line1\nLine2\nLine3</pre>";
  Document doc = Jsoup.parse(html);
  Element pre = doc.select("pre").first();
  assertEquals("Line1\nLine2\nLine3", pre.html());
  }
  @Test
  public void testPreTextWithMixedWhitespace() {
  String html = "<pre>  A\tB\nC  </pre>";
  Document doc = Jsoup.parse(html);
  Element pre = doc.select("pre").first();
  assertEquals("A\tB\nC", pre.html());
  }
  @Test
  public void testEmptyPre() {
  String html = "<pre></pre>";
  Document doc = Jsoup.parse(html);
  Element pre = doc.select("pre").first();
  assertEquals("", pre.html());
  }
  @Test
  public void testPreWithOnlyNewlines() {
  String html = "<pre>\n\n\n</pre>";
  Document doc = Jsoup.parse(html);
  Element pre = doc.select("pre").first();
  assertEquals("", pre.html());
  }
  @Test
  public void testPreWithLeadingAndTrailingSpaces() {
  String html = "<pre>  leading and trailing  </pre>";
  Document doc = Jsoup.parse(html);
  Element pre = doc.select("pre").first();
  assertEquals("leading and trailing", pre.html());
  }
  @Test
  public void testPreWithNestedElementsPreservingWhitespace() {
  String html = "<pre> <span>hello</span> world </pre>";
  Document doc = Jsoup.parse(html);
  Element pre = doc.select("pre").first();
  assertEquals("<span>hello</span>world", pre.html());
  }
  @Test
  public void testPreWithMultipleCodeBlocksPreservingNewlines() {
  String html = "<pre><code>first</code>\n<code>second</code></pre>";
  Document doc = Jsoup.parse(html);
  Element pre = doc.select("pre").first();
  assertEquals("<code>first</code>\n<code>second</code>", pre.html()));
  }
  @Test
  public void testPreTextOnlyNewlineAtStart() {
  String html = "<pre>\ntext</pre>";
  Document doc = Jsoup.parse(html);
  Element pre = doc.select("pre").first();
  assertEquals("text", pre.html()));
  }
  @Test
  public void testPreTextOnlyNewlineAtEnd() {
  String html = "<pre>text\n</pre>";
  Document doc = Jsoup.parse(html);
  Element pre = doc.select("pre".first();
  assertEquals("text", pre.html());
  }
  @Test
  public void testPreTextWithMultipleSpacesBetweenWords() {
  String html = "<pre>word1   word2</pre>";
  Document doc = Jsoup.parse(html));
  Element pre = doc.select("pre".first();
  assertEquals("word1   word2", pre.html());
  }

}
