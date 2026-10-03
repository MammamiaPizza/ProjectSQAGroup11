package org.apache.commons.compress.archivers.tar;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**

 - Tests for {@link TarUtils#parseOctal(byte[], int, int)}.
  */
 public class TarUtilsTest extends TestCase {
  public TarUtilsTest(final String name) {
  super(name);
  }
  public static Test suite() {
  return new TestSuite(TarUtilsTest.class);
  }
  public void testParseOctalInvalid() {
  try {
      TarUtils.parseOctal(new byte[0], 0, 0);
      fail("Expected IllegalArgumentException - should be at least 2 bytes long");
  } catch (IllegalArgumentException expected) {
      // expected
  }
  }
  public void testParseOctalLengthOneThrows() {
  try {
      TarUtils.parseOctal(new byte[] { '7' }, 0, 1);
      fail("Expected IllegalArgumentException - should be at least 2 bytes long");
  } catch (IllegalArgumentException expected) {
      // expected
  }
  }
  public void testParseOctalNullBufferLengthTooShortThrows() {
  try {
      TarUtils.parseOctal(null, 0, 0);
      fail("Expected IllegalArgumentException - should be at least 2 bytes long");
  } catch (IllegalArgumentException expected) {
      // expected
  }
  }
  public void testParseOctalAllNulReturnsZero() {
  assertEquals(0L, TarUtils.parseOctal(new byte[] { 0, 0, 0, 0 }, 0, 4));
  }
  public void testParseOctalMinimalLengthTrailingNul() {
  assertEquals(7L, TarUtils.parseOctal(new byte[] { '7', 0 }, 0, 2));
  }
  public void testParseOctalMinimalLengthTrailingSpace() {
  assertEquals(7L, TarUtils.parseOctal(new byte[] { '7', ' ' }, 0, 2));
  }
  public void testParseOctalValidWithTrailingSpace() {
  assertEquals(83L, TarUtils.parseOctal(new byte[] { '1', '2', '3', ' ' }, 0, 4));
  }
  public void testParseOctalLeadingSpacesAndZeroes() {
  assertEquals(83L, TarUtils.parseOctal(new byte[] { ' ', '0', '1', '2', '3', ' ' }, 0, 6));
  }
  public void testParseOctalInvalidDigitThrows() {
  assertInvalidByte(new byte[] { '1', '8' }, 0, 2);
  }
  public void testParseOctalInvalidLetterThrows() {
  assertInvalidByte(new byte[] { '1', 'a' }, 0, 2);
  }
  public void testParseOctalInvalidAfterLeadingSpacesThrows() {
  assertInvalidByte(new byte[] { ' ', '8', ' ' }, 0, 3);
  }
  public void testParseOctalOffsetPlusLengthExceedsBufferThrows() {
  try {
      TarUtils.parseOctal(new byte[] { '7', ' ' }, 1, 2);
      fail("Expected IllegalArgumentException for offset + length > buffer.length");
  } catch (IllegalArgumentException expected) {
      // expected
  }
  }
  private void assertInvalidByte(final byte[] buffer, final int offset, final int length) {
  try {
      TarUtils.parseOctal(buffer, offset, length);
      fail("Expected IllegalArgumentException for invalid octal byte");
  } catch (IllegalArgumentException expected) {
      // expected
  }
  }

}
