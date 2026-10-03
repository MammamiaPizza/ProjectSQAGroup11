package org.jsoup.helper;

 import org.jsoup.Jsoup;
 import org.junit.Test;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;

 public class W3CDomTest {

     @Test
     public void handlesInvalidAttributeNames() {
         String[] names = {"-leading", "1leading", "a b", "=", "<", "属性", "."};

         for (String name : names) {
             org.jsoup.nodes.Document html = Jsoup.parse("<div data-ok=\"yes\"></div>");
             org.jsoup.nodes.Element div = html.select("div").first();
             div.attr(name, "value");

             org.w3c.dom.Document w3cDoc = new W3CDom().fromJsoup(html);

             assertNotNull(w3cDoc);
             org.w3c.dom.Element w3cDiv = (org.w3c.dom.Element) w3cDoc.getDocumentElement();
             assertEquals("yes", w3cDiv.getAttribute("data-ok"));
         }
     }

     @Test
     public void handlesColonContainingAndUnboundPrefixAttributeNames() {
         org.jsoup.nodes.Document html = Jsoup.parse("<div></div>");
         org.jsoup.nodes.Element div = html.select("div").first();
         div.attr("fb:like", "test");
         div.attr(":", "");

         org.w3c.dom.Document w3cDoc = new W3CDom().fromJsoup(html);

         assertNotNull(w3cDoc);
         org.w3c.dom.Element w3cDiv = (org.w3c.dom.Element) w3cDoc.getDocumentElement();
         assertEquals("div", w3cDiv.getTagName());
         assertEquals("test", w3cDiv.getAttribute("fb:like"));
     }

     @Test
     public void ignoresInvalidAttributesWhenElementHasOnlyInvalidAttributes() {
         org.jsoup.nodes.Document html = Jsoup.parse("<div></div>");
         org.jsoup.nodes.Element div = html.select("div").first();
         div.attr("-bad", "x");
         div.attr("123abc", "x");
         div.attr("属性", "x");

         org.w3c.dom.Document w3cDoc = new W3CDom().fromJsoup(html);

         assertNotNull(w3cDoc);
         org.w3c.dom.Element w3cDiv = (org.w3c.dom.Element) w3cDoc.getDocumentElement();
         assertEquals("div", w3cDiv.getTagName());
     }

     @Test
     public void convertsEmptyAttributeValue() {
         org.jsoup.nodes.Document html = Jsoup.parse("<div data-empty=\"\"></div>");

         org.w3c.dom.Document w3cDoc = new W3CDom().fromJsoup(html);

         org.w3c.dom.Element w3cDiv = (org.w3c.dom.Element) w3cDoc.getDocumentElement();
         assertEquals("", w3cDiv.getAttribute("data-empty"));
     }

     @Test
     public void convertsVeryLongAttributeName() {
         String longName = repeat('a', 1024);
         org.jsoup.nodes.Document html = Jsoup.parse("<div></div>");
         html.select("div").first().attr(longName, "long");

         org.w3c.dom.Document w3cDoc = new W3CDom().fromJsoup(html);

         org.w3c.dom.Element w3cDiv = (org.w3c.dom.Element) w3cDoc.getDocumentElement();
         assertEquals("long", w3cDiv.getAttribute(longName));
     }

     @Test
     public void convertsSimpleDocument() {
         org.jsoup.nodes.Document html = Jsoup.parse("<div>Hello</div>");

         org.w3c.dom.Document w3cDoc = new W3CDom().fromJsoup(html);

         org.w3c.dom.Element w3cDiv = (org.w3c.dom.Element) w3cDoc.getDocumentElement();
         assertEquals("div", w3cDiv.getTagName());
         assertEquals("Hello", w3cDiv.getTextContent());
     }

     @Test(expected = IllegalArgumentException.class)
     public void fromJsoupRejectsNullInput() {
         new W3CDom().fromJsoup(null);
     }

     private static String repeat(char c, int count) {
         StringBuilder sb = new StringBuilder(count);
         for (int i = 0; i < count; i++) {
             sb.append(c);
         }
         return sb.toString();
     }
 }