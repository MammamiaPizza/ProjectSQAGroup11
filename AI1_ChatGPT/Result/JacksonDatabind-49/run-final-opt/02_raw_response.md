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
}