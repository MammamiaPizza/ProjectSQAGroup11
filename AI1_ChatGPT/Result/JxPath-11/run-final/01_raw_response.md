## Scope analyzed

Source Version: **JxPath-11b**  
Target classes:

- `org.apache.commons.jxpath.ri.model.dom.DOMAttributeIterator`
- `org.apache.commons.jxpath.ri.model.jdom.JDOMAttributeIterator`

No test class is generated in this response.

---

## 1. Public methods and constructors to test

### `DOMAttributeIterator`

| Member | Purpose indicated by source |
|---|---|
| `DOMAttributeIterator(NodePointer parent, QName name)` | Builds a list of matching DOM attributes for the parent node and requested QName. |
| `NodePointer getNodePointer()` | Returns a `DOMAttributePointer` for the currently selected attribute, with special behavior at position zero. |
| `int getPosition()` | Returns the current iterator position. |
| `boolean setPosition(int position)` | Sets the position and returns whether it is within the populated attribute-list bounds. |

### `JDOMAttributeIterator`

| Member | Purpose indicated by source |
|---|---|
| `JDOMAttributeIterator(NodePointer parent, QName name)` | Builds a list of matching JDOM attributes for the parent node and requested QName. |
| `NodePointer getNodePointer()` | Returns a `JDOMAttributePointer` for the currently selected attribute, with special behavior at position zero. |
| `int getPosition()` | Returns the current iterator position. |
| `boolean setPosition(int position)` | Sets the position if attribute storage is initialized and returns whether it is in bounds. |

The private matching methods in `DOMAttributeIterator` (`testAttr`, `getAttribute`, and `equalStrings`) must be covered indirectly through the public constructor and iterator methods.

---

## 2. Input types and valid input ranges

### Constructor inputs

Both iterator constructors accept:

- `NodePointer parent`
  - Must provide a node via `parent.getNode()`.
  - Must support namespace resolution through `parent.getNamespaceURI(String)` where namespace-qualified DOM matching is exercised.
- `QName name`
  - Expected to provide:
    - `name.getName()` — local name or wildcard `"*"`.
    - `name.getPrefix()` — possibly `null`, `"xml"`, a declared prefix, or an undeclared prefix.

### Parent node types

#### DOM iterator

`parent.getNode()` is cast to `org.w3c.dom.Node`.

Relevant node variants:

- `Node.ELEMENT_NODE`: attributes are inspected.
- Any non-element DOM `Node`: constructor leaves an empty attribute list.
- `null`: causes a `NullPointerException` when `getNodeType()` is invoked.
- A non-DOM object: causes `ClassCastException` in the constructor.

#### JDOM iterator

`parent.getNode()` is checked with `instanceof org.jdom.Element`.

Relevant variants:

- `org.jdom.Element`: attributes are inspected.
- Non-`Element`, including `null`: no attribute list is initialized (`attributes` remains `null`).

### QName name inputs

Relevant QName categories:

1. **Unprefixed concrete name**
   - Local name such as `"discount"`.
2. **Prefixed concrete name**
   - Prefix such as `"rate"` and local name such as `"discount"`.
3. **Wildcard local name**
   - Local name `"*"`.
4. **Reserved XML prefix**
   - JDOM has explicit behavior for `"xml"`, using `Namespace.XML_NAMESPACE`.
5. **Unknown or undeclared prefix**
   - DOM and JDOM have different source-level behavior.
6. **Null QName**
   - Not supported by either implementation; constructor dereferences `name`.
7. **Null QName local name**
   - Not reliably supported; calls such as `lname.equals("*")` can throw `NullPointerException`.

### Position input range

Both iterators consider positions valid only when:

```text
1 <= position <= number of matched attributes
```

Position zero has special meaning in `getNodePointer()`:

- It temporarily attempts position `1`.
- If the iterator has at least one result, it returns the first pointer and resets the stored position to `0`.
- If no result exists, it returns `null`.

Other numeric cases:

- Negative position: invalid.
- Position greater than result count: invalid.
- `Integer.MIN_VALUE` and `Integer.MAX_VALUE`: invalid unless an unrealistic attribute-list size makes them valid.

---

## 3. Reachable conditions and branches

## `DOMAttributeIterator` constructor branches

### A. Parent node is an element

```java
if (node.getNodeType() == Node.ELEMENT_NODE)
```

#### A1. Concrete QName local name (`name.getName()` is not `"*"`)

- Calls `getAttribute((Element) node, name)`.
- Adds exactly one matching attribute if the method returns non-null.
- Otherwise produces an empty iterator.

#### A2. Wildcard QName local name (`"*"`)

- Iterates every DOM attribute from `node.getAttributes()`.
- Adds only attributes accepted by `testAttr`.

This branch must distinguish:

- Regular unprefixed attributes.
- Prefixed attributes.
- Namespace declaration attributes:
  - `xmlns:prefix`
  - `xmlns`
- Attributes with a matching local name but nonmatching namespace/prefix.
- Attributes matching a requested namespace through different prefix spellings.

### B. Parent node is not an element

- No attributes are populated.
- `attributes` is an initialized empty `ArrayList`.
- `setPosition(1)` returns `false`.
- `getNodePointer()` at initial position returns `null`.

---

## `DOMAttributeIterator.getAttribute` branches

### A. QName has a prefix and `parent.getNamespaceURI(prefix)` is non-null

```java
if (testNS != null)
```

- First uses `element.getAttributeNodeNS(testNS, name.getName())`.
- If found, returns that attribute.
- If not found, falls back to iterating all attributes and calling `testAttr`.

The fallback is explicitly intended to support parsers that do not correctly support namespace-aware attribute lookup.

### B. QName has no prefix, or prefix resolution returns `null`

```java
return element.getAttributeNode(name.getName());
```

This branch relies on the DOM implementation’s `getAttributeNode(String)` behavior and does not use namespace-aware lookup.

---

## `DOMAttributeIterator.testAttr` branches

This private method is reached from wildcard processing and namespace-aware lookup fallback.

1. Rejects attributes prefixed with `"xmlns"`.
2. Rejects an unprefixed local name `"xmlns"`.
3. Requires either:
   - requested local name `"*"`, or
   - requested local name equal to the attribute local name.
4. Accepts directly matching prefixes, including both prefixes being `null`.
5. If prefixes do not directly match:
   - resolves the requested prefix through `parent.getNamespaceURI(testPrefix)`;
   - resolves the attribute prefix through `parent.getNamespaceURI(nodePrefix)`;
   - accepts if the namespace URIs are equal, including both being `null`.

Notably, the supplied source uses the instance field `name` for the local-name check:

```java
String testLocalName = name.getName();
```

rather than using `testName.getName()`. In current usage, `testName` is always passed as `name`, so this distinction is not externally observable from the supplied code.

---

## `JDOMAttributeIterator` constructor branches

### A. Parent node is a JDOM `Element`

#### A1. Prefixed QName with prefix `"xml"`

```java
if (prefix.equals("xml")) {
    ns = Namespace.XML_NAMESPACE;
}
```

#### A2. Prefixed QName with another prefix

```java
ns = element.getNamespace(prefix);
```

- If the namespace is not found:
  - `attributes` is set to `Collections.EMPTY_LIST`.
  - Constructor returns immediately.
  - Iterator has no result.

#### A3. QName has no prefix

```java
ns = Namespace.NO_NAMESPACE;
```

Only unqualified attributes are considered.

#### A4. Concrete local name

```java
if (!lname.equals("*"))
```

- Creates an `ArrayList`.
- Calls `element.getAttribute(lname, ns)`.
- Adds the returned attribute if present.

#### A5. Wildcard local name

```java
else
```

- Creates an `ArrayList`.
- Iterates all element attributes.
- Adds those whose namespace equals `ns`.

Unlike the DOM iterator’s wildcard processing, this source does not separately filter namespace declaration attributes. Whether such declarations appear in `Element.getAttributes()` depends on JDOM behavior/version, which is not supplied.

### B. Parent node is not a JDOM `Element`

- `attributes` remains `null`.
- `setPosition(...)` returns `false`.
- `getNodePointer()` at initial position returns `null`, because its temporary `setPosition(1)` call fails.
- Some later call patterns can cause `NullPointerException`; see exceptional cases.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

## Normal cases

For both implementations, tests should cover:

1. A single matching unqualified attribute.
2. No matching attribute.
3. Multiple attributes with wildcard selection.
4. Positioning to each valid result.
5. Initial `getNodePointer()` behavior when one or more attributes match.
6. Matching a concrete qualified attribute.
7. Matching a wildcard qualified attribute.
8. Requesting an attribute using an alias prefix that maps to the same namespace URI as the attribute’s actual prefix, if the project’s namespace semantics require URI-based matching.

## Boundary cases

1. **Zero matching attributes**
   - `getNodePointer()` at initial position should return `null`.
   - `setPosition(1)` should return `false`.
2. **Exactly one matching attribute**
   - Valid position range is only `1`.
3. **First valid position**
   - `setPosition(1)` should return `true`.
4. **Last valid position**
   - Must return `true`.
5. **Position zero**
   - `setPosition(0)` returns `false`.
   - The source still records `0` when `attributes` is non-null.
6. **Position immediately above the last result**
   - `setPosition(size + 1)` returns `false`.
7. **Negative positions**
   - `setPosition(-1)` returns `false`.

## Invalid and null cases

### Null `parent`

Both constructors dereference `parent` immediately:

- DOM: `parent.getNode()`
- JDOM: `parent.getNode()`

Expected observable result from the supplied source: `NullPointerException`.

### Null `QName`

Both constructors dereference `name`:

- DOM: `name.getName()`
- JDOM: `name.getPrefix()`

Expected observable result: `NullPointerException`.

### Null parent node

- DOM: `parent.getNode()` returning `null` causes `NullPointerException` at `node.getNodeType()`.
- JDOM: `null instanceof Element` is false, so construction itself succeeds with `attributes == null`.

### Wrong node representation

- DOM iterator requires `parent.getNode()` to be a W3C DOM `Node`; otherwise `ClassCastException`.
- JDOM iterator does not cast before checking type; non-`Element` values simply lead to an uninitialized attribute list.

Whether these exception behaviors are contractual requirements is **not established** by the supplied documentation. They are observable implementation behavior only.

## Exceptional iterator-state cases

### DOM iterator

`setPosition(position)` always assigns:

```java
this.position = position;
```

before returning the validity result.

Consequences when at least one attribute exists:

- After `setPosition(-1)`, `getNodePointer()` computes index `0` and returns the first attribute pointer, despite the position being invalid.
- After `setPosition(0)`, `getNodePointer()` uses its special initial-position behavior and can return the first pointer.
- After `setPosition(size + 1)`, `getNodePointer()` attempts `attributes.get(size)` and throws `IndexOutOfBoundsException`.

### JDOM iterator

When `attributes != null`, behavior is equivalent: invalid positions are recorded before `false` is returned.

When `attributes == null`:

```java
if (attributes == null) {
    return false;
}
```

- `setPosition(...)` returns `false` and does **not** update `position`.
- At position zero, `getNodePointer()` returns `null`.
- If position somehow becomes nonzero is not possible through `setPosition` from the initial state, because it does not update the position. Reflection or other external state manipulation is outside normal testing scope.

The source does not document intended behavior after invalid `setPosition` calls. Tests should avoid treating the observed invalid-state behavior as an API contract unless an existing project test or interface documentation confirms it.

---

## 5. Constructors, dependencies, and external objects needed

### Required production objects

Both iterators require:

- A `NodePointer` implementation appropriate for the model.
- A `QName`.
- A node held by the `NodePointer`.

### DOM-specific objects

- `org.w3c.dom.Document`
- `org.w3c.dom.Element`
- `org.w3c.dom.Attr`
- Namespace-aware DOM parser or document builder configuration for qualified-attribute tests.
- A suitable concrete `NodePointer` capable of:
  - returning the DOM element from `getNode()`;
  - resolving prefixes from `getNamespaceURI(String)`.

The supplied code references:

- `DOMNodePointer.getPrefix(Attr)`
- `DOMNodePointer.getLocalName(Attr)`
- `DOMAttributePointer`

Their constructors and behavior are not included in the prompt.

### JDOM-specific objects

- `org.jdom.Element`
- `org.jdom.Attribute`
- `org.jdom.Namespace`
- A suitable concrete `NodePointer` capable of returning a JDOM `Element`.

The supplied code references:

- `JDOMAttributePointer`

Its constructor and behavior are not included.

### Test fixture likely needed for the reported bug

A namespaced XML/JDOM element structure containing:

- a `vendor` element;
- a `product` child;
- a `rate:amount` child;
- a qualified `rate:discount` attribute;
- at least one other same-local-name attribute or namespace mapping that can cause the DOM implementation to return `"20%"` instead of the expected `"10%"`.

However, the exact XML fixture, namespace declarations, and the source of values `"10%"` and `"20%"` are not supplied. They must not be invented if the goal is a reliable regression test.

---

## 6. JUnit version and build tool

Supplied project configuration states:

- **JUnit:** `junit-3.8.1.jar`
- **Build tool:** Ant

Therefore, a later test implementation should use JUnit 3 conventions, such as:

- extending `junit.framework.TestCase`;
- test methods named `test...`;
- JUnit 3 assertions from `TestCase` / `junit.framework.Assert`.

JUnit 4/5 annotations should not be assumed available.

---

## 7. Available test oracle

The prompt provides the following useful regression oracle.

### Bug report identity

- Defects4J Bug ID: `JxPath-11`
- Apache JIRA issue: `JXPATH-97`
- Fixed revision: `52d73022820d163104c6419f25ca955f86464f63`

### Existing triggering tests and expected behavior

#### DOM model

```text
org.apache.commons.jxpath.ri.model.dom.DOMModelTest::testNamespaceMapping
```

Failure in this source version:

```text
Evaluating <vendor[1]/product[1]/rate:amount[1]/@rate:discount>
expected:<10%> but was:<20%>
```

This is a concrete oracle:

- Given the existing `DOMModelTest` namespace-mapping fixture and context,
- evaluation of:

```text
vendor[1]/product[1]/rate:amount[1]/@rate:discount
```

must produce:

```text
10%
```

and must not produce:

```text
20%
```

#### JDOM model

```text
org.apache.commons.jxpath.ri.model.jdom.JDOMModelTest::testNamespaceMapping
```

Failure in this source version:

```text
org.apache.commons.jxpath.JXPathNotFoundException:
No value for xpath:
vendor[1]/product[1]/rate:amount[1]/@rate:discount
```

This is a concrete oracle:

- With the existing JDOM namespace-mapping fixture and context,
- the same XPath must resolve successfully;
- it must not throw `JXPathNotFoundException`;
- the expected resolved value is strongly suggested to be `"10%"` by the corresponding DOM failure, but the prompt does not explicitly state the JDOM assertion/value.

### Limits of the oracle

The supplied prompt does **not** include:

- source code for `DOMModelTest` or `JDOMModelTest`;
- their XML/JDOM object fixture;
- namespace declarations;
- namespace context setup;
- `NodePointer`, `QName`, or JXPath context API documentation;
- the fixed-version source diff.

Therefore, the exact direct unit-level expected behavior of every namespace case cannot be determined reliably from this prompt alone. The triggering test names and failures support regression testing at the existing model-test/XPath level, but do not fully specify all direct iterator semantics.

---

## 8. Bug-report-related behaviors that should be tested

The changed production classes are both attribute iterators, and the reported regression concerns a namespace-qualified attribute selection:

```text
@rate:discount
```

The following behaviors are directly relevant.

### A. Qualified attribute lookup through namespace mappings

For both DOM and JDOM models, test that an XPath-qualified attribute selection using `rate:discount` resolves the intended attribute under the namespace mapping used by the existing namespace-mapping fixture.

Required regression result, where the provided oracle is explicit:

```text
vendor[1]/product[1]/rate:amount[1]/@rate:discount == "10%"
```

### B. Avoid selecting a same-local-name attribute from the wrong namespace

The DOM failure shows that the implementation returned `"20%"` where `"10%"` was expected. Regression coverage should ensure that:

- attributes sharing the local name `discount` are not confused;
- namespace mapping, rather than only local-name coincidence or an unintended mapping, determines the selected attribute.

The exact fixture needed to reproduce the wrong `"20%"` selection is missing from the prompt.

### C. JDOM qualified attribute must be found

The JDOM failure shows that the iterator failed to find the requested attribute at all. Tests should ensure that the relevant qualified attribute is included in the JDOM iterator result and can be resolved by XPath.

### D. Namespace alias / context mapping behavior

The likely affected area is interaction among:

- requested QName prefix (`rate`);
- parent `NodePointer` namespace lookup;
- actual attribute namespace;
- element-level namespace declarations.

However, the exact intended rule cannot be conclusively derived without the existing fixture or API documentation. In particular, the JDOM source resolves non-`xml` prefixes through:

```java
element.getNamespace(prefix)
```

while the DOM source consults:

```java
parent.getNamespaceURI(prefix)
```

A regression test should use the existing `testNamespaceMapping` fixture rather than inventing a namespace-alias scenario.

---

## 9. Missing context needed for compilable, meaningful tests

The supplied information is enough to identify public methods, branch structure, and the high-level regression outcome. It is **not enough to write a reliable, compilable direct-unit test suite** without inspecting existing project sources already referenced by the prompt.

The missing context includes:

1. **`QName` API**
   - Constructor signatures.
   - Whether `null`, empty prefix, and empty local name are valid.
   - Namespace semantics expected by JXPath.

2. **`NodePointer` API and concrete implementations**
   - Constructor signatures for usable DOM and JDOM node pointers.
   - Behavior of `getNamespaceURI`.
   - Whether a test should create real model pointers or stub/subclass `NodePointer`.

3. **`DOMAttributePointer` and `JDOMAttributePointer` APIs**
   - How to inspect the selected underlying attribute from the returned `NodePointer`.
   - Whether pointer equality, node access, or value access is the intended assertion mechanism.

4. **Existing triggering test source**
   - `DOMModelTest::testNamespaceMapping`
   - `JDOMModelTest::testNamespaceMapping`
   - Their fixture setup and expected assertion values.

5. **JXPath public API setup**
   - The relevant context/model construction API needed to evaluate the supplied XPath expression.
   - Namespace registration API and its expected precedence.

6. **JDOM dependency version**
   - Needed to verify behavior of:
     - `Element.getNamespace(String)`;
     - `Element.getAttributes()`;
     - namespace declaration representation;
     - `Attribute.getNamespace()` equality.

7. **DOM parser/document configuration**
   - Namespace-aware parser setup is necessary for reliable DOM namespace tests.
   - The source explicitly contains a fallback for DOM parsers with limited namespace-aware attribute support, so parser behavior affects branch selection.

8. **Formal `NodeIterator` contract**
   - Needed to determine whether invalid-position behavior, resetting to position zero, and `getNodePointer()` behavior after invalid `setPosition` calls should be asserted as contractual behavior or merely treated as current implementation details.

---

## Test-planning conclusion

The strongest reliable regression test oracle supplied is the existing namespace-mapping behavior:

- DOM evaluation of `vendor[1]/product[1]/rate:amount[1]/@rate:discount` must return **`"10%"`**, not `"20%"`.
- JDOM evaluation of the same expression must resolve successfully and must not throw `JXPathNotFoundException`.

Direct iterator tests are feasible only after obtaining the already-referenced project context for `QName`, `NodePointer`, concrete model pointers, and the existing namespace-mapping fixtures. Without that context, inventing constructors, XML fixtures, namespace mappings, or expected direct-pointer behavior would violate the stated constraints.