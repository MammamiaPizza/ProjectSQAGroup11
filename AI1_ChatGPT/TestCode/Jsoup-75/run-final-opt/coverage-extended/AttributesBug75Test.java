package org.jsoup.nodes;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AttributesBug75Test {
    @Test
    public void serializesEmptyBooleanAttributesWithoutEmptyValue() {
        Attributes attributes = new Attributes();
        attributes.put("noshade", "");
        attributes.put("nohref", "");
        attributes.put("async", "");
        attributes.put("autofocus", "");

        assertEquals(" noshade nohref async autofocus", attributes.html());
    }

    @Test
    public void serializesMixedValuedAndEmptyBooleanAttributes() {
        Attributes attributes = new Attributes();
        attributes.put("src", "foo");
        attributes.put("noshade", "");
        attributes.put("nohref", "");
        attributes.put("async", "");

        assertEquals(" src=\"foo\" noshade nohref async", attributes.html());
    }

    @Test
    public void booleanPutAddsAndFalseRemovesAttribute() {
        Attributes attributes = new Attributes();

        attributes.put("checked", true);

        assertTrue(attributes.hasKey("checked"));
        assertEquals("", attributes.get("checked"));
        assertEquals(" checked", attributes.html());

        attributes.put("checked", false);

        assertFalse(attributes.hasKey("checked"));
        assertEquals(0, attributes.size());
        assertEquals("", attributes.html());
    }

    @Test
    public void retainsEmptyValueForNonBooleanAttribute() {
        Attributes attributes = new Attributes();
        attributes.put("data-state", "");

        assertEquals(" data-state=\"\"", attributes.html());
    }

    @Test
    public void xmlSyntaxKeepsExplicitEmptyValueForBooleanAttribute() throws IOException {
        Attributes attributes = new Attributes();
        attributes.put("async", "");

        Document.OutputSettings outputSettings = new Document.OutputSettings();
        outputSettings.syntax(Document.OutputSettings.Syntax.xml);
        StringBuilder output = new StringBuilder();

        attributes.html(output, outputSettings);

        assertEquals(" async=\"\"", output.toString());
    }

    @Test
    public void booleanPutIsVisibleThroughAttributeListAsEmptyValue() {
        Attributes attributes = new Attributes();
        attributes.put("async", true);

        List<Attribute> attributesList = attributes.asList();

        assertEquals(1, attributesList.size());
        assertEquals("async", attributesList.get(0).getKey());
        assertNull(attributesList.get(0).getValue());
    }

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
}
