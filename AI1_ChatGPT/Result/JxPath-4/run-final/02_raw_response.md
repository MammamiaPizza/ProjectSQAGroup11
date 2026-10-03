```java
package org.apache.commons.jxpath.ri.model;

import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.dom.DOMNodePointer;
import org.apache.commons.jxpath.ri.model.jdom.JDOMNodePointer;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;
import org.w3c.dom.Node;

/**
 * Tests DOMNodePointer and JDOMNodePointer behavior, including XML-space
 * related trimmed string values covered by JXPATH-83.
 */
public class NodePointerValueAndStructureTest extends TestCase {

    private org.w3c.dom.Document newDOMDocument() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().newDocument();
    }

    public void testDOMValueTrimsWhitespaceForXmlSpacePreserveElement()
            throws Exception {
        org.w3c.dom.Document document = newDOMDocument();
        org.w3c.dom.Element root = document.createElement("root");
        root.setAttributeNS(DOMNodePointer.XML_NAMESPACE_URI,
                "xml:space", "preserve");
        root.appendChild(document.createTextNode(" foo "));
        document.appendChild(root);

        DOMNodePointer pointer = new DOMNodePointer(root, Locale.US);

        assertEquals("foo", pointer.getValue());
    }

    public void testJDOMValueTrimsWhitespaceForXmlSpacePreserveElement() {
        Element root = new Element("root");
        root.setAttribute("space", "preserve", Namespace.XML_NAMESPACE);
        root.addContent(new Text(" foo "));

        JDOMNodePointer pointer = new JDOMNodePointer(root, Locale.US);

        assertEquals("foo", pointer.getValue());
    }

    public void testDOMValuesTrimTextCommentAndProcessingInstruction()
            throws Exception {
        org.w3c.dom.Document document = newDOMDocument();

        org.w3c.dom.Text text = document.createTextNode(" text ");
        org.w3c.dom.Comment comment = document.createComment(" comment ");
        org.w3c.dom.ProcessingInstruction pi =
                document.createProcessingInstruction("target", " data ");

        assertEquals("text", new DOMNodePointer(text, Locale.US).getValue());
        assertEquals("comment",
                new DOMNodePointer(comment, Locale.US).getValue());
        assertEquals("data", new DOMNodePointer(pi, Locale.US).getValue());
    }

    public void testJDOMValuesTrimTextCdataCommentAndProcessingInstruction() {
        Text text = new Text(" text ");
        CDATA cdata = new CDATA(" cdata ");
        Comment comment = new Comment(" comment ");
        ProcessingInstruction pi =
                new ProcessingInstruction("target", " data ");

        assertEquals("text",
                new JDOMNodePointer(text, Locale.US).getValue());
        assertEquals("cdata",
                new JDOMNodePointer(cdata, Locale.US).getValue());
        assertEquals("comment",
                new JDOMNodePointer(comment, Locale.US).getValue());
        assertEquals("data",
                new JDOMNodePointer(pi, Locale.US).getValue());
    }

    public void testDOMSetValueReplacesElementChildrenAndRemovesEmptyText()
            throws Exception {
        org.w3c.dom.Document document = newDOMDocument();
        org.w3c.dom.Element root = document.createElement("root");
        root.appendChild(document.createTextNode("old"));
        document.appendChild(root);

        DOMNodePointer rootPointer = new DOMNodePointer(root, Locale.US);
        rootPointer.setValue("new value");

        assertEquals("new value", rootPointer.getValue());
        assertEquals(1, root.getChildNodes().getLength());
        assertEquals(Node.TEXT_NODE, root.getFirstChild().getNodeType());

        org.w3c.dom.Text text = (org.w3c.dom.Text) root.getFirstChild();
        DOMNodePointer textPointer = new DOMNodePointer(text, Locale.US);
        textPointer.setValue("");

        assertEquals(0, root.getChildNodes().getLength());
    }

    public void testJDOMSetValueReplacesElementChildrenAndRemovesEmptyText() {
        Element root = new Element("root");
        Text oldText = new Text("old");
        root.addContent(oldText);

        JDOMNodePointer rootPointer = new JDOMNodePointer(root, Locale.US);
        rootPointer.setValue("new value");

        assertEquals("new value", rootPointer.getValue());
        assertEquals(1, root.getContent().size());

        JDOMNodePointer textPointer =
                new JDOMNodePointer((Text) root.getContent().get(0), Locale.US);
        textPointer.setValue("");

        assertEquals(0, root.getContent().size());
    }

    public void testDOMNamespaceLanguageAndPathResolution() throws Exception {
        org.w3c.dom.Document document = newDOMDocument();
        org.w3c.dom.Element root =
                document.createElementNS("urn:default", "root");
        root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns",
                "urn:default");
        root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:p",
                "urn:prefix");
        root.setAttributeNS(DOMNodePointer.XML_NAMESPACE_URI,
                "xml:lang", "en-US");

        org.w3c.dom.Element first =
                document.createElementNS("urn:default", "item");
        org.w3c.dom.Element second =
                document.createElementNS("urn:default", "item");
        root.appendChild(first);
        root.appendChild(second);
        document.appendChild(root);

        DOMNodePointer documentPointer =
                new DOMNodePointer(document, Locale.US);
        DOMNodePointer rootPointer = new DOMNodePointer(documentPointer, root);
        DOMNodePointer secondPointer = new DOMNodePointer(rootPointer, second);

        assertEquals("urn:default", rootPointer.getDefaultNamespaceURI());
        assertEquals("urn:prefix", rootPointer.getNamespaceURI("p"));
        assertEquals(DOMNodePointer.XML_NAMESPACE_URI,
                rootPointer.getNamespaceURI("xml"));
        assertNull(rootPointer.getNamespaceURI("unknown"));
        assertTrue(secondPointer.isLanguage("en"));
        assertFalse(secondPointer.isLanguage("fr"));
        assertEquals("/root[1]/item[2]", secondPointer.asPath());
    }

    public void testJDOMNamespaceLanguageAndPathResolution() {
        Namespace defaultNamespace = Namespace.getNamespace("urn:default");
        Namespace prefixNamespace = Namespace.getNamespace("p", "urn:prefix");

        Element root = new Element("root", defaultNamespace);
        root.addNamespaceDeclaration(prefixNamespace);
        root.setAttribute("lang", "en-US", Namespace.XML_NAMESPACE);

        Element first = new Element("item", defaultNamespace);
        Element second = new Element("item", defaultNamespace);
        root.addContent(first);
        root.addContent(second);
        Document document = new Document(root);

        JDOMNodePointer documentPointer =
                new JDOMNodePointer(document, Locale.US);
        JDOMNodePointer rootPointer =
                new JDOMNodePointer(documentPointer, root);
        JDOMNodePointer secondPointer =
                new JDOMNodePointer(rootPointer, second);

        assertEquals("urn:default", rootPointer.getNamespaceURI());
        assertEquals("urn:prefix", rootPointer.getNamespaceURI("p"));
        assertNull(rootPointer.getNamespaceURI("unknown"));
        assertTrue(secondPointer.isLanguage("en"));
        assertFalse(secondPointer.isLanguage("fr"));
        assertEquals("/root[1]/item[2]", secondPointer.asPath());
    }

    public void testDOMCreateAttributeAndRejectUnknownPrefix() throws Exception {
        org.w3c.dom.Document document = newDOMDocument();
        org.w3c.dom.Element root = document.createElement("root");
        root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:p",
                "urn:prefix");
        document.appendChild(root);

        DOMNodePointer pointer = new DOMNodePointer(root, Locale.US);
        pointer.createAttribute(null, new QName(null, "plain"));

        assertTrue(root.hasAttribute("plain"));

        try {
            pointer.createAttribute(null, new QName("missing", "attribute"));
            fail("An undeclared namespace prefix must be rejected");
        }
        catch (JXPathException expected) {
            assertTrue(expected.getMessage().indexOf("Unknown namespace prefix")
                    >= 0);
        }
    }

    public void testJDOMCreateAttributeAndRejectUnknownPrefix() {
        Namespace prefixNamespace = Namespace.getNamespace("p", "urn:prefix");
        Element root = new Element("root");
        root.addNamespaceDeclaration(prefixNamespace);

        JDOMNodePointer pointer = new JDOMNodePointer(root, Locale.US);
        pointer.createAttribute(null, new QName(null, "plain"));

        assertNotNull(root.getAttribute("plain"));

        try {
            pointer.createAttribute(null, new QName("missing", "attribute"));
            fail("An undeclared namespace prefix must be rejected");
        }
        catch (JXPathException expected) {
            assertTrue(expected.getMessage().indexOf("Unknown namespace prefix")
                    >= 0);
        }
    }

    public void testPointersRemoveChildrenButCannotRemoveRoots()
            throws Exception {
        org.w3c.dom.Document domDocument = newDOMDocument();
        org.w3c.dom.Element domRoot = domDocument.createElement("root");
        org.w3c.dom.Element domChild = domDocument.createElement("child");
        domRoot.appendChild(domChild);
        domDocument.appendChild(domRoot);

        new DOMNodePointer(domChild, Locale.US).remove();
        assertEquals(0, domRoot.getChildNodes().getLength());

        try {
            new DOMNodePointer(domDocument, Locale.US).remove();
            fail("Removing a DOM document root must fail");
        }
        catch (JXPathException expected) {
            assertTrue(expected.getMessage().indexOf("Cannot remove root")
                    >= 0);
        }

        Element jdomRoot = new Element("root");
        Element jdomChild = new Element("child");
        jdomRoot.addContent(jdomChild);
        new Document(jdomRoot);

        new JDOMNodePointer(jdomChild, Locale.US).remove();
        assertEquals(0, jdomRoot.getContent().size());

        try {
            new JDOMNodePointer(jdomRoot, Locale.US).remove();
            fail("Removing a JDOM document root must fail");
        }
        catch (JXPathException expected) {
            assertTrue(expected.getMessage().indexOf("Cannot remove root")
                    >= 0);
        }
    }

    public void testIdentityEqualityAndEscapedIdPaths() throws Exception {
        org.w3c.dom.Document domDocument = newDOMDocument();
        org.w3c.dom.Element domRoot = domDocument.createElement("root");
        domDocument.appendChild(domRoot);

        DOMNodePointer domPointer1 =
                new DOMNodePointer(domRoot, Locale.US, "a'b\"c");
        DOMNodePointer domPointer2 =
                new DOMNodePointer(domRoot, Locale.US);

        assertTrue(domPointer1.equals(domPointer2));
        assertEquals(domPointer1.hashCode(), domPointer2.hashCode());
        assertEquals("id('a&apos;b&quot;c')", domPointer1.asPath());

        Element jdomRoot = new Element("root");
        JDOMNodePointer jdomPointer1 =
                new JDOMNodePointer(jdomRoot, Locale.US, "a'b\"c");
        JDOMNodePointer jdomPointer2 =
                new JDOMNodePointer(jdomRoot, Locale.US);

        assertTrue(jdomPointer1.equals(jdomPointer2));
        assertEquals(jdomPointer1.hashCode(), jdomPointer2.hashCode());
        assertEquals("id('a&apos;b&quot;c')", jdomPointer1.asPath());
    }
}
```

Test coverage summary:

- `testDOMValueTrimsWhitespaceForXmlSpacePreserveElement` and `testJDOMValueTrimsWhitespaceForXmlSpacePreserveElement` target the JXPATH-83 XML-space regression: values containing preserved surrounding whitespace must still produce the expected trimmed JXPath value.
- The text/comment/CDATA/processing-instruction tests cover `getValue()` branches for supported node types.
- The `setValue` tests cover replacement of element content and removal of text nodes when assigned an empty value.
- Namespace, language, and path tests cover default/prefixed namespace lookup, reserved behavior, inherited `xml:lang`, sibling-relative path indexes, and missing namespaces.
- Attribute tests cover ordinary attribute creation and exceptional handling of undeclared namespace prefixes.
- Removal tests cover valid child removal and root-removal exceptions.
- Identity/path tests cover pointer equality, identity-based hash codes, and quote escaping in ID-based paths.