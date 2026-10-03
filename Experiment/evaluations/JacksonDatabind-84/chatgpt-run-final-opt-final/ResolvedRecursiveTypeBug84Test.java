package com.fasterxml.jackson.databind.type;

import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ResolvedRecursiveTypeBug84Test
{
    static class Base<T extends Base<T>> {
        public int base = 1;
    }

    static class Sub extends Base<Sub> {
        public int sub = 2;
    }

    @Test
    public void serializesInheritedPropertiesWhenBaseTypeWasResolvedFirst() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        mapper.getTypeFactory().constructType(Base.class);

        assertEquals("{\"base\":1,\"sub\":2}", mapper.writeValueAsString(new Sub()));
    }

    @Test
    public void setReferenceMakesReferencedTypeAvailable()
    {
        JavaType reference = TypeFactory.defaultInstance().constructType(String.class);
        ResolvedRecursiveType recursive = new ResolvedRecursiveType(String.class,
                TypeBindings.emptyBindings());

        recursive.setReference(reference);

        assertSame(reference, recursive.getSelfReferencedType());
        assertEquals(String.class, recursive.getSelfReferencedType().getRawClass());
    }

    @Test(expected = IllegalStateException.class)
    public void setReferenceRejectsSecondReference()
    {
        ResolvedRecursiveType recursive = new ResolvedRecursiveType(String.class,
                TypeBindings.emptyBindings());

        recursive.setReference(TypeFactory.defaultInstance().constructType(String.class));
        recursive.setReference(TypeFactory.defaultInstance().constructType(Integer.class));
    }

    @Test
    public void typeTransformationsApplyToResolvedReference()
    {
        TypeFactory factory = TypeFactory.defaultInstance();
        JavaType listType = factory.constructCollectionType(List.class, String.class);
        JavaType integerType = factory.constructType(Integer.class);
        ResolvedRecursiveType recursive = new ResolvedRecursiveType(List.class,
                TypeBindings.emptyBindings());
        Object typeHandler = new Object();
        Object contentTypeHandler = new Object();
        Object valueHandler = new Object();
        Object contentValueHandler = new Object();

        recursive.setReference(listType);

        JavaType contentChanged = recursive.withContentType(integerType);
        JavaType typeHandled = recursive.withTypeHandler(typeHandler);
        JavaType contentTypeHandled = recursive.withContentTypeHandler(contentTypeHandler);
        JavaType valueHandled = recursive.withValueHandler(valueHandler);
        JavaType contentValueHandled = recursive.withContentValueHandler(contentValueHandler);
        JavaType staticallyTyped = recursive.withStaticTyping();

        assertSame(recursive, contentChanged);
        assertSame(recursive, typeHandled);
        assertSame(recursive, contentTypeHandled);
        assertSame(recursive, valueHandled);
        assertSame(recursive, contentValueHandled);
        assertSame(recursive, staticallyTyped);
    }

@Test
public void delegatesGenericAndErasedSignaturesToReference() {
    com.fasterxml.jackson.databind.type.ResolvedRecursiveType recursive =
            new com.fasterxml.jackson.databind.type.ResolvedRecursiveType(
                    Object.class, com.fasterxml.jackson.databind.type.TypeBindings.emptyBindings());
    com.fasterxml.jackson.databind.JavaType reference =
            new com.fasterxml.jackson.databind.ObjectMapper().constructType(String.class);
    recursive.setReference(reference);

    org.junit.Assert.assertEquals(
            reference.getGenericSignature(new StringBuilder()).toString(),
            recursive.getGenericSignature(new StringBuilder()).toString());
    org.junit.Assert.assertEquals(
            reference.getErasedSignature(new StringBuilder()).toString(),
            recursive.getErasedSignature(new StringBuilder()).toString());
}

@Test
public void toStringDescribesUnresolvedAndResolvedReferences() {
    com.fasterxml.jackson.databind.type.ResolvedRecursiveType recursive =
            new com.fasterxml.jackson.databind.type.ResolvedRecursiveType(
                    Object.class, com.fasterxml.jackson.databind.type.TypeBindings.emptyBindings());

    org.junit.Assert.assertTrue(recursive.toString().contains("UNRESOLVED"));

    recursive.setReference(new com.fasterxml.jackson.databind.ObjectMapper().constructType(String.class));
    org.junit.Assert.assertTrue(recursive.toString().contains(String.class.getName()));
}

@Test
public void equalsUsesReferencedTypeAndDoesNotMatchUnresolvedTypes() {
    com.fasterxml.jackson.databind.type.ResolvedRecursiveType unresolved =
            new com.fasterxml.jackson.databind.type.ResolvedRecursiveType(
                    Object.class, com.fasterxml.jackson.databind.type.TypeBindings.emptyBindings());
    com.fasterxml.jackson.databind.type.ResolvedRecursiveType anotherUnresolved =
            new com.fasterxml.jackson.databind.type.ResolvedRecursiveType(
                    Object.class, com.fasterxml.jackson.databind.type.TypeBindings.emptyBindings());

    org.junit.Assert.assertTrue(unresolved.equals(unresolved));
    org.junit.Assert.assertFalse(unresolved.equals(anotherUnresolved));
    org.junit.Assert.assertFalse(unresolved.equals(null));

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    unresolved.setReference(mapper.constructType(String.class));
    anotherUnresolved.setReference(mapper.constructType(String.class));
    org.junit.Assert.assertTrue(unresolved.equals(anotherUnresolved));
    org.junit.Assert.assertTrue(anotherUnresolved.equals(unresolved));

    com.fasterxml.jackson.databind.type.ResolvedRecursiveType different =
            new com.fasterxml.jackson.databind.type.ResolvedRecursiveType(
                    Object.class, com.fasterxml.jackson.databind.type.TypeBindings.emptyBindings());
    different.setReference(mapper.constructType(Integer.class));
    org.junit.Assert.assertFalse(unresolved.equals(different));
}

@Test
public void recursiveTypeIsNotReportedAsContainer() {
    com.fasterxml.jackson.databind.type.ResolvedRecursiveType recursive =
            new com.fasterxml.jackson.databind.type.ResolvedRecursiveType(
                    Object.class, com.fasterxml.jackson.databind.type.TypeBindings.emptyBindings());

    org.junit.Assert.assertFalse(recursive.isContainerType());
}
}
