@org.junit.Test
public void deepCopyDoesNotShareNestedObjectNodes() {
    com.fasterxml.jackson.databind.node.ObjectNode original =
            new com.fasterxml.jackson.databind.node.ObjectNode(
                    com.fasterxml.jackson.databind.node.JsonNodeFactory.instance);
    com.fasterxml.jackson.databind.node.ObjectNode child =
            new com.fasterxml.jackson.databind.node.ObjectNode(
                    com.fasterxml.jackson.databind.node.JsonNodeFactory.instance);
    child.put("number", 1);
    original.set("child", child);

    com.fasterxml.jackson.databind.node.ObjectNode copy = original.deepCopy();
    child.put("number", 2);

    org.junit.Assert.assertEquals(1, copy.get("child").get("number").asInt());
    org.junit.Assert.assertEquals(2, original.get("child").get("number").asInt());
}

@org.junit.Test
public void findParentFindsDirectAndNestedParents() {
    com.fasterxml.jackson.databind.node.ObjectNode root =
            new com.fasterxml.jackson.databind.node.ObjectNode(
                    com.fasterxml.jackson.databind.node.JsonNodeFactory.instance);
    com.fasterxml.jackson.databind.node.ObjectNode child =
            new com.fasterxml.jackson.databind.node.ObjectNode(
                    com.fasterxml.jackson.databind.node.JsonNodeFactory.instance);
    child.put("needle", true);
    root.set("child", child);

    org.junit.Assert.assertSame(root, root.findParent("child"));
    org.junit.Assert.assertSame(child, root.findParent("needle"));
    org.junit.Assert.assertNull(root.findParent("missing"));
}

@org.junit.Test
public void constructorWithMapExposesFieldsThroughAllIterators() {
    java.util.Map<String, com.fasterxml.jackson.databind.JsonNode> children =
            new java.util.LinkedHashMap<String, com.fasterxml.jackson.databind.JsonNode>();
    com.fasterxml.jackson.databind.node.ObjectNode values =
            new com.fasterxml.jackson.databind.node.ObjectNode(
                    com.fasterxml.jackson.databind.node.JsonNodeFactory.instance);
    values.put("one", 1);
    values.put("two", 2);
    children.put("one", values.get("one"));
    children.put("two", values.get("two"));

    com.fasterxml.jackson.databind.node.ObjectNode node =
            new com.fasterxml.jackson.databind.node.ObjectNode(
                    com.fasterxml.jackson.databind.node.JsonNodeFactory.instance, children);

    org.junit.Assert.assertEquals(2, node.size());
    org.junit.Assert.assertEquals(1, node.get("one").asInt());

    java.util.Set<String> names = new java.util.HashSet<String>();
    java.util.Iterator<String> nameIterator = node.fieldNames();
    while (nameIterator.hasNext()) {
        names.add(nameIterator.next());
    }
    org.junit.Assert.assertEquals(children.keySet(), names);

    int valueTotal = 0;
    java.util.Iterator<com.fasterxml.jackson.databind.JsonNode> valueIterator = node.elements();
    while (valueIterator.hasNext()) {
        valueTotal += valueIterator.next().asInt();
    }
    org.junit.Assert.assertEquals(3, valueTotal);

    java.util.Set<String> fieldNames = new java.util.HashSet<String>();
    java.util.Iterator<java.util.Map.Entry<String, com.fasterxml.jackson.databind.JsonNode>> fields =
            node.fields();
    while (fields.hasNext()) {
        fieldNames.add(fields.next().getKey());
    }
    org.junit.Assert.assertEquals(children.keySet(), fieldNames);
}

@org.junit.Test
public void equalsAndHashCodeReflectChildrenAndType() {
    com.fasterxml.jackson.databind.node.ObjectNode first =
            new com.fasterxml.jackson.databind.node.ObjectNode(
                    com.fasterxml.jackson.databind.node.JsonNodeFactory.instance);
    com.fasterxml.jackson.databind.node.ObjectNode second =
            new com.fasterxml.jackson.databind.node.ObjectNode(
                    com.fasterxml.jackson.databind.node.JsonNodeFactory.instance);
    first.put("value", "same");
    second.put("value", "same");

    org.junit.Assert.assertTrue(first.equals(second));
    org.junit.Assert.assertEquals(first.hashCode(), second.hashCode());
    org.junit.Assert.assertFalse(first.equals("not an object node"));

    second.put("value", "different");
    org.junit.Assert.assertFalse(first.equals(second));
}