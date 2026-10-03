package org.jsoup.safety;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.junit.Test;
import static org.junit.Assert.*;

public class CleanerIsValidRegressionTest {

 private static final Whitelist BASIC = Whitelist.basic();
 private static final Whitelist NONE = Whitelist.none();
 private static final Whitelist RELAXED = Whitelist.relaxed();

 @Test
 public void testIsValidBodyHtmlWithAllowedTags() {
     String body = "<a href=\"http://example.com\">link</a>";
     assertTrue("Valid body with allowed tags should be valid", Jsoup.isValid(body, BASIC));
 }

 @Test
 public void testIsValidBodyHtmlWithDisallowedTag() {
     String body = "<b>bold</b>";
     assertFalse(Jsoup.isValid(body, NONE));
 }

 @Test
 public void testIsValidBodyHtmlEmpty() {
     assertTrue(Jsoup.isValid("", NONE));
 }

 @Test
 public void testIsValidBodyHtmlWhitespaceOnly() {
     assertTrue(Jsoup.isValid("   \n ", NONE));
 }

 @Test
 public void testIsValidBodyHtmlTextOnly() {
     assertTrue(Jsoup.isValid("just some text", NONE));
 }

 @Test
 public void testIsValidBodyHtmlWithDisallowedAttribute() {
     String body = "<a href=\"http://example.com\" id=\"myid\">link</a>";
     assertFalse(Jsoup.isValid(body, BASIC));
 }

 @Test
 public void testIsValidDocumentAllAllowed() {
     Document doc = Document.createShell("");
     doc.body().appendChild(new TextNode("Hello", ""));
     Cleaner cleaner = new Cleaner(NONE);
     assertTrue(cleaner.isValid(doc));
 }

 @Test
 public void testIsValidDocumentWithDisallowedTag() {
     Document doc = Document.createShell("");
     doc.body().appendElement("b").text("bold");
     Cleaner cleaner = new Cleaner(NONE);
     assertFalse(cleaner.isValid(doc));
 }

 @Test
 public void testIsValidDocumentWithHeadContent() {
     Document doc = Document.createShell("");
     doc.head().appendElement("title").text("Test");
     Cleaner cleaner = new Cleaner(RELAXED);
     assertFalse(cleaner.isValid(doc));
 }

 @Test(expected = IllegalArgumentException.class)
 public void testIsValidNullBodyStringThrows() {
     Jsoup.isValid(null, BASIC);
 }

 @Test(expected = IllegalArgumentException.class)
 public void testIsValidNullDocumentThrows() {
     new Cleaner(BASIC).isValid(null);
 }

 @Test
 public void testCleanDoesNotAlterValidInput() {
     String input = "<a href=\"http://example.com\">test</a>";
     String cleaned = Jsoup.clean(input, BASIC);
     assertEquals("Cleaned output should match input for valid body", input, cleaned);
     assertTrue("Valid cleaned body should be isValid", Jsoup.isValid(cleaned, BASIC));
 }

}