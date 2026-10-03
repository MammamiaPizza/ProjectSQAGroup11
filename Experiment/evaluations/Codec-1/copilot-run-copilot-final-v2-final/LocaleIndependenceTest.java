package org.apache.commons.codec.language;

import static org.junit.Assert.*;

import java.util.Locale;

import org.apache.commons.codec.EncoderException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**

 - Tests for locale independence bugs (CODEC-65).
 - The fault is that {@link SoundexUtils#clean(String)} and the single‑character branch of
 - {@link Metaphone#metaphone(String)} use {@code toUpperCase()} without
 - {@code Locale.ENGLISH}, causing wrong results under the Turkish locale (e.g., 'i' → 'İ').
 - {@link Caverphone#caverphone(String)} is affected via {@code toLowerCase()} (no Locale).
  */
 public class LocaleIndependenceTest {
  private Locale originalLocale;
  @Before
  public void saveLocale() {
  originalLocale = Locale.getDefault();
  }
  @After
  public void restoreLocale() {
  Locale.setDefault(originalLocale);
  }
  // ----- Metaphone single-character branch bug -----
  @Test
  public void testMetaphoneSingleCharIWithTurkishLocale() {
  Locale.setDefault(new Locale("tr", "TR"));
  Metaphone m = new Metaphone();
  assertEquals("Metaphone of 'i' must be 'I' regardless of locale", "I", m.metaphone("i"));
  }
  @Test
  public void testMetaphoneSingleCharIWithEnglishLocale() {
  Locale.setDefault(Locale.ENGLISH);
  assertEquals("I", new Metaphone().metaphone("i"));
  }
  @Test
  public void testMetaphoneEncodeSingleCharIWithTurkishLocale() throws EncoderException {
  Locale.setDefault(new Locale("tr", "TR"));
  assertEquals("I", new Metaphone().encode("i"));
  }
  @Test
  public void testMetaphoneEncodeObjectWithTurkishLocale() throws EncoderException {
  Locale.setDefault(new Locale("tr", "TR"));
  Metaphone m = new Metaphone();
  assertEquals("I", m.encode((Object) "i"));
  }
  // ----- SoundexUtils.clean bug -----
  @Test
  public void testSoundexUtilsCleanLowercaseIWithTurkishLocale() {
  Locale.setDefault(new Locale("tr", "TR"));
  String result = SoundexUtils.clean("i");
  assertEquals("clean('i') must produce 'I' under any locale", "I", result);
  }
  @Test
  public void testSoundexUtilsCleanLowercaseIWithEnglishLocale() {
  Locale.setDefault(Locale.ENGLISH);
  assertEquals("I", SoundexUtils.clean("i"));
  }
  @Test
  public void testSoundexUtilsCleanMixedCharTurkishLocale() {
  Locale.setDefault(new Locale("tr", "TR"));
  // non‑letter characters trigger the Locale.ENGLISH path in clean,
  // but the all‑letter path (count==len) is still exercised with "i2k"
  String result = SoundexUtils.clean("i2k");
  assertEquals("clean of 'i2k' must produce 'IK'", "IK", result);
  }
  // ----- Caverphone locale dependent toLowerCase bug -----
  @Test
  public void testCaverphoneTurkishLocaleInvariant() {
  Caverphone c = new Caverphone();
  Locale.setDefault(Locale.ENGLISH);
  String expected = c.caverphone("TR");
  Locale.setDefault(new Locale("tr", "TR"));
  String actual = c.caverphone("TR");
  assertEquals("Caverphone output must be locale independent", expected, actual);
  }
  @Test
  public void testCaverphoneEncodeTurkishLocaleInvariant() throws EncoderException {
  Caverphone c = new Caverphone();
  Locale.setDefault(Locale.ENGLISH);
  String expected = c.encode("TR");
  Locale.setDefault(new Locale("tr", "TR"));
  assertEquals("encode output must be locale independent", expected, c.encode("TR"));
  }
  @Test
  public void testCaverphoneVowelStartTurkishLocaleInvariant() {
  Caverphone c = new Caverphone();
  Locale.setDefault(Locale.ENGLISH);
  String expected = c.caverphone("apple");
  Locale.setDefault(new Locale("tr", "TR"));
  assertEquals("Caverphone of 'apple' must be locale independent", expected, c.caverphone("apple"));
  }
  // ----- Equality helpers -----
  @Test
  public void testIsMetaphoneEqualLocaleIndependent() {
  Locale.setDefault(new Locale("tr", "TR"));
  Metaphone m = new Metaphone();
  assertTrue("isMetaphoneEqual should work under Turkish locale", m.isMetaphoneEqual("i", "I"));
  }
  @Test
  public void testIsCaverphoneEqualLocaleIndependent() {
  Caverphone c = new Caverphone();
  Locale.setDefault(Locale.ENGLISH);
  boolean expected = c.isCaverphoneEqual("TR", "TR");
  Locale.setDefault(new Locale("tr", "TR"));
  assertEquals("isCaverphoneEqual must be locale independent", expected, c.isCaverphoneEqual("TR",
"TR"));
  }

}
