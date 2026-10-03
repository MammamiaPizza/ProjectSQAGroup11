package org.jsoup.parser;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;

 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.junit.Test;

 /**
  * Tests for {@link XmlTreeBuilder} focusing on case-normalisation of discordant end tags
  * (Bug-998: popStackToClose should match tags in a case-insensitive manner, ensuring
  * the stack is properly unwound even when start and end tags disagree in case.)
  */
 public class XmlTreeBuilderTest {

     // Helper: parse XML and return the outer markup of the document's root element.
     private static String outerHtmlOfRoot(String xml) {
         Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
         assertNotNull("Root element must exist", doc.childNodeSize() > 0 ? doc.child(0) : null);
         return doc.child(0).outerHtml();
     }

     @Test
     public void normalizesDiscordantTags_starLowerEndUpper() {
         // Bug scenario: start tag lowercase, end tag uppercase must still close.
         String result = outerHtmlOfRoot("<div>content</DIV>");
         assertTrue("Expected closing </div>", result.contains("</div>");
         assertEquals("<div>\n content\n</div>", result);
     }

     @Test
     public void normalizesDiscordantTags_starUpperEndLower() {
         String result = outerHtmlOfRoot("<DIV>content</div>");
         // Element retains the start tag's casing.
         assertTrue("Expected closing </DIV>", result.contains("</DIV>");
         assertEquals("<DIV>\n content\n</DIV>", result);
     }

     @Test
     public void sameCaseClosesCorrectly() {
         String result = outerHtmlOfRoot("<div>content</div>");
         assertEquals("<div>\n content\n</div>", result);
     }

     @Test
     public void nestedMismatchedTagsClosesOrder() {
         String xml = "<div>\n <span>text</SPAN>\n</DIV>";
         Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
         // Root element should contain the nested span
         Element root = doc.child(0);
         assertNotNull(root);
         assertEquals("div", root.normalName());
         Element span = root.child(0);
         assertNotNull(span);
         assertEquals("span", span.normalName());
         assertEquals("text", span.text());
         // The output must include both closing tags
         String outer = root.outerHtml();
         assertTrue(outer.contains("</span>"));
         assertTrue(outer.contains("</div>"));
     }

     @Test
     public void startTagWithAttributesAndMismatchedEnd() {
         String result = outerHtmlOfRoot("<div class='example'>content</DIV>");
         assertTrue(result.startsWith("<div class=\"example\">")); // normalised attribute quoting
         assertTrue(result.contains("</div>"));
     }

     @Test
     public void multipleDiscordantSiblinsBothCloses() {
         String xml = "<div>first</DIV><span>second</SPAN>";
         Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
         // Two root children because XML document root is a single element? Actually, an XML
document
         // must have a single root element; Jsoup will auto-wrap. For testing, we use a wrapper.
         xml = "<root>" + xml + "</root>";
         doc = Jsoup.parse(xml, "", Parser.xmlParser());
         Element root = doc.child(0);
         assertEquals(2, root.children().size());
         Element div = root.child(0);
         Element span = root.child(1);
         assertEquals("div", div.normalName());
         assertEquals("span", span.normalName());
         assertTrue(div.outerHtml().contains("</div>"));
         assertTrue(span.outerHtml().contains("</span>"));
     }

     @Test
     public void selfClosingTagUnaffectedByCaseMismatch() {
         // Self-closing tags like <br/> are not placed on the stack.
         String xml = "<br/><div>content</DIV>";
         Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
         Element root = doc.child(0);
         assertEquals("div", root.normalName());
         assertTrue(root.outerHtml().contains("</div>"));
         // <br/> should appear as a sibling before the root? Since <br/> is self-closing,
         // it may be a previous sibling of the root element; in Jsoup XML parsing the root
         // is the first element in the document. Usually the XML tree builder places the
         // document on stack, so the root element is the first child of the document.
         // Let's just ensure the document has at least two children: br and div wrapper.
         assertTrue("Document should contain <br/>", doc.body().childNodeSize() > 1 ||
doc.outerHtml().contains("<br"));
     }

     @Test
     public void nonMatchingEndTagIsIgnored() {
         // An end tag whose name (even normalised) does not match any open element is skipped.
         String xml = "<p>text</DIV>";
         Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
         // The root element should be <p>, still open
         Element root = doc.child(0);
         assertEquals("p", root.normalName());
         String outer = root.outerHtml();
         // The closing </p> should NOT be present because </DIV> was ignored and p remains open.
         assertTrue("Outer HTML should not contain closing p since end tag was ignored",
                 outer.contains("<p>") && !outer.contains("</p>"));
     }

     @Test
     public void emptyInputProducesEmptyDocument() {
         Document doc = Jsoup.parse("", "", Parser.xmlParser());
         assertNotNull(doc);
         assertEquals("", doc.body().html());
     }

     @Test
     public void caseInsensitiveMatchingPreservesTextContent() {
         String xml = "<div>text <b>bold</B></DIV>";
         Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
         Element root = doc.child(0);
         assertEquals("text bold", root.text());
         assertTrue(root.html().contains("</b>"));
         assertTrue(root.html().contains("</div>"));
     }

     @Test
     public void tagWithSameNameButDifferentCaseOnStackIsMatched() {
         // Regression: ensures that when the stack contains multiple elements, the
         // correct one is located using normalised name.
         String xml = "<div><div>inner</DIV>outer</DIV>";
         Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
         Element outerDiv = doc.child(0);
         Element innerDiv = outerDiv.child(0);
         assertEquals("div", innerDiv.normalName());
         assertEquals("inner", innerDiv.text());
         assertEquals("outer", outerDiv.ownText());
         // The inner div should be closed by the first </DIV>, leaving outer on stack.
         assertTrue(innerDiv.outerHtml().contains("</div>"));
         assertTrue(outerDiv.outerHtml().contains("</div>"));
     }

     @Test
     public void xmlDeclarationIsNotTreatedAsTag() {
         // XML declaration should remain as comment/declaration, not affect stack.
         String xml = "<?xml version=\"1.0\"?><div>content</DIV>";
         Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
         // Should still have a root element div
         assertTrue(doc.childNodeSize() >= 1);
         Element root = doc.child(0);
         assertEquals("div", root.normalName());
         assertTrue(root.outerHtml().contains("</div>"));
     }
 }

Wait, I need to check imports and syntax: In helper method, doc.childNodeSize() is wrong — it should
be doc.childNodeSize(). But actually, Node has childNodeSize(). Yes. Also, Element class is in
org.jsoup.nodes.Element. Must import. Also ParseErrorList? Not needed. Parser.xmlParser() — need to
import Parser. Actually Parser is in org.jsoup.parser.Parser. So imports:

 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.jsoup.parser.Parser;

Also @Test and assertEquals etc.

One issue: In outerHtmlOfRoot, we do doc.child(0) but Document's child() might return Node, not
Element. We can cast or use doc.child(0) as Element. But child(int index) returns Element. In jsoup,
Document extends Element, so doc.child(0) returns Node? Actually, Element.child(int index) returns
Node. But we want the root element. The XML document will have a single root element as first child.
We can use doc.child(0) and cast to Element. Or use doc.children().first(). Better to use
doc.child(0) which returns Node, but then we need to cast. Or we can just get the outerHtml of the
whole document and assert includes. Simpler: just use doc.outerHtml() and check content. For many
tests, that's fine.

Let's simplify: Instead of helper, just use doc.outerHtml() and assert on the string. For the
expected string, we need to format exactly what Jsoup produces. Jsoup's XML outerHtml pretty-prints
with newlines and indentation? By default, pretty-print is enabled and may add newlines. The
expected output may differ across versions. The bug report didn't specify exact formatting. We can
just assert that the string contains the expected closing tag and not rely on exact equality of the
whole document. For example, assertTrue(result.contains("</div>")) and also maybe
!result.contains("<div>" + ... )? Better to test that the document structure is correct: elements
are correctly nested. So we can check the DOM tree.

Let's redesign tests to assert on the document's structure (elements) rather than exact string,
because formatting may change. But the bug is about outerHtml() output. The trigger test likely used
Document.outerHtml(). So we can do approximate string assertions. We'll use assertTrue with
contains.

I'll revise the test code to be more robust, avoiding exact string equals.

Also, we need to avoid using normalName() method - that's the normalized (lowercase) name; we can
use tagName() for the preserved case.

Let's write final version with correct imports and method signatures.

I'll produce the final answer with the complete class```java
package org.jsoup.parser;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.junit.test;

/**

 - Tests for {@link XmlTreeBuilder} focusing on case-normalisation of discordant end tags
 - (Bug-998: popStackToClose should match tags in a case-insensitive manner, ensuring
 - the stack is properly unwound even when start and end tags disagree in case.)
  */
 public class XmlTreeBuilderTest {
  /** Parse XML and return the first element child of the document (the root).
  */
  private static Element rootElementOf(String xml) {
  Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
  assertTrue("Document must have at least one child", doc.childNodeSize() >0);
  return (Element) doc.child(0);
  }
  @Test
  public void startTagLowerEndTagUpperClosesProperly() {
  Element root = rootElementOf("<div>content</DIV>");
  assertEquals("div", root.normalName());
  assertEquals("content", root.text());
  assertTrue("Outer HTML should contain </div>", root.outerHtml().contains("</div>"));
  }
  @Test
  public void startTagUpperEndTagLowerClosesProperly() {
  Element root = rootElementOf("<DIV>content</div>");
  assertEquals("div", root.noralName()); // normalised for comparison
  assertEquals("DIV", root.tagName());  // but preserved case is the start tag
  assertTrue(root.outerHtml().contains("</DIV>"));
  }
  @Test
  public void sameCaseClosingWorks() {
  Element root = rootElementOf("<div>content</div>");
  assertEquals("content", root.text());
  assertTrue(root.outerHtml().contains("</div>"));
  }
  @Test
  public void nestedDiscordantTagsClosesInOrder() {
  String xml = "<div>\n <span>text</SPAN>\n</DIV>";
  Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
  Element root = (Element) doc.child(0);
  assertEquals("div", root.noralName());
  Element span = (Element) root.child(0);
  assertNotNull(span);
  assertEquals("span", span.normalName()));
  assertEquals("text", span.text());
  // Both closing tags must appear
  String outer = root.outerHtml();
  assertTrue(outer.contains("</span>"));
  assertTrue(outer.contains("</div>"));
  }
  @Test
  public void attributesRetainedOnCaseMismatch() {
  Element root = rootElementOf("<div class='bold'>content</DIV>");
  assertTrue(root.hasAttr("class"));
  assertEquals("bold", root.attr("class"));
  assertTrue(root.outerHtml().contains("</div>"));
  }
  @Test
  public void multipleDiscordantSiblinsBothClose() {
  // XML requires a single root element, so wrap in <root>
  String xml = "<root><div>first</DIV><span>second</SPAN></root>";
  Element root = rootElementOf(xml);
  assertEquals(2, root.children().size());
  Element div = root.child(0);
  Element span = root.child(1);
  assertEquals("div", div.noralName());
  assertEquals("span", span.normalName());
  assertTrue(div.outerHtml().contains("</div>"));
  assertTrue(span.outerHtml().contains("</spn>")); // note: span normalised
  }
  @Test
  public void selfClosingTagNotOnStackUnaffected() {
  String xml = "<br/><div>content</DIV>";
  Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
  // The document should contain <br/> and the div root
  assertTrue(doc.childNodeSize() >=1);
  Element root = (Element) doc.childNode(doc.childNodeSize()-1); // last child is the root element
  assertEquals("div", root.normalName());
  assertTrue(root.outerHtml().contains("</div>"));
  // Verify <br/> appears in the output
  assertTrue(doc.outerHtml().contains("<br"));
  }
  @Test
  public void nonMatchingEndTagIsIgnored() {
  // Different tag name should not close the open element
  String xml = "<p>text</DIV>";
  Element root = rootElementOf(xml);
  assertEquals("p", root.noralName());
  // The closing </p> must not appear because </DIV> was ignored
  assertFalse("Outter HTML should NOT contain closing p", root.outerHtml().contains("</p>"))
  }
  @Test
  public void emptyInputProducesEmptyDocument() {
  Document doc = Jsoup.parse("", "", Parser.xmlParser());
  assertNotNull(doc);
  assertEquals(0, doc.childNodeSize());
  }
  @Test
  public void innerElementCorrectlyMatchedWithOuterCaseMismatch() {
  // Regression: two elements with same name but different casings on the stack
  // <div><div>inner</DIV>outer</DIV>  -> inner div closed first, then outer
  String xml = "<div><div>inner</DIV>outer</DIV>";
  Element outerDiv = rootElementOf(xml);
  assertEquals("div", outerDiv.normalName());
  assertEquals("outer", outerDiv.ownText());
  Element innerDiv = (Element) outerDiv.child(0);
  assertEquals("div", innerDiv.normalName());
  assertEquals("inner", innerDiv.text());
  assertTrue(innerDiv.outerHtml().contains("</div>"));
  assertTrue(outerDiv.outerHtml().contains("</div>"));
  }
  @Test
  public void xmlDeclarationDoesNotInterfereWithStack() {
  String xml = "<?xml version=\"1.0\"?><div>content</DIV>";
  Element root = rootElementOf(xml);
  assertEquals("div", root.normalName());
  assertTrue(root.outerHtml().contains("</div>"));
  }

}
```