```java
package com.fasterxml.jackson.dataformat.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.dataformat.xml.ser.XmlSerializerProvider;
import com.fasterxml.jackson.dataformat.xml.util.XmlRootNameLookup;

/**
 * Tests XML root-name handling performed by XmlSerializerProvider.
 */
public class XmlSerializerProviderTest
{
    @Test
    public void testCopyDoesNotReuseRootNameLookupFromOriginalMapper() throws Exception
    {
        XmlMapper original = new XmlMapper();
        original.addMixIn(Pojo282.class, AnnotatedRootNameMixIn.class);

        /*
         * Serialize with the original mapper first so that its root-name lookup
         * contains the mix-in-derived name.
         */
        assertEquals("<AnnotatedName><a>3</a></AnnotatedName>",
                original.writeValueAsString(new Pojo282()));

        /*
         * A copied mapper must resolve root names using its own configuration.
         * Removing the mix-in changes the expected root element back to the
         * default class-derived name.
         */
        XmlMapper copy = original.copy();
        copy.addMixIn(Pojo282.class, null);

        assertEquals("<Pojo282><a>3</a></Pojo282>",
                copy.writeValueAsString(new Pojo282()));
    }

    @Test
    public void testSerializeNullUsesDefaultAndConfiguredRootNames() throws Exception
    {
        XmlMapper mapper = new XmlMapper();

        assertEquals("<null/>", mapper.writeValueAsString(null));

        assertEquals("<customNull/>",
                mapper.writer().withRootName("customNull").writeValueAsString(null));
    }

    @Test
    public void testConfiguredRootNameOverridesLookupRootName() throws Exception
    {
        XmlMapper mapper = new XmlMapper();

        assertEquals("<Pojo282><a>3</a></Pojo282>",
                mapper.writeValueAsString(new Pojo282()));

        assertEquals("<configuredRoot><a>3</a></configuredRoot>",
                mapper.writer().withRootName("configuredRoot")
                        .writeValueAsString(new Pojo282()));
    }

    @Test
    public void testTypedArrayRootIsWrappedAndClosed() throws Exception
    {
        XmlMapper mapper = new XmlMapper();

        assertEquals("<values><item>1</item><item>2</item></values>",
                mapper.writerFor(int[].class)
                        .withRootName("values")
                        .writeValueAsString(new int[] { 1, 2 }));
    }

    @Test
    public void testConfiguredNamespacedRootUsesDefaultNamespace() throws Exception
    {
        XmlMapper mapper = new XmlMapper();

        assertEquals("<namespacedRoot xmlns=\"urn:test\"><a>3</a></namespacedRoot>",
                mapper.writer()
                        .withRootName(PropertyName.construct("namespacedRoot", "urn:test"))
                        .writeValueAsString(new Pojo282()));
    }

    @Test
    public void testConvertValueUsesTokenBufferGenerator() throws Exception
    {
        XmlMapper mapper = new XmlMapper();

        Map<?, ?> converted = mapper.convertValue(new Pojo282(), Map.class);

        assertEquals(3, converted.get("a"));
    }

    @Test
    public void testAsXmlGeneratorAcceptsTokenBufferAndRejectsOtherGenerators() throws Exception
    {
        TestXmlSerializerProvider provider = new TestXmlSerializerProvider();

        TokenBuffer tokenBuffer = new TokenBuffer(null, false);
        assertNull(provider.asXmlGenerator(tokenBuffer));

        JsonGenerator jsonGenerator = new JsonFactory().createGenerator(new StringWriter());
        try {
            provider.asXmlGenerator(jsonGenerator);
            fail("Expected non-XML, non-TokenBuffer generator to be rejected");
        } catch (JsonMappingException e) {
            assertTrue(e.getMessage().contains("XmlMapper does not with generators"));
            assertTrue(e.getMessage().contains(jsonGenerator.getClass().getName()));
        } finally {
            jsonGenerator.close();
        }
    }

    @Test
    public void testWrapAsIOEPreservesIOExceptionsAndWrapsRuntimeExceptions()
    {
        TestXmlSerializerProvider provider = new TestXmlSerializerProvider();

        IOException original = new IOException("original");
        assertSame(original, provider.wrapAsIOE(original));

        IllegalStateException cause = new IllegalStateException();
        IOException wrapped = provider.wrapAsIOE(cause);

        assertTrue(wrapped instanceof JsonMappingException);
        assertSame(cause, wrapped.getCause());
        assertTrue(wrapped.getMessage().contains(
                "[no message for " + IllegalStateException.class.getName() + "]"));
    }

    public static class Pojo282 {
        public int a = 3;
    }

    @JacksonXmlRootElement(localName = "AnnotatedName")
    public abstract static class AnnotatedRootNameMixIn {
    }

    /**
     * Exposes protected provider behavior for direct branch testing without
     * changing production code.
     */
    private static class TestXmlSerializerProvider extends XmlSerializerProvider {
        TestXmlSerializerProvider() {
            super(new XmlRootNameLookup());
        }

        ToXmlGenerator asXmlGenerator(JsonGenerator generator) throws JsonMappingException {
            return _asXmlGenerator(generator);
        }

        IOException wrapAsIOE(Exception exception) {
            return _wrapAsIOE(null, exception);
        }
    }
}
```

### New test coverage targets

- **`testTypedArrayRootIsWrappedAndClosed`**
  - Covers the typed `serializeValue(JsonGenerator, Object, JavaType, JsonSerializer)` path used by `writerFor`.
  - Exercises `TypeUtil.isIndexedType(rootType)` returning `true`.
  - Covers `_startRootArray(...)`, including the `"item"` field name.
  - Covers the `asArray` branch that writes the closing root object after serialization.

- **`testConfiguredNamespacedRootUsesDefaultNamespace`**
  - Covers `_rootNameFromConfig()` when the configured `PropertyName` has a non-empty namespace.
  - Covers `_initWithRootName(...)` namespace handling where `QName.getNamespaceURI()` is non-empty.
  - Verifies the XML contract that the configured namespace is emitted as the default namespace.

- **`testConvertValueUsesTokenBufferGenerator`**
  - Covers the `serializeValue(JsonGenerator, Object)` path when the generator is a `TokenBuffer`, as used by `convertValue`.
  - Exercises `_asXmlGenerator(...)` returning `null` for `TokenBuffer`.
  - Covers the non-XML-generator path where `asArray` is set to `false`.

- **`testAsXmlGeneratorAcceptsTokenBufferAndRejectsOtherGenerators`**
  - Directly covers both branches of `_asXmlGenerator(...)` for non-`ToXmlGenerator` generators:
    - accepted `TokenBuffer` conversion generator;
    - rejected ordinary JSON generator.
  - Verifies the exceptional API behavior and diagnostic message for unsupported generator types.

- **`testWrapAsIOEPreservesIOExceptionsAndWrapsRuntimeExceptions`**
  - Covers `_wrapAsIOE(...)` when the supplied exception already is an `IOException`.
  - Covers wrapping of non-`IOException` failures in `JsonMappingException`.
  - Covers the null-message fallback branch that creates the `"[no message for ...]"` diagnostic text.