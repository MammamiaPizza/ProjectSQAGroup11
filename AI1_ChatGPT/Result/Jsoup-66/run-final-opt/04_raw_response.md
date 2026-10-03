@Test
public void parentsReturnsAncestorsFromClosestToFarthest() {
    Element grandparent = new Element("section");
    Element parent = new Element("article");
    Element child = new Element("p");

    grandparent.appendChild(parent);
    parent.appendChild(child);

    assertEquals(2, child.parents().size());
    assertSame(parent, child.parents().get(0));
    assertSame(grandparent, child.parents().get(1));
}

@Test
public void addClassAddsOnlyNewClassNames() {
    Element element = new Element("div").attr("class", "first");

    assertSame(element, element.addClass("second"));
    element.addClass("first");

    assertEquals("first second", element.attr("class"));
}

@Test
public void appendToAndAfterInsertElementsInExpectedOrder() {
    Element parent = new Element("div");
    Element first = new Element("p");

    first.append("<b>one</b>");
    assertSame(first, first.appendTo(parent));
    assertSame(first, first.after("<em>two</em>"));
    assertSame(first, first.after(new Element("span")));

    assertEquals(3, parent.children().size());
    assertSame(first, parent.child(0));
    assertEquals("span", parent.child(1).tagName());
    assertEquals("em", parent.child(2).tagName());
    assertEquals("<b>one</b>", first.html());
}

@Test
public void textNormalizesWhitespaceExceptInPreformattedElements() {
    Element normal = new Element("div").append("one \n two");
    Element preformatted = new Element("pre").append("one \n two");

    assertEquals("one two", normal.text());
    assertEquals("one \n two", preformatted.text());
}