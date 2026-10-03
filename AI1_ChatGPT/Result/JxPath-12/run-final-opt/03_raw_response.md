package org.apache.commons.jxpath.ri.model.dom;

import java.io.ByteArrayInputStream;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathNotFoundException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DOMNodePointerNamespaceTest extends TestCase {

    public void testXPathFindsPrefixedChildInNamespaceAwareDom() throws Exception {
        Document document = parse(
                "<ElementA xmlns:B='urn:external'>"
                        + "<B:ElementB>found</B:ElementB>"
                        + "</ElementA>",
                true);

        JXPathContext context = JXPathContext.newContext(document);
        context.registerNamespace("B", "urn:external");

        try {
            context.getValue("/ElementA/B:ElementB");
            fail("Expected JXPathNotFoundException");
        }
        catch (JXPathNotFoundException expected) {
        }
    }

    public void testXPathFindsPrefixedChildWhenDomDoesNotProvideNamespaceUri()
            throws Exception {
        Document document = parse(
                "<ElementA xmlns:B='urn:external'>"
                        + "<B:ElementB>found</B:ElementB>"
                        + "</ElementA>",
                false);

        JXPathContext context = JXPathContext.newContext(document);
        context.registerNamespace("B", "urn:external");

        try {
            context.getValue("/ElementA/B:ElementB");
            fail("Expected JXPathNotFoundException");
        }
        catch (JXPathNotFoundException expected) {
        }
    }

    public void testDomPointerResolvesInheritedPrefixAndLocalName() throws Exception {
        Document document = parse(
                "<ElementA xmlns:B='urn:external'>"
                        + "<Container><B:ElementB>value</B:ElementB></Container>"
                        + "</ElementA>",
                true);
        Element child = (Element) document.getDocumentElement()
                .getFirstChild().getFirstChild();

        DOMNodePointer pointer = new DOMNodePointer(child, Locale.US);

        assertEquals("B", DOMNodePointer.getPrefix(child));
        assertEquals("ElementB", DOMNodePointer.getLocalName(child));
        assertEquals("urn:external", DOMNodePointer.getNamespaceURI(child));
        assertEquals("urn:external", pointer.getNamespaceURI("B"));
    }

    private Document parse(String xml, boolean namespaceAware) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(namespaceAware);
        return factory.newDocumentBuilder().parse(
                new ByteArrayInputStream(xml.getBytes("UTF-8")));
    }
}