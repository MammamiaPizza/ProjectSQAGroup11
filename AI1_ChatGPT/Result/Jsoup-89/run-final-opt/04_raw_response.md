@Test
public void htmlSerializesAttributeValue() {
    org.jsoup.nodes.Attribute attribute = new org.jsoup.nodes.Attribute("title", "example");

    org.junit.Assert.assertEquals("title=\"example\"", attribute.html());
    org.junit.Assert.assertEquals(attribute.html(), attribute.toString());
}

@Test
public void createFromEncodedUnescapesValue() {
    org.jsoup.nodes.Attribute attribute =
        org.jsoup.nodes.Attribute.createFromEncoded("title", "Tom &amp; Jerry");

    org.junit.Assert.assertEquals("title", attribute.getKey());
    org.junit.Assert.assertEquals("Tom & Jerry", attribute.getValue());
}

@Test
public void equalityAndHashCodeDependOnKeyAndValue() {
    org.jsoup.nodes.Attribute first = new org.jsoup.nodes.Attribute("id", "one");
    org.jsoup.nodes.Attribute same = new org.jsoup.nodes.Attribute("id", "one");
    org.jsoup.nodes.Attribute differentValue = new org.jsoup.nodes.Attribute("id", "two");

    org.junit.Assert.assertEquals(first, same);
    org.junit.Assert.assertEquals(first.hashCode(), same.hashCode());
    org.junit.Assert.assertNotEquals(first, differentValue);
    org.junit.Assert.assertNotEquals(first, null);
    org.junit.Assert.assertNotEquals(first, "id");
}

@Test
public void cloneCreatesEqualIndependentAttribute() {
    org.jsoup.nodes.Attribute original = new org.jsoup.nodes.Attribute("id", "one");

    org.jsoup.nodes.Attribute clone = original.clone();
    clone.setValue("two");

    org.junit.Assert.assertNotSame(original, clone);
    org.junit.Assert.assertEquals("one", original.getValue());
    org.junit.Assert.assertEquals("two", clone.getValue());
}