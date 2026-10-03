```java
package com.fasterxml.jackson.dataformat.xml.ser;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
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

    public static class FailingValue
    {
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
    public void testConfiguredRootNameOverridesAnnotatedRootNameForNonNullValue()
            throws Exception
    {
        XmlMapper mapper = new XmlMapper();

        String xml = mapper.writer()
                .withRootName("customPerson")
                .writeValueAsString(new Person("Ada"));

        assertEquals("<customPerson><name>Ada</name></customPerson>", xml);
    }

    @Test
    public void testTypedWriterUsesAnnotatedRootNameForNonNullValue() throws Exception
    {
        XmlMapper mapper = new XmlMapper();

        String xml = mapper.writerFor(Person.class)
                .writeValueAsString(new Person("Ada"));

        assertEquals("<person><name>Ada</name></person>", xml);
    }

    @Test
    public void testConfiguredRootNameIsUsedForIndexedValue() throws Exception
    {
        XmlMapper mapper = new XmlMapper();

        String xml = mapper.writer()
                .withRootName("items")
                .writeValueAsString(Arrays.asList("first", "second"));

        assertEquals("<items><item>first</item><item>second</item></items>", xml);
    }

    @Test
    public void testExplicitSerializerIsUsedWithTypedSerializeValueOverload()
            throws Exception
    {
        XmlMapper mapper = new XmlMapper();
        XmlSerializerProvider provider = newProvider(mapper);
        JavaType type = mapper.constructType(Person.class);

        StringWriter output = new StringWriter();
        JsonGenerator generator = mapper.getFactory().createGenerator(output);
        try {
            provider.serializeValue(generator, new Person("ignored"), type,
                    new JsonSerializer<Object>() {
                        @Override
                        public void serialize(Object value, JsonGenerator gen,
                                SerializerProvider serializers) throws IOException {
                            gen.writeString("custom");
                        }
                    });
        } finally {
            generator.close();
        }

        assertEquals("<person>custom</person>", output.toString());
    }

    @Test
    public void testRuntimeExceptionFromExplicitSerializerIsWrappedWithCause()
            throws Exception
    {
        XmlMapper mapper = new XmlMapper();
        XmlSerializerProvider provider = newProvider(mapper);
        JavaType type = mapper.constructType(FailingValue.class);
        IllegalStateException problem = new IllegalStateException("broken serializer");

        StringWriter output = new StringWriter();
        JsonGenerator generator = mapper.getFactory().createGenerator(output);
        try {
            provider.serializeValue(generator, new FailingValue(), type,
                    new JsonSerializer<Object>() {
                        @Override
                        public void serialize(Object value, JsonGenerator gen,
                                SerializerProvider serializers) {
                            throw problem;
                        }
                    });
            fail("Expected JsonMappingException");
        } catch (JsonMappingException e) {
            assertTrue(e.getMessage().contains("broken serializer"));
            assertSame(problem, e.getCause());
        } finally {
            generator.close();
        }
    }

    @Test
    public void testIOExceptionFromExplicitSerializerIsNotWrapped() throws Exception
    {
        XmlMapper mapper = new XmlMapper();
        XmlSerializerProvider provider = newProvider(mapper);
        JavaType type = mapper.constructType(FailingValue.class);
        IOException problem = new IOException("stream failure");

        StringWriter output = new StringWriter();
        JsonGenerator generator = mapper.getFactory().createGenerator(output);
        try {
            provider.serializeValue(generator, new FailingValue(), type,
                    new JsonSerializer<Object>() {
                        @Override
                        public void serialize(Object value, JsonGenerator gen,
                                SerializerProvider serializers) throws IOException {
                            throw problem;
                        }
                    });
            fail("Expected original IOException");
        } catch (IOException e) {
            assertSame(problem, e);
        } finally {
            generator.close();
        }
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

    private XmlSerializerProvider newProvider(XmlMapper mapper)
    {
        XmlSerializerProvider prototype =
                (XmlSerializerProvider) mapper.getSerializerProvider();
        return (XmlSerializerProvider) prototype.createInstance(
                mapper.getSerializationConfig(), mapper.getSerializerFactory());
    }
}
```

### Added test coverage targets

- `testConfiguredRootNameOverridesAnnotatedRootNameForNonNullValue`
  - Covers `_rootNameFromConfig()` for a configured root name during ordinary non-null serialization.
  - Verifies configured root names take precedence over annotation-derived names.
  - Complements the bug-related null-root tests by exercising configured dynamic root names for non-null values.

- `testTypedWriterUsesAnnotatedRootNameForNonNullValue`
  - Exercises typed serialization through `writerFor(Person.class)`.
  - Targets the `serializeValue(JsonGenerator, Object, JavaType)` path and root-name lookup using `JavaType`.

- `testConfiguredRootNameIsUsedForIndexedValue`
  - Covers indexed-type detection through `TypeUtil.isIndexedType(...)`.
  - Exercises the array/collection branch that calls `_startRootArray(...)` and the later `writeEndObject()` branch.
  - Verifies configured root names are retained for collection serialization.

- `testExplicitSerializerIsUsedWithTypedSerializeValueOverload`
  - Directly exercises `serializeValue(JsonGenerator, Object, JavaType, JsonSerializer<Object>)`.
  - Covers the branch where the supplied serializer is non-null, avoiding the fallback call to `findTypedValueSerializer(...)`.
  - Verifies that XML root initialization and annotation-based root lookup still occur when an explicit serializer is supplied.

- `testRuntimeExceptionFromExplicitSerializerIsWrappedWithCause`
  - Covers the non-`IOException` exception handling branch in the explicit-serializer overload.
  - Verifies runtime exceptions are wrapped as `JsonMappingException` and preserve the original cause and message.

- `testIOExceptionFromExplicitSerializerIsNotWrapped`
  - Covers the `IOException` exception handling branch in the explicit-serializer overload.
  - Verifies the provider’s documented behavior of propagating the exact original `IOException` rather than wrapping it.