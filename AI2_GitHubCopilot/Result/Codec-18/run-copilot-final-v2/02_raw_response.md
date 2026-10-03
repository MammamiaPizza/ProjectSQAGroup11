package org.apache.commons.codec.binary;

import static org.junit.Assert.*;
import org.junit.Test;

public class StringUtilsTest {

 @Test
 public void testEqualsIdenticalStrings() {
     assertTrue(StringUtils.equals("abc", "abc"));
 }

 @Test
 public void testEqualsSameLengthDifferentContent() {
     assertFalse(StringUtils.equals("abc", "abd"));
 }

 @Test
 public void testEqualsBothEmpty() {
     assertTrue(StringUtils.equals("", ""));
 }

 @Test
 public void testEqualsEmptyVsNonEmpty() {
     assertFalse(StringUtils.equals("", "a"));
     assertFalse(StringUtils.equals("a", ""));
 }

 @Test
 public void testEqualsSingleCharSame() {
     assertTrue(StringUtils.equals("a", "a"));
 }

 @Test
 public void testEqualsSingleCharDifferent() {
     assertFalse(StringUtils.equals("a", "b"));
 }

 @Test
 public void testEqualsCs1LongerThanCs2() {
     assertFalse(StringUtils.equals("abcd", "abc"));
 }

 @Test
 public void testEqualsCs2LongerThanCs1() {
     assertFalse(StringUtils.equals("abc", "abcd"));
 }

 @Test
 public void testEqualsMixedTypesSameContent() {
     StringBuilder sb = new StringBuilder("hello");
     assertTrue(StringUtils.equals("hello", sb));
     assertTrue(StringUtils.equals(sb, "hello"));
     assertTrue(StringUtils.equals(sb, sb));
 }

 @Test
 public void testEqualsMixedTypesDifferentLength() {
     StringBuilder sb = new StringBuilder("ab");
     // cs1 String longer, cs2 StringBuilder shorter
     assertFalse(StringUtils.equals("abc", sb));
     // cs1 StringBuilder shorter, cs2 String longer
     assertFalse(StringUtils.equals(sb, "abc"));
 }

 @Test
 public void testEqualsNullHandling() {
     assertTrue(StringUtils.equals(null, null));
     assertFalse(StringUtils.equals(null, "x"));
     assertFalse(StringUtils.equals("x", null));
 }

 @Test
 public void testEqualsNoIndexOutOfBoundsOnLengthMismatch() {
     // Verify that comparing strings of different lengths does not throw
     try {
         StringUtils.equals("short", "longer");
     } catch (StringIndexOutOfBoundsException e) {
         fail("Should not throw StringIndexOutOfBoundsException");
     }
     try {
         StringUtils.equals("longer", "short");
     } catch (StringIndexOutOfBoundsException e) {
         fail("Should not throw StringIndexOutOfBoundsException");
     }
     // With mixed types
     try {
         StringUtils.equals(new StringBuilder("ab"), "abcdef");
     } catch (StringIndexOutOfBoundsException e) {
         fail("Should not throw StringIndexOutOfBoundsException");
     }
 }

}