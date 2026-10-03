@Test
public void parentsAreReturnedFromNearestToFarthest() {
    org.jsoup.nodes.Element outer = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("div"), "");
    org.jsoup.nodes.Element middle = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("section"), "");
    org.jsoup.nodes.Element child = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");

    outer.appendChild(middle);
    middle.appendChild(child);

    assertEquals(2, child.parents().size());
    assertEquals(middle, child.parents().get(0));
    assertEquals(outer, child.parents().get(1));
}

@Test
public void appendAndAfterInsertParsedAndNodeContentInSiblingOrder() {
    org.jsoup.nodes.Element container = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("div"), "");
    container.append("<p>One</p>");

    org.jsoup.nodes.Element paragraph = container.child(0);
    paragraph.after("<span>Two</span>");
    paragraph.after(new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("em"), "").appendText("Three"));

    assertEquals("<p>One</p><em>Three</em><span>Two</span>", container.html());
}

@Test
public void addClassAddsClassOnlyOnce() {
    org.jsoup.nodes.Element element = new org.jsoup.nodes.Element(org.jsoup.parser.Tag.valueOf("p"), "");

    element.addClass("one");
    element.addClass("one");

    assertTrue(element.hasClass("one"));
    assertEquals("one", element.attr("class"));
}

@Test
public void textNormalisesOrdinaryWhitespaceButPreservesPreWhitespaceAndSeparatesBreaks() {
    assertEquals("One Two", org.jsoup.Jsoup.parse("<p>One   Two</p>").select("p").first().text());
    assertEquals("One Two", org.jsoup.Jsoup.parse("<p>One<br>Two</p>").select("p").first().text());
    assertEquals("One   Two", org.jsoup.Jsoup.parse("<pre>One   Two</pre>").select("pre").first().text());
}