@org.junit.Test
public void writeAsIdReturnsFalseWhenNoIdHasBeenGenerated() throws Exception {
    com.fasterxml.jackson.databind.ser.impl.WritableObjectId objectId =
            new com.fasterxml.jackson.databind.ser.impl.WritableObjectId(
                    new com.fasterxml.jackson.annotation.ObjectIdGenerators.IntSequenceGenerator());

    org.junit.Assert.assertFalse(objectId.writeAsId(null, null, newObjectIdWriter(false)));
}

@org.junit.Test
public void writeAsIdSerializesAlwaysAsIdUsingRegularGenerator() throws Exception {
    com.fasterxml.jackson.databind.ser.impl.WritableObjectId objectId =
            new com.fasterxml.jackson.databind.ser.impl.WritableObjectId(
                    new com.fasterxml.jackson.annotation.ObjectIdGenerators.IntSequenceGenerator());
    objectId.id = Integer.valueOf(12);

    java.io.StringWriter output = new java.io.StringWriter();
    com.fasterxml.jackson.core.JsonGenerator generator =
            new com.fasterxml.jackson.databind.ObjectMapper().getFactory().createGenerator(output);

    org.junit.Assert.assertTrue(objectId.writeAsId(generator, null, newObjectIdWriter(true)));
    generator.close();

    org.junit.Assert.assertEquals("\"12\"", output.toString());
}

@org.junit.Test
public void writeAsFieldUsesNativeObjectIdAndLaterWritesNativeReference() throws Exception {
    com.fasterxml.jackson.databind.ser.impl.WritableObjectId objectId =
            new com.fasterxml.jackson.databind.ser.impl.WritableObjectId(
                    new com.fasterxml.jackson.annotation.ObjectIdGenerators.IntSequenceGenerator());
    objectId.id = Integer.valueOf(8);

    com.fasterxml.jackson.core.JsonGenerator generator =
            org.mockito.Mockito.mock(com.fasterxml.jackson.core.JsonGenerator.class);
    org.mockito.Mockito.when(generator.canWriteObjectId()).thenReturn(true);

    objectId.writeAsField(generator, null, newObjectIdWriter(false));

    org.junit.Assert.assertTrue(objectId.writeAsId(generator, null, newObjectIdWriter(false)));
    org.mockito.Mockito.verify(generator).writeObjectId("8");
    org.mockito.Mockito.verify(generator).writeObjectRef("8");
}

@org.junit.Test
public void writeAsFieldWritesConfiguredPropertyWhenNativeObjectIdsUnavailable() throws Exception {
    com.fasterxml.jackson.databind.ser.impl.WritableObjectId objectId =
            new com.fasterxml.jackson.databind.ser.impl.WritableObjectId(
                    new com.fasterxml.jackson.annotation.ObjectIdGenerators.IntSequenceGenerator());
    objectId.id = Integer.valueOf(7);

    java.io.StringWriter output = new java.io.StringWriter();
    com.fasterxml.jackson.core.JsonGenerator generator =
            new com.fasterxml.jackson.databind.ObjectMapper().getFactory().createGenerator(output);
    generator.writeStartObject();

    objectId.writeAsField(generator, null, newObjectIdWriter(false));

    generator.writeEndObject();
    generator.close();

    org.junit.Assert.assertEquals("{\"@id\":\"7\"}", output.toString());
}

private com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter newObjectIdWriter(boolean alwaysAsId) {
    return com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter.construct(
            new com.fasterxml.jackson.databind.ObjectMapper().getTypeFactory().constructType(Object.class),
            new com.fasterxml.jackson.databind.PropertyName("@id"),
            new com.fasterxml.jackson.annotation.ObjectIdGenerators.IntSequenceGenerator(),
            alwaysAsId).withSerializer(new com.fasterxml.jackson.databind.JsonSerializer<Object>() {
                @Override
                public void serialize(Object value, com.fasterxml.jackson.core.JsonGenerator generator,
                        com.fasterxml.jackson.databind.SerializerProvider provider) throws java.io.IOException {
                    generator.writeString(String.valueOf(value));
                }
            });
}