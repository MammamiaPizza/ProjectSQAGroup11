## 1. Public methods that should be tested

### `DOMNodePointer`

#### Constructors
- `DOMNodePointer(Node node, Locale locale)`
- `DOMNodePointer(Node node, Locale locale, String id)`
- `DOMNodePointer(NodePointer parent, Node node)`

#### Node matching and naming
- `boolean testNode(NodeTest test)`
- `static boolean testNode(Node node, NodeTest test)`
- `QName getName()`
- `String getNamespaceURI()`
- `String getNamespaceURI(String prefix)`
- `String getDefaultNamespaceURI()`
- `static String getPrefix(Node node)`
- `static String getLocalName(Node node)`
- `static String getNamespaceURI(Node node)`

#### Iterators and namespace support
- `NodeIterator childIterator(NodeTest test, boolean reverse, NodePointer startWith)`
- `NodeIterator attributeIterator(QName name)`
- `NodePointer namespacePointer(String prefix)`
- `NodeIterator namespaceIterator()`
- `NamespaceResolver getNamespaceResolver()`

#### Pointer/node properties
- `Object getBaseValue()`
- `Object getImmediateNode()`
- `boolean isActual()`
- `boolean isCollection()`
- `int getLength()`
- `boolean isLeaf()`
- `boolean isLanguage(String lang)`

#### Mutation and creation
- `void setValue(Object value)`
- `NodePointer createChild(JXPathContext context, QName name, int index)`
- `NodePointer createChild(JXPathContext context, QName name, int index, Object value)`
- `NodePointer createAttribute(JXPathContext context, QName name)`
- `void remove()`

#### Path, value, ID, equality, ordering
- `String asPath()`
- `Object getValue()`
- `Pointer getPointerByID(JXPathContext context, String id)`
- `int compareChildNodePointers(NodePointer pointer1, NodePointer pointer2)`
- `int hashCode()`
- `boolean equals(Object object)`

### `JDOMNodePointer`

#### Constructors
- `JDOMNodePointer(Object node, Locale locale)`
- `JDOMNodePointer(Object node, Locale locale, String id)`
- `JDOMNodePointer(NodePointer parent, Object node)`

#### Node matching and naming
- `boolean testNode(NodeTest test)`
- `static boolean testNode(NodePointer pointer, Object node, NodeTest test)`
- `QName getName()`
- `String getNamespaceURI()`
- `String getNamespaceURI(String prefix)`
- `static String getPrefix(Object node)`
- `static String getLocalName(Object node)`

#### Iterators and namespace support
- `NodeIterator childIterator(NodeTest test, boolean reverse, NodePointer startWith)`
- `NodeIterator attributeIterator(QName name)`
- `NodeIterator namespaceIterator()`
- `NodePointer namespacePointer(String prefix)`
- `NamespaceResolver getNamespaceResolver()`

#### Pointer/node properties
- `Object getBaseValue()`
- `Object getImmediateNode()`
- `boolean isCollection()`
- `int getLength()`
- `boolean isLeaf()`
- `boolean isLanguage(String lang)`

#### Mutation and creation
- `void setValue(Object value)`
- `NodePointer createChild(JXPathContext context, QName name, int index)`
- `NodePointer createChild(JXPathContext context, QName name, int index, Object value)`
- `NodePointer createAttribute(JXPathContext context, QName name)`
- `void remove()`

#### Path, equality, ordering
- `String asPath()`
- `int compareChildNodePointers(NodePointer pointer1, NodePointer pointer2)`
- `int hashCode()`
- `boolean equals(Object object)`

---

## 2. Input types and valid input ranges

### DOM implementation inputs

| Area | Input types | Meaningful valid inputs |
|---|---|---|
| Node constructors | `org.w3c.dom.Node` | `Document`, `Element`, `Attr`, `Text`, `CDATASection`, `Comment`, `ProcessingInstruction`; behavior varies by node type |
| Locale constructors | `Locale` | A non-null locale is expected by superclass behavior, though no local validation is present |
| ID constructor argument | `String` | Any string, including quotes requiring escaping in `asPath()` |
| Node tests | `NodeTest` | `null`, `NodeNameTest`, `NodeTypeTest`, `ProcessingInstructionTest`, or an unsupported `NodeTest` subtype |
| QName operations | `QName` | Unprefixed names, prefixed names, wildcard names according to `NodeNameTest`; namespace URI resolution depends on context/resolver |
| Namespace lookup | `String prefix` | `null`, `""`, `"xml"`, `"xmlns"`, declared prefixes, undeclared prefixes |
| Child creation index | `int` | `NodePointer.WHOLE_COLLECTION`, zero-based non-negative positions; negative and out-of-range behavior should be considered exceptional/factory-dependent |
| Values for `setValue` | `Object` | `String`, convertible scalar values, `Node`, `Element`, `Document`, text/CDATA nodes, `null`, empty string |
| ID lookup | `String id` | Existing DOM ID, non-existing ID, and possibly `null` depending on DOM implementation |
| Comparison | Two `NodePointer` instances | Pointers whose base values are children or attributes of this pointer’s node |

### JDOM implementation inputs

| Area | Input types | Meaningful valid inputs |
|---|---|---|
| Node constructors | `Object` | Intended JDOM objects: `Document`, `Element`, `Attribute`, `Text`, `CDATA`, `Comment`, `ProcessingInstruction` |
| Node tests | `NodeTest` | Same relevant variants as DOM: `null`, `NodeNameTest`, `NodeTypeTest`, `ProcessingInstructionTest`, unsupported subtype |
| Namespace lookup | `String prefix` | `"xml"`, declared prefixes, undeclared prefixes; unlike DOM, a `null` prefix is not handled safely by `getNamespaceURI(String)` |
| Values for `setValue` | `Object` | `Element`, `Document`, `Text`, `CDATA`, `ProcessingInstruction`, `Comment`, convertible scalar values, `null`, empty string |
| Child creation index | `int` | `WHOLE_COLLECTION`, non-negative zero-based index; actual creation is delegated to the configured abstract factory |
| Comparison | Two `NodePointer` instances | Base values should normally be JDOM attributes or content entries belonging to the current element |

---

## 3. Conditions and reachable branches

## Cross-cutting bug-relevant branch: sibling position in `asPath()`

Both implementations have a private relative-position method used by `asPath()` for elements:

- DOM: `getRelativePositionByQName()`
- JDOM: `getRelativePositionByQName()`

The supplied source version compares sibling names using lexical qualified names:

- DOM: `n.getNodeName().equals(node.getNodeName())`
- JDOM: `((Element) child).getQualifiedName().equals(name)`

This makes sibling counting depend on the literal prefix used in the XML document.

The supplied bug report establishes a scenario where two sibling elements are selected through an aliased namespace and should have paths:

```text
/a:doc[1]/a:elem[1]
/a:doc[1]/a:elem[2]
```

but both are currently reported as:

```text
/a:doc[1]/a:elem[1]
```

The reachable bug branch is therefore:

1. Two sibling element nodes have the same local name and namespace URI.
2. Their lexical qualified names differ because distinct prefixes are used, or because the selection namespace alias differs from the physical XML prefix.
3. The iterator selects both nodes as matching the same namespace-qualified XPath node test.
4. `asPath()` computes the second node’s position.
5. The current implementation does not count the first sibling as “like-named,” yielding `[1]` instead of `[2]`.

This behavior must be tested for both DOM and JDOM.

### `testNode` branches

Both implementations have equivalent high-level branches:

1. `test == null` returns `true`.
2. `test instanceof NodeNameTest`
   - Node is not an element: `false`.
   - Wildcard with no prefix: `true`.
   - Wildcard with prefix: namespace/prefix comparison.
   - Exact local-name match and namespace match: `true`.
   - Exact local-name mismatch: `false`.
   - Namespace mismatch: `false`.
   - Namespace-less fallback compares prefixes when node namespace URI is `null`.
3. `test instanceof NodeTypeTest`
   - `NODE_TYPE_NODE`: always `true`.
   - `NODE_TYPE_TEXT`: text or CDATA only.
   - `NODE_TYPE_COMMENT`: comment only.
   - `NODE_TYPE_PI`: processing instruction only.
   - Unsupported compiler node type: `false`.
4. `test instanceof ProcessingInstructionTest`
   - Returns matching target only for a processing-instruction node.
5. Unsupported `NodeTest` subtype returns `false`.

### `asPath()` branches

For both classes:

1. Pointer constructed with an `id`:
   - Returns `id('...')`, using inherited `escape`.
2. Element node:
   - Parent is an implementation-specific node pointer:
     - Namespace URI absent: local-name path with QName-relative sibling position.
     - Namespace URI present and namespace resolver has a prefix: prefixed path with QName-relative sibling position.
     - Namespace URI present but resolver has no prefix: `node()[element-position]`.
   - Parent is not a same-kind node pointer:
     - Parent is responsible for the element segment; this pointer may add no element segment.
3. Text or CDATA:
   - `/text()[n]`.
4. Processing instruction:
   - `/processing-instruction('target')[n]`.
5. Document node:
   - DOM explicitly returns the accumulated parent path without a document segment.
   - JDOM has no separate document segment branch.
6. Comments, attributes, and other types:
   - No additional path segment in these implementations.

### DOM namespace-resolution branches

`DOMNodePointer.getNamespaceURI(String prefix)`:

1. `prefix == null` or empty: resolve default namespace.
2. `"xml"`: returns XML namespace URI constant.
3. `"xmlns"`: returns XMLNS namespace URI constant.
4. Cached namespace available.
5. Traverse current element/ancestors to find `xmlns:prefix`.
6. No declaration or declaration is empty:
   - Cache `NodePointer.UNKNOWN_NAMESPACE`.
   - Return `null`.

`getDefaultNamespaceURI()`:

1. Cached default namespace available.
2. Traverse node/ancestors for `xmlns`.
3. No declaration or empty declaration returns `null`.

### Value/mutation branches

#### DOM `setValue`
1. Text or CDATA:
   - Converted string is non-empty: replace text value.
   - Converted string is `null` or empty: remove the text/CDATA node from its parent.
2. Other node type:
   - Remove existing children.
   - Value is `Element` or `Document`: clone and append its children.
   - Value is another `Node`: clone and append that node.
   - Other value converts to non-empty string: create and append text node.
   - Conversion produces `null`/empty string: leave node empty.

#### JDOM `setValue`
1. `Text`:
   - Converted string non-empty: set text.
   - `null`/empty: remove the text from its parent.
2. Otherwise assumes an `Element`:
   - Clear content.
   - Value is `Element`: clone/add its content.
   - Value is `Document`: clone/add document content.
   - Value is `Text` or `CDATA`: add a new `Text`.
   - Value is PI or comment: clone and add it.
   - Other value converts to a non-empty string: add `Text`.
   - Empty/null conversion: leave the element empty.

### Creation branches

`createChild` in both classes:

1. `index == WHOLE_COLLECTION`: normalized to zero.
2. Delegated abstract factory returns `true`:
   - Build a namespace-aware `NodeNameTest`.
   - Find child at `index + 1`.
   - Return child pointer if found.
3. Factory returns `false`, iterator is absent, or requested child position cannot be selected:
   - Throw `JXPathAbstractFactoryException`.

`createAttribute` in both classes:

1. Current node is not an element:
   - Delegate to superclass.
2. Prefixed name:
   - Resolve prefix.
   - Unknown prefix: throw `JXPathException`.
   - Existing/new attribute: return the first matching attribute iterator pointer.
3. Unprefixed name:
   - Create only if absent.
   - Return matching attribute pointer.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

### Normal cases
- Element pointers with and without namespaces.
- Nested element paths.
- First and later same-name siblings.
- Text, CDATA, comment, and processing-instruction values.
- Namespace declaration inherited from an ancestor.
- Default namespace declared locally and inherited.
- `xml` and `xmlns` special namespace prefixes in DOM.
- Existing and non-existing attributes.
- Existing and absent IDs in DOM.
- Child creation through a successful configured abstract factory.
- Attribute-vs-child pointer ordering.

### Boundary cases
- First matching sibling: position `[1]`.
- Second and later matching siblings: `[2]`, `[3]`, etc.
- Siblings with:
  - Same literal prefix and local name.
  - Same expanded name but different lexical prefixes — required for Bug 19.
  - Same local name but different namespace URIs — should not be treated as equivalent for QName position purposes, subject to the intended project contract.
  - Different local names but same namespace URI.
- Empty element/document content: `isLeaf() == true`.
- Element with only comments or processing instructions: affects `getValue()` and child traversal.
- Empty strings and whitespace-only content with and without `xml:space="preserve"`.
- `xml:lang` inherited through multiple ancestor elements.
- IDs containing quotes, to exercise path escaping.

### Invalid/null cases observable from the supplied source

These cases should be considered, but expected outcomes are not always defined by supplied documentation.

#### DOM
- `new DOMNodePointer(null, locale)`:
  - Construction itself succeeds locally.
  - Most subsequent methods dereference `node` and will throw `NullPointerException`.
- `DOMNodePointer.testNode(null, nonNullTest)`:
  - Likely `NullPointerException` for applicable tests.
- `DOMNodePointer.testNode(null, null)`:
  - Returns `true`, because `test == null` is checked before node use.
- `getNamespaceURI(Node)` with a non-`Document`, non-`Element` node:
  - Casts to `Element`; likely `ClassCastException`.
- `remove()` on a root DOM node:
  - Throws `JXPathException("Cannot remove root DOM node")`.
- `createAttribute` with unknown prefix:
  - Throws `JXPathException`.
- `createChild` when factory cannot create/find the child:
  - Throws `JXPathAbstractFactoryException`.
- `compareChildNodePointers` with pointer base values that are not children/attributes of the current node:
  - May return `0`; source labels this as “Should not happen,” so this is not a reliable supported-use case.

#### JDOM
- `new JDOMNodePointer(null, locale)`:
  - Construction succeeds locally; many methods may fail later or return type-dependent defaults.
- `getNamespaceURI(null)`:
  - Calls `prefix.equals("xml")`; therefore throws `NullPointerException`.
- `setValue` on a non-`Text`, non-`Element` JDOM node:
  - Casts the node to `Element`; likely `ClassCastException`.
- `remove()` for a root element/document-level node:
  - Throws `JXPathException("Cannot remove root JDOM node")`.
- `createAttribute` with unknown prefix:
  - Throws `JXPathException`.
- `createChild` failure:
  - Throws `JXPathAbstractFactoryException`.
- `compareChildNodePointers` when the current node is not an `Element` and pointer values differ:
  - Throws `RuntimeException` with an internal-error message.

### Important reliability note

The source comments describe some behavior, but the full public API contract for every method was not supplied. Therefore, tests can reliably assert:

- the explicit bug-report expectation;
- behavior explicitly documented in source comments/Javadocs;
- documented exception messages/conditions visible in the supplied source where appropriate;
- stable invariants such as pointer identity and sibling ordering.

For less documented behavior—especially null handling, unsupported JDOM node kinds, conversion semantics, and superclass delegation—the supplied material is insufficient to establish a reliable intended result beyond recording current behavior.

---

## 5. Required constructors, dependencies, and external objects

### For DOM-focused tests
Required objects/classes include:

- Standard JAXP DOM parser/factory, such as:
  - `javax.xml.parsers.DocumentBuilderFactory`
  - `javax.xml.parsers.DocumentBuilder`
- DOM node types:
  - `org.w3c.dom.Document`
  - `Element`
  - `Text`
  - `CDATASection`
  - `Comment`
  - `ProcessingInstruction`
  - `Attr`
- `java.util.Locale`, likely `Locale.US` or `Locale.ENGLISH`.
- JXPath types:
  - `QName`
  - `NodePointer`
  - `NodeIterator`
  - `JXPathContext`
  - `NamespaceResolver`
  - `NodeNameTest`
  - `NodeTypeTest`
  - `ProcessingInstructionTest`

### For JDOM-focused tests
Required objects/classes include:

- JDOM types:
  - `org.jdom.Document`
  - `Element`
  - `Namespace`
  - `Text`
  - `CDATA`
  - `Comment`
  - `ProcessingInstruction`
  - `Attribute`
- The JDOM version is not supplied. The imports indicate the older `org.jdom.*` API, not JDOM 2 (`org.jdom2.*`).
- The same JXPath testing types listed for DOM tests.

### For `createChild` tests
A meaningful test needs a configured JXPath abstract factory accessible through the inherited `getAbstractFactory(context)` behavior. The supplied target source does not provide:

- the concrete abstract-factory implementation;
- the mechanism for registering it on `JXPathContext`;
- existing examples of factory-backed child creation.

Therefore, standalone `createChild` tests cannot be designed reliably from the supplied material alone.

### For bug-regression integration tests
The most direct regression tests need:

- A namespace-aware DOM document and an equivalent JDOM document.
- At least two sibling elements that match one expanded QName but use different namespace prefixes in the serialized/object representation, if that is how the original failure is reproduced.
- A `JXPathContext` configured with alias prefix `a` mapped to the shared namespace URI.
- Evaluation of the XPath from the report:

```xpath
/a:doc/a:elem
```

- Iteration over pointers and collection of `Pointer.asPath()` results.

---

## 6. JUnit version and build tool

- **JUnit:** `junit-3.8.1.jar`
- **Build tool:** Maven

Tests should therefore use the JUnit 3 style:

- extend `junit.framework.TestCase`;
- use methods named `test...`;
- use `assertEquals`, `assertTrue`, `assertFalse`, `assertSame`, etc.;
- do not use JUnit 4 annotations such as `@Test`.

The actual Maven POM, source/test directory layout, and dependency versions were not supplied.

---

## 7. Available test oracle

### Strong oracle: supplied Bug 19 report

The bug report provides the clearest expected outcome.

For both DOM and JDOM namespace iteration:

```xpath
/a:doc/a:elem
```

Expected pointer paths:

```text
/a:doc[1]/a:elem[1]
/a:doc[1]/a:elem[2]
```

Observed faulty result in source version `JxPath-19b`:

```text
/a:doc[1]/a:elem[1]
/a:doc[1]/a:elem[1]
```

The named triggering tests are:

- `org.apache.commons.jxpath.ri.model.AliasedNamespaceIterationTest::testIterateJDOM`
- `org.apache.commons.jxpath.ri.model.AliasedNamespaceIterationTest::testIterateDOM`

This is sufficient as an oracle for the principal regression behavior.

### Source-level documentation

The supplied classes provide partial method-level documentation, including:

- `setValue` semantics;
- language and enclosing-attribute lookup;
- relative-position semantics;
- ID lookup;
- child creation failure behavior.

These comments can support tests, but they are not a complete external specification.

### Missing existing-test oracle

The source of `AliasedNamespaceIterationTest` was not supplied. Consequently, the following are unknown:

- its exact XML/JDOM fixture;
- the namespace URI used;
- how aliases are registered;
- whether it uses `JXPathContext` or lower-level pointer APIs;
- helper classes/utilities already available in the project;
- exact expected formatting assumptions beyond the reported pointer strings.

No other API documentation, issue details, or existing test source was supplied.

---

## 8. Behaviors related to Bug 19 that should be tested

The regression suite should cover both target implementations separately.

### Required DOM regression behavior
Given a DOM document and namespace context where `/a:doc/a:elem` matches two sibling elements:

1. Iterate the pointer results for `/a:doc/a:elem`.
2. Confirm two results are returned.
3. Confirm the first result’s path is:

   ```text
   /a:doc[1]/a:elem[1]
   ```

4. Confirm the second result’s path is:

   ```text
   /a:doc[1]/a:elem[2]
   ```

5. Confirm the two pointers do not stringify to the same path.

### Required JDOM regression behavior
Perform the same assertions using an equivalent JDOM document and JDOM pointer model.

### Essential fixture characteristic
To expose the reported defect, the fixture must cause two matching sibling nodes to be considered equivalent by namespace-aware XPath selection while their lexical qualified names differ. Examples include sibling elements with:

- same namespace URI;
- same local name, such as `elem`;
- different physical prefixes, such as `x:elem` and `y:elem`;
- a context alias, such as `a`, mapped to that namespace URI.

If all matching sibling nodes use the same physical prefix, the current lexical QName comparison may already produce `[1]` and `[2]`; such a test would not reliably detect Bug 19.

### Additional valuable bug-focused cases
Subject to project behavior and fixture support:

1. Three matching siblings with mixed prefixes:
   - Expected positions `[1]`, `[2]`, `[3]`.
2. Interleaved nonmatching siblings:
   - Position should be relative to siblings of the same expanded QName, not all elements.
3. Same local name under different namespace URI:
   - Must not be counted as the same expanded QName if intended semantics follow namespace-aware QName identity.
4. Same namespace URI and local name, but XPath alias differs from source prefixes:
   - Path should use the resolver’s path prefix consistently and still have distinct positions.
5. Both direct `asPath()` assertions and full `JXPathContext.iteratePointers()` assertions:
   - Direct pointer tests localize relative-position behavior.
   - Context/iterator tests reproduce the actual defect surface documented by the bug report.

---

## 9. Missing context required for compilable and meaningful tests

The supplied data is sufficient to identify the primary regression scenario, but not sufficient to guarantee a compilable test implementation without inspecting project files already present in the supplied project context.

Missing items include:

1. **The Maven POM and dependency versions**
   - Especially the exact JDOM 1.x artifact/version.
   - Whether XML parser/JAXP behavior is configured in a particular way.
   - Test-scope dependencies and project test utilities.

2. **The source of `AliasedNamespaceIterationTest`**
   - It is explicitly identified as the triggering test but not provided.
   - Its fixture and namespace setup are the most authoritative test template for Bug 19.

3. **The API for namespace registration on the project’s `JXPathContext` version**
   - The target classes call `context.getNamespaceURI(prefix)`, but the supplied context does not show how aliases are assigned.
   - A test must know the supported API for mapping alias `a` to the target namespace URI.

4. **The exact XML/JDOM fixture that reproduces aliases**
   - The bug output confirms expected paths but does not state whether the document contains source prefixes such as `x`/`y`, default namespaces, or another namespace arrangement.

5. **Superclass behavior and related iterator implementations**
   - `NodePointer`, `DOMNodeIterator`, `JDOMNodeIterator`, namespace iterators, and attribute iterators are referenced but not supplied.
   - This limits reliable expectations for iterator positioning, superclass fallback behavior, factory acquisition, and unsupported-node cases.

6. **Concrete abstract-factory configuration**
   - Required for reliable tests of `createChild`.
   - Without it, only failure-path testing might be possible, and even that depends on inherited factory behavior.

7. **Intended contract for null and unsupported node types**
   - Several methods have implementation-observable `NullPointerException` or `ClassCastException` paths, but no supplied specification establishes these as intended API behavior.
   - Such tests should not assert those outcomes as contractual unless an existing project test/specification establishes them.

No production code changes or JUnit test class should be generated yet.