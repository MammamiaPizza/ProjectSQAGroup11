package org.apache.commons.jxpath.ri.model;

 import junit.framework.TestCase;

 import java.io.ByteArrayInputStream;
 import java.io.StringReader;
 import java.util.Iterator;

 import javax.xml.parsers.DocumentBuilderFactory;

 import org.apache.commons.jxpath.JXPathContext;
 import org.jdom.input.SAXBuilder;
 import org.w3c.dom.Document;

 /**
  * Tests for {@link DOMAttributeIterator} and {@link JDOMAttributeIterator}
  * covering the bug that causes attributes only from a single parent node to
  * be returned when a path matches multiple parent nodes.
  */
 public class AttributeIteratorBugTest extends TestCase {

     // --------------- DOM tests ---------------

     private JXPathContext domContext;

     private JXPathContext createDOMContext(String xml) throws Exception {
         Document doc = DocumentBuilderFactory
                 .newInstance()
                 .newDocumentBuilder()
                 .parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
         return JXPathContext.newContext(doc);
     }

     public void testDOMWildcardMultipleElements() throws Exception {
         String xml = "<root><item a1='v1'/><item a2='v2'/></root>";
         JXPathContext ctx = createDOMContext(xml);
         Iterator it = ctx.iterate("/root/item/@*");
         Object r1 = it.next();
         Object r2 = it.next();
         assertFalse(it.hasNext());
         // v1 and v2 in document order
         assertTrue(("v1".equals(r1) && "v2".equals(r2))
                 || ("v2".equals(r1) && "v1".equals(r2)));
     }

     public void testDOMSpecificAttrMultipleElements() throws Exception {
         String xml = "<root><item a='x'/><item a='y'/><item b='z'/></root>";
         JXPathContext ctx = createDOMContext(xml);
         Iterator it = ctx.iterate("/root/item/@a");
         assertEquals("x", it.next());
         assertEquals("y", it.next());
         assertFalse(it.hasNext());
     }

     public void testDOMSingleElementMultipleAttrs() throws Exception {
         String xml = "<root><item a='1' b='2' c='3'/></root>";
         JXPathContext ctx = createDOMContext(xml);
         Iterator it = ctx.iterate("/root/item/@*");
         // All three attributes must be present
         assertTrue(it.hasNext());
         assertTrue(it.hasNext());
         assertTrue(it.hasNext());
         assertFalse(it.hasNext());
     }

     public void testDOMNoAttributes() throws Exception {
         String xml = "<root><item/><item/></root>";
         JXPathContext ctx = createDOMContext(xml);
         Iterator it = ctx.iterate("/root/item/@*");
         assertFalse("Expected no attributes", it.hasNext());
     }

     public void testDOMFirstNodeNoAttrSecondHasAttr() throws Exception {
         String xml = "<root><item/><item a='z'/></root>";
         JXPathContext ctx = createDOMContext(xml);
         Iterator it = ctx.iterate("/root/item/@*");
         assertTrue(it.hasNext());
         assertEquals("z", it.next());
         assertFalse(it.hasNext());
     }

     public void testDOMNamespaceAttributes() throws Exception {
         String xml = "<root xmlns:ns='http://example.com'>"
                 + "<item ns:a='ns-v1'/><item ns:a='ns-v2'/><item a='non-ns'/>"
                 + "</root>";
         JXPathContext ctx = createDOMContext(xml);
         ctx.registerNamespace("ns", "http://example.com");
         Iterator it = ctx.iterate("/root/item/@ns:a");
         assertEquals("ns-v1", it.next());
         assertEquals("ns-v2", it.next());
         assertFalse(it.hasNext());
     }

     public void testDOMSpecificAttrNotEveryNode() throws Exception {
         String xml = "<root><item a='1'/><item b='2'/></root>";
         JXPathContext ctx = createDOMContext(xml);
         Iterator it = ctx.iterate("/root/item/@a");
         assertTrue(it.hasNext());
         assertEquals("1", it.next());
         assertFalse(it.hasNext()); // only one node has @a
     }

     // --------------- JDOM tests ---------------

     private JXPathContext createJDOMContext(String xml) throws Exception {
         SAXBuilder builder = new SAXBuilder();
         org.jdom.Document doc = builder.build(new StringReader(xml));
         return JXPathContext.newContext(doc);
     }

     public void testJDOMWildcardMultipleElements() throws Exception {
         String xml = "<root><item a1='v1'/><item a2='v2'/></root>";
         JXPathContext ctx = createJDOMContext(xml);
         Iterator it = ctx.iterate("/root/item/@*");
         Object r1 = it.next();
         Object r2 = it.next();
         assertFalse(it.hasNext());
         assertTrue(("v1".equals(r1) && "v2".equals(r2))
                 || ("v2".equals(r1) && "v1".equals(r2)));
     }

     public void testJDOMSpecificAttrMultipleElements() throws Exception {
         String xml = "<root><item a='x'/><item a='y'/><item b='z'/></root>";
         JXPathContext ctx = createJDOMContext(xml);
         Iterator it = ctx.iterate("/root/item/@a");
         assertEquals("x", it.next());
         assertEquals("y", it.next());
         assertFalse(it.hasNext());
     }

     public void testJDOMSingleElementMultipleAttrs() throws Exception {
         String xml = "<root><item a='1' b='2' c='3'/></root>";
         JXPathContext ctx = createJDOMContext(xml);
         Iterator it = ctx.iterate("/root/item/@*");
         assertTrue(it.hasNext());
         assertTrue(it.hasNext());
         assertTrue(it.hasNext());
         assertFalse(it.hasNext());
     }

     public void testJDOMFirstNodeNoAttrSecondHasAttr() throws Exception {
         String xml = "<root><item/><item a='z'/></root>";
         JXPathContext ctx = createJDOMContext(xml);
         Iterator it = ctx.iterate("/root/item/@*");
         assertTrue(it.hasNext());
         assertEquals("z", it.next());
         assertFalse(it.hasNext());
     }
 }