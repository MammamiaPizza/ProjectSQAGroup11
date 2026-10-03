## 1. Public methods that should be tested

### `DoubleMetaphone`
| Method | Purpose |
|---|---|
| `DoubleMetaphone()` | Creates an encoder with default `maxCodeLen` of 4. |
| `String doubleMetaphone(String value)` | Produces the primary Double Metaphone code. |
| `String doubleMetaphone(String value, boolean alternate)` | Produces primary (`false`) or alternate (`true`) Double Metaphone code. |
| `Object encode(Object obj) throws EncoderException` | `StringEncoder` interface implementation; accepts only `String` runtime values. |
| `String encode(String value)` | Convenience overload returning the primary encoding. |
| `boolean isDoubleMetaphoneEqual(String value1, String value2)` | Compares primary encodings. |
| `boolean isDoubleMetaphoneEqual(String value1, String value2, boolean alternate)` | Compares primary or alternate encodings. |
| `int getMaxCodeLen()` | Returns configured maximum encoding length. |
| `void setMaxCodeLen(int maxCodeLen)` | Changes maximum encoding length. |

### Public inner class: `DoubleMetaphone.DoubleMetaphoneResult`
This is a public non-static inner class and therefore is externally constructible through an outer instance. Its public behavior can be tested separately if inner-class API coverage is desired.

| Method / constructor | Purpose |
|---|---|
| `DoubleMetaphoneResult(int maxLength)` | Creates a result holder with independently supplied maximum length. |
| `append(char)` | Appends the same character to primary and alternate values. |
| `append(char, char)` | Appends distinct primary and alternate characters. |
| `appendPrimary(char)` / `appendAlternate(char)` | Appends to one side, subject to maximum length. |
| `append(String)` | Appends the same string to both values, truncating as needed. |
| `append(String, String)` | Appends distinct strings to primary and alternate values. |
| `appendPrimary(String)` / `appendAlternate(String)` | Appends one string, truncating to the configured maximum. |
| `getPrimary()` / `getAlternate()` | Returns accumulated values. |
| `isComplete()` | Indicates whether both values have reached the configured maximum length. |

`charAt` and `contains` are `protected`, not public. The many `handle*`, `condition*`, and input-cleaning methods are private and should ordinarily be exercised through the public encoding methods rather than called directly.

---

## 2. Input types and valid input ranges

### String encoding methods
Applies to `doubleMetaphone`, `encode(String)`, and equality methods:

- Input type: `String`.
- `null` is accepted by `doubleMetaphone` and `encode(String)` and produces `null`.
- Empty strings and strings containing only whitespace are converted to `null` by `cleanInput`, so these methods return `null`.
- Nonblank input is:
  1. trimmed with `String.trim()`;
  2. uppercased using `Locale.ENGLISH`;
  3. processed character by character.
- There is no documented character-set restriction. The implementation has explicit handling for ASCII letters plus:
  - `\u00C7` (`Ç`) → `S`;
  - `\u00D1` (`Ñ`) → `N`.
- Other characters are reachable through the `default` switch branch and are skipped.

### `alternate`
- Input type: primitive `boolean`.
- `false`: return primary code.
- `true`: return alternate code.

### `encode(Object)`
- Input type: any `Object`.
- Valid runtime type: `String` only.
- Every non-`String` object, including `null`, causes `EncoderException`.

Important overload distinction:

```java
encoder.encode((String) null);  // calls encode(String), returns null
encoder.encode((Object) null);  // calls encode(Object), throws EncoderException
```

### Equality methods
- Input types: two `String` values, plus optional `boolean alternate`.
- For non-null, nonblank values, normal comparison behavior is defined by equality of generated encodings.
- `null` behavior is not safely supported by the current implementation: it invokes `.equals(...)` on the first encoding result. Since `doubleMetaphone(null)` returns `null`, a null/blank first argument leads to `NullPointerException`.

### Maximum code length
- Input type: `int`.
- Default: `4`.
- No validation is implemented.
- Therefore all `int` values are accepted by `setMaxCodeLen`, including `0` and negative values.
- Positive values are the meaningful normal range.
- Zero and negative values are important boundary/robustness cases because their behavior differs and is not documented.

### Inner result maximum length
- Input type: `int`.
- No validation.
- Positive values are meaningful normal inputs.
- Zero and negative values may produce empty results or exceptions depending on which append overload is invoked.

---

## 3. Conditions and reachable branches

## Top-level processing branches

`doubleMetaphone(String, boolean)` has these main reachable decisions:

1. Input cleaning:
   - `null` input;
   - empty after `trim()`;
   - nonempty input, converted to uppercase English locale.
2. Slavo-Germanic detection:
   - true if input contains `W`, `K`, `CZ`, or `WITZ`;
   - false otherwise.
3. Silent-start detection:
   - `GN`, `KN`, `PN`, `WR`, or `PS` starts processing from index 1;
   - all others start at index 0.
4. Processing stops when:
   - both primary and alternate codes have reached `maxCodeLen`; or
   - the input is exhausted.
5. Result selection:
   - primary if `alternate == false`;
   - alternate if `alternate == true`.

## Character-dispatch branches

The public encoder can reach branches for:

- vowels: `A`, `E`, `I`, `O`, `U`, `Y`;
- consonants: `B`, `C`, `D`, `F`, `G`, `H`, `J`, `K`, `L`, `M`, `N`, `P`, `Q`, `R`, `S`, `T`, `V`, `W`, `X`, `Z`;
- accented characters: `Ç`, `Ñ`;
- unhandled characters: skipped.

The complex behavior is concentrated in:

| Letter/area | Reachable decision families |
|---|---|
| `C` | `CHIA`; `CAESAR`; `CH`; `CZ`/`WICZ`; `CIA`; `CC`; `CK`/`CG`/`CQ`; soft `CI`/`CE`/`CY`; general hard-C and doubled consonants. |
| `CH` | `CHAE`; Greek-root patterns; Germanic/Greek patterns; `MC`; initial vs. non-initial forms. |
| `D` | `DG` before `I/E/Y`; other `DG`; `DT`/`DD`; ordinary `D`. |
| `G` | `GH`; `GN`; `GLI`; initial `GE/GI`-related patterns; `GER/GY`; soft-G versus hard-G; Germanic prefixes; `IER`; doubled `GG`. |
| `GH` | Consonant-preceded; initial `GH`; silent “Parker’s rule”; `-UGH`/`LAUGH`-like `F`; other forms. |
| `J` | `JOSE`; `SAN `; initial J; vowel-adjacent Spanish-like behavior; terminal J; doubled J. |
| `L` | single L; double L; special terminal `-ILLO`, `-ILLA`, `-ALLE`, `-AS/-OS/-A/-O` patterns. |
| `M` | doubled M; silent/combined `UMB` patterns; ordinary M. |
| `R` | ordinary R; terminal French-like `-IER` behavior. |
| `S` | `ISL`/`YSL`; `SUGAR`; `SH` Germanic/non-Germanic; `SIO/SIA/SIAN`; initial `SM/SN/SL/SW`; `SZ`; `SC`; terminal French-like `AI/OI + S`; repeated `S/Z`. |
| `SC` | `SCH` Dutch/non-Dutch variants; `SCI/SCE/SCY`; other `SC`. |
| `T` | `TION`; `TIA`; `TCH`; `TH`/`TTH`, including `THOMAS`, `THAMES`, `VAN`, `VON`, `SCH`; ordinary/repeated T/D. |
| `W` | `WR`; initial vowel/`WH`; terminal-after-vowel; `EWSKI/EWSKY/OWSKI/OWSKY`; `SCH`; `WICZ/WITZ`; ignored W. |
| `X` | initial X; terminal French exceptions (`IAU`, `EAU`, `AU`, `OU`); followed by `C/X`; standard `KS`. |
| `Z` | `ZH`; `ZO/ZI/ZA`; Slavo-Germanic behavior; doubled Z; ordinary Z. |

## Result-holder branches

`DoubleMetaphoneResult` has independent branches for:

- char append while below max length versus at/over max length;
- string append that entirely fits versus truncates;
- primary and alternate state differences;
- `isComplete()` false before both values reach max length, true after both reach it;
- zero and negative configured maximum lengths.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

## Normal cases

1. Default construction gives `getMaxCodeLen() == 4`.
2. Standard nonblank words generate a primary code.
3. Calling `doubleMetaphone(value, false)` should match `doubleMetaphone(value)`.
4. `encode(String)` should delegate to primary encoding.
5. `encode(Object)` with a `String` should return the same primary encoding as `doubleMetaphone`.
6. Case differences and leading/trailing whitespace should be normalized through English-locale uppercase conversion and trimming.
7. A configured positive maximum length should cap both primary and alternate outputs.
8. Equality methods should compare generated codes, not raw input text.
9. Alternate encoding can legitimately differ from primary encoding for some inputs.

## Boundary cases

1. One-character nonblank input.
2. Exactly `maxCodeLen` output characters.
3. Inputs whose natural code would exceed `maxCodeLen`.
4. `maxCodeLen == 1`.
5. `maxCodeLen == 0`.
6. Negative `maxCodeLen`.
7. Characters at the beginning and end of words, especially for handlers that inspect preceding/following positions.
8. Silent prefixes of exactly two characters: `GN`, `KN`, `PN`, `WR`, `PS`.
9. Inputs containing only unrecognized characters.
10. `Ç` and `Ñ`.
11. Locale-sensitive lowercase input, insofar as `Locale.ENGLISH` normalization defines the behavior.

## Invalid/type cases

1. `encode(Object)` with:
   - `null`;
   - numeric object;
   - arbitrary object;
   - character object;
   - `StringBuilder` or another non-`String` `CharSequence`.
   
   Each should throw `EncoderException` according to the method contract.

2. `setMaxCodeLen` accepts invalid/unsupported values without validation. Tests may characterize observed behavior, but the supplied API documentation does **not** state a required policy for zero or negative values.

## Null and blank cases

| Call | Observable behavior in supplied source |
|---|---|
| `doubleMetaphone(null)` | Returns `null`. |
| `doubleMetaphone("")` | Returns `null`. |
| `doubleMetaphone(" \t\n ")` | Returns `null`. |
| `encode((String) null)` | Returns `null`. |
| `encode((Object) null)` | Throws `EncoderException`. |
| `isDoubleMetaphoneEqual(null, value)` | Throws `NullPointerException`, because the left encoding is `null` and `.equals` is called on it. |
| `isDoubleMetaphoneEqual("", value)` | Also throws `NullPointerException`, because blank input cleans to `null`. |
| `isDoubleMetaphoneEqual(value, null)` where first encoding is non-null | Returns `false`, since `nonNullString.equals(null)` is false. |
| `isDoubleMetaphoneEqual(null, null)` | Throws `NullPointerException`. |

The asymmetric null behavior of equality is observable from this version, but it is not documented as an intentional API contract. It should be tested only as characterization/robustness behavior unless an external specification establishes the intended behavior.

## Exceptional cases in `DoubleMetaphoneResult`

For a negative `maxLength`:

- Char append methods do not append because current length `0` is not less than a negative value.
- `isComplete()` is immediately true because `0 >= negativeMaxLength`.
- `appendPrimary(String)` and `appendAlternate(String)` can throw `StringIndexOutOfBoundsException` when they attempt `substring(0, addChars)` with a negative `addChars`.

This behavior follows directly from the source and is not documented as supported behavior.

---

## 5. Required constructors, dependencies, and external objects

## Required construction

For ordinary target-class tests:

```java
DoubleMetaphone encoder = new DoubleMetaphone();
```

No constructor arguments or injected dependencies are required.

For direct testing of the public non-static inner result class:

```java
DoubleMetaphone outer = new DoubleMetaphone();
DoubleMetaphone.DoubleMetaphoneResult result =
    outer.new DoubleMetaphoneResult(maxLength);
```

## Dependencies visible in the supplied source

- `org.apache.commons.codec.StringEncoder`
  - Implemented by `DoubleMetaphone`.
- `org.apache.commons.codec.EncoderException`
  - Thrown by `encode(Object)` for non-`String` values.
- Java platform classes:
  - `java.util.Locale`;
  - `String`;
  - `StringBuffer`;
  - `Character`.

No I/O, filesystem, network, clocks, databases, mocks, or third-party services are involved.

---

## 6. JUnit version and build tool

- **JUnit:** `junit-4.12-hamcrest-1.3.jar`
  - Tests should use JUnit 4 conventions when generation is later requested, such as `org.junit.Test`, `org.junit.Assert`, and optionally `@Test(expected = ...)`.
- **Build tool:** Maven.

The supplied context does not include the project `pom.xml`, source roots, Maven test command, Java source/target level, or existing dependency declarations. Maven and JUnit versions are explicitly provided, but build configuration details beyond that are not.

---

## 7. Available test oracle

The supplied material provides these test oracles:

1. **Javadoc/API comments in `DoubleMetaphone`**
   - Encoding methods return encoded strings.
   - `encode(Object)` must throw `EncoderException` when its argument is not a `String`.
   - Equality methods compare encoded string values.
   - Default `maxCodeLen` is documented as 4.
   - `charAt` returns `Character.MIN_VALUE` outside bounds.
   - `cleanInput` semantics are directly visible in the source.

2. **Direct source behavior**
   - Useful for identifying branches and observable behavior.
   - It must not be treated as proof that all implementation results are correct, especially for the reported defect.

3. **Bug report / triggering failure**
   - Bug: `CODEC-84`.
   - Triggering test: `DoubleMetaphone2Test::testDoubleMetaphoneAlternate`.
   - Explicit expected result:
     - Input: `"Angier"`
     - Alternate code expected: `"ANJR"`
     - Faulty source result: `"ANKR"`

4. **Fixed revision identifier**
   - `a5dfe5cbc95d7f3ce0b4829756690c2cb8439f4c`
   - Only the identifier is provided; its source diff/content is not supplied and must not be inferred.

No complete existing test source, formal Double Metaphone reference corpus, or detailed JIRA description is supplied. Therefore, expected code strings for additional algorithm examples cannot be established reliably from the supplied information alone unless they follow directly from an explicit documented rule or are intended only as characterization tests.

---

## 8. Behaviors related to Codec-3 / CODEC-84 that should be tested

The directly reported regression behavior is:

```java
new DoubleMetaphone().doubleMetaphone("Angier", true)
```

Expected result according to the supplied bug report:

```java
"ANJR"
```

The supplied defective source instead returns:

```java
"ANKR"
```

### Why this path is relevant

After normalization, `"Angier"` becomes `"ANGIER"`:

- `A` contributes `A`;
- `N` contributes `N`;
- the `G` is followed by `I`;
- the `G` handler has a special `IER` decision:
  ```java
  else if (contains(value, index + 1, 4, "IER")) {
      result.append('J');
  } else {
      result.append('J', 'K');
  }
  ```
- In `"ANGIER"`, the suffix starting after `G` is `"IER"` of length 3. The source checks a length of 4, so that condition is unreachable for this exact three-character suffix.
- Consequently, the current code takes `result.append('J', 'K')`, producing alternate `K` where the bug report requires alternate `J`.

### Required bug-focused test assertions

At minimum, a regression test should verify:

1. `doubleMetaphone("Angier", true)` is exactly `"ANJR"`.
2. The test must request the alternate code (`true`), because the reported failure is specifically alternate behavior.
3. It is useful to additionally verify the primary result only if a reliable expected primary value is established. The supplied source and path indicate primary `"ANJR"` as well, but the bug report explicitly provides only the alternate assertion. The alternate assertion alone is the required defect oracle.
4. It is useful to ensure the behavior is available through `encode(String)` only for primary codes, but `encode(String)` cannot select alternate mode and therefore does not directly exercise the defect.

A focused regression test with the supplied expected alternate value will fail against Codec-3b and should pass against the stated fixed revision.

---

## 9. Missing context required for fully reliable, broad tests

The supplied information is sufficient to create a compilable JUnit 4 regression test for CODEC-84 and basic API-contract tests. It is insufficient to derive a comprehensive suite of exact expected Double Metaphone values across all algorithm branches.

Missing context includes:

1. **Existing test source**
   - The contents of `DoubleMetaphone2Test`, especially `testDoubleMetaphoneAlternate`.
   - Existing `DoubleMetaphoneTest` or reference-corpus tests.
   - These would provide established expected encodings and test style.

2. **Detailed bug report content**
   - Only the summary and one failing assertion are supplied.
   - The full CODEC-84 issue description could clarify intended scope, related input cases, and expected treatment of `IER`.

3. **Fixed-source diff or fixed implementation**
   - The fixed revision hash is known, but no diff is supplied.
   - Without its contents, tests should not assume the precise code change or infer additional corrected cases beyond `"Angier"`.

4. **Formal algorithm oracle/reference data**
   - A canonical Double Metaphone specification, approved word/code mapping, or project-maintained fixtures would be needed to assert exact outputs for all the numerous `C`, `G`, `S`, `T`, `W`, and related branches without relying on the potentially defective implementation.

5. **Maximum-length validation policy**
   - Documentation does not define whether `maxCodeLen <= 0` should be accepted, rejected, or normalized.
   - Tests can document current behavior, but cannot assert a product-level expected behavior for those values from the supplied material.

6. **Null-equality contract**
   - The equality method documentation does not state null handling.
   - The source produces asymmetric null behavior, but it is not possible to determine whether that is intended or defective from the supplied context.

7. **Maven project configuration**
   - The Maven build tool and JUnit version are known, but the supplied prompt does not contain the `pom.xml`, source encoding, Java version, or test package conventions. This is not a blocker for a conventional JUnit 4 test in the target package, but it limits certainty about project-specific compilation settings.