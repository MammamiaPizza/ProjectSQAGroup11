@Test
public void parentsReturnsAncestorsFromNearestToFarthest() {
    Element grandparent = new Element(org.jsoup.parser.Tag.valueOf("section"), "");
    Element parent = new Element(org.jsoup.parser.Tag.valueOf("article"), "");
    Element child = new Element(org.jsoup.parser.Tag.valueOf("p"), "");

    grandparent.appendChild(parent);
    parent.appendChild(child);

    assertEquals(2, child.parents().size());
    assertSame(parent, child.parents().get(0));
    assertSame(grandparent, child.parents().get(1));
}

@Test
public void addClassRetainsDistinctClassNames() {
    Element element = new Element(org.jsoup.parser.Tag.valueOf("div"), "");

    element.addClass("first").addClass("second").addClass("first");

    assertTrue(element.hasClass("first"));
    assertTrue(element.hasClass("second"));
    assertEquals(2, element.classNames().size());
}

@Test
public void appendAndAfterInsertParsedAndNodeContentInTreeOrder() {
    Element parent = new Element(org.jsoup.parser.Tag.valueOf("div"), "");
    Element first = new Element(org.jsoup.parser.Tag.valueOf("p"), "");
    Element inserted = new Element(org.jsoup.parser.Tag.valueOf("strong"), "");

    parent.appendChild(first);
    first.after("<span>middle</span>");
    first.after(inserted);

    assertEquals(3, parent.children().size());
    assertSame(first, parent.child(0));
    assertSame(inserted, parent.child(1));
    assertEquals("span", parent.child(2).tagName());
    assertEquals("middle", parent.child(2).text());
}

@Test
public void textNormalisesWhitespaceExceptInPreformattedElements() {
    Element normal = new Element(org.jsoup.parser.Tag.valueOf("div"), "");
    Element preformatted = new Element(org.jsoup.parser.Tag.valueOf("pre"), "");

    normal.appendText("one   two");
    preformatted.appendText("one   two");

    assertEquals("one two", normal.text());
    assertEquals("one   two", preformatted.text());
}