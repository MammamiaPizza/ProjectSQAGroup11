package org.apache.commons.jxpath.ri.model;

import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.jxpath.ri.model.dom.DOMNodePointer;
import org.apache.commons.jxpath.ri.model.jdom.JDOMNodePointer;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;

/**
 * Regression tests for JXPATH-83.
 *
 * The JXPath node-pointer string value is trimmed even when the source
 * element declares xml:space="preserve".
 */
public class NodePointerValueAndStructureTest extends TestCase {

    private org.w3c.dom.Document newDOMDocument() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().newDocument();
    }

    /**
     * JXPATH-83: DOM pointer values must not retain leading and trailing
     * whitespace solely because xml:space is set to preserve.
     */
    public void testDOMValueTrimsWhitespaceForXmlSpacePreserveElement()
            throws Exception {
        org.w3c.dom.Document document = newDOMDocument();
        org.w3c.dom.Element root = document.createElement("root");
        root.setAttributeNS(
                DOMNodePointer.XML_NAMESPACE_URI,
                "xml:space",
                "preserve");
        root.appendChild(document.createTextNode(" foo "));
        document.appendChild(root);

        DOMNodePointer pointer = new DOMNodePointer(root, Locale.US);

        assertEquals("foo", pointer.getValue());
    }

    /**
     * JXPATH-83: JDOM pointer values must have the same trimmed string-value
     * behavior as DOM pointer values for xml:space="preserve".
     */
    public void testJDOMValueTrimsWhitespaceForXmlSpacePreserveElement() {
        Element root = new Element("root");
        root.setAttribute("space", "preserve", Namespace.XML_NAMESPACE);
        root.addContent(new Text(" foo "));

        JDOMNodePointer pointer = new JDOMNodePointer(root, Locale.US);

        assertEquals("foo", pointer.getValue());
    }

    public void testDOMTextCommentAndProcessingInstructionValuesAreTrimmed()
            throws Exception {
        org.w3c.dom.Document document = newDOMDocument();

        org.w3c.dom.Text text = document.createTextNode(" text ");
        org.w3c.dom.Comment comment = document.createComment(" comment ");
        org.w3c.dom.ProcessingInstruction instruction =
                document.createProcessingInstruction("target", " data ");

        assertEquals("text", new DOMNodePointer(text, Locale.US).getValue());
        assertEquals(
                "comment",
                new DOMNodePointer(comment, Locale.US).getValue());
        assertEquals(
                "data",
                new DOMNodePointer(instruction, Locale.US).getValue());
    }

    public void testJDOMTextValuesAreTrimmed() {
        Text text = new Text(" text ");

        assertEquals(
                "text",
                new JDOMNodePointer(text, Locale.US).getValue());
    }
}
