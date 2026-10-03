package org.apache.commons.codec.language;

import static org.junit.Assert.*;
import org.junit.Test;
import org.apache.commons.codec.EncoderException;

/**

 - Tests for {@link Soundex} focusing on the H/W rule bug (CODEC-199) and standard Soundex behavior.
  */
 public class SoundexTest {
  private final Soundex soundex = new Soundex();
  @Test
  public void testUsEnglishMappingString() {
  assertEquals("01230120022455012623010202", Soundex.US_ENGLISH_MAPPING_STRING);
  }
  @Test
  public void testSoundexBasic() {
  assertEquals("W252", soundex.soundex("Washington"));
  assertEquals("A261", soundex.soundex("Ashcroft"));
  assertEquals("J250", soundex.soundex("Jackson"));
  assertEquals("R163", soundex.soundex("Robert"));
  }
  @Test
  public void testHWRuleSeparatingSameCode() {
  assertEquals("S200", soundex.soundex("Sachs"));
  assertEquals("A261", soundex.soundex("Ashcroft"));
  }
  @Test
  public void testHWRuleDifferentCodesAndBoundary() {
  assertEquals("H600", soundex.soundex("Harry"));
  assertEquals("M620", soundex.soundex("Marsh"));
  assertEquals("M250", soundex.soundex("Mawson"));
  }
  @Test
  public void testHWRuleBugIncorrectSkip() {
  // When preHWChar is 'H' or 'W' the buggy code incorrectly returns 0
  assertEquals("A200", soundex.soundex("AHWC"));
  assertEquals("U600", soundex.soundex("UHWR"));
  }
  @Test
  public void testEmptyString() {
  assertEquals("", soundex.soundex(""));
  }
  @Test
  public void testNullString() {
  assertNull(soundex.soundex(null));
  }
  @Test
  public void testSingleCharacter() {
  assertEquals("A000", soundex.soundex("A"));
  }
  @Test
  public void testVowelSeparatedSameCode() {
  assertEquals("B130", soundex.soundex("Babad"));
  }
  @Test
  public void testEncodeWithNonStringThrowsException() {
  try {
      soundex.encode(Integer.valueOf(1));
      fail("Expected EncoderException");
  } catch (EncoderException e) {
      assertEquals("Parameter supplied to Soundex encode is not of type java.lang.String",
e.getMessage());
  }
  }
  @Test
  public void testMaxLengthDeprecated() {
  soundex.setMaxLength(2);
  assertEquals(2, soundex.getMaxLength());
  assertEquals("W252", soundex.soundex("Washington"));
  }
  @Test
  public void testDifference() throws Exception {
  assertEquals(4, soundex.difference("Washington", "Washington"));
  assertTrue(soundex.difference("Washington", "Washnigton") >= 0);
  }

}
