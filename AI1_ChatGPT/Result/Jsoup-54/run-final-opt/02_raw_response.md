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
}