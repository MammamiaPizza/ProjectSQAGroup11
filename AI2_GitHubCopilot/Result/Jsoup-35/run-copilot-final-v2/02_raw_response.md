package org.jsoup.parser;

 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.junit.Test;
 import static org.junit.Assert.assertEquals;

 public class HtmlTreeBuilderStateTest {

     @Test
     public void unclosedAnchorAtEof() {
         // unclosed <a> at EOF must be auto‑closed
         String html = "<a>text";
         Document doc = Jsoup.parse(html);
         assertEquals("<html><head></head><body><a>text</a></body></html>", doc.outerHtml());
     }

     @Test
     public void nestedAnchorWithoutClose() {
         // <a><a> – first <a> must be closed before opening second
         String html = "<a>outer<a>inner</a>";
         Document doc = Jsoup.parse(html);
         assertEquals("<html><head></head><body><a>outer</a><a>inner</a></body></html>",
doc.outerHtml());
     }

     @Test
     public void multipleConsecutiveUnclosedAnchors() {
         // each unmatched <a> start tag must produce an implicit </a>
         String html = "<a>first<a>second<a>third";
         Document doc = Jsoup.parse(html);

assertEquals("<html><head></head><body><a>first</a><a>second</a><a>third</a></body></html>",
                 doc.outerHtml());
     }

     @Test
     public void anchorWithText() {
         // perfectly balanced anchor
         String html = "<a>Click me</a>";
         Document doc = Jsoup.parse(html);
         assertEquals("<html><head></head><body><a>Click me</a></body></html>", doc.outerHtml());
     }

     @Test
     public void anchorWithBold() {
         // anchor wraps inline formatting element      String html = "<a><b>bold text</b></a>";
         Document doc = Jsoup.parse(html);
         assertEquals("<html><head></head><body><a><b>bold text</b></a></body></html>",
                 doc.outerHtml());
     }

     @Test
     public void emptySelfClosingAnchor() {
         // <a/> – treat as start tag and auto‑close at end of document
         String html = "<a/>";
         Document doc = Jsoup.parse(html);
         assertEquals("<html><head></head><body><a></a></body></html>", doc.outerHtml());
     }

     @Test
     public void anchorAroundBlockElement() {
         // <a> cannot contain block elements; parser must close <a> before <div>,
         // and the stray </a> is ignored
         String html = "<a><div>block</div></a>";
         Document doc = Jsoup.parse(html);
         assertEquals("<html><head></head><body><a></a><div>block</div></body></html>",
                 doc.outerHtml());
     }

     @Test
     public void anchorAcrossParagraph() {
         // conservative test: <a><p>text</p> – adoption agency should close <a> before <p>
         String html = "<a><p>text</p>";
         Document doc = Jsoup.parse(html);
         assertEquals("<html><head></head><body><a></a><p>text</p></body></html>",
                 doc.outerHtml());
     }

     @Test
     public void deepNestedAnchorWithOtherTags() {
         // exercise reconstruction with <b> inside unclosed <a>
         String html = "<a>one<a><b>two</b><a>three";
         Document doc = Jsoup.parse(html);

assertEquals("<html><head></head><body><a>one</a><a><b>two</b></a><a>three</a></body></html>",
                 doc.outerHtml());
     ]

     @Test
     public void noAnchorButOtherUnclosedTags() {
         // ensure <b> without close is handled differently (auto‑close at body end)
         String html = "<b>bold";
         Document doc = Jsoup.parse(html);
         assertEquals("<html><head></head><body><b>bold</b></body></html>", doc.outerHtml());
     }

     @Test
     public void anchorWithHref() {
         // attribute preservation when auto‑closing
         String html = "<a href='/one'>first<a href='/two'>second";
         Document doc = Jsoup.parse(html);
         assertEquals("<html><head></head><body><a href=\"/one\">first</a><a
href=\"/two\">second</a></body></html>",
                 doc.outerHtml());
     }

     @Test
     public void anchorImplicitCloseAtBodyEnd() {
         // anchor left open after other content, should close at </body>
         String html = "<body><a>text</body>";
         Document doc = Jsoup.parse(html);
         assertEquals("<html><head></head><body><a>text</a></body></html>", doc.outerHtml());
     }
 }