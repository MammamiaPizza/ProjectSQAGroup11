package org.jsoup.parser;

 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;

 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.jsoup.parser.Parser;
 import org.junit.Test;

 /**
  * Tests for {@link XmlTreeBuilder} focusing on case-normalisation of discordant end tags
  * (Bug-998: popStackToClose should match tags in a case-insensitive manner, ensuring
  * the stack is properly unwound even when start and end tags disagree in case.)
  */
 public class XmlTreeBuilderTest {
   /** Parse XML and return the first element child of the document (the root). */
   private static Element rootElementOf(String xml) {
     Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
     assertTrue("Document must have at least one child", doc.childNodeSize() > 0);
     return (Element) doc.child(0);
   }

   @Test
   public void startTagLowerEndTagUpperClosesProperly() {
     Element root = rootElementOf("<div>content</DIV>");
     assertEquals("div", root.tag().getName());
     assertEquals("content", root.text());
     assertTrue("Outer HTML should contain </div>", root.outerHtml().contains("</div>"));
   }

   @Test
   public void startTagUpperEndTagLowerClosesProperly() {
     Element root = rootElementOf("<DIV>content</div>");
     assertEquals("div", root.tag().getName()); // normalised for comparison
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
     assertEquals("div", root.tag().getName());
     Element span = (Element) root.child(0);
     assertNotNull(span);
     assertEquals("span", span.tag().getName());
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
     assertEquals("div", div.tag().getName());
     assertEquals("span", span.tag().getName());
     assertTrue(div.outerHtml().contains("</div>"));
     assertTrue(span.outerHtml().contains("</span>"));
   }

   @Test
   public void selfClosingTagNotOnStackUnaffected() {
     String xml = "<br/><div>content</DIV>";
     Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
     // The document should contain <br/> and the div root
     assertTrue(doc.childNodeSize() >= 1);
     Element root = (Element) doc.childNode(doc.childNodeSize() - 1); // last child is the root
element
     assertEquals("div", root.tag().getName());
     assertTrue(root.outerHtml().contains("</div>"));
     // Verify <br/> appears in the output
     assertTrue(doc.outerHtml().contains("<br"));
   }

   @Test
   public void nonMatchingEndTagIsIgnored() {
     // Different tag name should not close the open element
     String xml = "<p>text</DIV>";
     Element root = rootElementOf(xml);
     assertEquals("p", root.tag().getName());
     // The ignored end tag </DIV> should not appear in the output
     assertFalse("Outer HTML should NOT contain the ignored end tag",
root.outerHtml().contains("</DIV>"));
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
     assertEquals("div", outerDiv.tag().getName());
     assertEquals("outer", outerDiv.ownText());
     Element innerDiv = (Element) outerDiv.child(0);
     assertEquals("div", innerDiv.tag().getName());
     assertEquals("inner", innerDiv.text());
     assertTrue(innerDiv.outerHtml().contains("</div>"));
     assertTrue(outerDiv.outerHtml().contains("</div>"));
   }

   @Test
   public void xmlDeclarationDoesNotInterfereWithStack() {
     String xml = "<?xml version=\"1.0\"?><div>content</DIV>";
     Element root = rootElementOf(xml);
     assertEquals("div", root.tag().getName());
     assertTrue(root.outerHtml().contains("</div>"));
   }
 }
