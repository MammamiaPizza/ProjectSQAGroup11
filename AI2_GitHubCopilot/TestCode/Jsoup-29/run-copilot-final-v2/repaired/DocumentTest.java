package org.jsoup.nodes;

import static org.junit.Assert.*;

import org.jsoup.Jsoup;
import org.junit.Test;

public class DocumentTest {

 @Test
 public void testTitleOnEmptyDocument() {
     Document doc = Document.createShell("");
     // No title element present
     assertEquals("", doc.title());
 }

 @Test
 public void testTitleNormalizesWhitespaceFromTextNodes() {
     // Simulate a title element whose text is split across text nodes with a newline,
     // which is the trigger condition for the reported bug.
     Document doc = Document.createShell("");
     Element titleEl = doc.head().appendElement("title");
     titleEl.appendChild(new TextNode("Hello", ""));
     titleEl.appendChild(new TextNode("\n there now", ""));
     // Expect whitespace to be collapsed into a single space and the result trimmed.
     assertEquals("Hello there now", doc.title());
 }

 @Test
 public void testTitleTrimsLeadingTrailingWhitespace() {
     Document doc = Document.createShell("");
     doc.title("   spaced title \t\n  ");
     assertEquals("spaced title", doc.title());
 }

 @Test
 public void testTitleWithOnlyWhitespaceReturnsEmpty() {
     Document doc = Document.createShell("");
     doc.title("   \t \n  ");
     assertEquals("", doc.title());
 }

 @Test
 public void testTitleSetterCreatesTitleInHead() {
     Document doc = Document.createShell("");
     // No title element initially
     doc.title("New title");
     assertEquals("New title", doc.title());
     // Verify that a <title> element was added inside <head>
     assertEquals("title", doc.head().children().first().tagName());
 }

 @Test
 public void testTitleSetterUpdatesExistingTitle() {
     Document doc = Document.createShell("");
     doc.title("First");
     assertEquals("First", doc.title());
     doc.title("Second");
     assertEquals("Second", doc.title());
 }

 @Test
 public void testTitleNormalizesMultipleSpaces() {
     Document doc = Document.createShell("");
     doc.head().appendElement("title").text("Hello    world");
     assertEquals("Hello world", doc.title());
 }

 @Test
 public void testHeadAndBodyAfterCreateShell() {
     Document doc = Document.createShell("");
     assertNotNull(doc.head());
     assertNotNull(doc.body());
     assertEquals("head", doc.head().tagName());
     assertEquals("body", doc.body().tagName());
 }

 @Test
 public void testNormaliseCreatesMissingHeadAndBody() {
     Document doc = new Document("");
     // Initially no <html>, <head>, or <body>
     doc.normalise();
     assertNotNull(doc.head());
     assertNotNull(doc.body());
 }

 @Test
 public void testNormaliseMovesStrayTextNodesToBody() {
     Document doc = Document.createShell("");
     // Put a non-blank text node directly under <head> (stray text)
     doc.head().appendChild(new TextNode("Stray text", ""));
     doc.normalise();
     // After normalise the stray text should have been moved to <body>
     assertTrue(doc.body().text().contains("Stray text"));
     assertFalse(doc.head().text().contains("Stray text"));
 }

 @Test
 public void testNormaliseMovesTextFromHtmlElementToBody() {
     Document doc = Document.createShell("");
     // Text directly under <html> should be moved to body as well
     Element htmlEl = doc.child(0); // the <html> element
     htmlEl.appendChild(new TextNode("Outside head", ""));
     doc.normalise();
     assertTrue(doc.body().text().contains("Outside head"));
 }

 @Test
 public void testTitleDoesNotAlterBody() {
     Document doc = Document.createShell("");
     doc.body().appendElement("p").text("Body paragraph");
     String bodyBefore = doc.body().html();
     doc.title("A title");
     assertEquals(bodyBefore, doc.body().html());
 }

}
