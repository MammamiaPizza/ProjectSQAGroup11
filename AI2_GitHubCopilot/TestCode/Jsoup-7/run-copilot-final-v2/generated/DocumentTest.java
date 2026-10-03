package org.jsoup.nodes;

 import static org.junit.Assert.*;

 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.jsoup.nodes.TextNode;
 import org.junit.Test;

 public class DocumentTest {

     // -----------------------------------------------------------------------//
     // Tests targeting the normalise bug (multiple head/body merging)
     // -----------------------------------------------------------------------//

     @Test
     public void testNormalisesStructure() {
         /* Exact case from the bug report */
         String html = "<html><head><script></script><noscript>"
                 + "<body><p>two</p></body><body><p>three</p></body>"
                 + "</noscript></head></html>";
         Document doc = Jsoup.parse(html);
         doc.normalise();
         assertEquals("<html><head><script></script><noscript></noscript></head>"
                 + "<body><p>two</p><p>three</p></body></html>", doc.outerHtml());
     }

     @Test
     public void testNormaliseMergesMultipleBodies() {
         Document doc = new Document("");
         Element html = doc.appendElement("html");
         html.appendElement("head");
         Element b1 = html.appendElement("body");
         b1.appendElement("p").text("one");
         Element b2 = html.appendElement("body");
         b2.appendElement("p").text("two");

         doc.normalise();
         assertEquals("only one body element after normalise", 1,
                 doc.select("body").size());
         assertEquals("body should contain both paragraphs", 2,
                 doc.body().children().size());
     }

     @Test
     public void testNormaliseMergesMultipleHeads() {
         Document doc = new Document("");
         Element html = doc.appendElement("html");
         Element h1 = html.appendElement("head");
         h1.appendElement("title").text("first");
         Element h2 = html.appendElement("head");
         h2.appendElement("meta").attr("name", "second");

         doc.normalise();
         assertEquals("only one head after normalise", 1,
                 doc.select("head").size());
         assertNotNull("title should exist",
                 doc.head().select("title").first());
         assertNotNull("meta should exist",
                 doc.head().select("meta[name=second]").first());
     }

     // -----------------------------------------------------------------------//
     // Tests covering text-node movement (already working, regression safe)
     // -----------------------------------------------------------------------//

     @Test
     public void testNormaliseMovesTextNodesIntoBody() {
         Document doc = Document.createShell("");
         doc.head().appendChild(new TextNode("misplaced", ""));
         doc.normalise();
         assertFalse("head should have no text", doc.head().hasText());
         assertTrue("body should contain the moved text",
                 doc.body().text().contains("misplaced"));
     }

     // -----------------------------------------------------------------------//
     // Tests covering creation of missing structural elements
     // -----------------------------------------------------------------------//

     @Test
     public void testNormaliseAddsMissingHtml() {
         Document doc = new Document("");
         Element head = doc.appendChild(doc.createElement("head"), "");
         doc.normalise();
         Element html = doc.child(0);
         assertEquals("html element should be added", "html", html.tagName());
         assertEquals("head should be moved under html", head, html.child(0));
     }

     @Test
     public void testNormaliseAddsMissingHead() {
         Document doc = new Document("");
         Element html = doc.appendElement("html");
         html.appendElement("body");
         doc.normalise();
         assertNotNull("head should be created", doc.head());
         assertEquals("head should be child of html", "head",
                 html.child(0).tagName());
     }

     @Test
     public void testNormaliseAddsMissingBody() {
         Document doc = new Document("");
         Element html = doc.appendElement("html");
         html.appendElement("head");
         doc.normalise();
         assertNotNull("body should be created", doc.body());
         assertEquals("body should be child of html", "body",
                 html.child(1).tagName());
     }

     // -----------------------------------------------------------------------//
     // Accessor and basic output tests
     // -----------------------------------------------------------------------//

     @Test
     public void testHeadAccessor() {
         Document doc = Document.createShell("");
         assertNotNull(doc.head());
         assertEquals("head", doc.head().tagName());
     }

     @Test
     public void testBodyAccessor() {
         Document doc = Document.createShell("");
         assertNotNull(doc.body());
         assertEquals("body", doc.body().tagName());
     }

     @Test
     public void testOuterHtmlNoWrapper() {
         Document doc = Document.createShell("");
         String html = doc.outerHtml();
         assertFalse("outerHtml must not contain #root", html.contains("#root"));
         assertTrue("outerHtml must start with <html>",
                 html.startsWith("<html>"));
     }

     // -----------------------------------------------------------------------//
     // Boundary / sanity checks
     // -----------------------------------------------------------------------//

     @Test
     public void testNormaliseDoesNotMoveElementsFromHead() {
         Document doc = Jsoup.parse("<html><head><title>keep</title></head><body></body></html>");
         doc.normalise();
         assertEquals("title should stay in head", "keep", doc.head().text());
     }

     @Test
     public void testNormalisePreservesBodyChildrenOrder() {
         Document doc = new Document("");
         Element html = doc.appendElement("html");
         html.appendElement("head");
         Element body = html.appendElement("body");
         body.appendElement("p").text("first");
         body.appendElement("span").text("second");
         doc.normalise();
         assertEquals("first child should be p", "p",
                 doc.body().child(0).tagName());
         assertEquals("second child should be span", "span",
                 doc.body().child(1).tagName());
     }
 }
