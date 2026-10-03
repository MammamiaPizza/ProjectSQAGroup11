@Test
public void deprecatedAddLongCreatorRegistersLongCreator() {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    com.fasterxml.jackson.databind.DeserializationConfig config = mapper.getDeserializationConfig();
    com.fasterxml.jackson.databind.BeanDescription description = config.introspect(
            mapper.getTypeFactory().constructType(java.lang.Long.class));
    com.fasterxml.jackson.databind.deser.impl.CreatorCollector collector =
            new com.fasterxml.jackson.databind.deser.impl.CreatorCollector(description, false);

    collector.addLongCreator(findSingleArgumentConstructor(description, java.lang.Long.TYPE));

    org.junit.Assert.assertTrue(collector.constructValueInstantiator(config).canCreateFromLong());
}

@Test
public void deprecatedAddDoubleCreatorRegistersDoubleCreator() {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    com.fasterxml.jackson.databind.DeserializationConfig config = mapper.getDeserializationConfig();
    com.fasterxml.jackson.databind.BeanDescription description = config.introspect(
            mapper.getTypeFactory().constructType(java.lang.Double.class));
    com.fasterxml.jackson.databind.deser.impl.CreatorCollector collector =
            new com.fasterxml.jackson.databind.deser.impl.CreatorCollector(description, false);

    collector.addDoubleCreator(findSingleArgumentConstructor(description, java.lang.Double.TYPE));

    org.junit.Assert.assertTrue(collector.constructValueInstantiator(config).canCreateFromDouble());
}

@Test
public void deprecatedAddBooleanCreatorRegistersBooleanCreator() {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    com.fasterxml.jackson.databind.DeserializationConfig config = mapper.getDeserializationConfig();
    com.fasterxml.jackson.databind.BeanDescription description = config.introspect(
            mapper.getTypeFactory().constructType(java.lang.Boolean.class));
    com.fasterxml.jackson.databind.deser.impl.CreatorCollector collector =
            new com.fasterxml.jackson.databind.deser.impl.CreatorCollector(description, false);

    collector.addBooleanCreator(findSingleArgumentConstructor(description, java.lang.Boolean.TYPE));

    org.junit.Assert.assertTrue(collector.constructValueInstantiator(config).canCreateFromBoolean());
}

private com.fasterxml.jackson.databind.introspect.AnnotatedWithParams findSingleArgumentConstructor(
        com.fasterxml.jackson.databind.BeanDescription description, java.lang.Class<?> parameterType) {
    for (com.fasterxml.jackson.databind.introspect.AnnotatedConstructor constructor
            : description.getConstructors()) {
        if (constructor.getParameterCount() == 1
                && constructor.getRawParameterType(0) == parameterType) {
            return constructor;
        }
    }
    throw new java.lang.AssertionError("No constructor found for " + parameterType);
}