@Test
public void preservedCaseNestedLinksWithAttributesCloseOuterAndMatchInnerEndTag() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(
        "<A href='outer'>One <a href='inner'>Two</A> Three</a>",
        "",
        org.jsoup.parser.Parser.htmlParser().settings(org.jsoup.parser.ParseSettings.preserveCase));

    assertEquals("<A href=\"outer\">One </A><a href=\"inner\">Two</a> Three", document.body().html());
    assertEquals(2, document.body().children().size());
    assertEquals("A", document.body().child(0).tagName());
    assertEquals("a", document.body().child(1).tagName());
}

@Test
public void tagValueOfPreservesCaseOnlyWhenConfigured() {
    assertEquals("A", org.jsoup.parser.Tag.valueOf("A", org.jsoup.parser.ParseSettings.preserveCase).getName());
    assertEquals("a", org.jsoup.parser.Tag.valueOf("A").getName());
}

@Test
public void elementHtmlSetterReplacesExistingStandaloneContents() {
    org.jsoup.nodes.Element element = new org.jsoup.nodes.Element("div");

    element.html("<span>one</span>");
    assertEquals("<span>one</span>", element.html());

    element.html("<b>two</b>");
    assertEquals(1, element.childNodeSize());
    assertEquals("<b>two</b>", element.html());
}