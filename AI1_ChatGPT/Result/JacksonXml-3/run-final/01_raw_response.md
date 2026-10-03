## Scope and constraints

This analysis is limited to the supplied `JacksonXml-3b` source for:

- `com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser`
- The supplied project metadata and bug summary
- The named triggering test only (its source is not supplied)

No test class is generated, and no production code changes are proposed.

---

## 1. Public methods that should be tested

### Constructor

```java
public FromXmlParser(IOContext ctxt, int genericParserFeatures, int xmlFeatures,
        ObjectCodec codec, XMLStreamReader xmlReader)
```

This is required for all instance-level behavior. It initializes parsing state, codec, feature flags, root context, initial buffered token (`START_OBJECT`), and `XmlTokenStream`.

### Public configuration and codec methods

```java
public Version version()
public ObjectCodec getCodec()
public void setCodec(ObjectCodec c)
public void setXMLTextElementName(String name)

public FromXmlParser enable(Feature f)
public FromXmlParser disable(Feature f)
public final boolean isEnabled(Feature f)
public FromXmlParser configure(Feature f, boolean state)

public int getFormatFeatures()
public JsonParser overrideFormatFeatures(int values, int mask)

public void addVirtualWrapping(Set<String> namesToWrap)
```

### Public parser/state/lifecycle methods

```java
public boolean requiresCustomCodec()
public XMLStreamReader getStaxReader()

public String getCurrentName() throws IOException
public void overrideCurrentName(String name)

public void close() throws IOException
public boolean isClosed()
public XmlReadContext getParsingContext()

public JsonLocation getTokenLocation()
public JsonLocation getCurrentLocation()

public boolean isExpectedStartArrayToken()

public JsonToken nextToken() throws IOException
public String nextTextValue() throws IOException
```

### Public text/value accessors

```java
public String getText() throws IOException
public final String getValueAsString() throws IOException
public String getValueAsString(String defValue) throws IOException

public char[] getTextCharacters() throws IOException
public int getTextLength() throws IOException
public int getTextOffset() throws IOException
public boolean hasTextCharacters()
```

### Public binary/object accessors

```java
public Object getEmbeddedObject() throws IOException
public byte[] getBinaryValue(Base64Variant b64variant) throws IOException
```

### Public numeric accessors

```java
public BigInteger getBigIntegerValue() throws IOException
public BigDecimal getDecimalValue() throws IOException
public double getDoubleValue() throws IOException
public float getFloatValue() throws IOException
public int getIntValue() throws IOException
public long getLongValue() throws IOException
public NumberType getNumberType() throws IOException
public Number getNumberValue() throws IOException
```

The supplied source explicitly contains TODO implementations for all numeric accessors. Their present behavior is observable, but a reliable intended contract cannot be inferred from this prompt alone.

---

## 2. Input types and valid input ranges

| Area | Input | Source-level constraints / observable range |
|---|---|---|
| Construction | `IOContext ctxt` | Required in practice: constructor immediately calls `ctxt.getSourceReference()`. `null` causes `NullPointerException`. |
| Construction | `int genericParserFeatures` | Any `int`; forwarded to `ParserMinimalBase`. Relevant flags include inherited `JsonParser.Feature.AUTO_CLOSE_SOURCE`. |
| Construction | `int xmlFeatures` | Any bitmask `int`; stored in `_formatFeatures`. |
| Construction | `ObjectCodec codec` | No explicit non-null validation. May be set/replaced with `null`. |
| Construction | `XMLStreamReader xmlReader` | Passed to `XmlTokenStream`; null validity cannot be established without `XmlTokenStream` source. |
| Text-name configuration | `String name` | No validation. `null`, empty, whitespace, and normal names are all accepted by this method. The empty default is documented. |
| Format configuration | `Feature f` | `Feature` enum has **no constants** in this version. Normal callers cannot supply a non-null `Feature` instance. Passing `null` would dereference it and cause `NullPointerException`. |
| Feature override | `int values`, `int mask` | Any bit patterns; resulting value is `(_formatFeatures & ~mask) | (values & mask)`. |
| Virtual wrapping | `Set<String> namesToWrap` | Intended to contain local XML element names. No null check; if a current local name exists, a null set can cause `NullPointerException`. |
| Name override | `String name` | No validation; null and arbitrary strings are accepted by this method and delegated to `XmlReadContext`. |
| Binary decoding | `Base64Variant b64variant` | Expected non-null in normal use; null behavior depends on inherited base64 decoder and is not determinable here. |
| XML input | `XMLStreamReader` event stream | Must provide StAX events that `XmlTokenStream` maps to its internal XML event constants. Valid XML semantics and exact mapping require `XmlTokenStream` context. |

---

## 3. Conditions and reachable branches

## Constructor and basic configuration

The constructor always:

- sets `_formatFeatures` to `xmlFeatures`;
- stores `ctxt` and `codec`;
- creates a root `XmlReadContext`;
- buffers `JsonToken.START_OBJECT` in `_nextToken`;
- wraps the supplied `XMLStreamReader` in `XmlTokenStream`.

Therefore, the first `nextToken()` call is expected to return the initially buffered `START_OBJECT`, not consume XML input.

`requiresCustomCodec()` has one unconditional branch: it always returns `true`.

`getStaxReader()` has one branch: it returns the XML reader held by `_xmlTokens`.

`version()` returns `PackageVersion.VERSION`; the precise version value is external project metadata, but object identity/value can be compared to that static field.

## Format-feature methods

Because `Feature` is empty:

- `Feature.collectDefaults()` always returns `0`.
- Normal non-null calls to `enable`, `disable`, `isEnabled`, and `configure` cannot be constructed from this source version.
- `getFormatFeatures()` returns the current raw bitmask.
- `overrideFormatFeatures(values, mask)` has a fully determinable bitmask contract:
  - bits outside `mask` remain unchanged;
  - bits in `mask` are copied from `values`;
  - it returns `this`.

## `addVirtualWrapping`

Reachable branches:

1. Current XML local name is non-null and is contained in `namesToWrap`:
   - calls `_xmlTokens.repeatStartElement()`.
2. Current XML local name is null, or it is not contained:
   - does not repeat the start element.
3. In all non-exceptional cases:
   - stores `_namesToWrap`;
   - applies the set to the parsing context.

The exact external token sequence after `repeatStartElement()` requires `XmlTokenStream` behavior and test input.

## Current-name behavior

`getCurrentName()`:

- For current token `START_OBJECT` or `START_ARRAY`, gets the name from the **parent** parsing context.
- For all other token states, gets the name from the current parsing context.
- Throws `IllegalStateException` when the resulting name is null.

`overrideCurrentName()` follows the same “start marker uses parent context” rule and assigns the supplied name.

## Closing behavior

`close()` branches:

1. Parser not yet closed:
   - sets `_closed = true`;
   - uses `_xmlTokens.closeCompletely()` when either:
     - `IOContext.isResourceManaged()` is true, or
     - `JsonParser.Feature.AUTO_CLOSE_SOURCE` is enabled;
   - otherwise uses `_xmlTokens.close()`;
   - always invokes `_releaseBuffers()` in `finally`.
2. Parser already closed:
   - no further close action.

Observable behavior of the underlying StAX reader after `close()` depends on `XmlTokenStream`, `IOContext`, and the XML reader implementation.

## `isExpectedStartArrayToken`

Branches:

1. Current token is `START_OBJECT`:
   - changes it to `START_ARRAY`;
   - converts current parsing context to array;
   - converts a buffered `END_OBJECT` to `END_ARRAY`, otherwise clears `_nextToken`;
   - skips XML attributes;
   - returns `true`.
2. Current token is already `START_ARRAY`:
   - returns `true` without conversion.
3. Any other current token:
   - returns `false`.

This should be tested with:
- a start element that is converted to an array;
- an already-array state;
- a non-start token;
- a start object with pending `END_OBJECT` if a reproducible XML path reaches that state.

## `nextToken()`

Important branches, based on `XmlTokenStream` events:

### Buffered `_nextToken`

When `_nextToken != null`, it returns that token and updates state:

- `START_OBJECT` → creates child object context.
- `START_ARRAY` → creates child array context.
- `END_OBJECT` / `END_ARRAY` → returns to parent context and restores `_namesToWrap`.
- `FIELD_NAME` → updates current name from `_xmlTokens.getLocalName()`.
- value tokens → no context update.

### XML start-element processing

For `XML_START_ELEMENT`:

1. `_mayBeLeaf == true`:
   - recognizes nested structure instead of a simple leaf;
   - buffers `FIELD_NAME`;
   - enters child object context;
   - returns `START_OBJECT`.

2. Current parsing context is an array:
   - suppresses reporting the XML element name;
   - reads another XML token;
   - sets `_mayBeLeaf = true`;
   - loops.

3. Normal object/root context:
   - records local element name as current name;
   - repeats start element if virtual wrapping applies;
   - sets `_mayBeLeaf = true`;
   - returns `FIELD_NAME`.

### XML end element

For `XML_END_ELEMENT`:

1. `_mayBeLeaf == true`:
   - marks leaf handling complete;
   - in array context, returns `START_OBJECT`, buffers `END_OBJECT`, and enters a child object context;
   - otherwise returns `VALUE_NULL`.

2. `_mayBeLeaf == false`:
   - returns `END_ARRAY` if in array context, otherwise `END_OBJECT`;
   - moves to parent parsing context;
   - restores `_namesToWrap`.

### XML attribute name/value

For `XML_ATTRIBUTE_NAME`:

1. `_mayBeLeaf == true`:
   - an attribute prevents interpretation as a simple leaf;
   - buffers `FIELD_NAME`;
   - stores current XML text in `_currText`;
   - enters a child object context;
   - returns `START_OBJECT`.

2. `_mayBeLeaf == false`:
   - sets current name to the attribute local name;
   - returns `FIELD_NAME`.

For `XML_ATTRIBUTE_VALUE`:

- sets `_currText`;
- returns `VALUE_STRING`.

### XML text

For `XML_TEXT`:

1. `_mayBeLeaf == true`:
   - marks leaf handling complete;
   - skips the following end element;
   - for an array plus empty/whitespace text:
     - returns `START_OBJECT`;
     - buffers `END_OBJECT`;
     - enters child object context.
   - otherwise returns `VALUE_STRING`.

2. `_mayBeLeaf == false` and parser context is object:
   - if prior token is not `FIELD_NAME` and text is empty/whitespace:
     - obtains the next XML event and continues looping.
   - otherwise:
     - exposes text as a synthetic field named `_cfgNameForTextElement`;
     - buffers `VALUE_STRING`;
     - returns `FIELD_NAME`.

### XML end-of-input

For `XML_END`, it sets `_currToken` to `null` and returns `null`.

## `nextTextValue()`

This method is central to the supplied bug report.

It has a separate implementation instead of delegating to `nextToken()`. Its behavior differs in several significant ways:

- If `_nextToken` is `VALUE_STRING`, it returns `_currText`.
- If `_nextToken` is another token, it updates state and returns `null`.
- For an empty XML element detected as a leaf, it returns `""` and sets `VALUE_STRING`. This deliberately differs from `nextToken()`, which emits `VALUE_NULL` for that non-array case.
- For leaf XML text, it returns text immediately.
- For attributes and nested structures, it may set a token but returns `null`.
- Unlike `nextToken()`, it does not apply the empty-text-in-array workaround.
- For non-leaf XML text, it exposes a synthetic field and buffers `VALUE_STRING`, but the immediate `nextTextValue()` call returns `null`.

Relevant `nextTextValue()` event branches:

| XML/internal state | Result |
|---|---|
| Buffered `VALUE_STRING` | Returns `_currText` |
| Buffered non-string token | Updates state and returns `null` |
| Start element under potential leaf | Returns `null`, current token becomes `START_OBJECT`, buffered `FIELD_NAME` |
| Start element in array | Suppresses element name and continues |
| Normal start element | Current token `FIELD_NAME`, returns `null` |
| Empty leaf end element | Current token `VALUE_STRING`, returns `""` |
| Non-leaf end element | Current token `END_OBJECT` or `END_ARRAY`, returns `null` |
| Attribute name while potential leaf | Current token `START_OBJECT`, buffered `FIELD_NAME`, returns `null` |
| Attribute name otherwise | Current token `FIELD_NAME`, returns `null` |
| Attribute value | Current token `VALUE_STRING`, but method returns `null` |
| Leaf XML text | Returns the text |
| Non-leaf XML text | Current token `FIELD_NAME`, buffered `VALUE_STRING`, returns `null` |
| XML end | Current token `null`, returns `null` |

## Text/value methods

`getText()` branches:

- current token `null` → `null`;
- `FIELD_NAME` → current field name;
- `VALUE_STRING` → `_currText`;
- all other tokens → `JsonToken.asString()`.

`getValueAsString(String defValue)` branches:

- current token `null` → `null`, not `defValue`;
- `FIELD_NAME` → current name;
- `VALUE_STRING` → `_currText`;
- `START_OBJECT`:
  - attempts `_xmlTokens.convertToString()`;
  - if conversion succeeds:
    - changes current token to `VALUE_STRING`;
    - rolls parsing context back to parent;
    - clears buffered next token;
    - skips the matching end element;
    - returns converted text;
  - if conversion fails → `null`, not `defValue`;
- scalar token → token’s string form;
- otherwise → `defValue`.

`getTextCharacters()`, `getTextLength()`, and `getTextOffset()` have straightforward derivation from `getText()`:

- null text → null character array and length 0;
- otherwise character copy and exact string length;
- offset always 0.
- `hasTextCharacters()` always returns false.

## Binary access

`getBinaryValue(Base64Variant)`:

1. If current token is not `VALUE_STRING`, and is not `VALUE_EMBEDDED_OBJECT` with cached binary bytes:
   - reports an error via inherited `_reportError`.
2. If `_binaryValue` is absent:
   - decodes current text as Base64;
   - wraps `IllegalArgumentException` from decoding as a parser error.
3. If `_binaryValue` is already cached:
   - returns the same cached byte array without decoding again.

`_binaryValue` is reset to null at the beginning of both `nextToken()` and `nextTextValue()`.

## Numeric accessors

All numeric methods are stubs in the supplied version:

| Method | Current source behavior |
|---|---|
| `getBigIntegerValue()` | returns `null` |
| `getDecimalValue()` | returns `null` |
| `getDoubleValue()` | returns `0` |
| `getFloatValue()` | returns `0` |
| `getIntValue()` | returns `0` |
| `getLongValue()` | returns `0` |
| `getNumberType()` | returns `null` |
| `getNumberValue()` | returns `null` |

There is no supplied specification indicating whether these results are intentional, unfinished, or defects. Tests may document current behavior, but should not treat it as a reliable correctness oracle.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

## Normal cases

Meaningful normal XML cases include:

1. Simple leaf:
   ```xml
   <root>text</root>
   ```
   Relevant for `FIELD_NAME`, `VALUE_STRING`, `nextTextValue()`, `getText()`, and text accessors.

2. Empty element:
   ```xml
   <root/>
   ```
   Relevant for the intentional difference:
   - `nextToken()` path emits `VALUE_NULL` for a leaf empty element outside an array;
   - `nextTextValue()` path produces `""`.

3. Element with attribute:
   ```xml
   <root attr="7"/>
   ```
   Relevant to Bug 204 and the distinction between start-element, attribute-name, and attribute-value handling.

4. Element with attribute and text:
   ```xml
   <root attr="a">text</root>
   ```
   Relevant to synthetic text property behavior and configured text-element name.

5. Nested child element:
   ```xml
   <root><child>value</child></root>
   ```
   Relevant to `_mayBeLeaf`, nested object context, start/end object sequence, and current names.

6. Repeated child elements / arrays:
   ```xml
   <root><item>a</item><item>b</item></root>
   ```
   Relevant when combined with `isExpectedStartArrayToken()` and/or virtual wrapping.

7. Base64 text:
   ```xml
   <root>SGVsbG8=</root>
   ```
   Relevant to valid binary decoding and decode caching.

## Boundary cases

- Empty XML element and whitespace-only element:
  ```xml
  <root/>
  <root>   </root>
  ```
- Empty string configured as text-property name (the documented default).
- Alternative text-property name, e.g. `"value"`.
- Empty set and populated set for virtual wrapping.
- `overrideFormatFeatures` with:
  - `mask == 0`;
  - all bits masked (`mask == -1`);
  - values containing both masked and unmasked bits.
- Multiple `close()` calls.
- Calls to `getText()`, `getValueAsString()`, and text-length methods before any token has been read and after end-of-input.

## Invalid or malformed input

The exact exception type and timing for malformed XML cannot be reliably specified from this source alone because XML event production is delegated to `XMLStreamReader` and `XmlTokenStream`.

Potential cases:

- malformed XML;
- incomplete XML with unclosed elements;
- StAX reader failure during `next()`;
- invalid Base64 data while current token is `VALUE_STRING`.

For invalid Base64, the source explicitly intends a parser error generated through `_constructError(...)`, but the concrete exception class/message details depend on inherited Jackson code.

## Null cases

| Input / state | Observable source-level result |
|---|---|
| `ctxt == null` constructor argument | Immediate `NullPointerException` from `ctxt.getSourceReference()`. |
| `codec == null` | Accepted by constructor/setter; `getCodec()` returns null. |
| `setCodec(null)` | Accepted; subsequent `getCodec()` returns null. |
| `setXMLTextElementName(null)` | Accepted. Later non-leaf text handling sets a null current name; downstream `getCurrentName()` may throw `IllegalStateException`. Exact parser usability after this setting should not be assumed correct. |
| `enable(null)`, `disable(null)`, `isEnabled(null)`, `configure(null, ...)` | `NullPointerException` due to `f.getMask()`. |
| `addVirtualWrapping(null)` | May throw `NullPointerException` when current local name is non-null because it evaluates `namesToWrap.contains(name)`; if current local name is null, it stores null and does not immediately fail. |
| `overrideCurrentName(null)` | No validation in this class; later `getCurrentName()` can throw `IllegalStateException`. |
| `getValueAsString(defValue)` with null current token | returns null, even when `defValue` is non-null. |
| `getBinaryValue(null)` | Insufficient context to state exact outcome reliably. |

## Exceptional cases

- `getCurrentName()` throws `IllegalStateException` when no current name is available.
- `getBinaryValue()` reports an error when invoked on a non-string/non-cached-embedded token.
- Invalid Base64 is converted from `IllegalArgumentException` to a Jackson parsing error.
- `close()` and token iteration may throw `IOException`.
- `_handleEOF()` reports invalid EOF if parsing context is not root; this protected method is normally reached through inherited parser EOF handling. A direct test would require a test subclass or a parser path that triggers it.
- Delegated StAX/`XmlTokenStream` exceptions are not specified by the supplied code.

---

## 5. Required constructors, dependencies, and external objects

To construct and test `FromXmlParser` directly, tests need:

1. **`IOContext`**
   - Required and non-null.
   - Its `getSourceReference()` is used in construction.
   - Its `isResourceManaged()` controls `close()` behavior.
   - A suitable instance may require Jackson core buffer-recycler/context setup, which is not supplied.

2. **`XMLStreamReader`**
   - Required to provide XML events.
   - Typically created by a StAX `XMLInputFactory` from XML text.
   - The exact implementation affects external close behavior and possibly event behavior.

3. **`ObjectCodec`**
   - Can be `null` for parser methods that do not use it.
   - For normal XML mapper integration, `XmlMapper` is the documented intended codec.
   - A real `XmlMapper` may be useful for integration-level construction if factory APIs are available in this project version, but those APIs are not supplied here.

4. **Jackson core types**
   - `JsonToken`, `JsonParser.Feature`, `Base64Variant`, `JsonLocation`, `ObjectCodec`, and `IOContext`.
   - These come from Jackson dependencies not enumerated in the prompt.

5. **`XmlTokenStream` and `XmlReadContext`**
   - Created/used internally.
   - Their source is not supplied, which limits exact expected token sequences for some behavior.

6. **Optional test subclass**
   - Only needed to expose protected behavior such as `_isEmpty`, `_getByteArrayBuilder`, `_decodeBase64`, or `_handleEOF`.
   - Such a subclass is technically possible, but whether it is appropriate depends on the intended test scope. Public behavior should be preferred.

---

## 6. JUnit version and build tool

Supplied project context specifies:

- **JUnit:** `junit-4.12.jar`
- **Build tool:** Maven

Therefore, eventual tests should use JUnit 4 style, such as:

```java
import org.junit.Test;
import static org.junit.Assert.*;
```

No Maven `pom.xml`, source roots, dependency coordinates, Surefire configuration, Java version, or existing test helper classes were supplied. Those details are needed to ensure a generated test compiles in the actual project layout.

---

## 7. Available test oracle

### Supplied oracle information

The available bug oracle is:

- Bug report: **Jackson XML issue 204**
- Fixed revision: `79a4b57f2bbe08ce46e6dabb0a8b76f4a787141c`
- Triggering test:
  ```text
  com.fasterxml.jackson.dataformat.xml.stream.XmlParserNextXxxTest
      ::testXmlAttributesWithNextTextValue
  ```
- Failure:
  ```text
  junit.framework.ComparisonFailure: expected:<7> but was:<null>
  ```

This establishes a reliable high-level expected behavior:

> In the triggering XML-attribute scenario, `nextTextValue()` is expected to return `"7"` rather than `null`.

### Other source-based oracles

The class documentation provides reliable expectations for:

- `DEFAULT_UNNAMED_TEXT_PROPERTY` equals `""`.
- `setXMLTextElementName` changes the pseudo-property name used for XML text.
- `requiresCustomCodec()` must return true.
- `isExpectedStartArrayToken()` converts a current XML-derived object start into an array start.
- `nextTextValue()` returns `""` for an empty leaf element, explicitly differing from `nextToken()`.
- `getTextOffset()` returns 0.
- `hasTextCharacters()` returns false.
- `getEmbeddedObject()` returns null.
- `getFormatFeatures()` and `overrideFormatFeatures()` can be asserted based on their explicit implementation.

### Insufficient oracle areas

A reliable intended expected result is not available in this prompt for:

- Numeric accessor semantics.
- Exact token stream behavior for all XML shapes, because `XmlTokenStream` is not supplied.
- Precise close behavior of underlying XML reader.
- Exact exceptions/messages from malformed XML or null `XMLStreamReader`.
- Exact behavior and intended API usage of virtual wrapping.
- `getValueAsString()` conversion behavior because `XmlTokenStream.convertToString()` is not supplied.

Tests in these areas should not invent expected semantics beyond what the supplied code and documentation establish.

---

## 8. Bug-report-related behaviors that should be tested

The reported regression specifically concerns XML attributes combined with `nextTextValue()`.

### Essential regression scenario

A test must reproduce the call sequence used by:

```text
XmlParserNextXxxTest::testXmlAttributesWithNextTextValue
```

and assert that the relevant call to:

```java
parser.nextTextValue()
```

returns:

```java
"7"
```

rather than `null`.

The likely XML input includes an attribute whose value is `7`, but the exact XML document and exact prior parser calls are not supplied. The failure message alone does not establish whether the XML was, for example:

```xml
<root attr="7"/>
```

or a nested variation, nor whether `nextTextValue()` was called immediately after a `FIELD_NAME`, after a `START_OBJECT`, or in a loop.

### Related scenarios worth covering once exact triggering sequence is known

1. **Attribute value accessible through `nextTextValue()`**
   - Verify returned text equals the attribute value.
   - Verify current token/state is consistent with the established API behavior.

2. **Multiple attributes**
   - Ensure consecutive attribute name/value handling does not lose values or return stale `_currText`.

3. **Attribute followed by element text**
   - Confirm the attribute value and synthetic text-property paths remain distinct.

4. **Attribute and nested child element**
   - Ensure `_mayBeLeaf` transition to object structure does not suppress attribute values.

5. **Use of `nextToken()` versus `nextTextValue()`**
   - The bug is specifically in the specialized method; tests should prevent accidental coverage only through `nextToken()`.

6. **Post-attribute continuation**
   - Verify subsequent tokens after the attribute remain correct, particularly the end-object/end-element handling.

The exact expected token sequence for these additional cases should be derived from the missing triggering-test source or from existing project tests, not invented.

---

## 9. Missing context required for compilable and meaningful tests

The supplied information is insufficient to produce a fully reliable, compilable regression test without risk of guessing. The main missing context is:

1. **Source of the triggering test**
   ```text
   XmlParserNextXxxTest::testXmlAttributesWithNextTextValue
   ```
   This is the most important missing item. It would provide:
   - exact XML input;
   - parser construction approach;
   - exact call sequence;
   - expected tokens and assertions;
   - any existing helper methods or base test classes.

2. **`XmlTokenStream` implementation**
   Needed to establish:
   - mapping from StAX events to `XML_*` constants;
   - semantics of `getText()` when at attribute-name events;
   - behavior of `skipEndElement()`;
   - behavior of `repeatStartElement()`;
   - `convertToString()` behavior;
   - close behavior.

3. **`XmlReadContext` implementation**
   Needed for exact expectations about:
   - parent/current-name transitions;
   - array conversion;
   - virtual wrapping state;
   - start locations.

4. **Maven project configuration**
   Needed for test compilation:
   - module/source layout;
   - exact Jackson core and StAX dependency versions;
   - available XML parser implementation;
   - test source roots;
   - existing test utility classes.

5. **Existing parser test conventions**
   The project may provide helpers to create an `XmlMapper`, factory, parser, and `IOContext`. Using those conventions is preferable to hand-constructing internal Jackson classes.

6. **Bug report content or fixed-source diff**
   The prompt gives only the issue identifier, failure summary, and fixed revision—not the issue discussion, fixed implementation, or test source. Without one of those, the exact minimal reproducer cannot be determined reliably.

---

## Conclusion

The primary test target is `nextTextValue()` in an attribute-processing sequence: the supplied bug oracle requires that the relevant call return `"7"` rather than `null`.

A meaningful test suite can also cover parser construction, first-token behavior, XML leaf/empty/nested structures, text accessors, array conversion, binary decoding, closing, and format-bitmask operations. However, tests requiring exact XML token sequencing—especially the Bug 204 regression test—need the missing triggering test source or equivalent existing project context to avoid inventing XML input and parser-call order.