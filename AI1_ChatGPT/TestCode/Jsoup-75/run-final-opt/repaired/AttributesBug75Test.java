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
}
