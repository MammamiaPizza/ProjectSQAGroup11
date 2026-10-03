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
    public void ruleContextPatternsSupportRegularExpressionFallbackAndExposeComponents() {
        final Phoneme phoneme = new Phoneme("X", Languages.ANY_LANGUAGE);
        final Rule rule = new Rule("x", "(a|b)", "[cd]", phoneme);

        assertSame(phoneme, rule.getPhoneme());
        assertTrue(rule.getLContext().isMatch("a"));
        assertTrue(rule.getRContext().isMatch("c"));
        assertTrue(rule.patternAndContextMatches("axc", 1));
        assertTrue(rule.patternAndContextMatches("bxd", 1));
        assertFalse(rule.patternAndContextMatches("zxc", 1));
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
    public void phonemeConstructorsPreserveAndCombineTextAndExposeSingletonExpression() {
        final Phoneme left = new Phoneme("left", languages("english"));
        final Phoneme right = new Phoneme("right", languages("german"));

        final Phoneme combined = new Phoneme(left, right);
        final Phoneme combinedWithExplicitLanguages =
                new Phoneme(left, right, languages("french"));

        assertEquals("leftright", combined.getPhonemeText().toString());
        assertEquals("english", combined.getLanguages().getAny());
        assertEquals("leftright", combinedWithExplicitLanguages.getPhonemeText().toString());
        assertEquals("french", combinedWithExplicitLanguages.getLanguages().getAny());
        assertSame(left, left.getPhonemes().iterator().next());
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
    public void phonemeBuilderBecomesEmptyWhenEveryAlternativeIsLanguageIncompatible() {
        final PhoneticEngine.PhonemeBuilder builder =
                PhoneticEngine.PhonemeBuilder.empty(languages("english"));

        builder.apply(new Rule.PhonemeList(Arrays.asList(
                new Phoneme("a", languages("german")),
                new Phoneme("b", languages("french")))), 10);

        assertTrue(builder.getPhonemes().isEmpty());
        assertEquals("", builder.makeString());
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

    @Test
    public void ruleRepositoryUsesAnyLanguageRulesForNonSingletonLanguageSets() {
        final Map<String, List<Rule>> anyRules =
                Rule.getInstanceMap(NameType.GENERIC, RuleType.APPROX,
                        Languages.ANY_LANGUAGE);
        final Map<String, List<Rule>> ambiguousLanguageRules =
                Rule.getInstanceMap(NameType.GENERIC, RuleType.APPROX,
                        languages("english", "german"));

        assertSame(anyRules, ambiguousLanguageRules);
        assertFalse(ambiguousLanguageRules.isEmpty());
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

    @Test
    public void enginePreservesCharactersForWhichNoRuleExists() {
        final PhoneticEngine engine =
                new PhoneticEngine(NameType.GENERIC, RuleType.EXACT, true);

        assertEquals("1", engine.encode("1", Languages.ANY_LANGUAGE));
    }

    @Test
    public void bendzinDoesNotRetainTheSpuriousApproximateEncodingFromCodec187() {
        final PhoneticEngine engine =
                new PhoneticEngine(NameType.GENERIC, RuleType.APPROX, true);

        assertEquals("bndzn|bntsn|bnzn|vndzn", engine.encode("Bendzin"));
    }

    @Test(expected = NullPointerException.class)
    public void phoneticEngineRejectsNullInput() {
        new PhoneticEngine(NameType.GENERIC, RuleType.EXACT, true).encode(null);
    }
}
```

New test coverage and fault targets:

- `ruleContextPatternsSupportRegularExpressionFallbackAndExposeComponents` exercises the regular-expression fallback path in `Rule.pattern`, verifies `getLContext`, `getRContext`, and `getPhoneme`, and covers both successful and unsuccessful left/right context matches.
- `phonemeConstructorsPreserveAndCombineTextAndExposeSingletonExpression` covers both `Phoneme` copy/combining constructors and the singleton `getPhonemes()` behavior.
- `phonemeBuilderBecomesEmptyWhenEveryAlternativeIsLanguageIncompatible` covers the `PhonemeBuilder.apply` path where no phoneme survives language-set restriction.
- `ruleRepositoryUsesAnyLanguageRulesForNonSingletonLanguageSets` covers the non-singleton branch of `Rule.getInstanceMap(NameType, RuleType, LanguageSet)`, which must resolve ambiguous language sets through the `any` rule set.
- `enginePreservesCharactersForWhichNoRuleExists` covers the `RulesApplication` no-rule-found path and the final-rule behavior that appends unmatched input characters unchanged.
- `bendzinDoesNotRetainTheSpuriousApproximateEncodingFromCodec187` is the regression test for CODEC-187 / Codec-14. It distinguishes the expected four encodings from the buggy result containing the extra `vntsn` encoding.