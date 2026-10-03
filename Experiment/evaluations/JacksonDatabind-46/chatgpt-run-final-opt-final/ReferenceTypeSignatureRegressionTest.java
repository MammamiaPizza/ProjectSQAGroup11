package com.fasterxml.jackson.databind.type;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import com.fasterxml.jackson.databind.JavaType;

public class ReferenceTypeSignatureRegressionTest
{
    private ReferenceType referenceTo(JavaType contentType) {
        return ReferenceType.construct(AtomicReference.class, contentType, null, null);
    }

    @Test
    public void genericSignatureForSimpleReferencedTypeClosesTypeArgument() {
        JavaType stringType = TypeFactory.defaultInstance().constructType(String.class);

        String signature = referenceTo(stringType).getGenericSignature(new StringBuilder()).toString();

        assertEquals("Ljava/util/concurrent/atomic/AtomicReference<Ljava/lang/String;>;", signature);
    }

    @Test
    public void genericSignatureForArrayReferencedTypePlacesArrayMarkerInsideTypeArgument() {
        JavaType arrayType = TypeFactory.defaultInstance().constructType(String[].class);

        String signature = referenceTo(arrayType).getGenericSignature(new StringBuilder()).toString();

        assertEquals("Ljava/util/concurrent/atomic/AtomicReference<[Ljava/lang/String;>;", signature);
    }

    @Test
    public void genericSignatureForMultiDimensionalArrayReferencedTypeKeepsAllArrayMarkersInsideTypeArgument() {
        JavaType arrayType = TypeFactory.defaultInstance().constructType(String[][].class);

        String signature = referenceTo(arrayType).getGenericSignature(new StringBuilder()).toString();

        assertEquals("Ljava/util/concurrent/atomic/AtomicReference<[[Ljava/lang/String;>;", signature);
    }

    @Test
    public void erasedSignatureDoesNotIncludeReferencedGenericType() {
        JavaType arrayType = TypeFactory.defaultInstance().constructType(String[].class);

        String signature = referenceTo(arrayType).getErasedSignature(new StringBuilder()).toString();

        assertEquals("Ljava/util/concurrent/atomic/AtomicReference;", signature);
    }
}
