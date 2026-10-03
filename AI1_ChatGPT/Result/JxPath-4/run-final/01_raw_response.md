## Scope analyzed

Only the supplied source for:

- `org.apache.commons.jxpath.ri.model.dom.DOMNodePointer`
- `org.apache.commons.jxpath.ri.model.jdom.JDOMNodePointer`

and the supplied JxPath-4 context were analyzed. No production changes or test class are generated.

---

## 1. Public methods that should be tested

### `DOMNodePointer`

#### Constructors
- `DOMNodePointer(Node node, Locale locale)`
- `DOMNodePointer(Node node, Locale locale, String id)`
- `DOMNodePointer(NodePointer parent, Node node)`

#### Node matching and static helpers
- `boolean testNode(NodeTest test)`
- `static boolean testNode(Node node, NodeTest test)`
- `static String getPrefix(Node node)`
- `static String getLocalName(Node node)`
- `static String getNamespaceURI(Node node)`

#### Node identity and metadata
- `QName getName()`
- `String getNamespaceURI()`
- `String getNamespaceURI(String prefix)`
- `String getDefaultNamespaceURI()`
- `Object getBaseValue()`
- `Object getImmediateNode()`
- `boolean isActual()`
- `boolean isCollection()`
- `int getLength()`
- `boolean isLeaf()`
- `boolean isLanguage(String lang)`
- `Object getValue()`
- `int hashCode()`
- `boolean equals(Object object)`

#### Navigation and iterators
- `NodeIterator childIterator(NodeTest test, boolean reverse, NodePointer startWith)`
- `NodeIterator attributeIterator(QName name)`
- `NodePointer namespacePointer(String prefix)`
- `NodeIterator namespaceIterator()`
- `Pointer getPointerByID(JXPathContext context, String id)`
- `int compareChildNodePointers(NodePointer pointer1, NodePointer pointer2)`

#### Mutation and creation
- `void setValue(Object value)`
- `NodePointer createChild(JXPathContext context, QName name, int index)`
- `NodePointer createChild(JXPathContext context, QName name, int index, Object value)`
- `NodePointer createAttribute(JXPathContext context, QName name)`
- `void remove()`

#### Path representation
- `String asPath()`

---

### `JDOMNodePointer`

#### Constructors
- `JDOMNodePointer(Object node, Locale locale)`
- `JDOMNodePointer(Object node, Locale locale, String id)`
- `JDOMNodePointer(NodePointer parent, Object node)`

#### Node matching and static helpers
- `boolean testNode(NodeTest test)`
- `static boolean testNode(NodePointer pointer, Object node, NodeTest test)`
- `static String getPrefix(Object node)`
- `static String getLocalName(Object node)`

#### Node identity and metadata
- `QName getName()`
- `String getNamespaceURI()`
- `String getNamespaceURI(String prefix)`
- `Object getBaseValue()`
- `Object getImmediateNode()`
- `boolean isCollection()`
- `int getLength()`
- `boolean isLeaf()`
- `boolean isLanguage(String lang)`
- `Object getValue()`
- `int hashCode()`
- `boolean equals(Object object)`

#### Navigation and iterators
- `NodeIterator childIterator(NodeTest test, boolean reverse, NodePointer startWith)`
- `NodeIterator attributeIterator(QName name)`
- `NodeIterator namespaceIterator()`
- `NodePointer namespacePointer(String prefix)`
- `int compareChildNodePointers(NodePointer pointer1, NodePointer pointer2)`

#### Mutation and creation
- `void setValue(Object value)`
- `NodePointer createChild(JXPathContext context, QName name, int index)`
- `NodePointer createChild(JXPathContext context, QName name, int index, Object value)`
- `NodePointer createAttribute(JXPathContext context, QName name)`
- `void remove()`

#### Path representation
- `String asPath()`

---

## 2. Input types and valid input ranges

### DOM inputs

The primary wrapped object must be a W3C DOM `Node`. Meaningful node types vary by method:

| Method area | Meaningful DOM input types |
|---|---|
| Element-name, namespace, attributes, child creation | `Element`, and in some namespace methods `Document` |
| Text mutation/value | `Text` and `CDATASection` |
| Processing-instruction behavior | `ProcessingInstruction` |
| Comment value | `Comment` |
| ID lookup | `Document` or a node having a non-null owner document |
| Path generation | `Document`, `Element`, `Text`, `CDATASection`, `ProcessingInstruction` |
| Ordering | Nodes that are children/attributes of the pointer's wrapped node |

Important non-null input assumptions made by production code:
- `node` is dereferenced in most methods. A null wrapped `Node` is not supported.
- `NodeTest` can be null for `testNode`; null means match any node.
- `QName`, `JXPathContext`, and arguments passed to creation methods are dereferenced and therefore should normally be non-null.
- `String prefix`, `String lang`, and `String id` have different behavior depending on method:
  - Namespace lookup accepts `null` or `""` as the default namespace.
  - `isLanguage(null)` will throw `NullPointerException`, because it calls `lang.toUpperCase()`.
  - `getPointerByID` passes the ID to DOM lookup; behavior for null depends on the DOM implementation.
- Child index:
  - `WHOLE_COLLECTION` is explicitly remapped to zero.
  - Other intended values appear to be zero-based child indexes.
  - Negative indexes other than `WHOLE_COLLECTION` are passed to the factory unchanged; no validation is implemented.

### JDOM inputs

The primary wrapped object is typed as `Object`, but supported meaningful types are:

- `org.jdom.Document`
- `org.jdom.Element`
- `org.jdom.Attribute`
- `org.jdom.Text`
- `org.jdom.CDATA`
- `org.jdom.Comment`
- `org.jdom.ProcessingInstruction`

Important assumptions:
- Some methods cast `node` to `Element` without checking it:
  - The non-text branch of `setValue`
  - `getRelativePositionOfElement`
  - Parts of ordering/path behavior
- Therefore, not every public method supports every listed JDOM type.
- `testNode` permits `test == null`, meaning match any node.
- `getNamespaceURI(String prefix)` accepts a prefix argument, but behavior for null is delegated to JDOM’s `Element.getNamespace(prefix)`.
- `isLanguage(null)` will throw `NullPointerException` if an effective `xml:lang` is found, due to `lang.toUpperCase()`.
- Creation APIs require a non-null context, QName, and factory if creation is expected to succeed.

---

## 3. Reachable conditions and branches

### A. `testNode` branches in both implementations

Both classes support these cases:

1. `test == null`
   - Expected result: `true`.

2. `test instanceof NodeNameTest`
   - Node is not an element: `false`.
   - Wildcard with no prefix: `true`.
   - Wildcard with prefix: namespace must match.
   - Non-wildcard local-name mismatch: `false`.
   - Matching local name: namespace comparison determines result.
   - Namespace comparison treats null as `""` and trims both values.

3. `test instanceof NodeTypeTest`
   - XPath node type maps to implementation node types:
     - `NODE_TYPE_NODE`: element or document.
     - `NODE_TYPE_TEXT`: text or CDATA.
     - `NODE_TYPE_COMMENT`: comment.
     - `NODE_TYPE_PI`: processing instruction.
   - Unknown compiler node type: `false`.

4. `test instanceof ProcessingInstructionTest`
   - Correct processing-instruction node and matching target: `true`.
   - Non-PI node or target mismatch: `false`.

5. Any unsupported `NodeTest` subtype
   - Expected result: `false`.

### B. Namespace branches

#### DOM
`getNamespaceURI(String prefix)`:
- `null`/empty prefix delegates to default namespace lookup.
- `"xml"` returns the XML namespace constant.
- `"xmlns"` returns the XMLNS namespace constant.
- Prefix found on current element: returns its URI.
- Prefix found only on ancestor: returns ancestor declaration.
- Prefix undeclared or declared as empty: returns `null`, internally cached as `UNKNOWN_NAMESPACE`.
- Wrapped `Document`: starts lookup at `getDocumentElement()`.

`getDefaultNamespaceURI()`:
- Finds `xmlns` on current element or nearest ancestor.
- Empty/default-less namespace yields `null`.
- Result is cached.

#### JDOM
`getNamespaceURI()`:
- `Element` URI is returned, except `""` is normalized to `null`.
- Any non-element returns `null`.

`getNamespaceURI(String prefix)`:
- Supports wrapped `Document` by consulting its root element.
- Supports wrapped `Element`.
- Missing namespace returns `null`.
- Other wrapped types return `null`.

### C. Value extraction branches — directly relevant to Bug 4

#### DOM `getValue()` / private `stringValue(Node)`
- `Comment`: returns trimmed comment data; null data becomes `""`.
- `Text` or CDATA: returns trimmed node value; null becomes `""`.
- Processing instruction: returns trimmed PI data; null becomes `""`.
- Other nodes, including `Element` and `Document`:
  - Iterates child nodes.
  - Direct text-node child values are appended without individual trimming.
  - Non-text children are recursively converted using `stringValue`.
  - Final resulting string is trimmed.

This means whitespace can be handled differently for:
- A leaf text or CDATA node.
- An element containing direct text.
- Nested elements containing text.
- Text separated by comments, CDATA, processing instructions, or child elements.

#### JDOM `getValue()`
- `Element`: `Element.getTextTrim()`.
- `Text`: `Text.getTextTrim()`.
- `CDATA`: `CDATA.getTextTrim()`.
- `Comment`: text trimmed if non-null; otherwise returns null.
- Processing instruction: data trimmed if non-null; otherwise returns null.
- Unsupported node types: returns null.

The JDOM implementation delegates element text semantics to JDOM’s `getTextTrim()`, whereas DOM recursively builds a string from all child content. This difference is particularly important for nested elements, comments, CDATA, and whitespace-only content.

### D. `setValue` branches

#### DOM
- Wrapped text or CDATA:
  - Converted string non-empty: replace node value.
  - Null/empty converted string: remove the node from its parent.
- Other nodes:
  - Remove all children.
  - Value is `Element` or `Document`: clone and append all its children.
  - Value is another `Node`: clone and append that node.
  - Other value: convert to string and append a text node only if non-empty.

#### JDOM
- Wrapped `Text`:
  - Converted string non-empty: set text.
  - Null/empty converted string: remove text from parent content.
- All other wrapped nodes are assumed to be `Element`:
  - Clear content.
  - Value `Element`: clone/add its content.
  - Value `Document`: clone/add document content.
  - Value `Text` or `CDATA`: add a new `Text`.
  - Value PI or comment: clone and add it.
  - Other value: convert to string and add non-empty text.

Potentially significant source behavior:
- In `JDOMNodePointer.addContent`, conditions after the first two test `node instanceof CDATA`, `node instanceof ProcessingInstruction`, and `node instanceof Comment`, rather than testing the `child`. Since `addContent` is invoked from the element branch of `setValue`, these later branches do not appear reachable for a wrapped element. This deserves separate regression coverage if tests are expanded beyond Bug 4, but there is no supplied bug report establishing intended behavior.

### E. Factory/create branches

For both pointers:
- `WHOLE_COLLECTION` becomes index `0`.
- Context has no factory: `JXPathException`.
- Factory returns `false`: `JXPathAbstractFactoryException`.
- Factory succeeds but matching child cannot be located at `index + 1`: `JXPathAbstractFactoryException`.
- Factory succeeds and child is found: return child pointer.
- Overload accepting `value`: creates child, then calls `setValue(value)`.

### F. Attribute creation branches

For both:
- Non-element wrapped node: delegates to superclass implementation. The behavior of that inherited method is not supplied.
- Unprefixed attribute:
  - Create only if absent.
  - Return iterator’s first attribute pointer.
- Prefixed attribute:
  - Resolve namespace.
  - Unknown prefix/namespace: `JXPathException`.
  - Create attribute only if absent.
  - Return pointer for the first matching attribute.

### G. Removal branches

- DOM:
  - Parent exists: removes wrapped node.
  - Parent is null: throws `JXPathException("Cannot remove root DOM node")`.

- JDOM:
  - Element parent exists: removes wrapped node from parent content.
  - No element parent: throws `JXPathException("Cannot remove root JDOM node")`.

### H. Path-generation branches

Both classes:
- If constructed with a non-null `id`, return `id('...')` and escape `'` as `&apos;` and `"` as `&quot;`.
- Element with compatible pointer parent:
  - Same/default namespace: local-name position path.
  - Namespace with known resolver prefix: prefixed path.
  - Namespace without resolver prefix: `node()[element-position]`.
- Text/CDATA: `/text()[position]`.
- Processing instruction: `/processing-instruction('target')[position]`.
- Root document contributes no path segment.
- DOM has no explicit comment path branch.
- JDOM has no explicit comment or document path branch.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

### Normal cases
- Construct pointers around valid DOM/JDOM documents, elements, text nodes, comments, CDATA, PIs, and attributes where supported.
- Verify identity methods:
  - `getBaseValue()`
  - `getImmediateNode()`
  - `isCollection() == false`
  - `getLength() == 1`
  - DOM `isActual() == true`
- Verify equality is based on wrapped-object identity, not structural XML equality.
- Verify hash code is based on object identity.
- Verify leaf/non-leaf status.
- Verify direct and inherited namespaces.
- Verify name, prefix, and local-name extraction.
- Verify ordering of attributes versus children and sibling ordering.
- Verify creation and mutation behavior with a suitable factory/context.

### Boundary cases
- Empty element/document content.
- Empty string and whitespace-only text.
- Null node values/data where permitted by DOM/JDOM implementations.
- First and later sibling positions for:
  - Same-name elements.
  - All elements.
  - Text/CDATA nodes.
  - PIs sharing target.
- Namespace declared on:
  - The current element.
  - An ancestor.
  - The document root when pointer wraps a document.
- Namespace declaration with empty URI.
- IDs containing both quote types.
- `WHOLE_COLLECTION` index.
- Existing versus absent attributes.
- Existing versus absent XML language attributes.
- Case-insensitive language prefix comparisons.

### Invalid or unsupported cases
- `testNode` with unsupported `NodeTest`.
- Name test applied to non-element node.
- Unknown namespace prefix in `createAttribute`.
- Unsupported node kinds passed to static name helpers:
  - DOM helpers can operate on any non-null DOM node but may return node-name-derived results.
  - JDOM helpers return null for unsupported objects.
- JDOM methods invoked on node types for which the implementation blindly casts to `Element`; these may throw `ClassCastException`.
- DOM `getNamespaceURI(Node)` on a non-`Document`, non-`Element` node will cast to `Element`, so it may throw `ClassCastException`. This is implementation behavior, not a documented safe API contract.

### Null cases
The source does not provide null-tolerant contracts for most public entry points. Tests should only assert null-related exceptions where the source determines them reliably.

Reliable observations:
- `testNode(null)` returns true.
- `getNamespaceURI(null)` in DOM means default namespace lookup.
- Most methods will throw `NullPointerException` if the wrapped node is null.
- `isLanguage(null)` can throw `NullPointerException` when an XML language value is found.
- `equals(null)` returns false.
- `setValue(null)` removes text nodes or clears element contents, subject to `TypeUtils.convert(null, String.class)` behavior; that conversion behavior is external and not supplied, so exact assertions require project context for `TypeUtils`.

### Explicit exceptional cases
- Missing factory in `createChild`: `JXPathException`.
- Factory cannot create object or created object cannot be found: `JXPathAbstractFactoryException`.
- Attribute creation with undeclared prefix: `JXPathException`.
- Root removal: `JXPathException`.
- JDOM child comparison on a non-element pointer with two non-attribute different nodes: `RuntimeException`.
- Several unsupported node-type paths may cause `ClassCastException` or `NullPointerException`; these are implementation consequences, not necessarily desired API behavior.

---

## 5. Required constructors, dependencies, and external objects

### DOM tests require
- A namespace-aware W3C DOM implementation, normally created through `DocumentBuilderFactory` with `setNamespaceAware(true)`.
- DOM objects:
  - `Document`
  - `Element`
  - `Text`
  - `CDATASection`
  - `Comment`
  - `ProcessingInstruction`
  - `Attr`
- `Locale`, typically `Locale.US` or `Locale.getDefault()`.
- JXPath internal types for matching/navigation:
  - `QName`
  - `NodeNameTest`
  - `NodeTypeTest`
  - `ProcessingInstructionTest`
  - `NodePointer`
- JXPath context and factory setup for creation tests:
  - `JXPathContext`
  - `AbstractFactory`
- Iterator implementations are instantiated internally:
  - `DOMNodeIterator`
  - `DOMAttributeIterator`
  - `DOMNamespaceIterator`
  - `NamespacePointer`

### JDOM tests require
- The JDOM version included by the project. The source imports the JDOM 1.x package names:
  - `org.jdom.Document`
  - `org.jdom.Element`
  - `org.jdom.Text`
  - `org.jdom.CDATA`
  - `org.jdom.Comment`
  - `org.jdom.ProcessingInstruction`
  - `org.jdom.Attribute`
  - `org.jdom.Namespace`
- `Locale`.
- The same JXPath context, QName, node-test, factory, and pointer infrastructure used by the DOM tests.
- Internally instantiated iterator/pointer classes:
  - `JDOMNodeIterator`
  - `JDOMAttributeIterator`
  - `JDOMNamespaceIterator`
  - `JDOMNamespacePointer`

### Additional dependencies needed for tests of inherited behavior
Several methods delegate to superclass behavior or depend on superclass state:
- `NodePointer`
- Namespace resolver returned by `getNamespaceResolver()`
- `NodePointer.UNKNOWN_NAMESPACE`
- `NodePointer.WHOLE_COLLECTION`
- `super.isLanguage(lang)`
- `super.createAttribute(context, name)`

The supplied source does not include these superclass implementations. Tests can exercise target-class branches that avoid those inherited paths, but complete expected-result assertions for delegated behavior require those sources or existing tests.

---

## 6. JUnit version and build tool

Supplied project metadata states:

- **JUnit version:** `junit-3.8.1.jar`
- **Build tool:** Ant

Therefore any future test class must use JUnit 3 conventions, such as:
- extending `junit.framework.TestCase`,
- methods named `test...`,
- `assertEquals`, `assertTrue`, `assertFalse`, `fail`,
- no JUnit 4/5 annotations.

---

## 7. Available test oracle

### Supplied oracle information
The bug report provides six failing test methods and exact observed assertion differences:

| Test | Expected | Actual in failing version |
|---|---:|---:|
| `XMLSpaceTest.testPreserveDOM` | `foo` | ` foo ` |
| `XMLSpaceTest.testPreserveJDOM` | `foo` | ` foo ` |
| `XMLSpaceTest.testNestedDOM` | begins with `foo` | failure output is truncated |
| `XMLSpaceTest.testNestedWithCommentsDOM` | begins with `foo` | failure output is truncated |
| `XMLSpaceTest.testNestedJDOM` | empty string | `foo;bar; baz ` |
| `XMLSpaceTest.testNestedWithCommentsJDOM` | empty string | `foo;bar; baz ` |

The following production classes are listed as modified by the fix:
- `DOMNodePointer`
- `JDOMNodePointer`

This strongly identifies node string-value/whitespace behavior, especially `getValue()` and DOM’s private `stringValue(Node)`, as the relevant behavior.

### Missing oracle information
The actual source of `org.apache.commons.jxpath.ri.model.XMLSpaceTest` is not supplied. Consequently, the following are unknown:
- The XML document structures used by the six tests.
- Whether the tests invoke `getValue()` directly, use `JXPathContext.getValue(...)`, or evaluate a more complex XPath expression.
- The exact placement and values of `xml:space="default"` and `xml:space="preserve"`.
- The complete expected DOM nested-string values; two failure messages are truncated.
- The intended cross-model equivalence rules between DOM and JDOM.
- Whether behavior follows XPath string-value rules, XML whitespace rules, a JXPath-specific XML-space contract, or another documented policy.

Therefore, exact replacement regression tests for JXPATH-83 cannot be generated reliably from the supplied information alone.

---

## 8. Bug-report behaviors that should be tested

The triggering test names and failure messages establish that tests should target XML whitespace behavior for both DOM and JDOM pointer models.

### Required regression scenarios

1. **DOM, XML-space preservation scenario**
   - Reproduce the document structure used by `testPreserveDOM`.
   - Assert the expected result is `foo`, not `" foo "`.

2. **JDOM, XML-space preservation scenario**
   - Reproduce the structure used by `testPreserveJDOM`.
   - Assert the expected result is `foo`, not `" foo "`.

3. **DOM nested-content scenario**
   - Reproduce `testNestedDOM`.
   - Include nested elements and the XML-space declarations used by the original test.
   - Assert the complete expected string, which cannot be determined from the supplied truncated failure output.

4. **DOM nested-content-with-comments scenario**
   - Reproduce `testNestedWithCommentsDOM`.
   - Verify comments do not cause unintended text inclusion, exclusion, or whitespace changes according to the original oracle.

5. **JDOM nested-content scenario**
   - Reproduce `testNestedJDOM`.
   - The supplied failure indicates the expected result is `""`, while the failing result was `"foo;bar; baz "`.
   - This requires the original document and expression to determine why nested text must be excluded.

6. **JDOM nested-content-with-comments scenario**
   - Reproduce `testNestedWithCommentsJDOM`.
   - Expected result is `""`; failing result was `"foo;bar; baz "`.

### Supporting tests relevant to the same defect

Once the intended XML-space rule is available, test both models across:
- `xml:space="default"` versus `xml:space="preserve"`.
- Whitespace-only text nodes.
- Leading/trailing spaces around text.
- Nested element boundaries.
- Text separated by comments.
- Text separated by processing instructions.
- Text and CDATA mixtures.
- Parent XML-space declaration inherited by descendants.
- Child XML-space declaration overriding a parent declaration.

These should primarily be exposed through the same public API used by the original `XMLSpaceTest`, rather than assuming direct calls to `DOMNodePointer.getValue()` or `JDOMNodePointer.getValue()` are the actual regression surface.

---

## 9. Missing context required for compilable and meaningful tests

The supplied information is insufficient for a reliable, compilable Bug 4 regression test. The most important missing items are:

1. **Source of `org.apache.commons.jxpath.ri.model.XMLSpaceTest`**
   - This is the key missing artifact.
   - It would provide the XML/JDOM fixture creation, XPath expression, invocation style, and complete expected results.

2. **The exact JXPATH-83 issue description or fixed-version diff**
   - The report only provides a summary and failure outputs.
   - It does not state the intended XML-space semantics.

3. **Project test source layout and existing test conventions**
   - Needed to place a test in the correct package/source directory and reuse project helpers.
   - Especially relevant in an Ant/JUnit-3 project.

4. **Dependency versions and Ant classpath configuration**
   - The JDOM source uses JDOM 1.x APIs, but the exact artifact/version is not supplied.
   - Needed to ensure any JDOM fixture code compiles.

5. **Superclass and related-class source if broad unit coverage is intended**
   - `NodePointer`
   - `JXPathContext`
   - `AbstractFactory`
   - `TypeUtils`
   - DOM/JDOM iterators and namespace pointer classes
   - Needed for reliable tests of inherited/delegated behavior and factory/iterator results.

6. **Whether the intended regression test must be a direct pointer unit test or an integration-level JXPath test**
   - The triggering tests are in `XMLSpaceTest`, suggesting an integration-facing contract.
   - Direct testing of `getValue()` alone may not reproduce the failure if XPath evaluation applies additional semantics.

---

## Conclusion

The supplied source supports broad unit testing of node matching, namespaces, path creation, mutation, equality, language lookup, iteration setup, ordering, and exception branches. However, Bug 4 is specifically tied to XML-space and nested text-value behavior, and the exact expected behavior cannot be derived safely from the production source and abbreviated failure messages alone.

A reliable JUnit 3 regression test should not be generated until the original `XMLSpaceTest` fixture/source or an equivalent explicit JXPATH-83 behavioral specification is available.