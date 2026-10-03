package org.apache.commons.lang.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StrBuilderLang295Test {

 @Test
 public void containsCharWithinValidContentReturnsTrue() {
     StrBuilder sb = new StrBuilder("abc");
     assertTrue(sb.contains('a'));
     assertTrue(sb.contains('b'));
     assertTrue(sb.contains('c'));
 }

 @Test
 public void containsCharAbsentFromValidContentReturnsFalse() {
     StrBuilder sb = new StrBuilder("abc");
     assertFalse(sb.contains('x'));
 }

 @Test
 public void containsCharDoesNotLookBeyondSizeAfterSetLength() {
     StrBuilder sb = new StrBuilder("abc");
     sb.setLength(2);
     assertFalse(sb.contains('c'));
     assertTrue(sb.contains('a'));
     assertTrue(sb.contains('b'));
 }

 @Test
 public void containsCharAfterClearIgnoresBufferRemnants() {
     StrBuilder sb = new StrBuilder("abc");
     sb.clear();
     assertFalse(sb.contains('a'));
     assertFalse(sb.contains('b'));
     assertFalse(sb.contains('c'));
     assertFalse(sb.contains('\u0000'));
 }

 @Test
 public void containsNullCharOnEmptyStrBuilderReturnsFalse() {
     StrBuilder sb = new StrBuilder();
     assertFalse(sb.contains('\u0000'));
 }

 @Test
 public void indexOfAndLastIndexOfCharIgnoreBeyondSize() {
     StrBuilder sb = new StrBuilder("abc");
     sb.setLength(2);
     assertEquals(-1, sb.indexOf('c'));
     assertEquals(-1, sb.lastIndexOf('c'));
     assertEquals(1, sb.lastIndexOf('b'));
 }

 @Test
 public void indexOfCharFromIndexAtOrPastSizeReturnsMinusOne() {
     StrBuilder sb = new StrBuilder("abc");
     sb.setLength(2);
     assertEquals(-1, sb.indexOf('a', 2));
     assertEquals(-1, sb.indexOf('b', 2));
 }

 @Test(expected = StringIndexOutOfBoundsException.class)
 public void charAtBeyondValidSizeThrows() {
     new StrBuilder("abc").charAt(3);
 }

}
