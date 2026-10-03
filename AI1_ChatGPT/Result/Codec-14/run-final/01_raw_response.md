## Scope and constraints

This analysis is limited to the supplied `Codec-14b` production sources, the stated Maven/JUnit context, and the bug-report excerpt. No test class or production-code modification is generated.

The target behavior depends heavily on Beider–Morse resource files and related types that were **not supplied in usable form** (`Languages`, `NameType`, `RuleType`, `ResourceConstants`, and the contents of the three modified `*_lang.txt` files). Therefore, some output-level expectations—especially for Codec-14—cannot be determined reliably from the supplied information.

---

## 1. Public methods and constructors that should be tested

### `Lang`

Public API:

- `static Lang instance(NameType nameType)`
- `static Lang loadFromResource(String languageRulesResourceName, Languages languages)`
- `String guessLanguage(String text)`
- `Languages.LanguageSet guessLanguages(String input)`

The class constructor is private and cannot be directly tested or invoked externally.

### `PhoneticEngine`

Public constructors:

- `PhoneticEngine(NameType nameType, RuleType ruleType, boolean concat)`
- `PhoneticEngine(NameType nameType, RuleType ruleType, boolean concat, int maxPhonemes)`

Public API:

- `String encode(String input)`
- `String encode(String input, Languages.LanguageSet languageSet)`
- `Lang getLang()`
- `NameType getNameType()`
- `RuleType getRuleType()`
- `boolean isConcat()`
- `int getMaxPhonemes()`

Package-private nested classes exist (`PhonemeBuilder`, `RulesApplication`) but are not public production API. Tests in package `org.apache.commons.codec.language.bm` could exercise them, but their inclusion should depend on the intended test scope.

### `Rule`

Public API:

- `static List<Rule> getInstance(NameType nameType, RuleType rt, Languages.LanguageSet langs)`
- `static List<Rule> getInstance(NameType nameType, RuleType rt, String lang)`
- `static Map<String, List<Rule>> getInstanceMap(NameType nameType, RuleType rt, Languages.LanguageSet langs)`
- `static Map<String, List<Rule>> getInstanceMap(NameType nameType, RuleType rt, String lang)`
- `Rule(String pattern, String lContext, String rContext, PhonemeExpr phoneme)`
- `RPattern getLContext()`
- `String getPattern()`
- `PhonemeExpr getPhoneme()`
- `RPattern getRContext()`
- `boolean patternAndContextMatches(CharSequence input, int i)`

Nested public APIs:

#### `Rule.Phoneme`

- `Phoneme(CharSequence phonemeText, Languages.LanguageSet languages)`
- `Phoneme(Phoneme phonemeLeft, Phoneme phonemeRight)`
- `Phoneme(Phoneme phonemeLeft, Phoneme phonemeRight, Languages.LanguageSet languages)`
- `Phoneme append(CharSequence str)`
- `Languages.LanguageSet getLanguages()`
- `Iterable<Phoneme> getPhonemes()`
- `CharSequence getPhonemeText()`
- Deprecated: `Phoneme join(Phoneme right)`
- `String toString()`
- Public constant: `Comparator<Phoneme> COMPARATOR`

#### `Rule.PhonemeList`

- `PhonemeList(List<Phoneme> phonemes)`
- `List<Phoneme> getPhonemes()`

#### `Rule.PhonemeExpr`

- `Iterable<Phoneme> getPhonemes()`

#### `Rule.RPattern`

- `boolean isMatch(CharSequence input)`

#### `Rule` public constants

- `RPattern ALL_STRINGS_RMATCHER`
- `String ALL`

---

## 2. Input types and valid input ranges

### `Lang`

| API | Input | Apparent valid range |
|---|---|---|
| `instance` | `NameType` | A supported enum value. The source references `ASHKENAZI`, `SEPHARDIC`, and `GENERIC`. |
| `loadFromResource` | Resource-name `String`, `Languages` | A non-null classpath resource name identifying a correctly formatted language-rule resource, plus a non-null `Languages` instance. |
| `guessLanguage` | `String` | Non-null text, including empty strings and arbitrary Unicode/case. |
| `guessLanguages` | `String` | Non-null text, including empty strings and arbitrary Unicode/case. |

There is no explicit input validation in `guessLanguage` or `guessLanguages`; null input reaches `toLowerCase(...)` and causes `NullPointerException`.

### `PhoneticEngine`

| API | Input | Apparent valid range |
|---|---|---|
| Constructors | `NameType`, `RuleType`, `boolean` | Supported enum values; `RuleType.RULES` is explicitly prohibited. |
| Four-arg constructor | `int maxPhonemes` | No source-level validation. The intended useful range appears to be positive values, but zero and negative values are accepted by the constructor. |
| `encode(String)` | `String` | Non-null input. Empty strings, spaces, hyphens, apostrophes, prefixes, and multi-word strings all reach distinct logic paths. |
| `encode(String, LanguageSet)` | `String`, `LanguageSet` | Both must effectively be non-null. A singleton versus non-singleton language set changes rule-map selection. |

### `Rule`

| API | Input | Apparent valid range |
|---|---|---|
| `Rule` constructor | Pattern and contexts as strings; a `PhonemeExpr` | Non-null values are required for meaningful use. No constructor validation is present. |
| `patternAndContextMatches` | `CharSequence input`, `int i` | Non-null input and `i >= 0`; `i` may be at or beyond the end of input, in which case normal non-match behavior generally applies. |
| Rule lookup methods | `NameType`, `RuleType`, language name or `LanguageSet` | Existing name/rule/language resource combinations. |
| `Phoneme` constructors | Text and language set / other phonemes | Non-null values are needed; no explicit validation exists. |
| `Phoneme.append` | `CharSequence` | Normally non-null. |
| `PhonemeList` | `List<Phoneme>` | Normally non-null; list is retained directly, not copied. |
| `RPattern.isMatch` | `CharSequence` | Non-null input for implementations created by `Rule`. |

---

## 3. Conditions and reachable branches

### `Lang`

#### `loadFromResource`

Reachable behavior includes:

- Resource exists versus resource missing.
  - Missing resource: `IllegalStateException`.
- Empty lines are skipped.
- `//` end-of-line comments are stripped.
- `/* ... */` multiline-comment mode is entered and exited only when a later line ends with `ResourceConstants.EXT_CMT_END`.
- Non-comment lines must split into exactly three whitespace-separated fields.
  - Otherwise: `IllegalArgumentException`.
- Rule regex compilation can fail with regex-related exceptions.
- Third column:
  - Exactly `"true"` results in `acceptOnMatch = true`.
  - Every other value, including malformed text, results in `false`; there is no strict boolean validation.
- Language lists are split on `+`.

#### `guessLanguages`

- Input is lowercased using `Locale.ENGLISH`.
- Every matching accept rule intersects the candidate set.
- Every matching reject rule removes its language set.
- No matching rules leaves all languages available.
- An empty final candidate set is converted to `Languages.ANY_LANGUAGE`.
- A singleton language set is later converted by `guessLanguage` to its sole language; any non-singleton result becomes `Languages.ANY`.

### `PhoneticEngine`

#### Constructor

- `ruleType == RuleType.RULES`:
  - Throws `IllegalArgumentException`.
- Any other `RuleType`:
  - Stores fields and obtains a `Lang` instance for the `NameType`.
- `maxPhonemes` has no validation branch.

#### `encode(String)`

- Obtains language candidates from `Lang.guessLanguages(input)`.
- Delegates to the two-argument overload.

#### `encode(String, LanguageSet)`

Important reachable branches:

1. **Input normalization**
   - Lowercase with `Locale.ENGLISH`.
   - Replace all `-` with spaces.
   - Trim leading and trailing whitespace.

2. **Generic-name prefix expansion (`NameType.GENERIC`)**
   - An input beginning with `"d'"` produces:
     - `("(" + encode(remainder) + ")-(" + encode("d" + remainder) + ")")`.
   - Inputs beginning with a configured generic prefix plus a space (such as `de ` or `van `) produce separate remainder and combined encodings in the same parenthesized form.
   - Inputs without those forms continue normally.

3. **Name-type word processing**
   - `SEPHARDIC`:
     - For each word, keeps only text after the final apostrophe.
     - Removes recognized prefixes.
   - `ASHKENAZI`:
     - Retains words, then removes recognized prefixes.
   - `GENERIC`:
     - Retains all words unchanged.
   - The `default` switch branch throws `IllegalStateException`, but should be unreachable for a normal `NameType` enum value.

4. **Concatenation mode**
   - `concat == true`: join processed words with a single space and encode as one sequence.
   - `concat == false` and exactly one processed word:
     - Uses `words.iterator().next()`, notably from the original split list rather than `words2`.
   - `concat == false` and multiple processed words:
     - Recursively encodes each processed word and joins results using `-`.

5. **Rule application**
   - At each character position, the first matching rule for the first character is applied.
   - No matching initial-rule entry or no matching contextual rule:
     - The input character is effectively dropped during initial rule application.
   - Final rules:
     - Empty final-rule maps return the original builder unchanged.
     - No matching final rule preserves the current character by appending it.

6. **Phoneme count limiting**
   - Phoneme combinations are limited to `maxPhonemes`.
   - A zero or negative maximum can result in no generated phonemes in `PhonemeBuilder.apply`; the constructor does not reject these values.

### `Rule`

#### Rule lookup

- `getInstanceMap(..., LanguageSet)`:
  - Singleton language set: loads that language’s map.
  - Non-singleton language set: loads `Languages.ANY`.
- `getInstanceMap(..., String)`:
  - Existing combination returns a map.
  - Missing combination throws `IllegalArgumentException`.

#### `patternAndContextMatches`

- `i < 0`: throws `IndexOutOfBoundsException`.
- Pattern extends beyond remaining input: returns `false`.
- Pattern text mismatch: returns `false`.
- Right-context mismatch: returns `false`.
- Left-context mismatch: returns `false`.
- Pattern and both contexts match: returns `true`.

#### Regex optimization paths

Although private, they affect observable matching behavior via constructed `Rule` instances:

- Exact literal regexes.
- Empty exact regex.
- Start-anchored literal regex.
- End-anchored literal regex.
- Character classes such as `[abc]` and negated classes `[^abc]`, with optional anchors.
- All other regex syntax delegates to `java.util.regex.Pattern`.

#### `Phoneme`

- `append` mutates the current `Phoneme` and returns the same instance.
- `getPhonemes` returns a singleton iterable containing itself.
- `join` returns a new phoneme with concatenated text and intersected language sets.
- `COMPARATOR` compares only phoneme text, not languages.
- `toString` includes both phoneme text and language-set rendering.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

### Normal cases

- Language guessing for ordinary non-null words.
- Case-insensitive language guessing and encoding under `Locale.ENGLISH`.
- Encoding a one-word name.
- Encoding names separated by spaces and/or hyphens.
- Encoding for each supported `NameType`.
- Encoding with `concat` both enabled and disabled.
- Encoding with a singleton language set and a non-singleton/any language set.
- Rule matching with exact pattern/context match.
- Rule lookup for valid loaded resources.
- Phoneme construction, append, joining, ordering, and textual formatting.

### Boundary cases

- Empty string:
  - `Lang.guessLanguages("")` is valid at the source level.
  - `PhoneticEngine.encode("")` proceeds through normalization and rule selection; exact expected output depends on resources and `Languages`.
- Whitespace-only input.
- Input with leading/trailing whitespace.
- Repeated whitespace after normalization.
- Input containing only hyphens, which normalize to spaces.
- Input at prefix boundaries:
  - Generic `"d'"`.
  - Generic prefix followed by a space, e.g., `de X`.
  - A prefix-like token without a following space, e.g., `deX`.
- Apostrophe boundaries for Sephardic processing.
- `maxPhonemes` values:
  - Default (`20`).
  - One.
  - Zero.
  - Negative.
  - A small limit that truncates otherwise multiple phoneme alternatives.
- Rule matching at:
  - Index `0`.
  - Last possible index.
  - Index equal to input length.
  - Input too short for the rule’s pattern.
- Empty rule pattern and/or empty contexts, if constructing a `Rule` directly.

### Invalid and exceptional cases established by source

| Case | Observable result from supplied source |
|---|---|
| `new PhoneticEngine(..., RuleType.RULES, ...)` | `IllegalArgumentException` |
| `Lang.loadFromResource` with an unresolved resource | `IllegalStateException` |
| Malformed non-comment language-resource line | `IllegalArgumentException` |
| Missing rule resource during static rule loading | `IllegalArgumentException`, potentially wrapped during static initialization |
| Unknown rule lookup combination | `IllegalArgumentException` |
| `Rule.patternAndContextMatches(input, negativeIndex)` | `IndexOutOfBoundsException` |
| `encode(null)` | `NullPointerException` is expected by dereference, though no explicit contract documents it |
| `Lang.guessLanguage(null)` / `guessLanguages(null)` | `NullPointerException` from `toLowerCase` |
| `encode(input, null)` | `NullPointerException` while selecting rules |
| `new Rule(..., nullContext, ...)` | `NullPointerException` during constructor regex construction |
| Null `input` to `patternAndContextMatches` | `NullPointerException`, unless a negative index is supplied first, in which case the explicit `IndexOutOfBoundsException` occurs first |

For null enum constructor arguments, behavior is not explicitly validated. For example, a null `RuleType` bypasses the `RuleType.RULES` check but will fail later when rules are accessed. Such cases can be tested as current behavior, but they do not have a documented API contract in the supplied material.

---

## 5. Required constructors, dependencies, and external objects

### Required production dependencies

The supplied classes depend on the following project types and resources, which are not fully supplied:

- `org.apache.commons.codec.language.bm.NameType`
- `org.apache.commons.codec.language.bm.RuleType`
- `org.apache.commons.codec.language.bm.Languages`
- `Languages.LanguageSet`
- `org.apache.commons.codec.language.bm.ResourceConstants`
- All Beider–Morse language and rule resource files loaded by `Lang` and `Rule`.

Examples of runtime resources needed include:

- `org/apache/commons/codec/language/bm/ash_lang.txt`
- `org/apache/commons/codec/language/bm/gen_lang.txt`
- `org/apache/commons/codec/language/bm/sep_lang.txt`
- Rule resources named according to:
  - `org/apache/commons/codec/language/bm/${nameType}_${ruleType}_${language}.txt`
- Included rule resources referenced by `#include`.

### Constructors needed for test setup

- `new PhoneticEngine(NameType, RuleType, boolean)`
- `new PhoneticEngine(NameType, RuleType, boolean, int)`
- `new Rule(String, String, String, Rule.PhonemeExpr)`
- `new Rule.Phoneme(CharSequence, Languages.LanguageSet)`
- `new Rule.PhonemeList(List<Rule.Phoneme>)`

`Lang` instances normally must be obtained with `Lang.instance(NameType)`. Custom `Lang` instances can only be built indirectly through `Lang.loadFromResource(...)`, requiring a test resource on the classpath.

### Test-access consideration

A test that needs to exercise package-private `PhoneticEngine.PhonemeBuilder` or `RulesApplication` must declare package:

```java
package org.apache.commons.codec.language.bm;
```

No mocking framework is identified in the supplied context, and none is necessary for straightforward API tests. Resource-dependent integration tests require the actual project resource set.

---

## 6. JUnit version and build tool

Supplied project information states:

- **JUnit:** `junit-4.12-hamcrest-1.3.jar`
- **Build tool:** Maven

Tests should therefore use JUnit 4 style, such as:

- `org.junit.Test`
- `org.junit.Assert`
- `@Test(expected = SomeException.class)` where appropriate
- Hamcrest 1.3 matchers only if needed

---

## 7. Available test oracle

### Available oracle material

1. **Javadocs and inline implementation contracts**
   - `Lang` is intended to guess possible source languages.
   - `PhoneticEngine` should encode names to phonetic representations.
   - `Rule.patternAndContextMatches` has a clear stated matching contract.
   - `PhoneticEngine` constructor explicitly disallows `RuleType.RULES`.
   - Rule lookup methods clearly specify that unavailable combinations are invalid.

2. **Source-defined deterministic behavior**
   - Getter values must reflect constructor arguments.
   - Case conversion uses `Locale.ENGLISH`.
   - Prefix handling, word splitting, and exception types can be asserted from source.
   - `Rule` context/pattern matching can be tested with directly constructed rules and hand-built `PhonemeExpr` objects, independently of external rule resources.

3. **Bug-report failure excerpt**
   - Triggering test:
     - `PhoneticEngineRegressionTest::testCompatibilityWithOriginalVersion`
   - Expected output ends with:
     - `...dzn|bntsn|bnzn|vndzn`
   - Buggy output ends with:
     - `...dzn|bntsn|bnzn|vndzn|vntsn`

### Oracle limitations

The complete triggering input and complete expected output are not supplied. Therefore, the excerpt alone is insufficient to create a reliable direct regression assertion for the reported failing case.

The supplied `ash_lang.txt`, `gen_lang.txt`, and `sep_lang.txt` artifacts are blank in the prompt. Since these files are among the bug-modified sources and language guessing drives phonetic rule selection, their contents are essential to establish the intended corrected behavior.

---

## 8. Behaviors related to Codec-14 that should be tested

The bug report identifies a regression in compatibility output:

- Expected output did **not** include a final alternative `|vntsn`.
- Actual buggy output included that extra phonetic alternative.

Given the modified areas, relevant behavior categories are:

1. **Language classification changes**
   - `Lang.guessLanguages` for names affected by the changed `ash_lang.txt`, `gen_lang.txt`, and `sep_lang.txt` resources.
   - Case-insensitive matching of revised language rules.
   - Effects of accept and reject rules on the final `LanguageSet`.
   - Conversion of zero surviving language candidates to `Languages.ANY_LANGUAGE`.

2. **Selection of phonetic rules from guessed languages**
   - `PhoneticEngine.encode(String)` must use the language set returned by its `Lang` instance.
   - Singleton language sets and non-singleton/ANY language sets select different rule maps through `Rule.getInstanceMap`.

3. **Regression output alternatives**
   - The known problematic input should produce precisely the corrected alternatives.
   - In particular, the unexpected `vntsn` alternative must not occur if the fixed behavior’s intended output excludes it.
   - Output ordering matters because the failure compares complete strings.

4. **Compatibility with the original version**
   - The named triggering test indicates the expected behavior is compatibility-oriented, likely against a previously established Beider–Morse implementation or golden output set.
   - The complete compatibility data is not supplied, so it cannot be reconstructed safely.

5. **Resource parser and rule behavior only where connected to modified resources**
   - The target includes `Lang`, `PhoneticEngine`, `Rule`, and language resources, but the exact fixed changes are not included.
   - Tests should not assume that every changed source has an independent externally observable Codec-14 behavior without the patch or full test oracle.

---

## 9. Missing context needed for compilable, meaningful, and reliable regression tests

The following is missing or incomplete:

1. **Complete contents of the modified resource files**
   - `ash_lang.txt`
   - `gen_lang.txt`
   - `sep_lang.txt`

   They are represented as blank artifacts in the prompt. Their actual content is necessary because `Lang` loads them at class initialization and they may be the direct cause of Codec-14.

2. **The triggering regression test source**
   - `org.apache.commons.codec.language.bm.PhoneticEngineRegressionTest::testCompatibilityWithOriginalVersion`
   - Specifically needed:
     - The input that generated the failure.
     - The full expected output.
     - Engine configuration (`NameType`, `RuleType`, `concat`, and potentially `maxPhonemes`).
     - Any language-set inputs or test fixtures.

3. **Supporting production classes**
   - `Languages`
   - `NameType`
   - `RuleType`
   - `ResourceConstants`

   Their APIs are referenced, but their exact constants, enum values, language-set semantics, resource mappings, and string representations are absent.

4. **Complete Beider–Morse resource set**
   - `Rule` statically loads resources for every `NameType`, every `RuleType`, and every language listed in `Languages`.
   - Without these resources, class initialization and integration tests may fail before assertions run.

5. **Build configuration details if a test must be compiled independently**
   - Maven is specified, but no `pom.xml`, source roots, test roots, compiler target, or dependency configuration is supplied.
   - The standard Maven layout can be presumed only if it is present in the actual project checkout; it cannot be inferred from this prompt alone.

6. **Reliable corrected Codec-14 oracle**
   - The failure excerpt confirms that `|vntsn` is unwanted in one omitted test case, but the excerpt truncates both strings and omits the input.
   - It is insufficient to safely invent a regression test input or complete expected phonetic encoding.

## Conclusion

Compilable unit tests can be designed now for several source-defined behaviors, particularly:

- `PhoneticEngine` constructor validation and getters.
- `Rule.patternAndContextMatches`.
- `Rule.Phoneme` behavior.
- Known exception paths.
- Direct `Lang` parser behavior if test resources are supplied.

However, a meaningful and reliable **Codec-14 regression test** cannot yet be generated from the supplied information because the triggering input, full expected output, relevant resource contents, and supporting language/rule context are missing.