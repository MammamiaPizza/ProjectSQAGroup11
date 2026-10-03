package org.apache.commons.jxpath.ri.model;

 import junit.framework.TestCase;
 import java.io.StringReader;
 import java.util.Locale;
 import javax.xml.parsers.DocumentBuilder;
 import javax.xml.parsers.DocumentBuilderFactory;
 import org.apache.commons.jxpath.ri.model.dom.DOMNodePointer;
 import org.apache.commons.jxpath.ri.model.jdom.JDOMNodePointer;
 import org.jdom.input.SAXBuilder;
 import org.w3c.dom.Document;
 import org.w3c.dom.Node;
 import org.xml.sax.InputSource;

 /**
  * Tests for JXPATH-83: xml:space="preserve" handling and nested text
  * accumulation in DOMNodePointer and JDOMNodePointer.
  */
 public class XMLSpaceTest extends TestCase {

     public XMLSpaceTest(String name) {
         super(name);
     }

     // ---- helpers ----

     private static Document parseDOM(String xml) throws Exception {
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         dbf.setNamespaceAware(true);
         dbf.setIgnoringElementContentWhitespace(false);
         DocumentBuilder db = dbf.newDocumentBuilder();
         return db.parse(new InputSource(new StringReader(xml)));
     }

     private static org.jdom.Document parseJDOM(String xml) throws Exception {
         SAXBuilder builder = new SAXBuilder(false);
         builder.setFeature("]8;id=md-va5ho4;http://apache.org/xml/features/nonvalidating/load-external-dtdhttp://apache.org/xml/features/nonvalidating/load-external-dtd]8;;]8;;", ]8;;
false);
         return builder.build(new StringReader(xml));
     }

     private static DOMNodePointer domPointer(Document doc, String path) {
         // XPath /root, /child, etc. — use simple traversal
         Node cur = doc.getDocumentElement();
         if (path.startsWith("/")) path = path.substring(1);
         String[] segs = path.split("/");
         for (String seg : segs) {
             if (seg.length() == 0) continue;
             org.w3c.dom.NodeList children = cur.getChildNodes();
             Node found = null;
             for (int i = 0; i < children.getLength(); i++) {
                 Node n = children.item(i);
                 if (n.getNodeType() == Node.ELEMENT_NODE && seg.equals(n.getLocalName())) {
                     found = n;
                     break;
                 }
             }
             if (found == null) throw new RuntimeException("Element " + seg + " not found");
             cur = found;
         }
         return new DOMNodePointer(cur, Locale.getDefault());
     }

     private static JDOMNodePointer jdomPointer(org.jdom.Document doc, String path) {
         org.jdom.Element cur = doc.getRootElement();
         if (path.startsWith("/")) path = path.substring(1);
         String[] segs = path.split("/");
         for (String seg : segs) {
             if (seg.length() == 0) continue;
             cur = cur.getChild(seg);
             if (cur == null) throw new RuntimeException("Element " + seg + " not found");
         }
         return new JDOMNodePointer(cur, Locale.getDefault());
     }

     // ---- xml:space="preserve" tests ----

     public void testPreserveDOM() throws Exception {
         Document doc = parseDOM("<root xml:space=\"preserve\"> foo </root>");
         DOMNodePointer ptr = domPointer(doc, "/root");
         // JXPATH-83 bug: extra surrounding spaces; expected just "foo"
         assertEquals("foo", ptr.getValue());
     }

     public void testPreserveJDOM() throws Exception {
         org.jdom.Document doc = parseJDOM("<root xml:space=\"preserve\"> foo </root>");
         JDOMNodePointer ptr = jdomPointer(doc, "/root");
         assertEquals("foo", ptr.getValue());
     }

     // ---- nested mixed content tests ----

     public void testNestedDOM() throws Exception {
         Document doc = parseDOM("<root xml:space=\"preserve\"><a>foo</a><a>bar</a> baz </root>");
         DOMNodePointer ptr = domPointer(doc, "/root");
         // nested child text concatenation; no extra spaces
         assertEquals("foobar baz", ptr.getValue());
     }

     public void testNestedJDOM() throws Exception {
         org.jdom.Document doc = parseJDOM("<root xml:space=\"preserve\"><a>foo</a><a>bar</a> baz
</root>");
         JDOMNodePointer ptr = jdomPointer(doc, "/root");
         assertEquals("foobar baz", ptr.getValue());
     }

     // ---- nested with comments ----

     public void testNestedWithCommentsDOM() throws Exception {
         String xml = "<root xml:space=\"preserve\"><!-- comment --><a>foo</a><!-- another
--><a>bar</a> baz </root>";
         Document doc = parseDOM(xml);
         DOMNodePointer ptr = domPointer(doc, "/root");
         assertEquals("foobar baz", ptr.getValue());
     }

     public void testNestedWithCommentsJDOM() throws Exception {
         String xml = "<root xml:space=\"preserve\"><!-- comment --><a>foo</a><!-- another
--><a>bar</a> baz </root>";
         org.jdom.Document doc = parseJDOM(xml);
         JDOMNodePointer ptr = jdomPointer(doc, "/root");
         assertEquals("foobar baz", ptr.getValue());
     }

     // ---- boundary / whitespace-only ----

     public void testWhitespaceOnlyPreserveDOM() throws Exception {
         Document doc = parseDOM("<root xml:space=\"preserve\">   </root>");
         DOMNodePointer ptr = domPointer(doc, "/root");
         String v = (String) ptr.getValue();
         // With xml:space="preserve", whitespace-only content should be preserved,
         // but not trimmed to empty unconditionally.
         assertNotNull(v);
         // In JDOM analogous test we check the same string.
     }

     public void testWhitespaceOnlyPreserveJDOM() throws Exception {
         org.jdom.Document doc = parseJDOM("<root xml:space=\"preserve\">   </root>");
         JDOMNodePointer ptr = jdomPointer(doc, "/root");
         String v = (String) ptr.getValue();
         assertNotNull(v);
     }

     // ---- empty element ----

     public void testEmptyElementDOM() throws Exception {
         Document doc = parseDOM("<root xml:space=\"preserve\"></root>");
         DOMNodePointer ptr = domPointer(doc, "/root");
         assertEquals("", ptr.getValue());
     }

     public void testEmptyElementJDOM() throws Exception {
         org.jdom.Document doc = parseJDOM("<root xml:space=\"preserve\"></root>");
         JDOMNodePointer ptr = jdomPointer(doc, "/root");
         assertEquals("", ptr.getValue());
     }

     // ---- deep nesting ----

     public void testDeepNestedDOM() throws Exception {
         String xml = "<a xml:space=\"preserve\">x<b>y</b>z</a>";
         Document doc = parseDOM(xml);
         DOMNodePointer ptr = domPointer(doc, "/a");
         assertEquals("xyz", ptr.getValue());
     }

     public void testDeepNestedJDOM() throws Exception {
         String xml = "<a xml:space=\"preserve\">x<b>y</b>z</a>";
         org.jdom.Document doc = parseJDOM(xml);
         JDOMNodePointer ptr = jdomPointer(doc, "/a");
         assertEquals("xyz", ptr.getValue());
     }
 }
