@Test
public void classBasedResolutionUsesRegisteredNamedSubtype() {
    com.fasterxml.jackson.databind.jsontype.impl.StdSubtypeResolver resolver =
            new com.fasterxml.jackson.databind.jsontype.impl.StdSubtypeResolver();
    resolver.registerSubtypes(
            new com.fasterxml.jackson.databind.jsontype.NamedType(RegisteredChild.class, "registered"),
            new com.fasterxml.jackson.databind.jsontype.NamedType(UnrelatedType.class, "unrelated"));

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    com.fasterxml.jackson.databind.introspect.AnnotatedClass base =
            com.fasterxml.jackson.databind.introspect.AnnotatedClassResolver.resolveWithoutSuperTypes(
                    mapper.getSerializationConfig(), AbstractRoot.class);

    java.util.Collection<com.fasterxml.jackson.databind.jsontype.NamedType> result =
            resolver.collectAndResolveSubtypesByClass(mapper.getSerializationConfig(), base);

    assertTrue(containsNamedType(result, RegisteredChild.class, "registered"));
    assertFalse(containsType(result, UnrelatedType.class));
}

@Test
public void propertyBasedResolutionIncludesRegisteredAndPropertySubtypes() {
    abstract class PropertyBase { }

    @com.fasterxml.jackson.annotation.JsonTypeName("registered")
    class RegisteredPropertySubtype extends PropertyBase { }

    class PropertySubtype extends PropertyBase { }

    class Holder {
        @com.fasterxml.jackson.annotation.JsonSubTypes({
                @com.fasterxml.jackson.annotation.JsonSubTypes.Type(
                        value = PropertySubtype.class, name = "property")
        })
        public PropertyBase value;
    }

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    com.fasterxml.jackson.databind.jsontype.impl.StdSubtypeResolver resolver =
            new com.fasterxml.jackson.databind.jsontype.impl.StdSubtypeResolver();
    resolver.registerSubtypes(RegisteredPropertySubtype.class);

    com.fasterxml.jackson.databind.introspect.AnnotatedMember property =
            mapper.getSerializationConfig()
                    .introspect(mapper.constructType(Holder.class))
                    .findProperties()
                    .get(0)
                    .getPrimaryMember();

    java.util.Collection<com.fasterxml.jackson.databind.jsontype.NamedType> byClass =
            resolver.collectAndResolveSubtypesByClass(
                    mapper.getSerializationConfig(), property, mapper.constructType(PropertyBase.class));
    java.util.Collection<com.fasterxml.jackson.databind.jsontype.NamedType> byTypeId =
            resolver.collectAndResolveSubtypesByTypeId(
                    mapper.getDeserializationConfig(), property, mapper.constructType(PropertyBase.class));

    assertTrue(containsNamedType(byClass, RegisteredPropertySubtype.class, "registered"));
    assertTrue(containsNamedType(byClass, PropertySubtype.class, "property"));
    assertTrue(containsNamedType(byTypeId, RegisteredPropertySubtype.class, "registered"));
    assertTrue(containsNamedType(byTypeId, PropertySubtype.class, "property"));
}