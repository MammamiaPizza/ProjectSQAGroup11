import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ObjectNodeSetAllIntrospectionTest {

    @Test
    public void standardNamingStrategyCanDeserializeObjectNode() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(
                PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);

        ObjectNode node = mapper.readValue(
                "{\"first_name\":\"Ada\",\"item_count\":2}", ObjectNode.class);

        assertNotNull(node);
        assertEquals("Ada", node.get("first_name").asText());
        assertEquals(2, node.get("item_count").asInt());
    }

    @Test
    public void introspectionDoesNotExposeConflictingAllSetters() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(
                PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);

        BeanDescription description = mapper.getDeserializationConfig().introspect(
                mapper.getTypeFactory().constructType(ObjectNode.class));
        List<BeanPropertyDefinition> properties = description.findProperties();

        int allProperties = 0;
        for (BeanPropertyDefinition property : properties) {
            if ("all".equals(property.getName())) {
                allProperties++;
                property.getSetter();
            }
        }

        assertTrue("ObjectNode must not have duplicate logical 'all' setters",
                allProperties <= 1);
    }

    @Test
    public void setAllMapReplacesValuesAndConvertsNullToNullNode() {
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        node.put("existing", 1);

        Map<String, JsonNode> additions = new LinkedHashMap<String, JsonNode>();
        additions.put("existing", JsonNodeFactory.instance.numberNode(3));
        additions.put("missing", null);

        node.setAll(additions);

        assertEquals(3, node.get("existing").asInt());
        assertNotNull(node.get("missing"));
        assertTrue(node.get("missing").isNull());
    }

    @Test
    public void setAllObjectNodeCopiesItsFields() {
        ObjectNode target = new ObjectNode(JsonNodeFactory.instance);
        target.put("shared", "old");

        ObjectNode source = new ObjectNode(JsonNodeFactory.instance);
        source.put("shared", "new");
        source.put("added", true);

        target.setAll(source);

        assertEquals("new", target.get("shared").asText());
        assertTrue(target.get("added").asBoolean());
    }
}
