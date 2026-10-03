package com.fasterxml.jackson.dataformat.xml.ser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import static org.junit.Assert.assertEquals;

public class ToXmlGeneratorBinaryStreamTest
{
    public static class TestPojo {
        public InputStream field;

        public TestPojo() { }

        public TestPojo(byte[] bytes) {
            field = new ByteArrayInputStream(bytes);
        }
    }

    private final XmlMapper mapper = new XmlMapper();

    @Test
    public void serializesEmptyBinaryStream() throws Exception {
        assertBinaryValue(new byte[0], "");
    }

    @Test
    public void serializesOneByteBinaryStreamWithDoublePadding() throws Exception {
        assertBinaryValue(new byte[] { 1 }, "AQ==");
    }

    @Test
    public void serializesTwoByteBinaryStreamWithSinglePadding() throws Exception {
        assertBinaryValue(new byte[] { 1, 2 }, "AQI=");
    }

    @Test
    public void serializesThreeByteBinaryStreamWithoutPadding() throws Exception {
        assertBinaryValue(new byte[] { 1, 2, 3 }, "AQID");
    }

    @Test
    public void serializesFourByteBinaryStreamAcrossBase64Groups() throws Exception {
        assertBinaryValue(new byte[] { 1, 2, 3, 4 }, "AQIDBA==");
    }

    private void assertBinaryValue(byte[] input, String expectedBase64) throws Exception {
        String xml = mapper.writeValueAsString(new TestPojo(input));

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document document = factory.newDocumentBuilder()
                .parse(new InputSource(new StringReader(xml)));

        NodeList fields = document.getElementsByTagNameNS("*", "field");
        if (fields.getLength() == 0) {
            fields = document.getElementsByTagName("field");
        }

        assertEquals(1, fields.getLength());
        assertEquals(expectedBase64, fields.item(0).getTextContent());
    }
}
