## 1. Public methods in `DatasetUtilities` that are testable

`DatasetUtilities` is a final utility class with only static public methods and a private constructor. The public API consists of these method groups:

### Pie-dataset operations
- `calculatePieDatasetTotal(PieDataset)`
- `createPieDatasetForRow(CategoryDataset, Comparable)`
- `createPieDatasetForRow(CategoryDataset, int)`
- `createPieDatasetForColumn(CategoryDataset, Comparable)`
- `createPieDatasetForColumn(CategoryDataset, int)`
- `createConsolidatedPieDataset(PieDataset, Comparable, double)`
- `createConsolidatedPieDataset(PieDataset, Comparable, double, int)`
- `isEmptyOrNull(PieDataset)`

### Category-dataset creation and inspection
- `createCategoryDataset(String, String, double[][])`
- `createCategoryDataset(String, String, Number[][])`
- `createCategoryDataset(Comparable[], Comparable[], double[][])`
- `createCategoryDataset(Comparable, KeyedValues)`
- `isEmptyOrNull(CategoryDataset)`
- `findRangeBounds(CategoryDataset)`
- `findRangeBounds(CategoryDataset, boolean)`
- `findRangeBounds(CategoryDataset, List, boolean)`
- `iterateCategoryRangeBounds(CategoryDataset, boolean)` — deprecated delegating method
- `iterateRangeBounds(CategoryDataset)`
- `iterateRangeBounds(CategoryDataset, boolean)`
- `iterateToFindRangeBounds(CategoryDataset, List, boolean)`
- `findMinimumRangeValue(CategoryDataset)`
- `findMaximumRangeValue(CategoryDataset)`
- `findStackedRangeBounds(CategoryDataset)`
- `findStackedRangeBounds(CategoryDataset, double)`
- `findStackedRangeBounds(CategoryDataset, KeyToGroupMap)`
- `findMinimumStackedRangeValue(CategoryDataset)`
- `findMaximumStackedRangeValue(CategoryDataset)`
- `findCumulativeRangeBounds(CategoryDataset)`

### Function sampling
- `sampleFunction2D(Function2D, double, double, int, Comparable)`
- `sampleFunction2DToSeries(Function2D, double, double, int, Comparable)`

### XY-dataset domain/range inspection
- `isEmptyOrNull(XYDataset)`
- `findDomainBounds(XYDataset)`
- `findDomainBounds(XYDataset, boolean)`
- `findDomainBounds(XYDataset, List, boolean)`
- `iterateDomainBounds(XYDataset)`
- `iterateDomainBounds(XYDataset, boolean)`
- `findRangeBounds(XYDataset)`
- `findRangeBounds(XYDataset, boolean)`
- `findRangeBounds(XYDataset, List, Range, boolean)`
- `iterateXYRangeBounds(XYDataset)` — deprecated delegating method
- `iterateRangeBounds(XYDataset)`
- `iterateRangeBounds(XYDataset, boolean)`
- `iterateToFindDomainBounds(XYDataset, List, boolean)`
- `iterateToFindRangeBounds(XYDataset, List, Range, boolean)`
- `findMinimumDomainValue(XYDataset)`
- `findMaximumDomainValue(XYDataset)`
- `findMinimumRangeValue(XYDataset)`
- `findMaximumRangeValue(XYDataset)`

### Table-XY stacking
- `findStackedRangeBounds(TableXYDataset)`
- `findStackedRangeBounds(TableXYDataset, double)`
- `calculateStackTotal(TableXYDataset, int)`

---

## 2. Input types and valid input ranges

### Dataset parameters
The class accepts the following dataset interfaces:

| Input type | Relevant methods |
|---|---|
| `PieDataset` | pie totals, consolidation, pie emptiness |
| `CategoryDataset` | pie conversion, category range calculations, category stacking, cumulative range |
| `IntervalCategoryDataset` | interval-aware category range calculations |
| `BoxAndWhiskerCategoryDataset` | visible-series category range calculations |
| `MultiValueCategoryDataset` | visible-series category range calculations |
| `StatisticalCategoryDataset` | visible-series category range calculations |
| `XYDataset` | domain/range calculations and empty check |
| `IntervalXYDataset` | interval-aware XY domain/range calculations |
| `OHLCDataset` | high/low-aware XY range calculations |
| `BoxAndWhiskerXYDataset` | min/max-regular-aware visible XY range calculations |
| `TableXYDataset` | stacking and stack-total calculations |

### Numeric inputs
- `minimumPercent`: conceptually a ratio such as `0.10` for ten percent. The implementation does **not** validate bounds, so negative values, values above `1.0`, `NaN`, and infinities are reachable inputs.
- `minItems`: no validation. Values below zero, zero, one, and values greater than the low-value item count are reachable.
- `start`, `end`: `sampleFunction2D*()` explicitly requires `start < end`.
- `samples`: `sampleFunction2D*()` explicitly requires `samples >= 2`.
- `base`: any `double` is accepted by stacked-range methods, including negative values, `NaN`, and infinities; no validation is implemented.
- Dataset values can be positive, negative, zero, `Double.NaN`, positive infinity, negative infinity, or `null` where the dataset API permits a `Number`.

### Index inputs
- Row/column/item indexes are `int`.
- The API documents zero-based indexes for pie-dataset conversion methods.
- No explicit index validation is performed by utility methods; behavior for negative or out-of-range indexes depends on the supplied dataset implementation.
- `calculateStackTotal(TableXYDataset, int)` does not validate the item index.

### Key inputs
- Keys are `Comparable`.
- Some methods document non-null keys but do not consistently validate them.
- Visible-series lists are raw `List` instances and are assumed to contain `Comparable` series keys.

### Arrays
For `createCategoryDataset(Comparable[], Comparable[], double[][])`:
- `rowKeys` and `columnKeys` must be non-null.
- Duplicate row or column keys are explicitly rejected.
- `rowKeys.length` must equal `data.length`.
- `columnKeys.length` must equal the maximum row length in `data`.
- The implementation assumes `data` and every `data[r]` are non-null; null handling is not explicitly implemented.

---

## 3. Reachable conditions and branches

### General dispatch branches
Many range methods dispatch by interface type:

- `DomainInfo` versus iteration.
- `RangeInfo` versus iteration.
- `XYDomainInfo` versus visible-series iteration.
- `XYRangeInfo` versus visible-series/x-range iteration.
- `CategoryRangeInfo` versus visible-series iteration.
- `IntervalCategoryDataset` versus ordinary `CategoryDataset`.
- `IntervalXYDataset`, `OHLCDataset`, and ordinary `XYDataset`.
- `BoxAndWhiskerCategoryDataset`, `IntervalCategoryDataset`, `MultiValueCategoryDataset`, `StatisticalCategoryDataset`, and ordinary category dataset.
- `OHLCDataset`, `BoxAndWhiskerXYDataset`, `IntervalXYDataset`, and ordinary XY dataset.

These type-dispatch branches should be covered independently, because the methods use different value accessors and different range semantics for each type.

### Common data branches
Relevant branches include:

- Null dataset.
- Empty dataset: no rows, columns, series, or items.
- Dataset with only null values.
- Dataset with only `NaN` values.
- Dataset with mixed valid values, null values, and `NaN`.
- Positive-only values.
- Negative-only values.
- Mixed positive and negative values.
- Zero values.
- Multiple rows/series and columns/items.
- Multiple visible series versus a subset of visible series.
- Empty `visibleSeriesKeys`.
- Values inside, on either endpoint of, and outside the supplied `xRange`.
- `includeInterval == true` and `includeInterval == false`.
- Range calculation resulting in a real `Range` versus no valid data resulting in `null`.

### Pie-specific branches
- Positive values are included in `calculatePieDatasetTotal()`.
- Zero, negative, and null values are ignored.
- Null keys in the key list are skipped by `calculatePieDatasetTotal()`.
- Consolidation when:
  - no values meet the percentage threshold;
  - fewer than `minItems` meet the threshold;
  - exactly `minItems` meet the threshold;
  - more than `minItems` meet the threshold;
  - source values are null;
  - total is zero, causing division by zero semantics in `value / total`.

### Function-sampling branches
- Valid function sampling produces exactly `samples` points.
- First x-value is `start`.
- Last x-value is `end`.
- Intermediate x-values are separated by `(end - start) / (samples - 1)`.
- Invalid arguments:
  - null `Function2D`;
  - null series key;
  - `start == end`;
  - `start > end`;
  - `samples < 2`.

### Stacking branches
- Positive values contribute to positive stack totals.
- Negative values contribute to negative stack totals.
- Zero values do not affect either side.
- Null values are ignored.
- `NaN` behavior differs by method and should not be assumed consistent without an oracle:
  - some methods explicitly ignore `NaN`;
  - some category stack methods do not explicitly filter it.
- Empty category dataset behavior differs across overloads:
  - `findStackedRangeBounds(CategoryDataset, double)` can derive a range from loop initialization only when categories exist;
  - map-based method returns `null` unless a non-null value was observed;
  - table-XY implementation starts at `base` and can return `[base, base]` for no items.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

### Explicitly specified/implemented exceptions
The following methods explicitly throw `IllegalArgumentException` for listed invalid arguments:

| Method family | Explicit invalid inputs |
|---|---|
| `calculatePieDatasetTotal()` | null dataset |
| `createCategoryDataset(Comparable[], Comparable[], double[][])` | null key arrays, duplicate keys, row-key/data-row mismatch, column-key/max-data-column mismatch |
| `createCategoryDataset(Comparable, KeyedValues)` | null row key, null row data |
| `sampleFunction2DToSeries()` and therefore `sampleFunction2D()` | null function, null key, `start >= end`, `samples < 2` |
| `findDomainBounds(XYDataset, ...)` | null dataset |
| `findRangeBounds(CategoryDataset, ...)` | null dataset |
| `findRangeBounds(XYDataset, ...)` | null dataset |
| `iterateDomainBounds(XYDataset, boolean)` | null dataset |
| `iterateToFindDomainBounds()` | null dataset, null visible-series list |
| `iterateToFindRangeBounds(CategoryDataset, List, boolean)` | null dataset, null visible-series list |
| `iterateToFindRangeBounds(XYDataset, List, Range, boolean)` | null dataset, null visible-series list, null x-range |
| `findMinimum/MaximumDomainValue()` | null dataset |
| `findMinimum/MaximumRangeValue()` | null dataset |
| `findStackedRangeBounds(CategoryDataset, ...)` | null dataset |
| `findStackedRangeBounds(TableXYDataset, ...)` | null dataset |
| `findMinimum/MaximumStackedRangeValue()` | null dataset |
| `findCumulativeRangeBounds()` | null dataset |

### Methods whose null-dataset behavior is not explicitly guarded
The source does not explicitly validate null in some public methods, despite documentation saying “null not permitted.” These methods will normally fail with a `NullPointerException` when dereferencing the dataset:

- `createPieDatasetForRow(...)`
- `createPieDatasetForColumn(...)`
- `createConsolidatedPieDataset(...)` indirectly fails through `calculatePieDatasetTotal()` with `IllegalArgumentException`, before later dereference.
- `createCategoryDataset(String, String, double[][])` when `data` is null.
- `createCategoryDataset(String, String, Number[][])` when `data` is null.
- `isEmptyOrNull(...)` overloads deliberately accept null and return `true`.
- `iterateRangeBounds(CategoryDataset, boolean)`
- `iterateRangeBounds(XYDataset, boolean)`
- `findStackedRangeBounds(CategoryDataset, KeyToGroupMap)` when `map` is null.
- `calculateStackTotal(TableXYDataset, int)` when `dataset` is null.

Tests should distinguish:
1. explicitly documented and checked `IllegalArgumentException` behavior;
2. documented non-null preconditions without an implemented check;
3. deliberately null-tolerant `isEmptyOrNull()` methods.

A test should not silently normalize these differences as though the current implementation were necessarily correct.

### Boundary cases to test
- Empty pie/category/XY datasets.
- One row, one column, one series, and one item.
- One visible key versus no visible keys.
- Values exactly equal to x-range lower and upper bounds, because `Range.contains()` is used.
- `minimumPercent` exactly equal to an item’s `value / total`: source uses `<`, not `<=`.
- `otherKeys.size()` exactly equal to `minItems`.
- `samples == 2`, the smallest valid sample count.
- `start` and `end` extremely close but ordered.
- Arrays with zero rows, zero columns, ragged rows, and duplicate keys.
- Interval datasets with:
  - central value only;
  - lower endpoint only;
  - upper endpoint only;
  - both endpoints;
  - null and/or `NaN` endpoints;
  - endpoints that differ from the central value.

---

## 5. Required constructors, dependencies, and external objects

No instance of `DatasetUtilities` is required or possible through normal access because its constructor is private.

Tests require concrete datasets or controlled test doubles implementing the relevant interfaces.

### Useful supplied production classes
The imports identify available concrete/supporting project types:

- `org.jfree.data.pie.DefaultPieDataset`
- `org.jfree.data.category.DefaultCategoryDataset`
- `org.jfree.data.xy.XYSeries`
- `org.jfree.data.xy.XYSeriesCollection`
- `org.jfree.data.Range`
- `org.jfree.data.KeyToGroupMap`

### Additional required context for specialized branches
To cover all branches, tests need concrete implementations or test fixtures for:

- `IntervalCategoryDataset`
- `BoxAndWhiskerCategoryDataset`
- `MultiValueCategoryDataset`
- `StatisticalCategoryDataset`
- `IntervalXYDataset`
- `OHLCDataset`
- `BoxAndWhiskerXYDataset`
- `TableXYDataset`
- datasets implementing `DomainInfo`, `RangeInfo`, `XYDomainInfo`, `XYRangeInfo`, and/or `CategoryRangeInfo`
- `KeyedValues`
- `Function2D`

The prompt does not supply source or constructors for these classes. Their exact constructors and behavior cannot be assumed. If existing project test fixtures exist, they are the preferred dependency source.

---

## 6. JUnit version and build tool

- **JUnit:** JUnit 4.12 with Hamcrest 1.3 (`junit-4.12-hamcrest-1.3.jar`)
- **Build tool:** Defects4J project build for Chart, using the project build file:
  - `/root/defects4j/framework/projects/Chart/Chart.build.xml`

Any future test should use JUnit 4 conventions, such as:
- `org.junit.Test`
- `org.junit.Assert.*`
- `@Test(expected = IllegalArgumentException.class)` where an exception is part of the known contract.

---

## 7. Available test oracle sources

The supplied material provides these oracle sources:

1. **Javadoc in `DatasetUtilities`**
   - Defines intended nullability for many parameters.
   - Defines return-value semantics for most range methods.
   - Explicitly states sampling restrictions.
   - Describes pie-total treatment of negative and null values.
   - Describes stacking semantics.

2. **Production implementation**
   - Provides observable current behavior and branch structure.
   - It is not a reliable correctness oracle by itself because this is a buggy source version.

3. **Bug metadata**
   - Fixed revision: `2242`.
   - Fixed date: 2009-09-15.
   - Modified source: `DatasetUtilities`.
   - Triggering tests:
     - `DatasetUtilitiesTests::testBug2849731_2`
     - `DatasetUtilitiesTests::testBug2849731_3`
   - Failure observed on the source version: `NullPointerException`.

4. **Change log in the class header**
   - States: “10-Sep-2009 : Fix bug 2849731 for IntervalCategoryDataset (DG).”

### Important limitation
The actual bug-report content and the code of the triggering tests are not supplied. Therefore, the exact expected result for the reported cases cannot be determined with full reliability from this prompt alone.

The test names refer to bug **2849731**, whereas project metadata calls the Defects4J bug **Chart-2** and lists SourceForge bug report ID **959**. The supplied information does not explain this numbering relationship.

---

## 8. Behaviors related to the supplied bug report that should be tested

The available evidence localizes the defect to interval-category range processing, most specifically the visible-series overload path:

```java
iterateToFindRangeBounds(CategoryDataset dataset,
                         List visibleSeriesKeys,
                         boolean includeInterval)
```

and, through delegation, potentially:

```java
findRangeBounds(CategoryDataset dataset,
                List visibleSeriesKeys,
                boolean includeInterval)
```

### Why this is the relevant path
- The class history explicitly identifies bug 2849731 as an `IntervalCategoryDataset` fix.
- The two triggering tests have names `testBug2849731_2` and `testBug2849731_3`.
- The reported failure is a `NullPointerException`.
- `iterateToFindRangeBounds(CategoryDataset, List, boolean)` has a dedicated `IntervalCategoryDataset` branch.

### Bug-focused test scenarios that should be designed
Tests should exercise an `IntervalCategoryDataset` with visible-series filtering and `includeInterval == true`, especially where interval-related values can be null.

1. **Null start value**
   - A visible series/item where `getStartValue(row, column)` is `null`.
   - Verify no `NullPointerException`.
   - The expected range needs an oracle for whether central value and/or end value should determine the resulting lower bound.

2. **Null end value**
   - A visible series/item where `getEndValue(row, column)` is `null`.
   - Verify no `NullPointerException`.
   - The expected range needs an oracle for the intended fallback behavior.

3. **Both interval endpoints null with non-null central value**
   - This is especially important because the current interval branch examines only start and end values, not `icd.getValue(row, column)`.
   - The intended result is not conclusively derivable from the supplied source alone. It may be `null`, or it may be expected to use the central value as a fallback; the absent triggering tests/fixed patch are required to decide.

4. **One endpoint null and central value non-null**
   - Determine whether the non-null endpoint alone is used, or whether the central value should substitute for the missing endpoint.

5. **Interval endpoints containing `NaN`**
   - Confirm that `NaN` endpoints are ignored without producing an invalid `Range`.

6. **Visible-series filtering**
   - Ensure values from non-visible rows do not affect results.
   - Ensure a visible key maps to the expected row.
   - An unknown visible key may yield `dataset.getRowIndex(key) == -1`; behavior is not guarded by this method and needs clarification before asserting an expected exception or range.

7. **`includeInterval == false`**
   - Confirm that the ordinary category-value path is used rather than interval endpoints.

8. **Delegation through `findRangeBounds(CategoryDataset, List, boolean)`**
   - For an `IntervalCategoryDataset` that does not implement `CategoryRangeInfo`, confirm it reaches the iteration path.
   - For an implementation that does implement `CategoryRangeInfo`, the utility delegates to the dataset; this does not directly test the iteration bug.

### Current source observation
The current `IntervalCategoryDataset` branch in `iterateToFindRangeBounds()` already contains null guards before invoking `doubleValue()`:

```java
if (lvalue != null && !Double.isNaN(lvalue.doubleValue())) { ... }
if (uvalue != null && !Double.isNaN(uvalue.doubleValue())) { ... }
```

Therefore, the reported NPE may arise from a condition not reconstructible from this class alone—for example, a particular specialized dataset implementation, data arrangement, row resolution, or a branch that is not evident without the triggering test code and related dataset source.

---

## 9. Missing context required for compilable and meaningful tests

The following information is missing for reliable, compilable bug-regression tests:

1. **The code of the triggering tests**
   - `DatasetUtilitiesTests::testBug2849731_2`
   - `DatasetUtilitiesTests::testBug2849731_3`
   - This is the most important missing artifact because it specifies the exact reproducer and expected result.

2. **The actual SourceForge bug report / bug description**
   - The supplied metadata provides a URL and identifier but not the report text.
   - The reported intended behavior, dataset structure, and acceptance criteria are unavailable.

3. **The fixed diff or fixed-version source for revision 2242**
   - The prompt identifies the revision but does not provide the patch.
   - Without it, expected fallback semantics for null interval endpoints cannot be established conclusively.

4. **Constructors/source for interval category datasets**
   - A concrete `IntervalCategoryDataset` implementation and its construction API are not supplied.
   - In particular, the prompt does not identify how to construct values with null start/end interval bounds while retaining central values.

5. **Existing project test fixtures**
   - The target class imports several specialized dataset interfaces but no concrete implementation construction details are provided.
   - Existing test utility classes may be needed to instantiate specialized dataset types without inventing mocks or unsupported constructors.

6. **Expected behavior for unknown visible series keys**
   - The source does not validate that each key exists before using its row/series index.
   - The API documentation does not state whether unknown keys should be ignored, rejected, or allowed to fail.

7. **Expected semantics for infinities and `NaN` in all methods**
   - Some methods explicitly filter `NaN`; others do not.
   - Javadocs do not define all infinity/`NaN` outcomes.

In summary, meaningful broad unit tests can be designed from the supplied Javadocs and implementation, but a **reliable, focused regression test for Chart-2 / bug 2849731 cannot be finalized without the triggering-test source, bug report text, or fixed patch**, because the exact interval-category null-handling oracle is not fully present in the prompt.