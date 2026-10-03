import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class BeanDeserializerObjectIdCreatorTest {

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    public static class Child {
        public int id;
        public final String name;
        public List<Child> children;

        @JsonCreator
        public Child(@JsonProperty("name") String name) {
            this.name = name;
        }
    }

    @Test
    public void testCreatorBasedObjectWithArrayContainingObjectIdReference() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Child child = mapper.readValue(
                "{\"id\":1,\"name\":\"root\",\"children\":[1]}",
                Child.class);

        assertEquals(1, child.id);
        assertEquals("root", child.name);
        assertNotNull(child.children);
        assertEquals(1, child.children.size());
        assertSame(child, child.children.get(0));
    }

    @Test
    public void testCreatorBasedObjectWithEmptyChildrenArray() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Child child = mapper.readValue(
                "{\"id\":7,\"name\":\"only\",\"children\":[]}",
                Child.class);

        assertEquals(7, child.id);
        assertEquals("only", child.name);
        assertNotNull(child.children);
        assertEquals(0, child.children.size());
    }

@Test
public void testCreatorBasedObjectWithNestedAndRepeatedObjectIdReferences() throws Exception {
    Child root = new com.fasterxml.jackson.databind.ObjectMapper().readValue(
            "{\"id\":1,\"name\":\"root\",\"children\":["
                    + "{\"id\":2,\"name\":\"leaf\",\"children\":[2]},2]}",
            Child.class);

    org.junit.Assert.assertEquals(1, root.id);
    org.junit.Assert.assertEquals("root", root.name);
    org.junit.Assert.assertNotNull(root.children);
    org.junit.Assert.assertEquals(2, root.children.size());

    Child leaf = root.children.get(0);
    org.junit.Assert.assertEquals(2, leaf.id);
    org.junit.Assert.assertEquals("leaf", leaf.name);
    org.junit.Assert.assertSame(leaf, root.children.get(1));
    org.junit.Assert.assertNotNull(leaf.children);
    org.junit.Assert.assertEquals(1, leaf.children.size());
    org.junit.Assert.assertSame(leaf, leaf.children.get(0));
}
}
