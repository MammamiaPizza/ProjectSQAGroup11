package org.apache.commons.compress.archivers.tar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class TarUtilsTest {

 private static byte[] ascii(final String value) {
     final byte[] result = new byte[value.length()];
     for (int i = 0; i < value.length(); i++) {
         result[i] = (byte) value.charAt(i);
     }
     return result;
 }

 private static void assertIllegalArgument(final byte[] buffer, final int offset,
                                           final int length) {
     try {
         TarUtils.parseOctal(buffer, offset, length);
         fail("Expected IllegalArgumentException");
     } catch (final IllegalArgumentException expected) {
         // expected
     }
 }

 @Test
 public void testParseOctal() {
     final byte[] buffer = ascii("777777777777");
     assertEquals(68719476735L, TarUtils.parseOctal(buffer, 0, buffer.length));
 }

 @Test
 public void testParseSingleDigitWithNul() {
     final byte[] buffer = new byte[] {'7', 0};
     assertEquals(7L, TarUtils.parseOctal(buffer, 0, buffer.length));
 }

 @Test
 public void testParseAllZeroes() {
     final byte[] buffer = ascii("000000");
     assertEquals(0L, TarUtils.parseOctal(buffer, 0, buffer.length));
 }

 @Test
 public void testParseTrailingSpacesAndNuls() {
     assertEquals(7L, TarUtils.parseOctal(new byte[] {'7', ' ', ' '}, 0, 3));
     assertEquals(7L, TarUtils.parseOctal(new byte[] {'7', 0, 0}, 0, 3));
 }

 @Test
 public void testParseLeadingSpaces() {
     assertEquals(7L, TarUtils.parseOctal(new byte[] {' ', ' ', '7', ' '}, 0, 4));
 }

 @Test
 public void testParseBoundaryMaxOctalValue() {
     final byte[] buffer = ascii(Long.toOctalString(Long.MAX_VALUE));
     assertEquals(Long.MAX_VALUE, TarUtils.parseOctal(buffer, 0, buffer.length));
 }

 @Test
 public void testParseInvalidOctalCharacter() {
     assertIllegalArgument(ascii("89az"), 0, 4);
 }

 @Test
 public void testParseLengthValidation() {
     assertIllegalArgument(new byte[] {'7'}, 0, 1);
     assertIllegalArgument(new byte[0], 0, 0);
 }

 @Test
 public void testParseOffsetOutOfRange() {
     final byte[] buffer = new byte[] {'7', ' '};
     try {
         TarUtils.parseOctal(buffer, 2, 2);
         fail("Expected ArrayIndexOutOfBoundsException");
     } catch (final ArrayIndexOutOfBoundsException expected) {
         // expected
     }
 }

}