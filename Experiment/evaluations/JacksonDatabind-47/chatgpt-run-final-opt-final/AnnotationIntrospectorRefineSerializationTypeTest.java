package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AnnotationIntrospectorRefineSerializationTypeTest
{
    @Test
    public void incompatibleSerializationTypeReportsUnrelatedTypes() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValueAsString(new IncompatibleTypeBean());
            fail("Expected incompatible @JsonSerialize(as=...) types to fail");
        } catch (JsonMappingException e) {
            assertTrue("Expected unrelated-types diagnostic, got: " + e.getMessage(),
                    e.getMessage().contains("types not related"));
        }
    }

    @Test
    public void specializedAbstractSerializationTypeIsAccepted() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(mapper.writeValueAsString(new SpecializedTypeBean()));

        assertEquals(3, root.get("value").get("base").asInt());
        assertEquals("special", root.get("value").get("kind").asText());
    }

    public static class IncompatibleTypeBean {
        @JsonSerialize(as = String.class)
        public Long getValue() {
            return Long.valueOf(12L);
        }
    }

    public static class Bean1178Base {
        public int getBase() {
            return 3;
        }
    }

    public static abstract class Bean1178Abstract extends Bean1178Base {
        public abstract String getKind();
    }

    public static class Bean1178Impl extends Bean1178Abstract {
        @Override
        public String getKind() {
            return "special";
        }
    }

    public static class SpecializedTypeBean {
        @JsonSerialize(as = Bean1178Abstract.class)
        public Bean1178Base getValue() {
            return new Bean1178Impl();
        }
    }

@org.junit.Test
public void referencePropertiesRetainReferenceKindAndName() {
    com.fasterxml.jackson.databind.AnnotationIntrospector.ReferenceProperty managed =
            com.fasterxml.jackson.databind.AnnotationIntrospector.ReferenceProperty.managed("parent");
    com.fasterxml.jackson.databind.AnnotationIntrospector.ReferenceProperty back =
            com.fasterxml.jackson.databind.AnnotationIntrospector.ReferenceProperty.back("children");

    org.junit.Assert.assertEquals("parent", managed.getName());
    org.junit.Assert.assertTrue(managed.isManagedReference());
    org.junit.Assert.assertFalse(managed.isBackReference());
    org.junit.Assert.assertEquals(
            com.fasterxml.jackson.databind.AnnotationIntrospector.ReferenceProperty.Type.MANAGED_REFERENCE,
            managed.getType());

    org.junit.Assert.assertEquals("children", back.getName());
    org.junit.Assert.assertTrue(back.isBackReference());
    org.junit.Assert.assertFalse(back.isManagedReference());
    org.junit.Assert.assertEquals(
            com.fasterxml.jackson.databind.AnnotationIntrospector.ReferenceProperty.Type.BACK_REFERENCE,
            back.getType());
}

@org.junit.Test
public void nopIntrospectorListsItselfInAllIntrospectors() {
    com.fasterxml.jackson.databind.AnnotationIntrospector introspector =
            com.fasterxml.jackson.databind.AnnotationIntrospector.nopInstance();

    java.util.Collection<com.fasterxml.jackson.databind.AnnotationIntrospector> listed =
            introspector.allIntrospectors();
    org.junit.Assert.assertEquals(1, listed.size());
    org.junit.Assert.assertSame(introspector, listed.iterator().next());

    java.util.Collection<com.fasterxml.jackson.databind.AnnotationIntrospector> target =
            new java.util.ArrayList<com.fasterxml.jackson.databind.AnnotationIntrospector>();
    org.junit.Assert.assertSame(target, introspector.allIntrospectors(target));
    org.junit.Assert.assertEquals(1, target.size());
    org.junit.Assert.assertSame(introspector, target.iterator().next());
}

@org.junit.Test
public void nopIntrospectorHasNoDefaultOptionalMetadata() {
    com.fasterxml.jackson.databind.AnnotationIntrospector introspector =
            com.fasterxml.jackson.databind.AnnotationIntrospector.nopInstance();

    org.junit.Assert.assertNull(introspector.findClassDescription(
            (com.fasterxml.jackson.databind.introspect.AnnotatedClass) null));
    org.junit.Assert.assertNull(introspector.findIgnoreUnknownProperties(
            (com.fasterxml.jackson.databind.introspect.AnnotatedClass) null));
    org.junit.Assert.assertNull(introspector.findFilterId(
            (com.fasterxml.jackson.databind.introspect.Annotated) null));
    org.junit.Assert.assertNull(introspector.findFormat(
            (com.fasterxml.jackson.databind.introspect.Annotated) null));
    org.junit.Assert.assertNull(introspector.findImplicitPropertyName(
            (com.fasterxml.jackson.databind.introspect.AnnotatedMember) null));
    org.junit.Assert.assertNull(introspector.findContentSerializer(
            (com.fasterxml.jackson.databind.introspect.Annotated) null));
}
}
