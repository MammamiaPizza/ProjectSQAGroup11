@Test
    public void testRemoveChildUpdatesElementsList() {
        String html = "<form><input name='one' value='two'><input name='three'
value='four'></form>";
        Document doc = Jsoup.parse(html);
        FormElement form = (FormElement) doc.select("form").first();
        Element toRemove = doc.select("input[name=one]").first();
        form.removeChild(toRemove);
        assertEquals(1, form.elements().size());
        boolean hasOne = false;
        for (Element el : form.elements()) {
            if ("one".equals(el.attr("name"))) {
                hasOne = true;
                break;
            }
        }
        assertFalse(hasOne);
    }

 @Test
 public void testSubmitCreatesConnection() {
     String html = "<form action='/search' method='post'></form>";
     Document doc = Jsoup.parse(html);
     FormElement form = (FormElement) doc.select("form").first();
     Connection conn = form.submit();
     assertNotNull(conn);
     assertEquals(Connection.Method.POST, conn.request().method());
 }

 @Test
 public void nonSubmittableElementsAreSkipped() {
     String html = "<form><input name='a' value='b'></form>";
     Document doc = Jsoup.parse(html);
     FormElement form = (FormElement) doc.select("form").first();
     form.addElement(new Element(org.jsoup.parser.Tag.valueOf("div"), ""));
     List<Connection.KeyVal> data = form.formData();
     assertTrue(data.size() >= 1);
     for (Connection.KeyVal kv : data) {
         assertTrue(kv.key().length() > 0);
     }
 }

 @Test
 public void emptySelectAddsNoData() {
     String html = "<form><select name='sel'></select></form>";
     Document doc = Jsoup.parse(html);
     FormElement form = (FormElement) doc.select("form").first();
     List<Connection.KeyVal> data = form.formData();
     boolean hasSel = false;
     for (Connection.KeyVal kv : data) {
         if ("sel".equals(kv.key())) {
             hasSel = true;
         }
     }
     assertFalse(hasSel);
 }