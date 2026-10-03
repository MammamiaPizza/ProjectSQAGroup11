package org.apache.commons.jxpath.ri.axes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.jdom.Document;
import org.jdom.Namespace;
import org.w3c.dom.Element;

public class AttributeContextBug115Test extends TestCase {

    private static final String PRICE_NAMESPACE = "urn:test:price";
    private static final String ATTRIBUTE_AXIS_EXPRESSION =
            "vendor/product/price:amount/attribute::node()";

    public void testDomAttributeNodeAxisReturnsAttributesFromEachAmount()
            throws Exception {
        org.w3c.dom.Document document = createDomDocument();

        assertEquals(Arrays.asList(new String[] { "10%", "20%" }),
                evaluateAttributeValues(document, ATTRIBUTE_AXIS_EXPRESSION));
    }

    public void testJdomAttributeNodeAxisReturnsAttributesFromEachAmount() {
        Document document = createJdomDocument();

        assertEquals(Arrays.asList(new String[] { "10%", "20%" }),
                evaluateAttributeValues(document, ATTRIBUTE_AXIS_EXPRESSION));
    }

    public void testDomAttributeNodeAxisCanBeEvaluatedAgainAfterTraversal()
            throws Exception {
        org.w3c.dom.Document document = createDomDocument();
        List expected = Arrays.asList(new String[] { "10%", "20%" });

        assertEquals(expected,
                evaluateAttributeValues(document, ATTRIBUTE_AXIS_EXPRESSION));
        assertEquals(expected,
                evaluateAttributeValues(document, ATTRIBUTE_AXIS_EXPRESSION));
    }

    public void testNamedAttributeAxisStillSelectsTheMatchingAttributes()
            throws Exception {
        org.w3c.dom.Document document = createDomDocument();

        assertEquals(Arrays.asList(new String[] { "10%", "20%" }),
                evaluateAttributeValues(document,
                        "vendor/product/price:amount/attribute::discount"));
    }

    private List evaluateAttributeValues(Object model, String expression) {
        JXPathContext context = JXPathContext.newContext(model);
        context.registerNamespace("price", PRICE_NAMESPACE);

        List values = new ArrayList();
        Iterator iterator = context.iterate(expression);
        while (iterator.hasNext()) {
            values.add(iterator.next());
        }
        return values;
    }

    private org.w3c.dom.Document createDomDocument() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        org.w3c.dom.Document document =
                factory.newDocumentBuilder().newDocument();

        Element vendor = document.createElement("vendor");
        document.appendChild(vendor);

        Element firstProduct = document.createElement("product");
        firstProduct.appendChild(createDomAmount(document, "10%"));
        vendor.appendChild(firstProduct);

        Element secondProduct = document.createElement("product");
        secondProduct.appendChild(createDomAmount(document, "20%"));
        vendor.appendChild(secondProduct);

        return document;
    }

    private Element createDomAmount(org.w3c.dom.Document document,
            String discount) {
        Element amount = document.createElementNS(PRICE_NAMESPACE,
                "price:amount");
        amount.setAttribute("discount", discount);
        return amount;
    }

    private Document createJdomDocument() {
        Namespace price = Namespace.getNamespace("price", PRICE_NAMESPACE);
        org.jdom.Element vendor = new org.jdom.Element("vendor");

        org.jdom.Element firstProduct = new org.jdom.Element("product");
        firstProduct.addContent(createJdomAmount(price, "10%"));
        vendor.addContent(firstProduct);

        org.jdom.Element secondProduct = new org.jdom.Element("product");
        secondProduct.addContent(createJdomAmount(price, "20%"));
        vendor.addContent(secondProduct);

        return new Document(vendor);
    }

    private org.jdom.Element createJdomAmount(Namespace price,
            String discount) {
        org.jdom.Element amount = new org.jdom.Element("amount", price);
        amount.setAttribute("discount", discount);
        return amount;
    }
}
