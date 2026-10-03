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
}
