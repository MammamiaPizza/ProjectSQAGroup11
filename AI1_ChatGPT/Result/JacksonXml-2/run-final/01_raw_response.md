## 1. Public methods to test

`XmlTokenStream` exposes the following public API:

| Method / constructor | Test relevance |
|---|---|
| `XmlTokenStream(XMLStreamReader xmlReader, Object sourceRef)` | Primary construction path; validates initial StAX reader position and initializes element/attribute state. |
| `XMLStreamReader2 getXmlReader()` | Should return the adapted/wrapped StAX2 reader. |
| `int next()` | Core method. Produces this class’s XML token stream and contains the bug-relevant mixed-content behavior. |
| `void skipEndElement()` | Calls `next()` and requires the next token to be `XML_END_ELEMENT`. |
| `int getCurrentToken()` | Reports internal current token state. |
| `String getText()` | Reports current/stored text value. |
| `String getLocalName()` | Reports current element or attribute local name. |
| `String getNamespaceURI()` | Reports current element or attribute namespace URI. |
| `boolean hasAttributes()` | Indicates whether the current token is a start element with attributes. |
| `void closeCompletely()` | Delegates to `XMLStreamReader2.closeCompletely()`, converting `XMLStreamException` to `IOException`. |
| `void close()` | Delegates to `XMLStreamReader2.close()`, converting `XMLStreamException` to `IOException`. |
| `JsonLocation getCurrentLocation()` | Builds a Jackson `JsonLocation` from the StAX current location. |
| `JsonLocation getTokenLocation()` | Builds a Jackson `JsonLocation` from the StAX start/token location. |
| `String toString()` | Diagnostic representation of current internal state. |

The class also has protected methods:

- `repeatStartElement()`
- `skipAttributes()`
- `convertToString()`
- `_handleRepeatElement()`

These are not public API, but they control virtual-wrapper behavior. They may be tested indirectly through a subclass only if tests need to cover protected behavior. The supplied triggering test does not establish that such direct coverage is required.

---

## 2. Input types and valid input ranges

### Constructor inputs

#### `XMLStreamReader xmlReader`

Required non-null StAX reader.

The constructor requires that:

```java
xmlReader.getEventType() == XMLStreamConstants.START_ELEMENT
```

Therefore, valid construction requires a reader positioned at a start-element event, such as after advancing a normal `XMLStreamReader` to its first `START_ELEMENT`.

Examples of valid XML shapes include:

- Empty element: `<root/>`
- Element with text: `<root>text</root>`
- Element with attributes: `<root attr="value"/>`
- Nested content: `<root><child/></root>`
- CDATA: `<root><![CDATA[text]]></root>`
- Namespaced elements and attributes.
- Mixed content: `<root>text<child/>more text</root>`

A reader at `START_DOCUMENT`, `END_ELEMENT`, `CHARACTERS`, `END_DOCUMENT`, and similar states is invalid for construction and should cause `IllegalArgumentException`.

A `null` `xmlReader` is not explicitly validated. The constructor calls `xmlReader.getEventType()` immediately, so `null` currently produces `NullPointerException`.

#### `Object sourceRef`

No constraints are imposed by the class. It is retained and used as the source reference in returned `JsonLocation` instances.

`null` appears acceptable because it is passed directly to `new JsonLocation(...)`.

---

## 3. Conditions and reachable branches

### Core normal token progression

The class defines its own token constants:

| Token | Value |
|---|---:|
| `XML_START_ELEMENT` | 1 |
| `XML_END_ELEMENT` | 2 |
| `XML_ATTRIBUTE_NAME` | 3 |
| `XML_ATTRIBUTE_VALUE` | 4 |
| `XML_TEXT` | 5 |
| `XML_END` | 6 |

At construction:

- Current token/state is `XML_START_ELEMENT`.
- Local name and namespace are taken from the initial StAX start element.
- Attribute count is read from the initial element.

### `next()` branches

#### A. Virtual replay branch

```java
if (_repeatElement != 0) {
    return (_currentState = _handleRepeatElement());
}
```

This is only reachable after protected virtual-wrapping operations such as `repeatStartElement()`, or after the internal wrapper logic schedules replay.

Without subclass access to `repeatStartElement()`, ordinary parsing of XML does not appear to initiate wrapping.

#### B. Attribute handling

When the current state is `XML_START_ELEMENT` or `XML_ATTRIBUTE_VALUE`, and unreturned attributes remain:

1. `next()` returns `XML_ATTRIBUTE_NAME`.
2. A following `next()` returns `XML_ATTRIBUTE_VALUE`.
3. The process repeats for each attribute.
4. After the last attribute value, `next()` proceeds into element content.

For each attribute:

- `getLocalName()` becomes the attribute local name.
- `getNamespaceURI()` becomes the attribute namespace.
- `getText()` becomes the attribute value.

#### C. Text and CDATA collection

`_collectUntilTag()` advances the underlying reader until it reaches:

- `START_ELEMENT`
- `END_ELEMENT`
- `END_DOCUMENT`

It accumulates content from:

- `XMLStreamConstants.CHARACTERS`
- `XMLStreamConstants.CDATA`

It ignores:

- comments,
- processing instructions,
- other StAX event types,
- `SPACE` events explicitly, because only `CHARACTERS` and `CDATA` are appended.

If multiple adjacent `CHARACTERS` or CDATA events occur, their text is concatenated.

#### D. Nested child start element

From a start element after attributes are exhausted, `_collectUntilTag()` may reach another `START_ELEMENT`.

Current behavior is:

```java
if (_xmlReader.getEventType() == XMLStreamReader.START_ELEMENT) {
    return _initStartElement();
}
```

This returns the nested child start token.

Critically, the collected text is not emitted in this branch, even if non-null. This is directly relevant to the supplied mixed-content bug.

#### E. Text before an end element

If collected text is followed by `END_ELEMENT`, `next()` emits:

```java
XML_TEXT
```

and stores the text in `_textValue`.

The following `next()` from `XML_TEXT` returns an end-element token through `_handleEndElement()`.

#### F. End-element handling

If no text is collected before an end element, `_handleEndElement()` returns `XML_END_ELEMENT`.

The actual local name and namespace remain those previously set in certain ordinary paths; for simple parsing, the name generally corresponds to the current element. Wrapper behavior may adjust it.

#### G. End of document

When `_skipUntilTag()` encounters `END_DOCUMENT`, `next()` returns `XML_END`.

When already in `XML_END`, subsequent `next()` calls return `XML_END` again rather than throwing.

#### H. Skipping irrelevant StAX events

`_skipUntilTag()` skips events until it sees a start element, end element, or end document. This covers comments, processing instructions, and similar non-tag events.

### `hasAttributes()` branches

Returns `true` only when both conditions hold:

```java
_currentState == XML_START_ELEMENT
_attributeCount > 0
```

Consequently:

- True immediately after construction for an initial element with at least one attribute.
- False for an element with no attributes.
- False after the first attribute-name token has been returned, because current state is then `XML_ATTRIBUTE_NAME`.
- False for text/end/end-document states.

### `skipEndElement()` branches

- Succeeds if `next()` returns `XML_END_ELEMENT`.
- Throws `IOException` for every other token type, including `XML_TEXT`, `XML_START_ELEMENT`, attribute tokens, or `XML_END`.

### Location extraction branches

`_extractLocation(XMLStreamLocation2 location)` has two paths:

- Non-null location: creates `JsonLocation` from offset, line, and column.
- Null location: creates `JsonLocation(sourceRef, -1, -1, -1)`.

The class does not check whether `_xmlReader.getLocationInfo()` itself is null.

### Protected virtual-wrapper branches

The supplied class contains branches for virtual wrapping:

- `REPLAY_START_DUP`
- `REPLAY_END`
- `REPLAY_START_DELAYED`
- matching and non-matching `ElementWrapper` transitions
- delayed restoration of a start-element name after an injected end element

Their exact externally intended behavior cannot be fully established because the implementation of `ElementWrapper` is not supplied.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

### Normal cases

Tests should eventually cover at least:

1. **Initial start element**
   - Construct on `<root/>`.
   - Current token is `XML_START_ELEMENT`.
   - Local name is `"root"`.
   - Namespace is appropriate for the reader/input.
   - `hasAttributes()` is false.

2. **Empty element**
   - `<root/>`
   - Expected progression after construction: `XML_END_ELEMENT`, then `XML_END`.

3. **Element with one attribute**
   - `<root attr="value"/>`
   - Expected token progression: start element, attribute name, attribute value, end element, end.
   - Validate attribute name and value exposure.

4. **Multiple attributes**
   - Ensure attributes are returned in the underlying reader’s attribute-index order.
   - This ordering is a StAX reader behavior; the class processes index `0..attributeCount-1`.

5. **Plain text**
   - `<root>text</root>`
   - Expected progression: start element, text, end element, end.
   - `getText()` should return `"text"` when the text token is current.

6. **CDATA**
   - `<root><![CDATA[text]]></root>`
   - CDATA contributes to `XML_TEXT`.

7. **Concatenated textual StAX events**
   - Adjacent text and CDATA should be concatenated by `_collectUntilTag()`.

8. **Nested elements without text**
   - `<root><child/></root>`
   - Expected nested start/end transitions.

9. **Comments and processing instructions**
   - They are skipped by `_collectUntilTag()` / `_skipUntilTag()` and should not create custom tokens.

10. **Namespaces**
   - Element and attribute namespace URI should be exposed through `getNamespaceURI()`.

11. **Location methods**
   - Verify that the source reference is preserved and location values correspond to the StAX implementation used, if deterministic for the selected parser.

12. **Close delegation**
   - `close()` and `closeCompletely()` should invoke the appropriate underlying reader method.

### Boundary cases

1. **Zero attributes versus one attribute**
   - Exercises `hasAttributes()` and the attribute-state boundary.

2. **Empty textual content**
   - `<root></root>` and `<root/>`.
   - No `XML_TEXT` should be emitted by the shown ordinary `next()` logic when no characters/CDATA are collected.

3. **Whitespace-only textual content**
   - The comment says “no/all-whitespace text followed by START_ELEMENT, ignore text,” but the implementation does not explicitly test for whitespace.
   - `CHARACTERS` content, including whitespace delivered as `CHARACTERS`, is accumulated.
   - The behavior can vary according to which StAX event type is produced (`CHARACTERS` versus `SPACE`), so a test oracle needs a concrete parser configuration.

4. **Repeated `next()` after `XML_END`**
   - Should continue returning `XML_END`.

5. **`skipEndElement()` at the immediate end of an empty element**
   - Should succeed.

### Invalid cases

1. **Reader not positioned at `START_ELEMENT`**
   - Constructor should throw `IllegalArgumentException`.
   - Examples: `START_DOCUMENT`, `CHARACTERS`, `END_ELEMENT`, or `END_DOCUMENT`.

2. **`skipEndElement()` when next token is not an end element**
   - Should throw `IOException`.

3. **Protected `repeatStartElement()` invoked in a non-start state**
   - Throws `IllegalStateException`.
   - Requires a test subclass to expose the protected method.

4. **Protected `skipAttributes()` in unsupported states**
   - Throws `IllegalStateException`.
   - Requires a test subclass.

5. **Unexpected end in `_skipUntilTag()`**
   - Throws `IllegalStateException` if `hasNext()` becomes false without encountering a tag/end-document event.
   - This likely requires a custom/mocked reader because well-formed normal StAX parsing should reach `END_DOCUMENT`.

### Null cases

1. **`null` reader passed to constructor**
   - Current implementation throws `NullPointerException`.
   - This is implementation-observable but not documented as an intended API contract.

2. **`null` source reference**
   - Appears supported; resulting `JsonLocation` should retain a null source reference.

3. **Null location returned from `XMLStreamLocation2`**
   - Explicitly handled: location fields become `-1`.

4. **Null `getLocationInfo()`**
   - Not handled; calling either location accessor would throw `NullPointerException`.
   - Whether this scenario is relevant depends on the actual `XMLStreamReader2` implementation or mock.

### Exceptional cases

1. **`XMLStreamException` from parsing**
   - `next()` catches it and delegates conversion to `StaxUtil.throwXmlAsIOException(e)`.
   - Expected visible result is an `IOException`, assuming `StaxUtil` performs that conversion as implied by the method name.

2. **`XMLStreamException` from `close()` / `closeCompletely()`**
   - Also converted through `StaxUtil.throwXmlAsIOException(e)`.

3. **Failure behavior of `StaxUtil.throwXmlAsIOException`**
   - Exact exception message, cause preservation, and subtype behavior cannot be confirmed because `StaxUtil` source is not supplied.

---

## 5. Required constructors, dependencies, and external objects

### Production dependencies used directly

- `javax.xml.stream.XMLStreamReader`
- `javax.xml.stream.XMLStreamConstants`
- `javax.xml.stream.XMLStreamException`
- `org.codehaus.stax2.XMLStreamReader2`
- `org.codehaus.stax2.XMLStreamLocation2`
- `org.codehaus.stax2.ri.Stax2ReaderAdapter`
- `com.fasterxml.jackson.core.JsonLocation`
- `com.fasterxml.jackson.dataformat.xml.util.StaxUtil`
- `com.fasterxml.jackson.dataformat.xml.deser.ElementWrapper`

### Construction requirements for normal tests

A meaningful parser-based test needs:

1. An `XMLInputFactory`.
2. A real `XMLStreamReader` created from known XML input.
3. The reader advanced to its first `START_ELEMENT`.
4. Construction of `XmlTokenStream` using that reader and a source-reference object.

`Stax2ReaderAdapter.wrapIfNecessary(xmlReader)` adapts the supplied StAX reader to `XMLStreamReader2`.

### Dependencies needed for exceptional-path tests

To deterministically test XML exceptions, null locations, or close delegation/failure paths, tests may require one of:

- an `XMLStreamReader` / `XMLStreamReader2` mock;
- a custom stub implementation;
- a project-supported mocking library, if one exists in the Maven dependencies.

No mocking library or existing test utility is supplied in this prompt, so it cannot be assumed.

### Protected-method test access

Testing `repeatStartElement()`, `skipAttributes()`, or `convertToString()` directly would require a subclass of `XmlTokenStream` that exposes controlled wrapper methods. Such a subclass can be defined in test code, but it should only be used if protected behavior is in scope.

---

## 6. JUnit version and build tool

Supplied project metadata states:

- **JUnit version:** `junit-4.12.jar`
- **Build tool:** Maven

Therefore, any eventual test class should use JUnit 4 conventions, such as:

- `org.junit.Test`
- `org.junit.Assert.*`
- JUnit 4 exception assertions (`@Test(expected = ...)`) or explicit `try/catch` assertions.

No JUnit test class is generated here, per the requirement.

---

## 7. Available test oracles

The supplied material provides these possible oracles:

1. **The target class implementation**
   - It establishes observable token constants, state progression, validation behavior, and exception types for many scenarios.
   - It must not be treated as necessarily correct, especially for bug-related behavior.

2. **The class-level documentation**
   - Describes a flattened token stream that omits “fluff” tokens.
   - It explicitly mentions “mixed content” among omitted details. However, the bug report demonstrates that the expected behavior for the reported scenario differs from the current defective behavior, so this documentation alone is not a reliable oracle for mixed-content semantics.

3. **Bug report metadata**
   - Bug report ID: `196`
   - Triggering test: `com.fasterxml.jackson.dataformat.xml.misc.XmlTextTest::testMixedContent`
   - Failure: `expected:<27> but was:<0>`

4. **Modified-source information**
   - Only `XmlTokenStream` was modified for this bug.
   - This strongly associates the reported mixed-content regression with this class.

### Oracle limitations

The actual source of:

```text
com.fasterxml.jackson.dataformat.xml.misc.XmlTextTest::testMixedContent
```

is not supplied.

The prompt also does not provide:

- the XML document used by the triggering test;
- the API call that produced `27` versus `0`;
- the fixed implementation/diff;
- the GitHub issue text;
- a precise expected token sequence for mixed content.

Therefore, the supplied failure value alone is insufficient to create a reliable assertion for the exact intended mixed-content behavior.

---

## 8. Bug-report-related behaviors that should be tested

The reported failure is:

```text
XmlTextTest::testMixedContent
expected:<27> but was:<0>
```

The bug is specifically associated with `XmlTokenStream`.

### Observable defect-relevant behavior in the supplied source

In `_next()`, when processing a start element after attributes, the class calls `_collectUntilTag()`.

If text is encountered and then a child start element is encountered, this branch executes:

```java
if (_xmlReader.getEventType() == XMLStreamReader.START_ELEMENT) {
    return _initStartElement();
}
```

The locally collected `text` is discarded in this case.

For XML structurally resembling:

```xml
<root>text-before-child<child/>text-after-child</root>
```

the current code can discard text preceding a nested child, because it sees the child `START_ELEMENT` and returns the child start token instead of an `XML_TEXT` token.

Likewise, text appearing after a nested child can be skipped by `_skipUntilTag()` while advancing from the child end to the parent end, because `_skipUntilTag()` ignores all non-tag events, including character events.

### Bug-focused test scenarios to define once the intended contract is available

1. **Text before a child element**
   ```xml
   <root>prefix<child/></root>
   ```
   Determine whether `prefix` must be preserved and where it should appear in the consumer-visible result/token stream.

2. **Text after a child element**
   ```xml
   <root><child/>suffix</root>
   ```
   Determine whether `suffix` must be preserved.

3. **Text on both sides of a child**
   ```xml
   <root>prefix<child/>suffix</root>
   ```
   This is the strongest direct mixed-content scenario.

4. **Multiple child elements interspersed with text**
   ```xml
   <root>a<child1/>b<child2/>c</root>
   ```

5. **CDATA mixed with child elements**
   ```xml
   <root><![CDATA[a]]><child/><![CDATA[b]]></root>
   ```

6. **Mixed content with comments or processing instructions**
   ```xml
   <root>a<!-- comment --><child/><?pi value?>b</root>
   ```
   The intended preservation/discarding behavior for textual segments must be known.

7. **Integration behavior through the XML mapper**
   - The supplied triggering test is named `XmlTextTest`, and the assertion is `27` versus `0`, which likely concerns a deserialized or serialized value rather than directly asserting `XmlTokenStream` token constants.
   - A meaningful regression test may need to use the project’s XML mapper API and the exact test model/input from the missing `testMixedContent`.

### Important limitation

It cannot be reliably inferred from this prompt whether the correct low-level contract should be:

- emit separate `XML_TEXT` tokens around child elements;
- concatenate all mixed-content text;
- associate text with a particular element/property;
- ignore some text but preserve text used by the mapper;
- deserialize mixed content to a numeric/string value in a specific model.

The failure value `27` indicates that a value expected by the triggering test was lost or became `0`, but it does not establish the required token sequence or mapping semantics.

---

## 9. Missing context required for compilable and meaningful tests

The following information is missing for complete, reliable regression-test generation:

1. **Source of the triggering test**
   ```text
   com.fasterxml.jackson.dataformat.xml.misc.XmlTextTest::testMixedContent
   ```
   This is the most important missing item. It would define:
   - the test XML;
   - the mapper/model API used;
   - the exact assertion that expects `27`;
   - the intended mixed-content behavior.

2. **The bug report content or an explicit behavioral contract**
   - The issue URL is supplied, but its text is not.
   - The prompt does not state the intended behavior beyond the failing assertion.

3. **Fixed-version diff or fixed `XmlTokenStream` source**
   - Not required if a complete specification/test exists.
   - Without a specification or triggering-test source, it would be useful as an oracle, but it has not been supplied and must not be assumed.

4. **`ElementWrapper` implementation**
   - Required for precise virtual-wrapper test analysis.
   - Without it, the expected behavior of wrapper matching, intermediate wrappers, and replayed tokens cannot be determined reliably.

5. **`StaxUtil.throwXmlAsIOException` implementation**
   - Required to assert exact exception wrapping details beyond the broad expectation of an `IOException`.

6. **Project Maven dependency configuration / existing test utilities**
   - Needed to know whether a particular StAX implementation, mocking library, or helper base class is already available.
   - This matters especially for testing exceptional close/parse/location branches.

7. **Actual StAX implementation used under Maven**
   - Text event behavior, especially whitespace and `SPACE` versus `CHARACTERS`, may differ across implementations/configuration.
   - This affects precise assertions for whitespace-only or formatted XML.

8. **Location behavior expectations**
   - The source does not specify expected line/column/offset semantics for location methods.
   - Tests can verify fallback behavior only if a controllable `XMLStreamLocation2` implementation is available.

In summary: normal token-stream tests can be designed from the supplied source, but a reliable regression test for JacksonXml-2 cannot be finalized without the missing mixed-content test/specification that explains why `27` is expected instead of `0`.