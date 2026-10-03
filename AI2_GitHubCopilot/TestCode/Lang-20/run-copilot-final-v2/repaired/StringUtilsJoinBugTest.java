package org.apache.commons.lang3;

 import static org.junit.Assert.assertArrayEquals;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;

 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Iterator;
 import java.util.List;

 import org.junit.Test;

 public class StringUtilsJoinBugTest {

     // ---- join(Object[], char) overloads ----

     @Test
     public void testJoin_ObjectArrayChar_nullArray() {
         assertNull(StringUtils.join((Object[]) null, ','));
     }

     @Test
     public void testJoin_ObjectArrayChar_emptyArray() {
         assertEquals("", StringUtils.join(new Object[0], ','));
     }

     @Test
     public void testJoin_ObjectArrayChar_arrayWithNullElement() {
         assertEquals("a,,c", StringUtils.join(new Object[] { "a", null, "c" }, ','));
     }

     @Test
     public void testJoin_ObjectArrayChar_singleElement() {
         assertEquals("x", StringUtils.join(new Object[] { "x" }, '-'));
     }

     // ---- join(Object[], String) overloads ----

     @Test
     public void testJoin_ObjectArrayString_nullArray() {
         assertNull(StringUtils.join((Object[]) null, "-"));
     }

     @Test
     public void testJoin_ObjectArrayString_emptyArray() {
         assertEquals("", StringUtils.join(new Object[0], "-"));
     }

     @Test
     public void testJoin_ObjectArrayString_arrayWithNullElement() {
         assertEquals("hello::::world", StringUtils.join(new Object[] { "hello", null, "world" },
"::"));
     }

     @Test
     public void testJoin_ObjectArrayString_nullSeparator() {
         assertEquals("abc", StringUtils.join(new Object[] { "a", "b", "c" }, (String) null));
     }

     // ---- join(Iterator<?>, char) ----

     @Test
     public void testJoin_IteratorChar_nullIterator() {
         assertNull(StringUtils.join((Iterator<?>) null, ','));
     }

     @Test
     public void testJoin_IteratorChar_emptyIterator() {
         assertEquals("", StringUtils.join(new ArrayList<>().iterator(), ','));
     }

     // ---- join(Iterator<?>, String) ----

     @Test
     public void testJoin_IteratorString_emptyIterator() {
         assertEquals("", StringUtils.join(new ArrayList<>().iterator(), "/"));
     }

     // ---- join(Iterable<?>, char/ String) safety net ----

     @Test
     public void testJoin_IterableChar_nullIterable() {
         assertNull(StringUtils.join((Iterable<?>) null, ','));
     }
 }
