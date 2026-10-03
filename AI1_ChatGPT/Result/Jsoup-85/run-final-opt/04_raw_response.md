@Test(expected = IllegalArgumentException.class)
public void setKeyRejectsNullKey() {
    new Attribute("id", "value").setKey(null);
}

@Test
public void createFromEncodedDecodesAttributeValue() {
    Attribute attribute = Attribute.createFromEncoded("title", "Tom &amp; Jerry");

    assertEquals("title", attribute.getKey());
    assertEquals("Tom & Jerry", attribute.getValue());
}

@Test
public void htmlEscapesValuesAndToStringMatchesHtml() {
    Attribute attribute = new Attribute("title", "Tom & Jerry");

    assertEquals("title=\"Tom &amp; Jerry\"", attribute.html());
    assertEquals(attribute.html(), attribute.toString());
}

@Test
public void cloneProducesEqualIndependentAttribute() {
    Attribute original = new Attribute("id", "value");
    Attribute clone = original.clone();

    assertEquals(original, clone);
    assertEquals(original.hashCode(), clone.hashCode());
    assertEquals(false, original.equals(null));
    assertEquals(false, original.equals("id=value"));

    clone.setValue("changed");
    assertEquals("value", original.getValue());
    assertEquals("changed", clone.getValue());
    assertEquals(false, original.equals(clone));
}