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
         String xml = "<html xmlns=\"]8;id=md-ffz511;http://www.w3.org/1999/xhtmlhttp://www.w3.org/1999/xhtml]8;;]8;;\">" +]8;;
                      "<body>" +
                      "<div xmlns=\"]8;id=md-303dov;http://example.com/cliphttp://example.com/clip]8;;]8;;\"><span/></div>" +]8;;
                      "<p>hi</p>" +
                      "</body></html>";
         Document w3cDoc = convert(xml);
         Element html = w3cDoc.getDocumentElement();
         assertEquals("]8;id=md-ffz511;http://www.w3.org/1999/xhtmlhttp://www.w3.org/1999/xhtml]8;;]8;;", html.getNamespaceURI());]8;;

         NodeList ps = w3cDoc.getElementsByTagName("p");
         assertEquals(1, ps.getLength());
         assertEquals("]8;id=md-ffz511;http://www.w3.org/1999/xhtmlhttp://www.w3.org/1999/xhtml]8;;]8;;", ((Element) ps.item(0)).getNamespaceURI());]8;;

         NodeList divs = w3cDoc.getElementsByTagName("div");
         assertEquals(1, divs.getLength());
         assertEquals("]8;id=md-303dov;http://example.com/cliphttp://example.com/clip]8;;]8;;", ((Element) divs.item(0)).getNamespaceURI());]8;;
     }

     @Test
     public void nestedDefaultNamespaceOverride() {
         String xml = "<root xmlns=\"]8;id=md-nqhii5;http://a.comhttp://a.com]8;;]8;;\">" +]8;;
                      "<child xmlns=\"]8;id=md-647d5w;http://b.comhttp://b.com]8;;]8;;\"><grandchild/></child>" +]8;;
                      "<after/>" +
                      "</root>";
         Document w3cDoc = convert(xml);
         Element root = w3cDoc.getDocumentElement();
         assertEquals("]8;id=md-nqhii5;http://a.comhttp://a.com]8;;]8;;", root.getNamespaceURI());]8;;

         NodeList children = root.getElementsByTagName("child");
         assertEquals(1, children.getLength());
         Element child = (Element) children.item(0);
         assertEquals("]8;id=md-647d5w;http://b.comhttp://b.com]8;;]8;;", child.getNamespaceURI());]8;;

         NodeList grand = child.getElementsByTagName("grandchild");
         assertEquals(1, grand.getLength());
         assertEquals("]8;id=md-647d5w;http://b.comhttp://b.com]8;;]8;;", ((Element) grand.item(0)).getNamespaceURI());]8;;

         NodeList afterList = root.getElementsByTagName("after");
         assertEquals(1, afterList.getLength());
         assertEquals("]8;id=md-nqhii5;http://a.comhttp://a.com]8;;]8;;", ((Element) afterList.item(0)).getNamespaceURI());]8;;
     }

     @Test
     public void prefixedNamespace() {
         String xml = "<root xmlns:fb=\"]8;id=md-1xqn954;http://facebook.comhttp://facebook.com]8;;]8;;\"><fb:like>content</fb:like></root>";]8;;
         Document w3cDoc = convert(xml);
         NodeList likes = w3cDoc.getElementsByTagNameNS("]8;id=md-1xqn954;http://facebook.comhttp://facebook.com]8;;]8;;", "like");]8;;
         assertEquals(1, likes.getLength());
         Element like = (Element) likes.item(0);
         assertEquals("]8;id=md-1xqn954;http://facebook.comhttp://facebook.com]8;;]8;;", like.getNamespaceURI());]8;;
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
         String xml = "<root xmlns=\"]8;id=md-1xg3tm7;http://ns.comhttp://ns.com]8;;]8;;\"><a><b><c/></b></a></root>";]8;;
         Document w3cDoc = convert(xml);
         NodeList all = w3cDoc.getElementsByTagName("*");
         assertTrue(all.getLength() > 3);
         for (int i = 0; i < all.getLength(); i++) {
             Element el = (Element) all.item(i);
             assertEquals("]8;id=md-1xg3tm7;http://ns.comhttp://ns.com]8;;]8;;", el.getNamespaceURI());]8;;
         }
     }

     @Test
     public void emptyDefaultNamespaceOverride() {
         String xml = "<root xmlns=\"]8;id=md-nqhii5;http://a.comhttp://a.com]8;;]8;;\">" +]8;;
                      "<child xmlns=\"\"><grandchild/></child>" +
                      "<after/>" +
                      "</root>";
         Document w3cDoc = convert(xml);
         Element root = w3cDoc.getDocumentElement();
         assertEquals("]8;id=md-nqhii5;http://a.comhttp://a.com]8;;]8;;", root.getNamespaceURI());]8;;

         Element child = (Element) root.getElementsByTagName("child").item(0);
         assertNull("xmlns=\"\" should yield null namespace", child.getNamespaceURI());

         Element grandchild = (Element) child.getElementsByTagName("grandchild").item(0);
         assertNull("grandchild should inherit empty namespace", grandchild.getNamespaceURI());

         Element after = (Element) root.getElementsByTagName("after").item(0);
         assertEquals("after must revert to root's namespace", "]8;id=md-nqhii5;http://a.comhttp://a.com]8;;]8;;", ]8;;
after.getNamespaceURI());
     }

     @Test
     public void multipleDefaultNamespaceOverrides() {
         String xml = "<root xmlns=\"]8;id=md-nqhii5;http://a.comhttp://a.com]8;;]8;;\">" +]8;;
                      "<child1 xmlns=\"]8;id=md-647d5w;http://b.comhttp://b.com]8;;]8;;\"><sub/></child1>" +]8;;
                      "<child2 xmlns=\"]8;id=md-1nkxh5n;http://c.comhttp://c.com]8;;]8;;\"><sub/></child2>" +]8;;
                      "<after/>" +
                      "</root>";
         Document w3cDoc = convert(xml);
         Element root = w3cDoc.getDocumentElement();
         assertEquals("]8;id=md-nqhii5;http://a.comhttp://a.com]8;;]8;;", root.getNamespaceURI());]8;;

         Element child1 = (Element) root.getElementsByTagName("child1").item(0);
         assertEquals("]8;id=md-647d5w;http://b.comhttp://b.com]8;;]8;;", child1.getNamespaceURI());]8;;
         Element sub1 = (Element) child1.getElementsByTagName("sub").item(0);
         assertEquals("]8;id=md-647d5w;http://b.comhttp://b.com]8;;]8;;", sub1.getNamespaceURI());]8;;

         Element child2 = (Element) root.getElementsByTagName("child2").item(0);
         assertEquals("]8;id=md-1nkxh5n;http://c.comhttp://c.com]8;;]8;;", child2.getNamespaceURI());]8;;
         Element sub2 = (Element) child2.getElementsByTagName("sub").item(0);
         assertEquals("]8;id=md-1nkxh5n;http://c.comhttp://c.com]8;;]8;;", sub2.getNamespaceURI());]8;;

         Element after = (Element) root.getElementsByTagName("after").item(0);
         assertEquals("]8;id=md-nqhii5;http://a.comhttp://a.com]8;;]8;;", after.getNamespaceURI());]8;;
     }

     @Test
     public void prefixedNamespaceNotLeaked() {
         String xml = "<root xmlns:ns=\"]8;id=md-48brib;http://original.comhttp://original.com]8;;]8;;\">" +]8;;
                      "<child xmlns:ns=\"]8;id=md-9sg5jg;http://overridden.comhttp://overridden.com]8;;]8;;\"><ns:el/></child>" +]8;;
                      "<ns:el2/>" +
                      "</root>";
         Document w3cDoc = convert(xml);

         NodeList nsEl = w3cDoc.getElementsByTagNameNS("]8;id=md-9sg5jg;http://overridden.comhttp://overridden.com]8;;]8;;", "el");]8;;
         assertEquals(1, nsEl.getLength());
         assertEquals("]8;id=md-9sg5jg;http://overridden.comhttp://overridden.com]8;;]8;;", ((Element) nsEl.item(0)).getNamespaceURI());]8;;

         NodeList nsEl2 = w3cDoc.getElementsByTagNameNS("]8;id=md-48brib;http://original.comhttp://original.com]8;;]8;;", "el2");]8;;
         assertEquals(1, nsEl2.getLength());
         assertEquals("]8;id=md-48brib;http://original.comhttp://original.com]8;;]8;;", ((Element) nsEl2.item(0)).getNamespaceURI());]8;;
     }

     @Test
     public void copyAttributesPreserved() {
         String xml = "<root xmlns=\"]8;id=md-nqhii5;http://a.comhttp://a.com]8;;]8;;\" attr=\"val\"/>";]8;;
         Document w3cDoc = convert(xml);
         Element root = w3cDoc.getDocumentElement();
         assertEquals("val", root.getAttribute("attr"));
     }

     @Test
     public void convertPreservesDocumentUri() {
         String xml = "<root xmlns=\"]8;id=md-nqhii5;http://a.comhttp://a.com]8;;]8;;\"/>";]8;;
         org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(xml, "]8;id=md-x0lkzg;http://example.com/basehttp://example.com/base]8;;]8;;", ]8;;
Parser.xmlParser());
         Document w3cDoc = new W3CDom().fromJsoup(jsoupDoc);
         assertEquals("]8;id=md-x0lkzg;http://example.com/basehttp://example.com/base]8;;]8;;", w3cDoc.getDocumentURI());]8;;
     }

     @Test
     public void rootWithNoNamespaceIsNull() {
         String xml = "<root/>";
         Document w3cDoc = convert(xml);
         Element root = w3cDoc.getDocumentElement();
         assertNull(root.getNamespaceURI());
     }
 }
