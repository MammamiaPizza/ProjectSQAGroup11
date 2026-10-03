@org.junit.Test
public void idFromValueCanonicalizesEnumSetAndEnumMapTypes() {
    com.fasterxml.jackson.databind.type.TypeFactory typeFactory =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance();
    com.fasterxml.jackson.databind.jsontype.impl.ClassNameIdResolver resolver =
            new com.fasterxml.jackson.databind.jsontype.impl.ClassNameIdResolver(
                    typeFactory.constructType(Object.class), typeFactory);

    java.util.EnumSet<java.lang.annotation.ElementType> enumSet =
            java.util.EnumSet.of(java.lang.annotation.ElementType.TYPE);
    java.util.EnumMap<java.lang.annotation.ElementType, Object> enumMap =
            new java.util.EnumMap<java.lang.annotation.ElementType, Object>(
                    java.lang.annotation.ElementType.class);
    enumMap.put(java.lang.annotation.ElementType.TYPE, "value");

    org.junit.Assert.assertEquals(
            typeFactory.constructCollectionType(
                    java.util.EnumSet.class, java.lang.annotation.ElementType.class).toCanonical(),
            resolver.idFromValue(enumSet));
    org.junit.Assert.assertEquals(
            typeFactory.constructMapType(
                    java.util.EnumMap.class, java.lang.annotation.ElementType.class, Object.class).toCanonical(),
            resolver.idFromValue(enumMap));
}

@org.junit.Test
public void idFromValueCanonicalizesInaccessibleJdkCollectionImplementations() {
    com.fasterxml.jackson.databind.type.TypeFactory typeFactory =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance();
    com.fasterxml.jackson.databind.jsontype.impl.ClassNameIdResolver resolver =
            new com.fasterxml.jackson.databind.jsontype.impl.ClassNameIdResolver(
                    typeFactory.constructType(Object.class), typeFactory);

    org.junit.Assert.assertEquals(java.util.ArrayList.class.getName(),
            resolver.idFromValue(java.util.Arrays.asList("value")));
    org.junit.Assert.assertEquals(java.util.HashMap.class.getName(),
            resolver.idFromValue(java.util.Collections.singletonMap("key", "value")));
}

@org.junit.Test
public void idFromValueAndTypeUsesSpecifiedDeclaredType() {
    com.fasterxml.jackson.databind.type.TypeFactory typeFactory =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance();
    com.fasterxml.jackson.databind.jsontype.impl.ClassNameIdResolver resolver =
            new com.fasterxml.jackson.databind.jsontype.impl.ClassNameIdResolver(
                    typeFactory.constructType(Object.class), typeFactory);

    org.junit.Assert.assertEquals(java.util.List.class.getName(),
            resolver.idFromValueAndType(
                    new java.util.ArrayList<String>(), java.util.List.class));
}