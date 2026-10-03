import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.dom.DOMAttributeIterator;
import org.apache.commons.jxpath.ri.model.jdom.JDOMAttributeIterator;
import org.jdom.Element;
import org.jdom.Namespace;
import org.w3c.dom.Document;

public class JXPathAttributeIteratorTest extends TestCase {

    public void testDOMWildcardAttributeAxisReturnsAllAttributes() throws Exception {
        JXPathContext context = createDOMContext();

        Iterator values =
                context.getValueIterator("vendor/product/price:amount/@*");

        assertTrue(values.hasNext());
        assertEquals("10%", values.next());
        assertTrue(values.hasNext());
        assertEquals("20%", values.next());
        assertFalse(values.hasNext());
    }

    public void testJDOMWildcardAttributeAxisReturnsAllAttributes() {
        JXPathContext context = createJDOMContext();

        Iterator values =
                context.getValueIterator("vendor/product/price:amount/@*");

        assertTrue(values.hasNext());
        assertEquals("10%", values.next());
        assertTrue(values.hasNext());
        assertEquals("20%", values.next());
        assertFalse(values.hasNext());
    }

    public void testDOMAttributeIteratorVisitsFirstAndSecondWildcardAttributes()
            throws Exception {
        JXPathContext context = createDOMContext();
        NodePointer parent = (NodePointer)
                context.getPointer("vendor/product/price:amount");
        DOMAttributeIterator iterator =
                new DOMAttributeIterator(parent, new QName(null, "*"));

        assertFalse(iterator.setPosition(0));

        assertTrue(iterator.setPosition(1));
        assertEquals(1, iterator.getPosition());
        assertEquals("10%", iterator.getNodePointer().getValue());

        assertTrue(iterator.setPosition(2));
        assertEquals(2, iterator.getPosition());
        assertEquals("20%", iterator.getNodePointer().getValue());

        assertFalse(iterator.setPosition(3));
    }

    public void testJDOMAttributeIteratorVisitsFirstAndSecondWildcardAttributes() {
        JXPathContext context = createJDOMContext();
        NodePointer parent = (NodePointer)
                context.getPointer("vendor/product/price:amount");
        JDOMAttributeIterator iterator =
                new JDOMAttributeIterator(parent, new QName(null, "*"));

        assertFalse(iterator.setPosition(0));

        assertTrue(iterator.setPosition(1));
        assertEquals(1, iterator.getPosition());
        assertEquals("10%", iterator.getNodePointer().getValue());

        assertTrue(iterator.setPosition(2));
        assertEquals(2, iterator.getPosition());
        assertEquals("20%", iterator.getNodePointer().getValue());

        assertFalse(iterator.setPosition(3));
    }

    private JXPathContext createDOMContext() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        Document document = factory.newDocumentBuilder().newDocument();
        org.w3c.dom.Element vendor = document.createElement("vendor");
        vendor.setAttributeNS("http://www.w3.org/2000/xmlns/",
                "xmlns:price", "urn:test:price");
        document.appendChild(vendor);

        org.w3c.dom.Element product = document.createElement("product");
        vendor.appendChild(product);

        org.w3c.dom.Element amount =
                document.createElementNS("urn:test:price", "price:amount");
        amount.setAttribute("first", "10%");
        amount.setAttribute("second", "20%");
        product.appendChild(amount);

        JXPathContext context = JXPathContext.newContext(document);
        context.registerNamespace("price", "urn:test:price");
        return context;
    }

    private JXPathContext createJDOMContext() {
        Namespace price = Namespace.getNamespace("price", "urn:test:price");

        Element vendor = new Element("vendor");
        vendor.addNamespaceDeclaration(price);

        Element product = new Element("product");
        vendor.addContent(product);

        Element amount = new Element("amount", price);
        amount.setAttribute("first", "10%");
        amount.setAttribute("second", "20%");
        product.addContent(amount);

        JXPathContext context =
                JXPathContext.newContext(new org.jdom.Document(vendor));
        context.registerNamespace("price", "urn:test:price");
        return context;
    }
}
