package com.fasterxml.jackson.databind.objectid;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.SimpleObjectIdResolver;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class Issue1607ObjectIdReferenceTest
{
    @Test
    public void testClassAndPropertyAlwaysAsIdSerializeFirstOccurrenceAsId() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        assertEquals("{\"alwaysClass\":[1],\"alwaysProp\":2}",
                mapper.writeValueAsString(new MixedReferences()));
    }

    @Test
    public void testIdentityWithoutAlwaysAsIdSerializesFirstOccurrenceAsObject() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(mapper.writeValueAsString(new RegularReferences()));

        assertTrue(root.get("first").isObject());
        assertEquals(3, root.get("first").get("id").asInt());
        assertEquals(7, root.get("first").get("value").asInt());
        assertTrue(root.get("second").isInt());
        assertEquals(3, root.get("second").asInt());
    }

    @Test
    public void testWithAlwaysAsIdCreatesStatePreservingCopy() {
        ObjectIdInfo original = new ObjectIdInfo(new PropertyName("id"), Object.class,
                ObjectIdGenerators.IntSequenceGenerator.class, SimpleObjectIdResolver.class);

        assertFalse(original.getAlwaysAsId());
        assertSame(original, original.withAlwaysAsId(false));

        ObjectIdInfo asId = original.withAlwaysAsId(true);

        assertNotSame(original, asId);
        assertTrue(asId.getAlwaysAsId());
        assertEquals(original.getPropertyName(), asId.getPropertyName());
        assertEquals(original.getScope(), asId.getScope());
        assertEquals(original.getGeneratorType(), asId.getGeneratorType());
        assertEquals(original.getResolverType(), asId.getResolverType());
        assertSame(asId, asId.withAlwaysAsId(true));
        assertFalse(asId.withAlwaysAsId(false).getAlwaysAsId());
    }

    @Test
    public void testObjectIdInfoUsesDefaultResolverWhenNullResolverProvided() {
        ObjectIdInfo info = new ObjectIdInfo(new PropertyName("id"), Object.class,
                ObjectIdGenerators.IntSequenceGenerator.class, null);

        assertEquals(SimpleObjectIdResolver.class, info.getResolverType());
        assertFalse(info.getAlwaysAsId());
    }

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    public static class AlwaysClassReference {
        public int id;
        public int value;

        public AlwaysClassReference(int id, int value) {
            this.id = id;
            this.value = value;
        }
    }

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    public static class PropertyReference {
        public int id;
        public int value;

        public PropertyReference(int id, int value) {
            this.id = id;
            this.value = value;
        }
    }

    public static class MixedReferences {
        public List<AlwaysClassReference> alwaysClass =
                Arrays.asList(new AlwaysClassReference(1, 13));

        @JsonIdentityReference(alwaysAsId = true)
        public PropertyReference alwaysProp = new PropertyReference(2, 14);
    }

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    public static class RegularReference {
        public int id;
        public int value;

        public RegularReference(int id, int value) {
            this.id = id;
            this.value = value;
        }
    }

    public static class RegularReferences {
        public RegularReference first = new RegularReference(3, 7);
        public RegularReference second = first;
    }

@Test
public void testObjectIdInfoCopyPreservesExplicitResolverAndGenerator() {
    com.fasterxml.jackson.databind.introspect.ObjectIdInfo original =
            new com.fasterxml.jackson.databind.introspect.ObjectIdInfo(
                    new com.fasterxml.jackson.databind.PropertyName("id"),
                    String.class,
                    com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator.class,
                    com.fasterxml.jackson.annotation.SimpleObjectIdResolver.class);

    com.fasterxml.jackson.databind.introspect.ObjectIdInfo asId = original.withAlwaysAsId(true);

    assertNotSame(original, asId);
    assertSame(com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator.class,
            asId.getGeneratorType());
    assertSame(com.fasterxml.jackson.annotation.SimpleObjectIdResolver.class,
            asId.getResolverType());
    assertTrue(asId.getAlwaysAsId());
}
}
