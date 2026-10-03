package org.jsoup.parser;

 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.junit.Test;

 import static org.junit.Assert.*;

 /**
  * Tests for the bug that <image> tag is not converted to <img> void element.
  * Bug: #364, Jsoup-38b.
  */
 public class ImageToImgTest {

     private Document parse(String html) {
         return Jsoup.parse(html);
     }

     // Basic conversion: <image> -> <img />
     @Test
     public void convertsImageToImg() {
         Document doc = parse("<image>");
         Element img = doc.select("img").first();
         assertNotNull("Should have converted <image> to <img>", img);
         assertEquals(0, img.childNodeSize());
         assertEquals(0, doc.select("image").size());
         // verify output does not contain <image> as element name in HTML
         String bodyHtml = doc.body().html();
         assertFalse(bodyHtml, bodyHtml.contains("<image>"));
         assertTrue(bodyHtml, bodyHtml.contains("<img "));
     }

     @Test
     public void imageWithAttributes() {
         Document doc = parse("<image alt='x'>");
         Element img = doc.select("img").first();
         assertNotNull(img);
         assertEquals("x", img.attr("alt"));
         assertEquals(0, doc.select("image").size());
     }

     @Test
     public void imageSelfClosing() {
         Document doc = parse("<image/>");
         assertEquals(1, doc.select("img").size());
         assertEquals(0, doc.select("image").size());
     }

     @Test
     public void imageSelfClosingWithAttributes() {
         Document doc = parse("<image alt='pic' />");
         Element img = doc.select("img").first();
         assertNotNull(img);
         assertEquals("pic", img.attr("alt"));
         assertEquals(0, img.childNodeSize());
     }

     @Test
     public void imageWithSrc() {
         Document doc = parse("<image src='a.png'>");
         Element img = doc.select("img").first();
         assertNotNull(img);
         assertEquals("a.png", img.attr("src"));
         assertEquals(0, doc.select("image").size());
     }

     @Test
     public void imageInTable() {
         Document doc = parse("<table><image></table>");
         // The image should be converted to img inside the table context
         Element img = doc.select("table img").first();
         assertNotNull("Should find img inside table", img);
         assertEquals(0, doc.select("image").size());
     }

     @Test
     public void multipleImages() {
         Document doc = parse("<image><image>");
         assertEquals(2, doc.select("img").size());
         assertEquals(0, doc.select("image").size());
     }

     @Test
     public void imageFollowedByParagraph() {
         // <image> is void, so <p> is a sibling
         Document doc = parse("<image><p>text</p>");
         Element img = doc.select("img").first();
         Element p = doc.select("p").first();
         assertNotNull(img);
         assertNotNull(p);
         assertTrue("p should be a following sibling of img", img.siblingElements().contains(p));
         // image element should have no children
         assertTrue(img.children().isEmpty());
     }

     @Test
     public void imageInsideDiv() {
         Document doc = parse("<div><image alt='inside'></div>");
         Element img = doc.select("div img").first();
         assertNotNull(img);
         assertEquals("inside", img.attr("alt"));
         assertEquals(0, doc.select("image").size());
     }

     @Test
     public void imageInHeadIsIgnoredAndMovedToBody() {
         // HTML parser may move <image> to body when found in head
         Document doc = parse("<head><image></head>");
         // Should be converted to img in body (or as element in head but it's void, so likely
moved)
         Element img = doc.select("img").first();
         assertNotNull("img should appear somewhere", img);
         // it may be in body
         assertTrue(doc.select("body img").size() > 0 || doc.select("head img").size() > 0);
         assertEquals(0, doc.select("image").size());
     }

     @Test
     public void imageWithInvalidNesting() {
         // <image><b>bold</image> - void element, so end tag is ignored, <b> is sibling
         Document doc = parse("<image><b>bold</image>");
         Element img = doc.select("img").first();
         Element b = doc.select("b").first();
         assertNotNull(img);
         assertNotNull(b);
         assertTrue("b should be sibling, not child", img.siblingElements().contains(b));
         assertFalse("img should not have children", b.parent().equals(img));
     }

     @Test
     public void imageWithOtherVoidElements() {
         Document doc = parse("<image><br><hr>");
         assertEquals(1, doc.select("img").size());
         assertEquals(1, doc.select("br").size());
         assertEquals(1, doc.select("hr").size());
         assertEquals(0, doc.select("image").size());
     }
 }