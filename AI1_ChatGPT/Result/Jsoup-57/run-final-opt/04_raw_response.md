@Test
public void addAllCopiesIncomingAttributesAndIgnoresEmptyIncoming() {
    Attributes emptyIncoming = new Attributes();
    Attributes target = new Attributes();
    target.addAll(emptyIncoming);
    assertEquals(0, target.size());

    Attributes incoming = new Attributes();
    incoming.put("one", "1");
    incoming.put("two", "2");

    target.addAll(incoming);
    assertEquals(2, target.size());
    assertEquals("1", target.get("one"));
    assertEquals("2", target.get("two"));

    Attributes existing = new Attributes();
    existing.put("keep", "yes");
    existing.addAll(incoming);
    assertEquals(3, existing.size());
    assertEquals("yes", existing.get("keep"));
    assertEquals("1", existing.get("one"));
}

@Test(expected = java.lang.UnsupportedOperationException.class)
public void asListReflectsAttributeCountAndIsUnmodifiable() {
    Attributes attributes = new Attributes();
    assertEquals(0, attributes.asList().size());

    attributes.put("one", "1");
    attributes.put("two", "2");
    java.util.List<Attribute> list = attributes.asList();

    assertEquals(2, list.size());
    list.clear();
}

@Test
public void cloneCopiesAttributesIntoAnIndependentContainer() {
    Attributes original = new Attributes();
    original.put("name", "original");

    Attributes clone = original.clone();
    assertEquals("original", clone.get("name"));
    assertEquals(1, clone.size());

    clone.put("name", "clone");
    clone.put("extra", "value");

    assertEquals("original", original.get("name"));
    assertFalse(original.hasKey("extra"));
    assertEquals("clone", clone.get("name"));
    assertTrue(clone.hasKey("extra"));
}

@Test
public void datasetExposesOnlyDataAttributesAndWritesWithDataPrefix() {
    Attributes attributes = new Attributes();
    attributes.put("id", "identifier");
    attributes.put("data-state", "ready");

    java.util.Map<String, String> dataset = attributes.dataset();
    assertEquals(1, dataset.size());
    assertTrue(dataset.containsKey("state"));
    assertEquals("ready", dataset.get("state"));
    assertFalse(dataset.containsKey("id"));

    dataset.put("color", "blue");
    assertEquals("blue", attributes.get("data-color"));
    assertEquals("blue", dataset.get("color"));
}