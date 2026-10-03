```java
package com.fasterxml.jackson.dataformat.xml;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

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

    public static class Pojo282 {
        public int a = 3;
    }

    @JacksonXmlRootElement(localName = "AnnotatedName")
    public abstract static class AnnotatedRootNameMixIn {
    }
}
```

- `testCopyDoesNotReuseRootNameLookupFromOriginalMapper` targets the reported mapper-copy defect. It first caches a mix-in-derived root name in the original mapper, then verifies that a copied mapper resolves the root name again after its mix-in configuration changes.
- `testSerializeNullUsesDefaultAndConfiguredRootNames` covers null serialization, including the default `null` root element and an explicitly configured root name.
- `testConfiguredRootNameOverridesLookupRootName` verifies both the normal class-derived root-name lookup path and the configured-root-name override path.