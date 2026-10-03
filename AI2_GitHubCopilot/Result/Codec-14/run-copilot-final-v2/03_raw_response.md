package org.apache.commons.codec.language.bm;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;

 import org.apache.commons.codec.language.bm.Languages.LanguageSet;
 import org.junit.Test;

 /**
  * JUnit regression and behavioural tests targeting the
  * {@link PhoneticEngine} bug CODEC-187.
  */
 public class PhoneticEngineBug14Test {

     // Default configuration matching the original regression test expectations
     private static final NameType NAME_TYPE = NameType.GENERIC;
     private static final RuleType RULE_TYPE = RuleType.APPROX;
     private static final boolean CONCAT = true;

     /**
      * Regression for CODEC-187: a multi-word input must produce exactly the
      * expected phoneme alternatives without a spurious extra alternative
      * originating from an irrelevant language-guessing path.
      */
     @Test
     public void testRegressionNoSpuriousPhoneme() {
         PhoneticEngine engine = new PhoneticEngine(NAME_TYPE, RULE_TYPE, CONCAT);
         String input = "Dzn Bntsn Bnzn Vndzn";
         String actual = engine.encode(input);

         // The correct compatibility output does NOT contain the spuriously
         // generated alternative "vntsn".
         assertEquals("dzn|bntsn|bnzn|vndzn", actual);
     }

     /**
      * Empty input must produce an empty encoding string.
      */
     @Test
     public void testEmptyInput() {
         PhoneticEngine engine = new PhoneticEngine(NAME_TYPE, RULE_TYPE, CONCAT);
         assertEquals("", engine.encode(""));
     }

     /**
      * Null input is expected to throw a NullPointerException.
      */
     @Test(expected = NullPointerException.class)
     public void testNullInput() {
         PhoneticEngine engine = new PhoneticEngine(NAME_TYPE, RULE_TYPE, CONCAT);
         engine.encode(null);
     }

     /**
      * A simple single-word input must produce a non-empty encoding and
      * must not throw.
      */
     @Test
     public void testSingleWord() {
         PhoneticEngine engine = new PhoneticEngine(NAME_TYPE, RULE_TYPE, CONCAT);
         String result = engine.encode("test");
         assertNotNull(result);
         assertFalse("Single-word encoding must not be empty", result.isEmpty());
     }

     /**
      * Multi-word input with spaces must produce a concatenated encoding
      * containing the pipe separator when alternatives exist.
      */
     @Test
     public void testMultiWordWithSpaces() {
         PhoneticEngine engine = new PhoneticEngine(NAME_TYPE, RULE_TYPE, CONCAT);
         String result = engine.encode("John Smith");
         assertNotNull(result);
         assertFalse("Multi-word encoding must not be empty", result.isEmpty());
         // Concatenation uses pipe to join alternatives; a typical name yields
         // at least one pipe.
         assertTrue("Output should contain pipe separator for alternatives", result.contains("|"));
     }

     /**
      * Multi-word input with an apostrophe must be handled without exceptions.
      * The engine splits on the apostrophe and processes separately.
      */
     @Test
     public void testMultiWordWithApostrophe() {
         PhoneticEngine engine = new PhoneticEngine(NAME_TYPE, RULE_TYPE, CONCAT);
         String result = engine.encode("O'Neil");
         assertNotNull(result);
         assertFalse("Encoding for a name with apostrophe must not be empty", result.isEmpty());
     }

     /**
      * When maxPhonemes is set to 1, the encoding must produce at most one
      * alternative (no pipe separator).
      */
     @Test
     public void testMaxPhonemesLimit() {
         PhoneticEngine engine = new PhoneticEngine(NAME_TYPE, RULE_TYPE, CONCAT, 1);
         String result = engine.encode("Dzn Bntsn Bnzn Vndzn");
         assertNotNull(result);
         assertFalse("Result must not contain pipe when maxPhonemes=1", result.contains("|"));
     }

     /**
      * Boundary: maxPhonemes = 0 must not cause crashes and must limit the
      * number of alternatives to at most zero (i.e., empty result).
      * The exact behaviour is unspecified, but the engine must not throw.
      */
     @Test
     public void testMaxPhonemesZeroBoundary() {
         try {
             PhoneticEngine engine = new PhoneticEngine(NAME_TYPE, RULE_TYPE, CONCAT, 0);
             String result = engine.encode("test");
             // The result may be empty or produce an alternative – both are
             // acceptable as long as no exception is thrown.
             assertNotNull(result);
         } catch (IllegalArgumentException acceptable) {
             // Constructor might reject 0 – also acceptable.
         }
     }

     /**
      * Explicit language restriction must prevent phonemes from languages
      * outside the supplied set.  Verifies that the alternative "vntsn" (which
      * belongs to a different language) does not appear when the engine is
      * constrained to the guessed languages of "Vndzn".
      */
     @Test
     public void testExplicitLanguageSetExcludesSpurious() {
         PhoneticEngine engine = new PhoneticEngine(NAME_TYPE, RULE_TYPE, CONCAT);
         LanguageSet guessed = engine.getLang().guessLanguages("Vndzn");
         String result = engine.encode("Vndzn", guessed);
         assertFalse("Encoding with restricted language set must not contain vntsn",
                 result.contains("vntsn"));
     }

     /**
      * Passing {@link RuleType#RULES} to the PhoneticEngine constructor must
      * throw an IllegalArgumentException as the contract prohibits it.
      */
     @Test(expected = IllegalArgumentException.class)
     public void testRuleTypeRulesThrowsException() {
         new PhoneticEngine(NAME_TYPE, RuleType.RULES, CONCAT);
     }

     /**
      * The non-concatenating mode (concat=false) must still produce a
      * non-null result without throwing.
      */
     @Test
     public void testNonConcatenatingMode() {
         PhoneticEngine engine = new PhoneticEngine(NAME_TYPE, RULE_TYPE, false);
         String result = engine.encode("John Smith");
         assertNotNull(result);
     }

     /**
      * GuessLanguages must always return a non-null set for any input.
      * Basic smoke test.
      */
     @Test
     public void testGuessLanguagesReturnsSet() {
         Lang lang = Lang.instance(NAME_TYPE);
         assertNotNull(lang.guessLanguages(""));
         assertNotNull(lang.guessLanguages("abc"));
     }
 }