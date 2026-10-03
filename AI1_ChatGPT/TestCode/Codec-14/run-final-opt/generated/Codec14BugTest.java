package org.apache.commons.codec.language.bm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class Codec14BugTest {

    @Test
    public void testAshkenaziApproximateBendzinCompatibility() {
        final PhoneticEngine engine =
                new PhoneticEngine(NameType.ASHKENAZI, RuleType.APPROX, true);

        assertEquals("bndzn|bntsn|bnzn|vndzn", engine.encode("Bendzin"));
    }

    @Test
    public void testBendzinDoesNotContainIncompatibleVntsnAlternative() {
        final PhoneticEngine engine =
                new PhoneticEngine(NameType.ASHKENAZI, RuleType.APPROX, true);

        assertFalse(engine.encode("Bendzin").contains("vntsn"));
    }

    @Test
    public void testEncodingIsCaseInsensitive() {
        final PhoneticEngine engine =
                new PhoneticEngine(NameType.ASHKENAZI, RuleType.APPROX, true);

        assertEquals(engine.encode("Bendzin"), engine.encode("BENDZIN"));
    }

    @Test
    public void testNonConcatenatingEngineEncodesWordsSeparately() {
        final PhoneticEngine engine =
                new PhoneticEngine(NameType.GENERIC, RuleType.APPROX, false);

        assertEquals(engine.encode("John") + "-" + engine.encode("Smith"),
                engine.encode("John Smith"));
    }

    @Test
    public void testEngineExposesConstructorConfiguration() {
        final PhoneticEngine engine =
                new PhoneticEngine(NameType.SEPHARDIC, RuleType.EXACT, false, 7);

        assertEquals(NameType.SEPHARDIC, engine.getNameType());
        assertEquals(RuleType.EXACT, engine.getRuleType());
        assertFalse(engine.isConcat());
        assertEquals(7, engine.getMaxPhonemes());
        assertNotNull(engine.getLang());
    }

    @Test
    public void testRulesRuleTypeIsRejectedByEngineConstructor() {
        try {
            new PhoneticEngine(NameType.GENERIC, RuleType.RULES, true);
            fail("RULES is an internal rule type and must not be accepted");
        } catch (final IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("ruleType"));
        }
    }

    @Test
    public void testLangInstancesCanGuessLanguagesForAllNameTypes() {
        for (final NameType nameType : NameType.values()) {
            final Languages.LanguageSet languages =
                    Lang.instance(nameType).guessLanguages("Bendzin");

            assertNotNull(languages);
            assertFalse(languages.isEmpty());
        }
    }

    @Test
    public void testMissingLanguageRuleResourceIsRejected() {
        try {
            Lang.loadFromResource("org/apache/commons/codec/language/bm/not-present.txt",
                    Languages.getInstance(NameType.GENERIC));
            fail("A missing language-rule resource must be rejected");
        } catch (final IllegalStateException expected) {
            assertTrue(expected.getMessage().contains("Unable to resolve required resource"));
        }
    }

    @Test
    public void testRuleMatchesOnlyAtSpecifiedPositionAndContexts() {
        final Rule rule = new Rule("abc", "x", "y",
                new Rule.Phoneme("z", Languages.LanguageSet.ANY_LANGUAGE));

        assertTrue(rule.patternAndContextMatches("xabcy", 1));
        assertFalse(rule.patternAndContextMatches("abc y", 0));
        assertFalse(rule.patternAndContextMatches("zabcy", 1));
        assertFalse(rule.patternAndContextMatches("xabcq", 1));
        assertFalse(rule.patternAndContextMatches("xabcy", 2));
    }

    @Test
    public void testRuleAccessorsRetainSpecifiedPattern() {
        final Rule.Phoneme phoneme =
                new Rule.Phoneme("z", Languages.LanguageSet.ANY_LANGUAGE);
        final Rule rule = new Rule("abc", "", "", phoneme);

        assertEquals("abc", rule.getPattern());
        assertEquals(phoneme, rule.getPhoneme());
        assertNotNull(rule.getLContext());
        assertNotNull(rule.getRContext());
    }
}
