package org.apache.commons.jxpath.ri.model.dom;

import java.io.StringReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Tests for {@link DOMNodePointer}.
 */
public class DOMNodePointerTest extends TestCase {

    public DOMNodePointerTest(String name) {
        super(name);
    }

    /**
     * Verifies JXPATH-154. DOM implementations may represent xmlns=""
     * using an empty namespace URI rather than null. In either case, the
     * element must be addressed by its name instead of node().
     */
    public void testAsPathTreatsExplicitEmptyNamespaceAsNoNamespace()
            throws Exception {
        Document document = parse(
                "<b:foo xmlns:b='urn:example:b'>"
                    + "<before/>"
                    + "<test xmlns=''>value</test>"
                + "</b:foo>");

        Element foo = document.getDocumentElement();
        Element actualTest =
                (Element) foo.getElementsByTagName("test").item(0);
        Element test = withEmptyNamespaceURI(actualTest);

        DOMNodePointer documentPointer =
                new DOMNodePointer(document, Locale.US);
        DOMNodePointer fooPointer =
                new DOMNodePointer(documentPointer, foo);
        DOMNodePointer testPointer =
                new DOMNodePointer(fooPointer, test);

        assertEquals("",
                test.getNamespaceURI());
        assertEquals("b:foo[1]/test[1]", testPointer.asPath());
    }

    public void testNamespaceLookupUsesInheritedDeclarationsAndBuiltInPrefixes()
            throws Exception {
        Document document = parse(
                "<root xmlns='urn:default' xmlns:p='urn:prefix'>"
                    + "<p:child><nested/></p:child>"
                + "</root>");

        Element nested = (Element) document.getElementsByTagName("nested")
                .item(0);
        DOMNodePointer pointer = new DOMNodePointer(nested, Locale.US);

        assertEquals("urn:prefix", pointer.getNamespaceURI("p"));
        assertEquals("urn:default", pointer.getNamespaceURI(""));
        assertEquals("urn:default", pointer.getDefaultNamespaceURI());
        assertEquals(DOMNodePointer.XML_NAMESPACE_URI,
                pointer.getNamespaceURI("xml"));
        assertEquals(DOMNodePointer.XMLNS_NAMESPACE_URI,
                pointer.getNamespaceURI("xmlns"));
        assertNull(pointer.getNamespaceURI("unknown"));
    }

    public void testNodeNameAndTypeTests() throws Exception {
        Document document = parse(
                "<p:item xmlns:p='urn:test:p'>text<!--comment--></p:item>");

        Element element = document.getDocumentElement();
        Node text = element.getFirstChild();
        Comment comment = (Comment) text.getNextSibling();

        NodeNameTest matchingName =
                new NodeNameTest(new QName("p", "item"), "urn:test:p");
        NodeNameTest differentName =
                new NodeNameTest(new QName("p", "other"), "urn:test:p");

        assertTrue(DOMNodePointer.testNode(element, null));
        assertTrue(DOMNodePointer.testNode(element, matchingName));
        assertFalse(DOMNodePointer.testNode(element, differentName));
        assertFalse(DOMNodePointer.testNode(text, matchingName));

        assertTrue(DOMNodePointer.testNode(text,
                new NodeTypeTest(Compiler.NODE_TYPE_TEXT)));
        assertTrue(DOMNodePointer.testNode(comment,
                new NodeTypeTest(Compiler.NODE_TYPE_COMMENT)));
        assertTrue(DOMNodePointer.testNode(element,
                new NodeTypeTest(Compiler.NODE_TYPE_NODE)));
        assertFalse(DOMNodePointer.testNode(element,
                new NodeTypeTest(Compiler.NODE_TYPE_TEXT)));
    }

    public void testNamePrefixLocalNameAndBasicPointerProperties()
            throws Exception {
        Document document = parse("<p:item xmlns:p='urn:test:p'/>");
        Element element = document.getDocumentElement();
        DOMNodePointer pointer = new DOMNodePointer(element, Locale.US);

        assertEquals("p", DOMNodePointer.getPrefix(element));
        assertEquals("item", DOMNodePointer.getLocalName(element));
        assertEquals("p", pointer.getName().getPrefix());
        assertEquals("item", pointer.getName().getName());
        assertEquals("urn:test:p", pointer.getNamespaceURI());

        assertSame(element, pointer.getBaseValue());
        assertSame(element, pointer.getImmediateNode());
        assertTrue(pointer.isActual());
        assertFalse(pointer.isCollection());
        assertEquals(1, pointer.getLength());
        assertTrue(pointer.isLeaf());

        element.appendChild(document.createElement("child"));
        assertFalse(pointer.isLeaf());
    }

    public void testGetValueTrimsNormallyButPreservesXmlSpaceAndComments()
            throws Exception {
        Document document = parse(
                "<root>"
                    + "<trimmed>  value  </trimmed>"
                    + "<preserved xml:space='preserve'>  value  </preserved>"
                    + "<!--  comment value  -->"
                + "</root>");

        Element root = document.getDocumentElement();
        Element trimmed = (Element) root.getElementsByTagName("trimmed")
                .item(0);
        Element preserved = (Element) root.getElementsByTagName("preserved")
                .item(0);
        Comment comment = (Comment) root.getLastChild();

        assertEquals("value",
                new DOMNodePointer(trimmed, Locale.US).getValue());
        assertEquals("  value  ",
                new DOMNodePointer(preserved, Locale.US).getValue());
        assertEquals("comment value",
                new DOMNodePointer(comment, Locale.US).getValue());
    }

    public void testSetValueReplacesElementContentsAndRemovesEmptyTextNode()
            throws Exception {
        Document document = parse(
                "<root><holder><old/></holder><source><new>copied</new>"
                    + "</source><text>old text</text></root>");

        Element root = document.getDocumentElement();
        Element holder = (Element) root.getElementsByTagName("holder").item(0);
        Element source = (Element) root.getElementsByTagName("source").item(0);
        Element textElement = (Element) root.getElementsByTagName("text")
                .item(0);
        Node textNode = textElement.getFirstChild();

        DOMNodePointer holderPointer =
                new DOMNodePointer(holder, Locale.US);
        holderPointer.setValue(source);

        assertEquals(1, holder.getChildNodes().getLength());
        assertEquals("new", holder.getFirstChild().getNodeName());
        assertEquals("copied", holder.getFirstChild().getFirstChild()
                .getNodeValue());

        DOMNodePointer textPointer =
                new DOMNodePointer(textNode, Locale.US);
        textPointer.setValue("replacement");
        assertEquals("replacement", textElement.getFirstChild().getNodeValue());

        textPointer.setValue("");
        assertEquals(0, textElement.getChildNodes().getLength());
    }

    public void testCreateAttributeCreatesUnprefixedAttributeAndRejectsUnknownPrefix()
            throws Exception {
        Document document = parse("<element/>");
        Element element = document.getDocumentElement();
        DOMNodePointer pointer = new DOMNodePointer(element, Locale.US);

        NodePointer attributePointer =
                pointer.createAttribute(null, new QName(null, "flag"));
        Attr attribute = (Attr) attributePointer.getBaseValue();

        assertEquals("flag", attribute.getName());
        assertEquals("", attribute.getValue());
        assertSame(attribute, element.getAttributeNode("flag"));

        try {
            pointer.createAttribute(null, new QName("unknown", "flag"));
            fail("An unresolved attribute prefix must be rejected");
        }
        catch (JXPathException expected) {
            assertTrue(expected.getMessage().indexOf("unknown") >= 0);
        }
    }

    public void testRemoveAndRootRemovalFailure() throws Exception {
        Document document = parse("<root><child/></root>");
        Element root = document.getDocumentElement();
        Element child = (Element) root.getFirstChild();

        DOMNodePointer childPointer = new DOMNodePointer(child, Locale.US);
        childPointer.remove();
        assertEquals(0, root.getChildNodes().getLength());

        try {
            new DOMNodePointer(document, Locale.US).remove();
            fail("Removing a DOM document root must fail");
        }
        catch (JXPathException expected) {
            assertTrue(expected.getMessage().indexOf("Cannot remove root") >= 0);
        }
    }

    public void testEqualityAndChildPointerComparison() throws Exception {
        Document document = parse("<root first='1'><one/><two/></root>");
        Element root = document.getDocumentElement();
        Attr attribute = root.getAttributeNode("first");
        Node one = root.getFirstChild();
        Node two = one.getNextSibling();

        DOMNodePointer rootPointer = new DOMNodePointer(root, Locale.US);
        DOMNodePointer sameRootPointer = new DOMNodePointer(root, Locale.US);
        DOMNodePointer attributePointer =
                new DOMNodePointer(rootPointer, attribute);
        DOMNodePointer onePointer = new DOMNodePointer(rootPointer, one);
        DOMNodePointer twoPointer = new DOMNodePointer(rootPointer, two);

        assertTrue(rootPointer.equals(sameRootPointer));
        assertEquals(rootPointer.hashCode(), sameRootPointer.hashCode());
        assertFalse(rootPointer.equals(onePointer));

        assertEquals(0,
                rootPointer.compareChildNodePointers(onePointer, onePointer));
        assertEquals(-1,
                rootPointer.compareChildNodePointers(attributePointer,
                        onePointer));
        assertEquals(1,
                rootPointer.compareChildNodePointers(onePointer,
                        attributePointer));
        assertEquals(-1,
                rootPointer.compareChildNodePointers(onePointer, twoPointer));
        assertEquals(1,
                rootPointer.compareChildNodePointers(twoPointer, onePointer));
    }

    private Element withEmptyNamespaceURI(final Element element) {
        return (Element) Proxy.newProxyInstance(
                DOMNodePointerTest.class.getClassLoader(),
                new Class[] { Element.class },
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method,
                            Object[] arguments) throws Throwable {
                        if ("getNamespaceURI".equals(method.getName())
                                && method.getParameterTypes().length == 0) {
                            return "";
                        }
                        try {
                            return method.invoke(element, arguments);
                        }
                        catch (InvocationTargetException exception) {
                            throw exception.getTargetException();
                        }
                    }
                });
    }

    private Document parse(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().parse(
                new InputSource(new StringReader(xml)));
    }
}
