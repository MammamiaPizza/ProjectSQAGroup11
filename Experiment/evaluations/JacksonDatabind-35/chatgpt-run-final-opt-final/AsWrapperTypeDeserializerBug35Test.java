import java.util.ArrayList;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AsWrapperTypeDeserializerBug35Test
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = Node.class, name = "node"),
        @JsonSubTypes.Type(value = StringValues.class, name = "strings"),
        @JsonSubTypes.Type(value = TextValue.class, name = "text")
    })
    public static interface Value { }

    @JsonTypeName("node")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    public static class Node implements Value {
        public int id;
        public String name;
        public Value next;

        public Node() { }

        public Node(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @JsonTypeName("strings")
    public static class StringValues extends ArrayList<String> implements Value {
        private static final long serialVersionUID = 1L;
    }

    @JsonTypeName("text")
    public static class TextValue implements Value {
        private final String text;

        @JsonCreator
        public TextValue(String text) {
            this.text = text;
        }

        @JsonValue
        public String value() {
            return text;
        }
    }

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void deserializesWrappedObjectValue() throws Exception {
        Value value = mapper.readValue("{\"node\":{\"id\":3,\"name\":\"workstation\"}}", Value.class);

        assertTrue(value instanceof Node);
        Node node = (Node) value;
        assertEquals(3, node.id);
        assertEquals("workstation", node.name);
    }

    @Test
    public void deserializesWrappedArrayValue() throws Exception {
        Value value = mapper.readValue("{\"strings\":[\"one\",\"two\"]}", Value.class);

        assertTrue(value instanceof StringValues);
        assertEquals(2, ((StringValues) value).size());
        assertEquals("one", ((StringValues) value).get(0));
        assertEquals("two", ((StringValues) value).get(1));
    }

    @Test
    public void deserializesWrappedScalarValue() throws Exception {
        Value value = mapper.readValue("{\"text\":\"hello\"}", Value.class);

        assertTrue(value instanceof TextValue);
        assertEquals("hello", ((TextValue) value).value());
    }

    @Test
    public void deserializesWrappedObjectWithObjectIdReference() throws Exception {
        Value value = mapper.readValue(
                "{\"node\":{\"id\":1,\"name\":\"root\",\"next\":{\"node\":1}}}",
                Value.class);

        assertTrue(value instanceof Node);
        Node node = (Node) value;
        assertEquals("root", node.name);
        assertNotNull(node.next);
        assertSame(node, node.next);
    }

    @Test
    public void acceptsParserAlreadyPositionedOnWrapperTypeFieldAfterObjectStart() throws Exception {
        JsonParser parser = mapper.getFactory().createParser(
                "{\"node\":{\"id\":7,\"name\":\"already-positioned\"}}");
        try {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());

            Value value = mapper.readValue(parser, Value.class);

            assertTrue(value instanceof Node);
            Node node = (Node) value;
            assertEquals(7, node.id);
            assertEquals("already-positioned", node.name);
        } finally {
            parser.close();
        }
    }

    @Test
    public void rejectsNonObjectWrapperInput() throws Exception {
        try {
            mapper.readValue("17", Value.class);
            fail("A WRAPPER_OBJECT type value must start with an object");
        } catch (JsonMappingException e) {
            assertTrue(e.getMessage().contains("START_OBJECT"));
        }
    }

@Test(expected = com.fasterxml.jackson.databind.JsonMappingException.class)
public void rejectsWrapperObjectWithoutTypeField() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.enableDefaultTyping(com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL,
            com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT);

    mapper.readValue("{}", Object.class);
}

@Test(expected = com.fasterxml.jackson.databind.JsonMappingException.class)
public void rejectsWrapperObjectWithAdditionalFieldsAfterValue() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.enableDefaultTyping(com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL,
            com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT);

    mapper.readValue("{\"java.util.ArrayList\":[],\"extra\":true}", Object.class);
}
}
