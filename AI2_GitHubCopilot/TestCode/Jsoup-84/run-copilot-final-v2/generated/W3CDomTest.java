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
         String html = "<div xmlns=']8;id=md-1o86cwx;http://default.nshttp://default.ns]8;;]8;;'><child/></div>";]8;;
         Document jsoupDoc = Jsoup.parse(html);
         W3CDom w3c = new W3CDom();
         org.w3c.dom.Document doc = w3c.fromJsoup(jsoupDoc);
         Element root = doc.getDocumentElement();
         assertEquals("]8;id=md-1o86cwx;http://default.nshttp://default.ns]8;;]8;;", root.getNamespaceURI());]8;;
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
     assertEquals("]8;id=md-1o86cwx;http://default.nshttp://default.ns]8;;]8;;", child.getNamespaceURI());]8;;
     assertEquals("child", child.getLocalName());
 }

 @Test
 public void testElementWithPrefixNamespace() {
     String html = "<ns:div xmlns:ns=']8;id=md-lsgbwh;http://exnshttp://exns]8;;]8;;'><ns:child/></ns:div>";]8;;
     Document jsoupDoc = Jsoup.parse(html);
     W3CDom w3c = new W3CDom();
     org.w3c.dom.Document doc = w3c.fromJsoup(jsoupDoc);
     Element root = doc.getDocumentElement();
     assertEquals("]8;id=md-lsgbwh;http://exnshttp://exns]8;;]8;;", root.getNamespaceURI());]8;;
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
     assertEquals("]8;id=md-lsgbwh;http://exnshttp://exns]8;;]8;;", child.getNamespaceURI());]8;;
     assertEquals("child", child.getLocalName());
 }

 @Test
 public void testDeclaredNamespaceAttributePreserved() {
     String html = "<div xmlns:a=']8;id=md-z5rocr;http://a.nshttp://a.ns]8;;]8;;' a:attr='hello'/>";]8;;
     Document jsoupDoc = Jsoup.parse(html);
     W3CDom w3c = new W3CDom();
     org.w3c.dom.Document doc = w3c.fromJsoup(jsoupDoc);
     Element root = doc.getDocumentElement();
     // attribute should be accessible via namespace-aware methods
     assertEquals("hello", root.getAttributeNS("]8;id=md-z5rocr;http://a.nshttp://a.ns]8;;]8;;", "attr"));]8;;
     assertTrue(root.hasAttributeNS("]8;id=md-z5rocr;http://a.nshttp://a.ns]8;;]8;;", "attr"));]8;;
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
     String html = "<a:outer xmlns:a=']8;id=md-z5rocr;http://a.nshttp://a.ns]8;;]8;;' xmlns:b=']8;;]8;id=md-7ge4pc;http://b.nshttp://b.ns]8;;]8;;'>"]8;;
                 + "<b:inner b:attr='val'/>"
                 + "</a:outer>";
     Document jsoupDoc = Jsoup.parse(html);
     W3CDom w3c = new W3CDom();
     org.w3c.dom.Document doc = w3c.fromJsoup(jsoupDoc);
     Element outer = doc.getDocumentElement();
     assertEquals("]8;id=md-z5rocr;http://a.nshttp://a.ns]8;;]8;;", outer.getNamespaceURI());]8;;
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
     assertEquals("]8;id=md-7ge4pc;http://b.nshttp://b.ns]8;;]8;;", inner.getNamespaceURI());]8;;
     assertEquals("inner", inner.getLocalName());
     assertEquals("val", inner.getAttributeNS("]8;id=md-7ge4pc;http://b.nshttp://b.ns]8;;]8;;", "attr"));]8;;
 }

 @Test
 public void testMixedDeclaredAndUndeclaredAttributes() {
     String html = "<div xmlns:known=']8;id=md-1hoguyq;http://knownhttp://known]8;;]8;;' known:ok='1' unknown:attr='2'/>";]8;;
     Document jsoupDoc = Jsoup.parse(html);
     W3CDom w3c = new W3CDom();
     org.w3c.dom.Document doc = w3c.fromJsoup(jsoupDoc);
     Element root = doc.getDocumentElement();
     // declared attribute should be in the namespace
     assertEquals("1", root.getAttributeNS("]8;id=md-1hoguyq;http://knownhttp://known]8;;]8;;", "ok"));]8;;
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
     // copyAttributes filters invalid XML attribute characters, no exception expected
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
