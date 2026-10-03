## Scope analyzed

Target production classes:

- `org.apache.commons.jxpath.ri.model.dom.DOMAttributeIterator`
- `org.apache.commons.jxpath.ri.model.jdom.JDOMAttributeIterator`

Source version: `JxPath-17b`  
Bug: `JxPath-17` / `JXPATH-109`

No test class or production-source modifications are generated here.

---

## 1. Public methods that should be tested

Both target classes expose the same public API:

### `DOMAttributeIterator`

```java
public DOMAttributeIterator(NodePointer parent, QName name)
public NodePointer getNodePointer()
public int getPosition()
public boolean setPosition(int position)
```

### `JDOMAttributeIterator`

```java
public JDOMAttributeIterator(NodePointer parent, QName name)
public NodePointer getNodePointer()
public int getPosition()
public boolean setPosition(int position)
```

The constructor determines the iterator’s candidate attribute list. The remaining methods expose iterator positioning and the current attribute through a `NodePointer`.

---

## 2. Input types and valid input ranges

### Constructor inputs

| Parameter | Type | Relevant valid values |
|---|---|---|
| `parent` | `NodePointer` | Must provide a backing node via `parent.getNode()`. For meaningful attribute enumeration, it must wrap a DOM `org.w3c.dom.Element` for `DOMAttributeIterator`, or a JDOM `org.jdom.Element` for `JDOMAttributeIterator`. It must also provide namespace resolution where prefixed names are used. |
| `name` | `QName` | A QName with a local name, optionally a prefix. The local name may be `"*"` for wildcard attribute selection. Prefix may be `null`, `"xml"`, a resolvable application prefix, or an unresolved prefix. |

### `setPosition(int position)`

The valid iterator-position range depends on the number of selected attributes:

```text
1 <= position <= selectedAttributeCount
```

The initial position is `0`.

Boundary position values:

- `0`: initial/unpositioned state.
- `1`: first selected attribute, if one exists.
- `selectedAttributeCount`: last selected attribute.
- `selectedAttributeCount + 1`: invalid.
- Negative values: invalid.
- `Integer.MIN_VALUE` and `Integer.MAX_VALUE`: invalid unless an impossible matching list size made them valid.

---

## 3. Conditions and reachable branches

## `DOMAttributeIterator`

### Constructor branches

1. **Backing node is a DOM element**
   ```java
   node.getNodeType() == Node.ELEMENT_NODE
   ```
   The attribute list is populated.

2. **Backing node is not a DOM element**
   - The attribute list remains an empty `ArrayList`.
   - No attributes are selected.

3. **QName local name is not `"*"`**
   ```java
   !lname.equals("*")
   ```
   - Calls `getAttribute((Element) node, name)`.
   - Adds at most one matching attribute.

4. **QName local name is `"*"`**
   - Iterates all DOM attributes.
   - Adds only attributes accepted by `testAttr(attr)`.

### `getAttribute(Element, QName)` branches

1. **QName has no prefix**
   ```java
   testPrefix == null
   ```
   - Uses:
     ```java
     element.getAttributeNode(name.getName())
     ```
   - Looks up by unqualified name.

2. **QName has a prefix resolving to a namespace URI**
   ```java
   testNS != null
   ```
   - First attempts:
     ```java
     element.getAttributeNodeNS(testNS, name.getName())
     ```
   - If that fails, scans all attributes and applies `testAttr(attr)` as a compatibility fallback.

3. **QName has a prefix that does not resolve**
   ```java
   testPrefix != null && testNS == null
   ```
   - Falls through to:
     ```java
     element.getAttributeNode(name.getName())
     ```
   - This is observable behavior of the current source, though its intended API contract is not available in the supplied context.

### `testAttr(Attr)` branches

1. Namespace declaration attribute with prefix `xmlns`:
   ```java
   nodePrefix != null && nodePrefix.equals("xmlns")
   ```
   → excluded.

2. Default namespace declaration:
   ```java
   nodePrefix == null && nodeLocalName.equals("xmlns")
   ```
   → excluded.

3. Attribute local name does not match and the requested local name is not `"*"`
   → excluded.

4. Requested prefix equals attribute prefix:
   ```java
   equalStrings(testPrefix, nodePrefix)
   ```
   → included.

5. Prefixes differ but namespace URIs resolve to equal strings:
   ```java
   equalStrings(testNS, nodeNS)
   ```
   → included.

6. Prefixes and namespace URIs do not match
   → excluded.

### Positioning branches

`setPosition(position)`:

- Sets `this.position` before returning.
- Returns `true` only when `position` is in `[1, attributes.size()]`.
- Returns `false` for `0`, negative values, and values beyond the attribute count.

`getNodePointer()`:

1. At initial position `0`:
   - Temporarily calls `setPosition(1)`.
   - Returns `null` if no first attribute exists.
   - Restores the position to `0` after successfully determining a first attribute exists.
   - Returns a pointer to the first attribute.

2. At position `>= 1`:
   - Returns a new `DOMAttributePointer` for `attributes.get(position - 1)`.

3. At a negative position:
   - `index = position - 1`, then the source coerces negative indexes to zero:
     ```java
     if (index < 0) {
         index = 0;
     }
     ```
   - If the list is nonempty, a negative stored position can therefore yield the first attribute despite `setPosition` having returned `false`.
   - This is observable current behavior, but no supplied contract establishes that it is intended and it should not be treated as a reliable desired behavior.

4. At a position greater than the list size:
   - `attributes.get(index)` can throw `IndexOutOfBoundsException`.

---

## `JDOMAttributeIterator`

### Constructor branches

1. **`parent.getNode()` is a JDOM `Element`**
   ```java
   parent.getNode() instanceof Element
   ```
   - Namespace and attribute selection are performed.

2. **Backing object is not a JDOM `Element`**
   - `attributes` remains `null`.
   - `setPosition()` returns `false`.
   - A default `getNodePointer()` call returns `null`, because it calls `setPosition(1)` and that returns `false`.

### Namespace-resolution branches

1. **QName prefix is `null`**
   ```java
   prefix == null
   ```
   - Uses:
     ```java
     Namespace.NO_NAMESPACE
     ```
   - Thus selects only no-namespace attributes under the current implementation.

2. **QName prefix is `"xml"`**
   ```java
   prefix.equals("xml")
   ```
   - Uses:
     ```java
     Namespace.XML_NAMESPACE
     ```

3. **QName prefix is non-null and resolves through the parent namespace resolver**
   - Constructs:
     ```java
     Namespace.getNamespace(prefix, uri)
     ```

4. **QName prefix is non-null but unresolved**
   ```java
   uri == null
   ```
   - Assigns:
     ```java
     attributes = Collections.EMPTY_LIST;
     ```
   - Returns from the constructor.

### Attribute-selection branches

1. **QName local name is not `"*"`**
   - Looks up one attribute:
     ```java
     element.getAttribute(lname, ns)
     ```
   - Adds it when non-null.

2. **QName local name is `"*"`**
   - Iterates all JDOM attributes.
   - Adds an attribute only when:
     ```java
     attr.getNamespace().equals(ns)
     ```

This is the key behavior implicated by the reported defect: when the wildcard QName is unprefixed, `ns` is `Namespace.NO_NAMESPACE`, so the iterator selects only attributes with no namespace.

### Positioning branches

`setPosition(position)`:

1. `attributes == null`
   - Returns `false`.
   - Does **not** assign `this.position`.

2. `attributes != null`
   - Assigns `this.position = position`.
   - Returns `true` only for positions in `[1, attributes.size()]`.

`getNodePointer()`:

1. Initial position `0`
   - Calls `setPosition(1)`.
   - Returns `null` if no first item is available.
   - Restores `position` to `0` after successfully probing.
   - Returns a `JDOMAttributePointer` for the first attribute.

2. A valid positive position
   - Returns the selected attribute pointer.

3. A negative stored position
   - Uses index zero due to:
     ```java
     if (index < 0) {
         index = 0;
     }
     ```
   - As with the DOM implementation, this can expose the first item after invalid positioning if the list is nonempty.

4. A position beyond the attribute count
   - Can throw `IndexOutOfBoundsException`.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

## Normal cases

The following cases should be represented for both implementations where supported by their backing object models:

1. **A named unqualified attribute exists**
   - QName has no prefix and a concrete local name.
   - Iterator exposes exactly that attribute.

2. **A named namespaced attribute exists**
   - QName has a resolvable prefix and concrete local name.
   - Iterator exposes the correctly namespaced attribute.

3. **Requested named attribute does not exist**
   - No node pointer is available at the initial position.
   - `getNodePointer()` should return `null` based on the visible implementation.

4. **Wildcard attribute selection**
   - Element has multiple attributes.
   - Namespace declarations should not be returned by DOM wildcard processing.
   - The JDOM implementation inherently iterates `element.getAttributes()`, but the supplied context does not establish independently whether JDOM exposes namespace declarations in that list.

5. **`xml` namespace**
   - JDOM explicitly recognizes the `"xml"` prefix.
   - DOM relies on the parent namespace resolver for prefixed lookup.

6. **Equivalent namespace URI with different prefixes**
   - Relevant to DOM’s `testAttr`, which can match attributes through namespace URI equality when prefixes differ.
   - The desired semantics should be checked against an available JXPath namespace/API contract before asserting it as an oracle.

## Boundary cases

1. Element with zero selected attributes.
2. Element with exactly one selected attribute.
3. Element with multiple selected attributes.
4. Initial position:
   ```java
   getPosition() == 0
   ```
5. First valid position: `1`.
6. Last valid position: selected-attribute count.
7. `getNodePointer()` at initial position:
   - It probes the first item but restores the reported position to `0`.
   - This observable state behavior should be tested if direct iterator tests are written.

## Invalid cases

1. `setPosition(0)`.
2. `setPosition(-1)`.
3. `setPosition(attributeCount + 1)`.
4. Very large / very small integer positions.
5. QName with an unresolved prefix:
   - JDOM explicitly produces an empty attribute list.
   - DOM’s behavior is implementation-specific to the visible code: unresolved prefixed lookup falls back to `getAttributeNode(localName)`.

Because no `NodeIterator` interface contract was provided, the expected postcondition after invalid positioning is only reliably known from the current implementation, not from an external specification. A test that locks in negative-position behavior or out-of-range exception behavior may be fragile unless existing project tests or API documentation establish it.

## Null cases

The visible source does not validate constructor arguments. Likely consequences include:

| Case | DOM iterator | JDOM iterator |
|---|---|---|
| `parent == null` | `NullPointerException` at `parent.getNode()` | `NullPointerException` at `parent.getNode()` |
| `name == null`, parent wraps valid element | `NullPointerException` at `name.getName()` | `NullPointerException` at `name.getPrefix()` |
| parent node is `null` | `NullPointerException` at `node.getNodeType()` | `instanceof Element` is false; `attributes` remains `null` |
| wrong node kind | Empty list for non-element DOM node | `attributes == null` for non-JDOM-element object |

These outcomes are source-derived, but the supplied material contains no public API contract saying null inputs are supported or what exception type is intended. Null-input tests should therefore only be added if the project’s existing conventions or API documentation support them.

## Exceptional cases

Potential exceptions directly reachable from the provided code include:

- `ClassCastException`
  - DOM constructor casts `parent.getNode()` to `org.w3c.dom.Node`.
  - The cast fails if the pointer wraps a non-DOM object.
- `NullPointerException`
  - Null `parent`, null `name`, null node in DOM path, null namespace resolver if invoked, and potentially other malformed collaborators.
- `IndexOutOfBoundsException`
  - Calling `getNodePointer()` after a position above the matching attribute count has been stored by `setPosition`.

The intended exception contract cannot be determined from the provided context.

---

## 5. Required constructors, dependencies, and external objects

### Required production constructors

```java
new DOMAttributeIterator(NodePointer parent, QName name)
new JDOMAttributeIterator(NodePointer parent, QName name)
```

### Required collaborators

Both classes require:

- `org.apache.commons.jxpath.ri.QName`
- `org.apache.commons.jxpath.ri.model.NodePointer`
- A functioning namespace resolver reachable through:
  ```java
  parent.getNamespaceResolver()
  ```
  when a prefixed QName is used.

### DOM-specific objects

- A DOM `org.w3c.dom.Element`, normally created through a namespace-aware DOM parser or `DocumentBuilderFactory`.
- `org.w3c.dom.Attr`, `NamedNodeMap`, and related DOM interfaces.
- A project-provided `NodePointer` that wraps the DOM element, likely a `DOMNodePointer`, though its constructor and required context are not supplied.

### JDOM-specific objects

- A JDOM `org.jdom.Element`.
- JDOM `org.jdom.Attribute` and `org.jdom.Namespace`.
- A project-provided `NodePointer` that wraps the JDOM element, likely a `JDOMNodePointer`, though its constructor and required context are not supplied.

### Result verification dependencies

The iterator returns:

```java
DOMAttributePointer
JDOMAttributePointer
```

To assert the selected attribute’s name, namespace, or value, tests need the accessible API of `NodePointer`, `DOMAttributePointer`, or `JDOMAttributePointer`. Those sources/API details are not included.

An alternative is an integration-level JXPath expression test, like the reported triggering tests, but the supplied context does not include the test fixtures or the API used by `DOMModelTest` and `JDOMModelTest`.

---

## 6. JUnit version and build tool

Supplied project configuration states:

- **JUnit:** `junit-3.8.2.jar`
- **Build tool:** Maven

Tests must therefore use JUnit 3 conventions, for example:

- Extend `junit.framework.TestCase`
- Use `assertEquals`, `assertTrue`, `assertFalse`, `assertNull`, and `assertNotNull`
- Do not use JUnit 4 annotations such as `@Test`

The exact Maven module, dependency declarations, source roots, JDOM version, and test execution configuration are not supplied.

---

## 7. Available test oracles

The supplied reliable behavioral oracle is the bug report’s triggering-test result:

```text
Evaluating value iterator <vendor/product/price:amount/@*>
expected: <[10%, 20%]>
but was: <[20%]>
```

It occurs in both:

- `org.apache.commons.jxpath.ri.model.dom.DOMModelTest::testAxisAttribute`
- `org.apache.commons.jxpath.ri.model.jdom.JDOMModelTest::testAxisAttribute`

This establishes that, for the existing model fixture used by those tests:

1. The XPath expression:
   ```xpath
   vendor/product/price:amount/@*
   ```
   must return two values.
2. Those values must be:
   ```text
   10%, 20%
   ```
3. The failing `17b` implementation returns only:
   ```text
   20%
   ```
4. The failure exists for both DOM and JDOM models.

The source code strongly indicates that the omitted `10%` belongs to an attribute in a namespace, while `20%` belongs to an unqualified attribute. However, the exact XML fixture and exact attribute names are not supplied, so that detail should not be hard-coded as fact without obtaining it from the existing test source—if access to that source is permitted by the protocol.

No API documentation, full bug-report narrative, fixed patch, target model document, or existing test implementation was supplied beyond the failure summary.

---

## 8. Behaviors related to Bug JXPATH-109 that should be tested

The central regression behavior is:

> The unprefixed wildcard attribute axis `@*` must include both namespace-qualified and unqualified attributes of the selected element.

For the supplied triggering expression:

```xpath
vendor/product/price:amount/@*
```

the expected result is:

```text
[10%, 20%]
```

not:

```text
[20%]
```

### DOM regression coverage needed

A DOM element should contain at least:

- one ordinary/unqualified attribute with value corresponding to `20%`;
- one namespace-qualified non-namespace-declaration attribute with value corresponding to `10%`;
- optionally one or more `xmlns` declarations to ensure they are not exposed as ordinary attributes.

When selecting `@*`, both ordinary and namespace-qualified attributes should be returned, while namespace declaration attributes should remain excluded.

The defective source currently invokes `testAttr(attr)` for wildcard selection. With an unprefixed wildcard QName, `testAttr` only accepts attributes whose namespace/prefix comparison succeeds against the null test prefix/namespace. This explains why the wildcard can omit prefixed attributes.

### JDOM regression coverage needed

A JDOM element should contain:

- an unqualified attribute;
- an attribute in a non-empty namespace.

With an unprefixed wildcard QName (`@*`), both must be selected.

The defective source currently assigns:

```java
ns = Namespace.NO_NAMESPACE;
```

for an unprefixed QName, then filters wildcard attributes using:

```java
attr.getNamespace().equals(ns)
```

Therefore only no-namespace attributes are selected, matching the observed loss of one attribute.

### Integration-level regression coverage

The strongest supplied oracle is an integration test that reproduces the exact reported XPath evaluation through the DOM and JDOM model paths and asserts the ordered values:

```text
10%, 20%
```

A direct iterator unit test is also useful, but it must have a way to inspect the returned attribute pointers reliably.

---

## 9. Missing context required for compilable and meaningful tests

The supplied information is sufficient to identify the primary regression and to describe required coverage, but insufficient to produce fully reliable, compilable tests without inspecting additional already-existing project context.

Missing items include:

1. **Existing triggering test source**
   - `DOMModelTest::testAxisAttribute`
   - `JDOMModelTest::testAxisAttribute`
   - Especially the XML/JDOM fixture containing `price:amount` and the two attributes valued `10%` and `20%`.

2. **`QName` API**
   - Constructor signatures.
   - Whether prefix and local name are represented exactly as assumed.
   - Whether wildcard names are created as `new QName(null, "*")`, another constructor form, or through a factory.

3. **Concrete `NodePointer` construction**
   - Constructors and required parameters for `DOMNodePointer` and `JDOMNodePointer`.
   - Whether a `JXPathContext`, `Locale`, `NodePointer` parent, or namespace resolver must be supplied.

4. **Namespace resolver construction/configuration**
   - The type returned by `parent.getNamespaceResolver()`.
   - How test code should register a prefix-to-URI mapping.
   - This is essential for meaningful named namespaced-attribute tests.

5. **Pointer inspection API**
   - How to reliably obtain the underlying attribute, name, namespace URI, and value from the returned `NodePointer`.
   - The supplied classes instantiate `DOMAttributePointer` / `JDOMAttributePointer`, but their public APIs are not included.

6. **JDOM dependency version**
   - The exact JDOM version and available constructors/factories.
   - This matters because JDOM APIs and namespace behavior vary between major versions.

7. **Maven project layout and module dependencies**
   - Required for locating appropriate test package, classpath dependencies, and whether JDOM test dependencies are present in the relevant module.

8. **Formal `NodeIterator` contract**
   - Needed to determine intended behavior after invalid positions, repeated calls to `getNodePointer()`, and null or wrong-model inputs.
   - Without it, assertions about invalid-position behavior would test incidental implementation details rather than a reliable contract.

9. **Complete bug report or fixed revision diff**
   - The supplied failure proves required wildcard behavior for the specific fixture.
   - It does not fully specify ordering rules, all namespace-declaration semantics, or desired behavior for prefixed wildcards.

In particular, it is not reliable to invent the exact XML fixture, `QName` constructor call, concrete pointer constructor, or expected behavior for malformed inputs from the supplied material alone.