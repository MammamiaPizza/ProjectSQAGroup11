@Test
    public void testAddClass() {
        Element el = new Element(org.jsoup.parser.Tag.valueOf("div"), "");
        el.addClass("newClass");
        assertTrue(el.hasClass("newClass"));
    }

 @Test
 public void testAfterHtml() {
     Element parent = new Element(org.jsoup.parser.Tag.valueOf("div"), "");
     parent.appendElement("span").after("<p>hello</p>");
     org.jsoup.select.Elements children = parent.children();
     assertEquals(2, children.size());
 }

 @Test
 public void testAppendChild() {
     Element parent = new Element(org.jsoup.parser.Tag.valueOf("div"), "");
     Element child = new Element(org.jsoup.parser.Tag.valueOf("span"), "");
     parent.appendChild(child);
     assertEquals(parent, child.parent());
     assertEquals(1, parent.children().size());
 }

 @Test
 public void testParents() {
     Element grandparent = new Element(org.jsoup.parser.Tag.valueOf("div"), "");
     Element parent = grandparent.appendElement("div");
     Element child = parent.appendElement("span");
     org.jsoup.select.Elements parents = child.parents();
     assertEquals(2, parents.size());
     assertEquals(parent, parents.get(0));
     assertEquals(grandparent, parents.get(1));
 }