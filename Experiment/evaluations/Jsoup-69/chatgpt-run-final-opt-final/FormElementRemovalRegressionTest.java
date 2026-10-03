package org.jsoup.nodes;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.parser.Tag;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class FormElementRemovalRegressionTest {

    @Test
    public void removingRegisteredControlRemovesItFromElementsList() {
        Document document = Jsoup.parse(
                "<form><input name='one' value='1'><input name='two' value='2'><input name='three' value='3'></form>");
        FormElement form = (FormElement) document.select("form").first();

        assertEquals(3, form.elements().size());
        Element removed = form.elements().get(1);

        removed.remove();

        assertEquals(2, form.elements().size());
        assertFalse(form.elements().contains(removed));
    }

    @Test
    public void removingRegisteredControlExcludesItFromFormData() {
        Document document = Jsoup.parse(
                "<form><input name='keep' value='yes'><input name='discard' value='no'><input name='later' value='after'></form>");
        FormElement form = (FormElement) document.select("form").first();

        form.elements().get(1).remove();

        List<Connection.KeyVal> data = form.formData();
        assertEquals(2, data.size());
        assertEquals("keep", data.get(0).key());
        assertEquals("yes", data.get(0).value());
        assertEquals("later", data.get(1).key());
        assertEquals("after", data.get(1).value());
    }

    @Test
    public void remainingRegisteredControlsPersistAfterRemovingDifferentControl() {
        Document document = Jsoup.parse(
                "<form><input name='first' value='a'><input name='middle' value='b'><input name='last' value='c'></form>");
        FormElement form = (FormElement) document.select("form").first();
        Element first = form.elements().get(0);
        Element middle = form.elements().get(1);
        Element last = form.elements().get(2);

        middle.remove();

        assertEquals(2, form.elements().size());
        assertSame(first, form.elements().get(0));
        assertSame(last, form.elements().get(1));
        assertTrue(form.elements().contains(first));
        assertTrue(form.elements().contains(last));
    }

    @Test
    public void addElementRegistersControlAndReturnsSameForm() {
        FormElement form = new FormElement(Tag.valueOf("form"), "http://example.com/", new Attributes());
        Element input = new Element(Tag.valueOf("input"), "http://example.com/");
        input.attr("name", "query");
        input.attr("value", "jsoup");

        assertSame(form, form.addElement(input));
        assertEquals(1, form.elements().size());

        List<Connection.KeyVal> data = form.formData();
        assertEquals(1, data.size());
        assertEquals("query", data.get(0).key());
        assertEquals("jsoup", data.get(0).value());
    }

    @Test(expected = IllegalArgumentException.class)
    public void submitRejectsFormWithoutActionOrBaseUri() {
        FormElement form = new FormElement(Tag.valueOf("form"), "", new Attributes());

        form.submit();
    }

@Test
public void formDataIncludesAllSelectedOptionsAndCheckedCheckableControls() {
    org.jsoup.nodes.FormElement form = (org.jsoup.nodes.FormElement) org.jsoup.Jsoup.parse(
            "<form><select name='choice'><option value='one' selected>One</option><option value='two' selected>Two</option><option value='three'>Three</option></select>"
                    + "<input type='checkbox' name='check' checked><input type='checkbox' name='skip'>"
                    + "<input type='radio' name='radio' value='chosen' checked></form>")
            .select("form").first();

    java.util.List<org.jsoup.Connection.KeyVal> data = form.formData();

    assertEquals(4, data.size());
    assertEquals("choice", data.get(0).key());
    assertEquals("one", data.get(0).value());
    assertEquals("choice", data.get(1).key());
    assertEquals("two", data.get(1).value());
    assertEquals("check", data.get(2).key());
    assertEquals("on", data.get(2).value());
    assertEquals("radio", data.get(3).key());
    assertEquals("chosen", data.get(3).value());
}

@Test
public void formDataUsesFirstOptionWhenNoOptionIsSelectedAndOmitsEmptySelect() {
    org.jsoup.nodes.FormElement form = (org.jsoup.nodes.FormElement) org.jsoup.Jsoup.parse(
            "<form><select name='fallback'><option value='first'>First</option><option value='second'>Second</option></select>"
                    + "<select name='empty'></select></form>")
            .select("form").first();

    java.util.List<org.jsoup.Connection.KeyVal> data = form.formData();

    assertEquals(1, data.size());
    assertEquals("fallback", data.get(0).key());
    assertEquals("first", data.get(0).value());
}
}
