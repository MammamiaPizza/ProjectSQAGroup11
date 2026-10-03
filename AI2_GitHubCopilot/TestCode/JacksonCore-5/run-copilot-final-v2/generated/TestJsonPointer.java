package com.fasterxml.jackson.core;

 import org.junit.Test;
 import static org.junit.Assert.*;

 public class TestJsonPointer {

     @Test
     public void testEmptyAndNullPointer() {
         JsonPointer empty = JsonPointer.compile("");
         assertTrue(empty.matches());
         assertEquals("", empty.getMatchingProperty());
         assertEquals(-1, empty.getMatchingIndex());
         assertTrue(empty.mayMatchProperty());
         assertFalse(empty.mayMatchElement());

         JsonPointer nullCompiled = JsonPointer.compile(null);
         assertSame(JsonPointer.compile(""), nullCompiled); // EMPTY singleton
     }

     @Test(expected = IllegalArgumentException.class)
     public void testMissingLeadingSlash() {
         JsonPointer.compile("noSlash");
     }

     @Test
     public void testArrayIndexZero() {
         JsonPointer ptr = JsonPointer.compile("/0");
         assertEquals("0", ptr.getMatchingProperty());
         assertEquals(0, ptr.getMatchingIndex());
         assertTrue(ptr.mayMatchElement());
         assertTrue(ptr.mayMatchProperty());
         assertFalse(ptr.matches());
         assertNotNull(ptr.matchProperty("0"));
         assertNull(ptr.matchProperty("1"));
         assertNotNull(ptr.matchElement(0));
         assertNull(ptr.matchElement(1));
         assertTrue(ptr.tail().matches());
     }

     @Test
     public void testArrayIndex42() {
         JsonPointer ptr = JsonPointer.compile("/42");
         assertEquals("42", ptr.getMatchingProperty());
         assertEquals(42, ptr.getMatchingIndex());
         assertTrue(ptr.mayMatchElement());
     }

     @Test
     public void testMaxIntIndex() {
         JsonPointer ptr = JsonPointer.compile("/2147483647");
         assertEquals(2147483647, ptr.getMatchingIndex());
         assertEquals("2147483647", ptr.getMatchingProperty());
     }

     @Test
     public void testLeadingZeroIndex() {
         JsonPointer ptr = JsonPointer.compile("/01");
         assertEquals("01", ptr.getMatchingProperty());
         assertEquals(1, ptr.getMatchingIndex()); // parsed as integer 1
     }

     @Test
     public void testPropertyName() {
         JsonPointer ptr = JsonPointer.compile("/foo");
         assertEquals("foo", ptr.getMatchingProperty());
         assertEquals(-1, ptr.getMatchingIndex());
         assertFalse(ptr.mayMatchElement());
         assertNull(ptr.matchElement(0));
         assertNotNull(ptr.matchProperty("foo"));
         assertNull(ptr.matchProperty("bar"));
     }

     @Test
     public void testWonkyNumbersAsProperties() {
         // Every segment that contains non-digit characters or is too long must be a property,
         // must NOT throw NumberFormatException.
         String[] segments = {"1e0", "+1", "1.5", "0x1A", "123abc", "-1", "999999999999",
"3000000000"};
         for (String seg : segments) {
             String input = "/" + seg;
             JsonPointer ptr = JsonPointer.compile(input);
             assertEquals("matchingProperty for " + input, seg, ptr.getMatchingProperty());
             assertEquals("matchingIndex for " + input, -1, ptr.getMatchingIndex());
             assertFalse("mayMatchElement for " + input, ptr.mayMatchElement());
             assertTrue("mayMatchProperty for " + input, ptr.mayMatchProperty());
             assertNotNull("matchProperty for " + input, ptr.matchProperty(seg));
         }
     }

     @Test
     public void testQuotedSegments() {
         // RFC 6901 escaping: ~1 -> /, ~0 -> ~
         JsonPointer ptr1 = JsonPointer.compile("/a~1b");
         assertEquals("a/b", ptr1.getMatchingProperty());
         assertEquals(-1, ptr1.getMatchingIndex());

         JsonPointer ptr2 = JsonPointer.compile("/~0a");
         assertEquals("~a", ptr2.getMatchingProperty());
         assertEquals(-1, ptr2.getMatchingIndex());

         JsonPointer ptr3 = JsonPointer.compile("/~1e0");
         assertEquals("/e0", ptr3.getMatchingProperty()); // ~1 decoded to /
         assertEquals(-1, ptr3.getMatchingIndex());
     }

     @Test
     public void testMultiSegmentPointer() {
         JsonPointer ptr = JsonPointer.compile("/a/1/foo");
         assertEquals("a", ptr.getMatchingProperty());
         assertEquals(-1, ptr.getMatchingIndex());
         assertNotNull(ptr.matchProperty("a"));

         JsonPointer tail1 = ptr.tail();
         assertEquals("1", tail1.getMatchingProperty());
         assertEquals(1, tail1.getMatchingIndex());
         assertNotNull(tail1.matchElement(1));
         assertNotNull(tail1.matchProperty("1"));

         JsonPointer tail2 = tail1.tail();
         assertEquals("foo", tail2.getMatchingProperty());
         assertEquals(-1, tail2.getMatchingIndex());

         JsonPointer tail3 = tail2.tail();
         assertTrue(tail3.matches());
         assertEquals("", tail3.getMatchingProperty());
         assertEquals(-1, tail3.getMatchingIndex());
     }

     @Test
     public void testMatchElementWithNonIndex() {
         JsonPointer ptr = JsonPointer.compile("/foo");
         assertNull(ptr.matchElement(0));
         assertNull(ptr.matchElement(-1));
         assertNull(ptr.matchElement(100));
     }

     @Test
     public void testToStringEqualsHashCode() {
         JsonPointer p1 = JsonPointer.compile("/foo/0");
         JsonPointer p2 = JsonPointer.compile("/foo/0");
         assertEquals(p1.toString(), p2.toString());
         assertEquals(p1.hashCode(), p2.hashCode());
         assertEquals(p1, p2);
         assertFalse(p1.equals(null));
         assertFalse(p1.equals("string"));
     }
 }
