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
 }