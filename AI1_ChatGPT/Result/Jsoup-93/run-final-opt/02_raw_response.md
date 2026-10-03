package org.jsoup.nodes;

import org.jsoup.Connection;
import org.jsoup.parser.Tag;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class FormElementGeneratedTest {

    private FormElement form(String baseUri) {
        return new FormElement(Tag.valueOf("form"), baseUri, new Attributes());
    }

    private Element input(String type, String name, String value) {
        Element input = new Element(Tag.valueOf("input"), "", new Attributes());
        if (type != null)
            input.attr("type", type);
        if (name != null)
            input.attr("name", name);
        if (value != null)
            input.attr("value", value);
        return input;
    }

    @Test
    public void formDataDoesNotSubmitButtonInputs() {
        FormElement form = form("http://example.com/");

        form.addElement(input("text", "text", "one"));
        form.addElement(input("hidden", "hidden", "two"));

        Element checkbox = input("checkbox", "check", null);
        checkbox.attr("checked", "checked");
        form.addElement(checkbox);

        Element radio = input("radio", "radio", "four");
        radio.attr("checked", "checked");
        form.addElement(radio);

        Element select = new Element(Tag.valueOf("select"), "", new Attributes());
        select.attr("name", "select");
        Element option = new Element(Tag.valueOf("option"), "", new Attributes());
        option.attr("value", "five");
        option.attr("selected", "selected");
        select.appendChild(option);
        form.addElement(select);

        form.addElement(input("text", "last", "six"));
        form.addElement(input("button", "button", "must-not-be-submitted"));

        List<Connection.KeyVal> data = form.formData();

        assertEquals(6, data.size());
        assertEquals("text", data.get(0).key());
        assertEquals("one", data.get(0).value());
        assertEquals("select", data.get(4).key());
        assertEquals("five", data.get(4).value());
        assertEquals("last", data.get(5).key());
        assertEquals("six", data.get(5).value());
    }

    @Test
    public void formDataUsesOnForCheckedCheckboxWithoutValueAndSkipsIneligibleInputs() {
        FormElement form = form("http://example.com/");

        Element checked = input("checkbox", "enabled", null);
        checked.attr("checked", "checked");
        form.addElement(checked);

        form.addElement(input("checkbox", "unchecked", "no"));

        Element disabled = input("checkbox", "disabled", "no");
        disabled.attr("checked", "checked");
        disabled.attr("disabled", "disabled");
        form.addElement(disabled);

        form.addElement(input("text", "", "unnamed"));

        List<Connection.KeyVal> data = form.formData();

        assertEquals(1, data.size());
        assertEquals("enabled", data.get(0).key());
        assertEquals("on", data.get(0).value());
    }

    @Test
    public void formDataUsesSelectedOptionsOrFirstOptionWhenNoneSelected() {
        FormElement form = form("http://example.com/");

        Element selected = new Element(Tag.valueOf("select"), "", new Attributes());
        selected.attr("name", "selected");
        Element firstSelected = new Element(Tag.valueOf("option"), "", new Attributes());
        firstSelected.attr("value", "one");
        firstSelected.attr("selected", "selected");
        Element secondSelected = new Element(Tag.valueOf("option"), "", new Attributes());
        secondSelected.attr("value", "two");
        secondSelected.attr("selected", "selected");
        selected.appendChild(firstSelected);
        selected.appendChild(secondSelected);
        form.addElement(selected);

        Element fallback = new Element(Tag.valueOf("select"), "", new Attributes());
        fallback.attr("name", "fallback");
        Element first = new Element(Tag.valueOf("option"), "", new Attributes());
        first.attr("value", "first");
        Element second = new Element(Tag.valueOf("option"), "", new Attributes());
        second.attr("value", "second");
        fallback.appendChild(first);
        fallback.appendChild(second);
        form.addElement(fallback);

        List<Connection.KeyVal> data = form.formData();

        assertEquals(3, data.size());
        assertEquals("selected", data.get(0).key());
        assertEquals("one", data.get(0).value());
        assertEquals("selected", data.get(1).key());
        assertEquals("two", data.get(1).value());
        assertEquals("fallback", data.get(2).key());
        assertEquals("first", data.get(2).value());
    }

    @Test
    public void removingChildAlsoRemovesAssociatedFormControl() {
        FormElement form = form("http://example.com/");
        Element control = input("text", "gone", "value");

        form.appendChild(control);
        form.addElement(control);
        assertEquals(1, form.elements().size());

        form.removeChild(control);

        assertTrue(form.elements().isEmpty());
        assertTrue(form.formData().isEmpty());
    }

    @Test
    public void formDataReturnsIndependentListCopies() {
        FormElement form = form("http://example.com/");
        form.addElement(input("text", "field", "value"));

        List<Connection.KeyVal> first = form.formData();
        first.clear();

        List<Connection.KeyVal> second = form.formData();
        assertEquals(1, second.size());
        assertEquals("field", second.get(0).key());
        assertEquals("value", second.get(0).value());
    }

    @Test
    public void submitUsesResolvedActionMethodAndFormValues() {
        FormElement form = form("http://example.com/forms/page");
        form.attr("action", "/submit");
        form.attr("method", "post");
        form.addElement(input("text", "query", "jsoup"));

        Connection connection = form.submit();

        assertNotNull(connection);
        assertEquals("http://example.com/submit", connection.url().toExternalForm());
        assertEquals(Connection.Method.POST, connection.method());
        assertEquals(1, connection.request().data().size());
        Connection.KeyVal submitted = connection.request().data().iterator().next();
        assertEquals("query", submitted.key());
        assertEquals("jsoup", submitted.value());
    }

    @Test(expected = IllegalArgumentException.class)
    public void submitRejectsFormWithoutActionOrBaseUri() {
        form("").submit();
    }

    private static void assertTrue(boolean value) {
        assertEquals(true, value);
    }
}