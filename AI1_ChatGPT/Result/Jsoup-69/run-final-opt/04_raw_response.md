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