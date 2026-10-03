package org.apache.commons.jxpath.ri.model.dom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.jdom.JDOMAttributeIterator;
import org.jdom.Namespace;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Tests attribute-axis behavior for DOM and JDOM models, including namespaced
 * attributes selected through the wildcard attribute test.
 */
public class AttributeIteratorTest extends TestCase {

    private static final String PRICE_NAMESPACE = "urn:test:price";
    private static final String META_NAMESPACE = "urn:test:meta";

    public void testDomWildcardAttributeAxisIncludesQualifiedAndUnqualifiedAttributes()
            throws Exception {
        JXPathContext context = createDomContext();

        List values = iteratorValues(
                context.iterate("/vendor/product/price:amount/@*"));

        assertEquals("Only real attributes should be returned", 2, values.size());
        assertTrue(values.contains("10%"));
        assertTrue(values.contains("20%"));
    }

    public void testJdomWildcardAttributeAxisIncludesQualifiedAndUnqualifiedAttributes()
            throws Exception {
        JXPathContext context = createJdomContext();

        List values = iteratorValues(
                context.iterate("/vendor/product/price:amount/@*"));

        assertEquals("Wildcard attribute selection must include both namespaces",
                2, values.size());
        assertTrue(values.contains("10%"));
        assertTrue(values.contains("20%"));
    }

    public void testDomIteratorFindsNamedNamespacedAttributeAndHandlesPositions()
            throws Exception {
        JXPathContext context = createDomContext();
        NodePointer parent = (NodePointer) context.getPointer(
                "/vendor/product/price:amount");

        DOMAttributeIterator iterator =
                new DOMAttributeIterator(parent, new QName("meta", "qualified"));

        NodePointer pointer = iterator.getNodePointer();
        assertNotNull(pointer);
        assertEquals("10%", ((Attr) pointer.getNode()).getValue());
        assertEquals("Reading the first pointer must not advance the iterator",
                0, iterator.getPosition());

        assertFalse(iterator.setPosition(0));
        assertTrue(iterator.setPosition(1));
        assertEquals("10%",
                ((Attr) iterator.getNodePointer().getNode()).getValue());
        assertFalse(iterator.setPosition(2));

        DOMAttributeIterator missing =
                new DOMAttributeIterator(parent, new QName("meta", "missing"));
        assertNull(missing.getNodePointer());
    }

    public void testJdomIteratorFindsNamedNamespacedAttributeAndHandlesPositions()
            throws Exception {
        JXPathContext context = createJdomContext();
        NodePointer parent = (NodePointer) context.getPointer(
                "/vendor/product/price:amount");

        JDOMAttributeIterator iterator =
                new JDOMAttributeIterator(parent, new QName("meta", "qualified"));

        NodePointer pointer = iterator.getNodePointer();
        assertNotNull(pointer);
        assertEquals("10%",
                ((org.jdom.Attribute) pointer.getNode()).getValue());
        assertEquals("Reading the first pointer must not advance the iterator",
                0, iterator.getPosition());

        assertFalse(iterator.setPosition(0));
        assertTrue(iterator.setPosition(1));
        assertEquals("10%",
                ((org.jdom.Attribute) iterator.getNodePointer().getNode())
                        .getValue());
        assertFalse(iterator.setPosition(2));

        JDOMAttributeIterator missing =
                new JDOMAttributeIterator(parent, new QName("meta", "missing"));
        assertNull(missing.getNodePointer());
    }

    private JXPathContext createDomContext() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        Document document = factory.newDocumentBuilder().newDocument();

        Element vendor = document.createElement("vendor");
        Element product = document.createElement("product");
        Element amount = document.createElementNS(
                PRICE_NAMESPACE, "price:amount");

        amount.setAttributeNS(
                "http://www.w3.org/2000/xmlns/", "xmlns:price", PRICE_NAMESPACE);
        amount.setAttributeNS(
                "http://www.w3.org/2000/xmlns/", "xmlns:meta", META_NAMESPACE);
        amount.setAttributeNS(META_NAMESPACE, "meta:qualified", "10%");
        amount.setAttribute("plain", "20%");

        document.appendChild(vendor);
        vendor.appendChild(product);
        product.appendChild(amount);

        JXPathContext context = JXPathContext.newContext(document);
        context.registerNamespace("price", PRICE_NAMESPACE);
        context.registerNamespace("meta", META_NAMESPACE);
        return context;
    }

    private JXPathContext createJdomContext() {
        Namespace price = Namespace.getNamespace("price", PRICE_NAMESPACE);
        Namespace meta = Namespace.getNamespace("meta", META_NAMESPACE);

        org.jdom.Element vendor = new org.jdom.Element("vendor");
        org.jdom.Element product = new org.jdom.Element("product");
        org.jdom.Element amount = new org.jdom.Element("amount", price);

        amount.addNamespaceDeclaration(meta);
        amount.setAttribute("qualified", "10%", meta);
        amount.setAttribute("plain", "20%");

        vendor.addContent(product);
        product.addContent(amount);

        JXPathContext context = JXPathContext.newContext(
                new org.jdom.Document(vendor));
        context.registerNamespace("price", PRICE_NAMESPACE);
        context.registerNamespace("meta", META_NAMESPACE);
        return context;
    }

    private List iteratorValues(Iterator iterator) {
        List values = new ArrayList();
        while (iterator.hasNext()) {
            values.add(iterator.next());
        }
        return values;
    }
}
