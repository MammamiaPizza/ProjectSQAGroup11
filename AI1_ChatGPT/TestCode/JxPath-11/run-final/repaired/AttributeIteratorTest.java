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
 * Tests namespace-aware attribute selection for DOM and JDOM attribute
 * iterators.
 */
public class AttributeIteratorTest extends TestCase {

    private static final String RATE_URI = "urn:test:rate";
    private static final String OTHER_URI = "urn:test:other";

    public void testDOMQualifiedAttributeSelectsMatchingNamespace()
        throws Exception {
        NodePointer parent = getDOMAmountPointer();

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
        NodePointer parent = getDOMAmountPointer();

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
        NodePointer parent = getDOMAmountPointer();

        DOMAttributeIterator iterator =
            new DOMAttributeIterator(parent, new QName("other", "discount"));

        NodePointer pointer = iterator.getNodePointer();

        assertNotNull(pointer);
        assertEquals("20%", ((Attr) pointer.getNode()).getValue());
        assertEquals(
            OTHER_URI,
            ((Attr) pointer.getNode()).getNamespaceURI());
    }

    public void testJDOMQualifiedAttributeSelectsMatchingNamespace()
        throws Exception {
        NodePointer parent = getJDOMAmountPointer();

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

    public void testJDOMWildcardWithoutPrefixReturnsOnlyUnqualifiedAttributes()
        throws Exception {
        NodePointer parent = getJDOMAmountPointer();

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

    public void testJDOMUnknownPrefixHasNoMatchingAttributes()
        throws Exception {
        NodePointer parent = getJDOMAmountPointer();

        JDOMAttributeIterator iterator =
            new JDOMAttributeIterator(parent, new QName("unknown", "discount"));

        assertFalse(iterator.setPosition(1));
        assertNull(iterator.getNodePointer());
    }

    public void testJDOMIteratorWithNonJDOMParentHasNoAttributes()
        throws Exception {
        NodePointer domParent = getDOMAmountPointer();

        JDOMAttributeIterator iterator =
            new JDOMAttributeIterator(domParent, new QName(null, "*"));

        assertFalse(iterator.setPosition(1));
        assertNull(iterator.getNodePointer());
    }

    private NodePointer getDOMAmountPointer() throws Exception {
        JXPathContext context = JXPathContext.newContext(createDOMDocument());
        context.registerNamespace("rate", RATE_URI);
        context.registerNamespace("other", OTHER_URI);

        Pointer pointer = context.getPointer(
            "vendor[1]/product[1]/rate:amount[1]");
        return (NodePointer) pointer;
    }

    private NodePointer getJDOMAmountPointer() throws Exception {
        JXPathContext context = JXPathContext.newContext(createJDOMDocument());
        context.registerNamespace("rate", RATE_URI);
        context.registerNamespace("other", OTHER_URI);

        Pointer pointer = context.getPointer(
            "vendor[1]/product[1]/rate:amount[1]");
        return (NodePointer) pointer;
    }

    private Document createDOMDocument() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        String xml =
            "<vendor xmlns:rate=\"" + RATE_URI + "\" "
                + "xmlns:other=\"" + OTHER_URI + "\">"
                + "<product>"
                + "<rate:amount plain=\"plain-value\" "
                + "rate:discount=\"10%\" "
                + "other:discount=\"20%\"/>"
                + "</product>"
                + "</vendor>";

        return factory.newDocumentBuilder().parse(
            new ByteArrayInputStream(xml.getBytes("UTF-8")));
    }

    private org.jdom.Document createJDOMDocument() {
        Namespace rate = Namespace.getNamespace("rate", RATE_URI);
        Namespace other = Namespace.getNamespace("other", OTHER_URI);

        Element vendor = new Element("vendor");
        vendor.addNamespaceDeclaration(rate);
        vendor.addNamespaceDeclaration(other);

        Element product = new Element("product");
        Element amount = new Element("amount", rate);
        amount.addNamespaceDeclaration(other);
        amount.setAttribute("plain", "plain-value");
        amount.setAttribute("discount", "10%", rate);
        amount.setAttribute("discount", "20%", other);

        product.addContent(amount);
        vendor.addContent(product);

        return new org.jdom.Document(vendor);
    }
}
