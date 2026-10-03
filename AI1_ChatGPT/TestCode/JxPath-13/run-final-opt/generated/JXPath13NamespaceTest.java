import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.ri.NamespaceResolver;
import org.apache.commons.jxpath.ri.model.dom.DOMNodePointer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JXPath13NamespaceTest extends TestCase {

    public void testRegisteredNamespaceResolvesInBothDirections() {
        NamespaceResolver resolver = new NamespaceResolver();

        resolver.registerNamespace("A", "urn:test:external");

        assertEquals("urn:test:external", resolver.getNamespaceURI("A"));
        assertEquals("A", resolver.getPrefix("urn:test:external"));
    }

    public void testChildResolverUsesParentRegisteredNamespace() {
        NamespaceResolver parent = new NamespaceResolver();
        NamespaceResolver child = new NamespaceResolver(parent);

        parent.registerNamespace("A", "urn:test:parent");

        assertEquals("urn:test:parent", child.getNamespaceURI("A"));
        assertEquals("A", child.getPrefix("urn:test:parent"));
    }

    public void testEmptyPrefixCanBeRegisteredAndResolved() {
        NamespaceResolver resolver = new NamespaceResolver();

        resolver.registerNamespace("", "urn:test:default");

        assertEquals("urn:test:default", resolver.getNamespaceURI(""));
        assertEquals("", resolver.getPrefix("urn:test:default"));
    }

    public void testDOMNodePointerFindsNamespaceDeclaredOnElement() throws Exception {
        Document document = newDocument();
        Element root = document.createElement("root");
        root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:p",
                "urn:test:declared");
        document.appendChild(root);

        DOMNodePointer pointer = new DOMNodePointer(root, Locale.US);

        assertEquals("urn:test:declared", pointer.getNamespaceURI("p"));
    }

    public void testCreateAndSetAttributeUsesExternallyRegisteredNamespace()
            throws Exception {
        Document document = newDocument();
        Element root = document.createElement("root");
        document.appendChild(root);

        JXPathContext context = JXPathContext.newContext(root);
        context.registerNamespace("A", "urn:test:external");
        context.createPathAndSetValue("@A:code", "42");

        assertEquals("42", root.getAttributeNS("urn:test:external", "code"));
        assertNotNull(root.getAttributeNodeNS("urn:test:external", "code"));
        assertEquals("A",
                root.getAttributeNodeNS("urn:test:external", "code").getPrefix());
    }

    public void testCreateAttributeWithUnknownPrefixFails() throws Exception {
        Document document = newDocument();
        Element root = document.createElement("root");
        document.appendChild(root);

        JXPathContext context = JXPathContext.newContext(root);
        try {
            context.createPathAndSetValue("@missing:code", "42");
            fail("An attribute with an unknown namespace prefix must not be created");
        }
        catch (JXPathException expected) {
            assertNotNull(expected.getMessage());
        }
    }

    private Document newDocument() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().newDocument();
    }
}
