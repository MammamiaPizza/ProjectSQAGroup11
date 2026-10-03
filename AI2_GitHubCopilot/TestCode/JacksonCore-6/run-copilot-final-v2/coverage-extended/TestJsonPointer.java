package com.fasterxml.jackson.core;

 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for {@link JsonPointer}, targeting the bug where leading-zero
  * numeric segments (e.g. "00") are incorrectly parsed as index 0
  * instead of being rejected (index -1).
  */
 public class TestJsonPointer {

     // Bug-triggering test: leading-zero "00" must produce index -1
     @Test
     public void testIZeroIndex() {
         JsonPointer ptr = JsonPointer.compile("/00");
         assertEquals("leading-zero segment must not parse as numeric index",
                 -1, ptr.getMatchingIndex());
         assertFalse("leading-zero segment should not match element",
                 ptr.mayMatchElement());
         assertTrue("segment still matches as property name",
                 ptr.mayMatchProperty());
         assertEquals("property name preserved",
                 "00", ptr.getMatchingProperty());
     }

     // Additional leading-zero variants
     @Test
     public void testLeadingZeroVariants() {
         JsonPointer ptr01 = JsonPointer.compile("/01");
         assertEquals(-1, ptr01.getMatchingIndex());
         assertFalse(ptr01.mayMatchElement());
         assertEquals("01", ptr01.getMatchingProperty());

         JsonPointer ptr007 = JsonPointer.compile("/007");
         assertEquals(-1, ptr007.getMatchingIndex());
         assertFalse(ptr007.mayMatchElement());
         assertEquals("007", ptr007.getMatchingProperty());
     }

     // Single zero is a valid numeric index (boundary case)
     @Test
     public void testSingleZeroIndex() {
         JsonPointer ptr = JsonPointer.compile("/0");
         assertEquals(0, ptr.getMatchingIndex());
         assertTrue(ptr.mayMatchElement());
         assertTrue(ptr.mayMatchProperty());
         assertEquals("0", ptr.getMatchingProperty());
     }

     // Valid numeric indices
     @Test
     public void testValidNumericIndices() {
         JsonPointer ptr12 = JsonPointer.compile("/12");
         assertEquals(12, ptr12.getMatchingIndex());
         assertTrue(ptr12.mayMatchElement());

         JsonPointer ptr42 = JsonPointer.compile("/42");
         assertEquals(42, ptr42.getMatchingIndex());
         assertTrue(ptr42.mayMatchElement());

         JsonPointer ptrMax = JsonPointer.compile("/2147483647");
         assertEquals(2147483647, ptrMax.getMatchingIndex());
         assertTrue(ptrMax.mayMatchElement());
     }

     // Empty and purely non-numeric segments
     @Test
     public void testNonNumericSegments() {
         JsonPointer ptrSlash = JsonPointer.compile("/");
         assertEquals(-1, ptrSlash.getMatchingIndex());
         assertFalse(ptrSlash.mayMatchElement());
         assertEquals("", ptrSlash.getMatchingProperty());

         JsonPointer ptrAbc = JsonPointer.compile("/abc");
         assertEquals(-1, ptrAbc.getMatchingIndex());
         assertFalse(ptrAbc.mayMatchElement());
         assertEquals("abc", ptrAbc.getMatchingProperty());
     }

     // Negative sign and mixed alphanumeric segments are not valid indices
     @Test
     public void testInvalidNumericSegments() {
         JsonPointer ptrNeg = JsonPointer.compile("/-1");
         assertEquals(-1, ptrNeg.getMatchingIndex());
         assertFalse(ptrNeg.mayMatchElement());

         JsonPointer ptrMix = JsonPointer.compile("/1a");
         assertEquals(-1, ptrMix.getMatchingIndex());
         assertFalse(ptrMix.mayMatchElement());
     }

     // Integer overflow (> Integer.MAX_VALUE) leads to -1
     @Test
     public void testIntegerOverflow() {
         JsonPointer ptr = JsonPointer.compile("/3000000000");
         assertEquals(-1, ptr.getMatchingIndex());
         assertFalse(ptr.mayMatchElement());
     }

     // matchElement and matchProperty navigation
     @Test
     public void testMatchMethods() {
         JsonPointer ptr = JsonPointer.compile("/0/name");

         assertTrue(ptr.mayMatchElement());
         assertEquals(0, ptr.getMatchingIndex());

         JsonPointer tail = ptr.matchElement(0);
         assertNotNull("should match element 0", tail);
         assertEquals("name", tail.getMatchingProperty());
         assertEquals(-1, tail.getMatchingIndex());

         assertNull("should not match element 1", ptr.matchElement(1));
         assertNull("should not match element -1", ptr.matchElement(-1));

         JsonPointer propPtr = JsonPointer.compile("/name/child");
         JsonPointer propTail = propPtr.matchProperty("name");
         assertNotNull(propTail);
         assertEquals("child", propTail.getMatchingProperty());
         assertNull(propPtr.matchProperty("wrong"));
     }

     // tail() and matches() semantics
     @Test
     public void testTailAndMatches() {
         JsonPointer ptr = JsonPointer.compile("/a/b/c");
         assertFalse(ptr.matches());
         assertNotNull(ptr.tail());

         JsonPointer t1 = ptr.tail();
         assertEquals("b", t1.getMatchingProperty());
         assertFalse(t1.matches());

         JsonPointer t2 = t1.tail();
         assertEquals("c", t2.getMatchingProperty());
         assertFalse(t2.matches());

         JsonPointer t3 = t2.tail();
         assertTrue("final segment terminal", t3.matches());
         assertNull("no tail beyond terminal", t3.tail());
     }

     // EMPTY pointer and null/empty-string input
     @Test
     public void testEmptyPointer() {
         JsonPointer empty = JsonPointer.compile("");
         assertTrue(empty.matches());
         assertEquals("", empty.getMatchingProperty());
         assertEquals(-1, empty.getMatchingIndex());
         assertFalse(empty.mayMatchElement());
         assertTrue(empty.mayMatchProperty());
         assertNull(empty.tail());

         JsonPointer nullInput = JsonPointer.compile(null);
         assertSame(empty, nullInput);
     }

     // Invalid input: missing leading slash
     @Test(expected = IllegalArgumentException.class)
     public void testCompileInvalidNoSlash() {
         JsonPointer.compile("noSlash");
     }

     // Leading-zero segment in multi-segment pointer
     @Test
     public void testLeadingZeroInMultiSegment() {
         JsonPointer ptr = JsonPointer.compile("/arr/00/field");
         assertEquals("arr", ptr.getMatchingProperty());
         assertEquals(-1, ptr.getMatchingIndex());

         JsonPointer t1 = ptr.tail();
         assertEquals("00", t1.getMatchingProperty());
         assertEquals(-1, t1.getMatchingIndex());
         assertFalse(t1.mayMatchElement());

         JsonPointer t2 = t1.tail();
         assertEquals("field", t2.getMatchingProperty());
     }

@Test
    public void testTildeEscapeUnknownChar() {
        JsonPointer ptr = JsonPointer.compile("/ab~2cd");
        assertEquals("ab~2cd", ptr.getMatchingProperty());
        assertEquals(-1, ptr.getMatchingIndex());
        assertTrue(ptr.mayMatchProperty());
        assertFalse(ptr.mayMatchElement());
    }

 @Test
 public void testPointerEndingWithTilde() {
     JsonPointer ptr = JsonPointer.compile("/abc~");
     assertEquals("abc~", ptr.getMatchingProperty());
     assertEquals(-1, ptr.getMatchingIndex());
     assertTrue(ptr.mayMatchProperty());
     assertFalse(ptr.mayMatchElement());
 }

 @Test
 public void testMatchPropertyNonMatching() {
     JsonPointer ptr = JsonPointer.compile("/foo");
     assertNull(ptr.matchProperty("bar"));
     assertNotNull(ptr.matchProperty("foo"));
 }

 @Test
 public void testMatchElementNonMatching() {
     JsonPointer ptr = JsonPointer.compile("/1");
     assertEquals(1, ptr.getMatchingIndex());
     assertTrue(ptr.mayMatchElement());
     assertNull(ptr.matchElement(2));
     assertNotNull(ptr.matchElement(1));
 }
}
