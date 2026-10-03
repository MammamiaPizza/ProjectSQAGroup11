package org.apache.commons.lang3;

 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;

 import org.junit.Test;

 public class StringUtilsContainsAnySupplementaryTest {

     @Test
     public void testContainsAnyCharArrayWithSupplementaryChars() {
         // supplementary char in target, non-matching supplementary in search char[]
         // Bug: char-by-char iteration matches individual surrogate, causing false true
         CharSequence cs = "\uD83D\uDE00";
         char[] search = { '\uD83D', '\uDE01' };
         assertFalse(StringUtils.containsAny(cs, search));
     }

     @Test
     public void testContainsAnyStringWithSupplementaryChars() {
         CharSequence cs = "\uD83D\uDE00";
         assertFalse(StringUtils.containsAny(cs, "\uD83D\uDE01"));
     }

     @Test
     public void testContainsAnyCharArrayMatchingSupplementary() {
         CharSequence cs = "abc\uD83D\uDE00xyz";
         char[] search = { '\uD83D', '\uDE00' };
         assertTrue(StringUtils.containsAny(cs, search));
     }

     @Test
     public void testContainsAnyStringMatchingSupplementary() {
         CharSequence cs = "abc\uD83D\uDE00xyz";
         assertTrue(StringUtils.containsAny(cs, "\uD83D\uDE00"));
     }

     @Test
     public void testContainsAnyStringNonMatchingSupplementary() {
         CharSequence cs = "\uD83D\uDE00";
         assertFalse(StringUtils.containsAny(cs, "\uD83D\uDE01"));
     }

     @Test
     public void testContainsAnyCharArrayNonMatchingSupplementary() {
         CharSequence cs = "\uD83D\uDE00";
         char[] search = { '\uD83D', '\uDE01' };
         assertFalse(StringUtils.containsAny(cs, search));
     }

     @Test
     public void testNullInput() {
         assertFalse(StringUtils.containsAny(null, "abc"));
         assertFalse(StringUtils.containsAny(null, new char[] {'a'}));
     }

     @Test
     public void testEmptySearch() {
         CharSequence cs = "abc";
         assertFalse(StringUtils.containsAny(cs, ""));
         assertFalse(StringUtils.containsAny(cs, new char[0]));
     }

     @Test
     public void testBmpRegression() {
         assertTrue(StringUtils.containsAny("abc", "b"));
         assertTrue(StringUtils.containsAny("abc", new char[] {'b'}));
         assertFalse(StringUtils.containsAny("abc", "def"));
     }

     @Test
     public void testLoneSurrogateInTargetOnly() {
         CharSequence cs = "\uD83D";
         assertFalse(StringUtils.containsAny(cs, "abc"));
         assertFalse(StringUtils.containsAny(cs, new char[] {'a'}));
         // lone surrogate is a regular char; matching that char is correct
         assertTrue(StringUtils.containsAny(cs, "\uD83D"));
         assertTrue(StringUtils.containsAny(cs, new char[] {'\uD83D'}));
     }

     @Test
     public void testSupplementaryInSearchOnly() {
         CharSequence cs = "hello";
         assertFalse(StringUtils.containsAny(cs, "\uD83D\uDE00"));
         char[] search = { '\uD83D', '\uDE00' };
         assertFalse(StringUtils.containsAny(cs, search));
     }

     @Test
     public void testMultipleSupplementaryCharsInTarget() {
         CharSequence cs = "\uD83D\uDE00\uD83D\uDE01";
         assertTrue(StringUtils.containsAny(cs, "\uD83D\uDE00"));
         // different supplementary char; bug might match surrogates, expect false
         assertFalse(StringUtils.containsAny(cs, "\uD83D\uDE02"));
     }
 }