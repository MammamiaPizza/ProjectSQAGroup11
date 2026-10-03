## Scope analyzed

Only the supplied source for:

- `org.apache.commons.jxpath.ri.NamespaceResolver`
- `org.apache.commons.jxpath.ri.model.dom.DOMNodePointer`

and the stated project metadata / bug summary for **JxPath-13 (JXPATH-97)** were used. No assumptions were made from an unprovided fixed revision, external issue content, or unprovided existing test source.

---

## 1. Public methods that should be tested

### `NamespaceResolver`

#### Constructors
- `NamespaceResolver()`
- `NamespaceResolver(NamespaceResolver parent)`

#### Public instance methods
- `void registerNamespace(String prefix, String namespaceURI)`
- `void setNamespaceContextPointer(NodePointer pointer)`
- `Pointer getNamespaceContextPointer()`
- `String getNamespaceURI(String prefix)`
- `String getPrefix(String namespaceURI)`
- `boolean isSealed()`
- `void seal()`
- `Object clone()`

### `DOMNodePointer`

#### Constructors
- `DOMNodePointer(Node node, Locale locale)`
- `DOMNodePointer(Node node, Locale locale, String id)`
- `DOMNodePointer(NodePointer parent, Node node)`

#### Public instance methods
- `boolean testNode(NodeTest test)`
- `QName getName()`
- `String getNamespaceURI()`
- `NodeIterator childIterator(NodeTest test, boolean reverse, NodePointer startWith)`
- `NodeIterator attributeIterator(QName name)`
- `NodePointer namespacePointer(String prefix)`
- `NodeIterator namespaceIterator()`
- `String getNamespaceURI(String prefix)`
- `String getDefaultNamespaceURI()`
- `Object getBaseValue()`
- `Object getImmediateNode()`
- `boolean isActual()`
- `boolean isCollection()`
- `int getLength()`
- `boolean isLeaf()`
- `boolean isLanguage(String lang)`
- `void setValue(Object value)`
- `NodePointer createChild(JXPathContext context, QName name, int index)`
- `NodePointer createChild(JXPathContext context, QName name, int index, Object value)`
- `NodePointer createAttribute(JXPathContext context, QName name)`
- `void remove()`
- `String asPath()`
- `int hashCode()`
- `boolean equals(Object object)`
- `Object getValue()`
- `Pointer getPointerByID(JXPathContext context, String id)`
- `int compareChildNodePointers(NodePointer pointer1, NodePointer pointer2)`

#### Public static methods
- `static boolean testNode(Node node, NodeTest test)`
- `static String getPrefix(Node node)`
- `static String getLocalName(Node node)`
- `static String getNamespaceURI(Node node)`

---

## 2. Input types and valid input ranges

### `NamespaceResolver`

| Method | Inputs | Valid/routine inputs identifiable from source |
|---|---|---|
| constructors | optional `NamespaceResolver parent` | `parent` may be `null`. |
| `registerNamespace` | `String prefix`, `String namespaceURI` | Intended for namespace prefix/URI pairs. Source does not reject `null`, empty, or malformed strings. `HashMap` technically accepts `null` keys/values, but API-valid namespace syntax cannot be confirmed from supplied material. |
| `setNamespaceContextPointer` | `NodePointer pointer` | A DOM or another `NodePointer` providing namespace lookup/iteration. `null` is accepted by assignment. |
| `getNamespaceURI` | `String prefix` | Prefix may be locally registered, provided by the context pointer, inherited from parent, or unknown. Empty and `null` are not rejected by this implementation. |
| `getPrefix` | `String namespaceURI` | URI associated with local registrations, namespace iterator entries, or parents. `null` is not rejected. |
| `seal` / `isSealed` / `clone` | none | No input. |

### `DOMNodePointer`

| Method group | Inputs | Valid/routine inputs identifiable from source |
|---|---|---|
| constructors | DOM `Node`; `Locale`; optional ID; optional parent pointer | Normal use requires a non-null W3C DOM `Node`. Constructors do not validate `node`; many methods dereference it. `Locale` and `id` are stored/forwarded but not validated here. |
| node testing | `NodeTest`, or DOM `Node` + `NodeTest` | Supported explicit test types: `NodeNameTest`, `NodeTypeTest`, `ProcessingInstructionTest`, and `null`. |
| namespace methods | prefix `String`, DOM node | Prefixes may be `null`, empty, `"xml"`, `"xmlns"`, declared, inherited through ancestor elements, or undeclared. |
| child / attribute iteration | `NodeTest`, reverse flag, start pointer, `QName` | Iterators are constructed directly. Validity of arguments is partly controlled by `DOMNodeIterator` / `DOMAttributeIterator`, which were not supplied. |
| mutation methods | `Object value`, `JXPathContext`, `QName`, integer index | `setValue` supports DOM `Node` values and values convertible by `TypeUtils.convert(value, String.class)`. `createChild` recognizes inherited `WHOLE_COLLECTION`; the actual numeric constant is not supplied. |
| `createAttribute` | `JXPathContext context`, `QName name` | Routine use expects an element-backed pointer and a non-null name. A prefixed name requires an in-scope namespace declaration discoverable from the DOM pointer. |
| comparison | two `NodePointer`s | Intended to be child/attribute pointers backed by DOM `Node` values. The method casts their base values to `Node`. |
| ID lookup | `JXPathContext context`, ID `String` | Requires an owner `Document` and DOM ID typing recognizable by `Document.getElementById`. |
| path generation | none | Depends on node type, parent chain, namespace resolver, and sibling placement. |

---

## 3. Conditions and reachable branches

### `NamespaceResolver`

#### `registerNamespace`
Reachable branches:
- Resolver is not sealed: stores the prefix/URI and clears `reverseMap`.
- Resolver is sealed: throws `IllegalStateException`.

#### `getNamespaceContextPointer`
Reachable branches:
- Local `pointer` is non-null: returns it.
- Local pointer is null and parent exists: delegates to parent.
- Local pointer is null and parent is null: returns null.

#### `getNamespaceURI`
Lookup precedence implemented by the source:
1. Local `namespaceMap`.
2. Local `pointer.getNamespaceURI(prefix)`, only if no local URI was found and local pointer is non-null.
3. Parent resolver, only if still unresolved.
4. `null`.

Important observable behavior:
- A local map entry with `null` value is indistinguishable from no mapping for the purposes of this method and causes pointer/parent fallback.
- Explicitly registered values take precedence over pointer-derived values and parent mappings.

#### `getPrefix`
Reachable logic:
1. Lazily creates `reverseMap`.
2. Enumerates namespaces from `pointer.namespaceIterator()`.
3. Adds all local registered URI-to-prefix mappings.
4. Looks up requested URI in `reverseMap`.
5. If no local match, delegates to parent if present.
6. Otherwise returns null.

Important issue visible in supplied source:
- `pointer.namespaceIterator()` is called without checking whether `pointer` is null.
- Therefore, `new NamespaceResolver().getPrefix(...)`, or a resolver with no locally assigned pointer, reaches a `NullPointerException`.
- This is a reachable case because both constructors leave `pointer` null and `setNamespaceContextPointer` is optional.

Additional behavior:
- An empty prefix discovered from `namespaceIterator()` is intentionally ignored.
- A local `namespaceMap` entry may overwrite an iterator-derived URI-to-prefix entry because registrations are processed afterward.
- A repeated call uses the cached reverse map unless `registerNamespace` resets it.
- Changing namespace declarations in the underlying DOM after the first `getPrefix` call is not reflected by this resolver’s cached `reverseMap`.

#### `seal`
- Marks this resolver sealed.
- Recursively seals all ancestors.
- Does not seal descendants, because the resolver has no child collection.

#### `clone`
- Shallow clones the resolver.
- Sets the clone’s `sealed` state to `false`.
- The clone retains shared references to `namespaceMap`, `reverseMap`, `pointer`, and `parent` because no deep copy is performed.
- Consequently, namespace registrations made through the clone can affect the original map, subject to sealing behavior. This is an observable behavior worth characterizing, although correctness cannot be inferred without a contract.

---

### `DOMNodePointer`

#### `testNode(Node, NodeTest)`
Reachable branches:
- `test == null`: returns `true`.
- `NodeNameTest`:
  - Non-element node: false.
  - Wildcard with no prefix: true.
  - Wildcard or matching local name:
    - namespace URI matches: true;
    - or node namespace is null and prefix matches: true;
    - otherwise false.
  - Local-name mismatch: false.
- `NodeTypeTest`:
  - `NODE_TYPE_NODE`: element or document only.
  - `NODE_TYPE_TEXT`: text or CDATA.
  - `NODE_TYPE_COMMENT`: comment.
  - `NODE_TYPE_PI`: processing instruction.
  - Other type values: false.
- `ProcessingInstructionTest`:
  - PI node with matching target: true.
  - Non-PI or target mismatch: false.
- Unsupported `NodeTest` subtype: false.

#### Namespace resolution
`getNamespaceURI(String prefix)` branches:
- `null` or `""` prefix: delegates to default namespace lookup.
- `"xml"`: returns `XML_NAMESPACE_URI`.
- `"xmlns"`: returns `XMLNS_NAMESPACE_URI`.
- Other prefix:
  - returns cached value if known;
  - otherwise walks from current node to ancestors searching `xmlns:prefix`;
  - if no declaration exists, caches an internal unknown-namespace marker and returns null.

`getDefaultNamespaceURI()`:
- Walks current node / document element and ancestors looking for `xmlns`.
- Returns null for absent or empty declarations.
- Caches the first result found, including the no-default-namespace result represented internally by `""`.

`static getNamespaceURI(Node)`:
- If passed a `Document`, starts from its document element.
- Casts the resulting node to `Element`.
- Uses DOM `Element.getNamespaceURI()` first.
- Otherwise searches matching `xmlns` / `xmlns:prefix` declarations through ancestors.

Potential exceptional branch:
- Any non-`Document`, non-`Element` node passed to static `getNamespaceURI(Node)` causes `ClassCastException` due to `Element element = (Element) node`.
- A null node causes `NullPointerException`.
- Whether this limitation is intentional is not documented in the supplied material.

#### Node name extraction
- Elements produce prefix/local-name `QName`.
- Processing instructions produce a `QName` with target as local name.
- Other node types produce `new QName(null, null)`.

`getPrefix(Node)` / `getLocalName(Node)`:
- Prefer namespace-aware DOM properties (`getPrefix`, `getLocalName`).
- Fall back to parsing the DOM node name at its final colon.
- For unprefixed names, prefix is null and local name is full node name.

#### `setValue`
Branches:
- Text or CDATA pointer:
  - Converted nonempty string: replaces node value.
  - null or empty converted string: removes the current text/CDATA node from parent.
- Other node:
  - Removes all existing children first.
  - `Element` or `Document` input: clones and appends each child.
  - Other DOM `Node`: clones and appends that node.
  - Non-node input: converts to `String`; if nonempty, appends new text node; if null/empty, leaves node childless.

Potential constraints:
- A text/CDATA node with no parent will fail when an empty/null value attempts removal.
- DOM hierarchy restrictions can produce DOM exceptions during append/remove.
- The exact conversion behavior for arbitrary objects depends on unprovided `TypeUtils`.

#### `createChild`
Branches:
- `WHOLE_COLLECTION` index becomes `0`.
- Uses `context.getFactory()` through `getAbstractFactory`.
  - No factory: throws `JXPathException`.
- Calls factory `createObject(...)`.
  - Factory returns false: throws `JXPathAbstractFactoryException`.
  - Factory returns true but iterator does not locate requested created child: throws `JXPathAbstractFactoryException`.
  - Factory returns true and matching child at `index + 1`: returns child pointer.
- For prefixed `QName`, obtains namespace URI using `context.getNamespaceURI(prefix)`.

The factory behavior and index contract cannot be fully tested without the relevant factory implementation or test fixture.

#### `createAttribute`
Branches:
- Backing node is not an `Element`: delegates to inherited `NodePointer.createAttribute`; behavior not supplied.
- Unprefixed name:
  - Existing attribute: leaves it in place.
  - Missing attribute: creates empty attribute using `setAttribute`.
- Prefixed name:
  - Resolves prefix with `getNamespaceURI(prefix)` from the DOM node context.
  - Unresolved prefix: throws `JXPathException("Unknown namespace prefix: " + prefix)`.
  - Resolved prefix: calls `setAttributeNS(ns, name.toString(), "")`.
- Finally obtains the attribute through `attributeIterator(name)` and returns its first pointer.

This method contains the exception observed by the supplied bug report.

#### `remove`
- Parent exists: removes current DOM node.
- Parent is null: throws `JXPathException("Cannot remove root DOM node")`.

#### `asPath`
Branches by node type:
- Explicit `id` supplied in constructor: returns `id('...')`, escaping single and double quotes as XML entities.
- Element:
  - If parent is not a `DOMNodePointer`, does not append element test itself.
  - Element with no namespace: local name plus position among same node names.
  - Namespaced element with resolver-recognized prefix: qualified name plus relative position.
  - Namespaced element without usable prefix: `node()[position among elements]`.
- Text/CDATA: `/text()[n]`.
- Processing instruction: `/processing-instruction('target')[n]`.
- Document: no additional segment.
- Other node types: no explicit path segment.

#### Value extraction
- Comment pointer: returns trimmed comment content, or empty string for null comment data.
- Text / CDATA:
  - With `xml:space="preserve"` inherited: preserves whitespace.
  - Otherwise: trims whitespace.
- Processing instruction: same whitespace behavior for PI data.
- Element/document-like node: recursively concatenates descendants; comments contribute empty string.

#### `getPointerByID`
- Uses current document if current node is a `Document`; otherwise uses `node.getOwnerDocument()`.
- If `document.getElementById(id)` returns an element: returns a new `DOMNodePointer` with that ID.
- If not: returns `NullPointer`.

The supplied source does not establish how DOM IDs are declared or configured, which is necessary for a reliable positive lookup test.

#### `compareChildNodePointers`
- Same node identity: `0`.
- Attribute vs non-attribute: attributes sort first.
- Two attributes: compares order in this pointer’s backing node attribute map.
- Two non-attributes: compares order among this pointer’s direct child nodes.
- Nodes not found in the expected attribute/child collection: `0`.

This method assumes inputs are DOM-backed pointers and can throw `ClassCastException` otherwise.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

### `NamespaceResolver`

#### Normal cases
- Register prefix/URI then retrieve URI with `getNamespaceURI`.
- Retrieve registered prefix with `getPrefix`.
- Resolve from local context pointer.
- Resolve from parent when local resolver has no match.
- Child resolver local mapping overrides parent mapping.
- Local mapping overrides context-pointer mapping.
- Context-pointer mapping overrides parent mapping when local map has no mapping.

#### Boundary cases
- Empty prefix.
- Empty namespace URI.
- Duplicate registration of the same prefix; latest value should be observable.
- Multiple prefixes for same URI; `getPrefix` result depends on map/iterator insertion behavior and is not specified by supplied documentation.
- Empty prefix encountered from `namespaceIterator`; source intentionally excludes it from reverse lookup.

#### Null/invalid cases
- `registerNamespace(null, uri)` and `registerNamespace(prefix, null)` are accepted by the underlying map, but expected API behavior is not defined by supplied documentation.
- `getNamespaceURI(null)` is permitted by code but its semantic contract is not stated.
- `getPrefix(null)` is permitted by code but its semantic contract is not stated.
- `setNamespaceContextPointer(null)` is accepted.

#### Exceptional cases
- Registering after `seal()` throws `IllegalStateException`.
- `getPrefix` with a null local `pointer` causes a reachable `NullPointerException`.
- If the assigned pointer’s `namespaceIterator()` itself fails, that failure propagates.

### `DOMNodePointer`

#### Normal cases
- Element, document, text, CDATA, comment, and processing-instruction node behavior.
- Namespace declaration on current element, ancestor element, and document element.
- Standard `xml` and `xmlns` prefixes.
- Default namespace present, absent, or empty.
- Attribute creation on element for unprefixed names and declared prefixed names.
- Mutation of text/CDATA and element children.
- Node removal from a parent.
- Identity equality and identity-derived hash code.
- Path generation for supported node types.
- Recursive string-value behavior.

#### Boundary cases
- First and later same-named siblings for position calculations.
- Mixed text and CDATA siblings.
- Multiple processing instructions with same/different targets.
- Existing unprefixed attribute creation must not overwrite existing value.
- Empty/null-converted `setValue` input.
- `xml:space="preserve"` versus inherited/default trimming.
- Element with namespace URI but no resolver prefix for `asPath`, resulting in `node()[n]`.
- ID string containing `'` and `"` for `asPath` escaping.

#### Null/invalid cases
- Constructor accepts null node but most methods will then throw `NullPointerException`.
- `testNode(null, null)` returns true because the null test branch returns before dereferencing the node.
- `testNode(null, nonNullTest)` normally throws `NullPointerException`.
- Static `getPrefix(null)` and `getLocalName(null)` throw `NullPointerException`.
- Static `getNamespaceURI(null)` throws `NullPointerException`.
- Static `getNamespaceURI` on text/comment/attribute/PI nodes throws `ClassCastException` because it casts to `Element`.
- Many methods assume non-null `QName`, `JXPathContext`, node iterators, and DOM owner document; invalid values may produce null-pointer or DOM-level exceptions.

#### Explicit exceptions in source
- `createAttribute` with unknown prefixed namespace: `JXPathException`.
- `createChild` without a context factory: `JXPathException`.
- `createChild` when factory fails or matching result cannot be found: `JXPathAbstractFactoryException`.
- `remove` on root/no-parent node: `JXPathException`.

---

## 5. Required constructors, dependencies, and external objects

### For `NamespaceResolver` tests
Required:
- `NamespaceResolver`, optionally parent/child resolver pairs.
- A `NodePointer` implementation when testing pointer-driven namespace lookup and reverse lookup.

Useful supplied implementation:
- `DOMNodePointer` can serve as the namespace context pointer when built around a namespace-aware DOM element/document.

Additional dependencies for reverse lookup:
- `NodePointer.namespaceIterator()`
- `NodeIterator`
- `NodePointer.getNamespaceURI()`
- `NodePointer.getName()`

These classes’ implementation details are not included, so direct unit tests for `NamespaceResolver.getPrefix` may either:
- use real DOM pointer infrastructure from the project, or
- require a minimal test double/subclass only if the actual constructors/abstract requirements of `NodePointer` are available in project sources.

### For `DOMNodePointer` tests
Required:
- Standard W3C DOM implementation:
  - `DocumentBuilderFactory`
  - `DocumentBuilder`
  - `Document`
  - `Element`
  - text/CDATA/comment/PI nodes as needed.
- Namespace-aware parser/factory configuration for namespace-sensitive tests:
  - `DocumentBuilderFactory.setNamespaceAware(true)`.
- `Locale`, typically `Locale.US` or another explicit locale.
- Project classes:
  - `QName`
  - `NodeTest`, `NodeNameTest`, `NodeTypeTest`, `ProcessingInstructionTest`
  - `JXPathContext`
  - `AbstractFactory`
  - `NodePointer`
  - iterators invoked by child/attribute/namespace operations.

Additional requirements for special methods:
- `createChild`: a configured `JXPathContext` with a non-null `AbstractFactory` that can create the requested node.
- `createAttribute`: a `JXPathContext` is accepted but source does not use it for element-backed pointers; a valid `QName` and DOM namespace declaration are required for prefixed attributes.
- `getPointerByID`: document must contain an ID that the DOM implementation recognizes via `Document.getElementById`. Merely setting an attribute named `"id"` may not suffice unless it is typed/registered as an ID.
- `asPath` for namespaced nodes: a usable namespace resolver must be available through inherited `NodePointer` machinery. That behavior is not included in the prompt.

---

## 6. JUnit version and build tool

Provided project context states:

- **JUnit:** `junit-3.8.1.jar`
- **Build tool:** Ant

Therefore, eventual tests should use the JUnit 3 style, for example:

- extend `junit.framework.TestCase`;
- use `public void test...()` instance methods;
- use `assertEquals`, `assertTrue`, `assertFalse`, `assertNull`, `assertNotNull`, and explicit `try`/`catch` for exception assertions.

JUnit 4 annotations such as `@Test` should not be assumed.

---

## 7. Available test oracle

### Explicitly supplied oracle information
The only behavioral oracle supplied is the bug metadata:

- Bug report: **JXPATH-97**
- Triggering test:
  - `org.apache.commons.jxpath.ri.model.ExternalXMLNamespaceTest::testCreateAndSetAttributeDOM`
- Observed failure:
  - `org.apache.commons.jxpath.JXPathException: Unknown namespace prefix: A`
- Failure location implied by source:
  - `DOMNodePointer.createAttribute(...)`, specifically when:
    ```java
    String ns = getNamespaceURI(prefix);
    if (ns == null) {
        throw new JXPathException("Unknown namespace prefix: " + prefix);
    }
    ```

### Source-level oracle
The supplied source documents or makes explicit:
- Namespace lookup precedence in `NamespaceResolver.getNamespaceURI`.
- Sealed resolver behavior.
- Standard namespace handling for `"xml"` and `"xmlns"`.
- Unknown-prefix exception behavior in `createAttribute`.
- Root removal failure behavior.
- Missing factory failure behavior.
- Certain value and path behaviors described in Javadocs.

### Insufficient oracle areas
The following cannot be assigned reliable expected outcomes solely from the supplied prompt:
- The exact XML document shape used by `ExternalXMLNamespaceTest`.
- The exact JXPath expression/path used to create and set the external namespaced attribute.
- Whether namespace declarations originate from DOM namespace APIs, an external namespace resolver, serialized XML, or another object model integration.
- The intended semantics of the fixed version.
- Expected behavior for null prefixes/URIs in `NamespaceResolver`.
- Expected behavior of clone sharing versus deep-copying namespace maps.
- Full factory behavior for `createChild`.
- Full inherited `NodePointer` behavior, including namespace-resolver initialization.
- Iterator semantics and behavior of `DOMAttributeIterator`, `DOMNamespaceIterator`, and `DOMNodeIterator`.

---

## 8. Bug-report-related behaviors that should be tested

The supplied failure establishes that the regression test must exercise a path where a namespaced DOM attribute with prefix **`A`** is created and assigned without incorrectly raising:

```text
JXPathException: Unknown namespace prefix: A
```

### Required bug-focused scenarios

1. **Prefixed DOM attribute creation with an in-scope external namespace declaration**
   - Construct/use the same kind of DOM namespace context represented by the missing `ExternalXMLNamespaceTest`.
   - Ensure prefix `A` has an in-scope URI.
   - Invoke the JXPath operation or `DOMNodePointer.createAttribute` path that creates the `A:`-qualified attribute.
   - Verify no `Unknown namespace prefix: A` exception occurs.
   - Verify the resulting attribute is associated with the expected namespace URI and qualified name, if the missing fixture/spec confirms them.

2. **Set value after creation**
   - The triggering test name is `testCreateAndSetAttributeDOM`, so the relevant behavior includes both:
     - creating the attribute; and
     - setting its value.
   - Verify the created attribute’s value after the operation, once the exact intended input value is known from the original test/specification.

3. **Namespace resolution from the DOM context**
   - Test `DOMNodePointer.getNamespaceURI("A")` against the actual DOM arrangement used in the regression:
     - declaration on current element;
     - declaration on ancestor; or
     - declaration rooted in a document/external XML arrangement.
   - The exact relevant arrangement is currently unknown.

4. **Unknown-prefix negative control**
   - Retain a scenario where a genuinely undeclared prefix causes the documented `JXPathException`.
   - This distinguishes “correctly resolves declared external prefix A” from “silently accepts every unknown prefix.”

5. **Potential `NamespaceResolver` interaction**
   - Because `NamespaceResolver` is also listed as modified for this bug, tests should cover the namespace lookup path used by the failing JXPath operation:
     - context pointer assignment/inheritance;
     - external namespace lookup;
     - prefix-to-URI and URI-to-prefix lookup where applicable.
   - The supplied code alone does not reveal which exact `NamespaceResolver` method the triggering test reaches.

### Caution
It would be unsafe to assert that every DOM `xmlns:A` declaration must resolve in every external DOM implementation without seeing:
- the original `ExternalXMLNamespaceTest`,
- the XML input, and
- the intended fixed behavior.

The failure makes clear that prefix `A` should not be considered unknown in that particular triggering setup, but the setup itself is absent.

---

## 9. Missing context required for compilable and meaningful tests

The prompt is sufficient for a partial source-level test plan, but insufficient to generate a reliable, compilable regression test suite for the reported bug.

### Most important missing items

1. **Source of the triggering test**
   - `org.apache.commons.jxpath.ri.model.ExternalXMLNamespaceTest`
   - Especially `testCreateAndSetAttributeDOM`.
   - Needed to know the XML fixture, expression, expected attribute name/value, and setup sequence.

2. **JXPATH-97 issue description or acceptance criteria**
   - The prompt provides only title/ID and the observed exception.
   - It does not state the intended namespace-resolution contract for “external XML namespace” handling.

3. **The actual build/test directory layout**
   - Ant build target names, test source root, and package conventions are not supplied.
   - Although Ant and JUnit 3.8.1 are known, the correct test placement and invocation target are unknown.

4. **Relevant project source dependencies**
   - `NodePointer`
   - `QName`
   - `JXPathContext`
   - `AbstractFactory`
   - `DOMAttributeIterator`
   - `DOMNamespaceIterator`
   - `DOMNodeIterator`
   - `NamespacePointer`
   - `ExternalXMLNamespaceTest` support utilities, if any.

   These are required to determine constructor signatures, inherited behavior, and realistic test setup.

5. **How the external XML DOM is built**
   - Whether the regression uses Xerces, JAXP parser-created DOM, a DOM imported from another document, an external library object, or namespace-unaware parsing.
   - This is especially important because namespace lookup differs depending on DOM implementation and whether `DocumentBuilderFactory` is namespace-aware.

6. **Exact expected postcondition**
   - The expected qualified attribute name, namespace URI, and assigned value after the create-and-set operation are not provided.

7. **Fixed-version diff or approved behavioral specification**
   - The protocol prohibits requesting another program version unless explicitly permitted, so no fixed source should be requested here.
   - However, without a supplied specification or triggering test source, the desired exact fix behavior cannot be independently derived with confidence.

---

## Conclusion

The source exposes several directly testable behaviors, including namespace registration/inheritance, sealing, DOM namespace lookup, prefixed attribute creation, node mutation, and path/value logic. The bug report specifically requires a regression scenario in which prefix `A` is valid in an external DOM namespace context and `testCreateAndSetAttributeDOM` no longer fails with `Unknown namespace prefix: A`.

However, the supplied prompt does **not** include the triggering test implementation, its XML fixture, or the JXPATH-97 acceptance criteria. Therefore, a compilable and reliable regression test for Bug 13 cannot yet be specified without risking invented setup details or expected results.