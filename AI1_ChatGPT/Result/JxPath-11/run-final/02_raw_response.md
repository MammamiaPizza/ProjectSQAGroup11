```java
package org.apache.commons.jxpath.ri.model;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.dom.DOMAttributeIterator;
import org.apache.commons.jxpath.ri.model.jdom.JDOMAttributeIterator;
import org.jdom.Element;
import org.jdom.Namespace;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;

/**
 * Tests namespace-aware attribute selection for the DOM and JDOM model
 * attribute iterators.
 */
public class AttributeIteratorTest extends TestCase {

    private static final String RATE_URI = "urn:test:rate";
    private static final String OTHER_URI = "urn:test:other";

    public void testDOMQualifiedAttributeSelectsMatchingNamespace()
        throws Exception {
        org.w3c.dom.Element element = createDOMElementWithAttributes();
        NodePointer parent = getNodePointer(element);

        DOMAttributeIterator iterator =
            new DOMAttributeIterator(parent, new QName("rate", "discount"));

        assertEquals(0, iterator.getPosition());

        NodePointer pointer = iterator.getNodePointer();

        assertNotNull(pointer);
        assertEquals("10%", ((Attr) pointer.getNode()).getValue());
        assertEquals(0, iterator.getPosition());

        assertTrue(iterator.setPosition(1));
        assertEquals(1, iterator.getPosition());
        assertEquals(
            RATE_URI,
            ((Attr) iterator.getNodePointer().getNode()).getNamespaceURI());

        assertFalse(iterator.setPosition(2));
    }

    public void testDOMWildcardWithoutPrefixReturnsOnlyUnqualifiedAttributes()
        throws Exception {
        org.w3c.dom.Element element = createDOMElementWithAttributes();
        NodePointer parent = getNodePointer(element);

        DOMAttributeIterator iterator =
            new DOMAttributeIterator(parent, new QName(null, "*"));

        NodePointer pointer = iterator.getNodePointer();

        assertNotNull(pointer);
        assertEquals("plain", ((Attr) pointer.getNode()).getName());
        assertEquals("plain-value", ((Attr) pointer.getNode()).getValue());

        assertTrue(iterator.setPosition(1));
        assertFalse(iterator.setPosition(2));
    }

    public void testDOMQualifiedAttributeDoesNotMatchSameLocalNameInOtherNamespace()
        throws Exception {
        org.w3c.dom.Element element = createDOMElementWithAttributes();
        NodePointer parent = getNodePointer(element);

        DOMAttributeIterator iterator =
            new DOMAttributeIterator(parent, new QName("other", "discount"));

        NodePointer pointer = iterator.getNodePointer();

        assertNotNull(pointer);
        assertEquals("20%", ((Attr) pointer.getNode()).getValue());
        assertEquals(
            OTHER_URI,
            ((Attr) pointer.getNode()).getNamespaceURI());
    }

    public void testJDOMQualifiedAttributeSelectsMatchingNamespace() {
        Element element = createJDOMElementWithAttributes();
        NodePointer parent = getNodePointer(element);

        JDOMAttributeIterator iterator =
            new JDOMAttributeIterator(parent, new QName("rate", "discount"));

        assertEquals(0, iterator.getPosition());

        NodePointer pointer = iterator.getNodePointer();

        assertNotNull(pointer);
        assertEquals(
            "10%",
            ((org.jdom.Attribute) pointer.getNode()).getValue());
        assertEquals(0, iterator.getPosition());

        assertTrue(iterator.setPosition(1));
        assertEquals(1, iterator.getPosition());
        assertEquals(
            RATE_URI,
            ((org.jdom.Attribute) iterator.getNodePointer().getNode())
                .getNamespaceURI());

        assertFalse(iterator.setPosition(2));
    }

    public void testJDOMWildcardWithoutPrefixReturnsOnlyUnqualifiedAttributes() {
        Element element = createJDOMElementWithAttributes();
        NodePointer parent = getNodePointer(element);

        JDOMAttributeIterator iterator =
            new JDOMAttributeIterator(parent, new QName(null, "*"));

        NodePointer pointer = iterator.getNodePointer();

        assertNotNull(pointer);
        assertEquals(
            "plain",
            ((org.jdom.Attribute) pointer.getNode()).getName());
        assertEquals(
            "plain-value",
            ((org.jdom.Attribute) pointer.getNode()).getValue());

        assertTrue(iterator.setPosition(1));
        assertFalse(iterator.setPosition(2));
    }

    public void testJDOMUnknownPrefixHasNoMatchingAttributes() {
        Element element = createJDOMElementWithAttributes();
        NodePointer parent = getNodePointer(element);

        JDOMAttributeIterator iterator =
            new JDOMAttributeIterator(parent, new QName("unknown", "discount"));

        assertFalse(iterator.setPosition(1));
        assertNull(iterator.getNodePointer());
    }

    public void testJDOMIteratorWithNonJDOMParentHasNoAttributes()
        throws Exception {
        org.w3c.dom.Element domElement = createDOMElementWithAttributes();
        NodePointer domParent = getNodePointer(domElement);

        JDOMAttributeIterator iterator =
            new JDOMAttributeIterator(domParent, new QName(null, "*"));

        assertFalse(iterator.setPosition(1));
        assertNull(iterator.getNodePointer());
    }

    private org.w3c.dom.Element createDOMElementWithAttributes()
        throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        Document document = factory.newDocumentBuilder().parse(
            new ByteArrayInputStream(
                ("<amount xmlns:rate=\"" + RATE_URI + "\" "
                    + "xmlns:other=\"" + OTHER_URI + "\" "
                    + "plain=\"plain-value\" "
                    + "rate:discount=\"10%\" "
                    + "other:discount=\"20%\"/>").getBytes("UTF-8")));

        return document.getDocumentElement();
    }

    private Element createJDOMElementWithAttributes() {
        Namespace rate = Namespace.getNamespace("rate", RATE_URI);
        Namespace other = Namespace.getNamespace("other", OTHER_URI);

        Element element = new Element("amount");
        element.addNamespaceDeclaration(rate);
        element.addNamespaceDeclaration(other);
        element.setAttribute("plain", "plain-value");
        element.setAttribute("discount", "10%", rate);
        element.setAttribute("discount", "20%", other);

        return element;
    }

    private NodePointer getNodePointer(Object node) {
        JXPathContext context = JXPathContext.newContext(node);
        Pointer pointer = context.getPointer(".");
        return (NodePointer) pointer;
    }
}
```

Test coverage focus:

- `testDOMQualifiedAttributeSelectsMatchingNamespace` verifies that a qualified DOM attribute lookup returns the attribute associated with the requested namespace and not merely an attribute with the same local name.
- `testDOMWildcardWithoutPrefixReturnsOnlyUnqualifiedAttributes` covers wildcard attribute iteration for the no-namespace case and ensures namespace declarations and namespaced attributes are excluded.
- `testDOMQualifiedAttributeDoesNotMatchSameLocalNameInOtherNamespace` distinguishes attributes sharing the same local name but belonging to different namespaces, matching the namespace-related defect scenario.
- `testJDOMQualifiedAttributeSelectsMatchingNamespace` verifies JDOM qualified namespace lookup, including iterator positioning behavior.
- `testJDOMWildcardWithoutPrefixReturnsOnlyUnqualifiedAttributes` covers JDOM wildcard selection for unqualified attributes.
- `testJDOMUnknownPrefixHasNoMatchingAttributes` exercises the JDOM constructor path for an unresolved namespace prefix and verifies that no attribute pointer is available.
- `testJDOMIteratorWithNonJDOMParentHasNoAttributes` covers the JDOM iterator path where the parent node is not a JDOM `Element`, ensuring invalid model input does not expose an attribute pointer.