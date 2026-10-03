import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.jxpath.ri.model.dom.DOMNodePointer;
import org.apache.commons.jxpath.ri.model.jdom.JDOMNodePointer;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class JXPath1NodePointerGetNodeTest extends TestCase {

    public void testDomElementPointerReturnsElementAndStringValue() throws Exception {
        org.w3c.dom.Document document = newDomDocument();
        Element element = document.createElement("root");
        element.appendChild(document.createTextNode("value"));
        document.appendChild(element);

        DOMNodePointer pointer = new DOMNodePointer(element, Locale.US);

        assertSame(element, pointer.getImmediateNode());
        assertSame(element, pointer.getNode());
        assertEquals("value", pointer.getValue());
    }

    public void testDomTextPointerReturnsTextAndTextValue() throws Exception {
        org.w3c.dom.Document document = newDomDocument();
        Element element = document.createElement("root");
        Text text = document.createTextNode("text value");
        element.appendChild(text);
        document.appendChild(element);

        DOMNodePointer pointer = new DOMNodePointer(text, Locale.US);

        assertSame(text, pointer.getImmediateNode());
        assertSame(text, pointer.getNode());
        assertEquals("text value", pointer.getValue());
    }

    public void testDomAttributePointerReturnsAttributeAndValue() throws Exception {
        org.w3c.dom.Document document = newDomDocument();
        Element element = document.createElement("root");
        element.setAttribute("id", "attribute value");
        document.appendChild(element);
        Attr attribute = element.getAttributeNode("id");

        DOMNodePointer pointer = new DOMNodePointer(attribute, Locale.US);

        assertSame(attribute, pointer.getImmediateNode());
        assertSame(attribute, pointer.getNode());
        assertEquals("attribute value", pointer.getValue());
    }

    public void testDomDocumentPointerReturnsDocumentElement() throws Exception {
        org.w3c.dom.Document document = newDomDocument();
        Element root = document.createElement("root");
        root.appendChild(document.createTextNode("document value"));
        document.appendChild(root);

        DOMNodePointer pointer = new DOMNodePointer(document, Locale.US);

        assertSame(document, pointer.getNode());
        assertEquals("document value", pointer.getValue());
    }

    public void testDomEmptyDocumentNodeRetrievalDoesNotDereferenceMissingElement()
            throws Exception {
        org.w3c.dom.Document document = newDomDocument();

        Object node = new DOMNodePointer(document, Locale.US).getNode();

        assertTrue(node == null || node instanceof org.w3c.dom.Node);
    }

    public void testJdomElementPointerReturnsElementAndStringValue() {
        org.jdom.Element element = new org.jdom.Element("root");
        element.addContent(new org.jdom.Text("value"));

        JDOMNodePointer pointer = new JDOMNodePointer(element, Locale.US);

        assertSame(element, pointer.getImmediateNode());
        assertSame(element, pointer.getNode());
        assertEquals("value", pointer.getValue());
    }

    public void testJdomTextPointerReturnsTextAndTextValue() {
        org.jdom.Element element = new org.jdom.Element("root");
        org.jdom.Text text = new org.jdom.Text("text value");
        element.addContent(text);

        JDOMNodePointer pointer = new JDOMNodePointer(text, Locale.US);

        assertSame(text, pointer.getImmediateNode());
        assertSame(text, pointer.getNode());
        assertEquals("text value", pointer.getValue());
    }

    public void testJdomAttributePointerReturnsAttributeAndValue() {
        org.jdom.Element element = new org.jdom.Element("root");
        org.jdom.Attribute attribute = new org.jdom.Attribute("id", "attribute value");
        element.setAttribute(attribute);

        JDOMNodePointer pointer = new JDOMNodePointer(attribute, Locale.US);

        assertSame(element, pointer.getImmediateNode());
        assertSame(element, pointer.getNode());
        assertEquals("attribute value", pointer.getValue());
    }

    public void testJdomDocumentPointerReturnsRootElement() {
        org.jdom.Element root = new org.jdom.Element("root");
        root.addContent(new org.jdom.Text("document value"));
        org.jdom.Document document = new org.jdom.Document(root);

        JDOMNodePointer pointer = new JDOMNodePointer(document, Locale.US);

        assertSame(document, pointer.getNode());
        assertEquals("document value", pointer.getValue());
    }

    public void testJdomEmptyDocumentNodeRetrievalDoesNotDereferenceMissingRoot() {
        org.jdom.Document document = new org.jdom.Document();

        Object node = new JDOMNodePointer(document, Locale.US).getNode();

        assertTrue(node == null
                || node instanceof org.jdom.Document
                || node instanceof org.jdom.Element);
    }

    private org.w3c.dom.Document newDomDocument() throws Exception {
        return DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .newDocument();
    }
}
