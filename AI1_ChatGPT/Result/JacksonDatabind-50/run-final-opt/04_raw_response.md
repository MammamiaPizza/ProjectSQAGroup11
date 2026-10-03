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