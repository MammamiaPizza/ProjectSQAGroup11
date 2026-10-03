package org.jsoup.nodes;

import org.jsoup.parser.Tag;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AttributesBug57Test {

    @Test
    public void getAndSizeReflectEmptyAndAbsentAttributes() {
        Attributes attributes = new Attributes();

        assertEquals(0, attributes.size());
        assertEquals("", attributes.get("missing"));
        assertFalse(attributes.hasKey("missing"));

        attributes.put("present", "value");

        assertEquals(1, attributes.size());
        assertEquals("value", attributes.get("present"));
        assertTrue(attributes.hasKey("present"));
        assertEquals("", attributes.get("missing"));
    }

    @Test
    public void removeIsCaseSensitiveAndAbsentRemovalDoesNotChangeAttributes() {
        Attributes attributes = new Attributes();
        attributes.put("Name", "first");
        attributes.put("name", "second");

        attributes.remove("NAME");

        assertEquals(2, attributes.size());
        assertEquals("first", attributes.get("Name"));
        assertEquals("second", attributes.get("name"));

        attributes.remove("Name");

        assertEquals(1, attributes.size());
        assertFalse(attributes.hasKey("Name"));
        assertTrue(attributes.hasKey("name"));
        assertEquals("", attributes.get("Name"));
    }

    @Test
    public void removeIgnoreCaseRemovesAllCaseInsensitiveMatchesWithoutAffectingOthers() {
        Attributes attributes = new Attributes();
        attributes.put("First", "one");
        attributes.put("unrelated", "keep");
        attributes.put("FIRST", "two");
        attributes.put("last", "three");

        attributes.removeIgnoreCase("first");

        assertEquals(2, attributes.size());
        assertFalse(attributes.hasKey("First"));
        assertFalse(attributes.hasKey("FIRST"));
        assertFalse(attributes.hasKeyIgnoreCase("first"));
        assertEquals("", attributes.getIgnoreCase("FIRST"));
        assertEquals("keep", attributes.get("unrelated"));
        assertEquals("three", attributes.get("last"));
    }

    @Test
    public void iteratorRemoveCanRemoveEveryCurrentAttribute() {
        Attributes attributes = new Attributes();
        attributes.put("one", "1");
        attributes.put("two", "2");
        attributes.put("three", "3");

        Iterator<Attribute> iterator = attributes.iterator();
        int removed = 0;
        while (iterator.hasNext()) {
            Attribute attribute = iterator.next();
            assertTrue(attributes.hasKey(attribute.getKey()));
            iterator.remove();
            removed++;
        }

        assertEquals(3, removed);
        assertEquals(0, attributes.size());
        assertFalse(attributes.iterator().hasNext());
        assertEquals("", attributes.get("one"));
    }

    @Test
    public void chainedElementAttributeRemovalCompletesAndReturnsSameElement() {
        Element element = new Element(Tag.valueOf("div"), "");
        element.attr("First", "one");
        element.attr("middle", "two");
        element.attr("Last", "three");

        Node result = element.removeAttr("FIRST").removeAttr("LAST");

        assertSame(element, result);
        assertEquals(1, element.attributes().size());
        assertFalse(element.attributes().hasKeyIgnoreCase("first"));
        assertFalse(element.attributes().hasKeyIgnoreCase("last"));
        assertEquals("two", element.attributes().get("middle"));
    }

    @Test
    public void removeOperationsRejectEmptyKeys() {
        Attributes attributes = new Attributes();

        try {
            attributes.remove("");
            fail("Removing an empty key should be rejected");
        } catch (IllegalArgumentException expected) {
            // expected
        }

        try {
            attributes.removeIgnoreCase("");
            fail("Case-insensitive removal of an empty key should be rejected");
        } catch (IllegalArgumentException expected) {
            // expected
        }

        assertEquals(0, attributes.size());
    }

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
}
