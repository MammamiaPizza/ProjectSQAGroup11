package org.jsoup.parser;

 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.jsoup.select.Elements;
 import org.junit.Test;

 import static org.junit.Assert.*;

 /**
  * Tests for HtmlTreeBuilderState focusing on proper handling of known-empty
  * rawtext elements (style, noframes) when followed by structural tags.
  * Bug: Empty rawtext elements incorrectly cause subsequent tokens to be
  * emitted as escaped text instead of structural elements.
  */
 public class HtmlTreeBuilderStateTest {

     // --- Normal cases matching bug-report triggers ---

     @Test
     public void handlesKnownEmptyStyle() {
         Document doc = Jsoup.parse("<head><style></style><meta
name=\"foo\"></head><body>One</body></html>");
         String html = doc.html();
         assertTrue("meta should be a structural element after empty style",
             html.contains("<meta name=\"foo\">"));
         assertFalse("meta should not be escaped text after empty style",
             html.contains("&lt;meta"));
     }

     @Test
     public void handlesKnownEmptyNoFrames() {
         Document doc = Jsoup.parse("<head><noframes></noframes><meta
name=\"foo\"></head><body>One</body></html>");
         String html = doc.html();
         assertTrue("meta should be a structural element after empty noframes",
             html.contains("<meta name=\"foo\">"));
         assertFalse("meta should not be escaped text after empty noframes",
             html.contains("&lt;meta"));
     }

     // --- Boundary: self-closing, attributes, whitespace ---

     @Test
     public void handlesEmptyStyleWithSelfClosingMeta() {
         Document doc = Jsoup.parse("<head><style></style><meta name=\"foo\"
/></head><body>One</body></html>");
         String html = doc.html();
         assertTrue("self-closing meta should be structural after empty style",
             html.contains("meta name=\"foo\""));
         assertFalse("self-closing meta should not be escaped",
             html.contains("&lt;meta"));
     }

     @Test
     public void handlesEmptyNoFramesWithMetaAttributes() {
         Document doc = Jsoup.parse("<head><noframes></noframes><meta name=\"foo\"
content=\"bar\"></head><body>One</body></html>");
         String html = doc.html();
         assertTrue("meta with attributes should be structural after empty noframes",
             html.contains("meta name=\"foo\""));
         assertFalse("meta with attributes should not be escaped",
             html.contains("&lt;meta"));
     }

     @Test
     public void handlesEmptyStyleWithWhitespace() {
         Document doc = Jsoup.parse("<head>  <style></style>  <meta
name=\"foo\"></head><body>One</body></html>");
         String html = doc.html();
         assertTrue("meta after whitespace and empty style should be structural",
             html.contains("<meta name=\"foo\">"));
         assertFalse("meta after whitespace should not be escaped",
             html.contains("&lt;meta"));
     }

     @Test
     public void handlesSequenceOfEmptyRawtextElements() {
         Document doc = Jsoup.parse("<head><style></style><noframes></noframes><meta
name=\"foo\"></head><body>One</body></html>");
         String html = doc.html();
         assertTrue("style element should be present", html.contains("<style>"));
         assertTrue("noframes element should be present", html.contains("<noframes>"));
         assertTrue("meta should be structural after sequence of empty rawtext elements",
             html.contains("<meta name=\"foo\">"));
         assertFalse("meta should not be escaped after sequence",
             html.contains("&lt;meta"));
     }

     // --- Error cases: unclosed rawtext, rawtext content not tokenized ---

     @Test
     public void unclosedStyleTreatsMetaAsRawTextContent() {
         Document doc = Jsoup.parse("<head><style><meta
name=\"foo\"></style></head><body>One</body></html>");
         // When style is not immediately closed, meta inside it is raw text, not a page element
         Elements metas = doc.select("meta");
         assertEquals("meta inside unclosed style should not be a page-level element",
             0, metas.size());
         Element style = doc.select("style").first();
         assertNotNull("style element should exist", style);
         assertTrue("style should contain the raw text content",
             style.html().length() > 0);
     }

     @Test
     public void rawtextContentPreservedAndNotParsedAsMarkup() {
         Document doc = Jsoup.parse("<head><style>.cls { color: red; } </style><meta
name=\"foo\"></head><body>One</body></html>");
         Element style = doc.select("style").first();
         assertNotNull("style element should exist", style);
         assertEquals("style should not contain parsed child elements",
             0, style.children().select("*").size());
         // Meta after closed style must be a structural element
         String html = doc.html();
         assertTrue("meta after non-empty style should be structural",
             html.contains("<meta name=\"foo\">"));
     }

     // --- Boundary: state restoration after rawtext end tag ---

     @Test
     public void handlesEmptyStyleThenHeadEndTag() {
         Document doc = Jsoup.parse("<head><style></style></head><body>One</body></html>");
         String html = doc.html();
         int headStartCount = html.split("<head>").length - 1;
         int headEndCount = html.split("</head>").length - 1;
         assertEquals("should have exactly one <head> tag", 1, headStartCount);
         assertEquals("should have exactly one </head> tag", 1, headEndCount);
         assertTrue("body content should be intact", html.contains("One"));
     }

     @Test
     public void handlesEmptyNoFramesThenBodyTag() {
         Document doc = Jsoup.parse("<head><noframes></noframes><body>One</body></html>");
         String html = doc.html();
         assertTrue("body should be a structural element after empty noframes",
             html.contains("<body>"));
         assertFalse("body should not be escaped text after empty noframes",
             html.contains("&lt;body"));
     }

     // --- Regression: non-empty rawtext and interaction with other head elements ---

     @Test
     public void handlesNonEmptyStyleThenMeta() {
         Document doc = Jsoup.parse("<head><style>body { color: red; }</style><meta
name=\"foo\"></head><body>One</body></html>");
         String html = doc.html();
         Element style = doc.select("style").first();
         assertTrue("style should contain CSS content", style.data().contains("color"));
         assertTrue("meta after non-empty style should be structural",
             html.contains("<meta name=\"foo\">"));
         assertFalse("meta should not be escaped after non-empty style",
             html.contains("&lt;meta"));
     }

     @Test
     public void handlesEmptyStyleBeforeScriptAndMeta() {
         Document doc = Jsoup.parse("<head><style></style><script>alert(1)</script><meta
name=\"foo\"></head><body>One</body></html>");
         String html = doc.html();
         assertTrue("script content should be preserved", html.contains("alert(1)"));
         assertTrue("meta after empty style and script should be structural",
             html.contains("<meta name=\"foo\">"));
         assertFalse("meta should not be escaped", html.contains("&lt;meta"));
     }
 }
