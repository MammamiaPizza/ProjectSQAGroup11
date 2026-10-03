## Scope and available evidence

- **Target class:** `org.apache.commons.codec.binary.Base32`
- **Source version:** `Codec-16b` (the buggy version for Codec-16)
- **Bug report:** `CODEC-200`
- **Known triggering test:** `org.apache.commons.codec.binary.Base32Test::testCodec200`
- **Known observed failure:**
  ```text
  java.lang.IllegalArgumentException: pad must not be in alphabet or whitespace
  ```
- **Modified production class in the fix:** only `Base32`

The supplied class can be analyzed for its declared constructors, its declared public `isInAlphabet(byte)` method, and its package-private streaming `encode`/`decode` implementations. Some externally visible operations are inherited from `BaseNCodec`, but the parent source and existing test source were not supplied, so their exact signatures and behavior cannot be fully determined.

---

## 1. Public methods and constructors that should be tested

### Public constructors

`Base32` exposes seven public constructors:

1. `Base32()`
2. `Base32(byte pad)`
3. `Base32(boolean useHex)`
4. `Base32(boolean useHex, byte pad)`
5. `Base32(int lineLength)`
6. `Base32(int lineLength, byte[] lineSeparator)`
7. `Base32(int lineLength, byte[] lineSeparator, boolean useHex)`
8. `Base32(int lineLength, byte[] lineSeparator, boolean useHex, byte pad)`

The last constructor is the central configuration implementation; the other constructors delegate to it.

### Public declared method

9. `boolean isInAlphabet(byte octet)`

### Inherited public methods requiring parent-class context

`Base32` extends `BaseNCodec`. The actual public encoding/decoding API is likely inherited from that superclass, but its source was not provided. Therefore, tests will likely need to use inherited APIs such as byte-array/string encode/decode methods, but their exact signatures, null behavior, and return values must be confirmed from `BaseNCodec` or existing tests before generating a compilable test class.

### Package-private methods

These are not public API methods, but are directly testable by a test in package `org.apache.commons.codec.binary`:

- `void encode(byte[] in, int inPos, int inAvail, Context context)`
- `void decode(byte[] in, int inPos, int inAvail, Context context)`

They require `BaseNCodec.Context`, plus access to output state/buffer. The availability and accessibility of `Context` cannot be fully established without `BaseNCodec`.

---

## 2. Input types and valid input ranges

### Constructor inputs

| Input | Type | Relevant ranges / values |
|---|---|---|
| `pad` | `byte` | Java byte range: `-128` to `127`. It must not be recognized as a configured Base32 alphabet byte in the current implementation. The current source additionally rejects whitespace. |
| `useHex` | `boolean` | `false` selects standard Base32 alphabet; `true` selects Base32 Hex alphabet. |
| `lineLength` | `int` | Documentation states `<= 0` means no chunking. Positive values request chunking, with effective encoded line lengths rounded down to a multiple of 8. |
| `lineSeparator` | `byte[]` | May be `null` only if `lineLength <= 0`; must be non-null when `lineLength > 0`. For positive line lengths, it must not contain a byte recognized as alphabet or pad. |
| `octet` | `byte` | Any byte from `-128` through `127`; only nonnegative values within the selected decode-table range can be alphabet characters. |

### Data inputs for the internal streaming methods

| Input | Type | Valid/meaningful conditions based on source |
|---|---|---|
| `in` | `byte[]` | Input byte array. For normal nonnegative `inAvail`, it must contain at least `inAvail` bytes starting at `inPos`. |
| `inPos` | `int` | Starting offset into `in`. Must be valid for the bytes read. |
| `inAvail` | `int` | For `encode`, negative means EOF/flush. For `decode`, negative means EOF. Nonnegative means bytes to process. |
| `context` | `BaseNCodec.Context` | Required and expected non-null. Its creation/accessibility is not supplied. |

---

## 3. Conditions and reachable branches

## Constructor/configuration branches

### Alphabet selection

The final constructor selects tables based on `useHex`:

- `useHex == false`
  - Standard Base32 encode/decode tables.
  - Encoding alphabet: `A-Z`, then `2-7`.
- `useHex == true`
  - Base32 Hex encode/decode tables.
  - Encoding alphabet: `0-9`, then `A-V` according to RFC 4648’s Base32 Hex variant; however, exact behavior should be verified against the actual table/source behavior.

### Chunking configuration

#### `lineLength > 0`

Reachable paths:

1. `lineSeparator == null`
   - Throws:
     ```java
     IllegalArgumentException(
         "lineLength " + lineLength + " > 0, but lineSeparator is null")
     ```

2. `lineSeparator` contains a selected-alphabet byte or the padding byte
   - Throws:
     ```java
     IllegalArgumentException(
         "lineSeparator must not contain Base32 characters: [" + sep + "]")
     ```
   - The check uses `containsAlphabetOrPad(lineSeparator)`, inherited from `BaseNCodec`.

3. Valid non-null separator with no alphabet/pad characters
   - Stores a defensive copy of the separator.
   - Enables line-separator output during encoding.

#### `lineLength <= 0`

- No chunking is configured.
- `lineSeparator` is ignored by `Base32` after it passes its length to `super`.
- `this.lineSeparator` becomes `null`.
- No validation of separator contents occurs in this class for this path.

### Padding validation

After chunk configuration, all constructors eventually apply:

```java
if (isInAlphabet(pad) || isWhiteSpace(pad)) {
    throw new IllegalArgumentException("pad must not be in alphabet or whitespace");
}
```

Reachable outcomes:

1. Pad belongs to the selected Base32 alphabet:
   - Throws `IllegalArgumentException`.

2. Pad is considered whitespace by inherited `isWhiteSpace`:
   - Throws `IllegalArgumentException`.

3. Pad is neither in alphabet nor whitespace:
   - Construction succeeds.

The whitespace restriction is directly relevant to Codec-16/CODEC-200 because the known failing test encountered this exact exception.

---

## Encoding branches

The `encode` implementation has these principal branches.

### Early return for already-finalized context

```java
if (context.eof) {
    return;
}
```

Any call after EOF does nothing.

### EOF / flush path: `inAvail < 0`

Sets `context.eof = true`.

#### No remaining data and no chunking

```java
if (0 == context.modulus && lineLength == 0) {
    return;
}
```

This is a no-output EOF path.

#### Remaining input-byte counts

`context.modulus` is input length modulo 5. The following branches generate an 8-byte Base32 output block:

| Modulus | Remaining source bytes | Encoded alphabet bytes | Padding bytes |
|---:|---:|---:|---:|
| `0` | 0 | 0 | 0 |
| `1` | 1 | 2 | 6 |
| `2` | 2 | 4 | 4 |
| `3` | 3 | 5 | 3 |
| `4` | 4 | 7 | 1 |

The default branch throws `IllegalStateException`, but it is intended to be unreachable because modulus is maintained modulo 5.

#### EOF chunk separator

If chunking is enabled and output has been produced on the current line:

```java
if (lineLength > 0 && context.currentLinePos > 0)
```

the configured line separator is appended, including after the final encoded data.

### Normal input path: `inAvail >= 0`

For each input byte:

- A negative Java byte is converted to unsigned range `0..255`.
- `context.modulus` is updated modulo 5.
- Every complete group of 5 input bytes produces 8 encoded bytes.
- If chunking is enabled and accumulated encoded line position reaches/exceeds effective `lineLength`, the configured separator is appended and the line position resets.

Important branch cases:

- Input lengths `0`, `1`, `2`, `3`, `4`, `5`, `6`, etc.
- Values containing negative Java bytes, such as `(byte) 0x80` and `(byte) 0xFF`.
- Chunk boundaries reached exactly at a 5-byte input block.
- Final partial block with/without chunking.
- EOF after a complete block with chunking enabled.

---

## Decoding branches

### Early return for already-finalized context

```java
if (context.eof) {
    return;
}
```

### EOF signaled by negative availability

```java
if (inAvail < 0) {
    context.eof = true;
}
```

### Per-byte branches

For every input byte before EOF:

1. **Padding encountered**
   ```java
   if (b == pad) {
       context.eof = true;
       break;
   }
   ```
   - Decoding stops at the first padding byte.
   - Any later input is ignored.

2. **Byte outside decode table range**
   ```java
   if (b >= 0 && b < this.decodeTable.length)
   ```
   - Negative bytes and bytes outside the decode table range are silently ignored.

3. **Byte inside table but not in selected alphabet**
   ```java
   if (result >= 0)
   ```
   - Table entries with `-1` are ignored.

4. **Valid alphabet byte**
   - Added to the bit work area.
   - Every 8 valid Base32 characters emits 5 decoded bytes.

### EOF partial-block branches

At EOF or first padding, if there are at least 2 valid Base32 characters since the last complete 8-character group:

```java
if (context.eof && context.modulus >= 2)
```

the code handles the following partial-modulus cases:

| Decode modulus | Valid Base32 characters accumulated | Decoded output bytes |
|---:|---:|---:|
| `0` | complete block | Already emitted during normal processing |
| `1` | 1 | No output |
| `2` | 2 | 1 byte |
| `3` | 3 | 1 byte |
| `4` | 4 | 2 bytes |
| `5` | 5 | 3 bytes |
| `6` | 6 | 3 bytes |
| `7` | 7 | 4 bytes |

The `default` branch throws `IllegalStateException`, but is intended to be unreachable because modulus is maintained modulo 8.

### Documented malformed-input behavior

The supplied Javadoc explicitly states:

> Ignores all non-Base32 characters.

and:

> it will not check the provided data for validity.

Therefore, decoding tests should cover ignored nonalphabet characters rather than expecting strict rejection. This includes line separators and other junk bytes, except the configured padding byte, which terminates decoding.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

## Normal cases

1. Default standard Base32 configuration:
   - Encode/decode round trip through the inherited public API, once exact API signatures are confirmed.
   - RFC 4648 known vectors are appropriate oracle candidates because the class documentation explicitly states RFC 4648 compliance.

2. Standard alphabet:
   - `A-Z`, `2-7` accepted by `isInAlphabet`.
   - Valid standard alphabet encoding output.

3. Base32 Hex:
   - Constructor with `useHex == true`.
   - Hex alphabet encoding/decoding and `isInAlphabet` checks.

4. Custom valid nonalphabet, non-whitespace padding:
   - Constructor succeeds.
   - Partial input encoding uses that pad.
   - Decoder stops at that same pad.

5. Chunked encoding:
   - Valid nonalphabet separator.
   - Output line breaks at effective Base32 block boundaries.
   - Decoder ignores the separator.

6. Defensive copy behavior:
   - Construct with a valid `lineSeparator`.
   - Mutate the original caller-supplied array afterward.
   - Encoded output should continue to use the originally supplied separator bytes because the constructor copies it.

## Boundary cases

1. `lineLength`:
   - Negative values.
   - Zero.
   - Values `1` through `7` because encoded Base32 blocks have length 8.
   - Exactly `8`.
   - Values just above a multiple of 8, e.g. `9`.
   - A larger multiple such as `16`.

   The documented contract says the effective line length is rounded down to a multiple of 8. Exact inherited `BaseNCodec` line-length normalization should be confirmed before asserting output for values such as `1..7`.

2. Input byte lengths:
   - `0`
   - `1`, `2`, `3`, `4` — all padding branches
   - `5` — one complete Base32 block
   - `6` through `9` — complete block plus each remainder
   - Larger data crossing chunk boundaries.

3. `isInAlphabet(byte)`:
   - Lowest and highest recognized standard-alphabet values.
   - Characters immediately outside ranges:
     - Before `A`, after `Z`
     - Before `2`, after `7`
   - Negative bytes.
   - Whitespace.
   - Default pad `'='`.
   - Characters valid only in the alternative Base32 Hex alphabet.

4. Decode partial quantum sizes:
   - 1 through 7 valid encoded symbols followed by EOF or padding.
   - Especially modulus 1, which produces no output.

## Invalid and exceptional cases

1. Positive `lineLength` with `lineSeparator == null`
   - Must throw `IllegalArgumentException`.

2. Positive `lineLength` with separator containing a selected Base32 alphabet byte
   - Must throw `IllegalArgumentException`.

3. Padding byte in selected alphabet
   - Must throw `IllegalArgumentException` in this source version.

4. Padding byte recognized as whitespace
   - Must throw `IllegalArgumentException` in this source version.
   - This is the observed Codec-16 failure path and must be investigated against the bug oracle before treating the exception as correct behavior.

5. Decoder receives invalid/nonalphabet bytes
   - Per Javadoc and implementation, they are ignored, not rejected.

6. Decoder receives pad before valid data is complete
   - Decoding finalizes at first pad and emits any output implied by the accumulated modulus.

7. Direct package-private API invalid offsets or array lengths:
   - Normal `inPos`/`inAvail` bounds are not validated explicitly.
   - Invalid combinations can cause ordinary array access exceptions.
   - Such behavior should not be asserted as API contract without superclass/test context.

## Null cases

| Area | Behavior determinable from supplied code |
|---|---|
| `new Base32(lineLength > 0, null, ...)` | Throws `IllegalArgumentException`. |
| `new Base32(lineLength <= 0, null, ...)` | Accepted by this constructor. |
| `isInAlphabet` | Primitive `byte`; null is not applicable. |
| Package-private `encode(null, ..., inAvail >= 0, context)` | Would eventually fail through dereference; exact exception depends on path. |
| Package-private `decode(null, ..., inAvail >= 0, context)` | Would eventually fail through dereference; exact exception depends on path. |
| `Context` null | Would fail when dereferenced; no explicit validation. |
| Inherited public encode/decode methods accepting `null` | Cannot be determined because `BaseNCodec` is absent. |

---

## 5. Required constructors, dependencies, and external objects

### Required production dependencies

The class depends on:

- `org.apache.commons.codec.binary.BaseNCodec`
  - Superclass.
  - Provides fields/methods including at least:
    - `PAD_DEFAULT`
    - `MASK_8BITS`
    - `lineLength`
    - `pad`
    - `containsAlphabetOrPad(...)`
    - `isWhiteSpace(...)`
    - `ensureBufferSize(...)`
    - `Context`
  - Its source is required to confirm inherited public APIs and to write direct streaming tests.

- `org.apache.commons.codec.binary.StringUtils`
  - Used only to convert an invalid line separator to a UTF-8 string for the exception message.

### No dependency injection or external services

No network, file system, clock, database, or mockable external service is used by `Base32`. Tests should be deterministic and need no mocks.

### Objects needed for direct streaming-method tests

To test package-private `encode` and `decode` directly, tests need:

- Test package:
  ```java
  package org.apache.commons.codec.binary;
  ```
- An accessible `BaseNCodec.Context` constructor.
- A way to extract output from the context buffer, respecting its read/write positions.
- Confirmation of context lifecycle/reset requirements.

Those details are missing from the supplied material.

---

## 6. JUnit version and build tool

- **JUnit:** JUnit 4.12
- **Hamcrest:** 1.3
- **Build tool:** Maven

Tests should therefore use JUnit 4 style, for example:

- `@Test`
- `@Test(expected = IllegalArgumentException.class)` where appropriate
- `Assert.assertEquals`
- `Assert.assertArrayEquals`
- Hamcrest assertions only if useful and available through the stated dependency.

No JUnit 5 APIs should be used.

---

## 7. Available test oracles

### Strongest supplied oracles

1. **Class Javadoc**
   - States Base32 encoding/decoding is defined by RFC 4648.
   - Defines constructor behavior for line length and separators.
   - Defines that decode ignores non-Base32 characters.
   - Defines streaming EOF behavior.

2. **Implementation-level documented behavior**
   - Constructor exception conditions are explicit in source/Javadoc.
   - Standard and Base32 Hex table choices are explicit.
   - Padding behavior and output sizes for incomplete blocks are explicit.

3. **Bug metadata**
   - The known failing test is:
     ```text
     Base32Test::testCodec200
     ```
   - It failed because the constructor rejected a pad:
     ```text
     pad must not be in alphabet or whitespace
     ```

4. **RFC 4648 reference**
   - The class directly claims RFC 4648 compliance.
   - RFC Base32 and Base32 Hex test vectors would be a valid external specification oracle, provided the test author uses the actual RFC values rather than inventing expected encodings.

### Oracle limitations

The actual source of:

```text
org.apache.commons.codec.binary.Base32Test::testCodec200
```

was not supplied. Therefore, the following critical facts are unknown:

- The exact constructor arguments used by the triggering test.
- The exact padding byte used.
- Whether the test expected successful construction only, specific encoding output, successful decode, or all of these.
- The intended change in the fixed revision.
- Whether the bug concerns whitespace padding specifically or a different interaction that reaches the same exception.

The provided failure strongly indicates that the current pad-validation rule is incompatible with the CODEC-200 scenario, but it is insufficient to establish the exact expected behavior for every whitespace padding byte.

---

## 8. Bug-report-related behaviors to test

The primary regression area is constructor validation of custom padding.

### Observed buggy behavior

All custom-pad constructors eventually invoke:

```java
if (isInAlphabet(pad) || isWhiteSpace(pad)) {
    throw new IllegalArgumentException("pad must not be in alphabet or whitespace");
}
```

The known triggering test failed at this point.

### Regression scenarios that must be identified from the real oracle

The eventual regression test should use the exact configuration and expected result from `Base32Test::testCodec200` or the CODEC-200 issue/fixed diff.

At minimum, the test must establish whether the following should succeed in the fixed version:

1. Construction using the pad byte involved in CODEC-200.
2. Encoding a partial input with that pad, if the bug report covers output behavior.
3. Decoding output that contains that pad, if the bug report covers decoding behavior.
4. Continued rejection of pads that belong to the selected Base32 alphabet, unless the fixed oracle explicitly changes that contract.

### Important caution

It would be unsafe to write a regression test that broadly assumes:

> “All whitespace bytes must be accepted as Base32 padding.”

That behavior is plausible given the reported exception, but it is not explicitly supplied by the bug report text. The exact expected accepted pad and semantic reason need the actual triggering test, issue contents, or fixed-source diff.

---

## 9. Missing context needed before generating reliable, compilable tests

The following information is missing:

1. **`BaseNCodec` source**
   - Exact inherited public `encode`/`decode` method signatures.
   - Null-input behavior.
   - Line-length normalization behavior.
   - Definition of whitespace.
   - Behavior of `containsAlphabetOrPad`.
   - `Context` constructor and output extraction mechanics.
   - Potential preconditions and behavior imposed by superclass methods.

2. **The existing `Base32Test` source, especially `testCodec200`**
   - Required to reproduce Codec-16 precisely.
   - Needed to identify the exact input values and expected behavior.

3. **CODEC-200 issue contents or the fixed-source diff**
   - Needed to determine whether whitespace padding is intended to be accepted generally, or whether a narrower condition is expected.
   - Needed to avoid encoding the buggy behavior as a test oracle.

4. **Maven project configuration (`pom.xml`)**
   - Maven is specified, but source/test directory details, compiler source level, and dependency configuration are not shown.
   - This is less critical than `BaseNCodec`, but useful for ensuring compilation compatibility.

5. **Existing test conventions**
   - Existing project tests may use helper methods, encoding constants, package placement, or expected RFC vectors.
   - Reusing those patterns would improve compatibility and reduce duplicated assumptions.

---

## Conclusion

The supplied source is sufficient to plan constructor-validation, alphabet-selection, chunking, direct encoding/decoding branch, and `isInAlphabet` test coverage. It also clearly identifies the Codec-16 failure point: custom-pad validation rejects a pad that is “in alphabet or whitespace.”

However, it is **not sufficient to generate a reliable Codec-16 regression assertion** because the actual `testCodec200` inputs and expected outcome are not supplied. It is also insufficient to generate fully compilable inherited-API or direct-streaming tests without `BaseNCodec` source/API details.