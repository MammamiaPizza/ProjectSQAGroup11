package com.fasterxml.jackson.databind.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import com.fasterxml.jackson.databind.JavaType;

public class ReferenceTypeCanonicalNameTest
{
    private final TypeFactory typeFactory = TypeFactory.defaultInstance();

    @Test
    public void canonicalNameForReferenceToLongIncludesClosingDelimiter() {
        JavaType longType = typeFactory.constructType(Long.class);
        JavaType referenceType = typeFactory.constructReferenceType(AtomicReference.class, longType);

        assertEquals("java.util.concurrent.atomic.AtomicReference<java.lang.Long>",
                referenceType.toCanonical());
    }

    @Test
    public void canonicalNameForNestedReferencesClosesBothDelimiters() {
        JavaType longType = typeFactory.constructType(Long.class);
        JavaType inner = typeFactory.constructReferenceType(AtomicReference.class, longType);
        JavaType outer = typeFactory.constructReferenceType(AtomicReference.class, inner);

        assertEquals("java.util.concurrent.atomic.AtomicReference<java.util.concurrent.atomic.AtomicReference<java.lang.Long>>",
                outer.toCanonical());
    }

    @Test
    public void canonicalNameIsUpdatedWhenReferenceContentChanges() {
        JavaType longReference = typeFactory.constructReferenceType(
                AtomicReference.class, typeFactory.constructType(Long.class));
        JavaType stringReference = longReference.withContentType(
                typeFactory.constructType(String.class));

        assertEquals("java.util.concurrent.atomic.AtomicReference<java.lang.String>",
                stringReference.toCanonical());
    }

    @Test
    public void referenceTypeReportsItsReferencedContent() {
        JavaType longType = typeFactory.constructType(Long.class);
        JavaType referenceType = typeFactory.constructReferenceType(AtomicReference.class, longType);

        assertTrue(referenceType.isReferenceType());
        assertTrue(referenceType.hasContentType());
        assertSame(longType, referenceType.getContentType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void upgradeFromRejectsMissingReferencedType() {
        ReferenceType.upgradeFrom(typeFactory.constructType(String.class), null);
    }
}