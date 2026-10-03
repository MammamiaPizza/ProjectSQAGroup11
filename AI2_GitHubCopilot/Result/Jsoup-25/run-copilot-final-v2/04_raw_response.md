@Test
public void testEquals() {
    Tag p1 = Tag.valueOf("p");
    Tag p2 = Tag.valueOf("p");
    assertTrue(p1.equals(p2));
    assertTrue(p1.equals(p1));
    Tag div = Tag.valueOf("div");
    assertFalse(p1.equals(div));
    Tag foo1 = Tag.valueOf("foo");
    Tag foo2 = Tag.valueOf("foo");
    assertTrue(foo1.equals(foo2));
    Tag bar = Tag.valueOf("bar");
    assertFalse(foo1.equals(bar));
    assertFalse(p1.equals(foo1));
    assertFalse(p1.equals(null));
    assertFalse(p1.equals("string"));
}

@Test
public void testHashCode() {
    Tag p1 = Tag.valueOf("p");
    Tag p2 = Tag.valueOf("p");
    assertEquals(p1.hashCode(), p2.hashCode());
    Tag foo1 = Tag.valueOf("foo");
    Tag foo2 = Tag.valueOf("foo");
    assertEquals(foo1.hashCode(), foo2.hashCode());
}

@Test
public void testIsData() {
    assertTrue(Tag.valueOf("script").isData());
    assertTrue(Tag.valueOf("style").isData());
    assertTrue(Tag.valueOf("textarea").isData());
    assertFalse(Tag.valueOf("p").isData());
    assertFalse(Tag.valueOf("img").isData());
}

@Test
public void testCanContainBlock() {
    assertTrue(Tag.valueOf("div").canContainBlock());
    assertFalse(Tag.valueOf("p").canContainBlock());
    assertFalse(Tag.valueOf("span").canContainBlock());
    assertTrue(Tag.valueOf("foo").canContainBlock());
}