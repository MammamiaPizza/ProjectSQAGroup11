package org.jsoup.parser;

 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for CharacterReader targeting non-ASCII and supplementary character
  * handling bugs (Defects4J Jsoup-51). Covers consumeToAny, consumeToAnySorted,
  * rangeEquals, cacheString (indirectly), and related methods.
  */
 public class CharacterReaderTest {

     // 1. ASCII delimiters work correctly
     @Test
     public void testConsumeToAnyWithAscii() {
         CharacterReader reader = new CharacterReader("abc,def;ghi");
         assertEquals("abc", reader.consumeToAny(',', ';'));
         assertEquals(',', reader.current());
     }

     // 2. Non-ASCII BMP delimiters (é, ñ) stop at first match
     @Test
     public void testConsumeToAnyWithNonAsciiBmp() {
         CharacterReader reader = new CharacterReader("café ña");
         assertEquals("caf", reader.consumeToAny('é', 'ñ', ' '));
         assertEquals('é', reader.current());
     }

     // 3. Supplementary delimiters passed as surrogates – stops at first surrogate
     @Test
     public void testConsumeToAnyWithSupplementaryDelimiters() {
         // 💯 (U+1F4AF) = \uD83D\uDCAF
         CharacterReader reader = new CharacterReader("A\uD83D\uDCAF B");
         String result = reader.consumeToAny('\uD83D', '\uDCAF');
         assertEquals("A", result);
         assertEquals('\uD83D', reader.current());
     }

     // 4. consumeToAnySorted with sorted array containing non-ASCII char
     @Test
     public void testConsumeToAnySortedWithNonAscii() {
         CharacterReader reader = new CharacterReader("helloéworld");
         char[] sorted = {'a', 'e', 'o', 'é'};                // numeric: 97,101,111,233
         java.util.Arrays.sort(sorted);                        // guarantee sorted
         assertEquals("h", reader.consumeToAnySorted(sorted)); // stops at 'e'
         assertEquals('e', reader.current());
     }

     // 5. consumeTo(String) with a supplementary character sequence
     @Test
     public void testConsumeToStringWithSupplementary() {
         CharacterReader reader = new CharacterReader("Hello\uD83D\uDCAFWorld");
         assertEquals("Hello", reader.consumeTo("\uD83D\uDCAF"));
         assertEquals('\uD83D', reader.current());
     }

     // 6. consumeTo(String) with non-ASCII BMP
     @Test
     public void testConsumeToStringWithNonAsciiBmp() {
         CharacterReader reader = new CharacterReader("dataéxtra");
         assertEquals("data", reader.consumeTo("é"));
         assertEquals('é', reader.current());
     }

     // 7. rangeEquals returns true for a matching supplementary character range
     @Test
     public void testRangeEqualsWithSupplementary() {
         CharacterReader reader = new CharacterReader("X\uD83D\uDCAFY");
         reader.consume();                                     // pos = 1 (start of 💯)
         assertTrue(reader.rangeEquals(1, 2, "\uD83D\uDCAF"));
     }

     // 8. rangeEquals correctly rejects mismatching content
     @Test
     public void testRangeEqualsMismatch() {
         CharacterReader reader = new CharacterReader("abc");
         reader.consume();                                     // pos = 1
         assertFalse(reader.rangeEquals(1, 2, "bx"));
         assertTrue(reader.rangeEquals(1, 2, "bc"));
     }

     // 9. cacheString correctly handles non-ASCII strings (indirectly via repeated consume)
     @Test
     public void testCacheStringViaConsumeToAny() {
         CharacterReader reader = new CharacterReader("dataéxtra");
         String first = reader.consumeToAny('x');             // "dataé"
         reader = new CharacterReader("dataéxtra");           // fresh reader
         String second = reader.consumeToAny('x');            // should be identical
         assertEquals(first, second);
     }

     // 10. consumeTagName includes non-ASCII characters
     @Test
     public void testConsumeTagNameWithNonAscii() {
         CharacterReader reader = new CharacterReader("«div> rest");
         assertEquals("«div", reader.consumeTagName());
         assertEquals('>', reader.current());
     }

     // 11. Delimiter at position zero returns empty string
     @Test
     public void testDelimiterAtPositionZero() {
         CharacterReader reader = new CharacterReader(",rest");
         assertEquals("", reader.consumeToAny(','));
         assertEquals(',', reader.current());
     }

     // 12. No match returns the entire remaining string
     @Test
     public void testConsumeToAnyNoMatch() {
         CharacterReader reader = new CharacterReader("abc");
         assertEquals("abc", reader.consumeToAny('x', 'y', 'z'));
         assertTrue(reader.isEmpty());
     }
 }