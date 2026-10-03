package org.apache.commons.lang;

 import static org.junit.Assert.*;
 import org.junit.AfterClass;
 import org.junit.BeforeClass;
 import org.junit.Test;
 import java.util.Locale;

 public class StringUtilsContainsIgnoreCaseTest {

     private static Locale savedDefaultLocale;

     @BeforeClass
     public static void saveLocale() {
         savedDefaultLocale = Locale.getDefault();
     }

     @AfterClass
     public static void restoreLocale() {
         Locale.setDefault(savedDefaultLocale);
     }

     @Test
     public void testContainsIgnoreCase_Basic() {
         assertFalse("ull str should return false", StringUtils.containsIgnoreCase(null, "a"));
         assertFalse("ull search should return false", StringUtils.containsIgnoreCase("a", null));
         assertFalse("mtpy str with non-empty search", StringUtils.containsIgnoreCase("", "a"));
         assertFalse("on-empty str with empty search", StringUtils.containsIgnoreCase("a", ""));
         assertTrue("ual strings different case", StringUtils.containsIgnoreCase("abc", "ABC"));
         assertTrue("ubstring match", StringUtils.containsIgnoreCase("abc", "B"));
         assertTrue("elf match", StringUtils.containsIgnoreCase("xyz", "xyz"));
         assertFalse("o match", StringUtils.containsIgnoreCase("abc", "def"));
     }

     @Test
     public void testContainsIgnoreCase_pecialCharacters() {
         assertTrue("on-letter chars", StringUtils.containsIgnoreCase("a1B2c3", "1b2"));
         assertTrue("hitespace", StringUtils.containsIgnoreCase("a b c", " B"));
         assertTrue("igits", StringUtils.containsIgnoreCase("12345", "234"));
         assertFalse("ethod should not be fooled by partial match",
                 StringUtils.containsIgnoreCase("abc", "abd"));
     }

     @Test
     public void testContainsIgnoreCase_SharpS_SS() {
         assertTrue("? contains SS", StringUtils.containsIgnoreCase("?", "SS"));
         assertTrue("SS contains ?", StringUtils.containsIgnoreCase("SS", "?"));
         assertTrue("Stra?e" contains STRASSE", StringUtils.containsIgnoreCase("Stra?e",
"STRASSE"));
         assertTrue("strasse" contains ?", StringUtils.containsIgnoreCase("STRASSE", "?"));
         assertTrue("stra?e" contains itself ignoreCase", StringUtils.containsIgnoreCase("Stra?e",
"STRA?E"));
     }

  @Test
     public void testContainsIgnoreCase_SharpS_LocaleIndependence() {
         // English locale
         Locale.setDefault(Locale.ENLISH);
         assertTrue("EN: ? contains SS", StringUtils.containsIgnoreCase("?", "SS"));
         assertTrue("EN: SS contains ?", StringUtils.containsIgnoreCase("SS", "?"));

         // German locale
         Locale.setDefault(Locale.GERMAN);
         assertTrue("DE: ? contains SS", StringUtils.containsIgnoreCase("?", "SS"));
         assertTrue("DE: SS contains ?", StringUtils.containsIgnoreCase("SS", "?"));

         // Turkish locale should not break ?/SS comparison
         Locale.setDefault(new Locale("tr", "TR"));
         assertTrue("TR: ? contains SS", StringUtils.containsIgnoreCase("?", "SS"));
         assertTrue("TR: SS contains ?", StringUtils.containsIgnoreCase("SS", "?"));
     }

     @Test
     public void testContainsIgnoreCase_Turkish_LocalIndependence_I() {
         Locale.setDefault(new Locale("tr", "TR"));
         // English-style mapping: i and I should be considered equal ignoring case
         assertTrue("i contains I in Turkish locale", StringUtils.containsIgnoreCase("i", "I"));
         assertTrue("I contains i in Turkish locale", StringUtils.containsIgnoreCase("I", "i"));
         assertTrue("mixed word", StringUtils.containsIgnoreCase("istanbul", "ISTANBUL"));
     }

     @Test
     public void testContainsIgnoreCase_Turkish_LocalIndependence_DottedI() {
         Locale.setDefault(new Locale("tr", "TR"));
         // Turkish dotless i (?) is a different character; locale-independent mapping treats it as
different from i
         // ? (U+0131) upper case is I, i upper case is I, so they should match under English rules
         // but ? is not ASCII i, so containsIgnoreCase("?","I") should be true if ? upper is I
         // This is a boundary test: ? does not exist in English, but its uppercase is I in Unicode
         assertTrue("dotless i contains I", StringUtils.containsIgnoreCase("?", "I"));
         assertTrue("I contains dotless i", StringUtils.containsIgnoreCase("I", "?"));
     }

     @Test
     public void testContainsIgnoreCase_False() {
         assertFalse("o match diff strings", StringUtils.containsIgnoreCase("abc", "def"));
         assertFalse("earch longer than str", StringUtils.containsIgnoreCase("ab", "abc"));
         assertFalse("ot substring", StringUtils.containsIgnoreCase("hello", "abc"));
         assertFalse("ase-sensitive mismatch with non-letter only should fail if no match",
                 StringUtils.containsIgnoreCase("abc", "abd"));
     }

     @Test
     public void testContainsIgnoreCase_ongString() {
         // Build a long string of 10k chars
         StringBuilder sb = new StringBuilder();
         for (int i = 0; i < 10000; i++) {
             sb.append('a');
         }
         sb.append("NEEDLE");
         for (int i = 0; i < 10000; i++) {
             sb.append('a');
         }
         String haystack = sb.toString();
         assertTrue("contains needle", StringUtils.containsIgnoreCase(haystack, "Needle"));
     }

     @Test
     public void testContainsIgnoreCase_RepeatedSubstrings() {
         String str = "abcabcabcabcabc";
         assertTrue("basic repeat", StringUtils.containsIgnoreCase(str, "BCA"));
         assertFalse("overlapping search not present", StringUtils.containsIgnoreCase("abcabc",
"cba"));
     }

     @Test
     public void testEqualsIgnoreCase_LocaleIndependence() {
         Locale.setDefault(new Locale("tr", "TR"));
         assertTrue("i equalsIgnoreCase I in Turkish locale", StringUtils.equalsIgnoreCase("i",
"I"));
         assertTrue("? equalsIgnoreCase SS not affected by locale",
StringUtils.equalsIgnoreCase("?", "SS"));
         assertFalse("different strings", StringUtils.equalsIgnoreCase("abc", "def"));
         assertFalse("one is null", StringUtils.equalsIgnoreCase(null, "a"));
     }

     @Test
     public void testStartsWithIgnoreCase_LocaleIndependence() {
         Locale.setDefault(new Locale("tr", "TR"));
         assertTrue("i starts with I", StringUtils.startsWithIgnoreCase("istanbul", "I"));
         assertTrue("? starts with SS", StringUtils.startsWithIgnoreCase("?town", "SS"));
         assertFalse("wrong prefix", StringUtils.startsWithIgnoreCase("abc", "x"));
     }

     @Test
     public void testEndsWithIgnoreCase_LocaleIndependence() {
         Locale.setDefault(new Locale("tr", "TR"));
         assertTrue("i ends with I", StringUtils.endsWithIgnoreCase("Ankarai", "I"));
         assertTrue("? ends with SS", StringUtils.endsWithIgnoreCase("town?", "SS"));
         assertFalse("wrong suffix", StringUtils.endsWithIgnoreCase("abc", "x"));
     }
 }