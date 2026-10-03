@Test
public void testAccumulateParents() {
    Element grandparent = new Element(Tag.valueOf("div"), "");
    Element parent = new Element(Tag.valueOf("p"), "");
    Element child = new Element(Tag.valueOf("span"), "");
    grandparent.appendChild(parent);
    parent.appendChild(child);
    Elements parents = child.parents();
    assertEquals(2, parents.size());
    assertSame(parent, parents.get(0));
    assertSame(grandparent, parents.get(1));
}

@Test
public void testAddClass() {
    Element el = new Element(Tag.valueOf("span"), "");
    el.addClass("highlight");
    assertTrue(el.atr("class").contains("highlight"));
}

@Test
public void testAppendElement() {
    Element parent = new Element(Tag.valueOf("div"), "");
    Element child = parent.appendElement("span");
    assertSame(parent, child.parent());
    assertEquals("span", child.tagName());
    assertEquals(1, parent.children().size());
    assertSame(child, parent.child(0));
}

@Test
public void testElementSiblingIndexWithIdenticalSiblings() {
    Element parent = new Element(Tag.valueOf("div"), "");
    Element a = new Element(Tag.valueOf("p"), "");
    a.appendText("same");
    Element b = new Element(Tag.valueOf("p"), "");
    b.appendText("same");
    Element c = new Element(Tag.valueOf("p"), "");
    c.appendText("same");
    parent.appendChild(a);
    parent.appendChild(b);
    parent.appendChild(c);
    assertEquals(Integer.valueOf(0), a.elementSiblingIndex());
    assertEquals(Integer.valueOf(1), b.elementSiblingIndex());
    assertEquals(Integer.valueOf(2), c.elementSiblingIndex());
}