## 1. Public API surface to test

Declared public members of `org.apache.commons.jxpath.ri.compiler.CoreFunction`:

| Member | Test relevance |
|---|---|
| `CoreFunction(int functionCode, Expression[] args)` | Construction with recognized/unrecognized function codes and argument arrays, including `null`, empty, undersized, and oversized arrays. |
| `int getFunctionCode()` | Must return the code supplied to the constructor. |
| `Expression getArg1()` | Returns `args[0]`; requires a non-null array with at least one element. |
| `Expression getArg2()` | Returns `args[1]`; requires a non-null array with at least two elements. |
| `Expression getArg3()` | Returns `args[2]`; requires a non-null array with at least three elements. |
| `int getArgumentCount()` | Returns `0` for `null` arguments; otherwise returns `args.length`. |
| `boolean computeContextDependent()` | Depends on the function code, argument count, and inherited `Operation.computeContextDependent()` result. |
| `String toString()` | Renders the function name and argument expressions. |
| `Object compute(EvalContext context)` | Delegates directly to `computeValue(context)`. |
| `Object computeValue(EvalContext context)` | Main public function dispatcher; covers built-in function behavior. |

The following function implementations are `protected` or `private`, not public API, but are reachable through `computeValue` and therefore testable indirectly:

- `last`
- `position`
- `count`
- `lang`
- `id`
- `key`
- `local-name`
- `namespace-uri`
- `name`
- `string`
- `concat`
- `starts-with`
- `contains`
- `substring-before`
- `substring-after`
- `substring`
- `string-length`
- `normalize-space`
- `translate`
- `boolean`
- `not`
- `true`
- `false`
- `null`
- `number`
- `sum`
- `floor`
- `ceiling`
- `round`
- `format-number`

`getFunctionName()` is `protected`; it can be verified indirectly through `toString()` or directly only from a same-package test/subclass.

---

## 2. Inputs and valid ranges

### Constructor inputs

#### `functionCode`
- Type: `int`.
- Intended values: constants from `org.apache.commons.jxpath.ri.Compiler`, including:
  - `FUNCTION_LAST`
  - `FUNCTION_POSITION`
  - `FUNCTION_COUNT`
  - `FUNCTION_ID`
  - `FUNCTION_LOCAL_NAME`
  - `FUNCTION_NAMESPACE_URI`
  - `FUNCTION_NAME`
  - `FUNCTION_STRING`
  - `FUNCTION_CONCAT`
  - `FUNCTION_STARTS_WITH`
  - `FUNCTION_CONTAINS`
  - `FUNCTION_SUBSTRING_BEFORE`
  - `FUNCTION_SUBSTRING_AFTER`
  - `FUNCTION_SUBSTRING`
  - `FUNCTION_STRING_LENGTH`
  - `FUNCTION_NORMALIZE_SPACE`
  - `FUNCTION_TRANSLATE`
  - `FUNCTION_BOOLEAN`
  - `FUNCTION_NOT`
  - `FUNCTION_TRUE`
  - `FUNCTION_FALSE`
  - `FUNCTION_NULL`
  - `FUNCTION_LANG`
  - `FUNCTION_NUMBER`
  - `FUNCTION_SUM`
  - `FUNCTION_FLOOR`
  - `FUNCTION_CEILING`
  - `FUNCTION_ROUND`
  - `FUNCTION_KEY`
  - `FUNCTION_FORMAT_NUMBER`

No constructor validation is performed. Any `int`, including an unknown code, is accepted.

#### `args`
- Type: `Expression[]`.
- Permitted by the constructor: `null`, zero-length, or any-length array.
- Valid count depends on the selected function:
  - Exactly 0: `last`, `position`, `true`, `false`, `null`.
  - Exactly 1: `count`, `lang`, `id`, `starts-with` does **not** belong here, `boolean`, `not`, `sum`, `floor`, `ceiling`, `round`.
  - Zero or one: `local-name`, `namespace-uri`, `name`, `string`, `string-length`, `number`.
  - Two or more: `concat`.
  - Exactly 2: `starts-with`, `contains`, `substring-before`, `substring-after`, `normalize-space` actually exactly 1, `key`.
  - Exactly 3: `translate`.
  - Two or three: `substring`, `format-number`.

The source itself is the evidence for these arities. However, the intended XPath/JXPath contract beyond this implementation is not supplied.

### Evaluation inputs

#### `EvalContext context`
- Type: `org.apache.commons.jxpath.ri.EvalContext`.
- Most recognized functions require a non-null, appropriately initialized context.
- Certain functions additionally require:
  - Current node pointer: `last`, `position`, no-argument `local-name`, `namespace-uri`, `name`, `string`, `string-length`, `number`, default-locale `format-number`.
  - `JXPathContext`: `id`, `key`, `format-number`.
  - Iteration support: `last`, `count`, node-set functions, `sum`, and `key`.
- An unknown `functionCode` returns `null` before interacting with `context`; therefore `computeValue(null)` can return `null` for an unknown code.

#### Expression results
The class accepts expression results that may be:
- `NodePointer`
- `EvalContext`
- `Collection`
- `NodeSet`
- `String`
- `Number`
- `Boolean`
- `null`
- Other object types, depending on the function

The conversion semantics are delegated to `InfoSetUtil`, whose source/API is not included. Therefore, exact expected results for many conversion-related cases cannot be established reliably from the supplied material alone.

---

## 3. Reachable conditions and branches

### Dispatch branches in `computeValue`
Each recognized `Compiler.FUNCTION_*` constant dispatches to its matching implementation. Important additional dispatcher branches:

- `FUNCTION_NULL` is handled by `computeValue`, even though it is not named in `getFunctionName()` or handled in `computeContextDependent()`.
- An unknown function code reaches the default branch and returns `null`.

### Argument-count validation branches
`assertArgCount` and `assertArgRange` throw `JXPathInvalidSyntaxException` when too few or too many arguments are present.

Relevant arity branches include:

- No arguments required:
  - `last`, `position`, `true`, `false`, `null`.
- One argument required:
  - `count`, `lang`, `id`, `boolean`, `not`, `sum`, `floor`, `ceiling`, `round`, `normalize-space`.
- Two arguments required:
  - `starts-with`, `contains`, `substring-before`, `substring-after`, `key`.
- Three arguments required:
  - `translate`.
- Two or three:
  - `substring`, `format-number`.
- At least two:
  - `concat`.
- Zero or one:
  - `local-name`, `namespace-uri`, `name`, `string`, `string-length`, `number`.

For zero-or-one argument functions, any count greater than one is rejected. A zero argument count follows the context-node branch.

### `computeContextDependent()` branches
1. If `super.computeContextDependent()` returns `true`, the result is immediately `true`.
2. Always context-dependent:
   - `last`
   - `position`
3. Context-dependent only with no arguments:
   - `boolean`
   - `local-name`
   - `name`
   - `namespace-uri`
   - `string`
   - `lang`
   - `number`
4. Context-dependent when `format-number` has exactly two arguments.
5. Explicitly non-context-dependent:
   - `count`, `id`, `concat`, `starts-with`, `contains`,
     `substring-before`, `substring-after`, `substring`,
     `string-length`, `normalize-space`, `translate`, `not`,
     `true`, `false`, `sum`, `floor`, `ceiling`, `round`.
6. Any unlisted code, including `FUNCTION_NULL` and `FUNCTION_KEY`, reaches the final `false`.

The inherited `Operation.computeContextDependent()` behavior is unavailable, so tests that need to distinguish the first branch require the source or behavior of `Operation` and a suitable context-dependent `Expression`.

### Branches of particular interest

- `functionLast`
  - Resets the context.
  - Iterates all nodes.
  - Restores position only when the original position is not zero.
- `functionCount`
  - Unwraps `NodePointer` to its value.
  - Counts `EvalContext` contents, `Collection` size, `null` as zero, all other non-null values as one.
- `functionKey`
  - Empty expression-result `EvalContext` returns an empty `BasicNodeSet`.
  - One or multiple values result in calls to `JXPathContext.getNodeSetByKey`.
  - Multiple values accumulate results in `BasicNodeSet`.
- `local-name`, `namespace-uri`, and `name`
  - Zero arguments inspect the current node.
  - A non-empty `EvalContext` argument uses its first node.
  - Other argument result types and empty contexts return `""`.
- `substring`
  - Requires two or three arguments.
  - `NaN` start position returns `""`.
  - Rounds start and length via `Math.round`.
  - Has branches for start beyond the string, negative/zero start, negative length, end before first character, and end past the string.
- `normalize-space`
  - Handles XML whitespace characters: space, tab, carriage return, and line feed.
  - Removes leading/trailing whitespace and collapses internal sequences.
- `translate`
  - Replaces matching characters.
  - Deletes characters mapped beyond the replacement string length.
- `sum`
  - `null` expression result returns `ZERO`.
  - `EvalContext` result is iterated and summed.
  - Any other non-null result throws `JXPathException`.
- `format-number`
  - Two arguments use current node locale if available, otherwise the `JXPathContext` locale.
  - Three arguments obtain decimal symbols by name from `JXPathContext`.
  - Invalid formatting patterns may throw a formatting-related runtime exception; exact exception type is not established by the supplied source alone.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

### Normal cases
At a minimum, tests should cover each supported function through `computeValue` or through normal JXPath expression evaluation, subject to available project fixtures.

Particularly important normal cases include:
- Correct dispatch for each function code.
- Correct function rendering by `toString()`.
- Valid argument counts.
- Valid context/current-node behavior for no-argument functions.
- Valid `EvalContext` and `Collection` handling for `count`.
- Valid node sequence summation for `sum`.
- Valid two- and three-argument `substring`.
- Valid two- and three-argument `format-number`.

### Boundary cases
Relevant boundaries visible in the source include:

- Argument counts immediately below and above each accepted range.
- `concat` with exactly two arguments versus one and zero arguments.
- `substring`:
  - Start values near `1`.
  - Start at `0`, negative values, and values beyond `length + 1`.
  - Start equal to `length + 1`.
  - Length equal to `0`.
  - Negative length.
  - End equal to `1`.
  - End equal to `string.length() + 1`.
  - `NaN` start.
- `translate`:
  - Mapping string shorter than source mapping string.
  - Duplicate characters in the mapping string; `String.indexOf` causes the first occurrence to govern.
- `normalize-space`:
  - Empty string.
  - All-whitespace string.
  - Leading, internal, and trailing XML whitespace.
- `count`:
  - Empty `EvalContext`.
  - Empty/non-empty `Collection`.
  - `null`.
- `key`:
  - Empty and multiple-node expression-result contexts.
- `format-number`:
  - Two vs. three arguments.
  - Presence vs. absence of a current node pointer.

### Invalid cases
- Incorrect function arity should throw `JXPathInvalidSyntaxException`.
- `sum` supplied a non-null value that is not an `EvalContext` should throw `JXPathException`.
- Unknown function code:
  - `getFunctionName()` would render it as `unknownFunction<code>()`.
  - `computeValue()` returns `null`.
- `getArg1`, `getArg2`, and `getArg3` with a `null` or too-short argument array will naturally throw `NullPointerException` or `ArrayIndexOutOfBoundsException`; no explicit validation exists.

### Null cases
- Constructor accepts `args == null`.
- `getArgumentCount()` returns zero for `null` arguments.
- `toString()` handles `args == null` and produces a no-argument function representation.
- `functionCount`: a computed `null` value counts as `0.0`.
- `functionSum`: a computed `null` value returns the static `Double(0)`.
- `functionNull`: returns `null`.
- Several context-dependent functions can throw `NullPointerException` if context/current pointer is absent; the source does not provide a null-safe contract.
- Unknown function code can safely return `null` even with a null context.

### Exceptional cases
Explicit exceptions in this class:
- `JXPathInvalidSyntaxException`
  - Wrong argument count.
- `JXPathException`
  - `sum()` receives a non-null, non-`EvalContext` result.

Potential exceptions delegated to collaborators:
- `NullPointerException` for absent context/current pointer/JXPath context where dereferenced.
- `ClassCastException` where results are assumed to be `NodePointer`, notably in `functionKey` and `functionSum`.
- Formatting exceptions from `DecimalFormat.applyLocalizedPattern`.
- Any exceptions thrown by `Expression`, `EvalContext`, `NodePointer`, `JXPathContext`, or `InfoSetUtil`.

The exact expected behavior for collaborator-thrown exceptions cannot be fully determined from the supplied class alone.

---

## 5. Required constructors, dependencies, and external objects

### Required direct construction
```java
new CoreFunction(int functionCode, Expression[] args)
```

### Required supporting types
Tests exercising computation need real or test-double instances of:

- `Expression`
- `EvalContext`
- `NodePointer`
- `JXPathContext`
- Potentially `NodeSet` / `BasicNodeSet`
- `QName` or equivalent name object returned from `NodePointer.getName()`
- Locale / decimal-format symbol configuration for `format-number`

### Important dependency methods used by this class
The following collaborator behavior is needed to make direct unit tests meaningful:

| Dependency | Methods used |
|---|---|
| `Expression` | `compute(EvalContext)`, `computeValue(EvalContext)`, `toString()`, likely inherited context-dependency behavior |
| `EvalContext` | `getCurrentPosition()`, `reset()`, `nextNode()`, `setPosition(int)`, `hasNext()`, `next()`, `getSingleNodePointer()`, `getCurrentNodePointer()`, `getJXPathContext()` |
| `NodePointer` | `getValue()`, `isLanguage(String)`, `getPointerByID(JXPathContext, String)`, `getNamespaceURI()`, `getName()`, `getLocale()` |
| `JXPathContext` | `getContextPointer()`, `getNodeSetByKey(String, Object)`, `getDecimalFormatSymbols(String)`, `getLocale()` |
| `InfoSetUtil` | `stringValue`, `doubleValue`, `booleanValue`, `number` |
| `Operation` | Constructor, `getArguments()`, `computeContextDependent()` |

The supplied context does not include constructors or contracts for these types. Tests can potentially avoid extensive mocking by using existing JXPath parsing/evaluation infrastructure, but that infrastructure and its test patterns are not included.

---

## 6. JUnit version and build tool

Supplied project metadata specifies:

- **JUnit:** `junit-3.8.2.jar`
- **Build tool:** Maven

JUnit 3.8.2 implies tests should use JUnit 3 style, for example:
- Extend `junit.framework.TestCase`, or
- Use `junit.framework.TestSuite`.
- Test methods conventionally have the form `public void test...()`.

There is also a supplied Defects4J project build-file path ending in `JxPath.build.xml`. That confirms project-specific build infrastructure exists, but the actual Maven `pom.xml`, dependency declarations, module structure, and test source layout are not supplied. Therefore, the precise compilation command and test class placement cannot be verified from this prompt alone.

---

## 7. Available test oracle

### Explicit bug-report oracle
The strongest available oracle is the reported failing assertion:

> Evaluating `<round('NaN')>` expected `<NaN>` but was `<0.0>`

Therefore, the required corrected behavior is:

- Evaluating `round('NaN')` must produce a `Double` whose value is `NaN`.
- The test must not compare `Double.NaN` using ordinary `==`.
- Suitable JUnit 3 assertions include:
  ```java
  assertTrue(Double.isNaN(((Number) result).doubleValue()));
  ```
  or, if evaluation returns `Double`,
  ```java
  assertTrue(Double.isNaN(((Double) result).doubleValue()));
  ```

### Existing triggering test
The supplied information identifies:

- `org.apache.commons.jxpath.ri.compiler.CoreFunctionTest::testCoreFunctions`

However, its source is not provided. It cannot be used as a detailed test oracle beyond the supplied failure message.

### Source-derived oracle
The target source gives implementation-level behavior for:
- Dispatcher mappings.
- Argument-count validation.
- String rendering.
- Explicit exception paths.
- Several function calculations.

However, the request explicitly requires not assuming the current implementation is correct. Consequently, source behavior alone is insufficient as a reliable expected-result oracle for broad semantic tests, especially for XPath conformance behavior and `InfoSetUtil` conversions.

No formal API documentation, XPath conformance specification, fixed source diff, or full existing test source is provided.

---

## 8. Bug-report-specific behaviors to test

Bug JxPath-14 / JXPATH-102 concerns `round()` applied to NaN.

### Required regression scenario
- Construct or parse an expression equivalent to:
  ```xpath
  round('NaN')
  ```
- Evaluate it through the normal JXPath/CoreFunction path.
- Verify that the returned numeric value is NaN.

### Why this fails in the supplied source
The current implementation is:

```java
protected Object functionRound(EvalContext context) {
    assertArgCount(1);
    double v = InfoSetUtil.doubleValue(getArg1().computeValue(context));
    return new Double(Math.round(v));
}
```

For Java:
```java
Math.round(Double.NaN)
```
returns `0L`, which is then wrapped as `Double(0.0)`. This exactly explains the reported result.

### Additional regression-adjacent cases
The bug report only establishes the expected result for NaN. It does **not** explicitly establish requirements for these cases, though they are relevant to guarding a potential fix:

- `round()` of ordinary positive and negative finite values.
- `round()` of positive infinity.
- `round()` of negative infinity.
- `round()` of a value whose conversion through `InfoSetUtil.doubleValue` becomes NaN.
- Direct use of a `Double.NaN` expression result versus the string `"NaN"`.

Expected results for infinity and finite rounding should not be newly asserted unless established by existing project tests, documentation, XPath specifications, or the fixed-version implementation. The supplied bug report only makes the NaN expectation reliable.

---

## 9. Missing context required for compilable, meaningful tests

The following material is missing:

1. **`CoreFunctionTest` source**
   - Its fixture setup.
   - Existing helper methods.
   - How expressions are normally constructed/evaluated.
   - Existing expected behavior for other built-in functions.

2. **`Expression` and `Operation` source**
   - Required abstract methods.
   - Whether test-local expression stubs are practical.
   - The behavior of inherited `getArguments()` and `computeContextDependent()`.

3. **`EvalContext` source and usable implementations**
   - Constructor availability.
   - Required state and iteration semantics.
   - Whether a simple concrete context exists in the project.

4. **`NodePointer` source and concrete implementations**
   - Needed for current-node, language, locale, ID, name, and namespace tests.

5. **`InfoSetUtil` contract**
   - Especially how `"NaN"`, `null`, node pointers, collections, and other values convert to doubles, strings, booleans, and numbers.

6. **`Compiler` constants**
   - Their actual numeric values are unnecessary if tests compile against the project, but the class is needed to compile tests using named constants.

7. **The actual Maven configuration**
   - `pom.xml`, source/test directories, dependencies, compiler source level, and Surefire configuration.

8. **Formal function contract**
   - XPath/JXPath version semantics for all core functions.
   - Needed to write reliable broad functional tests without treating this potentially defective implementation as the oracle.

9. **Fixed-version source or patch**
   - Not required to identify the reported defect, but useful to determine whether the intended correction has additional behavior beyond preserving NaN in `round()`.

### Conclusion on test readiness
A focused, meaningful regression test for `round('NaN')` is supported by the supplied bug report, provided the project’s normal expression-evaluation fixture or APIs can be located in the supplied project context.

A broad, compilable direct-unit test suite for all `CoreFunction` branches cannot be generated reliably from this prompt alone because the necessary collaborator APIs, fixtures, and behavioral specifications are absent.