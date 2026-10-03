@Test
public void addAllMergesIncomingAttributesAndIgnoresEmptyIncoming() {
    Attributes attributes = new Attributes();
    attributes.put("id", "original");

    attributes.addAll(new Attributes());
    assertEquals("original", attributes.get("id"));
    assertEquals(1, attributes.size());

    Attributes incoming = new Attributes();
    incoming.put("id", "replacement");
    incoming.put("class", "note");
    attributes.addAll(incoming);

    assertEquals("replacement", attributes.get("id"));
    assertEquals("note", attributes.get("class"));
    assertEquals(2, attributes.size());
}

@Test
public void cloneDoesNotShareAttributeValuesWithOriginal() {
    Attributes original = new Attributes();
    original.put("id", "one");

    Attributes clone = original.clone();
    clone.put("id", "two");

    assertEquals("one", original.get("id"));
    assertEquals("two", clone.get("id"));
}

@Test
public void attributeListIncludesValuedAttributes() {
    Attributes attributes = new Attributes();
    attributes.put("id", "header");

    assertEquals(1, attributes.asList().size());
    assertEquals("id", attributes.asList().get(0).getKey());
}

@Test
public void datasetExposesAndAddsDataAttributes() {
    Attributes attributes = new Attributes();
    attributes.put("data-user", "alice");

    assertEquals("alice", attributes.dataset().get("user"));

    attributes.dataset().put("role", "admin");
    assertEquals("admin", attributes.get("data-role"));
}