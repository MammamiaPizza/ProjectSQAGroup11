## 1. Public methods to test

`org.apache.commons.csv.CSVRecord` publicly exposes:

1. `String get(int i)`
2. `String get(String name)`
3. `boolean isConsistent()`
4. `boolean isMapped(String name)`
5. `boolean isSet(String name)`
6. `Iterator<String> iterator()`
7. `String getComment()`
8. `long getRecordNumber()`
9. `int size()`
10. `String toString()`

The class also implements `Serializable` and `Iterable<String>`.

The package-private members are not public API:

- Constructor: `CSVRecord(String[] values, Map<String, Integer> mapping, String comment, long recordNumber)`
- `String[] values()`

A test class in package `org.apache.commons.csv` can directly construct `CSVRecord` instances and access `values()`. A test in another package cannot.

---

## 2. Input types and valid input ranges

### Constructor inputs

| Parameter | Type | Observed handling / range |
|---|---|---|
| `values` | `String[]` | May be `null`; constructor replaces it with a shared empty array. Array elements may be `null`. No defensive copy is made. |
| `mapping` | `Map<String, Integer>` | May be `null`. No validation is performed for key/value contents or index validity. |
| `comment` | `String` | May be `null`; returned unchanged by `getComment()`. |
| `recordNumber` | `long` | No range validation. Negative, zero, and positive values are accepted and returned unchanged. |

### Method inputs

| Method | Input | Relevant ranges / cases |
|---|---|---|
| `get(int)` | `int i` | Valid array indexes are `0` through `size() - 1`. Negative or `>= size()` indexes cause normal Java array-index failure. |
| `get(String)` | `String name` | May be `null` as far as this class is concerned; the behavior depends on `mapping.get(null)` when a mapping exists. |
| `isMapped(String)` | `String name` | May be `null`; behavior depends on `mapping.containsKey(null)` when mapping exists. |
| `isSet(String)` | `String name` | May be `null`; behavior depends on map support for null keys and the associated mapped index. |

No documented constraints are supplied for header names, duplicate headers, map index values, comments, record numbers, or null value elements.

---

## 3. Conditions and reachable branches

### `get(int i)`

Implementation:

```java
return values[i];
```

Reachable cases:

- Valid index: returns the corresponding array element, including `null`.
- Negative index: `ArrayIndexOutOfBoundsException`.
- Index equal to `size()`: `ArrayIndexOutOfBoundsException`.
- Index greater than `size()`: `ArrayIndexOutOfBoundsException`.
- With `values == null` passed to constructor: array is normalized to empty, so every index is invalid.

### `get(String name)`

Implementation branches:

1. `mapping == null`
   - Throws `IllegalStateException`.

2. `mapping != null`
   - Retrieves `Integer index = mapping.get(name)`.
   - `index == null`
     - Returns `null`.
     - This covers an absent key and, for typical map implementations, a key explicitly associated with `null`.
   - `index != null`
     - Returns `values[index.intValue()]`.
     - Valid index returns the value.
     - Invalid index can cause `ArrayIndexOutOfBoundsException` in the supplied source.

Important bug-related branch:

- If `mapping.size() != values.length`, `isConsistent()` returns `false`.
- The Javadoc for `get(String)` declares `IllegalArgumentException` “if the record is inconsistent.”
- The supplied implementation does **not** check consistency.
- Consequently, a mapping entry whose index is outside `values` can currently cause `ArrayIndexOutOfBoundsException`, which is the reported defect.

### `isConsistent()`

```java
return mapping == null ? true : mapping.size() == values.length;
```

Branches:

- No mapping: always `true`.
- Mapping present and map size equals number of values: `true`.
- Mapping present and map size differs from number of values: `false`.

The method does **not** validate that mapping values are valid indexes, unique, non-negative, or aligned with the map size.

### `isMapped(String name)`

```java
return mapping != null ? mapping.containsKey(name) : false;
```

Branches:

- No mapping: `false`.
- Mapping exists and contains key: `true`.
- Mapping exists but does not contain key: `false`.

### `isSet(String name)`

```java
return isMapped(name) && mapping.get(name).intValue() < values.length;
```

Branches:

1. Name is not mapped, including when `mapping == null`
   - Returns `false`.
   - Short-circuiting prevents `mapping.get(name)` from being evaluated.

2. Name is mapped and mapped integer is less than `values.length`
   - Returns `true`.

3. Name is mapped and mapped integer is greater than or equal to `values.length`
   - Returns `false`.

Additional malformed-map behavior reachable from the supplied implementation:

- A mapped key with a `null` mapped value:
  - `isMapped(name)` is `true`.
  - `mapping.get(name).intValue()` throws `NullPointerException`.
- A mapped key with a negative index:
  - `negativeIndex < values.length` is normally `true`.
  - Therefore `isSet(name)` may return `true`, although `get(name)` would fail with `ArrayIndexOutOfBoundsException`.
- The method does not call `isConsistent()`.

### `iterator()`

```java
return Arrays.asList(values).iterator();
```

Cases:

- Empty values: empty iterator.
- Non-empty values: iteration order follows the array order.
- Null elements are returned as `null`.
- The iterator is backed by the `values` array, because `Arrays.asList(values)` is array-backed.
- The precise `remove()` behavior is defined by the JDK iterator returned by `Arrays.asList`; it is normally unsupported. This is an implementation consequence rather than an explicitly documented `CSVRecord` contract.

### Accessors and utility methods

- `getComment()` returns the exact stored comment, including `null`.
- `getRecordNumber()` returns the exact supplied `long`.
- `size()` returns `values.length`; it is `0` when constructor `values` was `null`.
- `toString()` returns `Arrays.toString(values)`, including standard Java formatting for empty arrays and null elements.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

### Normal cases

- Constructing a record with values and no mapping.
- Constructing a record with values and a map whose size matches the value count.
- Indexed retrieval for first, middle, and last values.
- Named retrieval for an existing name with a valid mapped index.
- Named retrieval for a missing name when mapping exists: expected result is `null`.
- `isMapped()` and `isSet()` for known and unknown names.
- Iteration through all values in order.
- Returning comment, record number, size, and string representation.

### Boundary cases

- Empty values array.
- `values == null`, which becomes an empty record.
- Single value record.
- `get(0)` for one element.
- `get(size() - 1)` for non-empty records.
- `get(size())`, which is invalid.
- Mapping size exactly equal to values length.
- Mapping size one less or one greater than values length.
- Record numbers such as `0`, negative values, `Long.MIN_VALUE`, and `Long.MAX_VALUE`, if testing pass-through behavior.
- Empty string names and empty string value elements.

### Null cases

- `values == null` in the constructor: expected normalized empty record.
- `mapping == null`: name-based `get` must throw `IllegalStateException`; `isMapped` and `isSet` must return `false`; `isConsistent` must return `true`.
- `comment == null`: `getComment()` returns `null`.
- Null elements within `values`: `get`, iteration, and `toString` should follow normal array/JDK behavior.
- `name == null`: no class-level validation exists. With a conventional `Map` implementation that supports null keys, result depends on whether null is mapped. With a map implementation that rejects null keys, map operations may throw that map’s exception. A portable test should use a known map implementation and only assert its documented behavior.
- A map entry with a `null` Integer value: not a normal valid mapping; `get(name)` returns `null`, while `isSet(name)` can throw `NullPointerException`.

### Invalid / exceptional cases

- `get(int)` with invalid index: expected `ArrayIndexOutOfBoundsException` from direct array access.
- `get(String)` without mapping: expected `IllegalStateException`, explicitly documented.
- `get(String)` on an inconsistent record: expected `IllegalArgumentException` according to the Javadoc and supplied bug report, but the supplied source version does not meet that contract.
- Invalid mapping indexes:
  - Out-of-range mapped indexes can cause `ArrayIndexOutOfBoundsException` in the supplied implementation.
  - Negative mapping indexes can also cause `ArrayIndexOutOfBoundsException` in `get(String)`.
- `isSet(String)` with a key mapped to a null integer: `NullPointerException`.
- No validation exists to prevent malformed `Map` contents.

---

## 5. Required constructors, dependencies, and external objects

### Constructor access

The only constructor is package-private:

```java
CSVRecord(String[] values, Map<String, Integer> mapping,
          String comment, long recordNumber)
```

Therefore, direct unit tests should be declared in package:

```java
package org.apache.commons.csv;
```

No external parser instance is required to test this class directly.

### Dependencies

Production dependencies used directly by `CSVRecord` are JDK-only:

- `java.io.Serializable`
- `java.util.Arrays`
- `java.util.Iterator`
- `java.util.Map`

For tests, a concrete JDK map such as `HashMap<String, Integer>` or `LinkedHashMap<String, Integer>` would be sufficient. The supplied context does not require mocks or third-party test dependencies.

### Potential external object considerations

- `CSVParser` may normally create `CSVRecord` instances, but no parser source or parser behavior is supplied, and it is not required for direct unit tests of this class.
- Serialization testing is possible because the class implements `Serializable`, but no serialization contract beyond that marker interface is supplied. It is not necessary to cover the reported bug.

---

## 6. JUnit version and build tool

Supplied project configuration states:

- **JUnit version:** `junit-4.11.jar`
- **Build tool:** Maven

Tests should therefore use JUnit 4 conventions, such as:

- `@Test`
- `@Test(expected = SomeException.class)` where suitable
- `Assert.assertEquals`, `Assert.assertTrue`, `Assert.assertFalse`, `Assert.assertNull`, etc.

No JUnit 5 APIs should be assumed.

---

## 7. Available test oracle

The available oracle sources are:

1. **The `CSVRecord` Javadocs in the supplied production source**
   - Especially the contract for `get(String)`:
     - `IllegalStateException` if no header mapping was provided.
     - `IllegalArgumentException` if the record is inconsistent.
     - `null` if the name is not found.

2. **The bug report summary, CSV-96**
   - Triggering test:
     - `org.apache.commons.csv.CSVRecordTest::testGetStringInconsistentRecord`
   - Reported failure:
     - Expected: `IllegalArgumentException`
     - Actual: `ArrayIndexOutOfBoundsException`

3. **The implementation**
   - Useful for identifying reachable branches and malformed-input behavior.
   - It must not be treated as the authoritative expected behavior where it conflicts with the documented contract and bug report.

No actual source code for the existing `CSVRecordTest`, no full issue description, no fixed-version source diff, and no broader API documentation were supplied.

---

## 8. Bug-report behaviors that should be tested

The central regression behavior for Csv-2 / CSV-96 is:

- Given a record with a non-null header mapping where:
  ```java
  mapping.size() != values.length
  ```
  then:
  ```java
  record.isConsistent()
  ```
  should return `false`, and:
  ```java
  record.get(name)
  ```
  should throw `IllegalArgumentException`, as documented.

The test should specifically ensure that an inconsistent mapping does **not** leak an `ArrayIndexOutOfBoundsException`.

A representative defect-triggering setup is:

- `values` has fewer elements than the mapping size.
- The requested name maps to an index unavailable in `values`.

For example, conceptually:

```java
values = new String[] { "value0" };
mapping = { "header0" -> 0, "header1" -> 1 };
```

The record is inconsistent because mapping size is `2` and value count is `1`. Calling `get("header1")` is the scenario that currently accesses `values[1]` and produces `ArrayIndexOutOfBoundsException` in Csv-2b.

Additional bug-focused cases worth considering from the stated contract:

- Inconsistent record where the requested mapped name would otherwise point to a valid index.
- Inconsistent record with an unmapped requested name.

However, the supplied information does not explicitly establish whether `IllegalArgumentException` must take precedence for *every* name on an inconsistent record, including an unmapped name. The Javadoc wording (“if the record is inconsistent”) strongly suggests that it should, but the exact intended fixed implementation is not included. The direct triggering scenario above is fully supported by the supplied bug report.

---

## 9. Missing context and limitations

The supplied information is sufficient to create a meaningful direct regression test for the reported Csv-2 defect and unit tests for the explicit methods.

However, the following are missing and limit certainty for some tests:

1. **Existing `CSVRecordTest` source**
   - Only the triggering test name and its expected/actual exception types are supplied.
   - Exact setup, assertion message, and test style are unavailable.

2. **Full CSV-96 issue content or fixed-source diff**
   - The summary identifies the expected exception correction but does not state all intended edge cases or exception precedence rules.

3. **Project `pom.xml` and Maven test configuration**
   - Maven and JUnit 4.11 are explicitly supplied, but source/target Java version, package layout, and any custom Surefire configuration are not shown.

4. **Broader API contract for malformed maps**
   - No reliable expected behavior is specified for maps containing null integer values, negative indexes, indexes unrelated to map size, null-rejecting maps, or other custom map implementations.
   - Such cases can be described as current behavior but should not be asserted as intended API behavior without a stronger oracle.

5. **Parser integration behavior**
   - No `CSVParser` source or contract is supplied to establish whether all normally created records have valid mappings, how mappings are built, or whether direct malformed construction represents parser-reachable state.
   - This is not required for the reported direct `CSVRecord` regression test.

No JUnit test class is generated at this stage.