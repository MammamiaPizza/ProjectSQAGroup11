@Test
public void testParents() {
    Element child = new Element(org.jsoup.parser.Tag.valueOf("p"), "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
    Element parent = new Element(org.jsoup.parser.Tag.valueOf("div"), "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
    parent.appendChild(child);
    org.jsoup.nodes.Elements parents = child.parents();
    assertEquals(1, parents.size());
    assertEquals(parent, parents.get(0));
}

@Test
public void testAddClass() {
    Element el = new Element(org.jsoup.parser.Tag.valueOf("div"), "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
    el.addClass("foo");
    assertTrue(el.hasClass("foo"));
    el.addClass("bar");
    assertTrue(el.hasClass("bar") && el.hasClass("foo"));
}

@Test
public void testAppendElement() {
    Element parent = new Element(org.jsoup.parser.Tag.valueOf("div"), "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
    Element child = parent.appendElement("span");
    assertEquals("span", child.tagName());
    assertEquals("span", parent.child(0).tagName());
}

@Test
public void testAppendHtml() {
    Element el = new Element(org.jsoup.parser.Tag.valueOf("div"), "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
    el.append("<p>Hello</p>");
    assertEquals(1, el.children().size());
    assertEquals("p", el.child(0).tagName());
}