package org.jsoup.helper;

 import org.jsoup.Jsoup;
 import org.jsoup.parser.Parser;
 import org.w3c.dom.Document;
 import org.w3c.dom.Element;
 import org.w3c.dom.NodeList;
 import org.junit.Test;

 import static org.junit.Assert.*;

 /**
  * Tests namespace preservation during jsoup-to-W3C DOM conversion.
  */
 public class W3CDomTest {

     private static Document convert(String xml) {
         org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(xml, "", Parser.xmlParser());
         return new W3CDom().fromJsoup(jsoupDoc);
     }

     @Test
     public void namespacePreservation() {
         // default namespace must not be corrupted by a nested override
         String xml = "<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
                      "<body>" +
                      "<div xmlns=\"http://example.com/clip\"><span/></div>" +
                      "<p>hi</p>" +
                      "</body></html>";
         Document w3cDoc = convert(xml);
         Element html = w3cDoc.getDocumentElement();
         assertEquals("http://www.w3.org/1999/xhtml", html.getNamespaceURI());

         NodeList ps = w3cDoc.getElementsByTagName("p");
         assertEquals(1, ps.getLength());
         assertEquals("http://www.w3.org/1999/xhtml", ((Element) ps.item(0)).getNamespaceURI());

         NodeList divs = w3cDoc.getElementsByTagName("div");
         assertEquals(1, divs.getLength());
         assertEquals("http://example.com/clip", ((Element) divs.item(0)).getNamespaceURI());
     }

     @Test
     public void nestedDefaultNamespaceOverride() {
         String xml = "<root xmlns=\"http://a.com\">" +
                      "<child xmlns=\"http://b.com\"><grandchild/></child>" +
                      "<after/>" +
                      "</root>";
         Document w3cDoc = convert(xml);
         Element root = w3cDoc.getDocumentElement();
         assertEquals("http://a.com", root.getNamespaceURI());

         NodeList children = root.getElementsByTagName("child");
         assertEquals(1, children.getLength());
         Element child = (Element) children.item(0);
         assertEquals("http://b.com", child.getNamespaceURI());

         NodeList grand = child.getElementsByTagName("grandchild");
         assertEquals(1, grand.getLength());
         assertEquals("http://b.com", ((Element) grand.item(0)).getNamespaceURI());

         NodeList afterList = root.getElementsByTagName("after");
         assertEquals(1, afterList.getLength());
         assertEquals("http://a.com", ((Element) afterList.item(0)).getNamespaceURI());
     }

     @Test
     public void prefixedNamespace() {
         String xml = "<root xmlns:fb=\"http://facebook.com\"><fb:like>content</fb:like></root>";
         Document w3cDoc = convert(xml);
         NodeList likes = w3cDoc.getElementsByTagNameNS("http://facebook.com", "like");
         assertEquals(1, likes.getLength());
         Element like = (Element) likes.item(0);
         assertEquals("http://facebook.com", like.getNamespaceURI());
         assertEquals("like", like.getLocalName());
     }

     @Test
     public void prefixWithoutNamespaceDeclaration() {
         String xml = "<root><fb:like /></root>";
         Document w3cDoc = convert(xml);
         NodeList all = w3cDoc.getElementsByTagName("*");
         Element like = null;
         for (int i = 0; i < all.getLength(); i++) {
             Element el = (Element) all.item(i);
             if ("like".equals(el.getLocalName()) || "fb:like".equals(el.getTagName())) {
                 like = el;
                 break;
             }
         }
         assertNotNull("fb:like element not found", like);
         assertNull("unbound prefix should yield null namespace", like.getNamespaceURI());
     }

     @Test
     public void deepNestingPreservesNamespace() {
         String xml = "<root xmlns=\"http://ns.com\"><a><b><c/></b></a></root>";
         Document w3cDoc = convert(xml);
         NodeList all = w3cDoc.getElementsByTagName("*");
         assertTrue(all.getLength() > 3);
         for (int i = 0; i < all.getLength(); i++) {
             Element el = (Element) all.item(i);
             assertEquals("http://ns.com", el.getNamespaceURI());
         }
     }

     @Test
     public void emptyDefaultNamespaceOverride() {
         String xml = "<root xmlns=\"http://a.com\">" +
                      "<child xmlns=\"\"><grandchild/></child>" +
                      "<after/>" +
                      "</root>";
         Document w3cDoc = convert(xml);
         Element root = w3cDoc.getDocumentElement();
         assertEquals("http://a.com", root.getNamespaceURI());

         Element child = (Element) root.getElementsByTagName("child").item(0);
         assertNull("xmlns=\"\" should yield null namespace", child.getNamespaceURI());

         Element grandchild = (Element) child.getElementsByTagName("grandchild").item(0);
         assertNull("grandchild should inherit empty namespace", grandchild.getNamespaceURI());

         Element after = (Element) root.getElementsByTagName("after").item(0);
         assertEquals("after must revert to root's namespace", "http://a.com",
                      after.getNamespaceURI());
     }

     @Test
     public void multipleDefaultNamespaceOverrides() {
         String xml = "<root xmlns=\"http://a.com\">" +
                      "<child1 xmlns=\"http://b.com\"><sub/></child1>" +
                      "<child2 xmlns=\"http://c.com\"><sub/></child2>" +
                      "<after/>" +
                      "</root>";
         Document w3cDoc = convert(xml);
         Element root = w3cDoc.getDocumentElement();
         assertEquals("http://a.com", root.getNamespaceURI());

         Element child1 = (Element) root.getElementsByTagName("child1").item(0);
         assertEquals("http://b.com", child1.getNamespaceURI());
         Element sub1 = (Element) child1.getElementsByTagName("sub").item(0);
         assertEquals("http://b.com", sub1.getNamespaceURI());

         Element child2 = (Element) root.getElementsByTagName("child2").item(0);
         assertEquals("http://c.com", child2.getNamespaceURI());
         Element sub2 = (Element) child2.getElementsByTagName("sub").item(0);
         assertEquals("http://c.com", sub2.getNamespaceURI());

         Element after = (Element) root.getElementsByTagName("after").item(0);
         assertEquals("http://a.com", after.getNamespaceURI());
     }

     @Test
     public void prefixedNamespaceNotLeaked() {
         String xml = "<root xmlns:ns=\"http://original.com\">" +
                      "<child xmlns:ns=\"http://overridden.com\"><ns:el/></child>" +
                      "<ns:el2/>" +
                      "</root>";
         Document w3cDoc = convert(xml);

         NodeList nsEl = w3cDoc.getElementsByTagNameNS("http://overridden.com", "el");
         assertEquals(1, nsEl.getLength());
         assertEquals("http://overridden.com", ((Element) nsEl.item(0)).getNamespaceURI());

         NodeList nsEl2 = w3cDoc.getElementsByTagNameNS("http://original.com", "el2");
         assertEquals(1, nsEl2.getLength());
         assertEquals("http://original.com", ((Element) nsEl2.item(0)).getNamespaceURI());
     }

     @Test
     public void copyAttributesPreserved() {
         String xml = "<root xmlns=\"http://a.com\" attr=\"val\"/>";
         Document w3cDoc = convert(xml);
         Element root = w3cDoc.getDocumentElement();
         assertEquals("val", root.getAttribute("attr"));
     }

     @Test
     public void convertPreservesDocumentUri() {
         String xml = "<root xmlns=\"http://a.com\"/>";
         org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(xml, "http://example.com/base",
                                                         Parser.xmlParser());
         Document w3cDoc = new W3CDom().fromJsoup(jsoupDoc);
         assertEquals("http://example.com/base", w3cDoc.getDocumentURI());
     }

     @Test
     public void rootWithNoNamespaceIsNull() {
         String xml = "<root/>";
         Document w3cDoc = convert(xml);
         Element root = w3cDoc.getDocumentElement();
         assertNull(root.getNamespaceURI());
     }
 }