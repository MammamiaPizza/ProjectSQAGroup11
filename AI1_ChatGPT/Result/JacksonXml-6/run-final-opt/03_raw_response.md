package com.fasterxml.jackson.dataformat.xml.ser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
        try {
            mapper.writeValueAsString(new TestPojo(input));
            fail("Expected binary InputStream serialization to be unsupported");
        } catch (JsonMappingException e) {
            assertTrue(e.getMessage(),
                    e.getMessage().contains("Operation not supported by generator of type "
                            + ToXmlGenerator.class.getName()));
            assertTrue(e.getMessage(), e.getMessage().contains("TestPojo[\"field\"]"));
        }
    }
}