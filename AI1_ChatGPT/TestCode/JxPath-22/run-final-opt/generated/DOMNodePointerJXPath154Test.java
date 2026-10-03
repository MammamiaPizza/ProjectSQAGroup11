package org.apache.commons.jxpath.ri.model.dom;

import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DOMNodePointerJXPath154Test extends TestCase {

    private static final String B_NAMESPACE = "urn:jxpath154:b";
    private static final String OUTER_NAMESPACE = "urn:jxpath154:outer";
    private static final String XMLNS_NAMESPACE =
            "http://www.w3.org/2000/xmlns/";

    public void testInnerEmptyNamespaceElementUsesItsElementNameInPath()
            throws Exception {
        Document document = newDocument();
        Element foo = createFoo(document, true);
        foo.appendChild(document.createTextNode(" "));
        Element test = addEmptyNamespaceTest(document, foo);

        DOMNodePointer pointer = pointerFor(document, foo, test);

        assertEquals("/b:foo[1]/test[1]", pointer.asPath());
    }

    public void testEmptyNamespaceElementPositionIgnoresNonElementSiblings()
            throws Exception {
        Document document = newDocument();
        Element foo = createFoo(document, true);
        foo.appendChild(document.createTextNode(" "));
        foo.appendChild(document.createComment("before"));
        Element first = addEmptyNamespaceTest(document, foo);
        foo.appendChild(document.createTextNode(" "));
        foo.appendChild(document.createElementNS(null, "other"));
        foo.appendChild(document.createComment("between"));
        Element second = addEmptyNamespaceTest(document, foo);

        assertEquals("/b:foo[1]/test[1]",
                pointerFor(document, foo, first).asPath());
        assertEquals("/b:foo[1]/test[2]",
                pointerFor(document, foo, second).asPath());
    }

    public void testOrdinaryNoNamespaceElementStillUsesQNamePosition()
            throws Exception {
        Document document = newDocument();
        Element foo = createFoo(document, false);
        foo.appendChild(document.createTextNode(" "));
        Element test = document.createElementNS(null, "test");
        foo.appendChild(test);

        assertEquals("/b:foo[1]/test[1]",
                pointerFor(document, foo, test).asPath());
    }

    public void testRemovingRootDomNodeThrowsJXPathException() throws Exception {
        Document document = newDocument();
        DOMNodePointer pointer = new DOMNodePointer(document, Locale.US);

        try {
            pointer.remove();
            fail("Removing a root DOM node must fail");
        }
        catch (JXPathException expected) {
            assertEquals("Cannot remove root DOM node", expected.getMessage());
        }
    }

    private Document newDocument() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().newDocument();
    }

    private Element createFoo(Document document, boolean withOuterDefaultNamespace) {
        Element foo = document.createElementNS(B_NAMESPACE, "b:foo");
        foo.setAttributeNS(XMLNS_NAMESPACE, "xmlns:b", B_NAMESPACE);
        if (withOuterDefaultNamespace) {
            foo.setAttributeNS(XMLNS_NAMESPACE, "xmlns", OUTER_NAMESPACE);
        }
        document.appendChild(foo);
        return foo;
    }

    private Element addEmptyNamespaceTest(Document document, Element parent) {
        Element test = document.createElementNS(null, "test");
        test.setAttributeNS(XMLNS_NAMESPACE, "xmlns", "");
        parent.appendChild(test);
        return test;
    }

    private DOMNodePointer pointerFor(Document document, Element foo,
            Element child) {
        DOMNodePointer documentPointer =
                new DOMNodePointer(document, Locale.US);
        DOMNodePointer fooPointer =
                new DOMNodePointer(documentPointer, foo);
        return new DOMNodePointer(fooPointer, child);
    }
}
