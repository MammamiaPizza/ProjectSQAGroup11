import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.dom.DOMAttributeIterator;
import org.apache.commons.jxpath.ri.model.jdom.JDOMAttributeIterator;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.w3c.dom.Attr;

public class JXPath97AttributeNamespaceTest extends TestCase {

    private static final String RATE_URI = "urn:test:rate";
    private static final String OTHER_URI = "urn:test:other";

    public void testDomXPathSelectsNamespacedDiscountInsteadOfUnqualifiedDiscount()
            throws Exception {
        JXPathContext context = newDomContext();

        assertEquals("10%", context.getValue(
                "vendor[1]/product[1]/rate:amount[1]/@rate:discount"));
    }

    public void testJdomXPathSelectsNamespacedDiscountInsteadOfUnqualifiedDiscount()
            throws Exception {
        JXPathContext context = newJdomContext();

        assertEquals("10%", context.getValue(
                "vendor[1]/product[1]/rate:amount[1]/@rate:discount"));
    }

    public void testDomAttributeIteratorPositionsNamespacedAttribute() throws Exception {
        JXPathContext context = newDomContext();
        NodePointer amount = (NodePointer) context.getPointer(
                "vendor[1]/product[1]/rate:amount[1]");
        DOMAttributeIterator iterator = new DOMAttributeIterator(
                amount, new QName("rate", "discount"));

        assertEquals(0, iterator.getPosition());
        assertFalse(iterator.setPosition(0));
        assertTrue(iterator.setPosition(1));

        NodePointer attributePointer = iterator.getNodePointer();
        assertNotNull(attributePointer);
        assertEquals(1, iterator.getPosition());
        assertTrue(attributePointer.getNode() instanceof Attr);
        assertEquals("10%", ((Attr) attributePointer.getNode()).getValue());

        assertFalse(iterator.setPosition(2));
    }

    public void testJdomAttributeIteratorPositionsNamespacedAttribute() throws Exception {
        JXPathContext context = newJdomContext();
        NodePointer amount = (NodePointer) context.getPointer(
                "vendor[1]/product[1]/rate:amount[1]");
        JDOMAttributeIterator iterator = new JDOMAttributeIterator(
                amount, new QName("rate", "discount"));

        assertEquals(0, iterator.getPosition());
        assertFalse(iterator.setPosition(0));
        assertTrue(iterator.setPosition(1));

        NodePointer attributePointer = iterator.getNodePointer();
        assertNotNull(attributePointer);
        assertEquals(1, iterator.getPosition());
        assertTrue(attributePointer.getNode() instanceof Attribute);
        assertEquals("10%", ((Attribute) attributePointer.getNode()).getValue());

        assertFalse(iterator.setPosition(2));
    }

    private JXPathContext newDomContext() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        org.w3c.dom.Document document =
                factory.newDocumentBuilder().newDocument();
        org.w3c.dom.Element vendor = document.createElement("vendor");
        vendor.setAttributeNS("http://www.w3.org/2000/xmlns/",
                "xmlns:rate", RATE_URI);
        vendor.setAttributeNS("http://www.w3.org/2000/xmlns/",
                "xmlns:other", OTHER_URI);
        document.appendChild(vendor);

        org.w3c.dom.Element product = document.createElement("product");
        vendor.appendChild(product);

        org.w3c.dom.Element amount =
                document.createElementNS(RATE_URI, "rate:amount");
        amount.setAttribute("discount", "20%");
        amount.setAttributeNS(OTHER_URI, "other:discount", "30%");
        amount.setAttributeNS(RATE_URI, "rate:discount", "10%");
        product.appendChild(amount);

        JXPathContext context = JXPathContext.newContext(document);
        context.registerNamespace("rate", RATE_URI);
        return context;
    }

    private JXPathContext newJdomContext() {
        Namespace rate = Namespace.getNamespace("rate", RATE_URI);
        Namespace other = Namespace.getNamespace("other", OTHER_URI);

        Element vendor = new Element("vendor");
        vendor.addNamespaceDeclaration(rate);
        vendor.addNamespaceDeclaration(other);

        Element product = new Element("product");
        vendor.addContent(product);

        Element amount = new Element("amount", rate);
        amount.setAttribute("discount", "20%");
        amount.setAttribute("discount", "30%", other);
        amount.setAttribute("discount", "10%", rate);
        product.addContent(amount);

        JXPathContext context = JXPathContext.newContext(new Document(vendor));
        context.registerNamespace("rate", RATE_URI);
        return context;
    }
}