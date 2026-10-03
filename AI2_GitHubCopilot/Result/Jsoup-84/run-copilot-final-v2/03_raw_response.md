package org.jsoup.helper;

 import static org.junit.Assert.*;

 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.junit.Test;
 import org.w3c.dom.Element;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;

 public class W3CDomTest {

     @Test
     public void testSimpleElementConversion() {
         String html = "<div></div>";
         Document jsoupDoc = Jsoup.parse(html);
         W3CDom w3c = new W3CDom();
         org.w3c.dom.Document doc = w3c.fromJsoup(jsoupDoc);
         Element root = doc.getDocumentElement();
         assertNotNull(root);
         assertEquals("div", root.getTagName());
     }

     @Test
     public void testTextNodeConversion() {
         String html = "<p>Hello World</p>";
         Document jsoupDoc = Jsoup.parse(html);
         W3CDom w3c = new W3CDom();
         org.w3c.dom.Document doc = w3c.fromJsoup(jsoupDoc);
         Element root = doc.getDocumentElement();
         assertEquals("Hello World", root.getTextContent());
     }

     @Test
     public void testCommentNodeConversion() {
         String html = "<div><!-- comment --></div>";
         Document jsoupDoc = Jsoup.parse(html);
         W3CDom w3c = new W3CDom();
         org.w3c.dom.Document doc = w3c.fromJsoup(jsoupDoc);
         Element root = doc.getDocumentElement();
         Node child = root.getFirstChild();
         assertNotNull(child);
         assertEquals(Node.COMMENT_NODE, child.getNodeType());
         assertEquals(" comment ", child.getNodeValue());
     }

     @Test
     public void testElementWithDefaultNamespace() {
         String html = "<div xmlns='http://default.ns'><child/></div>";
         Document jsoupDoc = Jsoup.parse(html);
         W3CDom w3c = new W3CDom();
         org.w3c.dom.Document doc = w3c.fromJsoup(jsoupDoc);
         Element root = doc.getDocumentElement();
         assertEquals("http://default.ns", root.getNamespaceURI());
         assertEquals("div", root.getLocalName());

         // child should inherit default namespace
         NodeList children = root.getChildNodes();
         Element child = null;
         for (int i = 0; i < children.getLength(); i++) {
             Node n = children.item(i);
             if (n.getNodeType() == Node.ELEMENT_NODE) {
                 child = (Element) n;
                 break;
             }
         }
         assertNotNull(child);
         assertEquals("http://default.ns", child.getNamespaceURI());
         assertEquals("child", child.getLocalName());
     }

     @Test
     public void testElementWithPrefixNamespace() {
         String html = "<ns:div xmlns:ns='http://exns'><ns:child/></ns:div>";
         Document jsoupDoc = Jsoup.parse(html);
         W3CDom w3c = new W3CDom();
         org.w3c.dom.Document doc = w3c.fromJsoup(jsoupDoc);
         Element root = doc.getDocumentElement();
         assertEquals("http://exns", root.getNamespaceURI());
         assertEquals("div", root.getLocalName());

         NodeList children = root.getChildNodes();
         Element child = null;
         for (int i = 0; i < children.getLength(); i++) {
             Node n = children.item(i);
             if (n.getNodeType() == Node.ELEMENT_NODE) {
                 child = (Element) n;
                 break;
             }
         }
         assertNotNull(child);
         assertEquals("http://exns", child.getNamespaceURI());
         assertEquals("child", child.getLocalName());
     }

     @Test
     public void testDeclaredNamespaceAttributePreserved() {
         String html = "<div xmlns:a='http://a.ns' a:attrr='hello'/>";
         Document jsoupDoc = Jsoup.parse(html);
         W3CDom w3c = new W3CDom();
         org.w3c.dom.Document doc = w3c.fromJsoup(jsoupDoc);
         Element root = doc.getDocumentElement();
         // attribute should be accessible via namespace-aware methods
         assertEquals("hello", root.getAttributeNS("http://a.ns", "attr"));
         assertTrue(root.hasAttributeNS("http://a.ns", "attr"));
     }

     @Test
     public void testTreatsUndeclaredNamespaceAsLocalName() {
         // Bug trigger: attribute with undeclared prefix must not cause NAMESPACE_ERR
         String html = "<div pre:attr='value'/>";
         Document jsoupDoc = Jsoup.parse(html);
         W3CDom w3c = new W3CDom();
         org.w3c.dom.Document doc = w3c.fromJsoup(jsoupDoc);
         Element root = doc.getDocumentElement();
         // After fix, the attribute should be stored as a local attribute
         assertEquals("value", root.getAttribute("attr"));
     }

     @Test
     public void testNestedElementsWithDifferentNamespaces() {
         String html = "<a:outer xmlns:a='http://a.ns' xmlns:b='http://b.ns'>"
                 + "<b:inner b:attrr='val'/>"
                 + "</a:outer>";
         Document jsoupDoc = Jsoup.parse(html);
         W3CDom w3c = new W3CDom();
         org.w3c.dom.Document doc = w3c.fromJsoup(jsoupDoc);
         Element outer = doc.getDocumentElement();
         assertEquals("http://a.ns", outer.getNamespaceURI());
         assertEquals("outer", outer.getLocalName());

         NodeList children = outer.getChildNodes();
         Element inner = null;
         for (int i = 0; i < children.getLength(); i++) {
             Node n = children.item(i);
             if (n.getNodeType() == Node.ELEMENT_NODE) {
                 inner = (Element) n;
                 break;
             }
         }
         assertNotNull(inner);
         assertEquals("http://b.ns", inner.getNamespaceURI());
         assertEquals("inner", inner.getLocalName());
         assertEquals("val", inner.getAttributeNS("http://b.ns", "attr"));
     }

     @Test
     public void testMixedDeclaredAndUndeclaredAttributes() {
         String html = "<div xmlns:known='http://known' known:ok='1' unknown:attrr='2'/>";
         Document jsoupDoc = Jsoup.parse(html);
         W3CDom w3c = new W3CDom();
         org.w3c.dom.Document doc = w3c.fromJsoup(jsoupDoc);
         Element root = doc.getDocumentElement();
         // declared attribute should be in the namespace
         assertEquals("1", root.getAttributeNS("http://known", "ok");
         // undeclared should become local attribute
         assertEquals("2", root.getAttribute("attr"));
     }

     @Test
     public void testEmptyDocumentConversion() {
         String html = "";
         Document jsoupDoc = Jsoup.parse(html);
         W3CDom w3c = new W3CDom();
         org.w3c.dom.Document doc = w3c.fromJsoup(jsoupDoc);
         // empty jsoup document yields a document with <html><head></head><body></body></html>
 automatically
         Element root = doc.getDocumentElement();
         assertNotNull(root);
         assertEquals("html", root.getNodeName());
     }

     @Test
     public void testInvalidXmlAttributeNameFiltered() {
         // copyAttributes filters invalid XLM attribute characters, no exception expected
         String html = "<div 012invalid='should not be set' valid='ok'/>";
         Document jsoupDoc = Jsoup.parse(html);
         W3CDom w3c = new W3CDom();
         org.w3c.dom.Document doc = w3c.fromJsoup(jsoupDoc);
         Element root = doc.getDocumentElement();
         // valid attribute passes through
         assertEquals("ok", root.getAttribute("valid"));
         // invalid name should be skipped
         assertFalse(root.hasAttribute("012invalid"));
     }

     @Test
     public void testMultipleUndeclaredPrefixesNoException() {
         String html = "<div a:x='1' b:y='2' c:z='3'/>";
         Document jsoupDoc = Jsoup.parse(html);
         W3CDom w3c = new W3CDom();
         org.w3c.dom.Document doc = w3c.fromJsoup(jsoupDoc);
         Element root = doc.getDocumentElement();
         assertEquals("1", root.getAttribute("x"));
         assertEquals("2", root.getAttribute("y"));
         assertEquals("3", root.getAttribute("z"));
     }

     @Test
     public void testAsStringSerialization() {
         String html = "<div><p>text</p></div>";
         Document jsoupDoc = Jsoup.parse(html);
         W3CDom w3c = new W3CDom();
         org.w3c.dom.Document doc = w3c.fromJsoup(jsoupDoc);
         String xml = w3c.asString(doc);
         assertTrue(xml.contains("<div>"));
         assertTrue(xml.contains("<p>text</p>"));
         assertTrue(xml.contains("</div>"));
     }
 }