```java
package org.apache.commons.jxpath.ri.model;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.jdom.Document;
import org.jdom.Element;
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
         * These two nodes occur after the employeeCount text node.  Therefore,
         * in reverse document order, the employeeCount text node is the third
         * node on the preceding axis of the second location.
         */
        firstLocation.appendChild(document.createElement("afterFirst"));
        firstLocation.appendChild(document.createElement("afterSecond"));

        vendor.appendChild(document.createElement("location"));

        /*
         * The first following node is "first".  The second must therefore be
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
         * These two nodes occur after the employeeCount text node.  Therefore,
         * in reverse document order, the employeeCount text node is the third
         * node on the preceding axis of the second location.
         */
        firstLocation.addContent(new Element("afterFirst"));
        firstLocation.addContent(new Element("afterSecond"));

        vendor.addContent(new Element("location"));

        /*
         * The first following node is "first".  The second must therefore be
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

Test coverage summary:

- `testDOMFollowingAxisReturnsSecondFollowingSiblingNotItsChild` verifies the DOM pointer model returns the `product` element for `following::node()[2]`, rather than incorrectly descending into `product/name`.
- `testDOMPrecedingAxisIncludesDescendantTextInReverseDocumentOrder` verifies DOM preceding-axis traversal includes descendant text nodes and honors reverse document order.
- `testJDOMFollowingAxisReturnsSecondFollowingSiblingNotItsChild` applies the same JXPATH-114 following-axis regression scenario to `JDOMNodePointer`.
- `testJDOMPrecedingAxisIncludesDescendantTextInReverseDocumentOrder` applies the preceding-axis regression scenario to `JDOMNodePointer`, ensuring the text node is selected instead of an unrelated preceding sibling.