package org.apache.commons.jxpath.ri.model;

import java.util.Iterator;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.model.dom.DOMNodePointer;
import org.apache.commons.jxpath.ri.model.jdom.JDOMNodePointer;
import org.jdom.Namespace;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * Regression and behavioral tests for DOMNodePointer and JDOMNodePointer.
 */
public class AliasedNamespacePointerTest extends TestCase {

    private static final String NAMESPACE_URI = "urn:test:aliased-namespace";

    public void testIterateDOMElementsWithDifferentSourcePrefixesUsesDistinctPositions()
            throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        Document document = factory.newDocumentBuilder().newDocument();

        Element root = document.createElementNS(NAMESPACE_URI, "sourceDoc:doc");
        root.setAttributeNS(DOMNodePointer.XMLNS_NAMESPACE_URI,
                "xmlns:sourceDoc", NAMESPACE_URI);
        root.setAttributeNS(DOMNodePointer.XMLNS_NAMESPACE_URI,
                "xmlns:first", NAMESPACE_URI);
        root.setAttributeNS(DOMNodePointer.XMLNS_NAMESPACE_URI,
                "xmlns:second", NAMESPACE_URI);
        document.appendChild(root);

        Element first = document.createElementNS(NAMESPACE_URI, "first:elem");
        Element second = document.createElementNS(NAMESPACE_URI, "second:elem");
        root.appendChild(first);
        root.appendChild(second);

        JXPathContext context = JXPathContext.newContext(document);
        context.registerNamespace("a", NAMESPACE_URI);

        Iterator pointers = context.iteratePointers("/a:doc/a:elem");

        assertTrue("The first namespace-equivalent element must be selected",
                pointers.hasNext());
        Pointer firstPointer = (Pointer) pointers.next();
        assertSame(first, firstPointer.getNode());
        assertEquals("/a:doc[1]/a:elem[1]", firstPointer.asPath());

        assertTrue("The second namespace-equivalent element must be selected",
                pointers.hasNext());
        Pointer secondPointer = (Pointer) pointers.next();
        assertSame(second, secondPointer.getNode());
        assertEquals("/a:doc[1]/a:elem[2]", secondPointer.asPath());

        assertFalse("Only the two matching elements should be returned",
                pointers.hasNext());
    }

    public void testIterateJDOMElementsWithDifferentSourcePrefixesUsesDistinctPositions() {
        Namespace documentNamespace =
                Namespace.getNamespace("sourceDoc", NAMESPACE_URI);
        Namespace firstNamespace = Namespace.getNamespace("first", NAMESPACE_URI);
        Namespace secondNamespace =
                Namespace.getNamespace("second", NAMESPACE_URI);

        org.jdom.Element root = new org.jdom.Element("doc", documentNamespace);
        root.addNamespaceDeclaration(firstNamespace);
        root.addNamespaceDeclaration(secondNamespace);

        org.jdom.Element first = new org.jdom.Element("elem", firstNamespace);
        org.jdom.Element second = new org.jdom.Element("elem", secondNamespace);
        root.addContent(first);
        root.addContent(second);

        org.jdom.Document document = new org.jdom.Document(root);

        JXPathContext context = JXPathContext.newContext(document);
        context.registerNamespace("a", NAMESPACE_URI);

        Iterator pointers = context.iteratePointers("/a:doc/a:elem");

        assertTrue("The first namespace-equivalent element must be selected",
                pointers.hasNext());
        Pointer firstPointer = (Pointer) pointers.next();
        assertSame(first, firstPointer.getNode());
        assertEquals("/a:doc[1]/a:elem[1]", firstPointer.asPath());

        assertTrue("The second namespace-equivalent element must be selected",
                pointers.hasNext());
        Pointer secondPointer = (Pointer) pointers.next();
        assertSame(second, secondPointer.getNode());
        assertEquals("/a:doc[1]/a:elem[2]", secondPointer.asPath());

        assertFalse("Only the two matching elements should be returned",
                pointers.hasNext());
    }

    public void testDOMPointerSetValueReplacesElementContentAndRemovesEmptyText()
            throws Exception {
        Document document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        Element element = document.createElement("element");
        element.appendChild(document.createTextNode("old"));
        document.appendChild(element);

        DOMNodePointer elementPointer =
                new DOMNodePointer(element, Locale.ENGLISH);
        elementPointer.setValue("replacement");

        assertEquals(1, element.getChildNodes().getLength());
        assertEquals("replacement", element.getFirstChild().getNodeValue());
        assertEquals("replacement", elementPointer.getValue());

        Text text = (Text) element.getFirstChild();
        DOMNodePointer textPointer = new DOMNodePointer(text, Locale.ENGLISH);
        textPointer.setValue("");

        assertEquals("An empty value removes a DOM text node",
                0, element.getChildNodes().getLength());
        assertTrue(elementPointer.isLeaf());
    }

    public void testJDOMPointerSetValueReplacesElementContentAndRemovesEmptyText() {
        org.jdom.Element element = new org.jdom.Element("element");
        element.addContent(new org.jdom.Text("old"));

        JDOMNodePointer elementPointer =
                new JDOMNodePointer(element, Locale.ENGLISH);
        elementPointer.setValue("replacement");

        assertEquals(1, element.getContent().size());
        assertEquals("replacement", element.getText());
        assertEquals("replacement", elementPointer.getValue());

        org.jdom.Text text = (org.jdom.Text) element.getContent().get(0);
        JDOMNodePointer textPointer =
                new JDOMNodePointer(text, Locale.ENGLISH);
        textPointer.setValue("");

        assertEquals("An empty value removes a JDOM text node",
                0, element.getContent().size());
        assertTrue(elementPointer.isLeaf());
    }

    public void testRemovingRootNodesIsRejectedForDOMAndJDOM() throws Exception {
        Document domDocument = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        domDocument.appendChild(domDocument.createElement("root"));

        try {
            new DOMNodePointer(domDocument, Locale.ENGLISH).remove();
            fail("Removing a root DOM node must fail");
        }
        catch (JXPathException expected) {
            assertTrue(expected.getMessage().indexOf("root DOM node") >= 0);
        }

        org.jdom.Document jdomDocument =
                new org.jdom.Document(new org.jdom.Element("root"));

        try {
            new JDOMNodePointer(jdomDocument, Locale.ENGLISH).remove();
            fail("Removing a root JDOM node must fail");
        }
        catch (JXPathException expected) {
            assertTrue(expected.getMessage().indexOf("root JDOM node") >= 0);
        }
    }
}
