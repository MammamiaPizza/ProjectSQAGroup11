package com.fasterxml.jackson.databind.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResolvedRecursiveTypeGeneratedTest
{
    static class RecursiveBase<T extends RecursiveBase<T>> {
        public int base;
    }

    static class RecursiveSub extends RecursiveBase<RecursiveSub> {
        public int sub;
    }

    @Test
    public void recursiveReferenceDelegatesToResolvedTypeSuperClass() {
        ObjectMapper mapper = new ObjectMapper();
        JavaType subType = mapper.getTypeFactory().constructType(RecursiveSub.class);
        JavaType baseType = subType.getSuperClass();
        JavaType recursiveArgument = baseType.containedType(0);

        assertTrue(recursiveArgument instanceof ResolvedRecursiveType);
        ResolvedRecursiveType recursive = (ResolvedRecursiveType) recursiveArgument;
        assertSame(subType, recursive.getSelfReferencedType());
        assertEquals(RecursiveBase.class, recursive.getSuperClass().getRawClass());
    }

    @Test
    public void serializingSubtypeAfterBaseTypeResolutionIncludesInheritedProperties()
            throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.getTypeFactory().constructType(RecursiveBase.class);

        RecursiveSub value = new RecursiveSub();
        value.base = 1;
        value.sub = 2;

        assertEquals("{\"base\":1,\"sub\":2}", mapper.writeValueAsString(value));
    }

    @Test
    public void referenceIsRetainedAndSignatureMethodsDelegate() {
        JavaType stringType = TypeFactory.defaultInstance().constructType(String.class);
        ResolvedRecursiveType recursive = new ResolvedRecursiveType(String.class,
                TypeBindings.emptyBindings());

        assertNull(recursive.getSelfReferencedType());

        recursive.setReference(stringType);

        assertSame(stringType, recursive.getSelfReferencedType());
        assertEquals(stringType.getGenericSignature(new StringBuilder()).toString(),
                recursive.getGenericSignature(new StringBuilder()).toString());
        assertEquals(stringType.getErasedSignature(new StringBuilder()).toString(),
                recursive.getErasedSignature(new StringBuilder()).toString());
    }

    @Test(expected = IllegalStateException.class)
    public void referenceCannotBeAssignedMoreThanOnce() {
        ResolvedRecursiveType recursive = new ResolvedRecursiveType(String.class,
                TypeBindings.emptyBindings());
        JavaType stringType = TypeFactory.defaultInstance().constructType(String.class);

        recursive.setReference(stringType);
        recursive.setReference(stringType);
    }
}