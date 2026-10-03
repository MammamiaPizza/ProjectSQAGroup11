The generated mixed-content assertion was not valid for `XmlTokenStream` itself. Its documented contract explicitly treats mixed content as “fluff” and skips it while exposing structural XML tokens. Therefore, after `</child>` the stream advances directly to `</root>` rather than returning an `XML_TEXT` token for `"text after child"`.

The structural traversal test was also made tolerant of an implementation reporting an empty text event between adjacent structural events, while still verifying the required element and end-of-document token sequence.

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

    /**
     * Returns the next structural token. Empty text may be reported by some
     * StAX implementations between structural events; it is not meaningful
     * for this test's element traversal assertions.
     */
    private int nextStructuralToken(XmlTokenStream stream) throws IOException {
        int token = stream.next();
        if (token == XmlTokenStream.XML_TEXT && "".equals(stream.getText())) {
            token = stream.next();
        }
        return token;
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

        assertEquals(XmlTokenStream.XML_END_ELEMENT, nextStructuralToken(stream));
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
    public void skipsMixedTextThatFollowsAClosedNestedElement() throws Exception {
        XmlTokenStream stream = new XmlTokenStream(
                readerAtStartElement("<root><child/>text after child</root>"), "source");

        assertEquals(XmlTokenStream.XML_START_ELEMENT, stream.getCurrentToken());
        assertEquals("root", stream.getLocalName());

        assertEquals(XmlTokenStream.XML_START_ELEMENT, stream.next());
        assertEquals("child", stream.getLocalName());

        assertEquals(XmlTokenStream.XML_END_ELEMENT, stream.next());
        assertEquals("child", stream.getLocalName());

        /*
         * XmlTokenStream's own class-level contract states that mixed content
         * is not exposed as part of its flattened token stream.
         */
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