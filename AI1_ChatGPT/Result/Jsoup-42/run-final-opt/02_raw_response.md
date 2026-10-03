package org.jsoup.nodes;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class FormElementGeneratedTest {
    private FormElement form(String html) {
        Document document = Jsoup.parse(html);
        return (FormElement) document.select("form").first();
    }

    @Test
    public void formDataIncludesSuccessfulControlsAndExcludesButtonAndUncheckedControls() {
        FormElement form = form(
            "<form>"
                + "<input name='text' value='alpha'>"
                + "<input type='hidden' name='hidden' value='secret'>"
                + "<input type='checkbox' name='checked' value='yes' checked>"
                + "<input type='checkbox' name='unchecked' value='no'>"
                + "<input type='radio' name='choice' value='a' checked>"
                + "<input type='radio' name='otherChoice' value='b'>"
                + "<select name='selection'><option value='one' selected>One</option>"
                + "<option value='two'>Two</option></select>"
                + "<textarea name='notes'>hello</textarea>"
                + "<input type='button' name='button' value='do not submit'>"
                + "<input value='unnamed'>"
                + "<div name='notAControl'>ignored</div>"
                + "</form>");

        List<Connection.KeyVal> data = form.formData();

        assertEquals(6, data.size());
        assertEquals("text", data.get(0).key());
        assertEquals("alpha", data.get(0).value());
        assertEquals("hidden", data.get(1).key());
        assertEquals("secret", data.get(1).value());
        assertEquals("checked", data.get(2).key());
        assertEquals("yes", data.get(2).value());
        assertEquals("choice", data.get(3).key());
        assertEquals("a", data.get(3).value());
        assertEquals("selection", data.get(4).key());
        assertEquals("one", data.get(4).value());
        assertEquals("notes", data.get(5).key());
        assertEquals("hello", data.get(5).value());
    }

    @Test
    public void checkedCheckboxWithoutValueUsesOnAsDefaultValue() {
        FormElement form = form("<form><input type='checkbox' name='agree' checked></form>");

        List<Connection.KeyVal> data = form.formData();

        assertEquals(1, data.size());
        assertEquals("agree", data.get(0).key());
        assertEquals("on", data.get(0).value());
    }

    @Test
    public void uncheckedCheckboxesAndRadiosAreNotSubmitted() {
        FormElement form = form(
            "<form>"
                + "<input name='kept' value='value'>"
                + "<input type='checkbox' name='uncheckedBox' value='box'>"
                + "<input type='radio' name='uncheckedRadio' value='radio'>"
                + "</form>");

        List<Connection.KeyVal> data = form.formData();

        assertEquals(1, data.size());
        assertEquals("kept", data.get(0).key());
        assertEquals("value", data.get(0).value());
    }

    @Test
    public void disabledControlsAreNotSubmitted() {
        FormElement form = form(
            "<form>"
                + "<input name='enabled' value='kept'>"
                + "<input name='disabledText' value='ignored' disabled>"
                + "<input type='checkbox' name='disabledCheck' value='ignored' checked disabled>"
                + "<select name='disabledSelect' disabled><option value='ignored' selected>Ignored</option></select>"
                + "</form>");

        List<Connection.KeyVal> data = form.formData();

        assertEquals(1, data.size());
        assertEquals("enabled", data.get(0).key());
        assertEquals("kept", data.get(0).value());
    }

    @Test
    public void selectUsesSelectedOptionsAndDefaultsToFirstOptionWhenNoneSelected() {
        FormElement form = form(
            "<form>"
                + "<select name='multi'>"
                + "<option value='one' selected>One</option>"
                + "<option value='two' selected>Two</option>"
                + "</select>"
                + "<select name='fallback'>"
                + "<option value='first'>First</option>"
                + "<option value='second'>Second</option>"
                + "</select>"
                + "</form>");

        List<Connection.KeyVal> data = form.formData();

        assertEquals(3, data.size());
        assertEquals("multi", data.get(0).key());
        assertEquals("one", data.get(0).value());
        assertEquals("multi", data.get(1).key());
        assertEquals("two", data.get(1).value());
        assertEquals("fallback", data.get(2).key());
        assertEquals("first", data.get(2).value());
    }

    @Test
    public void returnedFormDataListCanBeChangedWithoutChangingFormValues() {
        FormElement form = form("<form><input name='field' value='original'></form>");

        List<Connection.KeyVal> first = form.formData();
        first.clear();
        List<Connection.KeyVal> second = form.formData();

        assertEquals(0, first.size());
        assertEquals(1, second.size());
        assertEquals("field", second.get(0).key());
        assertEquals("original", second.get(0).value());
    }
}