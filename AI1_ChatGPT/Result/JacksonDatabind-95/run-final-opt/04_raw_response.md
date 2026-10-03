@org.junit.Test
public void constructsPrimitiveTypeFromCanonicalName() {
    com.fasterxml.jackson.databind.JavaType type =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance()
                    .constructFromCanonical("int");

    org.junit.Assert.assertEquals(Integer.TYPE, type.getRawClass());
    org.junit.Assert.assertEquals("int", type.toCanonical());
}

@org.junit.Test
public void constructsGenericInnerClassTypeFromCanonicalName() {
    com.fasterxml.jackson.databind.JavaType type =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance()
                    .constructFromCanonical(
                            "java.util.Map$Entry<java.lang.String,java.lang.Integer>");

    org.junit.Assert.assertEquals(java.util.Map.Entry.class, type.getRawClass());
    org.junit.Assert.assertEquals(2, type.containedTypeCount());
    org.junit.Assert.assertEquals(String.class, type.containedType(0).getRawClass());
    org.junit.Assert.assertEquals(Integer.class, type.containedType(1).getRawClass());
}

@org.junit.Test
public void specializesGenericCollectionTypePreservingElementType() {
    com.fasterxml.jackson.databind.type.TypeFactory factory =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance();
    com.fasterxml.jackson.databind.JavaType baseType =
            factory.constructCollectionType(java.util.List.class, String.class);

    com.fasterxml.jackson.databind.JavaType specialized =
            factory.constructSpecializedType(baseType, java.util.ArrayList.class);

    org.junit.Assert.assertEquals(java.util.ArrayList.class, specialized.getRawClass());
    org.junit.Assert.assertEquals(1, specialized.containedTypeCount());
    org.junit.Assert.assertEquals(String.class, specialized.containedType(0).getRawClass());
}

@org.junit.Test(expected = IllegalArgumentException.class)
public void rejectsCanonicalNameWithWrongNumberOfGenericArguments() {
    com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance()
            .constructFromCanonical("java.util.List<java.lang.String,java.lang.Integer>");
}