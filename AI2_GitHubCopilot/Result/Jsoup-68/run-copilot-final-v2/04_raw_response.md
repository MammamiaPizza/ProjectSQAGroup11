@Test
public void testTableStackContextClearing() {
    String html = "<table><tbody><tr><td>cell</table>";
    Document doc = Jsoup.parse(html);
    Element body = doc.body();
    assertNotNull(body);
    Element table = body.child(0);
    assertEquals("table", table.tagName());
    Element tbody = table.child(0);
    assertEquals("tbody", tbody.tagName());
    Element tr = tbody.child(0);
    assertEquals("tr", tr.tagName());
    Element td = tr.child(0);
    assertEquals("td", td.tagName());
    assertEquals("cell", td.text());
}

@Test
public void testClearFormattingElementsToLastMarker() {
    String html = "<p><b>bold</p><p>next</p>";
    Document doc = Jsoup.parse(html);
    Element body = doc.body();
    assertEquals(2, body.children().size());
    Element p1 = body.child(0);
    assertEquals("p", p1.tagName());
    assertEquals(1, p1.children().size());
    Element b = p1.child(0);
    assertEquals("b", b.tagName());
    assertEquals("bold", b.text());
    Element p2 = body.child(1);
    assertEquals("p", p2.tagName());
    assertEquals("next", p2.text());
}

@Test
public void testAboveOnStackViaMisnestedBoldItalic() {
    String html = "<b><i>text</b>after</i>";
    Document doc = Jsoup.parse(html);
    Element body = doc.body();
    assertEquals(2, body.children().size());
    Element b = body.child(0);
    assertEquals("b", b.tagName());
    Element iInside = b.child(0);
    assertEquals("i", iInside.tagName());
    assertEquals("text", iInside.text());
    Element iAfter = body.child(1);
    assertEquals("i", iAfter.tagName());
    assertEquals("after", iAfter.text());
}

@Test
public void testParagraphImplicitCloseWithFormatting() {
    String html = "<p><em>italic <b>bold</em> normal</b> after.</p>";
    Document doc = Jsoup.parse(html);
    Element body = doc.body();
    Element p = body.child(0);
    assertEquals("p", p.tagName());
    Element em = p.child(0);
    assertEquals("em", em.tagName());
    assertTrue(em.text().contains("italic"));
    Element b = em.child(0);
    assertEquals("b", b.tagName());
    assertEquals("bold", b.text());
    assertTrue(p.children().size() >= 2);
}