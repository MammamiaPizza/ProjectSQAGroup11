package com.fasterxml.jackson.dataformat.xml.deser;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class XmlTokenStreamMixedContentTest
{
    private XmlTokenStream streamFor(String xml) throws Exception {
        XMLStreamReader reader = XMLInputFactory.newInstance()
                .createXMLStreamReader(new StringReader(xml));
        return new XmlTokenStream(reader, xml);
    }

    private void assertNext(XmlTokenStream stream, int expectedToken) throws Exception {
        assertEquals(expectedToken, stream.next());
        assertEquals(expectedToken, stream.getCurrentToken());
    }

    @Test
    public void mixedTextBeforeAndAfterEmptyChildIsPreservedInOrder() throws Exception {
        XmlTokenStream stream = streamFor("<root>left<child/>right</root>");

        assertNext(stream, XmlTokenStream.XML_START_ELEMENT);
        assertEquals("root", stream.getLocalName());

        assertNext(stream, XmlTokenStream.XML_TEXT);
        assertEquals("left", stream.getText());

        assertNext(stream, XmlTokenStream.XML_START_ELEMENT);
        assertEquals("child", stream.getLocalName());

        assertNext(stream, XmlTokenStream.XML_END_ELEMENT);
        assertEquals("child", stream.getLocalName());

        assertNext(stream, XmlTokenStream.XML_TEXT);
        assertEquals("right", stream.getText());

        assertNext(stream, XmlTokenStream.XML_END_ELEMENT);
        assertEquals("root", stream.getLocalName());

        assertNext(stream, XmlTokenStream.XML_END);
    }

    @Test
    public void mixedTextAroundNonEmptyChildRetainsAllTextTokens() throws Exception {
        XmlTokenStream stream = streamFor("<root>before<child>inside</child>after</root>");

        assertNext(stream, XmlTokenStream.XML_START_ELEMENT);
        assertEquals("root", stream.getLocalName());

        assertNext(stream, XmlTokenStream.XML_TEXT);
        assertEquals("before", stream.getText());

        assertNext(stream, XmlTokenStream.XML_START_ELEMENT);
        assertEquals("child", stream.getLocalName());

        assertNext(stream, XmlTokenStream.XML_TEXT);
        assertEquals("inside", stream.getText());

        assertNext(stream, XmlTokenStream.XML_END_ELEMENT);
        assertEquals("child", stream.getLocalName());

        assertNext(stream, XmlTokenStream.XML_TEXT);
        assertEquals("after", stream.getText());

        assertNext(stream, XmlTokenStream.XML_END_ELEMENT);
        assertEquals("root", stream.getLocalName());
        assertNext(stream, XmlTokenStream.XML_END);
    }

    @Test
    public void adjacentCharacterAndCdataSegmentsAreCollectedAsOneTextValue() throws Exception {
        XmlTokenStream stream = streamFor("<root>one<!-- ignored --><![CDATA[two]]>three</root>");

        assertNext(stream, XmlTokenStream.XML_START_ELEMENT);
        assertEquals("root", stream.getLocalName());

        assertNext(stream, XmlTokenStream.XML_TEXT);
        assertEquals("onetwothree", stream.getText());

        assertNext(stream, XmlTokenStream.XML_END_ELEMENT);
        assertEquals("root", stream.getLocalName());
        assertNext(stream, XmlTokenStream.XML_END);
    }

    @Test
    public void emptyElementsDoNotProduceSyntheticTextTokens() throws Exception {
        XmlTokenStream stream = streamFor("<root><child/></root>");

        assertNext(stream, XmlTokenStream.XML_START_ELEMENT);
        assertEquals("root", stream.getLocalName());
        assertFalse(stream.hasAttributes());

        assertNext(stream, XmlTokenStream.XML_START_ELEMENT);
        assertEquals("child", stream.getLocalName());

        assertNext(stream, XmlTokenStream.XML_END_ELEMENT);
        assertEquals("child", stream.getLocalName());

        assertNext(stream, XmlTokenStream.XML_END_ELEMENT);
        assertEquals("root", stream.getLocalName());

        assertNext(stream, XmlTokenStream.XML_END);
    }

    @Test
    public void attributesAreReturnedBeforeFollowingText() throws Exception {
        XmlTokenStream stream = streamFor("<root id=\"7\">value</root>");

        assertNext(stream, XmlTokenStream.XML_START_ELEMENT);
        assertEquals("root", stream.getLocalName());
        assertTrue(stream.hasAttributes());

        assertNext(stream, XmlTokenStream.XML_ATTRIBUTE_NAME);
        assertEquals("id", stream.getLocalName());

        assertNext(stream, XmlTokenStream.XML_ATTRIBUTE_VALUE);
        assertEquals("7", stream.getText());

        assertNext(stream, XmlTokenStream.XML_TEXT);
        assertEquals("value", stream.getText());

        assertNext(stream, XmlTokenStream.XML_END_ELEMENT);
        assertEquals("root", stream.getLocalName());
        assertNext(stream, XmlTokenStream.XML_END);
    }

    @Test
    public void elementNamesAndNamespacesAreAvailableForStartAndEndTokens() throws Exception {
        XmlTokenStream stream = streamFor("<root xmlns=\"urn:test\"><child/></root>");

        assertNext(stream, XmlTokenStream.XML_START_ELEMENT);
        assertEquals("root", stream.getLocalName());
        assertEquals("urn:test", stream.getNamespaceURI());

        assertNext(stream, XmlTokenStream.XML_START_ELEMENT);
        assertEquals("child", stream.getLocalName());
        assertEquals("urn:test", stream.getNamespaceURI());

        assertNext(stream, XmlTokenStream.XML_END_ELEMENT);
        assertEquals("child", stream.getLocalName());
        assertEquals("urn:test", stream.getNamespaceURI());

        assertNext(stream, XmlTokenStream.XML_END_ELEMENT);
        assertEquals("root", stream.getLocalName());
        assertEquals("urn:test", stream.getNamespaceURI());

        assertNext(stream, XmlTokenStream.XML_END);
    }

    @Test(expected = IOException.class)
    public void skipEndElementRejectsAFollowingStartElement() throws Exception {
        XmlTokenStream stream = streamFor("<root><child/></root>");

        assertNext(stream, XmlTokenStream.XML_START_ELEMENT);
        stream.skipEndElement();
    }
}
