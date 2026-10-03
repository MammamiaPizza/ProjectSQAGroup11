package org.jsoup.parser;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;

 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.junit.Test;

 public class TokeniserStateTest {

     private Document parse(String html) {
         return Jsoup.parse(html);
     }

     @Test
     public void normalTextarea() {
         Document doc = parse("<textarea>text</textarea>");
         Element textarea = doc.select("textarea").first();
         assertNotNull(textarea);
         assertEquals("text", textarea.text());
     }

     @Test
     public void unterminatedTextarea() {
         Document doc = parse("<textarea>one<p>two");
         Element textarea = doc.select("textarea").first();
         assertNotNull(textarea);
         assertEquals("one", textarea.text());
     }

     @Test
     public void normalTitle() {
         Document doc = parse("<html><head><title>Hello</title></head><body></body></html>");
         Element title = doc.select("title").first();
         assertNotNull(title);
         assertEquals("Hello", title.text());
     }

     @Test
     public void unclosedTitle() {
         Document doc = parse("<title>One<b>Two <p>Test</p>");
         Element title = doc.select("title").first();
         assertNotNull(title);
         assertEquals("One", title.text());
     }

     @Test
     public void emptyTextarea() {
         Document doc = parse("<textarea></textarea>");
         Element textarea = doc.select("textarea").first();
         assertNotNull(textarea);
         assertEquals("", textarea.text());
     }

     @Test
     public void emptyTitle() {
         Document doc = parse("<title></title>");
         Element title = doc.select("title").first();
         assertNotNull(title);
         assertEquals("", title.text());
     }

     @Test
     public void textareaOnlyWhitespace() {
         Document doc = parse("<textarea>   </textarea>");
         Element textarea = doc.select("textarea").first();
         assertNotNull(textarea);
         assertEquals("   ", textarea.text());
     }

     @Test
     public void unclosedTitleAtEof() {
         Document doc = parse("<title>NoEnd");
         Element title = doc.select("title").first();
         assertNotNull(title);
         assertEquals("NoEnd", title.text());
     }

     @Test
     public void deeplyNestedInUnclosedTitle() {
         Document doc = parse("<title>a<b>c<i>d");
         Element title = doc.select("title").first();
         assertNotNull(title);
         assertEquals("a", title.text());
     }

     @Test
     public void unclosedTitleStopsAtHeadClose() {
         Document doc = parse("<html><head><title>One</head><body></body></html>");
         Element title = doc.select("title").first();
         assertNotNull(title);
         assertEquals("One", title.text());
     }

     @Test
     public void textareaWithNestedTags() {
         Document doc = parse("<textarea><b>bold</textarea>");
         Element textarea = doc.select("textarea").first();
         assertNotNull(textarea);
         assertEquals("<b>bold", textarea.text());
     }
 }
