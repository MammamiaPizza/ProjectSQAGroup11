@org.junit.Test
public void booleanAttributeWithNullValueSerializesAsCollapsedAttribute() {
    Attribute attribute = new Attribute("checked", null);

    org.junit.Assert.assertEquals("checked", attribute.html());
}

@org.junit.Test
public void createFromEncodedUnescapesAttributeValue() {
    Attribute attribute = Attribute.createFromEncoded("data-query", "one&amp;two");

    org.junit.Assert.assertEquals("data-query", attribute.getKey());
    org.junit.Assert.assertEquals("one&two", attribute.getValue());
}

@org.junit.Test
public void htmlEscapesNonBooleanAttributeValues() {
    Attribute attribute = new Attribute("title", "one & two");

    org.junit.Assert.assertEquals("title=\"one &amp; two\"", attribute.html());
}

@org.junit.Test
public void clonedAttributesAreEqualButIndependent() {
    Attribute original = new Attribute("title", "original");
    Attribute clone = original.clone();

    org.junit.Assert.assertNotSame(original, clone);
    org.junit.Assert.assertEquals(original, clone);
    org.junit.Assert.assertEquals(original.hashCode(), clone.hashCode());

    clone.setValue("changed");

    org.junit.Assert.assertEquals("original", original.getValue());
    org.junit.Assert.assertEquals("changed", clone.getValue());
    org.junit.Assert.assertFalse(original.equals(null));
    org.junit.Assert.assertFalse(original.equals(new Attribute("name", "original")));
}