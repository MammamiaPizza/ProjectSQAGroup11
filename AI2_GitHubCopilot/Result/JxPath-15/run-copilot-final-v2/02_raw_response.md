package org.apache.commons.jxpath.ri.axes;

 import java.util.Iterator;
 import java.util.NoSuchElementException;

 import javax.xml.parsers.DocumentBuilder;
 import javax.xml.parsers.DocumentBuilderFactory;

 import junit.framework.TestCase;

 import org.apache.commons.jxpath.JXPathContext;
 import org.apache.commons.jxpath.Pointer;
 import org.w3c.dom.Document;
 import org.w3c.dom.Element;

 /**
  * Verifies that UnionContext returns merged nodes in document order, not in the order
  * of the contexts array (JXPATH-100).
  */
 public class UnionContextBug15Test extends TestCase {

     private Document doc;
     private JXPathContext ctx;

     public void setUp() throws Exception {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = factory.newDocumentBuilder();
         doc = builder.newDocument();
         Element root = doc.createElement("vendor");
         doc.appendChild(root);

         Element c1 = doc.createElement("contact");
         c1.setTextContent("John");
         root.appendChild(c1);

         Element c2 = doc.createElement("contact");
         c2.setTextContent("Jack Black");
         root.appendChild(c2);

         Element c3 = doc.createElement("contact");
         c3.setTextContent("Bob");
         root.appendChild(c3);

         Element c4 = doc.createElement("contact");
         c4.setTextContent("Alice");
         root.appendChild(c4);

         ctx = JXPathContext.newContext(doc);
     }

     // ---- Document-order tests -------------------------------------------------------

     public void testUnionDocumentOrderReversedExpression() {
         // /vendor[1]/contact[4] | /vendor[1]/contact[1] -> contact[1] must come first.
         Iterator it = ctx.iterate("//contact[4] | //contact[1]");
         assertTrue(it.hasNext());
         String first = ((Pointer) it.next()).getValue().toString();
         assertEquals("John", first);
     }

     public void testUnionDocumentOrderNormalExpression() {
         Iterator it = ctx.iterate("//contact[1] | //contact[2]");
         assertTrue(it.hasNext());
         assertEquals("John", ((Pointer) it.next()).getValue().toString());
         assertEquals("Jack Black", ((Pointer) it.next()).getValue().toString());
     }

     public void testUnionThreeNodesReversed() {
         Iterator it = ctx.iterate("//contact[3] | //contact[1] | //contact[2]");
         assertTrue(it.hasNext());
         assertEquals("John", ((Pointer) it.next()).getValue().toString());
         assertEquals("Jack Black", ((Pointer) it.next()).getValue().toString());
         assertEquals("Bob", ((Pointer) it.next()).getValue().toString());
         assertFalse(it.hasNext());
     }

     public void testUnionPointerReturnsFirstInDocumentOrder() {
         // getPointer should also respect document order.
         Pointer ptr = ctx.getPointer("//contact[4] | //contact[1]");
         assertNotNull(ptr);
         assertEquals("John", ptr.getValue().toString());
     }

     // ---- Overlapping / duplicate nodes ----------------------------------------------

     public void testUnionExactDuplicateSelection() {
         // Same node selected twice must appear only once.
         Iterator it = ctx.iterate("//contact[1] | //contact[1]");
         assertTrue(it.hasNext());
         assertEquals("John", ((Pointer) it.next()).getValue().toString());
         assertFalse(it.hasNext());
     }

     public void testUnionOverlappingWithReversed() {
         Iterator it = ctx.iterate("//contact[2] | //contact[1] | //contact[1]");
         assertTrue(it.hasNext());
         assertEquals("John", ((Pointer) it.next()).getValue().toString());
         assertEquals("Jack Black", ((Pointer) it.next()).getValue().toString());
         assertFalse(it.hasNext());
     }

     // ---- Empty / single-context branches --------------------------------------------

     public void testUnionWithEmptyBranch() {
         // Non-existent branches must be ignored.
         Iterator it = ctx.iterate("//contact[99] | //contact[2] | //contact[1]");
         assertTrue(it.hasNext());
         assertEquals("John", ((Pointer) it.next()).getValue().toString());
         assertEquals("Jack Black", ((Pointer) it.next()).getValue().toString());
         assertFalse(it.hasNext());
     }

     public void testUnionSingleContext() {
         Iterator it = ctx.iterate("//contact[3]");
         assertTrue(it.hasNext());
         assertEquals("Bob", ((Pointer) it.next()).getValue().toString());
         assertFalse(it.hasNext());
     }

     public void testUnionNoResults() {
         Iterator it = ctx.iterate("//nonexistent | //fake");
         assertFalse(it.hasNext());
     }

     // ---- Boundary behaviour ---------------------------------------------------------

     public void testUnionIterationExhaustion() {
         Iterator it = ctx.iterate("//contact[1] | //contact[2]");
         it.next();
         it.next();
         assertFalse(it.hasNext());
         try {
             it.next();
             fail("Expected NoSuchElementException");
         } catch (NoSuchElementException e) {
             // expected
         }
     }

     public void testUnionPointerSingle() {
         Pointer ptr = ctx.getPointer("//contact[2]");
         assertNotNull(ptr);
         assertEquals("Jack Black", ptr.getValue().toString());
     }
 }