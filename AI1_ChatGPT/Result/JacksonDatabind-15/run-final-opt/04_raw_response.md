@org.junit.Test
public void forcedNarrowByPreservesHandlersAndReturnsSameTypeForSameClass() {
    Object valueHandler = new Object();
    Object typeHandler = new Object();
    com.fasterxml.jackson.databind.JavaType type =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance()
                    .constructType(Number.class)
                    .withValueHandler(valueHandler)
                    .withTypeHandler(typeHandler);

    org.junit.Assert.assertSame(type, type.forcedNarrowBy(Number.class));

    com.fasterxml.jackson.databind.JavaType narrowed = type.forcedNarrowBy(Integer.class);
    org.junit.Assert.assertEquals(Integer.class, narrowed.getRawClass());
    org.junit.Assert.assertSame(valueHandler, narrowed.getValueHandler());
    org.junit.Assert.assertSame(typeHandler, narrowed.getTypeHandler());
}

@org.junit.Test(expected = IllegalArgumentException.class)
public void narrowByRejectsUnrelatedClass() {
    com.fasterxml.jackson.databind.JavaType type =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance()
                    .constructType(Number.class);

    type.narrowBy(String.class);
}

@org.junit.Test
public void simpleTypeHasUnknownContainedTypeWhenRequested() {
    com.fasterxml.jackson.databind.JavaType type =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance()
                    .constructType(String.class);

    org.junit.Assert.assertEquals(
            com.fasterxml.jackson.databind.type.TypeFactory.unknownType(),
            type.containedTypeOrUnknown(0));
}