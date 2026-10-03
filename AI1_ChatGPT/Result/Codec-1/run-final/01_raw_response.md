## Scope analyzed

Only the supplied source for:

- `org.apache.commons.codec.language.Caverphone`
- `org.apache.commons.codec.language.Metaphone`
- `org.apache.commons.codec.language.SoundexUtils`

and the supplied Codec-1 / CODEC-65 context were analyzed. No production code changes or JUnit test class are generated here.

---

## 1. Public methods that should be tested

### `Caverphone`

Public API:

| Method | Notes |
|---|---|
| `Caverphone()` | Public no-argument constructor. |
| `String caverphone(String txt)` | Primary Caverphone 2.0 encoding operation. |
| `Object encode(Object pObject) throws EncoderException` | `StringEncoder` compatibility overload; only accepts `String` objects. |
| `String encode(String pString)` | Delegates to `caverphone`. |
| `boolean isCaverphoneEqual(String str1, String str2)` | Compares the generated Caverphone codes. |

### `Metaphone`

Public API:

| Method | Notes |
|---|---|
| `Metaphone()` | Public no-argument constructor. |
| `String metaphone(String txt)` | Primary Metaphone encoding operation. |
| `Object encode(Object pObject) throws EncoderException` | `StringEncoder` compatibility overload; only accepts `String` objects. |
| `String encode(String pString)` | Delegates to `metaphone`. |
| `boolean isMetaphoneEqual(String str1, String str2)` | Compares generated Metaphone codes. |
| `int getMaxCodeLen()` | Returns configurable output-length limit. |
| `void setMaxCodeLen(int maxCodeLen)` | Changes output-length limit; no validation is performed. |

### `SoundexUtils`

`SoundexUtils` is package-private (`final class SoundexUtils`), so it has no public API. Its package-visible static methods are testable only from a test in package `org.apache.commons.codec.language`:

| Method | Notes |
|---|---|
| `static String clean(String str)` | Removes non-letters and uppercases the retained letters. |
| `static int difference(StringEncoder encoder, String s1, String s2) throws EncoderException` | Encodes both values and compares the encoded strings. |
| `static int differenceEncoded(String es1, String es2)` | Counts matching characters at identical positions. |

---

## 2. Input types and valid input ranges

### Caverphone inputs

| API | Accepted compile-time input | Relevant runtime inputs |
|---|---|---|
| `caverphone` / `encode(String)` | `String` | `null`, empty, ASCII words, mixed case, punctuation, digits, Unicode letters, whitespace |
| `encode(Object)` | `Object` | `String`; all non-`String` objects; `null` |
| `isCaverphoneEqual` | Two `String` values | Each may independently be `null`, empty, or arbitrary text |

The implementation explicitly defines behavior for `null` and empty strings in `caverphone`: both return a ten-character string of `'1'`.

The algorithm then retains only `[a-z]`. Therefore, after lowercasing, non-ASCII letters are removed by the supplied implementation.

### Metaphone inputs

| API | Accepted compile-time input | Relevant runtime inputs |
|---|---|---|
| `metaphone` / `encode(String)` | `String` | `null`, empty, one character, ASCII words, mixed case, punctuation, digits, Unicode |
| `encode(Object)` | `Object` | `String`; all non-`String` objects; `null` |
| `isMetaphoneEqual` | Two `String` values | Each may independently be `null`, empty, or arbitrary text |
| `setMaxCodeLen` | `int` | Negative, zero, normal positive values, and large values |

The Javadoc says the intended input is “a single ASCII word” containing only `A-Z`, without punctuation or numbers. However, the implementation does not validate that constraint. Consequently:

- Invalid/non-ASCII/punctuation input is accepted rather than rejected.
- Its exact encoding behavior is implementation-defined by the current code path and Java character case conversion.
- Tests should distinguish documented valid ASCII input from robustness behavior for unsupported input.

### SoundexUtils inputs

| Method | Input types | Relevant values |
|---|---|---|
| `clean` | `String` | `null`, empty, only letters, only non-letters, mixed text, Unicode letters, locale-sensitive letters |
| `difference` | `StringEncoder`, `String`, `String` | Valid encoder, encoder that throws `EncoderException`, `null` encoder, `null` values |
| `differenceEncoded` | `String`, `String` | `null`, empty, unequal lengths, equal/unequal characters |

`differenceEncoded` explicitly returns `0` when either encoded string is `null`.

---

## 3. Conditions and reachable branches

## `Caverphone.caverphone`

Important reachable conditions include:

1. **Null or empty input**
   - `txt == null || txt.length() == 0`
   - Returns `"1111111111"`.

2. **Lowercasing**
   - Current code uses `txt.toLowerCase()` with no explicit locale.
   - This is directly relevant to CODEC-65.

3. **Removal of characters outside lowercase ASCII `a-z`**
   - `replaceAll("[^a-z]", "")`.
   - Inputs containing punctuation, digits, whitespace, or non-ASCII letters may become shorter or empty.

4. **Final `e` removal**
   - Applies only when the cleaned/lowercased input ends in lowercase `e`.

5. **Beginning-of-word replacements**
   - `cough`, `rough`, `tough`, `enough`, `trough`, `gn`, and `mb`.

6. **Internal phonetic replacement rules**
   - Includes special handling for sequences such as `cq`, `ci`, `ce`, `cy`, `tch`, `dg`, `tio`, `tia`, `ph`, `sh`, `gh`, and vowel-related patterns.
   - Includes repeated consonant collapsing via patterns such as `s+`, `t+`, `p+`, etc.

7. **Position-specific handling**
   - Initial vowels.
   - Initial `y`.
   - Initial `h`.
   - Terminal `w`, `r`, `l`.
   - `w3`, `wh3`, `r3`, and `l3`.

8. **Removal and final padding**
   - All `'2'` markers removed.
   - Final `'3'` converted to `A`; remaining `'3'` markers removed.
   - Pads with ten `'1'` characters.
   - Always returns the first ten characters.

9. **Output invariant**
   - For every non-throwing call to `caverphone`, the returned code has length 10.

### `Caverphone.encode(Object)`

Branches:

- `pObject instanceof String`: delegates to `caverphone`.
- Any non-`String`, including `null`: throws `EncoderException`.

Important overload distinction:

- `encode((String) null)` invokes `encode(String)` and returns `"1111111111"`.
- `encode((Object) null)` invokes `encode(Object)` and throws `EncoderException`.

### `Caverphone.isCaverphoneEqual`

Branches derive from the two calls to `caverphone`:

- Equal encodings return `true`.
- Different encodings return `false`.
- Two null inputs compare equal because each maps to `"1111111111"`.
- A null and an empty string also compare equal under the current defined encoding behavior.

---

## `Metaphone.metaphone`

Important reachable conditions include:

1. **Null or empty input**
   - Returns `""`.

2. **Single-character input**
   - Returns `txt.toUpperCase()`.
   - This call uses the JVM default locale and is directly relevant to CODEC-65.

3. **Two-or-more-character input**
   - Converts using `txt.toUpperCase(Locale.ENGLISH)`.
   - The multi-character path is explicitly locale-independent with respect to the uppercasing operation.

4. **Initial two-character exception rules**
   - Initial `KN`, `GN`, `PN`: removes initial first letter.
   - Initial `AE`: removes `A`.
   - Initial `WR`: removes `W`.
   - Initial `WH`: changes behavior to leading `W`.
   - Initial `X`: converted to leading `S`.
   - Default: no initial preprocessing.

5. **Duplicate removal**
   - Repeated characters are skipped except repeated `C`.

6. **Character-specific algorithm branches**
   - Vowels only contribute at the initial position.
   - `B` after final `M` is silent.
   - Multiple `C` rules: `SCI/SCE/SCY`, `CIA`, `CI/CE/CY`, `SCH`, `CH`.
   - `DGE/DGI/DGY`.
   - Numerous `G` cases, including terminal `GH`, `GH` before a consonant, `GN`, `GNED`, repeated `GG`, and front-vowel behavior.
   - `H` handling based on terminal position, preceding consonants, and following vowels.
   - `K` after `C`.
   - `PH`.
   - `SH`, `SIO`, `SIA`.
   - `TIA`, `TIO`, `TCH`, `TH`.
   - `W` and `Y` only when followed by vowels.
   - `X` contributes two characters (`KS`).
   - `V`, `Q`, and `Z` substitutions.

7. **Maximum output length**
   - The loop only executes while `code.length() < getMaxCodeLen()`.
   - If encoding adds too many characters, the result is truncated to `maxCodeLen`.
   - Default value is 4.
   - A value of 0 or negative causes the loop not to execute, yielding `""` for non-null/non-empty inputs.
   - No validation rejects negative values.

8. **Potential unsupported-character behavior**
   - The implementation processes the uppercased text but has no `default` branch in the per-character `switch`.
   - Unsupported characters normally add no code character and are skipped.
   - Exact behavior for Unicode case mappings should not be treated as an algorithmic contract beyond the documented ASCII domain.

### `Metaphone.encode(Object)`

Branches mirror Caverphone:

- A `String` delegates to `metaphone`.
- A non-`String`, including `null`, throws `EncoderException`.

Overload distinction:

- `encode((String) null)` returns `""`.
- `encode((Object) null)` throws `EncoderException`.

### `Metaphone.isMetaphoneEqual`

- True when output strings are equal.
- False when they differ.
- Two null values compare equal because both encode to `""`.
- Null and empty also compare equal in the current implementation.

### `getMaxCodeLen` and `setMaxCodeLen`

- Default after construction: `4`.
- Setter stores exactly the supplied integer.
- No range checking or exceptions are implemented.
- The value affects subsequent calls to `metaphone`.

---

## `SoundexUtils.clean`

Reachable branches:

1. **Null or empty**
   - Returns the input reference/value unchanged (`null` or `""`).

2. **All input characters are letters**
   - `count == len`.
   - Returns `str.toUpperCase()` using the JVM default locale.
   - This is directly relevant to CODEC-65.

3. **At least one non-letter**
   - Retains only characters for which `Character.isLetter(...)` is true.
   - Returns the retained characters uppercased using `Locale.ENGLISH`.

4. **No letters**
   - The partial-cleaning branch is taken (`count == 0` and normally `len > 0`).
   - Returns the empty string.

Important inconsistency in the supplied source:

- All-letter input: `toUpperCase()` uses the default locale.
- Mixed input after removing at least one non-letter: `toUpperCase(Locale.ENGLISH)` is used.

This means the locale behavior differs solely according to whether every input character is a letter.

---

## `SoundexUtils.difference`

Reachable branches:

1. Calls `encoder.encode(s1)`.
2. Calls `encoder.encode(s2)` if the first call completed normally.
3. Passes both encoded results to `differenceEncoded`.
4. Propagates `EncoderException` from either encode call.
5. A `null` encoder causes `NullPointerException`; no null guard exists.
6. The behavior for null `s1` or `s2` depends on the supplied `StringEncoder`; SoundexUtils does not prevalidate them.

A controllable test double implementing `StringEncoder` would be needed to isolate and test this method’s delegation and exception propagation without relying on unspecified behavior of external encoders.

---

## `SoundexUtils.differenceEncoded`

Reachable branches:

1. **Either value null**
   - Returns `0`.

2. **Both non-null**
   - Iterates from index `0` up to the shorter length.
   - Counts character matches at the same index.
   - Does not compare characters beyond the shorter string.
   - Handles empty strings naturally, returning `0` unless there are no positions to compare.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

## Caverphone

### Normal cases

- Valid lowercase ASCII word.
- Equivalent mixed-case ASCII word, subject to locale independence requirements.
- Known Caverphone examples from the linked Caverphone 2.0 specification, if that external specification is permitted as the oracle.
- `encode(String)` producing the same result as `caverphone`.
- `encode(Object)` with a `String` producing the same result as `caverphone`.
- `isCaverphoneEqual` for known equivalent and non-equivalent input pairs.

### Boundary cases

- `null`.
- Empty string.
- One-character vowels and consonants.
- Input that becomes empty after non-`[a-z]` removal.
- Input whose transformed code is shorter than ten characters and must be padded.
- Input whose transformed code exceeds ten characters and must be truncated.
- Words ending in `e`, `w`, `r`, or `l`.
- Prefixes covered by start rules.

### Invalid/unsupported cases

- Digits, punctuation, whitespace.
- Non-ASCII letters.
- Locale-sensitive letters, especially `"I"` under Turkish locale.
- These should be tested as robustness/bug-regression behavior, not assumed to have a complete general Unicode Caverphone contract.

### Exceptional cases

- `encode(Object)` passed:
  - `null`
  - a non-String object
  - another `CharSequence` such as `StringBuilder`
- Expected exception type from the supplied source: `EncoderException`.

---

## Metaphone

### Normal cases

- Valid ASCII words following documented input constraints.
- Standard Metaphone branch examples for initial rules and character rules.
- `encode(String)` and `encode(Object)` delegation.
- Equal/non-equal phonetic values.
- Default maximum code length of four.
- Larger positive maximum code lengths.

### Boundary cases

- `null`.
- Empty string.
- Single-character input.
- Two-character input, especially initial-rule prefixes.
- Outputs shorter than max length.
- Output that would exceed max length, including `X`, which may append two output characters.
- `maxCodeLen = 0`.
- Negative `maxCodeLen`.
- `maxCodeLen = 1`.
- A large positive value.

### Invalid/unsupported cases

- Punctuation, digits, whitespace, and non-ASCII input.
- Since the Javadoc declares ASCII words as the supported format, reliable semantic expected codes for arbitrary invalid input cannot be inferred from the documentation alone.
- Locale-sensitive `"i"`/`"I"` must be covered because it is explicitly implicated in the bug report.

### Exceptional cases

- `encode(Object)` with null or non-String inputs must throw `EncoderException`.
- No exception is specified or implemented for invalid `maxCodeLen`; tests should not expect validation.

---

## SoundexUtils

### Normal cases

- `clean` with ordinary ASCII alphabetic input.
- `clean` with mixed letters and separators.
- `differenceEncoded` with equal strings, unequal strings, and partial positional matches.
- `difference` using a known encoder or test double that returns controlled values.

### Boundary cases

- `clean(null)`, `clean("")`, single character, no letters, and Unicode letter input.
- `differenceEncoded` with either/both null, empty values, and unequal-length strings.
- `difference` with encoders returning null encoded values, if a test double does so.

### Invalid and exceptional cases

- `difference(null, s1, s2)` produces `NullPointerException` under this implementation.
- An encoder throwing `EncoderException` must have that exception propagated by `difference`.
- `clean` has no explicit exceptional path for ordinary `String` inputs.

---

## 5. Required constructors, dependencies, and external objects

### Required constructors

No constructor arguments or dependency injection are required:

```java
new Caverphone()
new Metaphone()
```

`SoundexUtils` cannot be instantiated because it is package-private and has no public constructor shown; its methods are static.

### Production dependencies referenced by the supplied code

- `org.apache.commons.codec.StringEncoder`
- `org.apache.commons.codec.EncoderException`
- Java standard library:
  - `java.util.Locale`
  - `String`, `StringBuffer`
  - `Character`
  - regular-expression support through `String.replaceAll`

### Test-specific dependency needs

- JUnit 4.12 and Hamcrest 1.3, per supplied context.
- Tests for `SoundexUtils` must be declared in:
  ```java
  package org.apache.commons.codec.language;
  ```
  because the class and methods are package-private.
- To directly test `SoundexUtils.difference`, a concrete `StringEncoder` is needed:
  - Existing project encoders could be used if their source/API is available to the test compilation environment.
  - A minimal test-local implementation is preferable for delegation and exception-propagation tests, because it makes the encoded outputs and failure behavior explicit.
- Locale regression tests must save and restore `Locale.getDefault()` in a `try/finally` block to avoid contaminating other tests.

---

## 6. JUnit version and build tool

Supplied project context states:

- **JUnit:** `junit-4.12-hamcrest-1.3.jar`
- **Build tool:** Maven

The supplied context does not include:

- `pom.xml`
- Maven Surefire/Failsafe plugin configuration
- Java source/target level
- Existing test source layout
- Any Maven profile or Defects4J-specific execution command

Therefore, JUnit 4 test style is supported by the provided information, but exact build-plugin behavior and project compiler compatibility cannot be verified from the prompt alone.

---

## 7. Available test oracles

### Strongest supplied oracle: bug-report failure output

The CODEC-65 report gives concrete expected versus actual results under Turkish locale:

| Affected test | Locale-sensitive expected behavior | Current faulty behavior reported |
|---|---|---|
| `CaverphoneTest::testLocaleIndependence` | For Turkish locale, expected `"A111111111"` | Actual `"1111111111"` |
| `MetaphoneTest::testLocaleIndependence` | For Turkish locale, expected `"I"` | Actual `"İ"` |
| `SoundexTest::testLocaleIndependence` | Should not fail due to unmapped `İ` | Reported failure: `The character is not mapped: İ` |
| `RefinedSoundexTest::testLocaleIndependence` | Should remain locale-independent; expected difference value stated as `239` in failure context | Actual behavior failed under Turkish locale |
| `DoubleMetaphoneTest::testLocaleIndependence` | Expected `"I"` | Actual `"İ"` |

For the three target classes, the directly relevant explicit expected outputs are:

- Caverphone Turkish-locale regression result: `"A111111111"`.
- Metaphone Turkish-locale single-character regression result: `"I"`.
- SoundexUtils must uppercase locale-independently so downstream Soundex/RefinedSoundex processing does not receive Turkish capital dotted `İ` for ordinary ASCII lowercase `i`.

### Javadocs in the supplied source

The source Javadocs provide these usable contract statements:

- Caverphone implements Caverphone 2.0.
- Metaphone is intended for a single ASCII word in the `A-Z` range.
- `encode(Object)` should throw `EncoderException` when its object is not a `String`.
- `SoundexUtils.differenceEncoded` returns the number of matching positional characters.
- `SoundexUtils.difference` delegates to an encoder and compares encoded strings.
- Soundex difference commonly ranges from 0–4, while refined Soundex may exceed 4.

### External specifications referenced by Javadocs

The Caverphone Javadoc links the Caverphone 2.0 specification. It could serve as a detailed expected-output oracle only if use of that external specification is within the test-generation protocol. The prompt supplies the reference but not the specification content itself.

### Existing tests

The prompt supplies only the names and failure results of triggering tests, not their source. Consequently:

- Their assertions, setup, locale management, and complete test data are unavailable.
- They cannot be copied or relied upon as a full source-level oracle.
- The shown failure messages can still be used as bug-regression expectations.

---

## 8. Bug-report behaviors that should be tested

CODEC-65 is a locale-independence bug. The relevant locale is Turkish (`"tr"`), where default-locale case conversion differs for `i`/`I`.

### Caverphone regression behavior

Current supplied implementation:

```java
txt = txt.toLowerCase();
```

Because this uses the JVM default locale, an ASCII `"I"` can become Turkish dotless lowercase `ı`. The later filter:

```java
txt.replaceAll("[^a-z]", "")
```

removes dotless `ı`, producing an empty transformed input and ultimately `"1111111111"`.

The supplied bug report specifies the intended Turkish-locale result for the relevant test:

```text
"A111111111"
```

Required regression coverage:

1. Set default locale to Turkish.
2. Encode the locale-sensitive ASCII input used by the triggering behavior (`"I"` is implied by the reported expected Caverphone code).
3. Verify the output remains the locale-neutral/English-algorithm result: `"A111111111"`.
4. Restore the original default locale.
5. Preferably compare behavior under Turkish locale to behavior under a locale such as English/root, ensuring locale independence rather than only one hardcoded case.

### Metaphone regression behavior

Current supplied implementation is partially locale-safe:

- Multi-character input uses `toUpperCase(Locale.ENGLISH)`.
- Single-character input uses `txt.toUpperCase()` with the JVM default locale.

Thus, with default Turkish locale, input `"i"` becomes `"İ"` in the one-character branch. The supplied failure says expected `"I"` but actual `"İ"`.

Required regression coverage:

1. Set default locale to Turkish.
2. Call `metaphone("i")` (the expected/actual values strongly indicate lowercase ASCII `i`).
3. Expect `"I"`.
4. Verify `encode("i")` follows the same behavior if testing the delegating overload.
5. Restore the original locale.
6. Cover a multi-character locale-sensitive case only if an expected code is independently known; the source already uses `Locale.ENGLISH` on that path.

### SoundexUtils regression behavior

Current supplied implementation has two uppercasing paths:

```java
if (count == len) {
    return str.toUpperCase(); // default locale: faulty under Turkish locale
}
return new String(chars, 0, count).toUpperCase(Locale.ENGLISH);
```

Required regression coverage should include both paths:

1. **All-letter path**
   - Under Turkish default locale, `clean("i")` should produce `"I"` for locale-independent downstream Soundex behavior, not `"İ"`.

2. **Mixed-content path**
   - Under Turkish default locale, an input such as `"i-"` already takes the explicit-English branch.
   - It should also result in `"I"`.
   - Comparing the all-letter and mixed-content cases detects the inconsistency in the supplied implementation.

3. **Downstream relevance**
   - The supplied failures name `SoundexTest` and `RefinedSoundexTest`.
   - Tests of `SoundexUtils.clean` directly target the modified root cause.
   - Integration tests against `Soundex` and `RefinedSoundex` would be valuable, but their implementations/API are not supplied in this prompt. Their reported failing test names alone do not provide enough source-level detail to safely author complete integration tests here.

### Related but non-target bug-report behavior

`DoubleMetaphoneTest::testLocaleIndependence` also fails, but `DoubleMetaphone` is not among the supplied target source classes or modified sources. No test should be generated for it unless its source/API is supplied or otherwise explicitly made available under the protocol.

---

## 9. Missing context needed for fully compilable and meaningful tests

The supplied information is sufficient to plan direct unit tests for the three shown classes, including the central locale regression behavior. However, the following information is missing for fully reliable, project-integrated test generation:

1. **Existing test source**
   - The actual source of:
     - `CaverphoneTest`
     - `MetaphoneTest`
     - `SoundexTest`
     - `RefinedSoundexTest`
     - `DoubleMetaphoneTest`
   - Needed to avoid duplicate test names, follow project conventions, and reproduce the intended locale setup precisely.

2. **`pom.xml` and Maven configuration**
   - Needed to confirm source/test Java level, dependency scopes, test naming conventions, and Maven execution configuration.

3. **Source or API details for dependent classes**
   - `StringEncoder` and `EncoderException` are referenced but not shown.
   - Their likely behavior is evident from usage, but exact package API and method signatures should be confirmed from the project source/classpath before compiling tests.
   - `Soundex` and `RefinedSoundex` are needed for meaningful end-to-end validation of their reported triggering tests.

4. **Authoritative general expected-output vectors**
   - The Caverphone Javadoc references an external Caverphone 2.0 specification, but its expected examples are not supplied.
   - The Metaphone Javadoc describes the algorithm but provides no input/output example table.
   - Without existing tests or an approved external algorithm specification, broad expected results for every phonetic transformation branch cannot be established with complete reliability.
   - Structural properties, delegation, null behavior, exception behavior, length limits, and the explicit CODEC-65 expected values are reliably testable from the supplied material.

5. **Exact intended contract for invalid/non-ASCII Metaphone input**
   - The documentation limits valid input to ASCII `A-Z`, but implementation accepts broader input without validation.
   - Therefore, tests should not invent expected phonetic codes for Unicode, punctuation, or numeric inputs beyond explicit locale-regression behavior and observed implementation-level robustness expectations.

6. **Whether fixed-version behavior may be consulted**
   - The prompt provides a fixed revision identifier but does not provide its source and explicitly restricts analysis to the supplied Source Version and Project Context.
   - The fixed revision must not be used as an oracle unless explicitly permitted.

