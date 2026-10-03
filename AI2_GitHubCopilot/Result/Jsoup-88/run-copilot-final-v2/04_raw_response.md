@Test public void testSetKeyWithParentTrimmed() {
    Attributes attrs = new Attributes();
    attrs.put("original", "val");
    Attribute attr = attrs.attribute("original");
    attr.setKey(" newKey ");
    assertEquals("newKey", attr.getKey());
    assertSame(attr, attrs.attribute("newKey"));
    assertNull(attrs.attribute("original"));
}

@Test public void testSetValueWithParent() {
    Attributes attrs = new Attributes();
    attrs.put("key", "oldVal");
    Attribute attr = attrs.attribute("key");
    assertEquals("oldVal", attr.setValue("newVal"));
    assertEquals("newVal", attr.getValue());
    assertEquals("newVal", attrs.attribute("key").getValue());
}

@Test public void testCreateFromEncoded() {
    Attribute attr = Attribute.createFromEncoded("key", "&");
    assertEquals("key", attr.getKey());
    assertEquals("&", attr.getValue());
}

@Test public void testIsDataAttribute() {
    assertTrue(new Attribute("data-key", "val").isDataAttribute());
    assertFalse(new Attribute("id", "val").isDataAttribute());
}