@org.junit.Test
public void deprecatedCollectionTypeMutationMethodsReturnAppropriateTypes() {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    com.fasterxml.jackson.databind.JavaType stringType = mapper.constructType(String.class);
    com.fasterxml.jackson.databind.JavaType integerType = mapper.constructType(Integer.class);
    com.fasterxml.jackson.databind.type.CollectionType type =
            com.fasterxml.jackson.databind.type.CollectionType.construct(java.util.ArrayList.class, stringType);

    org.junit.Assert.assertSame(type, type.withContentType(stringType));
    org.junit.Assert.assertNotSame(type, type.withContentType(integerType));
    org.junit.Assert.assertNotSame(type, type.withTypeHandler(new Object()));
    org.junit.Assert.assertNotSame(type, type.withContentTypeHandler(new Object()));
    org.junit.Assert.assertNotSame(type, type.withValueHandler(new Object()));
    org.junit.Assert.assertNotSame(type, type.withContentValueHandler(new Object()));

    com.fasterxml.jackson.databind.JavaType staticType = type.withStaticTyping();
    org.junit.Assert.assertNotSame(type, staticType);
    org.junit.Assert.assertSame(staticType, staticType.withStaticTyping());
}

@org.junit.Test
public void deprecatedMapTypeMutationMethodsReturnAppropriateTypes() {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    com.fasterxml.jackson.databind.JavaType stringType = mapper.constructType(String.class);
    com.fasterxml.jackson.databind.JavaType integerType = mapper.constructType(Integer.class);
    com.fasterxml.jackson.databind.type.MapType type =
            com.fasterxml.jackson.databind.type.MapType.construct(java.util.LinkedHashMap.class,
                    stringType, integerType);

    org.junit.Assert.assertSame(type, type.withContentType(integerType));
    org.junit.Assert.assertSame(type, type.withKeyType(stringType));
    org.junit.Assert.assertNotSame(type, type.withContentType(stringType));
    org.junit.Assert.assertNotSame(type, type.withKeyType(integerType));
    org.junit.Assert.assertNotSame(type, type.withTypeHandler(new Object()));
    org.junit.Assert.assertNotSame(type, type.withContentTypeHandler(new Object()));
    org.junit.Assert.assertNotSame(type, type.withValueHandler(new Object()));
    org.junit.Assert.assertNotSame(type, type.withContentValueHandler(new Object()));
    org.junit.Assert.assertNotSame(type, type.withKeyTypeHandler(new Object()));
    org.junit.Assert.assertNotSame(type, type.withKeyValueHandler(new Object()));

    com.fasterxml.jackson.databind.JavaType staticType = type.withStaticTyping();
    org.junit.Assert.assertNotSame(type, staticType);
    org.junit.Assert.assertSame(staticType, staticType.withStaticTyping());
}

@org.junit.Test
public void deprecatedSimpleTypeMutationMethodsReturnAppropriateTypes() {
    com.fasterxml.jackson.databind.type.SimpleType type =
            com.fasterxml.jackson.databind.type.SimpleType.construct(String.class);

    org.junit.Assert.assertNotSame(type, type.withTypeHandler(new Object()));
    org.junit.Assert.assertNotSame(type, type.withValueHandler(new Object()));

    com.fasterxml.jackson.databind.JavaType staticType = type.withStaticTyping();
    org.junit.Assert.assertNotSame(type, staticType);
    org.junit.Assert.assertSame(staticType, staticType.withStaticTyping());
}