package com.fasterxml.jackson.databind.jsontype.impl;

import java.util.Collection;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedClassResolver;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StdSubtypeResolverBug80Test
{
    @Test
    public void typeIdResolutionDoesNotIncludeUnnamedAbstractBase() {
        ObjectMapper mapper = new ObjectMapper();
        StdSubtypeResolver resolver = new StdSubtypeResolver();
        AnnotatedClass base = AnnotatedClassResolver.resolveWithoutSuperTypes(
                mapper.getDeserializationConfig(), AbstractRoot.class);

        Collection<NamedType> result = resolver.collectAndResolveSubtypesByTypeId(
                mapper.getDeserializationConfig(), base);

        assertTrue(containsNamedType(result, AnnotatedChild.class, "annotated"));
        assertFalse(containsType(result, AbstractRoot.class));
    }

    @Test
    public void typeIdResolutionForUnannotatedAbstractBaseIsEmpty() {
        ObjectMapper mapper = new ObjectMapper();
        StdSubtypeResolver resolver = new StdSubtypeResolver();
        AnnotatedClass base = AnnotatedClassResolver.resolveWithoutSuperTypes(
                mapper.getDeserializationConfig(), PlainAbstractRoot.class);

        Collection<NamedType> result = resolver.collectAndResolveSubtypesByTypeId(
                mapper.getDeserializationConfig(), base);

        assertEquals(0, result.size());
    }

    @Test
    public void registeredNamedAndUnnamedSubtypesAreResolvedButUnrelatedOnesAreIgnored() {
        ObjectMapper mapper = new ObjectMapper();
        StdSubtypeResolver resolver = new StdSubtypeResolver();
        resolver.registerSubtypes(new NamedType(RegisteredChild.class, "registered"));
        resolver.registerSubtypes(UnnamedRegisteredChild.class, UnrelatedType.class);

        AnnotatedClass base = AnnotatedClassResolver.resolveWithoutSuperTypes(
                mapper.getDeserializationConfig(), AbstractRoot.class);
        Collection<NamedType> result = resolver.collectAndResolveSubtypesByTypeId(
                mapper.getDeserializationConfig(), base);

        assertTrue(containsNamedType(result, RegisteredChild.class, "registered"));
        assertTrue(containsType(result, UnnamedRegisteredChild.class));
        assertFalse(containsType(result, UnrelatedType.class));
        assertFalse(containsType(result, AbstractRoot.class));
    }

    @Test
    public void classBasedResolutionIncludesAnnotatedSubtypeAndBaseClass() {
        ObjectMapper mapper = new ObjectMapper();
        StdSubtypeResolver resolver = new StdSubtypeResolver();
        AnnotatedClass base = AnnotatedClassResolver.resolveWithoutSuperTypes(
                mapper.getSerializationConfig(), AbstractRoot.class);

        Collection<NamedType> result = resolver.collectAndResolveSubtypesByClass(
                mapper.getSerializationConfig(), base);

        assertTrue(containsType(result, AbstractRoot.class));
        assertTrue(containsNamedType(result, AnnotatedChild.class, "annotated"));
    }

    @Test
    public void objectMapperCanRoundTripAnnotationNamedSubtype() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Envelope input = new Envelope();
        input.value = new AnnotatedChild();
        input.value.number = 13;

        String json = mapper.writeValueAsString(input);
        Envelope restored = mapper.readValue(json, Envelope.class);

        assertTrue(json.contains("\"kind\":\"annotated\""));
        assertTrue(restored.value instanceof AnnotatedChild);
        assertEquals(13, restored.value.number);
    }

    private static boolean containsType(Collection<NamedType> types, Class<?> type) {
        for (NamedType namedType : types) {
            if (namedType.getType() == type) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsNamedType(Collection<NamedType> types,
            Class<?> type, String name) {
        for (NamedType namedType : types) {
            if (namedType.getType() == type && name.equals(namedType.getName())) {
                return true;
            }
        }
        return false;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY, property = "kind")
    @JsonSubTypes({
            @JsonSubTypes.Type(AnnotatedChild.class)
    })
    abstract static class AbstractRoot {
        public int number;
    }

    @JsonTypeName("annotated")
    static class AnnotatedChild extends AbstractRoot {
    }

    static class RegisteredChild extends AbstractRoot {
    }

    static class UnnamedRegisteredChild extends AbstractRoot {
    }

    static class UnrelatedType {
    }

    abstract static class PlainAbstractRoot {
    }

    static class Envelope {
        public AbstractRoot value;
    }
}
