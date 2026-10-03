package com.fasterxml.jackson.databind.introspect;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AnnotationMapGeneratedTest
{
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Name {
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Flag { }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface OtherFlag { }

    @Name("primary")
    private static class PrimaryName { }

    @Name("secondary")
    private static class SecondaryName { }

    @Flag
    private static class Flagged { }

    @OtherFlag
    private static class OtherFlagged { }

    private static Name primaryName() {
        return PrimaryName.class.getAnnotation(Name.class);
    }

    private static Name secondaryName() {
        return SecondaryName.class.getAnnotation(Name.class);
    }

    private static Flag flag() {
        return Flagged.class.getAnnotation(Flag.class);
    }

    private static OtherFlag otherFlag() {
        return OtherFlagged.class.getAnnotation(OtherFlag.class);
    }

    @Test
    public void mergeUsesPrimaryAnnotationWhenTypesConflict() {
        AnnotationMap primary = new AnnotationMap();
        AnnotationMap secondary = new AnnotationMap();
        primary.add(primaryName());
        secondary.add(secondaryName());

        AnnotationMap merged = AnnotationMap.merge(primary, secondary);

        assertEquals(1, merged.size());
        assertEquals("primary", merged.get(Name.class).value());
    }

    @Test
    public void mergePreservesDisjointAnnotationsAndExposesThemThroughIterable() {
        AnnotationMap primary = new AnnotationMap();
        AnnotationMap secondary = new AnnotationMap();
        primary.add(flag());
        secondary.add(otherFlag());

        AnnotationMap merged = AnnotationMap.merge(primary, secondary);

        assertEquals(2, merged.size());
        assertEquals(flag(), merged.get(Flag.class));
        assertEquals(otherFlag(), merged.get(OtherFlag.class));

        Set<Class<? extends Annotation>> types = new HashSet<Class<? extends Annotation>>();
        for (Annotation annotation : merged.annotations()) {
            types.add(annotation.annotationType());
        }
        assertEquals(2, types.size());
        assertTrue(types.contains(Flag.class));
        assertTrue(types.contains(OtherFlag.class));
    }

    @Test
    public void mergeWithEmptyMapRetainsNonEmptyMapContents() {
        AnnotationMap populated = new AnnotationMap();
        populated.add(primaryName());

        AnnotationMap merged = AnnotationMap.merge(new AnnotationMap(), populated);

        assertEquals(1, merged.size());
        assertEquals("primary", merged.get(Name.class).value());
        assertNull(merged.get(Flag.class));
    }

    @Test
    public void mergingTwoEmptyMapsProducesNoAnnotations() {
        AnnotationMap merged = AnnotationMap.merge(new AnnotationMap(), new AnnotationMap());

        assertEquals(0, merged.size());
        assertFalse(merged.annotations().iterator().hasNext());
    }

    @Test
    public void mergeOfTwoNullMapsIsNull() {
        assertNull(AnnotationMap.merge(null, null));
    }

    @Test
    public void addReportsTrueWhenAddingNewAnnotationChangesContents() {
        AnnotationMap map = new AnnotationMap();

        assertTrue(map.add(primaryName()));
        assertEquals(1, map.size());
        assertEquals("primary", map.get(Name.class).value());
    }

    @Test
    public void addReportsFalseWhenSameAnnotationIsAlreadyPresent() {
        AnnotationMap map = new AnnotationMap();
        map.add(primaryName());

        assertFalse(map.add(primaryName()));
        assertEquals(1, map.size());
        assertEquals("primary", map.get(Name.class).value());
    }

    @Test
    public void addReportsTrueAndReplacesExistingAnnotationWhenValueDiffers() {
        AnnotationMap map = new AnnotationMap();
        map.add(primaryName());

        assertTrue(map.add(secondaryName()));
        assertEquals(1, map.size());
        assertEquals("secondary", map.get(Name.class).value());
    }

    @Test
    public void addIfNotPresentKeepsExistingTypeAndAcceptsNewTypes() {
        AnnotationMap map = new AnnotationMap();

        assertTrue(map.addIfNotPresent(primaryName()));
        assertFalse(map.addIfNotPresent(secondaryName()));
        assertTrue(map.addIfNotPresent(flag()));

        assertEquals(2, map.size());
        assertEquals("primary", map.get(Name.class).value());
        assertEquals(flag(), map.get(Flag.class));
    }
}
