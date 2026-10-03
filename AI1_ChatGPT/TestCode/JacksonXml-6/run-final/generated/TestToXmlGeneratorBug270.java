package com.fasterxml.jackson.dataformat.xml.ser;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestToXmlGeneratorBug270
{
    public static class BinaryPojo {
        public ByteArrayInputStream field;

        public BinaryPojo() { }

        public BinaryPojo(byte[] value) {
            field = new ByteArrayInputStream(value);
        }
    }

    @Test
    public void testBinaryInputStreamWithZeroBytes() throws Exception
    {
        verifyInputStreamSerialization(new byte[0]);
    }

    @Test
    public void testBinaryInputStreamWithOneByte() throws Exception
    {
        verifyInputStreamSerialization(new byte[] { 0 });
    }

    @Test
    public void testBinaryInputStreamWithTwoBytes() throws Exception
    {
        verifyInputStreamSerialization(new byte[] { 0, 0 });
    }

    @Test
    public void testBinaryInputStreamWithThreeBytes() throws Exception
    {
        verifyInputStreamSerialization(new byte[] { 0, 0, 0 });
    }

    @Test
    public void testBinaryInputStreamWithFourBytes() throws Exception
    {
        verifyInputStreamSerialization(new byte[] { 0, 0, 0, 0 });
    }

    @Test
    public void testNullBinaryInputStreamIsWrittenAsEmptyElement() throws Exception
    {
        BinaryPojo pojo = new BinaryPojo();
        pojo.field = null;

        String xml = new XmlMapper().writeValueAsString(pojo);
        Document document = parse(xml);
        NodeList fields = document.getElementsByTagName("field");

        assertEquals(1, fields.getLength());
        assertEquals("", fields.item(0).getTextContent());
    }

    @Test
    public void testBinaryAttributeUsesOnlyRequestedByteRange() throws Exception
    {
        StringWriter output = new StringWriter();
        ToXmlGenerator generator = (ToXmlGenerator) new XmlFactory().createGenerator(output);

        generator.setNextName(new QName("root"));
        generator.writeStartObject();
        generator.writeFieldName("payload");
        generator.setNextIsAttribute(true);
        generator.writeBinary(Base64Variants.getDefaultVariant(),
                new byte[] { 12, 0, 34 }, 1, 1);
        generator.writeEndObject();
        generator.close();

        Document document = parse(output.toString());
        assertEquals(Base64Variants.getDefaultVariant().encode(new byte[] { 0 }),
                document.getDocumentElement().getAttribute("payload"));
    }

    @Test
    public void testSetNextNameIfMissingPreservesExistingName() throws Exception
    {
        StringWriter output = new StringWriter();
        ToXmlGenerator generator = (ToXmlGenerator) new XmlFactory().createGenerator(output);

        assertTrue(generator.setNextNameIfMissing(new QName("first")));
        assertFalse(generator.setNextNameIfMissing(new QName("second")));

        generator.writeString("value");
        generator.close();

        Document document = parse(output.toString());
        assertEquals("first", document.getDocumentElement().getNodeName());
        assertEquals("value", document.getDocumentElement().getTextContent());
    }

    @Test
    public void testWritingValueWithoutElementNameFails() throws Exception
    {
        ToXmlGenerator generator = (ToXmlGenerator) new XmlFactory().createGenerator(new StringWriter());

        try {
            generator.writeString("value");
            fail("Writing an XML value without an element or attribute name must fail");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("No element/attribute name specified"));
        }
    }

    @Test
    public void testXmlFeatureConfigurationAndOverride() throws Exception
    {
        ToXmlGenerator generator = (ToXmlGenerator) new XmlFactory().createGenerator(new StringWriter());

        assertFalse(generator.isEnabled(ToXmlGenerator.Feature.WRITE_XML_DECLARATION));
        assertFalse(generator.isEnabled(ToXmlGenerator.Feature.WRITE_XML_1_1));

        assertEquals(generator, generator.enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION));
        assertTrue(generator.isEnabled(ToXmlGenerator.Feature.WRITE_XML_DECLARATION));

        assertEquals(generator, generator.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, false));
        assertFalse(generator.isEnabled(ToXmlGenerator.Feature.WRITE_XML_DECLARATION));

        generator.overrideFormatFeatures(ToXmlGenerator.Feature.WRITE_XML_1_1.getMask(),
                ToXmlGenerator.Feature.WRITE_XML_1_1.getMask());
        assertTrue(generator.isEnabled(ToXmlGenerator.Feature.WRITE_XML_1_1));

        generator.close();
    }

    private void verifyInputStreamSerialization(byte[] data) throws Exception
    {
        String xml = new XmlMapper().writeValueAsString(new BinaryPojo(data));
        Document document = parse(xml);
        NodeList fields = document.getElementsByTagName("field");

        assertEquals(1, fields.getLength());
        assertEquals(Base64Variants.getDefaultVariant().encode(data),
                fields.item(0).getTextContent());
    }

    private Document parse(String xml) throws Exception
    {
        return DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new org.xml.sax.InputSource(new StringReader(xml)));
    }
}
