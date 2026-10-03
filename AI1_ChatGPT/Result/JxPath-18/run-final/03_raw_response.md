The generated test class already uses JUnit 3 APIs and only public JXPath APIs. The reported failures are assertion failures, not compilation/import/API errors: `attribute::node()` must return attributes for the DOM model, as required by JXPATH-115. The named-attribute and no-attribute cases are also valid regression coverage and are retained.

```java
package org.apache.commons.jxpath.ri.axes;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Regression tests for attribute-axis evaluation with DOM-backed contexts.
 *
 * <p>JXPATH-115 requires {@code attribute::node()} to return attributes of
 * namespaced DOM elements.</p>
 */
public class AttributeContextTest extends TestCase {

    private static final String PRICE_NAMESPACE = "urn:test:prices";

    public void testAttributeNodeAxisReturnsAttributesOfNamespacedElements()
            throws Exception {
        JXPathContext context = createContext(
                "<vendor xmlns:price='" + PRICE_NAMESPACE + "'>"
                        + "<product><price:amount discount='10%'/></product>"
                        + "<product><price:amount discount='20%'/></product>"
                        + "</vendor>");

        List values = collectValues(context.iterate(
                "vendor/product/price:amount/attribute::node()"));

        assertEquals(Arrays.asList(new String[] { "10%", "20%" }), values);
    }

    public void testNamedAttributeAxisReturnsMatchingAttributeValues()
            throws Exception {
        JXPathContext context = createContext(
                "<vendor xmlns:price='" + PRICE_NAMESPACE + "'>"
                        + "<product><price:amount discount='10%'/></product>"
                        + "<product><price:amount discount='20%'/></product>"
                        + "</vendor>");

        List values = collectValues(context.iterate(
                "vendor/product/price:amount/attribute::discount"));

        assertEquals(Arrays.asList(new String[] { "10%", "20%" }), values);
    }

    public void testAttributeNodeAxisOnElementWithoutAttributesIsEmpty()
            throws Exception {
        JXPathContext context = createContext(
                "<vendor xmlns:price='" + PRICE_NAMESPACE + "'>"
                        + "<product><price:amount/></product>"
                        + "</vendor>");

        Iterator values = context.iterate(
                "vendor/product/price:amount/attribute::node()");

        assertFalse(values.hasNext());
    }

    private JXPathContext createContext(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        Document document = factory.newDocumentBuilder().parse(
                new InputSource(new StringReader(xml)));

        JXPathContext context = JXPathContext.newContext(document);
        context.registerNamespace("price", PRICE_NAMESPACE);
        return context;
    }

    private List collectValues(Iterator iterator) {
        List values = new ArrayList();
        while (iterator.hasNext()) {
            values.add(iterator.next());
        }
        return values;
    }
}
```

The relevant production defect is that `AttributeContext.nextNode()` rejects a non-`NodeNameTest`. The XPath expression `attribute::node()` uses a node-type test rather than a named-attribute test, so the buggy implementation returns no attributes. The assertions expecting `"10%"` and `"20%"` therefore correctly expose JXPATH-115 and should pass only with the fixed `AttributeContext` behavior.