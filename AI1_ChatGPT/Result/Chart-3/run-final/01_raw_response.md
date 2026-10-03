## 1. Public API declared by `TimeSeries` that should be tested

### Constructors
- `TimeSeries(Comparable name)`
- `TimeSeries(Comparable name, String domain, String range)`

### Metadata and configuration
- `getDomainDescription()`
- `setDomainDescription(String description)`
- `getRangeDescription()`
- `setRangeDescription(String description)`
- `getMaximumItemCount()`
- `setMaximumItemCount(int maximum)`
- `getMaximumItemAge()`
- `setMaximumItemAge(long periods)`
- `getMinY()`
- `getMaxY()`
- `getTimePeriodClass()`

### Data and lookup accessors
- `getItemCount()`
- `getItems()`
- `getDataItem(int index)`
- `getDataItem(RegularTimePeriod period)`
- `getTimePeriod(int index)`
- `getNextTimePeriod()`
- `getTimePeriods()`
- `getTimePeriodsUniqueToOtherSeries(TimeSeries series)`
- `getIndex(RegularTimePeriod period)`
- `getValue(int index)`
- `getValue(RegularTimePeriod period)`

### Addition and update operations
- `add(TimeSeriesDataItem item)`
- `add(TimeSeriesDataItem item, boolean notify)`
- `add(RegularTimePeriod period, double value)`
- `add(RegularTimePeriod period, double value, boolean notify)`
- `add(RegularTimePeriod period, Number value)`
- `add(RegularTimePeriod period, Number value, boolean notify)`
- `update(RegularTimePeriod period, Number value)`
- `update(int index, Number value)`
- `addAndOrUpdate(TimeSeries series)`
- `addOrUpdate(RegularTimePeriod period, double value)`
- `addOrUpdate(RegularTimePeriod period, Number value)`
- `addOrUpdate(TimeSeriesDataItem item)`

### Removal and aging
- `removeAgedItems(boolean notify)`
- `removeAgedItems(long latest, boolean notify)`
- `clear()`
- `delete(RegularTimePeriod period)`
- `delete(int start, int end)`
- `delete(int start, int end, boolean notify)`

### Copying and object contract
- `clone()`
- `createCopy(int start, int end)`
- `createCopy(RegularTimePeriod start, RegularTimePeriod end)`
- `equals(Object obj)`
- `hashCode()`

Package-private `getRawDataItem(...)` methods are not public API and should not be directly targeted by tests outside the `org.jfree.data.time` package, though public-method tests will exercise them indirectly.

---

## 2. Input types and valid input ranges

| API area | Inputs | Validity/range identifiable from source |
|---|---|---|
| Constructors | `Comparable name` | Documentation says non-null; validation is delegated to `Series`, whose implementation is not supplied. |
| Domain/range descriptions | `String` | `null` expressly permitted. |
| Maximum item count | `int maximum` | Must be `>= 0`; default is `Integer.MAX_VALUE`. |
| Maximum item age | `long periods` | Must be `>= 0`; default is `Long.MAX_VALUE`. |
| Item indexes | `int index` | Valid indexes are normally `0` through `getItemCount() - 1`. Invalid index behavior comes from `List.get()`/`List.remove()`, normally `IndexOutOfBoundsException`. |
| Delete range | `int start`, `int end` | Requires `start <= end`; indexes must also refer to existing data for successful deletion. |
| Time period | `RegularTimePeriod` | Most documented as non-null. The series requires every stored period to have exactly one runtime class, established by the first item added. Periods must support ordering and serial indexes through `RegularTimePeriod`. |
| Values | `double` or `Number` | `Number` may be `null` for add/update APIs where stated. `Double.NaN` is accepted and is ignored for cached min/max calculation. The source does not reject infinities. |
| Data item | `TimeSeriesDataItem` | Non-null for `add(...)` and `addOrUpdate(TimeSeriesDataItem)`. Its period must be usable and match the series period class once established. |
| Series arguments | `TimeSeries` | `addAndOrUpdate()` and `getTimePeriodsUniqueToOtherSeries()` do not explicitly validate null. A null input will dereference and fail, likely with `NullPointerException`. |
| Copy range by periods | `RegularTimePeriod start/end` | Both non-null; `start.compareTo(end) <= 0`. The boundaries themselves need not be items in the source series. |
| Millisecond aging time | `long latest` | Any `long` is syntactically accepted and converted to `Date`. Correct behavior also depends on reflective creation of a matching `RegularTimePeriod` subtype. |

---

## 3. Reachable conditions and important branches

### Construction and basic state
- A newly constructed series has:
  - no data;
  - `timePeriodClass == null`;
  - `maximumItemCount == Integer.MAX_VALUE`;
  - `maximumItemAge == Long.MAX_VALUE`;
  - `minY` and `maxY` equal to `Double.NaN`.
- The one-argument constructor supplies default domain `"Time"` and range `"Value"`.
- The three-argument constructor permits null domain/range descriptions.

### Adding items
`add(TimeSeriesDataItem, boolean)` has these significant branches:

1. `item == null` → `IllegalArgumentException`.
2. First successfully added item establishes `timePeriodClass`.
3. Later item has a different period class → `SeriesException`.
4. Empty series → append item.
5. Period after current last period → append item.
6. Earlier non-duplicate period → insert at binary-search insertion point.
7. Duplicate period → `SeriesException`.
8. Added item causes `maximumItemCount` to be exceeded → remove first/oldest item.
9. Added item causes age policy to be exceeded → remove aged items.
10. `notify == true` → series-change notification; otherwise no notification from this addition.
11. Values that are `null` or `NaN` do not establish a normal min/max bound.

The implementation clones an incoming `TimeSeriesDataItem` before storing it. Therefore, a test should verify that later mutation of the caller’s item does not mutate the stored series item.

### Updating items
`update(int, Number)`:
- Updates an existing item at the specified index.
- If the old value was the current min or max, it recalculates bounds over all items.
- Otherwise it incrementally updates bounds if the new value is non-null.
- Always sends a series-change event after a successful indexed update.
- Invalid indexes rely on the underlying list exception.

`update(RegularTimePeriod, Number)`:
- Existing period → delegates to indexed update.
- Missing period → `SeriesException`.

`addOrUpdate(TimeSeriesDataItem)`:
- Null item → `IllegalArgumentException`.
- New period → inserts a cloned item in sorted order.
- Existing period → returns a clone of the overwritten item and changes the stored value.
- Different period class → `SeriesException`.
- Applies item-count aging and time-age aging.
- Always calls `fireSeriesChanged()` after the operation succeeds.

Notably, in the existing-period update branch, the source assigns:

```java
this.maxY = minIgnoreNaN(this.maxY, yy);
```

This appears inconsistent with:
- `getMaxY()` documentation (“largest y-value”); and
- the corresponding normal addition path, which uses `maxIgnoreNaN(...)`.

A test of updating a non-extreme existing value to a new larger value should cover this branch. The supplied source alone supports expecting `getMaxY()` to remain the largest non-NaN value because that is the public API contract.

### Bounds cache maintenance
Bounds are recalculated or updated after:
- add;
- add-or-update;
- indexed update;
- deletion;
- maximum-count eviction;
- aging removal;
- clear.

Relevant cases:
- Empty series → both bounds should be `Double.NaN`.
- Only null-valued items → both bounds remain `Double.NaN`.
- Only `Double.NaN` values → both bounds remain `Double.NaN`.
- Null and `NaN` are ignored when calculating bounds.
- Removing/replacing the current min or max triggers iteration to recalculate bounds.
- Removing/replacing a non-extreme value should not require complete recalculation, but results must remain correct.

### Item count and age policies
- `setMaximumItemCount(-1)` → `IllegalArgumentException`.
- Reducing maximum count below current count immediately removes items from the beginning.
- Setting maximum count to zero removes all current items.
- `setMaximumItemAge(-1)` → `IllegalArgumentException`.
- `setMaximumItemAge(...)` immediately calls `removeAgedItems(true)`.
- Age removal occurs when:

```java
latestSerialIndex - oldestSerialIndex > maximumItemAge
```

Thus, an item exactly `maximumItemAge` periods old is retained; one more period older is removed.
- `removeAgedItems(boolean)` only has aging work when more than one item exists.
- `removeAgedItems(long, boolean)` returns immediately for empty data.

### Deletion and clear
- `delete(RegularTimePeriod)`:
  - present period → removes it, updates bounds, notifies;
  - absent period → does nothing and does not explicitly notify;
  - deleting final item resets `timePeriodClass` to null.
- `delete(start, end, notify)`:
  - `end < start` → `IllegalArgumentException`;
  - otherwise removes inclusively;
  - resets period class when all items are removed;
  - notification depends on `notify`.
- `clear()`:
  - non-empty series → empties it, resets period class and bounds, notifies;
  - already empty series → no state change and no explicit notification.

### Copying
`clone()`:
- Performs a deep clone of the `data` list.
- Does not explicitly reset or recalculate cached `minY`/`maxY`.

`createCopy(int start, int end)`:
- `start < 0` → `IllegalArgumentException`.
- `end < start` → `IllegalArgumentException`.
- Copies inclusively from `start` through `end`.
- Valid non-empty copy ranges depend on source-list bounds; an over-large `end` will reach `this.data.get(index)` and normally cause `IndexOutOfBoundsException`.
- For an empty source series, the loop is skipped even if `end` would otherwise not be a valid data index.

`createCopy(RegularTimePeriod start, RegularTimePeriod end)`:
- null `start` or `end` → `IllegalArgumentException`.
- `start > end` → `IllegalArgumentException`.
- Supports boundary periods that are absent from the source:
  - starts at the first source item at or after `start`;
  - ends at the last source item at or before `end`;
  - returns an empty copy when no source items fall in the range.

### Equality and hash code
`equals(Object)` has branches for:
- same reference;
- null/non-`TimeSeries`;
- differing domain, range, time-period class, maximum age, maximum count, count, data, or superclass equality state;
- equivalent state.

`hashCode()` uses superclass state, metadata, period class, first/last/middle items, maximum count, and maximum age. The standard contract test is that equal series produce equal hash codes. Unequal series are not required to produce different hash codes.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

### Normal cases
- Create an empty series and verify defaults.
- Add chronological items and verify ordering, values, period class, count, min, and max.
- Add periods in non-chronological order and verify sorted insertion.
- Retrieve by index and by period.
- Update existing values.
- Add-or-update both a new and existing period.
- Merge two series using `addAndOrUpdate()`, checking overwritten data.
- Delete individual items and inclusive index ranges.
- Apply maximum count and maximum age.
- Clone and copy series.
- Verify equals/hashCode behavior for equal state.

### Boundary cases
- `maximumItemCount == 0`.
- `maximumItemCount == current count`.
- Reduction of maximum count from greater than current count, equal to current count, and less than current count.
- `maximumItemAge == 0`: only items with the same serial index as the latest are retainable under the `>` comparison; actual usefulness depends on period classes and serial indexes.
- An item exactly at the age threshold versus one beyond it.
- First and final indexes: `0` and `getItemCount() - 1`.
- Copy ranges containing:
  - exactly one item;
  - all items;
  - a start/end matching existing periods;
  - a start/end falling between stored periods;
  - a range entirely before or entirely after data.
- Values: negative values, zero, positive values, duplicate numeric values, `Double.NaN`, and null `Number`.

### Invalid and null cases explicitly defined by source
- `setMaximumItemCount(-1)` → `IllegalArgumentException`.
- `setMaximumItemAge(-1)` → `IllegalArgumentException`.
- `getIndex(null)` → `IllegalArgumentException`.
- Consequently, `getDataItem((RegularTimePeriod) null)`, `getValue((RegularTimePeriod) null)`, and `delete((RegularTimePeriod) null)` also reach this exception.
- `add((TimeSeriesDataItem) null)` / `add(null, boolean)` → `IllegalArgumentException`.
- `addOrUpdate((TimeSeriesDataItem) null)` → `IllegalArgumentException`.
- `update(period, value)` with a missing period → `SeriesException`.
- Adding a duplicate time period through `add(...)` → `SeriesException`.
- Adding a different `RegularTimePeriod` runtime class after the class has been established → `SeriesException`.
- `delete(start, end)` or `delete(start, end, notify)` where `end < start` → `IllegalArgumentException`.
- `createCopy(int, int)` with negative start or `end < start` → `IllegalArgumentException`.
- `createCopy((RegularTimePeriod) null, end)` and the inverse → `IllegalArgumentException`.
- `createCopy(start, end)` where start is after end → `IllegalArgumentException`.

### Exceptional behavior not fully specified in supplied context
The following are likely to throw standard runtime exceptions, but exact behavior should not be asserted more narrowly without confirming dependency implementations:
- Invalid item indexes for `getDataItem(int)`, `getTimePeriod(int)`, `getValue(int)`, `update(int, Number)`, and indexed deletion.
- `getNextTimePeriod()` on an empty series, because it requests index `-1`.
- Null `TimeSeries` passed to `addAndOrUpdate()` or `getTimePeriodsUniqueToOtherSeries()`.
- A null period passed through constructors or methods that create `TimeSeriesDataItem` before dereferencing its period; exact point and exception depend on `TimeSeriesDataItem`, which is not supplied.
- Failures in the reflective `RegularTimePeriod.createInstance(...)` invocation in `removeAgedItems(long, boolean)`. The source catches three reflection exceptions and prints stack traces, then retains `Long.MAX_VALUE` as the computed index. That behavior is visible, but it is not a robust API contract to assert without the `RegularTimePeriod` implementation.

---

## 5. Required constructors, dependencies, and external objects

### Required production types
Tests require the project’s implementations of:
- `org.jfree.data.time.RegularTimePeriod`
- A concrete `RegularTimePeriod` subtype, likely one of the project’s standard classes such as `Day`, `Month`, `Year`, etc.
- `org.jfree.data.time.TimeSeriesDataItem`
- `org.jfree.data.general.Series`
- `org.jfree.data.general.SeriesException`
- `org.jfree.data.event.SeriesChangeEvent`
- A usable series/property listener type, if notification behavior is tested.
- `org.jfree.chart.util.ObjectUtilities`

### External Java-platform types
- `Comparable`
- `Number`, `Double`
- `Date`
- `TimeZone`
- `Collection`, `List`
- Java reflection API, indirectly for millisecond-based aging.

### Construction needed for meaningful tests
At minimum:
1. Create a `TimeSeries` using a non-null `Comparable` key, commonly a `String`.
2. Create concrete compatible `RegularTimePeriod` objects.
3. Add values with the `add(...)` or `addOrUpdate(...)` overloads.
4. For event assertions, register an appropriate listener supported by the superclass `Series`.

The supplied source confirms that all items in a given series must use the same concrete time-period class. Tests that intentionally mix types need two distinct concrete subclasses.

---

## 6. JUnit version and build tool

- **JUnit:** `junit-4.12-hamcrest-1.3.jar`
- **Build tool:** Defects4J project build, with the project build file identified as:

  ```text
  /root/defects4j/framework/projects/Chart/Chart.build.xml
  ```

The triggering test name is:

```text
org.jfree.data.time.junit.TimeSeriesTests::testCreateCopy3
```

Its `junit.framework.AssertionFailedError` failure type indicates the existing test suite may contain JUnit 3-style tests or JUnit 3 compatibility usage, despite the supplied JUnit 4.12 dependency. A future test class should use the project’s established test conventions after inspecting existing test sources; those sources were not supplied here.

---

## 7. Available test oracles

### Source-level API documentation
The Javadoc in the supplied class is the principal specification. It specifies:
- nullability;
- validation requirements;
- ordering and uniqueness of periods;
- retention semantics;
- cloning/copying behavior;
- min/max semantics;
- notification intent;
- exception conditions for many public methods.

### Explicit bug information
The supplied defect metadata is a strong regression oracle:

- Project: `Chart`
- Bug: `Chart-3`
- Affected source: `org.jfree.data.time.TimeSeries`
- Triggering test: `TimeSeriesTests::testCreateCopy3`
- Observed failure:

  ```text
  expected:<101.0> but was:<102.0>
  ```

### Change-history oracle
The source header explicitly records the relevant intended fix:

```text
31-Aug-2009 : Clear minY and maxY cache values in createCopy (DG);
```

Because the supplied source is version `Chart-3b`, it contains the buggy behavior rather than that stated correction.

### Existing test suite
The name of a triggering existing test is supplied, but its source code is not. Therefore:
- its exact setup, selected copy range, assertion target (`getMinY()` or `getMaxY()`), and expected series values cannot be determined reliably from the prompt alone;
- no exact reconstruction of `testCreateCopy3` should be assumed.

---

## 8. Bug-report-related behavior that should be tested

The defect is directly associated with `createCopy(...)` and stale cached `minY`/`maxY` values.

### Root issue visible in the supplied source
Both `createCopy(...)` implementations create a copy by calling `super.clone()`. As a result, the copy inherits the original series’ cached `minY` and `maxY`.

In `createCopy(int, int)`, the copied series then has a new empty `data` list and adds selected cloned items. However, `add(...)` updates bounds incrementally from the inherited cached values rather than recalculating them from an empty/unknown bounds state.

Therefore, if the copied subset excludes the original minimum and/or maximum, the copied series can report min/max values that belong to the original series but not to the copy.

### Regression scenarios required
Tests should verify that, after `createCopy(...)`:

1. **Subset excludes original minimum**
   - Source contains a global minimum outside the copied range.
   - The copy’s `getMinY()` equals the minimum of copied items only.

2. **Subset excludes original maximum**
   - Source contains a global maximum outside the copied range.
   - The copy’s `getMaxY()` equals the maximum of copied items only.

3. **Subset contains neither source bound**
   - The copied range is an interior subset.
   - Both `getMinY()` and `getMaxY()` are based only on the copied data.

4. **Both overloads**
   - `createCopy(int start, int end)`;
   - `createCopy(RegularTimePeriod start, RegularTimePeriod end)`.

   The period-based overload eventually delegates to the indexed overload for a non-empty range, so both APIs should be covered as public entry points.

5. **Single-item copy**
   - Both `getMinY()` and `getMaxY()` should equal the single copied numeric value.

6. **Empty copy**
   - `getItemCount()` is zero;
   - `getMinY()` and `getMaxY()` should be `Double.NaN`, consistent with the documented empty-series contract.

7. **Source/copy isolation**
   - Subsequent modifications to the source do not alter the copy.
   - Subsequent modifications to the copy do not alter the source.
   - This is relevant because copying is intended to produce a new series and the data items are cloned.

The precise value sequence needed to reproduce the reported `expected:<101.0> but was:<102.0>` cannot be inferred because the triggering test body is absent. However, the expected result for any proposed regression test can reliably be derived from the documented `getMinY()`/`getMaxY()` contracts and the data included in the created copy.

---

## 9. Missing context required for fully reliable, compilable tests

The supplied source is sufficient to plan tests, but the following missing materials are needed to produce tests that are guaranteed to compile and align with project conventions:

1. **`TimeSeriesTests::testCreateCopy3` source**
   - Needed to reproduce the exact bug-triggering input and the exact failed assertion.

2. **Concrete `RegularTimePeriod` constructor/API details**
   - The target class refers to types such as `Day` and `Year`, but their constructors, equality, ordering, serial-index behavior, and date/time-zone behavior are not supplied.
   - A concrete period type and valid construction syntax must be confirmed before generating compilable tests.

3. **`TimeSeriesDataItem` implementation**
   - Needed to precisely determine behavior for null periods, clone semantics, comparison semantics, and mutability tests.

4. **Superclass `Series` and listener API**
   - Needed to write reliable event/property-change tests, including listener registration methods and event-count expectations.
   - It also determines the exact exception behavior for a null series key and portions of equality/hash-code behavior.

5. **Existing project test style**
   - Although JUnit 4.12 is supplied, the triggering test uses a JUnit 3-style failure class. Existing test source would establish whether new tests should use `org.junit.Test`, `TestCase`, JUnit 3 naming conventions, or a mixed compatibility setup.

6. **Defects4J build/test invocation details or relevant build configuration**
   - The build file location is provided, but source/test output layout and project-specific test commands are not supplied.

7. **Expected reflection behavior for `removeAgedItems(long, boolean)`**
   - The method depends on the non-public/static reflective method `RegularTimePeriod.createInstance(...)`. Its successful behavior cannot be fully tested without the corresponding class implementation.

No production code changes should be made at this stage.