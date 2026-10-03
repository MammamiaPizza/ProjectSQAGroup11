```java
package org.apache.commons.jxpath.ri.model;

import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;
import org.w3c.dom.Node;

/**
 * Regression tests for JXPATH-114.
 *
 * These tests verify that following and preceding axes enumerate document
 * nodes in the correct order without incorrectly descending into children
 * of a node that should itself be returned by the axis.
 */
public class DOMAndJDOMNodePointerAxisTest extends TestCase {

    public void testDOMFollowingAxisReturnsSecondFollowingSiblingNotItsChild()
            throws Exception {
        org.w3c.dom.Document document = createDOMDocument();

        Node expectedProduct = document.getDocumentElement()
                .getElementsByTagName("product").item(0);

        JXPathContext context = JXPathContext.newContext(document);
        Pointer pointer =
                context.getPointer("//location[2]/following::node()[2]");

        assertSame(
                "The second following node must be the product element itself,"
                    + " not the product's name child.",
                expectedProduct,
                ((NodePointer) pointer).getImmediateNode());
    }

    public void testDOMFollowingAxisExcludesDescendantsOfContextNode()
            throws Exception {
        org.w3c.dom.Document document = createDOMDocument();

        Node expectedSecondLocation = document.getDocumentElement()
                .getElementsByTagName("location").item(1);

        JXPathContext context = JXPathContext.newContext(document);
        Pointer pointer =
                context.getPointer("//location[1]/following::node()[1]");

        assertSame(
                "The following axis must exclude descendants of its context "
                    + "node; its first result must be the second location.",
                expectedSecondLocation,
                ((NodePointer) pointer).getImmediateNode());
    }

    public void testDOMPrecedingAxisIncludesDescendantTextInReverseDocumentOrder()
            throws Exception {
        org.w3c.dom.Document document = createDOMDocument();

        Node expectedText = document.getDocumentElement()
                .getElementsByTagName("employeeCount").item(0)
                .getFirstChild();

        JXPathContext context = JXPathContext.newContext(document);
        Pointer pointer =
                context.getPointer("//location[2]/preceding::node()[3]");

        assertSame(
                "The third preceding node must be the employeeCount text node"
                    + " when preceding nodes are traversed in reverse document order.",
                expectedText,
                ((NodePointer) pointer).getImmediateNode());
    }

    public void testDOMNodePointerNamespacePathMutationAndRootRemoval()
            throws Exception {
        org.w3c.dom.Document document =
                DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder()
                        .newDocument();

        org.w3c.dom.Element root =
                document.createElementNS("urn:test", "p:root");
        root.setAttributeNS(
                "http://www.w3.org/2000/xmlns/", "xmlns:p", "urn:test");
        document.appendChild(root);

        org.w3c.dom.Element firstChild =
                document.createElementNS("urn:test", "p:child");
        org.w3c.dom.Element secondChild =
                document.createElementNS("urn:test", "p:child");
        root.appendChild(firstChild);
        root.appendChild(secondChild);

        org.apache.commons.jxpath.ri.model.dom.DOMNodePointer documentPointer =
                new org.apache.commons.jxpath.ri.model.dom.DOMNodePointer(
                        document, Locale.US);
        org.apache.commons.jxpath.ri.model.dom.DOMNodePointer rootPointer =
                new org.apache.commons.jxpath.ri.model.dom.DOMNodePointer(
                        documentPointer, root);
        org.apache.commons.jxpath.ri.model.dom.DOMNodePointer childPointer =
                new org.apache.commons.jxpath.ri.model.dom.DOMNodePointer(
                        rootPointer, secondChild);

        assertEquals("urn:test", childPointer.getNamespaceURI());
        assertEquals("urn:test", childPointer.getNamespaceURI("p"));
        assertEquals("/p:root[1]/p:child[2]", childPointer.asPath());

        childPointer.setValue("replacement");
        assertEquals("replacement", secondChild.getFirstChild().getNodeValue());

        childPointer.remove();
        assertEquals(1, root.getChildNodes().getLength());
        assertSame(firstChild, root.getFirstChild());

        try {
            documentPointer.remove();
            fail("Removing the root DOM document node must fail.");
        }
        catch (JXPathException expected) {
            assertEquals("Cannot remove root DOM node", expected.getMessage());
        }
    }

    public void testJDOMFollowingAxisReturnsSecondFollowingSiblingNotItsChild() {
        Document document = createJDOMDocument();

        Element expectedProduct = document.getRootElement()
                .getChild("product");

        JXPathContext context = JXPathContext.newContext(document);
        Pointer pointer =
                context.getPointer("//location[2]/following::node()[2]");

        assertSame(
                "The second following node must be the product element itself,"
                    + " not the product's name child.",
                expectedProduct,
                ((NodePointer) pointer).getImmediateNode());
    }

    public void testJDOMFollowingAxisExcludesDescendantsOfContextNode() {
        Document document = createJDOMDocument();

        Element expectedSecondLocation = (Element) document.getRootElement()
                .getChildren("location").get(1);

        JXPathContext context = JXPathContext.newContext(document);
        Pointer pointer =
                context.getPointer("//location[1]/following::node()[1]");

        assertSame(
                "The following axis must exclude descendants of its context "
                    + "node; its first result must be the second location.",
                expectedSecondLocation,
                ((NodePointer) pointer).getImmediateNode());
    }

    public void testJDOMPrecedingAxisIncludesDescendantTextInReverseDocumentOrder() {
        Document document = createJDOMDocument();

        Text expectedText = (Text) document.getRootElement()
                .getChild("location")
                .getChild("employeeCount")
                .getContent()
                .get(0);

        JXPathContext context = JXPathContext.newContext(document);
        Pointer pointer =
                context.getPointer("//location[2]/preceding::node()[3]");

        assertSame(
                "The third preceding node must be the employeeCount text node"
                    + " when preceding nodes are traversed in reverse document order.",
                expectedText,
                ((NodePointer) pointer).getImmediateNode());
    }

    public void testJDOMNodePointerNamespacePathMutationAndRootRemoval() {
        Namespace namespace = Namespace.getNamespace("p", "urn:test");
        Element root = new Element("root", namespace);
        Document document = new Document(root);

        Element firstChild = new Element("child", namespace);
        Element secondChild = new Element("child", namespace);
        root.addContent(firstChild);
        root.addContent(secondChild);

        org.apache.commons.jxpath.ri.model.jdom.JDOMNodePointer documentPointer =
                new org.apache.commons.jxpath.ri.model.jdom.JDOMNodePointer(
                        document, Locale.US);
        org.apache.commons.jxpath.ri.model.jdom.JDOMNodePointer rootPointer =
                new org.apache.commons.jxpath.ri.model.jdom.JDOMNodePointer(
                        documentPointer, root);
        org.apache.commons.jxpath.ri.model.jdom.JDOMNodePointer childPointer =
                new org.apache.commons.jxpath.ri.model.jdom.JDOMNodePointer(
                        rootPointer, secondChild);

        assertEquals("urn:test", childPointer.getNamespaceURI());
        assertEquals("urn:test", childPointer.getNamespaceURI("p"));
        assertEquals("/p:root[1]/p:child[2]", childPointer.asPath());

        childPointer.setValue("replacement");
        assertEquals("replacement", secondChild.getText());

        childPointer.remove();
        assertEquals(1, root.getChildren().size());
        assertSame(firstChild, root.getChildren().get(0));

        try {
            documentPointer.remove();
            fail("Removing the root JDOM document node must fail.");
        }
        catch (JXPathException expected) {
            assertEquals("Cannot remove root JDOM node", expected.getMessage());
        }
    }

    private org.w3c.dom.Document createDOMDocument() throws Exception {
        org.w3c.dom.Document document =
                DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder()
                        .newDocument();

        org.w3c.dom.Element vendor = document.createElement("vendor");
        document.appendChild(vendor);

        org.w3c.dom.Element firstLocation = document.createElement("location");
        vendor.appendChild(firstLocation);

        firstLocation.appendChild(document.createElement("before"));

        org.w3c.dom.Element employeeCount =
                document.createElement("employeeCount");
        employeeCount.appendChild(document.createTextNode("17"));
        firstLocation.appendChild(employeeCount);

        /*
         * These two nodes occur after the employeeCount text node. Therefore,
         * in reverse document order, the employeeCount text node is the third
         * node on the preceding axis of the second location.
         */
        firstLocation.appendChild(document.createElement("afterFirst"));
        firstLocation.appendChild(document.createElement("afterSecond"));

        vendor.appendChild(document.createElement("location"));

        /*
         * The first following node is "first". The second must therefore be
         * "product", rather than its nested "name" element.
         */
        vendor.appendChild(document.createElement("first"));

        org.w3c.dom.Element product = document.createElement("product");
        product.appendChild(document.createElement("name"));
        vendor.appendChild(product);

        return document;
    }

    private Document createJDOMDocument() {
        Element vendor = new Element("vendor");
        Document document = new Document(vendor);

        Element firstLocation = new Element("location");
        vendor.addContent(firstLocation);

        firstLocation.addContent(new Element("before"));

        Element employeeCount = new Element("employeeCount");
        employeeCount.addContent(new Text("17"));
        firstLocation.addContent(employeeCount);

        /*
         * These two nodes occur after the employeeCount text node. Therefore,
         * in reverse document order, the employeeCount text node is the third
         * node on the preceding axis of the second location.
         */
        firstLocation.addContent(new Element("afterFirst"));
        firstLocation.addContent(new Element("afterSecond"));

        vendor.addContent(new Element("location"));

        /*
         * The first following node is "first". The second must therefore be
         * "product", rather than its nested "name" element.
         */
        vendor.addContent(new Element("first"));

        Element product = new Element("product");
        product.addContent(new Element("name"));
        vendor.addContent(product);

        return document;
    }
}
```

### Added-test coverage and fault-detection targets

* `testDOMFollowingAxisExcludesDescendantsOfContextNode`  
  Targets the DOM model’s `following`-axis traversal boundary: descendants of the context node must not be included. This distinguishes traversal that incorrectly descends into `location[1]` from correct traversal beginning at `location[2]`.

* `testJDOMFollowingAxisExcludesDescendantsOfContextNode`  
  Covers the corresponding JDOM traversal boundary and ensures both model implementations conform to XPath following-axis semantics.

* `testDOMNodePointerNamespacePathMutationAndRootRemoval`  
  Exercises previously uncovered `DOMNodePointer` behavior including:
  - Namespace-prefix lookup through `getNamespaceURI(String)`.
  - Namespace-aware `asPath()` construction.
  - Relative same-name sibling position calculation (`p:child[2]`).
  - Element `setValue(Object)` replacement behavior.
  - `remove()` normal path.
  - Exceptional root-document removal path.

* `testJDOMNodePointerNamespacePathMutationAndRootRemoval`  
  Exercises analogous uncovered `JDOMNodePointer` behavior:
  - Namespace lookup and resolver interaction.
  - Namespace-aware paths and sibling positional paths.
  - Element content replacement in `setValue(Object)`.
  - Normal child removal.
  - Exceptional root-document removal behavior.

The original four regression tests remain intact and continue to directly detect JXPATH-114’s incorrect following-axis descent and preceding-axis reverse-order behavior for both DOM and JDOM models.