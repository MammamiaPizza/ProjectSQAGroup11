package org.jsoup.nodes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AttributeValidationTest {
    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsEmptyKey() {
        new Attribute("", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsWhitespaceOnlyKey() {
        new Attribute("   ", "value");
    }

    @Test
    public void constructorStoresTrimmedKeyAndValue() {
        Attribute attribute = new Attribute("  data-id  ", "123");

        assertEquals("data-id", attribute.getKey());
        assertEquals("123", attribute.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setKeyRejectsEmptyKey() {
        Attribute attribute = new Attribute("id", "value");

        attribute.setKey("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setKeyRejectsWhitespaceOnlyKey() {
        Attribute attribute = new Attribute("id", "value");

        attribute.setKey(" \t ");
    }

    @Test
    public void setKeyUpdatesAndTrimsKey() {
        Attribute attribute = new Attribute("id", "value");

        attribute.setKey("  class  ");

        assertEquals("class", attribute.getKey());
    }

    @Test
    public void setValueReturnsPreviousValueAndUpdatesStandaloneAttribute() {
        Attribute attribute = new Attribute("id", "old");

        String previous = attribute.setValue("new");

        assertEquals("old", previous);
        assertEquals("new", attribute.getValue());
    }
}
