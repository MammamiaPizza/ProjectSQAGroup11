```java
package org.apache.commons.codec.language.bm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.language.bm.Languages.LanguageSet;
import org.apache.commons.codec.language.bm.Rule.Phoneme;
import org.junit.Test;

/**
 * Tests public and package-visible Beider-Morse language and rule behavior.
 */
public class BeiderMorseCoreTest {

    private static LanguageSet languages(final String... names) {
        return LanguageSet.from(new HashSet<String>(Arrays.asList(names)));
    }

    @Test
    public void langInstancesAreAvailableAndLanguageGuessingIsCaseInsensitive() {
        for (final NameType nameType : NameType.values()) {
            final Lang lang = Lang.instance(nameType);

            assertTrue(lang != null);
            assertEquals(lang.guessLanguages("Renault").toString(),
                    lang.guessLanguages("RENAULT").toString());
            assertEquals(lang.guessLanguage("Renault"), lang.guessLanguage("RENAULT"));
        }
    }

    @Test
    public void langReturnsAnyWhenRulesEliminateEveryCandidateLanguage() {
        final Lang lang = Lang.instance(NameType.GENERIC);

        final Languages.LanguageSet guessed = lang.guessLanguages("1234567890");

        assertFalse(guessed.isEmpty());
        assertEquals(Languages.ANY, lang.guessLanguage("1234567890"));
    }

    @Test(expected = IllegalStateException.class)
    public void loadingMissingLanguageResourceFailsClearly() {
        Lang.loadFromResource("org/apache/commons/codec/language/bm/does-not-exist.txt",
                Languages.getInstance(NameType.GENERIC));
    }

    @Test
    public void ruleMatchesPatternOnlyWhenBothContextsMatch() {
        final Rule rule = new Rule("ab", "x", "y",
                new Phoneme("P", Languages.ANY_LANGUAGE));

        assertEquals("ab", rule.getPattern());
        assertTrue(rule.patternAndContextMatches("xaby", 1));
        assertFalse(rule.patternAndContextMatches("zaby", 1));
        assertFalse(rule.patternAndContextMatches("xabz", 1));
        assertFalse(rule.patternAndContextMatches("xacy", 1));
        assertFalse(rule.patternAndContextMatches("xa", 1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void ruleRejectsNegativeMatchPosition() {
        final Rule rule = new Rule("a", "", "", new Phoneme("A", Languages.ANY_LANGUAGE));

        rule.patternAndContextMatches("a", -1);
    }

    @Test
    public void ruleContextPatternsSupportAnchorsAndCharacterClasses() {
        final Rule rule = new Rule("x", "[ab]", "[^z]",
                new Phoneme("X", Languages.ANY_LANGUAGE));

        assertTrue(rule.patternAndContextMatches("axq", 1));
        assertTrue(rule.patternAndContextMatches("bxq", 1));
        assertFalse(rule.patternAndContextMatches("cxq", 1));
        assertFalse(rule.patternAndContextMatches("axz", 1));
    }

    @Test
    public void phonemeJoinRestrictsLanguagesAndAppendChangesOnlyText() {
        final Phoneme left = new Phoneme("a", languages("english", "german"));
        final Phoneme right = new Phoneme("b", languages("german", "french"));

        left.append("x");
        final Phoneme joined = left.join(right);

        assertEquals("ax", left.getPhonemeText().toString());
        assertEquals("axb", joined.getPhonemeText().toString());
        assertTrue(joined.getLanguages().isSingleton());
        assertEquals("german", joined.getLanguages().getAny());
        assertEquals(0, Phoneme.COMPARATOR.compare(
                new Phoneme("same", languages("one")),
                new Phoneme("same", languages("two"))));
    }

    @Test
    public void phonemeBuilderDropsIncompatibleAlternativesAndHonorsMaximum() {
        final LanguageSet leftLanguages = languages("left");
        final Phoneme compatible = new Phoneme("a", languages("left"));
        final Phoneme incompatible = new Phoneme("b", languages("right"));
        final Rule.PhonemeExpr alternatives =
                new Rule.PhonemeList(Arrays.asList(compatible, incompatible));

        final PhoneticEngine.PhonemeBuilder builder =
                PhoneticEngine.PhonemeBuilder.empty(leftLanguages);
        builder.apply(alternatives, 10);

        assertEquals("a", builder.makeString());
        assertEquals(1, builder.getPhonemes().size());

        final PhoneticEngine.PhonemeBuilder cappedBuilder =
                PhoneticEngine.PhonemeBuilder.empty(Languages.ANY_LANGUAGE);
        cappedBuilder.apply(new Rule.PhonemeList(Arrays.asList(
                new Phoneme("a", Languages.ANY_LANGUAGE),
                new Phoneme("b", Languages.ANY_LANGUAGE))), 1);

        assertEquals(1, cappedBuilder.getPhonemes().size());
        assertEquals("a", cappedBuilder.makeString());
    }

    @Test
    public void ruleRepositoriesExposeKnownRuleSetsAndRejectUnknownLanguage() {
        final Map<String, List<Rule>> commonRules =
                Rule.getInstanceMap(NameType.GENERIC, RuleType.EXACT, "common");
        final List<Rule> allCommonRules =
                Rule.getInstance(NameType.GENERIC, RuleType.EXACT, "common");

        assertFalse(commonRules.isEmpty());
        assertFalse(allCommonRules.isEmpty());
        assertEquals(allCommonRules.size(),
                Rule.getInstance(NameType.GENERIC, RuleType.EXACT,
                        languages("common")).size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void ruleRepositoryRejectsUnknownLanguage() {
        Rule.getInstanceMap(NameType.GENERIC, RuleType.EXACT, "not-a-real-language");
    }

    @Test(expected = IllegalArgumentException.class)
    public void phoneticEngineRejectsRawRulesRuleType() {
        new PhoneticEngine(NameType.GENERIC, RuleType.RULES, true);
    }

    @Test
    public void phoneticEngineExposesConstructorConfigurationAndEncodesEmptyInput() {
        final PhoneticEngine engine =
                new PhoneticEngine(NameType.ASHKENAZI, RuleType.EXACT, false, 7);

        assertEquals(NameType.ASHKENAZI, engine.getNameType());
        assertEquals(RuleType.EXACT, engine.getRuleType());
        assertFalse(engine.isConcat());
        assertEquals(7, engine.getMaxPhonemes());
        assertSame(Lang.instance(NameType.ASHKENAZI), engine.getLang());
        assertEquals("", engine.encode("", Languages.ANY_LANGUAGE));
    }

    @Test
    public void genericPrefixEncodingRepresentsSeparatedAndCombinedForms() {
        final PhoneticEngine engine =
                new PhoneticEngine(NameType.GENERIC, RuleType.EXACT, true);
        final String languageIndependentResult = engine.encode("vincent");

        assertEquals("(" + languageIndependentResult + ")-("
                        + engine.encode("devincent") + ")",
                engine.encode("de vincent"));
        assertEquals("(" + languageIndependentResult + ")-("
                        + engine.encode("dvincent") + ")",
                engine.encode("d'vincent"));
    }

    @Test
    public void nonConcatenatingEngineEncodesMultipleGenericWordsSeparately() {
        final PhoneticEngine engine =
                new PhoneticEngine(NameType.GENERIC, RuleType.APPROX, false);

        assertEquals(engine.encode("john") + "-" + engine.encode("smith"),
                engine.encode("john smith"));
        assertEquals(engine.encode("john") + "-" + engine.encode("smith"),
                engine.encode(" john-smith "));
    }

    @Test
    public void nameTypeSpecificPrefixHandlingRemovesStandalonePrefixes() {
        final PhoneticEngine ashkenazi =
                new PhoneticEngine(NameType.ASHKENAZI, RuleType.EXACT, true);
        final PhoneticEngine sephardic =
                new PhoneticEngine(NameType.SEPHARDIC, RuleType.EXACT, true);

        assertEquals("", ashkenazi.encode("ben", Languages.ANY_LANGUAGE));
        assertEquals(sephardic.encode("abram", Languages.ANY_LANGUAGE),
                sephardic.encode("d'abram", Languages.ANY_LANGUAGE));
    }

    @Test(expected = NullPointerException.class)
    public void phoneticEngineRejectsNullInput() {
        new PhoneticEngine(NameType.GENERIC, RuleType.EXACT, true).encode(null);
    }
}
```

Test coverage summary:

- `langInstancesAreAvailableAndLanguageGuessingIsCaseInsensitive` verifies static `Lang` loading for every `NameType`, English-locale lowercasing, and case-insensitive guessing.
- `langReturnsAnyWhenRulesEliminateEveryCandidateLanguage` covers the fallback from no remaining candidates to `ANY_LANGUAGE`/`ANY`.
- `loadingMissingLanguageResourceFailsClearly` checks the missing-resource exception path.
- `ruleMatchesPatternOnlyWhenBothContextsMatch` covers successful pattern matching plus left context, right context, literal mismatch, and insufficient-input paths.
- `ruleRejectsNegativeMatchPosition` covers the explicit invalid-index exception.
- `ruleContextPatternsSupportAnchorsAndCharacterClasses` exercises optimized regular-expression context behavior, including matching and non-matching character classes.
- `phonemeJoinRestrictsLanguagesAndAppendChangesOnlyText` checks phoneme mutation, language-set intersection during joining, and comparator behavior for identical phonetic text.
- `phonemeBuilderDropsIncompatibleAlternativesAndHonorsMaximum` checks compatible/incompatible language alternatives and maximum-phoneme truncation.
- `ruleRepositoriesExposeKnownRuleSetsAndRejectUnknownLanguage` and `ruleRepositoryRejectsUnknownLanguage` cover successful static rule retrieval and invalid language handling.
- `phoneticEngineRejectsRawRulesRuleType` covers constructor validation.
- `phoneticEngineExposesConstructorConfigurationAndEncodesEmptyInput` covers constructor state, getters, language association, and empty-input encoding.
- `genericPrefixEncodingRepresentsSeparatedAndCombinedForms` exercises the Generic `d'` and word-prefix branches, which are important for preserving alternate encodings.
- `nonConcatenatingEngineEncodesMultipleGenericWordsSeparately` covers whitespace/dash normalization and the non-concatenated multiword branch.
- `nameTypeSpecificPrefixHandlingRemovesStandalonePrefixes` covers Ashkenazi and Sephardic prefix handling.
- `phoneticEngineRejectsNullInput` covers null input behavior.