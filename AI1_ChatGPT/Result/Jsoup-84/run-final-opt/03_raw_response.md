package org.jsoup.helper;

import org.junit.Test;
import org.w3c.dom.Element;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class W3CDomGeneratedTest {

    @Test
    public void convertsUndeclaredElementPrefixToLocalName() {
        org.jsoup.nodes.Document source = new org.jsoup.nodes.Document("");
        org.jsoup.nodes.Element root = source.appendElement("unknown:item");
        root.appendText("payload");

        org.w3c.dom.Document converted = new W3CDom().fromJsoup(source);

        assertEquals("item", converted.getDocumentElement().getNodeName());
        assertEquals("item", converted.getDocumentElement().getLocalName());
        assertNull(converted.getDocumentElement().getNamespaceURI());
        assertEquals("payload", converted.getDocumentElement().getTextContent());
    }

    @Test
    public void convertsNestedUndeclaredPrefixWithoutUsingInheritedDefaultNamespace() {
        org.jsoup.nodes.Document source = new org.jsoup.nodes.Document("");
        org.jsoup.nodes.Element root = source.appendElement("root");
        root.attr("xmlns", "urn:default");
        root.appendElement("missing:child");

        org.w3c.dom.Document converted = new W3CDom().fromJsoup(source);
        Element convertedRoot = converted.getDocumentElement();
        Element convertedChild = (Element) convertedRoot.getFirstChild();

        assertEquals("urn:default", convertedRoot.getNamespaceURI());
        assertEquals("child", convertedChild.getNodeName());
        assertEquals("child", convertedChild.getLocalName());
        assertNull(convertedChild.getNamespaceURI());
    }

    @Test
    public void appliesDeclaredPrefixAndNestedPrefixOverrideInTheirScopes() {
        org.jsoup.nodes.Document source = new org.jsoup.nodes.Document("");
        org.jsoup.nodes.Element root = source.appendElement("p:root");
        root.attr("xmlns:p", "urn:one");
        root.appendElement("p:inherited");
        org.jsoup.nodes.Element overridden = root.appendElement("p:overridden");
        overridden.attr("xmlns:p", "urn:two");

        org.w3c.dom.Document converted = new W3CDom().fromJsoup(source);
        Element convertedRoot = converted.getDocumentElement();
        Element inherited = (Element) convertedRoot.getFirstChild();
        Element overriddenElement = (Element) inherited.getNextSibling();

        assertEquals("urn:one", convertedRoot.getNamespaceURI());
        assertEquals("urn:one", inherited.getNamespaceURI());
        assertEquals("urn:two", overriddenElement.getNamespaceURI());
        assertEquals("overridden", overriddenElement.getLocalName());
    }

    @Test
    public void inheritsDefaultNamespaceForUnprefixedDescendants() {
        org.jsoup.nodes.Document source = new org.jsoup.nodes.Document("");
        org.jsoup.nodes.Element root = source.appendElement("root");
        root.attr("xmlns", "urn:root");
        root.appendElement("child");

        org.w3c.dom.Document converted = new W3CDom().fromJsoup(source);
        Element convertedRoot = converted.getDocumentElement();
        Element child = (Element) convertedRoot.getFirstChild();

        assertEquals("urn:root", convertedRoot.getNamespaceURI());
        assertEquals("root", convertedRoot.getLocalName());
        assertEquals("urn:root", child.getNamespaceURI());
        assertEquals("child", child.getLocalName());
    }

    @Test
    public void retainsDeclaredAndUndeclaredPrefixedAttributes() {
        org.jsoup.nodes.Document source = new org.jsoup.nodes.Document("");
        org.jsoup.nodes.Element root = source.appendElement("p:root");
        root.attr("xmlns:p", "urn:declared");
        root.attr("p:id", "42");
        root.attr("missing:flag", "present");

        org.w3c.dom.Document converted = new W3CDom().fromJsoup(source);
        Element convertedRoot = converted.getDocumentElement();

        assertEquals("urn:declared", convertedRoot.getNamespaceURI());
        assertEquals("42", convertedRoot.getAttribute("p:id"));
        assertEquals("present", convertedRoot.getAttribute("missing:flag"));
    }
}