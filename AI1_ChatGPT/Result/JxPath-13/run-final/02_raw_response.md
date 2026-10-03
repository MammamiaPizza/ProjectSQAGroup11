```java
package org.apache.commons.jxpath.ri.model.dom;

import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.NamespaceResolver;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Tests namespace resolution and DOM node behavior, including creation of an
 * attribute whose namespace is registered externally on a JXPathContext.
 */
public class NamespaceResolverAndDOMNodePointerTest extends TestCase {

    private Document newDocument() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().newDocument();
    }

    public void testNamespaceResolverRegisteredNamespaceCanBeResolvedInBothDirections() {
        NamespaceResolver resolver = new NamespaceResolver();

        resolver.registerNamespace("p", "urn:test");

        assertEquals("urn:test", resolver.getNamespaceURI("p"));
        assertEquals("p", resolver.getPrefix("urn:test"));
        assertNull(resolver.getNamespaceURI("missing"));
        assertNull(resolver.getPrefix("urn:missing"));
    }

    public void testNamespaceResolverUsesParentAndNamespaceContextPointer()
            throws Exception {
        NamespaceResolver parent = new NamespaceResolver();
        parent.registerNamespace("parent", "urn:parent");

        Document document = newDocument();
        Element root = document.createElement("root");
        root.setAttribute("xmlns:dom", "urn:dom");
        document.appendChild(root);

        DOMNodePointer pointer = new DOMNodePointer(root, Locale.US);
        NamespaceResolver child = new NamespaceResolver(parent);
        child.setNamespaceContextPointer(pointer);

        assertEquals("urn:parent", child.getNamespaceURI("parent"));
        assertEquals("urn:dom", child.getNamespaceURI("dom"));
        assertSame(pointer, child.getNamespaceContextPointer());
    }

    public void testNamespaceResolverSealPreventsRegistrationButCloneIsWritable() {
        NamespaceResolver parent = new NamespaceResolver();
        NamespaceResolver resolver = new NamespaceResolver(parent);
        resolver.registerNamespace("before", "urn:before");

        resolver.seal();

        try {
            resolver.registerNamespace("after", "urn:after");
            fail("A sealed resolver must reject namespace registration");
        }
        catch (IllegalStateException expected) {
            assertTrue(resolver.isSealed());
        }

        assertTrue(parent.isSealed());

        NamespaceResolver clone = (NamespaceResolver) resolver.clone();
        assertFalse(clone.isSealed());

        clone.registerNamespace("clone", "urn:clone");
        assertEquals("urn:clone", clone.getNamespaceURI("clone"));
    }

    public void testDOMNodePointerResolvesDeclaredDefaultAndPrefixedNamespaces()
            throws Exception {
        Document document = newDocument();
        Element root = document.createElement("root");
        root.setAttribute("xmlns", "urn:default");
        root.setAttribute("xmlns:p", "urn:prefixed");
        document.appendChild(root);

        Element child = document.createElement("p:child");
        root.appendChild(child);

        DOMNodePointer pointer = new DOMNodePointer(child, Locale.US);

        assertEquals("urn:default", pointer.getNamespaceURI(null));
        assertEquals("urn:default", pointer.getNamespaceURI(""));
        assertEquals("urn:prefixed", pointer.getNamespaceURI("p"));
        assertEquals(DOMNodePointer.XML_NAMESPACE_URI,
                pointer.getNamespaceURI("xml"));
        assertEquals(DOMNodePointer.XMLNS_NAMESPACE_URI,
                pointer.getNamespaceURI("xmlns"));
        assertNull(pointer.getNamespaceURI("unknown"));

        assertEquals("p", DOMNodePointer.getPrefix(child));
        assertEquals("child", DOMNodePointer.getLocalName(child));
        assertEquals("urn:prefixed", DOMNodePointer.getNamespaceURI(child));
    }

    public void testCreateAttributeUsesNamespaceRegisteredOnContext()
            throws Exception {
        Document document = newDocument();
        Element root = document.createElement("root");
        document.appendChild(root);

        JXPathContext context = JXPathContext.newContext(document);
        context.registerNamespace("A", "urn:external");

        DOMNodePointer pointer = new DOMNodePointer(root, Locale.US);
        NodePointer created =
                pointer.createAttribute(context, new QName("A", "created"));

        Attr attribute = root.getAttributeNodeNS("urn:external", "created");
        assertNotNull(attribute);
        assertEquals("A:created", attribute.getName());
        assertEquals("urn:external", attribute.getNamespaceURI());
        assertNotNull(created);
        assertSame(attribute, created.getBaseValue());
    }

    public void testCreateAttributeHandlesUnqualifiedAndUnknownPrefixes()
            throws Exception {
        Document document = newDocument();
        Element root = document.createElement("root");
        document.appendChild(root);

        JXPathContext context = JXPathContext.newContext(document);
        DOMNodePointer pointer = new DOMNodePointer(root, Locale.US);

        NodePointer unqualified =
                pointer.createAttribute(context, new QName(null, "plain"));

        assertTrue(root.hasAttribute("plain"));
        assertEquals("", root.getAttribute("plain"));
        assertNotNull(unqualified);

        try {
            pointer.createAttribute(context, new QName("missing", "attribute"));
            fail("An attribute with an unknown prefix must not be created");
        }
        catch (JXPathException expected) {
            assertTrue(expected.getMessage().indexOf("missing") >= 0);
        }
    }

    public void testSetValueLanguageAndRemoveBehavior() throws Exception {
        Document document = newDocument();
        Element root = document.createElement("root");
        root.setAttribute("xml:lang", "en-US");
        document.appendChild(root);

        Element child = document.createElement("child");
        child.appendChild(document.createTextNode("  old value  "));
        root.appendChild(child);

        DOMNodePointer childPointer = new DOMNodePointer(child, Locale.US);
        assertEquals("old value", childPointer.getValue());
        assertTrue(childPointer.isLanguage("en"));
        assertFalse(childPointer.isLanguage("fr"));

        childPointer.setValue("replacement");
        assertEquals("replacement", child.getTextContent());
        assertEquals("replacement", childPointer.getValue());

        Node text = child.getFirstChild();
        DOMNodePointer textPointer = new DOMNodePointer(text, Locale.US);
        textPointer.setValue("");
        assertFalse(child.hasChildNodes());

        DOMNodePointer rootPointer = new DOMNodePointer(root, Locale.US);
        rootPointer.remove();
        assertNull(document.getDocumentElement());

        DOMNodePointer documentPointer = new DOMNodePointer(document, Locale.US);
        try {
            documentPointer.remove();
            fail("Removing a DOM document node without a parent must fail");
        }
        catch (JXPathException expected) {
            assertTrue(expected.getMessage().indexOf("Cannot remove root") >= 0);
        }
    }

    public void testPointerByIdReturnsNullPointerWhenIdDoesNotExist()
            throws Exception {
        Document document = newDocument();
        Element root = document.createElement("root");
        document.appendChild(root);

        DOMNodePointer pointer = new DOMNodePointer(document, Locale.US);
        Pointer result = pointer.getPointerByID(
                JXPathContext.newContext(document), "does-not-exist");

        assertNotNull(result);
        assertEquals("id('does-not-exist')", result.asPath());
    }
}
```

### Test coverage summary

- **`testNamespaceResolverRegisteredNamespaceCanBeResolvedInBothDirections`**  
  Verifies normal namespace registration, forward URI lookup, reverse prefix lookup, and missing namespace behavior. In particular, it exercises `NamespaceResolver.getPrefix` when no namespace context pointer has been configured.

- **`testNamespaceResolverUsesParentAndNamespaceContextPointer`**  
  Covers resolver inheritance through a parent resolver and lookup from a `DOMNodePointer` namespace context.

- **`testNamespaceResolverSealPreventsRegistrationButCloneIsWritable`**  
  Covers sealing behavior, recursive sealing of the parent, rejection of registrations after sealing, and the clone behavior that resets the sealed state.

- **`testDOMNodePointerResolvesDeclaredDefaultAndPrefixedNamespaces`**  
  Covers default namespace lookup, empty-prefix handling, prefixed lookup through ancestor declarations, special `xml` and `xmlns` prefixes, unknown-prefix behavior, and static DOM name/namespace helper methods.

- **`testCreateAttributeUsesNamespaceRegisteredOnContext`**  
  Targets JXPATH-97. It verifies that `DOMNodePointer.createAttribute` can create a namespaced attribute when the prefix is registered externally on the `JXPathContext`, rather than declared directly in the DOM document.

- **`testCreateAttributeHandlesUnqualifiedAndUnknownPrefixes`**  
  Covers successful creation of an unqualified attribute and the exceptional branch for an unknown namespace prefix.

- **`testSetValueLanguageAndRemoveBehavior`**  
  Covers text trimming in `getValue`, inherited `xml:lang` matching, element content replacement, removal of an empty text node, normal node removal, and the exception raised when attempting to remove a root document node.

- **`testPointerByIdReturnsNullPointerWhenIdDoesNotExist`**  
  Covers the absent-ID branch of `getPointerByID`, ensuring a non-null `NullPointer` result with the expected ID path.