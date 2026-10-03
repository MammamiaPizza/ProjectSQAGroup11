package org.jsoup.nodes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AttributeNullValueTest {
    @Test
    public void nullBooleanAttributeValueIsExposedAsEmptyString() {
        Attribute attribute = new Attribute("checked", null);

        assertEquals("", attribute.getValue());
    }

    @Test
    public void nullBooleanAttributeValueMatchesExplicitEmptyValue() {
        Attribute nullValue = new Attribute("disabled", null);
        Attribute emptyValue = new Attribute("disabled", "");

        assertEquals(emptyValue.getValue(), nullValue.getValue());
    }

    @Test
    public void explicitEmptyBooleanAttributeValueRemainsEmpty() {
        Attribute attribute = new Attribute("selected", "");

        assertEquals("", attribute.getValue());
    }

    @Test
    public void nullValueForNonBooleanAttributeIsAlsoExposedAsEmptyString() {
        Attribute attribute = new Attribute("data-state", null);

        assertEquals("", attribute.getValue());
    }
}