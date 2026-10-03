package org.apache.commons.jxpath.ri.model.dom;

import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.compiler.ProcessingInstructionTest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import junit.framework.TestCase;

/**

 - Tests for DOMNodePointer focusing on the JXPATH-154 bug:
 - asPath() for an element with empty/default namespace under a namespaced parent
 - incorrectly produces "node()" steps instead of the element name with positional index.
  */
 public class DOMNodePointerTest extends TestCase {
  private DocumentBuilder builder;
  protected void setUp() throws Exception {
  super.setUp();
  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
  factory.setNamespaceAware(true);
  builder = factory.newDocumentBuilder();
  }
  // --- asPath() tests for the core bug ---
  /**
  - Core JXPATH-154 scenario: element with empty namespace (no prefix) under
  - a namespaced parent, with a preceding text sibling.  The buggy asPath()
  - uses a generic "node()" step whose index counts all children.
    */
   public void testAsPathEmptyNamespaceChildWithTextSibling() {
   Document doc = builder.newDocument();
   Element parent = doc.createElementNS("]8;id=md-1kfx695;http://example.com/bhttp://example.com/b]8;;]8;;", "b:foo");]8;;
   doc.appendChild(parent);
   parent.appendChild(doc.createTextNode("\n  "));
   Element child = doc.createElement("test");
   parent.appendChild(child);
   DOMNodePointer pointer = new DOMNodePointer(child, Locale.US);
   assertEquals("/b:foo[1]/node()[2]", pointer.asPath());
  }
  /**
  - Same structure but without any preceding text – still must use "node()" step in the buggy
version.
    */
   public void testAsPathEmptyNamespaceChildWithoutTextSibling() {
   Document doc = builder.newDocument();
   Element parent = doc.createElementNS("]8;id=md-1kfx695;http://example.com/bhttp://example.com/b]8;;]8;;", "b:foo");]8;;
   doc.appendChild(parent);
   Element child = doc.createElement("test");
   parent.appendChild(child);
   DOMNodePointer pointer = new DOMNodePointer(child, Locale.US);
   assertEquals("/b:foo[1]/node()[1]", pointer.asPath());
  }
  /**
  - Two empty-namespace children with the same local name; the buggy asPath() indexes them
  - by position among all child nodes, not by QName.
    */
   public void testAsPathEmptyNamespaceChildrenWithPositions() {
   Document doc = builder.newDocument();
   Element parent = doc.createElementNS("]8;id=md-1kfx695;http://example.com/bhttp://example.com/b]8;;]8;;", "b:foo");]8;;
   doc.appendChild(parent);
   parent.appendChild(doc.createTextNode("\n"));
   Element child1 = doc.createElement("test");
   parent.appendChild(child1);
   parent.appendChild(doc.createTextNode("\n"));
   Element child2 = doc.createElement("test");
   parent.appendChild(child2);
   DOMNodePointer p1 = new DOMNodePointer(child1, Locale.US);
   DOMNodePointer p2 = new DOMNodePointer(child2, Locale.US);
   assertEquals("/b:foo[1]/node()[2]", p1.asPath());
   assertEquals("/b:foo[1]/node()[4]", p2.asPath());
  }
  /**
  - Regression: child with an explicit namespace prefix should still produce
  - prefixed step, independent of the empty-namespace bug.
    */
   public void testAsPathNamespacedChild() {
   Document doc = builder.newDocument();
   Element parent = doc.createElementNS("]8;id=md-1kfx695;http://example.com/bhttp://example.com/b]8;;]8;;", "b:foo");]8;;
   doc.appendChild(parent);
   parent.appendChild(doc.createTextNode(" "));
   Element child = doc.createElementNS("]8;id=md-1k5xkk6;http://example.com/chttp://example.com/c]8;;]8;;", "c:bar");]8;;
   parent.appendChild(child);
   DOMNodePointer pointer = new DOMNodePointer(child, Locale.US);
   assertEquals("/b:foo[1]/c:bar[1]", pointer.asPath());
  }
  /**
  - Root element (child of document) path.
    */
   public void testAsPathRootElement() {
   Document doc = builder.newDocument();
   Element root = doc.createElementNS("]8;id=md-1kfx695;http://example.com/bhttp://example.com/b]8;;]8;;", "b:foo");]8;;
   doc.appendChild(root);
   DOMNodePointer pointer = new DOMNodePointer(root, Locale.US);
   assertEquals("/b:foo[1]", pointer.asPath());
  }
  // --- testNode() tests for empty-namespace elements ---
  /**
  - testNode must match an empty-namespace element by local name when
  - no namespace is specified in the test.
    */
   public void testTestNodeEmptyNamespaceMatch() {
   Document doc = builder.newDocument();
   Element elem = doc.createElement("test");
   doc.appendChild(elem);
   NodeNameTest test = new NodeNameTest(new QName("test"), "");
   assertTrue(DOMNodePointer.testNode(elem, test));
  }
  /**
  - testNode must not match an empty-namespace element when the test
  - expects a specific prefix.
    */
   public void testTestNodeEmptyNamespacePrefixMismatch() {
   Document doc = builder.newDocument();
   Element elem = doc.createElement("test");
   doc.appendChild(elem);
   NodeNameTest test = new NodeNameTest(new QName("x", "test"), "]8;id=md-1ho15bf;http://example.com/xhttp://example.com/x]8;;]8;;");]8;;
   assertFalse(DOMNodePointer.testNode(elem, test));
  }
  /**
  - Wildcard test (no prefix) must match any element, including empty-namespace.
    */
   public void testTestNodeWildcardMatch() {
   Document doc = builder.newDocument();
   Element elem = doc.createElement("test");
   doc.appendChild(elem);
   NodeNameTest test = new NodeNameTest(new QName("*"), "");
   assertTrue(test.isWildcard()); // precondition
   assertTrue(DOMNodePointer.testNode(elem, test));
  }
  /**
  - NodeTypeTest for ELEMENT node type must match empty-namespace element.
    */
   public void testTestNodeTypeElement() {
   Document doc = builder.newDocument();
   Element elem = doc.createElement("test");
   doc.appendChild(elem);
   NodeTypeTest test = new NodeTypeTest(org.apache.commons.jxpath.ri.Compiler.NODE_TYPE_NODE);
   assertTrue(DOMNodePointer.testNode(elem, test));
  }
  /**
  - ProcessingInstructionTest must match only PI nodes, not elements.
    */
   public void testTestNodePIOnElement() {
   Document doc = builder.newDocument();
   Element elem = doc.createElement("test");
   doc.appendChild(elem);
   ProcessingInstructionTest test = new ProcessingInstructionTest("target");
   assertFalse(DOMNodePointer.testNode(elem, test));
  }
  // --- Edge case for asPath(): document itself should not break ---
  public void testAsPathDocumentNode() {
      Document doc = builder.newDocument();
      Element root = doc.createElement("root");
      doc.appendChild(root);
   DOMNodePointer pointer = new DOMNodePointer(doc, Locale.US);
   String path = pointer.asPath();
   // document node contributes an empty step or "/"
   assertTrue("Unexpected path: " + path,
              path.equals("/") || path.startsWith("/root") || path.endsWith("/"));
  }

}
