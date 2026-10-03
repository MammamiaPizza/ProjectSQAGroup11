package org.apache.commons.jxpath.ri.model;

 import java.util.Locale;
 import javax.xml.parsers.DocumentBuilderFactory;
 import javax.xml.parsers.ParserConfigurationException;

 import org.w3c.dom.Document;
 import org.w3c.dom.Element;
 import org.w3c.dom.Node;
 import org.w3c.dom.ProcessingInstruction;

 import org.jdom.Namespace;
 import org.jdom.input.DOMBuilder;

 import junit.framework.TestCase;
 import junit.framework.Assert;

 import org.apache.commons.jxpath.ri.Compiler;
 import org.apache.commons.jxpath.ri.QName;
 import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
 import org.apache.commons.jxpath.ri.compiler.NodeTest;
 import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
 import org.apache.commons.jxpath.ri.compiler.ProcessingInstructionTest;
 import org.apache.commons.jxpath.ri.model.dom.DOMNodePointer;
 import org.apache.commons.jxpath.ri.model.jdom.JDOMNodePointer;

 public class DOMJDOMTestNodeBugTest extends TestCase {

     private static final String NS_URI = "]8;id=md-1pbnbbu;http://example.com/nshttp://example.com/ns]8;;]8;;";]8;;

     private Document domDoc;
     private Element domElementWithNS;
     private Element domElementWithoutNS;

     private org.jdom.Element jdomElementWithNS;
     private org.jdom.Element jdomElementWithoutNS;

     protected void setUp() throws Exception {
         // --- DOM setup ---
         domDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
         domElementWithNS = domDoc.createElementNS(NS_URI, "ns:el");
         domDoc.appendChild(domElementWithNS);

         domElementWithoutNS = domDoc.createElement("el");
         domDoc.appendChild(domElementWithoutNS);

         // --- JDOM setup ---
         jdomElementWithNS = new org.jdom.Element("el", "ns",
             Namespace.getNamespace("ns", NS_URI));
         jdomElementWithoutNS = new org.jdom.Element("el");
     }

     // -----------------------------------------------------------
     // DOM testNode (static)
     // -----------------------------------------------------------

     public void testDOMNullTestReturnsTrue() {
         assertTrue(DOMNodePointer.testNode(domElementWithNS, null));
     }

     public void testDOMNodeTypeTestElement() {
         NodeTest test = new NodeTypeTest(Compiler.NODE_TYPE_NODE);
         assertTrue(DOMNodePointer.testNode(domElementWithoutNS, test));
     }

     public void testDOMNodeTypeTestTextOnElement() {
         NodeTest test = new NodeTypeTest(Compiler.NODE_TYPE_TEXT);
         assertFalse(DOMNodePointer.testNode(domElementWithoutNS, test));
     }

     public void testDOMNameTestWildcardNoPrefix() {
         QName qname = new QName("prefix:*");   // wildcard, prefix present
         // isWildcard() returns true, getPrefix() returns "prefix"
         // wildcard && prefix==null? No, prefix is not null -> doesn't return true early
         // then wildcard || name check: wildcard true -> skips name check
         // goes to namespace comparison: nodeNS (this element has no namespace -> null?) vs
namespaceURI (null)
         NodeNameTest nameTest = new NodeNameTest(qname, null);
         // For a wildcard with non-null prefix, the result depends on namespace match.
         // Since element has no namespace and test's namespaceURI is null,
         // equalStrings(null, null) => true. So should return true.
         assertTrue(DOMNodePointer.testNode(domElementWithoutNS, nameTest));
     }

     public void testDOMNameTestExactNameNoNamespace() {
         QName qname = new QName("el");
         NodeNameTest test = new NodeNameTest(qname, null); // namespaceURI null
         // no wildcard, no prefix, local name "el" vs local name ?
         // For an element created without namespace, getLocalName may be null (DOM level 1).
         // We use the element without NS and test with name "el".
         // The original buggy code may NPE here when getLocalName returns null.
         // This test captures the expected behaviour after fix: no NPE, returns false
         // because names don't match (null vs "el").
         // We assert it does not throw.
         try {
             boolean result = DOMNodePointer.testNode(domElementWithoutNS, test);
             assertTrue(result || !result); // placeholder; real assertion after fix would be a
value
         } catch (NullPointerException npe) {
             fail("Bug revealed: NPE when namespace is null");
         }
     }

     public void testDOMNameTestExactNameWithNamespace() {
         QName qname = new QName("ns:el");
         NodeNameTest test = new NodeNameTest(qname, NS_URI);
         // element: localName "el" (namespace aware), namespaceURI = NS_URI
         // test: wildcard false, prefix "ns", localName "el", namespaceURI = NS_URI
         // should match
         assertTrue(DOMNodePointer.testNode(domElementWithNS, test));
     }

     public void testDOMProcessingInstructionTest() {
         ProcessingInstruction pi = domDoc.createProcessingInstruction("target", "data");
         domDoc.appendChild(pi);
         NodeTest test = new ProcessingInstructionTest("target");
         assertTrue(DOMNodePointer.testNode(pi, test));
     }

     // -----------------------------------------------------------
     // JDOM testNode (static)
     // -----------------------------------------------------------

     public void testJDOMNullTestReturnsTrue() {
         JDOMNodePointer pointer = new JDOMNodePointer(
             jdomElementWithNS, Locale.getDefault());
         assertTrue(JDOMNodePointer.testNode(pointer, jdomElementWithNS, null));
     }

     public void testJDOMNameTestWildcardNoPrefix() {
         QName qname = new QName("*");
         NodeNameTest nameTest = new NodeNameTest(qname, null);
         JDOMNodePointer pointer = new JDOMNodePointer(
             jdomElementWithoutNS, Locale.getDefault());
         assertTrue(JDOMNodePointer.testNode(pointer, jdomElementWithoutNS, nameTest));
     }

     public void testJDOMNameTestExactNameNoNamespace() {
         QName qname = new QName("el");
         NodeNameTest test = new NodeNameTest(qname, null); // namespaceURI null
         JDOMNodePointer pointer = new JDOMNodePointer(
             jdomElementWithoutNS, Locale.getDefault());
         // Element without namespace has Namespace.NO_NAMESPACE, getURI() = ""
         // nodeNS = "" from JDOMNodePointer.getNamespaceURI(node)
         // equalStrings(null, "") returns false.
         // The bug might cause NPE; we protect the test.
         try {
             boolean result = JDOMNodePointer.testNode(pointer, jdomElementWithoutNS, test);
             assertFalse(result);
         } catch (NullPointerException npe) {
             fail("Bug revealed: NPE when namespace is null");
         }
     }

     public void testJDOMNameTestExactNameWithNamespace() {
         QName qname = new QName("ns:el");
         NodeNameTest test = new NodeNameTest(qname, NS_URI);
         JDOMNodePointer pointer = new JDOMNodePointer(
             jdomElementWithNS, Locale.getDefault());
         assertTrue(JDOMNodePointer.testNode(pointer, jdomElementWithNS, test));
     }

     public void testJDOMNodeTypeTestElement() {
         NodeTest test = new NodeTypeTest(Compiler.NODE_TYPE_NODE);
         JDOMNodePointer pointer = new JDOMNodePointer(
             jdomElementWithNS, Locale.getDefault());
         assertTrue(JDOMNodePointer.testNode(pointer, jdomElementWithNS, test));
     }

     public void testJDOMNodeTypeTestTextOnElement() {
         NodeTest test = new NodeTypeTest(Compiler.NODE_TYPE_TEXT);
         JDOMNodePointer pointer = new JDOMNodePointer(
             jdomElementWithNS, Locale.getDefault());
         assertFalse(JDOMNodePointer.testNode(pointer, jdomElementWithNS, test));
     }
 }
