package org.apache.commons.jxpath.ri.model;

import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.model.dom.DOMNodePointer;
import org.apache.commons.jxpath.ri.model.jdom.JDOMNodePointer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * Regression tests for JXPATH-114.
 *
 * XPath's node() test must include text nodes.  In particular, text nodes
 * must be visible on the following and preceding axes for both DOM and JDOM
 * models.
 */
public class NodeAxisTest extends TestCase {

    public NodeAxisTest(String name) {
        super(name);
    }

    public void testDomNodeTypeNodeMatchesTextNode() throws Exception {
        Document document = newDocument();
        Text text = document.createTextNode("text");

        assertTrue(
                "XPath node() must match a DOM text node",
                DOMNodePointer.testNode(
                        text,
                        new NodeTypeTest(Compiler.NODE_TYPE_NODE)));
    }

    public void testJdomNodeTypeNodeMatchesTextNode() {
        org.jdom.Text text = new org.jdom.Text("text");
        JDOMNodePointer pointer =
                new JDOMNodePointer(text, Locale.getDefault());

        assertTrue(
                "XPath node() must match a JDOM text node",
                JDOMNodePointer.testNode(
                        pointer,
                        text,
                        new NodeTypeTest(Compiler.NODE_TYPE_NODE)));
    }

    public void testDomFollowingNodeAxisIncludesTextNodes() throws Exception {
        Document document = newDocument();
        Element root = document.createElement("root");
        document.appendChild(root);

        root.appendChild(document.createElement("location"));
        Element secondLocation = document.createElement("location");
        root.appendChild(secondLocation);

        Text separator = document.createTextNode("between-locations-and-product");
        root.appendChild(separator);

        Element product = document.createElement("product");
        product.appendChild(document.createElement("name"));
        root.appendChild(product);

        JXPathContext context = JXPathContext.newContext(document);

        Pointer firstFollowing =
                context.getPointer("/root/location[2]/following::node()[1]");
        Pointer secondFollowing =
                context.getPointer("/root/location[2]/following::node()[2]");

        assertTrue(firstFollowing instanceof DOMNodePointer);
        assertSame(
                "The first following node must be the intervening text node",
                separator,
                ((NodePointer) firstFollowing).getBaseValue());

        assertTrue(secondFollowing instanceof DOMNodePointer);
        assertSame(
                "The second following node must be the product element, not "
                    + "the product's child after text nodes are skipped",
                product,
                ((NodePointer) secondFollowing).getBaseValue());
    }

    public void testJdomFollowingNodeAxisIncludesTextNodes() {
        org.jdom.Element root = new org.jdom.Element("root");
        org.jdom.Document document = new org.jdom.Document(root);

        root.addContent(new org.jdom.Element("location"));
        root.addContent(new org.jdom.Element("location"));

        org.jdom.Text separator =
                new org.jdom.Text("between-locations-and-product");
        root.addContent(separator);

        org.jdom.Element product = new org.jdom.Element("product");
        product.addContent(new org.jdom.Element("name"));
        root.addContent(product);

        JXPathContext context = JXPathContext.newContext(document);

        Pointer firstFollowing =
                context.getPointer("/root/location[2]/following::node()[1]");
        Pointer secondFollowing =
                context.getPointer("/root/location[2]/following::node()[2]");

        assertTrue(firstFollowing instanceof JDOMNodePointer);
        assertSame(
                "The first following node must be the intervening text node",
                separator,
                ((NodePointer) firstFollowing).getBaseValue());

        assertTrue(secondFollowing instanceof JDOMNodePointer);
        assertSame(
                "The second following node must be the product element, not "
                    + "the product's child after text nodes are skipped",
                product,
                ((NodePointer) secondFollowing).getBaseValue());
    }

    public void testDomPrecedingNodeAxisIncludesTextNodes() throws Exception {
        Document document = newDocument();
        Element root = document.createElement("root");
        document.appendChild(root);

        Element firstLocation = document.createElement("location");
        Element address = document.createElement("address");
        Element employeeCount = document.createElement("employeeCount");
        Text employeeCountText = document.createTextNode("10");
        employeeCount.appendChild(employeeCountText);
        firstLocation.appendChild(address);
        firstLocation.appendChild(employeeCount);
        root.appendChild(firstLocation);

        Text separator = document.createTextNode("between-locations");
        root.appendChild(separator);

        root.appendChild(document.createElement("location"));

        JXPathContext context = JXPathContext.newContext(document);

        Pointer firstPreceding =
                context.getPointer("/root/location[2]/preceding::node()[1]");
        Pointer secondPreceding =
                context.getPointer("/root/location[2]/preceding::node()[2]");

        assertTrue(firstPreceding instanceof DOMNodePointer);
        assertSame(
                "The nearest preceding node must be the text node between "
                    + "the two locations",
                separator,
                ((NodePointer) firstPreceding).getBaseValue());

        assertTrue(secondPreceding instanceof DOMNodePointer);
        assertSame(
                "The next preceding node must be the employeeCount text node",
                employeeCountText,
                ((NodePointer) secondPreceding).getBaseValue());
    }

    public void testJdomPrecedingNodeAxisIncludesTextNodes() {
        org.jdom.Element root = new org.jdom.Element("root");
        org.jdom.Document document = new org.jdom.Document(root);

        org.jdom.Element firstLocation = new org.jdom.Element("location");
        org.jdom.Element address = new org.jdom.Element("address");
        org.jdom.Element employeeCount =
                new org.jdom.Element("employeeCount");
        org.jdom.Text employeeCountText = new org.jdom.Text("10");
        employeeCount.addContent(employeeCountText);
        firstLocation.addContent(address);
        firstLocation.addContent(employeeCount);
        root.addContent(firstLocation);

        org.jdom.Text separator = new org.jdom.Text("between-locations");
        root.addContent(separator);

        root.addContent(new org.jdom.Element("location"));

        JXPathContext context = JXPathContext.newContext(document);

        Pointer firstPreceding =
                context.getPointer("/root/location[2]/preceding::node()[1]");
        Pointer secondPreceding =
                context.getPointer("/root/location[2]/preceding::node()[2]");

        assertTrue(firstPreceding instanceof JDOMNodePointer);
        assertSame(
                "The nearest preceding node must be the text node between "
                    + "the two locations",
                separator,
                ((NodePointer) firstPreceding).getBaseValue());

        assertTrue(secondPreceding instanceof JDOMNodePointer);
        assertSame(
                "The next preceding node must be the employeeCount text node",
                employeeCountText,
                ((NodePointer) secondPreceding).getBaseValue());
    }

    private Document newDocument() throws Exception {
        return DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .newDocument();
    }
}
