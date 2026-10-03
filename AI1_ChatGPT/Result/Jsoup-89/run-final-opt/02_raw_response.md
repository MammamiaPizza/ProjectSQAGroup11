package org.jsoup.nodes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AttributeOrphanSettersTest {
    @Test
    public void setKeyUpdatesOrphanAttributeKey() {
        Attribute attribute = new Attribute("key", "value");

        attribute.setKey("newKey");

        assertEquals("newKey", attribute.getKey());
    }

    @Test
    public void setValueUpdatesOrphanAttributeAndReturnsPreviousValue() {
        Attribute attribute = new Attribute("key", "value");

        String previousValue = attribute.setValue("newValue");

        assertEquals("value", previousValue);
        assertEquals("newValue", attribute.getValue());
    }

    @Test
    public void settersCanBeUsedSequentiallyOnOrphanAttribute() {
        Attribute attribute = new Attribute("key", "value");

        attribute.setKey("renamed");
        String previousValue = attribute.setValue("updated");

        assertEquals("renamed", attribute.getKey());
        assertEquals("value", previousValue);
        assertEquals("updated", attribute.getValue());
    }
}