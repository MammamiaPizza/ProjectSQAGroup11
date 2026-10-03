## 1. Public API surface that should be tested

`org.apache.commons.codec.language.Soundex` exposes the following public constructors, methods, and public constants/instances:

### Constructors
1. `Soundex()`
   - Uses the built-in US English mapping.

2. `Soundex(char[] mapping)`
   - Uses a defensive copy of the provided mapping array.

3. `Soundex(String mapping)`
   - Converts the provided mapping string to an internal character array.

### Public methods
1. `int difference(String s1, String s2) throws EncoderException`
   - Delegates comparison behavior to `SoundexUtils.difference(...)`.

2. `Object encode(Object obj) throws EncoderException`
   - Encoder-interface-compatible method.
   - Only accepts a non-null `String` instance.

3. `String encode(String str)`
   - Delegates to `soundex(str)`.

4. `String soundex(String str)`
   - Main Soundex encoding operation.

5. `int getMaxLength()` — deprecated
   - Returns the stored `maxLength` field.

6. `void setMaxLength(int maxLength)` — deprecated
   - Stores the supplied value in `maxLength`.
   - The class documentation states that this field is not actually used for encoding length.

### Public fields
1. `public static final String US_ENGLISH_MAPPING_STRING`
2. `public static final Soundex US_ENGLISH`

These may be tested as API constants/instances if project conventions cover public constants, though they are not the primary behavioral target.

---

## 2. Input types and valid input ranges

### String inputs
Methods accepting `String`:
- `difference(String, String)`
- `encode(String)`
- `soundex(String)`

Known behavior from the supplied class:

| Input category | `soundex` / `encode(String)` behavior |
|---|---|
| `null` | Returns `null` |
| String that becomes empty after `SoundexUtils.clean` | Returns that empty cleaned string |
| Non-empty cleaned String | Produces a four-character code, unless mapping fails |
| Character not supported by the active mapping | May throw `IllegalArgumentException` through `map(...)` |

The exact behavior of `SoundexUtils.clean(str)` is not supplied. Therefore, reliable test expectations for:
- lowercase input,
- whitespace,
- punctuation,
- digits,
- accented characters,
- non-Latin characters,

cannot be fully determined from the supplied target-class source alone. The class passes the input through `SoundexUtils.clean`, but its cleaning rules are not present in the prompt.

### `Object` input
Method:
- `encode(Object obj)`

Valid input:
- A non-null object whose runtime type is `String`.

Invalid inputs:
- `null`
- Any object that is not a `String`, such as `Integer`, `Character`, `StringBuilder`, or arbitrary `Object`.

Expected result for invalid `Object` input:
- `EncoderException` with the implementation’s specified failure path.

Important overload distinction:

```java
soundex.encode((String) null); // calls encode(String), returns null
soundex.encode((Object) null); // calls encode(Object), throws EncoderException
soundex.encode(null);          // resolves to encode(String), returns null
```

### Custom mapping inputs

#### `Soundex(char[] mapping)`
- Accepts a `char[]`.
- The constructor copies the supplied array.
- `null` causes `NullPointerException` because `mapping.length` is accessed.
- Empty and short arrays are accepted by the constructor but may make many alphabetic characters unmappable during encoding.
- No mapping content validation is performed.
- Mapping character values are not restricted by the constructor. However, encoding logic gives special meaning to `'0'` and otherwise emits mapping characters as code values.

#### `Soundex(String mapping)`
- Accepts a `String`.
- `null` causes `NullPointerException` because `mapping.toCharArray()` is called.
- Empty and short strings are accepted but may later cause `IllegalArgumentException` for unmapped letters.
- No validation requires a 26-character mapping.

#### Effective mapping range
`map(char ch)` computes:

```java
int index = ch - 'A';
```

A character is mapped only when:

```java
0 <= index < soundexMapping.length
```

For the default 26-character mapping, this means the cleaned character must be within `'A'` through `'Z'`.

### `maxLength` input
Method:
- `setMaxLength(int maxLength)`

Input range:
- Any Java `int`, including negative values, `0`, and `Integer.MIN_VALUE`/`Integer.MAX_VALUE`.

No validation is present. The stored value can be retrieved through `getMaxLength()`, but the supplied class documentation explicitly says it is not used by the encoding algorithm.

---

## 3. Conditions and reachable branches

### `encode(Object obj)`

Branches:

1. `obj` is not a `String`, including `null`
   - Throws `EncoderException`.

2. `obj` is a `String`
   - Casts it and calls `soundex((String) obj)`.

Within the valid `String` branch, the eventual `soundex` null branch is only reachable if a runtime `String` value is supplied; a null reference is not an `instanceof String`, so `encode((Object) null)` does not reach `soundex`.

---

### `encode(String str)`

Single delegation path:

```java
return soundex(str);
```

Its behavior is entirely determined by `soundex`.

---

### `soundex(String str)`

Reachable branches:

1. `str == null`
   - Returns `null`.

2. `str != null`
   - Calls `SoundexUtils.clean(str)`.

3. Cleaned string has length `0`
   - Returns the cleaned empty string.

4. Cleaned string is non-empty
   - Initializes a four-character output filled with `'0'`.
   - Copies the first cleaned character to position zero.
   - Determines the first character’s mapping.
   - Iterates through later cleaned characters while:
     - there are remaining input characters, and
     - fewer than four output positions have been filled.

5. For each later character:
   - Its mapping is obtained through `getMappingCode(...)`.
   - If the returned value is `0` (the NUL character), the algorithm does not update `last`.
   - If the returned mapping is not `'0'` and differs from `last`, it is appended to output.
   - If the returned mapping equals `last`, it is suppressed as a repeated adjacent code.
   - If the returned mapping is `'0'`, it is not appended, but `last` becomes `'0'`.
   - Processing ends as soon as the output contains first letter plus three code characters.

6. Mapping failure
   - `getMappingCode` calls `map`.
   - `map` throws `IllegalArgumentException` if the cleaned character falls outside the configured mapping array.

Important distinction:
- The code uses both NUL character `0` and character literal `'0'`.
- `getMappingCode(...)` can return NUL character `0` specifically for H/W-rule suppression.
- Mapping values of `'0'` represent normal “do not encode” mapping entries.

---

### H/W rule in `getMappingCode(String str, int index)`

This private method is not directly unit-testable without reflection and should normally be exercised through `soundex`.

Branches:

1. Always map the current character.

2. No H/W check if:
   - `index <= 1`, or
   - current mapped code is `'0'`.

3. H/W check when:
   - `index > 1`, and
   - current mapped code is not `'0'`, and
   - preceding character is `'H'` or `'W'`.

4. Within the H/W check, return NUL character `0` if either:
   - the code of the character before H/W equals the current character’s mapped code, or
   - the character before H/W is itself `'H'` or `'W'`.

5. Otherwise return the current mapped code.

The condition involving consecutive H/W characters is directly relevant to Codec-15.

---

### `difference(String s1, String s2)`

This method has no local branches except delegation:

```java
return SoundexUtils.difference(this, s1, s2);
```

Its behavior depends on `SoundexUtils.difference`, which was not supplied. The Javadoc states a result range of `0` through `4`, but null handling, exception behavior, and comparison details cannot be confirmed solely from the supplied target class.

---

### Custom constructor branches

#### `Soundex(char[] mapping)`
- Non-null mapping: creates a new internal array and copies values.
- Null mapping: `NullPointerException`.

#### `Soundex(String mapping)`
- Non-null mapping: converts to a new character array.
- Null mapping: `NullPointerException`.

---

### Deprecated max-length methods

#### `getMaxLength()`
- Returns current stored field value.

#### `setMaxLength(int maxLength)`
- Always assigns the supplied value.
- No range validation.
- No effect on the four-character output generated by `soundex`, according to the source and class-level documentation.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

### Normal cases

The following categories should be covered, but exact expected Soundex code values require an oracle from existing tests, specification examples, or verified algorithm behavior:

1. Default-constructor encoding with normal alphabetic names.
2. Equivalent invocation paths:
   - `soundex(value)`
   - `encode(value)`
   - `encode((Object) value)`
3. Encoding of strings whose codes:
   - require fewer than three digits and therefore retain trailing `'0'` padding,
   - produce exactly three digits,
   - contain more than three eligible codes and must truncate to four output characters total,
   - contain repeated adjacent mapping codes,
   - contain vowels or other `'0'` mapped characters between consonants,
   - contain H/W separators.
4. `difference` for equal, similar, and dissimilar inputs, subject to a reliable oracle.

### Boundary cases

1. `null` String:
   - `soundex(null)` → `null`
   - `encode((String) null)` → `null`

2. Empty string:
   - Expected to return an empty string if `SoundexUtils.clean("")` returns empty, which is strongly implied but the dependency source is not supplied.

3. Input that cleans to empty:
   - Requires `SoundexUtils.clean` behavior to be known before specifying expected results.

4. One-character input:
   - Produces first character followed by three zeros, assuming that character remains after cleaning and is mapped.

5. Two-character input:
   - Exercises loop entry and one-character processing.

6. Inputs that produce:
   - zero emitted numeric codes,
   - one or two emitted numeric codes,
   - exactly three emitted numeric codes,
   - more than three emitted numeric codes.

7. H/W at positions:
   - first position,
   - second position,
   - after another consonant,
   - consecutive H/W characters,
   - H/W between same-code consonants,
   - H/W between different-code consonants.

8. Custom mappings:
   - mapping of length zero,
   - mapping shorter than required for input letter,
   - mapping length exactly sufficient for a selected letter,
   - mapping longer than 26,
   - mapping values containing `'0'`,
   - mapping values other than digits.

9. Deprecated `maxLength` values:
   - negative,
   - zero,
   - standard value `4`,
   - a larger value.
   - Verify getter/setter storage behavior and that encoding output remains fixed at four characters.

### Invalid cases

1. `encode(Object)` with non-String object:
   - Must throw `EncoderException`.

2. `encode((Object) null)`:
   - Must throw `EncoderException`.

3. Encoding a cleaned character outside the configured mapping:
   - Must throw `IllegalArgumentException`.

4. `new Soundex((char[]) null)`:
   - `NullPointerException`.

5. `new Soundex((String) null)`:
   - `NullPointerException`.

### Exceptional cases with insufficient context

The exact behavior for punctuation, whitespace, lowercase letters, digits, accented letters, and non-Latin characters depends on the missing `SoundexUtils.clean` implementation. It is therefore not reliable to prescribe whether these values:
- are removed,
- are normalized,
- are retained,
- result in an empty result, or
- cause an `IllegalArgumentException`.

---

## 5. Required constructors, dependencies, and external objects

### Constructors needed
Tests may instantiate:

```java
new Soundex()
new Soundex(char[] mapping)
new Soundex(String mapping)
```

The public singleton may also be exercised:

```java
Soundex.US_ENGLISH
```

### Dependencies used by the target class
1. `org.apache.commons.codec.StringEncoder`
   - Implemented by `Soundex`.

2. `org.apache.commons.codec.EncoderException`
   - Thrown by `encode(Object)` and declared by `difference`.

3. `org.apache.commons.codec.language.SoundexUtils`
   - Used by:
     - `SoundexUtils.clean(str)`
     - `SoundexUtils.difference(this, s1, s2)`

No dependency injection, filesystem, network service, clock, random generator, database, or external process is required.

### Test framework dependencies
Supplied framework information:
- JUnit: `junit-4.12-hamcrest-1.3.jar`
- Build tool: Maven

Tests should therefore use JUnit 4 conventions, such as:
- `org.junit.Test`
- `org.junit.Assert`
- JUnit 4 exception assertions via `@Test(expected = ...)` or explicit `try/catch` assertions.

No JUnit test class should be generated yet, per the request.

---

## 6. JUnit version and build tool

- **JUnit version:** JUnit 4.12 with Hamcrest 1.3
- **Build tool:** Maven

This is explicitly supplied in the project context.

---

## 7. Available test oracles

### Supplied behavioral/documentation oracle
The class Javadoc provides these usable contractual expectations:

1. `soundex(null)` returns `null` according to the implementation, though this is not explicitly documented in method Javadoc.
2. Soundex output is intended to be four characters:
   - The class states Soundex codes are “only four characters by definition.”
   - The implementation always creates a four-character output for non-empty cleaned input.
3. `difference(...)` is documented to return an integer from `0` through `4`.
4. `encode(Object)` must throw `EncoderException` for a supplied object that is not a `String`.
5. Unmapped characters must cause `IllegalArgumentException`.
6. The deprecated `maxLength` feature is documented as not needed because encoding size is constant.

### Supplied bug-report oracle
For the triggering test:

```text
org.apache.commons.codec.language.SoundexTest::testHWRuleEx1
expected: Y330
actual:   Y300
```

This is a concrete regression oracle for the missing test scenario.

However, the prompt does **not** provide:
- the source code of `SoundexTest`,
- the input string passed by `testHWRuleEx1`,
- the fixed source version,
- the CODEC-199 issue description or reproducer.

Therefore, the supplied information establishes that some input should encode to `Y330`, but does not identify the input required to write a compilable, meaningful regression test reproducing Codec-15.

### Existing tests
Only the triggering test name is supplied:

```text
org.apache.commons.codec.language.SoundexTest::testHWRuleEx1
```

Its source is not supplied. It is the most important missing oracle source.

---

## 8. Bug-report-related behaviors that should be tested

Codec-15 is specifically related to the H/W rule in `getMappingCode`.

The supplied source includes this relevant branch:

```java
if (index > 1 && mappedChar != '0') {
    final char hwChar = str.charAt(index - 1);
    if ('H' == hwChar || 'W' == hwChar) {
        final char preHWChar = str.charAt(index - 2);
        final char firstCode = this.map(preHWChar);
        if (firstCode == mappedChar || 'H' == preHWChar || 'W' == preHWChar) {
            return 0;
        }
    }
}
```

The bug-report outcome indicates that the source version under test incorrectly omits a second code digit in at least one H/W-related scenario:

```text
Expected: Y330
Actual:   Y300
```

The regression test suite should cover:

1. The exact `testHWRuleEx1` input and expected `Y330` output once the existing test source or bug reproducer is supplied.

2. A case where a consonant follows `H` or `W` and the character before H/W has the **same** Soundex code.
   - This exercises intended duplicate suppression across H/W.

3. A case where a consonant follows `H` or `W` and the character before H/W has a **different** Soundex code.
   - This verifies the code is retained when it should not be suppressed.

4. A case where H/W is preceded by another H/W.
   - This executes:
     ```java
     'H' == preHWChar || 'W' == preHWChar
     ```
   - This is the branch most visibly suspicious in relation to the reported missing digit, but the supplied source alone is insufficient to assert the intended outcome for every such sequence.

5. A case where the current character maps to `'0'`.
   - This confirms the H/W special handling is not entered due to:
     ```java
     mappedChar != '0'
     ```

6. Index boundaries:
   - H/W-related behavior at index `0`, `1`, and `2`, because the H/W branch requires `index > 1`.

The test expected values for these additional cases need to come from a supplied Soundex specification, existing `SoundexTest`, or the exact fixed implementation. They should not be inferred solely from the current buggy implementation.

---

## 9. Missing context required for reliable, compilable, meaningful tests

The supplied source is sufficient to write tests for several direct API contracts, including null handling, invalid `Object` handling, mapping constructor null behavior, mapping-range failures, defensive copying of `char[]` mappings, and deprecated max-length storage behavior.

However, the following missing context is needed for a complete and reliable behavioral test suite:

1. **Source for `SoundexUtils`**
   - Required to establish precise behavior of `SoundexUtils.clean`.
   - Required to establish exact `difference` semantics, including null handling and exception behavior.

2. **Source for `SoundexTest::testHWRuleEx1`**
   - Required to identify the exact Codec-15 reproducing input.
   - The report provides only expected and actual output, not the input string.

3. **CODEC-199 issue contents or a supplied reproduction case**
   - Required to determine intended semantics for the H/W sequence that caused `Y300` instead of `Y330`.

4. **Existing Soundex test suite**
   - Needed as the project’s established oracle for standard Soundex examples, case normalization, cleaning behavior, custom mappings, and `difference`.

5. **Maven project configuration (`pom.xml`) or test source layout**
   - Maven is specified, so this is not necessary merely to know the build tool.
   - It would be needed to verify exact dependency coordinates, source compatibility, test naming conventions, and whether the project has special Maven test configuration.

6. **Expected behavior for non-US-English/custom mappings**
   - Constructors permit arbitrary mapping lengths and characters, but no external specification states which non-default mapping configurations are semantically supported beyond the mechanics visible in the implementation.

In particular, a Codec-15 regression test cannot be generated reliably from the supplied information alone because the input for `testHWRuleEx1` is missing.