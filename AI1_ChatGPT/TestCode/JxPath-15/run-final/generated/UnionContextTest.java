package org.apache.commons.jxpath.ri.axes;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Tests for {@link UnionContext}.
 */
public class UnionContextTest extends TestCase {

    public void testUnionUsesDocumentOrderRatherThanOperandOrder()
            throws Exception {
        JXPathContext context = JXPathContext.newContext(createVendorDocument());

        Object value = context.getValue(
                "/vendor[1]/contact[4] | /vendor[1]/contact[1]");

        assertEquals("John", value);
    }

    public void testUnionRemovesDuplicatesAndKeepsFirstNodeInDocumentOrder()
            throws Exception {
        JXPathContext context = JXPathContext.newContext(createVendorDocument());

        Object count = context.getValue(
                "count(/vendor[1]/contact[4] | "
                        + "/vendor[1]/contact[1] | "
                        + "/vendor[1]/contact[4])");
        Object firstValue = context.getValue(
                "(/vendor[1]/contact[4] | "
                        + "/vendor[1]/contact[1] | "
                        + "/vendor[1]/contact[4])[1]");

        assertEquals(2.0, ((Number) count).doubleValue(), 0.0);
        assertEquals("John", firstValue);
    }

    public void testGetDocumentOrderIsForwardForMultipleContexts() {
        UnionContext union = new UnionContext(
                new StubContext(-1),
                new EvalContext[] { new StubContext(-1), new StubContext(-1) });

        assertEquals(1, union.getDocumentOrder());
    }

    public void testGetDocumentOrderIsInheritedForSingleContext() {
        UnionContext union = new UnionContext(
                new StubContext(-1),
                new EvalContext[] { new StubContext(1) });

        assertEquals(-1, union.getDocumentOrder());
    }

    private Document createVendorDocument() throws Exception {
        Document document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();

        Element vendor = document.createElement("vendor");
        document.appendChild(vendor);

        addContact(document, vendor, "John");
        addContact(document, vendor, "Jane Doe");
        addContact(document, vendor, "Mary");
        addContact(document, vendor, "Jack Black");

        return document;
    }

    private void addContact(Document document, Element vendor, String name) {
        Element contact = document.createElement("contact");
        contact.appendChild(document.createTextNode(name));
        vendor.appendChild(contact);
    }

    /**
     * Minimal context used only to control inherited document-order values.
     */
    private static final class StubContext extends EvalContext {
        private final int documentOrder;

        private StubContext(int documentOrder) {
            super(null);
            this.documentOrder = documentOrder;
        }

        public NodePointer getCurrentNodePointer() {
            return null;
        }

        public boolean nextNode() {
            return false;
        }

        public int getDocumentOrder() {
            return documentOrder;
        }
    }
}
