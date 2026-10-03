package com.fasterxml.jackson.dataformat.xml;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

public class XmlSerializerProviderCopyTest
{
    @JacksonXmlRootElement(localName = "AnnotatedName")
    public static class Pojo282 {
        public int a;

        public Pojo282() { }

        public Pojo282(int value) {
            a = value;
        }
    }

    @Test
    public void testAnnotatedRootNameIsUsedNormally() throws Exception {
        XmlMapper mapper = new XmlMapper();

        assertEquals("<AnnotatedName><a>3</a></AnnotatedName>",
                mapper.writeValueAsString(new Pojo282(3)));
    }

    @Test
    public void testCopiedMapperDoesNotReuseAnnotatedRootNameLookup() throws Exception {
        XmlMapper original = new XmlMapper();

        assertEquals("<AnnotatedName><a>3</a></AnnotatedName>",
                original.writeValueAsString(new Pojo282(3)));

        XmlMapper copied = original.copy();
        copied.disable(MapperFeature.USE_ANNOTATIONS);

        assertEquals("<Pojo282><a>3</a></Pojo282>",
                copied.writeValueAsString(new Pojo282(3)));

        assertEquals("<AnnotatedName><a>3</a></AnnotatedName>",
                original.writeValueAsString(new Pojo282(3)));
    }

@org.junit.Test
public void testNullValueUsesNullRootElement() throws Exception {
    String xml = new com.fasterxml.jackson.dataformat.xml.XmlMapper().writeValueAsString(null);

    org.junit.Assert.assertEquals("<null/>", xml);
}

@org.junit.Test
public void testRootArrayUsesItemElements() throws Exception {
    String xml = new com.fasterxml.jackson.dataformat.xml.XmlMapper()
            .writeValueAsString(new String[] { "a", "b" });

    org.junit.Assert.assertTrue(xml.contains("<item>a</item>"));
    org.junit.Assert.assertTrue(xml.contains("<item>b</item>"));
}

@org.junit.Test
public void testSerializeValueRejectsNonXmlGenerator() throws Exception {
    com.fasterxml.jackson.dataformat.xml.ser.XmlSerializerProvider provider =
            new com.fasterxml.jackson.dataformat.xml.ser.XmlSerializerProvider(null);
    java.io.StringWriter output = new java.io.StringWriter();
    com.fasterxml.jackson.core.JsonGenerator generator =
            new com.fasterxml.jackson.core.JsonFactory().createGenerator(output);
    try {
        provider.serializeValue(generator, "value");
        org.junit.Assert.fail("Expected JsonMappingException");
    } catch (com.fasterxml.jackson.databind.JsonMappingException e) {
        org.junit.Assert.assertTrue(e.getMessage().contains(
                "XmlMapper does not with generators of type other than ToXmlGenerator"));
    } finally {
        generator.close();
    }
}
}
