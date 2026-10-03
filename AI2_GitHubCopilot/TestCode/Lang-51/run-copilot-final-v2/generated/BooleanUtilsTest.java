package org.apache.commons.lang;

import static org.junit.Assert.*;
import org.junit.Test;

public class BooleanUtilsTest {

 @Test
 public void testToBoolean_NullAndEmpty() {
     assertFalse(BooleanUtils.toBoolean(null));
     assertFalse(BooleanUtils.toBoolean(""));
 }

 @Test
 public void testToBoolean_TrueCaseInsensitive() {
     assertTrue(BooleanUtils.toBoolean("true"));
     assertTrue(BooleanUtils.toBoolean("TRUE"));
     assertTrue(BooleanUtils.toBoolean("True"));
     assertTrue(BooleanUtils.toBoolean("tRuE"));
     assertTrue(BooleanUtils.toBoolean("tRUE"));
 }

 @Test
 public void testToBoolean_FalseCaseInsensitive() {
     assertFalse(BooleanUtils.toBoolean("false"));
     assertFalse(BooleanUtils.toBoolean("FALSE"));
     assertFalse(BooleanUtils.toBoolean("False"));
     assertFalse(BooleanUtils.toBoolean("fALsE"));
 }

 @Test
 public void testToBoolean_OnOff() {
     assertTrue(BooleanUtils.toBoolean("on"));
     assertTrue(BooleanUtils.toBoolean("ON"));
     assertTrue(BooleanUtils.toBoolean("On"));
     assertTrue(BooleanUtils.toBoolean("oN"));
     assertFalse(BooleanUtils.toBoolean("off"));
     assertFalse(BooleanUtils.toBoolean("OFF"));
     assertFalse(BooleanUtils.toBoolean("Off"));
     assertFalse(BooleanUtils.toBoolean("oFf"));
 }

 @Test
 public void testToBoolean_YesNo() {
     assertTrue(BooleanUtils.toBoolean("yes"));
     assertTrue(BooleanUtils.toBoolean("YES"));
     assertTrue(BooleanUtils.toBoolean("Yes"));
     assertTrue(BooleanUtils.toBoolean("yEs"));
     assertTrue(BooleanUtils.toBoolean("yeS"));
     assertFalse(BooleanUtils.toBoolean("no"));
     assertFalse(BooleanUtils.toBoolean("NO"));
     assertFalse(BooleanUtils.toBoolean("No"));
     assertFalse(BooleanUtils.toBoolean("nO"));
 }

 @Test(expected = StringIndexOutOfBoundsException.class)
 public void testToBoolean_ThreeCharNonYesThrowsException() {
     BooleanUtils.toBoolean("abc");
 }

 @Test(expected = StringIndexOutOfBoundsException.class)
 public void testToBoolean_ThreeCharPrefixOfTrueThrowsException() {
     BooleanUtils.toBoolean("tru");
 }

 @Test
 public void testToBoolean_ThreeCharYesLikeReturnsFalse() {
     // starts with 'y' but not "yes" – handled in case 3 without fallthrough
     assertFalse(BooleanUtils.toBoolean("yee"));
     assertFalse(BooleanUtils.toBoolean("yap"));
     assertFalse(BooleanUtils.toBoolean("YEP"));
 }

 @Test
 public void testToBoolean_TwoCharNotOnReturnsFalse() {
     assertFalse(BooleanUtils.toBoolean("ab"));
     assertFalse(BooleanUtils.toBoolean("zz"));
 }

 @Test
 public void testToBoolean_VariousInvalidStringsNoException() {
     assertFalse(BooleanUtils.toBoolean("hello"));
     assertFalse(BooleanUtils.toBoolean("foo"));
     assertFalse(BooleanUtils.toBoolean("bar"));
     assertFalse(BooleanUtils.toBoolean("truf"));
     assertFalse(BooleanUtils.toBoolean("fals"));
     assertFalse(BooleanUtils.toBoolean("abcd"));
 }

 @Test
 public void testToBooleanObject_Various() {
     assertEquals(Boolean.TRUE, BooleanUtils.toBooleanObject("true"));
     assertEquals(Boolean.TRUE, BooleanUtils.toBooleanObject("TRUE"));
     assertEquals(Boolean.FALSE, BooleanUtils.toBooleanObject("false"));
     assertEquals(Boolean.TRUE, BooleanUtils.toBooleanObject("on"));
     assertEquals(Boolean.FALSE, BooleanUtils.toBooleanObject("OFF"));
     assertEquals(Boolean.TRUE, BooleanUtils.toBooleanObject("yes"));
     assertEquals(Boolean.FALSE, BooleanUtils.toBooleanObject("No"));
     assertNull(BooleanUtils.toBooleanObject("garbage"));
     assertNull(BooleanUtils.toBooleanObject(null));
     assertNull(BooleanUtils.toBooleanObject(""));
 }

 @Test
 public void testToBoolean_CustomStrings() {
     assertTrue(BooleanUtils.toBoolean("tr", "tr", "fa"));
     assertFalse(BooleanUtils.toBoolean("fa", "tr", "fa"));
     assertTrue(BooleanUtils.toBoolean(null, null, "fa"));
     assertFalse(BooleanUtils.toBoolean(null, "tr", null));
     try {
         BooleanUtils.toBoolean("zz", "tr", "fa");
         fail("Expected IllegalArgumentException");
     } catch (IllegalArgumentException e) {
         // expected
     }
 }

}
