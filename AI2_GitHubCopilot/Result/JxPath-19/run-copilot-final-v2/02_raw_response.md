package org.apache.commons.jxpath.ri.model;

 import java.io.ByteArrayInputStream;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;

 import javax.xml.parsers.DocumentBuilder;
 import javax.xml.parsers.DocumentBuilderFactory;

 import junit.framework.TestCase;

 import org.apache.commons.jxpath.JXPathContext;
 import org.apache.commons.jxpath.Pointer;
 import org.apache.commons.jxpath.ri.model.dom.DOMNodePointer;
 import org.apache.commons.jxpath.ri.model.jdom.JDOMNodePointer;
 import org.jdom.input.SAXBuilder;
 import org.w3c.dom.Document;
 import org.w3c.dom.Element;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;

 /**
  * Tests for aliased namespace iteration bug JXPATH-125.
  */
 public class AliasedNamespaceIterationTest extends TestCase {

     private static final String NS_URI = "http://example.com/a";
     private static final String PREFIX = "a";

     // --- DOM tests ---

     public void testIterateDOM_TwoElements() throws Exception {
         String xml = "<a:doc xmlns:a='" + NS_URI + "'><a:elem/><a:elem/></a:doc>";
         Document doc = parseDOM(xml);
         JXPathContext ctx = JXPathContext.newContext(doc);
         ctx.registerNamespace(PREFIX, NS_URI);
         Iterator it = ctx.iteratePointers("/a:elem")';
         List paths = collectPaths(it);
         assertEquals(2, paths.size());
         assertEquals("/a:doc[1]/a:elem[1]", paths.get(0));
         assertEquals("/a:doc[1]/a:elem[2]", paths.get(1));
     }

     public void testIterateDOM_SingleElement() throws Exception {
         String xml = "<a:doc xmlns:a='" + NS_URI + "'><a:elem/></a:doc>";
         Document doc = parseDOM(xml);
         JXPathContext ctx = JXPathContext.newContext(doc);
         ctx.registerNamespace(PREFIX, NS_URI);
         Iterator it = ctx.iteratePointers("/a:elem");
         List paths = collectPaths(it);
         assertEquals(1, paths.size());
         assertEquals("/a:doc[1]/a:elem[1]", paths.get(0));
     }

     public void testIterateDOM_NoElements() throws Exception {
         String xml = "<a:doc xmlns:a='" + NS_URI + "'/>";
         Document doc = parseDOM(xml);
         JXPathContext ctx = JXPathContext.newContext(doc);
         ctx.registerNamespace(PREFIX, NS_URI);
         Iterator it = ctx.iteratePointers("/a:elem");
         assertFalse(it.hasNext());
     }

     public void testIterateDOM_DeeplyNested() throws Exception {
         String xml = "<a:doc xmlns:a='" + NS_URI + "'>"
                    + "  <a:child>"
                    + "    <a:elem/>"
                    + "    <a:elem/>"
                    + "  </a:child>"
                    + "</a:doc>";
         Document doc = parseDOM(xml);
         JXPathContext ctx = JXPathContext.newContext(doc);
         ctx.registerNamespace(PREFIX, NS_URI);
         Iterator it = ctx.iteratePointers("/a:child/a:elem");
         List paths = collectPaths(it);
         assertEquals(2, paths.size());
         assertEquals("/a:doc[1]/a:child[1]/a:elem[1]", paths.get(0));
         assertEquals("/a:doc[1]/a:child[1]/a:elem[2]", paths.get(1));
     }

     public void testIterateDOM_ThreeElements() throws Exception {
         String xml = "<a:doc xmlns:a='" + NS_URI + "'><a:elem/><a:elem/><a:elem/></a:doc>";
         Document doc = parseDOM(xml);
         JXPathContext ctx = JXPathContext.newContext(doc);
         ctx.registerNamespace(PREFIX, NS_URI);
         Iterator it = ctx.iteratePointers("/a:elem")';
         List paths = collectPaths(it);
         assertEquals(3, paths.size());
         assertEquals("/a:doc[1]/a:elem[1]", paths.get(0));
         assertEquals("/a:doc[1]/a:elem[2]", paths.get(1));
         assertEquals("/a:doc[1]/a:elem[3]", paths.get(2));
     }

     public void testIterateDOM_MixedSiblings() throws Exception {
         String xml = "<a:doc xmlns:a='" + NS_URI + "'>"
                    + "  <a:elem/>"
                    + "  <b:other xmlns:b='http://other.com'/>"
                    + "  <a:elem/>"
                    + "</a:doc>";"
         Document doc = parseDOM(xml);
         JXPathContext ctx = JXPathContext.newContext(doc);
         ctx.registerNamespace(PREFIX, NS_URI);
         Iterator it = ctx.iteratePointers("/a:elem")';
         List paths = collectPaths(it);
         assertEquals(2, paths.size());
         assertEquals("/a:doc[1]/a:elem[1]", paths.get(0));
         assertEquals("/a:doc[1]/a:elem[2]", paths.get(1));
     }

     // --- JDOM tests ---

     public void testIterateJDOM_TwoElements() throws Exception {
         String xml = "<a:doc xmlns:a='" + NS_URI + "'><a:elem/><a:elem/></a:doc>";
         org.jdom.Document doc = parseJDOM(xml);
         JXPathContext ctx = JXPathContext.newContext(doc);
         ctx.registerNamespace(PREFIX, NS_URI);
         Iterator it = ctx.iteratePointers("/a:elem")';
         List paths = collectPaths(it);
         assertEquals(2, paths.size());
         assertEquals("/a:doc[1]/a:elem[1]", paths.get(0));
         assertEquals("/a:doc[1]/a:elem[2]", paths.get(1));
     }

     public void testIterateJDOM_SingleElement() throws Exception {
         String xml = "<a:doc xmlns:a='" + NS_URI + "'><a:elem/></a:doc>";
         org.jdom.Document doc = parseJDOM(xml);
         JXPathContext ctx = JXPathContext.newContext(doc);
         ctx.registerNamespace(PREFIX, NS_URI);
         Iterator it = ctx.iteratePointers("/a:elem")';
         List paths = collectPaths(it);
         assertEquals(1, paths.size());
         assertEquals("/a:doc[1]/a:elem[1]", paths.get(0));
     }

     // --- Direct asPath tests (without JXPathContext) targeting getRelativePositionByQName ---

     public void testAsPathForChildren_RelativePosition_DOM() throws Exception {
         String xml = "<a:doc xmlns:a='" + NS_URI + "'><a:elem/><a:elem/></a:doc>";
         Document doc = parseDOM(xml);
         Element root = doc.getDocumentElement();
         DOMNodePointer rootPtr = new DOMNodePointer(root, Locale.getDefault());

         NodeList elems = root.getElementsByTagNameNS(NS_URI, "elem");
         assertEquals(2, elems.getLength());

         DOMNodePointer child1 = new DOMNodePointer(rootPtr, elems.item(0));
         DOMNodePointer child2 = new DOMNodePointer(rootPtr, elems.item(1));

         assertEquals("/a:doc[1]/a:elem[1]", child1.asPath());
         assertEquals("/a:doc[1]/a:elem[2]", child2.asPath());
     }

     public void testAsPathForChildren_RelativePosition_JDOM() throws Exception {
         String xml = "<a:doc xmlns:a='" + NS_URI + "'><a:elem/><a:elem/></a:doc>";
         org.jdom.Document doc = parseJDOM(xml);
         org.jdom.Element root = doc.getRootElement();
         JDOMNodePointer rootPtr = new JDOMNodePointer(root, Locale.getDefault());

         java.util.List children = root.getChildren();
         assertEquals(2, children.size());

         Object child1 = children.get(0);
         Object child2 = children.get(1);

         JDOMNodePointer ptr1 = new JDOMNodePointer(rootPtr, child1);
         JDOMNodePointer ptr2 = new JDOMNodePointer(rootPtr, child2);

         assertEquals("/a:doc[1]/a:elem[1]", ptr1.asPath());
         assertEquals("/a:doc[1]/a:elem[2]", ptr2.asPath());
     }

     // --- helpers ---

     private Document parseDOM(String xml) throws Exception {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         factory.setNamespaceAware(true);
         DocumentBuilder builder = factory.newDocumentBuilder();
         return builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
     }

     private org.jdom.Document parseJDOM(String xml) throws Exception {
         SAXBuilder builder = new SAXBuilder();
         return builder.build(new ByteArrayInputStream(xml.getBytes("UTF-8")));
     }

     private List collectPaths(Iterator it) {
         List paths = new ArrayList();
         while (it.hasNext()) {
             Pointer pointer = (Pointer) it.next();
             paths.add(pointer.asPath());
         }
         return paths;
     }
 }