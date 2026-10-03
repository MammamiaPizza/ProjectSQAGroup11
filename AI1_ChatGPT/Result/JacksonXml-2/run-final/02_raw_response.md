```java
package com.fasterxml.jackson.dataformat.xml.deser;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonLocation;

import static org.junit.Assert.*;

public class XmlTokenStreamTest
{
    private XMLStreamReader readerAtStartElement(String xml) throws Exception {
        XMLStreamReader reader = XMLInputFactory.newInstance()
                .createXMLStreamReader(new StringReader(xml));
        while (reader.getEventType() != XMLStreamConstants.START_ELEMENT) {
            reader.next();
        }
        return reader;
    }

    @Test
    public void constructorRequiresReaderToPointToStartElement() throws Exception {
        XMLStreamReader reader = XMLInputFactory.newInstance()
                .createXMLStreamReader(new StringReader("<root/>"));

        assertEquals(XMLStreamConstants.START_DOCUMENT, reader.getEventType());

        try {
            new XmlTokenStream(reader, "source");
            fail("Expected constructor to reject a reader not positioned at START_ELEMENT");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("START_ELEMENT"));
        }
    }

    @Test
    public void exposesAttributesThenNestedElementsAndEndOfDocument() throws Exception {
        XmlTokenStream stream = new XmlTokenStream(
                readerAtStartElement("<root first='1' second='2'><child/></root>"), "source");

        assertEquals(XmlTokenStream.XML_START_ELEMENT, stream.getCurrentToken());
        assertEquals("root", stream.getLocalName());
        assertTrue(stream.hasAttributes());

        assertEquals(XmlTokenStream.XML_ATTRIBUTE_NAME, stream.next());
        assertEquals("first", stream.getLocalName());
        assertEquals("1", stream.getText());

        assertEquals(XmlTokenStream.XML_ATTRIBUTE_VALUE, stream.next());
        assertEquals("1", stream.getText());

        assertEquals(XmlTokenStream.XML_ATTRIBUTE_NAME, stream.next());
        assertEquals("second", stream.getLocalName());
        assertEquals("2", stream.getText());

        assertEquals(XmlTokenStream.XML_ATTRIBUTE_VALUE, stream.next());
        assertEquals("2", stream.getText());

        assertEquals(XmlTokenStream.XML_START_ELEMENT, stream.next());
        assertEquals("child", stream.getLocalName());
        assertFalse(stream.hasAttributes());

        assertEquals(XmlTokenStream.XML_END_ELEMENT, stream.next());
        assertEquals("child", stream.getLocalName());

        assertEquals(XmlTokenStream.XML_END_ELEMENT, stream.next());
        assertEquals("root", stream.getLocalName());

        assertEquals(XmlTokenStream.XML_END, stream.next());
        assertEquals(XmlTokenStream.XML_END, stream.next());
    }

    @Test
    public void collectsCharacterDataAndCDataWhileIgnoringCommentsAndProcessingInstructions()
            throws Exception {
        XmlTokenStream stream = new XmlTokenStream(
                readerAtStartElement("<root>before<![CDATA[-cdata-]]><!-- ignored --><?pi value?>after</root>"),
                "source");

        assertEquals(XmlTokenStream.XML_TEXT, stream.next());
        assertEquals("before-cdata-after", stream.getText());

        assertEquals(XmlTokenStream.XML_END_ELEMENT, stream.next());
        assertEquals("root", stream.getLocalName());
    }

    @Test
    public void returnsTextThatFollowsAClosedNestedElementBeforeParentEnd() throws Exception {
        XmlTokenStream stream = new XmlTokenStream(
                readerAtStartElement("<root><child/>text after child</root>"), "source");

        assertEquals(XmlTokenStream.XML_START_ELEMENT, stream.getCurrentToken());
        assertEquals("root", stream.getLocalName());

        assertEquals(XmlTokenStream.XML_START_ELEMENT, stream.next());
        assertEquals("child", stream.getLocalName());

        assertEquals(XmlTokenStream.XML_END_ELEMENT, stream.next());
        assertEquals("child", stream.getLocalName());

        assertEquals("Text after a nested element must not be discarded",
                XmlTokenStream.XML_TEXT, stream.next());
        assertEquals("text after child", stream.getText());

        assertEquals(XmlTokenStream.XML_END_ELEMENT, stream.next());
        assertEquals("root", stream.getLocalName());
    }

    @Test
    public void skipAttributesAfterAttributeNameSkipsRemainingAttributeTokens() throws Exception {
        XmlTokenStream stream = new XmlTokenStream(
                readerAtStartElement("<root first='1' second='2'><child/></root>"), "source");

        assertEquals(XmlTokenStream.XML_ATTRIBUTE_NAME, stream.next());
        assertEquals("first", stream.getLocalName());

        stream.skipAttributes();

        assertEquals(XmlTokenStream.XML_START_ELEMENT, stream.next());
        assertEquals("child", stream.getLocalName());
        assertFalse(stream.hasAttributes());
    }

    @Test
    public void convertToStringReturnsTextAndPositionsStreamForEndElement() throws Exception {
        XmlTokenStream stream = new XmlTokenStream(
                readerAtStartElement("<root attribute='value'>contents</root>"), "source");

        assertEquals(XmlTokenStream.XML_ATTRIBUTE_NAME, stream.next());
        assertEquals("attribute", stream.getLocalName());

        assertEquals("contents", stream.convertToString());
        assertEquals(XmlTokenStream.XML_TEXT, stream.getCurrentToken());
        assertEquals("contents", stream.getText());
        assertEquals("root", stream.getLocalName());

        assertEquals(XmlTokenStream.XML_END_ELEMENT, stream.next());
        assertEquals("root", stream.getLocalName());
    }

    @Test
    public void convertToStringTreatsAnEmptyElementAsEmptyText() throws Exception {
        XmlTokenStream stream = new XmlTokenStream(
                readerAtStartElement("<root attribute='value'/>"), "source");

        assertEquals(XmlTokenStream.XML_ATTRIBUTE_NAME, stream.next());

        assertEquals("", stream.convertToString());
        assertEquals(XmlTokenStream.XML_TEXT, stream.getCurrentToken());
        assertEquals("", stream.getText());

        assertEquals(XmlTokenStream.XML_END_ELEMENT, stream.next());
        assertEquals("root", stream.getLocalName());
    }

    @Test
    public void convertToStringIsNotApplicableBeforeAnAttributeNameToken() throws Exception {
        XmlTokenStream stream = new XmlTokenStream(
                readerAtStartElement("<root attribute='value'>contents</root>"), "source");

        assertNull(stream.convertToString());
        assertEquals(XmlTokenStream.XML_START_ELEMENT, stream.getCurrentToken());
        assertEquals("root", stream.getLocalName());
    }

    @Test
    public void skipEndElementConsumesExpectedEndElement() throws Exception {
        XmlTokenStream stream = new XmlTokenStream(readerAtStartElement("<root/>"), "source");

        stream.skipEndElement();

        assertEquals(XmlTokenStream.XML_END_ELEMENT, stream.getCurrentToken());
        assertEquals("root", stream.getLocalName());
        assertEquals(XmlTokenStream.XML_END, stream.next());
    }

    @Test
    public void skipEndElementFailsWhenTheNextTokenIsNotAnEndElement() throws Exception {
        XmlTokenStream stream = new XmlTokenStream(
                readerAtStartElement("<root><child/></root>"), "source");

        try {
            stream.skipEndElement();
            fail("Expected IOException when next token is a START_ELEMENT");
        } catch (IOException e) {
            assertTrue(e.getMessage().contains("Expected END_ELEMENT"));
        }

        assertEquals(XmlTokenStream.XML_START_ELEMENT, stream.getCurrentToken());
        assertEquals("child", stream.getLocalName());
    }

    @Test
    public void exposesNamespaceInformationForElementsAndAttributes() throws Exception {
        XmlTokenStream stream = new XmlTokenStream(
                readerAtStartElement("<root xmlns='urn:root' xmlns:p='urn:attribute' p:value='x'/>"),
                "source");

        assertEquals("root", stream.getLocalName());
        assertEquals("urn:root", stream.getNamespaceURI());

        assertEquals(XmlTokenStream.XML_ATTRIBUTE_NAME, stream.next());
        assertEquals("value", stream.getLocalName());
        assertEquals("urn:attribute", stream.getNamespaceURI());
        assertEquals("x", stream.getText());
    }

    @Test
    public void locationsRetainTheProvidedSourceReference() throws Exception {
        Object source = new Object();
        XmlTokenStream stream = new XmlTokenStream(readerAtStartElement("<root/>"), source);

        JsonLocation current = stream.getCurrentLocation();
        JsonLocation token = stream.getTokenLocation();

        assertSame(source, current.getSourceRef());
        assertSame(source, token.getSourceRef());
    }
}
```

Test coverage summary:

- `constructorRequiresReaderToPointToStartElement`: validates constructor precondition and invalid reader positioning.
- `exposesAttributesThenNestedElementsAndEndOfDocument`: covers normal element, attribute-name/value sequencing, nested elements, closing elements, and stable `XML_END`.
- `collectsCharacterDataAndCDataWhileIgnoringCommentsAndProcessingInstructions`: covers text aggregation, CDATA handling, comments, and processing instructions.
- `returnsTextThatFollowsAClosedNestedElementBeforeParentEnd`: targets the reported mixed-content defect by requiring text after a nested element to be emitted before the parent end token.
- `skipAttributesAfterAttributeNameSkipsRemainingAttributeTokens`: covers protected attribute-skipping behavior from the attribute-name state.
- `convertToStringReturnsTextAndPositionsStreamForEndElement`: covers attribute-induced scalar conversion with non-empty text.
- `convertToStringTreatsAnEmptyElementAsEmptyText`: covers the documented empty-element conversion boundary case.
- `convertToStringIsNotApplicableBeforeAnAttributeNameToken`: covers the non-applicable branch returning `null`.
- `skipEndElementConsumesExpectedEndElement`: verifies successful end-element consumption.
- `skipEndElementFailsWhenTheNextTokenIsNotAnEndElement`: verifies exceptional behavior when the next token is a start element.
- `exposesNamespaceInformationForElementsAndAttributes`: verifies namespace propagation for elements and attributes.
- `locationsRetainTheProvidedSourceReference`: verifies location conversion preserves the configured source reference.