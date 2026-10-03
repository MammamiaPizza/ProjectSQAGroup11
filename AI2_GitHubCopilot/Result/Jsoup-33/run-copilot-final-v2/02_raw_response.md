package org.jsoup.parser;

import static org.junit.Assert.*;
import org.junit.Test;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.List;

/**

 - Tests for HtmlTreeBuilder focusing on known empty (void) block handling.
 -
 - The bug (issue 305) causes void elements like img, hr, br, input to be
 - treated as text when parsed in certain contexts, leading to escaped HTML
 - in the output instead of real elements.
  */
 public class HtmlTreeBuilderTest {
  // ---- direct HtmlTreeBuilder tests ----
  @Test
  public void insertEmptyKnownVoidTagCreatesElement() {
  HtmlTreeBuilder tb = new HtmlTreeBuilder();
  tb.initialiseParse("", "", new ParseErrorList(0, 0));
  // Create a token for <img>
  Token.StartTag t = new Token.StartTag("img");
  t.attributes.put("src", "x");
  t.setSelfClosing();
  Element el = tb.insertEmpty(t);
  assertNotNull(el);
  assertEquals("img", el.tagName());
  assertEquals("x", el.attr("src"));
  // The element should be a child of the current stack element or doc
  // after setting up correctly. Here we just check the element itself.
  // No escaped text should be generated.
  }
  @Test
  public void insertEmptyUnknownVoidTagCreatesElement() {
  HtmlTreeBuilder tb = new HtmlTreeBuilder();
  tb.initialiseParse("", "", new ParseErrorList(0, 0));
  Token.StartTag t = new Token.StartTag("customvoid");
  t.setSelfClosing();
  Element el = tb.insertEmpty(t);
  assertNotNull(el);
  assertEquals("customvoid", el.tagName());
  }
  @Test
  public void insertEmptyDoesNotEscapeTagName() {
  HtmlTreeBuilder tb = new HtmlTreeBuilder();
  tb.initialiseParse("", "", new ParseErrorList(0, 0));
  Token.StartTag t = new Token.StartTag("img");
  t.setSelfClosing();
  tb.insertEmpty(t);
  // After inserting, the document (or stack element) should contain the
  // element, not a text node with "<img".
  Node child = tb.doc.childNode(0);
  assertTrue("Expected an Element, got " + child.getClass().getSimpleName(),
          child instanceof Element);
  assertEquals("img", child.nodeName());
  }
  @Test
  public void processKnownEmptyBlockDoesNotCreateTextNode() {
  HtmlTreeBuilder tb = new HtmlTreeBuilder();
  tb.initialiseParse("", "", new ParseErrorList(0, 0));
  // Simulate a state that would normally process tags in body
  Token.StartTag tag = new Token.StartTag("img");
  tag.setSelfClosing();
  // Processing should insert element, not text
  tb.process(tag);
  // The document should now have an img element, not a text node
  Node child = tb.doc.childNode(0);
  assertTrue(child instanceof Element);
  assertEquals("img", child.nodeName());
  }
  // ---- parser-based tests reproducing trigger scenario ----
  @Test
  public void handlesKnownEmptyBlocksFragmentDoesNotEscapeTags() {
  // This mimics the fragment from the trigger test's expected output.
  // The bug causes escaped tags like <div...> in the output.
  String input = "<div id=2><img /><img></div><a id=3 /><i /><foo /><foo>One</foo> <hr /> hr text
<hr> hr text two";
  Document doc = Jsoup.parseBodyFragment(input);
  // The outerHtml of the body should contain the real tags, not entities
  String bodyHtml = doc.body().html().trim();
  assertFalse("Output should not contain escaped div tag",
          bodyHtml.contains("<div"));
  assertFalse("Output should not contain escaped img tag",
          bodyHtml.contains("<img"));
  assertTrue("Output should contain img element",
          bodyHtml.contains("<img") || bodyHtml.contains("<img />") || bodyHtml.contains("<img>"));
  // Also verify key known empty tags appear as elements
  assertTrue(bodyHtml.contains("<hr") || bodyHtml.contains("<hr />") || bodyHtml.contains("<hr>"));
  assertTrue(bodyHtml.contains("<a id="3""));
  // Check that the id attribute is preserved with quotes (normalization)
  assertTrue(bodyHtml.contains("id="2""));
  }
  @Test
  public void handlesKnownEmptyBlocksInFullDocumentDoesNotEscapeTags() {
  String input = "<script src="/foo"></script><div id=2><img /><img></div><a id=3 /><i /><foo
/><foo>One</foo> <hr /> hr text <hr> hr text two";
  Document doc = Jsoup.parse(input);
  String bodyHtml = doc.body().html().trim();
  // The body must not contain escaped angle brackets for these known elements
  assertFalse("Output should not contain escaped div tag",
          bodyHtml.contains("<div"));
  assertFalse("Output should not contain escaped img tag",
          bodyHtml.contains("<img"));
  assertFalse("Output should not contain escaped a tag",
          bodyHtml.contains("<a"));
  // Known void elements should be present
  assertTrue(bodyHtml.contains("<img") || bodyHtml.contains("<img />"));
  assertTrue(bodyHtml.contains("<hr") || bodyHtml.contains("<hr />"));
  assertTrue(bodyHtml.contains("<br") || bodyHtml.contains("<br />"));
  }
  @Test
  public void parseSingleKnownEmptyBlockImgCreatesElement() {
  Document doc = Jsoup.parseBodyFragment("<img src='x'>");
  Elements imgs = doc.body().getElementsByTag("img");
  assertEquals(1, imgs.size());
  assertEquals("x", imgs.first().attr("src"));
  }
  @Test
  public void parseSingleKnownEmptyBlockHrCreatesElement() {
  Document doc = Jsoup.parseBodyFragment("<hr>");
  Elements hrs = doc.body().getElementsByTag("hr");
  assertEquals(1, hrs.size());
  }
  @Test
  public void parseSingleKnownEmptyBlockBrCreatesElement() {
  Document doc = Jsoup.parseBodyFragment("<br>");
  Elements brs = doc.body().getElementsByTag("br");
  assertEquals(1, brs.size());
  }
  @Test
  public void parseSingleKnownEmptyBlockInputCreatesElement() {
  Document doc = Jsoup.parseBodyFragment("<input type='text'>");
  Elements inputs = doc.body().getElementsByTag("input");
  assertEquals(1, inputs.size());
  assertEquals("text", inputs.first().attr("type"));
  }
  @Test
  public void knownEmptyBlockWithTextSiblingsKeepsStructure() {
  Document doc = Jsoup.parseBodyFragment("before <img /> after <br>");
  List<Node> children = doc.body().childNodes();
  assertEquals(3, children.size());
  assertTrue(children.get(0) instanceof TextNode);
  assertEquals("before ", ((TextNode)children.get(0)).getWholeText().trim());
  assertTrue(children.get(1) instanceof Element);
  assertEquals("img", ((Element)children.get(1)).tagName());
  assertTrue(children.get(2) instanceof TextNode);
  // The text after should not be corrupted
  assertTrue(((TextNode)children.get(2)).getWholeText().contains("after"));
  // Also verify that the br is handled (it might be in a following <p> etc; this test is
simplistic)
  }
  @Test
  public void nestedKnownEmptyBlocksInDiv() {
  Document doc = Jsoup.parseBodyFragment("<div><img src='a'><br><hr></div>");
  Element div = doc.body().child(0);
  assertEquals("div", div.tagName());
  Elements imgs = div.getElementsByTag("img");
  assertEquals(1, imgs.size());
  Elements brs = div.getElementsByTag("br");
  assertEquals(1, brs.size());
  Elements hrs = div.getElementsByTag("hr");
  assertEquals(1, hrs.size());
  }
  @Test
  public void fragmentParsingWithNullContextDoesNotEscapeTags() {
  // Use parseFragment directly via the package-private HtmlTreeBuilder
  HtmlTreeBuilder tb = new HtmlTreeBuilder();
  tb.initialiseParse("", "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;", new ParseErrorList(0, 0));]8;;
  List<Node> nodes = tb.parseFragment("<img src='test'> <br/> text", null,
          "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;", new ParseErrorList(0, 0));]8;;
  String combined = "";
  for (Node node : nodes) {
      combined += node.outerHtml();
  }
  assertFalse("Output should not contain escaped img tag",
          combined.contains("<img"));
  assertTrue("Output should contain img element",
          combined.contains("<img"));
  }

}