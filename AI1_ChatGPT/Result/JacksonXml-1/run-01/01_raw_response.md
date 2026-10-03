## Scope and source-context observations

The supplied target is `com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser`, an XML-to-Jackson-token parser. It depends heavily on classes not supplied in full:

- `XmlTokenStream`
- `XmlReadContext`
- Jackson core classes such as `ParserMinimalBase`, `IOContext`, `JsonToken`, `ObjectCodec`, `Base64Variant`, and `ByteArrayBuilder`
- StAX `XMLStreamReader`

The supplied source contains comments explicitly referencing bug `dataformat-xml#180`, including behavior such as:

> “need to expose as empty Object, not null”

However, the supplied bug metadata identifies this as `JacksonXml-1b` (the buggy version), and the target source itself is listed as modified in the fixed revision. This creates an important inconsistency: the shown source appears to contain at least comments, and possibly code, associated with the fix. Therefore, the supplied code must not be treated as a reliable oracle for the intended buggy or fixed behavior.

No JUnit test class is generated here.

---

## 1. Public methods that should be tested

### Construction and basic parser configuration

| Method / constructor | Test relevance |
|---|---|
| `FromXmlParser(IOContext, int, int, ObjectCodec, XMLStreamReader)` | Required construction path; initializes parsing context, initial next token, codec, feature flags, and `XmlTokenStream`. |
| `version()` | Returns project package version. |
| `getCodec()` | Returns currently assigned codec. |
| `setCodec(ObjectCodec)` | Replaces codec. |
| `setXMLTextElementName(String)` | Configures pseudo-property name used for text content. |
| `requiresCustomCodec()` | Contractually always returns `true`. |

### Format-feature configuration

| Method | Test relevance |
|---|---|
| `enable(Feature)` | Sets a format-feature bit and returns the same parser. |
| `disable(Feature)` | Clears a format-feature bit and returns the same parser. |
| `isEnabled(Feature)` | Checks whether a format-feature bit is set. |
| `configure(Feature, boolean)` | Delegates to enable or disable. |
| `getFormatFeatures()` | Returns current feature-bit mask. |
| `overrideFormatFeatures(int, int)` | Replaces only bits selected by mask and returns parser. |
| `Feature.collectDefaults()` | Computes mask of enabled-by-default XML features. |
| `Feature.enabledByDefault()` | Returns feature’s configured default state. |
| `Feature.getMask()` | Returns bit based on enum ordinal. |
| `Feature.enabledIn(int)` | Checks whether the feature bit is set in a mask. |

**Limitation:** `Feature` declares no enum constants in the supplied source. Consequently, normal non-null tests of `enable`, `disable`, `configure`, and `isEnabled` cannot be created using only this source. `Feature.collectDefaults()` is testable and should return `0` because there are no values.

### StAX and virtual wrapping API

| Method | Test relevance |
|---|---|
| `getStaxReader()` | Must expose the same reader used by `XmlTokenStream`. |
| `addVirtualWrapping(Set<String>)` | Critical for nested unwrapped-list handling and the reported bug. It configures virtual element replaying and names-to-wrap in the parsing context. |

### Parser state, names, locations, lifecycle

| Method | Test relevance |
|---|---|
| `getCurrentName()` | Retrieves current XML/Jackson logical property name; has special behavior for `START_OBJECT` and `START_ARRAY`; can throw `IllegalStateException`. |
| `overrideCurrentName(String)` | Changes current property name, with special parent-context behavior for object/array starts. |
| `close()` | Closes underlying token stream differently based on managed-resource/AUTO_CLOSE_SOURCE state; must release buffers even if close fails. |
| `isClosed()` | Reports close state. |
| `getParsingContext()` | Returns active `XmlReadContext`. |
| `getTokenLocation()` | Delegates token-start location retrieval. |
| `getCurrentLocation()` | Delegates current location retrieval. |
| `isExpectedStartArrayToken()` | Converts a current `START_OBJECT` to `START_ARRAY`, changes parsing context, clears pending token, skips attributes. |

### Core token parsing

| Method | Test relevance |
|---|---|
| `nextToken()` | Main behavior. Must be tested extensively with XML elements, attributes, text, empty elements, arrays, nested objects, virtual wrapping, end-of-input, and malformed XML. |
| `nextTextValue()` | Optimized text-token path with behavior intentionally different from `nextToken()` for empty leaves and empty array content. |

### Text/value accessors

| Method | Test relevance |
|---|---|
| `getText()` | Returns token-dependent text representation. |
| `getValueAsString()` | Equivalent to `getValueAsString(null)`. |
| `getValueAsString(String)` | Handles field names, text values, scalar token names, and potential `START_OBJECT` conversion through `XmlTokenStream.convertToString()`. |
| `getTextCharacters()` | Returns `getText().toCharArray()` or `null`. |
| `getTextLength()` | Returns text length or zero. |
| `getTextOffset()` | Always returns zero. |
| `hasTextCharacters()` | Always returns false. |

### Binary-value accessors

| Method | Test relevance |
|---|---|
| `getEmbeddedObject()` | Always returns `null`. |
| `getBinaryValue(Base64Variant)` | Decodes current string token as Base64, caches result, rejects invalid token state, wraps invalid Base64 decoding errors as parser errors. |

### Numeric accessors

| Method | Test relevance |
|---|---|
| `getBigIntegerValue()` | Currently returns `null`. |
| `getDecimalValue()` | Currently returns `null`. |
| `getDoubleValue()` | Currently returns `0`. |
| `getFloatValue()` | Currently returns `0`. |
| `getIntValue()` | Currently returns `0`. |
| `getLongValue()` | Currently returns `0`. |
| `getNumberType()` | Currently returns `null`. |
| `getNumberValue()` | Currently returns `null`. |

These methods are visibly incomplete (`TODO Auto-generated method stub`). The source provides no specification establishing whether these placeholder return values are intentional. A meaningful expected result for valid numeric XML content cannot be determined reliably from the supplied material alone.

---

## 2. Input types and valid input ranges

### Constructor inputs

| Input | Type | Validity / constraints inferable from source |
|---|---|---|
| `ctxt` | `IOContext` | Must be non-null in practice: constructor calls `ctxt.getSourceReference()` immediately. |
| `genericParserFeatures` | `int` | Jackson generic parser feature bitmask; any integer is mechanically accepted. |
| `xmlFeatures` | `int` | XML format feature bitmask; any integer is mechanically accepted. Since `Feature` is empty, no defined feature bits are supplied. |
| `codec` | `ObjectCodec` | Can be null mechanically; stored directly. Whether null is acceptable to callers depends on external Jackson/XML mapper behavior not supplied. |
| `xmlReader` | `XMLStreamReader` | Required by `XmlTokenStream`; null behavior cannot be determined without `XmlTokenStream`. |

### XML input

The parser consumes events from an `XMLStreamReader` indirectly through `XmlTokenStream`. Meaningful test XML should include:

- A root element.
- Empty element: `<item/>` or `<item></item>`.
- Text leaf: `<item>value</item>`.
- Whitespace-only text: `<item> </item>`, `<item>\n\t</item>`.
- Elements with attributes: `<item attr="value"/>`.
- Elements with attributes and text: `<item attr="value">text</item>`.
- Nested elements: `<root><child>value</child></root>`.
- Repeated sibling elements intended as arrays.
- Nested unwrapped-list structures, especially empty nested list elements.
- Invalid/malformed XML, if the StAX implementation exposes it through parser operations.

### Other public-method inputs

| Method | Input | Valid range / notable invalid input |
|---|---|---|
| `setCodec` | `ObjectCodec` | Any object reference; null is not rejected here. |
| `setXMLTextElementName` | `String` | Any string, including empty string. Null is accepted by assignment but may later produce missing-name failures. |
| `enable`, `disable`, `isEnabled`, `configure` | `Feature` | No non-null values exist in supplied enum. Passing null causes dereference and therefore `NullPointerException`. |
| `configure` | `boolean` | Both `true` and `false`. |
| `overrideFormatFeatures` | `int values`, `int mask` | All integer values; behavior is bitwise replacement: `(_formatFeatures & ~mask) \| (values & mask)`. |
| `addVirtualWrapping` | `Set<String>` | Intended non-null set of XML local names. Null can cause `NullPointerException` when a current local name exists because `namesToWrap.contains(name)` is called. |
| `overrideCurrentName` | `String` | Any string mechanically accepted, including null; later `getCurrentName()` may fail if current name is null. |
| `getValueAsString` | `String defValue` | Any string, including null. |
| `getBinaryValue` | `Base64Variant` | A non-null Jackson Base64 variant is required in practice. Null handling is not defined by this class and depends on inherited decoder behavior. |

---

## 3. Conditions and reachable branches

## `nextToken()` branches

`nextToken()` first clears `_binaryValue`, then has two main paths.

### A. Pending `_nextToken` path

When `_nextToken != null`, it returns that token and updates parser state:

- `START_OBJECT`: creates a child object context.
- `START_ARRAY`: creates a child array context.
- `END_OBJECT` / `END_ARRAY`: restores parent context and updates `_namesToWrap`.
- `FIELD_NAME`: updates current name from `_xmlTokens.getLocalName()`.
- Other values (`VALUE_STRING`, `VALUE_NULL`): no context update.

Test scenarios should reach queued tokens after:

- XML elements that contain child elements.
- Attribute-containing elements.
- Mixed content where text is exposed as a pseudo-property.

### B. XML event path

#### `XML_START_ELEMENT`

Reachable sub-branches:

1. `_mayBeLeaf == true`
   - Existing element was tentatively considered a leaf but a nested element appears.
   - Returns `START_OBJECT`.
   - Queues `FIELD_NAME`.
   - Creates a child object context.

2. Current parsing context is an array
   - Suppresses reporting of a field name for array member elements.
   - Advances to the next XML event.
   - Sets `_mayBeLeaf = true`.
   - Loops for further XML start elements.

3. Normal object/root element
   - Sets current name to local element name.
   - If `_namesToWrap` contains that name, calls `_xmlTokens.repeatStartElement()`.
   - Sets `_mayBeLeaf = true`.
   - Returns `FIELD_NAME`.

#### `XML_END_ELEMENT`

1. `_mayBeLeaf == true`
   - Treats the element as an empty leaf.
   - Clears `_mayBeLeaf`.
   - Returns `VALUE_NULL` in the supplied implementation.

2. `_mayBeLeaf == false`
   - Returns `END_ARRAY` if current context is an array.
   - Otherwise returns `END_OBJECT`.
   - Restores parent context and `_namesToWrap`.

This empty-leaf branch is directly relevant to Bug 180.

#### `XML_ATTRIBUTE_NAME`

1. `_mayBeLeaf == true`
   - The element cannot remain a simple leaf because it has attributes.
   - Clears `_mayBeLeaf`.
   - Saves current XML text.
   - Creates a child object context.
   - Queues a field name.
   - Returns `START_OBJECT`.

2. `_mayBeLeaf == false`
   - Sets current name to attribute local name.
   - Returns `FIELD_NAME`.

#### `XML_ATTRIBUTE_VALUE`

- Saves XML text.
- Returns `VALUE_STRING`.

#### `XML_TEXT`

1. `_mayBeLeaf == true`
   - Treats content as leaf text.
   - Clears `_mayBeLeaf`.
   - Calls `_xmlTokens.skipEndElement()`.

   If current context is an array and text is empty/whitespace-only:
   - Returns `END_ARRAY`.
   - Restores parent context and wrapping names.

   Otherwise:
   - Returns `VALUE_STRING`.

2. `_mayBeLeaf == false`, current context is an object, current token is not `FIELD_NAME`, and text is empty/whitespace-only
   - Returns `END_OBJECT`.
   - Restores parent context and wrapping names.

3. Other non-leaf text
   - Converts text into a named pseudo-property.
   - Sets current name to `_cfgNameForTextElement`.
   - Queues `VALUE_STRING`.
   - Returns `FIELD_NAME`.

#### `XML_END`

- Returns `null`, indicating end of input.

#### Unexpected `XmlTokenStream` event

- Calls `_throwInternal()`.
- Exact exception behavior depends on inherited Jackson implementation.

---

## `nextTextValue()` branches

This method resembles `nextToken()` but is intentionally different in significant cases.

Important differences:

- An empty leaf at `XML_END_ELEMENT` returns `""` and sets `VALUE_STRING`.
- For leaf text inside an array, it always returns the text directly, including empty/whitespace text.
- It does **not** apply the `nextToken()` workaround that returns `END_ARRAY` for empty array text.
- Pending `VALUE_STRING` returns `_currText` directly.
- For a pending non-string token, it calls `_updateState(t)` and returns null.

Tests should explicitly compare token/state behavior of `nextToken()` versus `nextTextValue()` for:

- Empty element.
- Empty/whitespace text in an array.
- Normal text value.
- Attribute-bearing element.
- Nested object.

---

## `getCurrentName()` and `overrideCurrentName()` branches

### `getCurrentName()`

- On `START_OBJECT` or `START_ARRAY`, retrieves the name from the parent context.
- On all other tokens, retrieves it from the current context.
- Throws `IllegalStateException` if resolved name is null.

Required states to test:

- Field name token.
- Value token following field name.
- Object start.
- Array start.
- Root/initial/no-name state, if obtainable.
- State after `overrideCurrentName()`.

### `overrideCurrentName(String)`

- For `START_OBJECT` and `START_ARRAY`, writes to parent context.
- For other tokens, writes to current context.

---

## `getValueAsString(String)` branches

- Current token is `null`: returns `null`, not `defValue`.
- `FIELD_NAME`: returns `getCurrentName()`.
- `VALUE_STRING`: returns `_currText`.
- `START_OBJECT`:
  - Calls `_xmlTokens.convertToString()`.
  - If result is non-null:
    - Moves parser context to parent.
    - Changes current token to `VALUE_STRING`.
    - Clears pending token.
    - Skips matching end element.
    - Returns converted string.
  - If result is null: returns null.
- Other scalar tokens: returns `token.asString()`.
- Other non-scalar tokens: returns `defValue`.

The behavior of `XmlTokenStream.convertToString()` is not supplied, so exact XML cases that produce a non-null conversion result cannot be established reliably.

---

## `getBinaryValue(Base64Variant)` branches

- If current token is neither `VALUE_STRING` nor a `VALUE_EMBEDDED_OBJECT` with cached binary data:
  - Reports parser error.
- If `_binaryValue` is null:
  - Decodes Base64 from current text.
  - Wraps `IllegalArgumentException` as a Jackson parse error.
- If `_binaryValue` is already present:
  - Returns cached byte array without re-decoding.

Tests should include:

- Valid Base64 text.
- Repeated call on same value, checking equal content and preferably cache identity if that is part of observable behavior.
- Invalid Base64 text.
- Calling at `FIELD_NAME`, object start, end token, and no-current-token state.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

### Normal cases

- Parser initial state and first token progression.
- Parsing a simple text leaf.
- Parsing a nested XML object.
- Parsing attributes as field/value pairs.
- Parsing text plus attributes.
- Changing codec and retrieving it.
- Configuring custom text pseudo-property name.
- Location access during parsing.
- Close and closed-state behavior.
- Valid Base64 decoding.
- Text accessor behavior for field names, value strings, and structural tokens.
- Virtual wrapping with a non-empty, valid set of element names.

### Boundary cases

- Empty XML element: `<item/>`.
- Explicit empty pair: `<item></item>`.
- Empty string text: `<item></item>` or StAX-exposed empty text event, depending on reader.
- Whitespace-only text.
- One element in a repeated/unwrapped list.
- Nested lists with a single empty nested item.
- Text-name configuration as empty string, default behavior.
- Empty virtual wrapping set.
- `overrideFormatFeatures` with:
  - `mask == 0`
  - `mask == -1`
  - `values == 0`
  - flags outside defined feature range
- Repeated `close()` calls.
- `getText()` and related methods when `_currToken == null`.

### Invalid / exceptional cases

- `getCurrentName()` when no current name exists: expected `IllegalStateException`.
- `getBinaryValue()` on non-string token: expected parser error.
- Invalid Base64: expected wrapped parsing exception.
- Malformed XML / StAX exception propagation: exact exception type and timing require `XmlTokenStream` and StAX configuration.
- Premature EOF with an open non-root parsing context: `_handleEOF()` reports invalid EOF. Reaching this reliably requires malformed/truncated XML and knowledge of `XmlTokenStream`.
- `enable(null)`, `disable(null)`, `isEnabled(null)`, `configure(null, ...)`: expected `NullPointerException` due to `f.getMask()`.
- Constructor with null `IOContext`: immediate `NullPointerException` is expected from `ctxt.getSourceReference()`.
- `addVirtualWrapping(null)`: may throw `NullPointerException` if a current local name exists; behavior otherwise is state-dependent.
- `setXMLTextElementName(null)`: accepted immediately, but later non-leaf text handling may establish a null current name and make `getCurrentName()` fail.

### Null-specific cases

| API | Null status based on supplied code |
|---|---|
| Constructor `ctxt` | Not supported; immediate dereference. |
| Constructor `codec` | Stored as null; no immediate failure. |
| Constructor `xmlReader` | Undetermined without `XmlTokenStream`. |
| `setCodec(null)` | Accepted by assignment. |
| `setXMLTextElementName(null)` | Accepted by assignment; later parse behavior may be invalid. |
| `getValueAsString(null)` | Explicitly supported as default value argument. |
| `addVirtualWrapping(null)` | Unsafe when current name is non-null. |
| Feature arguments | Null throws `NullPointerException`. |
| `getBinaryValue(null)` | Undefined from supplied class; likely fails during decoding, but exact error is not reliable without inherited decoder details. |

---

## 5. Required constructors, dependencies, and external objects

### Direct construction dependencies

To instantiate `FromXmlParser` directly, tests need:

1. `IOContext`
2. Generic Jackson parser feature bitmask
3. XML feature bitmask
4. `ObjectCodec`, commonly an `XmlMapper`
5. A StAX `XMLStreamReader`

### External objects/classes required for meaningful parser tests

- `javax.xml.stream.XMLInputFactory`
- `javax.xml.stream.XMLStreamReader`
- `com.fasterxml.jackson.core.io.IOContext`
- A Jackson `BufferRecycler` or another supported way to create `IOContext`, depending on the actual Jackson-core version
- `ObjectCodec` / `XmlMapper`
- Jackson `JsonToken`
- Jackson `Base64Variants` / `Base64Variant`
- `XmlReadContext` and `XmlTokenStream` behavior indirectly

### Alternative integration construction path

A more stable regression test may create a parser via the project’s XML factory/mapper API rather than directly constructing `FromXmlParser`. However, the supplied context does not include:

- `XmlFactory`
- `XmlMapper` constructors/factory methods
- the project `pom.xml`
- the exact Jackson dependency version

Therefore, the exact compilable factory call cannot be determined solely from the prompt.

---

## 6. JUnit version and build tool

- **JUnit:** `junit-4.12.jar`
- **Build tool:** Maven

The existing failures are reported as `junit.framework.AssertionFailedError`, which may indicate legacy JUnit 3-style assertion inheritance or JUnit compatibility behavior in existing tests. Nevertheless, the explicitly supplied test framework version is JUnit 4.12.

The Maven configuration, source/target Java level, Surefire configuration, dependency versions, and test source layout are not supplied.

---

## 7. Available test oracles

### Supplied oracles

1. **Bug metadata**
   - Bug report: GitHub issue `#180`.
   - Fixed revision: `2d7683ed820116b77cba9b4b290cd7ce7dfa5cf4`.
   - Triggering tests:
     - `NestedUnwrappedLists180Test::testNestedUnwrappedLists180`
     - `NestedUnwrappedListsTest::testNestedWithEmpty2`
     - `NestedUnwrappedListsTest::testNestedWithEmpty`

2. **Failure information**
   - Two failures report: `expected:<1> but was:<0>`.
   - This establishes that an expected collection/list size or nested-item count is 1 but was observed as 0.

3. **Public Javadocs/comments in the supplied class**
   - `requiresCustomCodec()` should return true.
   - `setXMLTextElementName` configures pseudo-property name for text.
   - `addVirtualWrapping` supports unwrapped arrays and has special handling for lists-in-lists.
   - `isExpectedStartArrayToken()` converts XML object representation to an expected array representation.
   - `getTextOffset()` and `hasTextCharacters()` have clear constant behavior.
   - `getEmbeddedObject()` has clear null behavior.

4. **Direct source behavior**
   - This can serve as an oracle only for simple explicit contracts/constant-return behavior, not for validating correctness of potentially buggy parsing logic.

### Oracle limitations

The actual contents of the bug report and the triggering test classes are not supplied. In particular, missing are:

- The XML input used by `NestedUnwrappedLists180Test`.
- The POJO/list model being deserialized.
- Annotation configuration for unwrapped lists.
- The exact expected token sequence.
- Whether an empty nested XML element represents:
  - an empty object,
  - an empty list,
  - a null value,
  - a retained list entry,
  - or another mapping-specific representation.

Therefore, a fully reliable regression expectation for Bug 180 cannot be derived from the supplied material alone.

---

## 8. Bug-report-related behavior that should be tested

The reported defect concerns nested unwrapped lists and empty nested content. Tests should focus on the interaction among:

- `addVirtualWrapping(Set<String>)`
- `_namesToWrap`
- `_xmlTokens.repeatStartElement()`
- `_mayBeLeaf`
- array-context handling in `nextToken()`
- `XML_END_ELEMENT` handling for empty leaf elements
- `XML_TEXT` handling for empty or whitespace-only text in arrays
- restoration of `_parsingContext` and `_namesToWrap` after array/object end tokens

### Required bug-focused scenarios

1. **Nested unwrapped list with an empty nested element**
   - The outer list must retain the nested item rather than silently dropping it.
   - The observed result should contain one nested item where the intended model/input represents one nested item.
   - This directly corresponds to the reported `expected:<1> but was:<0>` failures.

2. **Two forms of empty nested XML**
   - Self-closing form, such as `<item/>`.
   - Explicit opening/closing form, such as `<item></item>`.
   - The triggering test names `testNestedWithEmpty` and `testNestedWithEmpty2` strongly suggest multiple empty-element representations or related structural variants.

3. **Whitespace-only nested content**
   - XML such as `<item> </item>` and `<item>\n</item>`.
   - `_isEmpty()` treats all characters `<= ' '` as empty.
   - The `XML_TEXT` / array branch contains special handling for this exact case.

4. **Nested list after an empty element**
   - Verify that parser context and wrapping state are restored correctly so following sibling list entries are not lost or misclassified.

5. **Virtual wrapping initialization when current XML name is wrapped**
   - `addVirtualWrapping()` repeats the current start element when the current local name belongs to the wrapping set.
   - This is explicitly documented as necessary to avoid “Lists-in-Lists properties” problems.

6. **Token-level behavior versus data-binding behavior**
   - A token-level regression test can identify whether an element is skipped, exposed as `VALUE_NULL`, converted to an end token, or represented as a nested structure.
   - A mapper-level regression test is needed to verify the actual list size and object retention reported by the bug.

### Important uncertainty

The comments in `nextToken()` claim that Bug 180 requires exposing an empty object “not null,” but the visible `XML_END_ELEMENT` branch returns `JsonToken.VALUE_NULL` for `_mayBeLeaf`. The empty-text-in-array branch returns `END_ARRAY`. Without the original triggering tests or issue text, it is not possible to state a reliable expected token sequence for Bug 180 from the supplied context.

---

## 9. Missing context required for compilable and meaningful tests

The following information is missing or insufficient.

### Essential for an exact Bug 180 regression test

1. Source of:
   - `NestedUnwrappedLists180Test`
   - `NestedUnwrappedListsTest`
2. XML documents used by those tests.
3. Java POJO classes used for list deserialization.
4. Jackson XML annotations and mapper configuration used to mark lists as unwrapped.
5. Exact expected assertion values beyond the observed size mismatch.
6. The full GitHub issue #180 description or accepted behavior.

### Essential for direct-constructor test compilation

1. The project’s Jackson-core version.
2. `IOContext` constructor/factory signature for that version.
3. How the project normally creates `IOContext`.
4. `XmlTokenStream` constructor and behavior, especially null and malformed-XML handling.
5. `XmlReadContext` behavior and accessible state APIs.
6. Exact Maven dependency versions from `pom.xml`.

### Needed to test close behavior completely

1. `IOContext.isResourceManaged()` setup mechanism.
2. A controllable `XMLStreamReader` or `XmlTokenStream` test double.
3. Whether a test reader can distinguish:
   - `closeCompletely()`
   - `close()`
   - normal close versus auto-close-source close.

### Needed to determine numeric expectations

1. API contract or existing tests defining numeric accessor behavior.
2. Whether the TODO methods are intentionally unsupported for this source version or are defects outside the reported bug scope.

---

## Conclusion

The supplied material is sufficient to plan broad parser, state, lifecycle, text, binary, and virtual-wrapping tests. It is also sufficient to identify Bug 180 as a regression involving empty elements in nested unwrapped lists, where list/item retention is the key observable outcome.

However, it is **not sufficient to generate a reliable, compilable, bug-specific JUnit regression test** that asserts the exact intended XML-to-object or XML-to-token behavior. The triggering test sources, test XML, model classes, annotations, and relevant mapper/factory construction details are missing.