package org.apache.commons.lang3.text.translate;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.fail;

 import java.io.IOException;
 import java.io.StringWriter;
 import java.io.Writer;
 import java.nio.CharBuffer;

 import org.junit.Test;

 /**
  *
  * Tests for {@link LookupTranslator} focusing on the translate() return value
  * (codepoint consumption) and the greedy longest-match behaviour.
  * <p>
  * The bug LANG-882 caused translate() to always return 0 regardless of
  * whether a match was found.  These tests use both {@code String} and
  * non-{@code String} {@code CharSequence} inputs to exercise the fix that
  * requires keys to behave as proper {@code HashMap} keys.
  * </p>
  */
 public class LookupTranslatorTest {

     // --- helper ----------------------------------------------------------------

     private static void assertTranslate(LookupTranslator lt, CharSequence input, int index,
             int expectedConsumed, String expectedOutput) {
         StringWriter sw = new StringWriter();
         try {
             int consumed = lt.translate(input, index, sw);
             assertEquals("codepoint consumption", expectedConsumed, consumed);
             assertEquals("writer content", expectedOutput, sw.toString());
         } catch (IOException e) {
             fail("Unexpected IOException: " + e.getMessage());
         }
     }

     // --- tests ---------------------------------------------------------------

     @Test
     public void testBasicTranslation() {
         LookupTranslator lt = new LookupTranslator(
                 new CharSequence[][] { { "abc", "def" } });
         assertTranslate(lt, "abc", 0, 3, "def");
     }

     @Test
     public void testShortestKey() {
         LookupTranslator lt = new LookupTranslator(
                 new CharSequence[][] { { "a", "X" } });
         assertTranslate(lt, "abc", 0, 1, "X");
     }

     @Test
     public void testLongestMatchGreedy() {
         LookupTranslator lt = new LookupTranslator(
                 new CharSequence[][] { { "ab", "AB" }, { "abc", "ABC" } });
         assertTranslate(lt, "abcde", 0, 3, "ABC");
     }

     @Test
     public void testNoMatch() {
         LookupTranslator lt = new LookupTranslator(
                 new CharSequence[][] { { "xyz", "X" } });
         assertTranslate(lt, "abc", 0, 0, "");
     }

     @Test
     public void testInputShorterThanAllKeys() {
         LookupTranslator lt = new LookupTranslator(
                 new CharSequence[][] { { "abcd", "X" } });
         assertTranslate(lt, "ab", 0, 0, "");
     }

     @Test
     public void testIndexAtEnd() {
         LookupTranslator lt = new LookupTranslator(
                 new CharSequence[][] { { "ab", "X" } });
         assertTranslate(lt, "ab", 2, 0, "");
     }

     @Test
     public void testEmptyInput() {
         LookupTranslator lt = new LookupTranslator(
                 new CharSequence[][] { { "a", "X" } });
         assertTranslate(lt, "", 0, 0, "");
     }

     @Test
     public void testNullLookup() {
         // Constructor should accept null gracefully (shortest/longest remain 0 /
Integer.MAX_VALUE)
         LookupTranslator lt = new LookupTranslator((CharSequence[][]) null);
         assertTranslate(lt, "abc", 0, 0, "");
     }

     @Test
     public void testEmptyLookup() {
         LookupTranslator lt = new LookupTranslator(new CharSequence[0][0]);
         assertTranslate(lt, "abc", 0, 0, "");
     }

     @Test
     public void testMultipleMatches() {
         LookupTranslator lt = new LookupTranslator(
                 new CharSequence[][] { { "a", "1" }, { "b", "2" } });
         StringWriter sw = new StringWriter();
         try {
             int c1 = lt.translate("ab", 0, sw);
             assertEquals(1, c1);
             int c2 = lt.translate("ab", 1, sw);
             assertEquals(1, c2);
             assertEquals("12", sw.toString());
         } catch (IOException e) {
             fail("Unexpected IOException: " + e.getMessage());
         }
     }

     /**
      * Triggers LANG-882: input is a non-{@code String} {@code CharSequence}
      * (here {@code CharBuffer}).  The bug caused translate() to return 0
      * even though a matching key exists.
      */
     @Test
     public void testLang882() {
         // Use a CharSequence that is not a String to replicate the original failing scenario
         LookupTranslator lt = new LookupTranslator(
                 new CharSequence[][] { { "aaa", "BBB" } });
         // CharBuffer.wrap returns a CharSequence whose subSequence does NOT return a String
         CharBuffer input = CharBuffer.wrap("aaabbb");
         assertTranslate(lt, input, 0, 3, "BBB");
     }

 }
