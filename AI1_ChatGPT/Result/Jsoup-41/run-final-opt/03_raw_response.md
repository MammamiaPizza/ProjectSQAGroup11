package org.jsoup.nodes;

import org.jsoup.parser.Tag;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ElementEqualsHashCodeTest {

    private Element element(String tagName, String baseUri, String className, String text) {
        Element element = new Element(Tag.valueOf(tagName), baseUri);
        if (className != null)
            element.attr("class", className);
        if (text != null)
            element.appendText(text);
        return element;
    }

    @Test
    public void equalElementsWithEquivalentStateAreEqualAndHaveSameHashCode() {
        Element first = element("p", "http://example.com/", "one", "One");
        Element second = element("p", "http://example.com/", "one", "One");
        Element third = element("p", "http://example.com/", "one", "One");

        assertEquals(first, second);
        assertEquals(second, first);
        assertEquals(second, third);
        assertEquals(first, third);
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals(second.hashCode(), third.hashCode());
    }

    @Test
    public void elementIsEqualToItself() {
        Element element = element("p", "http://example.com/", "one", "One");

        assertTrue(element.equals(element));
        assertEquals(element.hashCode(), element.hashCode());
    }

    @Test
    public void elementIsNotEqualToNullOrNonElementObject() {
        Element element = element("p", "http://example.com/", "one", "One");

        assertFalse(element.equals(null));
        assertFalse(element.equals("<p class=\"one\">One</p>"));
    }

    @Test
    public void elementsDifferingOnlyInAttributesAreNotEqual() {
        Element first = element("p", "http://example.com/", "one", "One");
        Element second = element("p", "http://example.com/", "two", "One");

        assertFalse(first.equals(second));
        assertFalse(second.equals(first));
    }

    @Test
    public void elementsDifferingInChildContentAreNotEqual() {
        Element first = element("p", "http://example.com/", "one", "One");
        Element second = element("p", "http://example.com/", "one", "Two");

        assertFalse(first.equals(second));
        assertFalse(second.equals(first));
    }

    @Test
    public void elementsDifferingInTagAreNotEqual() {
        Element paragraph = element("p", "http://example.com/", "one", "One");
        Element division = element("div", "http://example.com/", "one", "One");

        assertFalse(paragraph.equals(division));
        assertFalse(division.equals(paragraph));
    }

    @Test
    public void elementsDifferingInBaseUriAreNotEqual() {
        Element first = element("p", "http://example.com/one/", "one", "One");
        Element second = element("p", "http://example.com/two/", "one", "One");

        assertEquals(first, second);
        assertEquals(second, first);
    }

    @Test
    public void equalNestedElementsHaveEqualHashCodes() {
        Element first = new Element(Tag.valueOf("div"), "http://example.com/");
        first.appendElement("span").attr("data-key", "value").text("child");

        Element second = new Element(Tag.valueOf("div"), "http://example.com/");
        second.appendElement("span").attr("data-key", "value").text("child");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}