package com.fasterxml.jackson.dataformat.xml.ser;

import java.io.StringWriter;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class XmlSerializerProviderTest
{
    @JacksonXmlRootElement(localName = "person")
    public static class Person
    {
        private final String name;

        public Person(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Test
    public void testNullValueUsesDefaultNullRootName() throws Exception
    {
        XmlMapper mapper = new XmlMapper();

        String xml = mapper.writeValueAsString(null);

        assertEquals("<null/>", xml);
    }

    @Test
    public void testConfiguredRootNameIsUsedForNullValue() throws Exception
    {
        XmlMapper mapper = new XmlMapper();

        String xml = mapper.writer()
                .withRootName("rudy")
                .writeValueAsString(null);

        assertEquals("<rudy/>", xml);
    }

    @Test
    public void testConfiguredRootNameIsUsedForTypedNullValue() throws Exception
    {
        XmlMapper mapper = new XmlMapper();

        String xml = mapper.writerFor(String.class)
                .withRootName("typedNull")
                .writeValueAsString(null);

        assertEquals("<typedNull/>", xml);
    }

    @Test
    public void testAnnotatedRootNameIsUsedForNonNullValue() throws Exception
    {
        XmlMapper mapper = new XmlMapper();

        String xml = mapper.writeValueAsString(new Person("Ada"));

        assertEquals("<person><name>Ada</name></person>", xml);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConvertValueUsesTokenBufferGenerator() throws Exception
    {
        XmlMapper mapper = new XmlMapper();

        Map<String, Object> converted = mapper.convertValue(new Person("Ada"), Map.class);

        assertEquals("Ada", converted.get("name"));
        assertEquals(1, converted.size());
    }

    @Test
    public void testNonXmlAndNonTokenBufferGeneratorIsRejected() throws Exception
    {
        XmlMapper mapper = new XmlMapper();
        XmlSerializerProvider prototype =
                (XmlSerializerProvider) mapper.getSerializerProvider();
        DefaultSerializerProvider provider = prototype.createInstance(
                mapper.getSerializationConfig(), mapper.getSerializerFactory());

        assertTrue(provider instanceof XmlSerializerProvider);
        assertNotSame(prototype, provider);

        StringWriter output = new StringWriter();
        JsonGenerator generator = new JsonFactory().createGenerator(output);
        try {
            ((XmlSerializerProvider) provider).serializeValue(generator, "value");
            fail("Expected JsonMappingException for a non-XML generator");
        } catch (JsonMappingException e) {
            assertTrue(e.getMessage().contains(
                    "XmlMapper does not with generators of type other than ToXmlGenerator"));
        } finally {
            generator.close();
        }
    }
}
