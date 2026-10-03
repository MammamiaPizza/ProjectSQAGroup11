import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.WritableObjectId;
import org.junit.Test;

public class WritableObjectIdBug1255Test
{
    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
    public static class Bar
    {
        public int value;

        public Bar() { }

        public Bar(int value) {
            this.value = value;
        }
    }

    @JsonPropertyOrder({ "bar1", "bar2" })
    public static class Pair
    {
        @JsonIdentityReference(alwaysAsId = true)
        public Bar bar1;

        public Bar bar2;

        public Pair() { }
    }

    @Test
    public void generateIdReusesPreviouslyGeneratedId()
    {
        WritableObjectId objectId = new WritableObjectId(
                new ObjectIdGenerators.IntSequenceGenerator());
        Object pojo = new Object();

        assertEquals(null, objectId.id);

        Object firstId = objectId.generateId(pojo);
        Object secondId = objectId.generateId(pojo);

        assertNotNull(firstId);
        assertEquals(firstId, secondId);
        assertEquals(firstId, objectId.id);
    }

    @Test
    public void firstReferenceAndLaterDefinitionUseSameIdAndDeserialize()
            throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        Bar shared = new Bar(3);
        Pair input = new Pair();
        input.bar1 = shared;
        input.bar2 = shared;

        String json = mapper.writeValueAsString(input);
        JsonNode root = mapper.readTree(json);

        assertNotNull(root.get("bar1"));
        assertNotNull(root.get("bar2"));
        assertNotNull(root.get("bar2").get("@id"));
        assertEquals(root.get("bar1").asText(), root.get("bar2").get("@id").asText());

        Pair result = mapper.readValue(json, Pair.class);

        assertNotNull(result.bar1);
        assertSame(result.bar1, result.bar2);
        assertEquals(3, result.bar2.value);
    }

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
}
