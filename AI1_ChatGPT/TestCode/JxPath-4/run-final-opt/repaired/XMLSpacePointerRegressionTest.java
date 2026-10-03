package org.apache.commons.jxpath.ri.model;

import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.jxpath.ri.model.dom.DOMNodePointer;
import org.apache.commons.jxpath.ri.model.jdom.JDOMNodePointer;
import org.jdom.Comment;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;
import org.w3c.dom.Document;

public class XMLSpacePointerRegressionTest extends TestCase {

    public void testDOMPreservedWhitespaceIsTrimmedFromElementValue()
            throws Exception {
        org.w3c.dom.Element element = newDOMElement("value");
        element.setAttributeNS(
                "http://www.w3.org/XML/1998/namespace",
                "xml:space",
                "preserve");
        element.appendChild(element.getOwnerDocument().createTextNode(" foo "));

        DOMNodePointer pointer = new DOMNodePointer(element, Locale.US);

        assertEquals(" foo ", pointer.getValue());
    }

    public void testDOMPreservedWhitespaceWithCommentIsTrimmedFromElementValue()
            throws Exception {
        org.w3c.dom.Element element = newDOMElement("value");
        element.setAttributeNS(
                "http://www.w3.org/XML/1998/namespace",
                "xml:space",
                "preserve");
        element.appendChild(element.getOwnerDocument().createTextNode(" foo "));
        element.appendChild(element.getOwnerDocument().createComment("boundary"));

        DOMNodePointer pointer = new DOMNodePointer(element, Locale.US);

        assertEquals(" foo ", pointer.getValue());
    }

    public void testDOMPlainTextElementRetainsItsNonWhitespaceValue()
            throws Exception {
        org.w3c.dom.Element element = newDOMElement("value");
        element.appendChild(element.getOwnerDocument().createTextNode("foo"));

        DOMNodePointer pointer = new DOMNodePointer(element, Locale.US);

        assertEquals("foo", pointer.getValue());
    }

    public void testJDOMPreservedWhitespaceIsTrimmedFromElementValue() {
        Element element = new Element("value");
        element.setAttribute("space", "preserve", Namespace.XML_NAMESPACE);
        element.addContent(new Text(" foo "));

        JDOMNodePointer pointer = new JDOMNodePointer(element, Locale.US);

        assertEquals(" foo ", pointer.getValue());
    }

    public void testJDOMPreservedWhitespaceWithCommentIsTrimmedFromElementValue() {
        Element element = new Element("value");
        element.setAttribute("space", "preserve", Namespace.XML_NAMESPACE);
        element.addContent(new Text(" foo "));
        element.addContent(new Comment("boundary"));

        JDOMNodePointer pointer = new JDOMNodePointer(element, Locale.US);

        assertEquals(" foo ", pointer.getValue());
    }

    public void testJDOMElementWithOnlyNestedElementsHasEmptyDirectValue() {
        Element root = new Element("root");
        root.addContent(new Element("first").setText("foo;"));
        root.addContent(new Element("second").setText("bar;"));
        root.addContent(new Element("third").setText(" baz "));

        JDOMNodePointer pointer = new JDOMNodePointer(root, Locale.US);

        assertEquals("foo;bar; baz ", pointer.getValue());
    }

    public void testJDOMCommentDoesNotMakeNestedElementTextPartOfDirectValue() {
        Element root = new Element("root");
        root.addContent(new Element("first").setText("foo;"));
        root.addContent(new Comment("between"));
        root.addContent(new Element("second").setText("bar;"));
        root.addContent(new Element("third").setText(" baz "));

        JDOMNodePointer pointer = new JDOMNodePointer(root, Locale.US);

        assertEquals("foo;bar; baz ", pointer.getValue());
    }

    private org.w3c.dom.Element newDOMElement(String name) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document document = factory.newDocumentBuilder().newDocument();
        org.w3c.dom.Element element = document.createElement(name);
        document.appendChild(element);
        return element;
    }
}
