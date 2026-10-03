### Corrections made

* `DOMNodePointer.testNode(item.getFirstChild(), ...)` passed `null` because `item` had no child nodes, causing a `NullPointerException`. A text node is now added and used for the non-element type test.
* A DOM document element is removable; only the `Document` itself has no parent and must reject `remove()`. The removal assertion now uses a `DOMNodePointer` for the `Document`.
* Exact `asPath()` strings are namespace-resolver dependent. In particular, JDOM may use `node()[n]` when an unqualified element does not match the resolver’s default namespace representation. The tests retain validation of sibling and text positions without assuming one lexical node-test representation.
* All tests remain independent and use JUnit 3.8.1 APIs only.

```java
package org.apache.commons.jxpath.ri.model;

import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.model.dom.DOMNodePointer;
import org.apache.commons.jxpath.ri.model.jdom.JDOMNodePointer;
import org.jdom.Attribute;
import org.jdom.Namespace;
import org.jdom.Text;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Tests DOM and JDOM node pointers, including retrieval of the actual
 * underlying node through JXPath pointers.
 */
public class NodePointerTest extends TestCase {

    private Document newDOMDocument() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().newDocument();
    }

    public void testDOMPointerGetNodeFromJXPathContext() throws Exception {
        Document document = newDOMDocument();
        Element root = document.createElement("root");
        Element first = document.createElement("item");
        Element second = document.createElement("item");

        document.appendChild(root);
        root.appendChild(first);
        root.appendChild(second);

        JXPathContext context = JXPathContext.newContext(document);
        Pointer pointer = context.getPointer("/root/item[2]");

        assertSame(second, pointer.getNode());
    }

    public void testJDOMPointerGetNodeFromJXPathContext() {
        org.jdom.Element root = new org.jdom.Element("root");
        org.jdom.Element first = new org.jdom.Element("item");
        org.jdom.Element second = new org.jdom.Element("item");

        root.addContent(first);
        root.addContent(second);

        org.jdom.Document document = new org.jdom.Document(root);
        JXPathContext context = JXPathContext.newContext(document);
        Pointer pointer = context.getPointer("/root/item[2]");

        assertSame(second, pointer.getNode());
    }

    public void testDOMNodeNameNamespaceAndTypeTests() throws Exception {
        Document document = newDOMDocument();
        Element root = document.createElement("root");
        Element item = document.createElementNS("urn:test", "p:item");
        Node text = document.createTextNode("value");

        item.setAttributeNS(DOMNodePointer.XMLNS_NAMESPACE_URI,
                "xmlns:p", "urn:test");
        item.appendChild(text);
        root.appendChild(item);
        document.appendChild(root);

        NodeNameTest matchingName =
                new NodeNameTest(new QName("p", "item"), "urn:test");
        NodeNameTest wrongName =
                new NodeNameTest(new QName("p", "other"), "urn:test");

        assertTrue(DOMNodePointer.testNode(item, null));
        assertTrue(DOMNodePointer.testNode(item, matchingName));
        assertFalse(DOMNodePointer.testNode(item, wrongName));
        assertTrue(DOMNodePointer.testNode(item,
                new NodeTypeTest(Compiler.NODE_TYPE_NODE)));
        assertFalse(DOMNodePointer.testNode(text,
                new NodeTypeTest(Compiler.NODE_TYPE_NODE)));
        assertEquals("p", DOMNodePointer.getPrefix(item));
        assertEquals("item", DOMNodePointer.getLocalName(item));
        assertEquals("urn:test", DOMNodePointer.getNamespaceURI(item));
    }

    public void testJDOMNodeNameNamespaceAndTypeTests() {
        Namespace namespace = Namespace.getNamespace("p", "urn:test");
        org.jdom.Element item = new org.jdom.Element("item", namespace);
        Text text = new Text("value");

        NodeNameTest matchingName =
                new NodeNameTest(new QName("p", "item"), "urn:test");
        NodeNameTest wrongName =
                new NodeNameTest(new QName("p", "other"), "urn:test");

        assertTrue(JDOMNodePointer.testNode(null, item, null));
        assertTrue(JDOMNodePointer.testNode(null, item, matchingName));
        assertFalse(JDOMNodePointer.testNode(null, item, wrongName));
        assertTrue(JDOMNodePointer.testNode(null, item,
                new NodeTypeTest(Compiler.NODE_TYPE_NODE)));
        assertTrue(JDOMNodePointer.testNode(null, text,
                new NodeTypeTest(Compiler.NODE_TYPE_TEXT)));
        assertEquals("p", JDOMNodePointer.getPrefix(item));
        assertEquals("item", JDOMNodePointer.getLocalName(item));
    }

    public void testDOMPointerNamespaceValueAttributeAndRemovalBehavior()
            throws Exception {
        Document document = newDOMDocument();
        Element root = document.createElement("root");
        Element child = document.createElement("child");

        root.setAttribute("xmlns", "urn:default");
        root.setAttribute("xmlns:p", "urn:prefix");
        child.appendChild(document.createTextNode(" old value "));
        root.appendChild(child);
        document.appendChild(root);

        DOMNodePointer documentPointer =
                new DOMNodePointer(document, Locale.US);
        DOMNodePointer rootPointer =
                new DOMNodePointer(documentPointer, root);
        DOMNodePointer childPointer =
                new DOMNodePointer(rootPointer, child);

        assertEquals("urn:default", childPointer.getNamespaceURI(""));
        assertEquals("urn:prefix", childPointer.getNamespaceURI("p"));
        assertEquals(DOMNodePointer.XML_NAMESPACE_URI,
                childPointer.getNamespaceURI("xml"));
        assertNull(childPointer.getNamespaceURI("unknown"));
        assertEquals("old value", childPointer.getValue());
        assertFalse(childPointer.isLeaf());

        childPointer.setValue("replacement");
        assertEquals("replacement", childPointer.getValue());
        assertEquals(1, child.getChildNodes().getLength());

        NodePointer attributePointer =
                rootPointer.createAttribute(null, new QName(null, "flag"));
        assertTrue(root.hasAttribute("flag"));
        assertSame(root.getAttributeNode("flag"),
                attributePointer.getBaseValue());

        childPointer.remove();
        assertNull(child.getParentNode());

        try {
            documentPointer.remove();
            fail("Removing the root DOM document must fail");
        }
        catch (JXPathException expected) {
            assertTrue(expected.getMessage().indexOf("Cannot remove root") >= 0);
        }
    }

    public void testDOMTextSetValueAndPathPositions() throws Exception {
        Document document = newDOMDocument();
        Element root = document.createElement("root");
        Element first = document.createElement("item");
        Element second = document.createElement("item");
        Node firstText = document.createTextNode("one");
        Node secondText = document.createTextNode("two");

        first.appendChild(firstText);
        second.appendChild(secondText);
        root.appendChild(first);
        root.appendChild(second);
        document.appendChild(root);

        DOMNodePointer documentPointer =
                new DOMNodePointer(document, Locale.US);
        DOMNodePointer rootPointer =
                new DOMNodePointer(documentPointer, root);
        DOMNodePointer secondPointer =
                new DOMNodePointer(rootPointer, second);
        DOMNodePointer textPointer =
                new DOMNodePointer(secondPointer, secondText);

        assertTrue(secondPointer.asPath().endsWith("[2]"));
        assertTrue(textPointer.asPath().endsWith("/text()[1]"));

        textPointer.setValue("changed");
        assertEquals("changed", secondText.getNodeValue());

        textPointer.setValue("");
        assertNull(secondText.getParentNode());
        assertTrue(secondPointer.isLeaf());
    }

    public void testJDOMPointerNamespaceValueAttributeAndRemovalBehavior() {
        Namespace prefixNamespace = Namespace.getNamespace("p", "urn:prefix");
        org.jdom.Element root = new org.jdom.Element("root");
        org.jdom.Element child = new org.jdom.Element("child");

        root.addNamespaceDeclaration(prefixNamespace);
        child.addContent(new Text(" old value "));
        root.addContent(child);

        org.jdom.Document document = new org.jdom.Document(root);

        JDOMNodePointer rootPointer =
                new JDOMNodePointer(root, Locale.US);
        JDOMNodePointer childPointer =
                new JDOMNodePointer(rootPointer, child);

        assertEquals("urn:prefix", rootPointer.getNamespaceURI("p"));
        assertNull(rootPointer.getNamespaceURI("unknown"));
        assertEquals("old value", childPointer.getValue());
        assertFalse(childPointer.isLeaf());

        childPointer.setValue("replacement");
        assertEquals("replacement", childPointer.getValue());
        assertEquals(1, child.getContent().size());

        NodePointer attributePointer =
                rootPointer.createAttribute(null, new QName(null, "flag"));
        Attribute attribute = root.getAttribute("flag");

        assertNotNull(attribute);
        assertSame(attribute, attributePointer.getBaseValue());

        childPointer.remove();
        assertNull(child.getParent());

        JDOMNodePointer documentPointer =
                new JDOMNodePointer(document, Locale.US);
        try {
            documentPointer.remove();
            fail("Removing the root JDOM document must fail");
        }
        catch (JXPathException expected) {
            assertTrue(expected.getMessage().indexOf("Cannot remove root") >= 0);
        }
    }

    public void testJDOMTextSetValueAndPathPositions() {
        org.jdom.Element root = new org.jdom.Element("root");
        org.jdom.Element first = new org.jdom.Element("item");
        org.jdom.Element second = new org.jdom.Element("item");
        Text firstText = new Text("one");
        Text secondText = new Text("two");

        first.addContent(firstText);
        second.addContent(secondText);
        root.addContent(first);
        root.addContent(second);

        org.jdom.Document document = new org.jdom.Document(root);

        JDOMNodePointer documentPointer =
                new JDOMNodePointer(document, Locale.US);
        JDOMNodePointer rootPointer =
                new JDOMNodePointer(documentPointer, root);
        JDOMNodePointer secondPointer =
                new JDOMNodePointer(rootPointer, second);
        JDOMNodePointer textPointer =
                new JDOMNodePointer(secondPointer, secondText);

        assertTrue(secondPointer.asPath().endsWith("[2]"));
        assertTrue(textPointer.asPath().endsWith("/text()[1]"));

        textPointer.setValue("changed");
        assertEquals("changed", secondText.getText());

        textPointer.setValue("");
        assertNull(secondText.getParent());
        assertTrue(secondPointer.isLeaf());
    }
}
```