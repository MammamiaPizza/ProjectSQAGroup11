package org.apache.commons.jxpath.ri.model.dom;

 import javax.xml.parsers.DocumentBuilder;
 import javax.xml.parsers.DocumentBuilderFactory;
 import javax.xml.parsers.ParserConfigurationException;

 import junit.framework.TestCase;

 import org.apache.commons.jxpath.JXPathContext;
 import org.apache.commons.jxpath.Pointer;
 import org.apache.commons.jxpath.ri.QName;
 import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
 import org.apache.commons.jxpath.ri.model.NodePointer;
 import org.w3c.dom.Document;
 import org.w3c.dom.Element;
 import org.w3c.dom.Node;

 /**
  * Tests for {@link DOMNodePointer} focusing on namespace‑aware behaviour:
  * prefix resolution for elements whose xmlns:prefix is declared on an
  * ancestor node, default namespace, unknown prefixes, and interaction
  * with the test‑node logic that underlies XPath step matching.
  *
  * Bug JXPATH-97: when a namespace prefix is declared on an ancestor
  * (not on the child itself), the pointer fails to resolve the element
  * in XPath evaluations such as /ElementA/B:ElementB.
  */
 public class DOMNodePointerNamespaceTest extends TestCase {

     private Document doc;

     @Override
     protected void setUp() throws Exception {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         factory.setNamespaceAware(true);
         DocumentBuilder builder = factory.newDocumentBuilder();
         doc = builder.newDocument();
     }

     // ------------------------------------------------------------------
     //  1. getName() with ancestor‑declared prefix
     // ------------------------------------------------------------------
     /**
      * getName must return a QName whose prefix matches the in‑scope
      * namespace prefix, even when the xmlns:prefix is declared on an
      * ancestor rather than on the element itself.
      */
     public void testGetNameWithAncestorDeclaredPrefix() {
         Element parent = doc.createElement("parent");
         parent.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:b",
                               "http://example.com/b");
         // Child is namespace‑aware: created with createElementNS.
         Element child = doc.createElementNS("http://example.com/b", "b:child");
         parent.appendChild(child);
         doc.appendChild(parent);

         DOMNodePointer pointer = new DOMNodePointer(child, null);
         QName qname = pointer.getName();

         assertEquals("prefix", "b", qname.getPrefix());
         assertEquals("local name", "child", qname.getName());
     }

     // ------------------------------------------------------------------
     //  2. getName with non‑namespace‑aware element (bug trigger)
     // ------------------------------------------------------------------
     /**
      * When an element is created without namespace awareness (plain
      * {@code createElement("b:element")}) but the prefix is declared on
      * a parent, {@link DOMNodePointer#getName()} should still expose the
      * prefix as the in‑scope prefix.  The current buggy implementation
      * might return {@code null} for the prefix.
      */
     public void testGetNameForNonNamespaceAwareElementWithAncestorPrefix() {
         Element parent = doc.createElement("parent");
         parent.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:b",
                               "http://example.com/b");
         // Deliberately created without namespace awareness.
         Element child = doc.createElement("b:child");
         parent.appendChild(child);
         doc.appendChild(parent);

         DOMNodePointer pointer = new DOMNodePointer(child, null);
         QName qname = pointer.getName();

         // Bug JXPATH-97: prefix expected "b", but buggy code may give null.
         // The W3C DOM would return null for node.getPrefix() when the element
         // is not namespace‑aware.  We assert the expected corrected behaviour.
         assertEquals("prefix from ancestor", "b", qname.getPrefix());
         assertEquals("local name", "child", qname.getName());
     }

     // ------------------------------------------------------------------
     //  3. getNamespaceURI(prefix) for ancestor‑declared prefix
     // ------------------------------------------------------------------
     /**
      * The instance method {@link DOMNodePointer#getNamespaceURI(String)}
      * must return the correct URI for a prefix declared on an ancestor.
      */
     public void testGetNamespaceURIForAncestorPrefix() {
         Element grandparent = doc.createElement("grandparent");
         grandparent.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:b",
                                    "http://example.com/b");
         Element parent = doc.createElement("parent");
         Element child = doc.createElementNS("http://example.com/b", "b:child");
         grandparent.appendChild(parent);
         parent.appendChild(child);
         doc.appendChild(grandparent);

         DOMNodePointer childPointer = new DOMNodePointer(child, null);
         String ns = childPointer.getNamespaceURI("b");
         assertEquals("http://example.com/b", ns);
         assertNotNull(ns);
     }

     // ------------------------------------------------------------------
     //  4. getNamespaceURI returns null / UNKNOWN_NAMESPACE for unknown prefix
     // ------------------------------------------------------------------
     public void testGetNamespaceURIUnknownPrefixReturnsNull() {
         Element elem = doc.createElement("elem");
         doc.appendChild(elem);
         DOMNodePointer pointer = new DOMNodePointer(elem, null);
         String ns = pointer.getNamespaceURI("nonexistent");
         // According to the implementation, unknown prefix maps to
         // NodePointer.UNKNOWN_NAMESPACE and the method returns null.
         assertNull(ns);
     }

     // ------------------------------------------------------------------
     //  5. testNode (static) with matching namespace and local name
     // ------------------------------------------------------------------
     /**
      * {@link DOMNodePointer#testNode(Node,NodeTest)} must return true
      * when the test supplies the correct namespace URI and local name.
      */
     public void testTestNodeMatchingNSAndLocalName() {
         Element parent = doc.createElement("parent");
         parent.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:b",
                               "http://example.com/b")
         Element child = doc.createElementNS("http://example.com/b", "b:child");
         parent.appendChild(child;)
         doc.appendChild(parent);

         QName testQName = new QName("b", "child");
         NodeNameTest nodeTest = new NodeNameTest(testQName,
                                                 "http://example.com/b");
         assertTrue(DOMNodePointer.testNode(child, nodeTest));
     }

     // ------------------------------------------------------------------
     //  6. testNode returns false for mismatched namespace
     // ------------------------------------------------------------------
     public void testTestNodeMismatchedNamespace() {
         Element parent = doc.createElement("parent");
         parent.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:b",
                               "http://example.com/b");
         Element child = doc.createElementNS("http://example.com/b", "b:child");
         parent.appendChild(child);
         doc.appendChild(parent);

         QName testQName = new QName("b", "child");
         NodeNameTest nodeTest = new NodeNameTest(testQName,
                                                 "http://other.com");
         assertFalse(DOMNodePointer.testNode(child, nodeTest));
     }

     // ------------------------------------------------------------------
     //  7. testNode with wildcard name and matching namespace
     // ------------------------------------------------------------------
     public void testTestNodeWildcardWithMatchingNamespace() {
         Element parent = doc.createElement("parent");
         parent.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:b",
                               "http://example.com/b");
         Elem child = doc.createElementNS("http://example.com/b", "b:child");
         parent.appendChild(child);
         doc.appendChild(parent);

         // Wildcard QName: prefix set, name set to null/empty to indicate wildcard.
         // In JXPath, a NodeNameTest is wildcard when the QName's name is null after
         // stripping prefix. We emulate that by creating a QName with null name.
         QName wildQName = new QName("b", null);
         NodeNameTest nodeTest = new NodeNameTest(wildQName,
                                                 "http://example.com/b");
         assertTrue(DOMNodePointer.testNode(child, nodeTest));
     }

     // ------------------------------------------------------------------
     //  8. namespacePointer returns valid pointer to namespace node
     // ------------------------------------------------------------------
     /**
      * {@link DOMNodePointer#namespacePointer(String)} must return a
      * pointer whose base value is the {@code xmlns:prefix} attribute
      * on the nearest declaring ancestor.
      */
     public void testNamespacePointerForAncestorPrefix() {
         Element parent = doc.createElement("parent");
         parent.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:b",
                               "http://example.com/b");
         Elem child = doc.createElementNS("http://example.com/b", "b:child");
         parent.appendChild(child);
         doc.appendChild(parent);

         DOMNodePointer childPointer = new DOMNodePointer(child, null);
         NodePointer nsPointer = (NodePointer) childPointer.namespacePointer("b");
         assertNotNull("namespace pointer must not be null", nsPointer);
         assertTrue("namespace pointer should be a DOMNodePointer",
                    nsPointer instanceof DOMNodePointer);
         Object base = nsPointer.getBaseValue();
         assertTrue("base should be an Attr node", base instanceof Attr);
         Attr attr = (Attr) base;
         assertEquals("http://www.w3.org/2000/xmlns/", attr.getNamespaceURI());
         assertEquals("b", attr.getLocalName());
         assertEquals("http://example.com/b", attr.getValue());
     }

     // ------------------------------------------------------------------
     //  9. getDefaultNamespaceURI resolves default namespace on ancestor
     // ------------------------------------------------------------------
     public void testDefaultNamespaceURIFromAncestor() {
         Element parent = doc.createElement("parent");
         parent.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns",
                               "http://default.com");
         Element child = doc.createElementNS("http://default.com", "child");
         parent.appendChild(child);
         doc.appendChild(parent);

         DOMNodePointer childPointer = new DOMNodePointer(child, null);
         String defaultNS = childPointer.getDefaultNamespaceURI();
         assertEquals("http://default.com", defaultNS);
     }

     // ------------------------------------------------------------------
     // 10. multiple nested prefix re‑declarations
     // ------------------------------------------------------------------
     public void testMultipleNestedPrefixOverride() {
         Element grandparent = doc.createElement("grandparent");
         grandparent.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:b",
                                    "http://uri1.com");
         Element parent = doc.createElement("parent");
         parent.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:b",
                               "http://uri2.com");
         Element child = doc.createElementNS("http://uri2.com", "b:child");
         grandparent.appendChild(parent);
         parent.appendChild(child);
         doc.appendChild(grandparent);

         DOMNodePointer childPointer = new DOMNodePointer(child, null);
         String ns = childPointer.getNamespaceURI("b");
         assertEquals("inner prefix should override outer",
                      "http://uri2.com", ns);
     }

     // ------------------------------------------------------------------
     // 11. static getNamespaceURI(Node) for element with ancestor prefix
     // ------------------------------------------------------------------
     public void testStaticGetNamespaceURIFindsAncestorPrefix() {
         Element parent = doc.createElement("parent");
         parent.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:b",
                               "http://example.com/b");
         Element child = doc.createElementNS("http://example.com/b", "b:child");
         parent.appendChild(child);
         doc.appendChild(parent);

         String ns = DOMNodePointer.getNamespaceURI(child);
         assertEquals("http://example.com/b", ns);
     }

     // ------------------------------------------------------------------
     // 12. Integration: JXPath evaluation with external namespace mapping
     // ------------------------------------------------------------------
     /**
      * Simulates the failing test {@code ExternalXMLNamespaceTest#testElementDOM}.
      * The XPath {@code /ElementA/B:ElementB} must resolve correctly when the
      * prefix {@code B} is registered externally and declared on ancestor.
      */
     public void testJXPathEvaluationWithExternalNamespace() {
         // Build DOM: <ElementA xmlns:B="http://b.com"><B:ElementB/></ElementA>
         Element elementA = doc.createElement("ElementA");
         elementA.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:B",
                                 "http://b.com");
         Element elementB = doc.createElementNS("http://b.com", "B:ElementB");
         elementA.appendChild(elementB);
         doc.appendChild(elementA);

         JXPathContext context = JXPathContext.newContext(doc);
         // Register the same prefix externally (may be redundant but mirrors
         // the ExternalXMLNamespaceTest pattern).
         context.registerNamespace("B", "http://b.com");

         Pointer pointer = context.getPointer("/ElementA/B:ElementB");
         assertNotNull("Must locate element with externally registered prefix",
                       pointer);
         assertEquals("http://b.com",
                      ((Node) pointer.getNode()).getNamespaceURI());
     }
 }
