package org.jsoup.helper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class W3CDomInvalidAttributeNamesTest {

    @Test
    public void convertsElementWithDigitLeadingAttributeName() {
        org.jsoup.nodes.Document input = Jsoup.parse("<div id='target'>text</div>");
        Element source = input.select("div").first();
        source.attr("1invalid", "value");

        assertTrue(source.hasAttr("1invalid"));

        Document output = new W3CDom().fromJsoup(input);

        NodeList divs = output.getElementsByTagName("div");
        assertEquals(1, divs.getLength());
        assertEquals("target", ((org.w3c.dom.Element) divs.item(0)).getAttribute("id"));
    }

    @Test
    public void preservesValidAttributesWhenInvalidAttributeIsAlsoPresent() {
        org.jsoup.nodes.Document input = Jsoup.parse("<div></div>");
        Element source = input.select("div").first();
        source.attr("id", "target");
        source.attr("data-state", "active");
        source.attr("1notXmlName", "ignoredForXml");

        Document output = new W3CDom().fromJsoup(input);

        org.w3c.dom.Element converted = (org.w3c.dom.Element) output.getElementsByTagName("div").item(0);
        assertNotNull(converted);
        assertEquals("target", converted.getAttribute("id"));
        assertEquals("active", converted.getAttribute("data-state"));
    }

    @Test
    public void convertAcceptsAttributesBeginningWithXmlIllegalPunctuation() throws Exception {
        org.jsoup.nodes.Document input = Jsoup.parse("<div title='kept'></div>");
        Element source = input.select("div").first();
        source.attr("-leadingHyphen", "one");
        source.attr(".leadingDot", "two");

        Document output = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        new W3CDom().convert(input, output);

        org.w3c.dom.Element converted = (org.w3c.dom.Element) output.getElementsByTagName("div").item(0);
        assertNotNull(converted);
        assertEquals("kept", converted.getAttribute("title"));
    }

@Test
public void setsDocumentUriFromNonBlankSourceLocation() {
    org.jsoup.nodes.Document source = org.jsoup.Jsoup.parse("<p>content</p>", "http://example.com/source");
    org.w3c.dom.Document converted = new W3CDom().fromJsoup(source);

    assertEquals("http://example.com/source", converted.getDocumentURI());
}

@Test
public void doesNotOverwriteExistingDocumentUriWhenSourceLocationIsBlank() throws Exception {
    org.jsoup.nodes.Document source = org.jsoup.Jsoup.parse("<p>content</p>");
    org.w3c.dom.Document target = javax.xml.parsers.DocumentBuilderFactory.newInstance()
        .newDocumentBuilder().newDocument();
    target.setDocumentURI("http://example.com/existing");

    new W3CDom().convert(source, target);

    assertEquals("http://example.com/existing", target.getDocumentURI());
}

@Test
public void serializesW3cDocumentToString() throws Exception {
    org.w3c.dom.Document document = javax.xml.parsers.DocumentBuilderFactory.newInstance()
        .newDocumentBuilder().newDocument();
    org.w3c.dom.Element root = document.createElement("root");
    root.appendChild(document.createTextNode("serialized"));
    document.appendChild(root);

    String serialized = new W3CDom().asString(document);

    assertTrue(serialized.contains("<root>serialized</root>"));
}
}
