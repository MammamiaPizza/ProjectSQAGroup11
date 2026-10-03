package org.apache.commons.jxpath.ri.model.dom;

import java.io.StringReader;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Tests for {@link DOMNodePointer}.
 */
public class DOMNodePointerTest extends TestCase {

    private static final String EXTERNAL_NAMESPACE =
            "http://example.org/external";

    private Document parse(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().parse(
                new InputSource(new StringReader(xml)));
    }

    /**
     * Exercises the behavior reported by JXPATH-97: a prefixed child element
     * whose namespace declaration is supplied by the DOM document must be
     * selectable through a JXPath namespace prefix.
     */
    public void testExternalNamespaceElementCanBeSelectedByJXPath()
            throws Exception {
        Document document = parse(
                "<ElementA xmlns:B=\"" + EXTERNAL_NAMESPACE + "\">"
                + "<B:ElementB>value</B:ElementB>"
                + "</ElementA>");

        Element child = (Element) document.getDocumentElement()
                .getFirstChild();
        DOMNodePointer pointer = new DOMNodePointer(child, Locale.US);

        assertEquals(EXTERNAL_NAMESPACE, pointer.getNamespaceURI());
        assertEquals(EXTERNAL_NAMESPACE, pointer.getNamespaceURI("B"));

        JXPathContext context = JXPathContext.newContext(document);
        context.registerNamespace("B", EXTERNAL_NAMESPACE);

        assertEquals("value", context.getValue("/ElementA/B:ElementB"));
    }

    public void testNamespaceLookupFindsAncestorDefaultAndReservedNamespaces()
            throws Exception {
        Document document = parse(
                "<root xmlns=\"urn:default\" xmlns:p=\"urn:parent\">"
                + "<p:child><inner/></p:child>"
                + "</root>");

        Element child = (Element) document.getDocumentElement()
                .getFirstChild();
        Element inner = (Element) child.getFirstChild();
        DOMNodePointer childPointer = new DOMNodePointer(child, Locale.US);
        DOMNodePointer innerPointer = new DOMNodePointer(inner, Locale.US);

        assertEquals("urn:parent", childPointer.getNamespaceURI("p"));
        assertEquals("urn:default", innerPointer.getDefaultNamespaceURI());
        assertEquals("urn:default", innerPointer.getNamespaceURI(""));
        assertEquals(DOMNodePointer.XML_NAMESPACE_URI,
                innerPointer.getNamespaceURI("xml"));
        assertEquals(DOMNodePointer.XMLNS_NAMESPACE_URI,
                innerPointer.getNamespaceURI("xmlns"));
        assertNull(innerPointer.getNamespaceURI("unknown"));
    }

    public void testSetValueUpdatesTextRemovesEmptyTextAndReplacesElementContents()
            throws Exception {
        Document document = parse(
                "<root><value>old</value><remove>obsolete</remove>"
                + "<container><old/></container></root>");

        Element root = document.getDocumentElement();
        Element value = (Element) root.getFirstChild();
        Element remove = (Element) value.getNextSibling();
        Element container = (Element) remove.getNextSibling();

        DOMNodePointer valueTextPointer =
                new DOMNodePointer(value.getFirstChild(), Locale.US);
        valueTextPointer.setValue("new");
        assertEquals("new", value.getFirstChild().getNodeValue());

        DOMNodePointer removeTextPointer =
                new DOMNodePointer(remove.getFirstChild(), Locale.US);
        removeTextPointer.setValue("");
        assertFalse(remove.hasChildNodes());

        DOMNodePointer containerPointer =
                new DOMNodePointer(container, Locale.US);
        containerPointer.setValue("replacement");
        assertEquals(1, container.getChildNodes().getLength());
        assertEquals("replacement", container.getFirstChild().getNodeValue());

        Element source = document.createElement("source");
        source.appendChild(document.createElement("copied"));
        containerPointer.setValue(source);
        assertEquals("copied", container.getFirstChild().getNodeName());
        assertNotSame(source.getFirstChild(), container.getFirstChild());
    }

    public void testCreateAttributeSupportsUnprefixedAndDeclaredPrefixedNames()
            throws Exception {
        Document document = parse(
                "<root xmlns:p=\"urn:attributes\"/>");
        Element root = document.getDocumentElement();
        DOMNodePointer pointer = new DOMNodePointer(root, Locale.US);
        JXPathContext context = JXPathContext.newContext(document);

        NodePointer plain =
                pointer.createAttribute(context, new QName(null, "plain"));
        assertEquals("", root.getAttribute("plain"));
        assertEquals("plain", ((Node) plain.getBaseValue()).getNodeName());

        NodePointer prefixed =
                pointer.createAttribute(context, new QName("p", "qualified"));
        assertEquals("", root.getAttributeNS("urn:attributes", "qualified"));
        assertEquals("urn:attributes",
                ((Node) prefixed.getBaseValue()).getNamespaceURI());

        try {
            pointer.createAttribute(context, new QName("missing", "attribute"));
            fail("An undeclared prefix must not create an attribute");
        }
        catch (JXPathException expected) {
            assertTrue(expected.getMessage().indexOf("missing") >= 0);
        }
    }

    public void testValueHonorsWhitespaceAndLanguageInheritedFromAncestor()
            throws Exception {
        Document document = parse(
                "<root xml:lang=\"en-US\">"
                + "<trimmed>  value  </trimmed>"
                + "<preserved xml:space=\"preserve\">  value  </preserved>"
                + "<!--  comment value  -->"
                + "</root>");

        Element root = document.getDocumentElement();
        Element trimmed = (Element) root.getFirstChild();
        Element preserved = (Element) trimmed.getNextSibling();
        Node comment = preserved.getNextSibling();

        DOMNodePointer trimmedPointer =
                new DOMNodePointer(trimmed, Locale.US);
        DOMNodePointer preservedPointer =
                new DOMNodePointer(preserved, Locale.US);
        DOMNodePointer commentPointer =
                new DOMNodePointer(comment, Locale.US);

        assertEquals("value", trimmedPointer.getValue());
        assertEquals("  value  ", preservedPointer.getValue());
        assertEquals("comment value", commentPointer.getValue());
        assertTrue(trimmedPointer.isLanguage("en"));
        assertTrue(trimmedPointer.isLanguage("EN-us"));
        assertFalse(trimmedPointer.isLanguage("fr"));
    }

    public void testAsPathEqualityAndRemoveForSiblingElements()
            throws Exception {
        Document document = parse(
                "<root><item/><other/><item/></root>");
        Element root = document.getDocumentElement();
        Element firstItem = (Element) root.getFirstChild();
        Element secondItem = (Element) firstItem.getNextSibling()
                .getNextSibling();

        DOMNodePointer documentPointer =
                new DOMNodePointer(document, Locale.US);
        DOMNodePointer rootPointer =
                new DOMNodePointer(documentPointer, root);
        DOMNodePointer secondItemPointer =
                new DOMNodePointer(rootPointer, secondItem);

        assertEquals("/root[1]/item[2]", secondItemPointer.asPath());
        assertEquals(secondItemPointer,
                new DOMNodePointer(secondItem, Locale.US));
        assertFalse(secondItemPointer.equals(
                new DOMNodePointer(firstItem, Locale.US)));

        secondItemPointer.remove();
        assertEquals(2, root.getChildNodes().getLength());
        assertEquals("other", root.getLastChild().getNodeName());

        try {
            documentPointer.remove();
            fail("Removing the root DOM document must fail");
        }
        catch (JXPathException expected) {
            assertTrue(expected.getMessage().indexOf("root") >= 0);
        }
    }
}
