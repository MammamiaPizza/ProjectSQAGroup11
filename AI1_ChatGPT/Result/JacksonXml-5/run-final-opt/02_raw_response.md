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
}