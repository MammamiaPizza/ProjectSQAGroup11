package org.jsoup.helper;

import org.junit.Test;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class W3CDomNamespaceTest {
    private static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";
    private static final String CLIP_NAMESPACE = "http://example.com/clip";
    private static final String FIRST_PREFIX_NAMESPACE = "http://example.com/first";
    private static final String SECOND_PREFIX_NAMESPACE = "http://example.com/second";

    @Test
    public void restoresDefaultNamespaceAfterNestedNamespaceOverride() {
        org.jsoup.nodes.Document input = new org.jsoup.nodes.Document("http://example.com/page");
        org.jsoup.nodes.Element root = input.appendElement("html");
        root.attr("xmlns", XHTML_NAMESPACE);

        org.jsoup.nodes.Element clip = root.appendElement("clip");
        clip.attr("xmlns", CLIP_NAMESPACE);
        clip.appendElement("inside");

        root.appendElement("after");

        org.w3c.dom.Document output = new W3CDom().fromJsoup(input);

        assertEquals(XHTML_NAMESPACE, output.getDocumentElement().getNamespaceURI());
        assertEquals(CLIP_NAMESPACE, elementNamed(output, "clip").getNamespaceURI());
        assertEquals(CLIP_NAMESPACE, elementNamed(output, "inside").getNamespaceURI());
        assertEquals(XHTML_NAMESPACE, elementNamed(output, "after").getNamespaceURI());
    }

    @Test
    public void restoresPrefixedNamespaceAfterNestedPrefixRebinding() {
        org.jsoup.nodes.Document input = new org.jsoup.nodes.Document("");
        org.jsoup.nodes.Element root = input.appendElement("root");
        root.attr("xmlns:p", FIRST_PREFIX_NAMESPACE);

        root.appendElement("p:before");

        org.jsoup.nodes.Element nested = root.appendElement("nested");
        nested.attr("xmlns:p", SECOND_PREFIX_NAMESPACE);
        nested.appendElement("p:inside");

        root.appendElement("p:after");

        org.w3c.dom.Document output = new W3CDom().fromJsoup(input);

        assertEquals(FIRST_PREFIX_NAMESPACE, elementNamed(output, "p:before").getNamespaceURI());
        assertEquals(SECOND_PREFIX_NAMESPACE, elementNamed(output, "p:inside").getNamespaceURI());
        assertEquals(FIRST_PREFIX_NAMESPACE, elementNamed(output, "p:after").getNamespaceURI());
    }

    @Test
    public void convertsElementsWithoutNamespaceDeclarationsToNoNamespace() {
        org.jsoup.nodes.Document input = new org.jsoup.nodes.Document("");
        org.jsoup.nodes.Element root = input.appendElement("root");
        root.appendElement("child");

        org.w3c.dom.Document output = new W3CDom().fromJsoup(input);

        assertNull(output.getDocumentElement().getNamespaceURI());
        assertNull(elementNamed(output, "child").getNamespaceURI());
    }

    @Test
    public void convertSetsDocumentUriAndUsesDeclaredNamespace() throws Exception {
        org.jsoup.nodes.Document input = new org.jsoup.nodes.Document("http://example.com/document");
        org.jsoup.nodes.Element root = input.appendElement("html");
        root.attr("xmlns", XHTML_NAMESPACE);
        root.appendElement("body");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        org.w3c.dom.Document output = factory.newDocumentBuilder().newDocument();

        new W3CDom().convert(input, output);

        assertEquals("http://example.com/document", output.getDocumentURI());
        assertEquals(XHTML_NAMESPACE, output.getDocumentElement().getNamespaceURI());
        assertEquals(XHTML_NAMESPACE, elementNamed(output, "body").getNamespaceURI());
    }

    private org.w3c.dom.Element elementNamed(org.w3c.dom.Document document, String name) {
        NodeList elements = document.getElementsByTagName(name);
        assertEquals("Expected exactly one element named " + name, 1, elements.getLength());
        return (org.w3c.dom.Element) elements.item(0);
    }
}
