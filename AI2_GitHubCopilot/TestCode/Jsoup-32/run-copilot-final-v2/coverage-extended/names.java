import org.jsoup.nodes.Element;
 import org.jsoup.parser.Tag;
 import org.junit.Test;
 import static org.junit.Assert.*;
 import java.util.Set;

 class names {
     @Test
     public void testClonesClassnames() {
         Element original = new Element(Tag.valueOf("div"), "");
         original.addClass("foo");
         original.addClass("bar");

         Element clone = original.clone();

         assertTrue(clone.hasClass("foo"));
         assertTrue(clone.hasClass("bar"));
         assertEquals(original.classNames(), clone.classNames());

         clone.addClass("baz");
         clone.removeClass("foo");

         assertTrue(clone.hasClass("baz"));
         assertTrue(clone.hasClass("bar"));
         assertFalse(clone.hasClass("foo"));

         assertTrue(original.hasClass("foo"));
         assertTrue(original.hasClass("bar"));
         assertFalse(original.hasClass("baz"));

         Set<String> origSet = original.classNames();
         Set<String> cloneSet = clone.classNames();
         assertFalse(origSet.equals(cloneSet));

         original.removeClass("bar");
         original.addClass("qux");

         assertFalse(original.hasClass("bar"));
         assertTrue(original.hasClass("qux"));

         assertTrue(clone.hasClass("bar"));
         assertTrue(clone.hasClass("baz"));
         assertFalse(clone.hasClass("qux"));
     }

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
}
