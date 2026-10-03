## Scope and target of analysis

The supplied source is the buggy source version **Chart-4b** for `org.jfree.chart.plot.XYPlot`. The only modified production class for Chart-4 is `XYPlot`.

The most relevant defect location in the supplied code is `XYPlot.getDataRange(ValueAxis axis)`. In its dataset loop, the implementation allows `r` (`XYItemRenderer`) to be `null` when calculating the data bounds, but subsequently dereferences it unconditionally:

```java
XYItemRenderer r = getRendererForDataset(d);
if (isDomainAxis) {
    if (r != null) {
        result = Range.combine(result, r.findDomainBounds(d));
    }
    else {
        result = Range.combine(result,
                DatasetUtilities.findDomainBounds(d));
    }
}
else {
    if (r != null) {
        result = Range.combine(result, r.findRangeBounds(d));
    }
    else {
        result = Range.combine(result,
                DatasetUtilities.findRangeBounds(d));
    }
}

Collection c = r.getAnnotations();  // NPE when r == null
```

This is consistent with the supplied bug evidence: a `NullPointerException` occurs in auto-range, chart creation/drawing, serialization, dataset replacement, and range-gridline tests.

---

# 1. Public methods that should be tested

`XYPlot` has a large public API. For Chart-4 regression testing, the following methods are the primary targets.

## Directly bug-relevant methods

1. **Constructors**
   - `XYPlot()`
   - `XYPlot(XYDataset dataset, ValueAxis domainAxis, ValueAxis rangeAxis, XYItemRenderer renderer)`

2. **Dataset/renderer configuration**
   - `setDataset(XYDataset dataset)`
   - `setDataset(int index, XYDataset dataset)`
   - `getDataset()`
   - `getDataset(int index)`
   - `setRenderer(XYItemRenderer renderer)`
   - `setRenderer(int index, XYItemRenderer renderer)`
   - `setRenderer(int index, XYItemRenderer renderer, boolean notify)`
   - `getRenderer()`
   - `getRenderer(int index)`
   - `getRendererForDataset(XYDataset dataset)`
   - `datasetChanged(DatasetChangeEvent event)`

3. **Axis configuration and auto-range path**
   - `setDomainAxis(ValueAxis axis)`
   - `setDomainAxis(int index, ValueAxis axis)`
   - `setRangeAxis(ValueAxis axis)`
   - `setRangeAxis(int index, ValueAxis axis)`
   - `configureDomainAxes()`
   - `configureRangeAxes()`
   - `getDataRange(ValueAxis axis)` — the principal direct regression target.
   - `getDomainAxisForDataset(int index)`
   - `getRangeAxisForDataset(int index)`

4. **Drawing paths implicated by triggering tests**
   - `draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info)`
   - `render(Graphics2D g2, Rectangle2D dataArea, int index, PlotRenderingInfo info, CrosshairState crosshairState)`
   - `drawBackground(Graphics2D g2, Rectangle2D area)`
   - `drawDomainTickBands(Graphics2D g2, Rectangle2D dataArea, List ticks)`
   - `drawRangeTickBands(Graphics2D g2, Rectangle2D dataArea, List ticks)`
   - `drawAnnotations(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info)`
   - `getLegendItems()`

5. **Serialization/clone/equality paths implicated by triggering tests**
   - `equals(Object obj)`
   - `clone()`
   - Java serialization externally exercised through `ObjectOutputStream` / `ObjectInputStream`; `writeObject()` and `readObject()` themselves are private.

## Other public API areas deserving ordinary unit coverage

These are not directly shown to be related to Chart-4, but are public behavior exposed by this class:

- Orientation and axis offset:
  - `getOrientation()`, `setOrientation(PlotOrientation)`
  - `getAxisOffset()`, `setAxisOffset(RectangleInsets)`

- Axis locations, counts, and clearing:
  - all `getDomainAxis*`, `setDomainAxis*`, `getRangeAxis*`, `setRangeAxis*`
  - `clearDomainAxes()`, `clearRangeAxes()`
  - `getDomainAxisCount()`, `getRangeAxisCount()`

- Dataset-to-axis mapping:
  - `mapDatasetToDomainAxis(...)`, `mapDatasetToDomainAxes(...)`
  - `mapDatasetToRangeAxis(...)`, `mapDatasetToRangeAxes(...)`

- Render settings:
  - rendering-order setters/getters
  - gridline, baseline, tick-band, and quadrant APIs
  - crosshair APIs
  - panning and zoom APIs

- Markers and annotations:
  - `add...Marker`, `remove...Marker`, `clear...Markers`, `get...Markers`
  - `addAnnotation`, `removeAnnotation`, `getAnnotations`, `clearAnnotations`

- Selection:
  - `canSelectByPoint()`, `canSelectByRegion()`
  - both `select(...)` overloads
  - `clearSelection()`

A comprehensive test suite could cover all of these, but a focused Chart-4 regression test should prioritize the first group.

---

# 2. Input types and valid input ranges

## Bug-relevant inputs

### `getDataRange(ValueAxis axis)`

- Input type: `ValueAxis`.
- The source does not explicitly reject `null`.
- Meaningful valid inputs are:
  - the plot’s primary domain axis;
  - the plot’s primary range axis;
  - a configured secondary domain/range axis;
  - an axis inherited through a parent `XYPlot`, where applicable.
- An axis that is not present in the plot is not explicitly rejected. The resulting range behavior is not fully documented in the supplied source and should not be asserted without an oracle.

### Constructor parameters

```java
XYPlot(XYDataset dataset,
       ValueAxis domainAxis,
       ValueAxis rangeAxis,
       XYItemRenderer renderer)
```

The Javadoc explicitly permits `null` for every argument:

- `dataset`: `XYDataset`, nullable.
- `domainAxis`: `ValueAxis`, nullable.
- `rangeAxis`: `ValueAxis`, nullable.
- `renderer`: `XYItemRenderer`, nullable.

The null-renderer state is central to the defect. The class explicitly supports a `null` renderer in constructor and setter documentation, so operations which only need dataset bounds must not necessarily assume a renderer is present.

### Dataset input

- `XYDataset`, nullable for `setDataset(...)`.
- Meaningful data values depend on the concrete dataset implementation.
- For deterministic range testing, a dataset with simple finite numeric x/y values should be used.
- Empty datasets and null datasets are meaningful conditions because `DatasetUtilities.isEmptyOrNull(dataset)` is checked in `render()`.

### Axis input

- Concrete `ValueAxis` subclasses such as `NumberAxis` and `LogAxis` are implied by the supplied triggering-test names.
- Auto-range behavior is delegated to the axis and relies on `XYPlot.getDataRange(...)`.
- Numeric axis values must be selected to satisfy the particular axis’s contract:
  - `NumberAxis` can be used with ordinary finite numeric values.
  - `LogAxis` normally requires values valid for logarithmic interpretation; exact permitted values cannot be established solely from the supplied `XYPlot` source.

### Renderer input

- `XYItemRenderer`, nullable.
- A renderer can exist:
  - at the same dataset index;
  - only at renderer index `0`, causing fallback in `getRendererForDataset(...)` or `render(...)`;
  - not at all, yielding `null`.

## Explicitly validated index/range inputs in supplied code

| Method area | Validity rule visible in source |
|---|---|
| `getQuadrantPaint(int)` / `setQuadrantPaint(int, Paint)` | index must be `0..3`; otherwise `IllegalArgumentException`. |
| `mapDatasetToDomainAxes(int, List)` | dataset index must be `>= 0`; otherwise `IllegalArgumentException`. |
| `mapDatasetToRangeAxes(int, List)` | dataset index must be `>= 0`; otherwise `IllegalArgumentException`. |
| Mapping list | may be `null`; otherwise must be non-empty, contain only unique `Integer` instances. |
| `getDomainAxisForDataset(int)` | index must be `0 <= index < max(datasetCount, rendererCount)`; otherwise `IllegalArgumentException`. |
| `getRangeAxisForDataset(int)` | same rule as domain-axis version. |
| Primary axis locations | index `0` may not have a null `AxisLocation`; otherwise `IllegalArgumentException`. |

For numerous other index-based getters/setters, the supplied source does not explicitly validate negative indexes. A test should not assume the exact exception or behavior without inspecting the dependent `ObjectList` implementation, which was not supplied.

---

# 3. Conditions and reachable branches

## Principal Chart-4 branches in `getDataRange()`

### A. Axis classification

1. **Input axis is a configured domain axis**
   - `domainIndex >= 0`.
   - Datasets mapped to that domain axis are considered.
   - Plot-level annotations implementing `XYAnnotationBoundsInfo` are included only when `domainIndex == 0`.

2. **Input axis is a configured range axis**
   - `rangeIndex >= 0`.
   - Datasets mapped to that range axis are considered.
   - Plot-level bounds-aware annotations are included only when `rangeIndex == 0`.

3. **Axis may be both a domain and range axis**
   - The source evaluates domain membership and then range membership.
   - If it is found as a range axis, `isDomainAxis` becomes false and range mappings are additionally accumulated.
   - This unusual configuration is reachable in principle, but expected semantics are not established by the supplied documentation.

4. **Axis belongs to neither axis list**
   - No mapped datasets are added.
   - The method may return `null` unless annotation behavior changes the result.
   - The API contract for this case is not sufficiently specified.

### B. Dataset iteration

For every mapped dataset:

1. Dataset is `null`
   - Skipped.

2. Dataset is non-null and renderer is non-null
   - Uses renderer-provided bounds:
     - `r.findDomainBounds(d)` or
     - `r.findRangeBounds(d)`.
   - Then retrieves renderer annotations with `r.getAnnotations()`.

3. Dataset is non-null and renderer is null
   - Uses fallback:
     - `DatasetUtilities.findDomainBounds(d)` or
     - `DatasetUtilities.findRangeBounds(d)`.
   - **Current source then dereferences `r` and throws `NullPointerException`.**
   - This is the key faulty branch.

4. Renderer has annotations:
   - Each `XYAnnotationBoundsInfo` is collected.
   - Only annotations whose `getIncludeInDataBounds()` returns true contribute their x or y range.

### C. Result accumulation

- `Range.combine(result, nextRange)` handles the accumulation.
- No mapped data and no included bounds-aware annotations can leave the result as `null`.
- The exact behavior of `Range.combine` is external context, but returning a `Range` or `null` is consistent with the local code.

## Related drawing branches

### `draw(...)`

Reachable important branches:

- Returns immediately when plot area is no larger than the inherited minimum drawing dimensions.
- Works with `info == null`, explicitly documented as permitted.
- Works with `anchor == null`, explicitly documented as permitted.
- Supports forward and reverse dataset rendering orders.
- Uses a `CrosshairState`.
- Can proceed with null datasets and null renderers in many branches, but particular optional features can require renderers or axes.

### `drawRangeGridlines(...)`

- Returns early if `getRenderer() == null`.
- Draws only when major or minor range gridlines are enabled.
- Draws only when the range axis is non-null.
- For major/minor ticks, chooses different stroke/paint.
- Suppresses an ordinary zero gridline when the zero baseline is enabled.
- This matches the named triggering test `XYPlotTests::testDrawRangeGridlines`, although the test body was not supplied.

### `render(...)`

- Empty/null dataset: returns `false`.
- Non-empty dataset with missing domain/range axis: returns `true` without rendering items.
- Non-empty dataset with no indexed or default renderer: returns `true` without rendering items.
- Supports forward/reverse series order.
- Supports visible-items-only renderer state.
- Skips empty series in reverse series order, but the forward-order branch still invokes pass-start/end even where item count is zero.

---

# 4. Normal, boundary, invalid, null, and exceptional cases

## High-priority Chart-4 regression cases

| Category | Scenario | Expected reliable assertion |
|---|---|---|
| Normal | Dataset + domain/range axes + renderer | Auto-range/data-range calculation completes and yields bounds according to dataset/renderer behavior. |
| Null renderer | Dataset + axes + `null` renderer | `getDataRange(axis)` must not throw `NullPointerException`; fallback dataset-bound calculation is the intended reachable branch shown by the source. |
| Null renderer via setter | Construct plot normally, then `setRenderer(null)` | Later auto-range/data-range processing must not throw `NullPointerException`. |
| Null renderer with dataset replacement | Plot with axes, `setDataset(replacementDataset)` while renderer is null | Dataset-change/axis-configuration path must not throw `NullPointerException`. |
| Default constructor | `new XYPlot()` with no dataset, axes, or renderer | Construction should succeed; operations explicitly documented to accept absent objects should be tested cautiously. |
| Null dataset | `setDataset(null)` | Must not fail merely because the dataset is null; source creates `DatasetChangeEvent(this, null)`. |
| Empty dataset | Empty but non-null `XYDataset` | Rendering identifies it as empty; auto-range exact expected value requires axis/dataset oracle. |
| Renderer annotation case | Non-null renderer that has `XYAnnotationBoundsInfo` annotations | Included annotations contribute only when `getIncludeInDataBounds()` is true. Exact range values require a suitable concrete annotation/renderer. |
| Plot annotation case | Plot-level `XYAnnotationBoundsInfo` | Included only for primary axis index `0`, and only when `getIncludeInDataBounds()` is true. |
| Serialization | Serialize/deserialize a chart/plot whose renderer configuration makes `getDataRange()` use the fallback path | No NPE during workflow; preservation details require serializable dependencies. |
| Drawing with null info | Call `draw(..., null)` with valid graphics/area | Must not throw due solely to `info == null`; `draw()` Javadoc permits it. |

## Boundary cases

1. **Primary versus secondary axis**
   - Dataset mapped to axis index `0`.
   - Dataset mapped to a non-primary axis.
   - Bounds-aware plot annotations should be included for axis `0` only according to source.

2. **Dataset/renderer slot boundaries**
   - One dataset / one renderer.
   - Multiple datasets with:
     - a renderer at each index;
     - no renderer at a secondary index but default renderer at index `0`;
     - no renderer at any index.

3. **Mapping list boundaries**
   - `null` list: permitted.
   - one unique `Integer`: permitted.
   - empty list: `IllegalArgumentException`.
   - duplicate `Integer`: `IllegalArgumentException`.
   - non-`Integer` element: `IllegalArgumentException`.

4. **Quadrant boundaries**
   - legal indexes `0` and `3`;
   - illegal indexes `-1` and `4`.

5. **Draw area boundary**
   - area at or below the inherited minimum draw dimensions returns without normal rendering.
   - Exact threshold values are not supplied because `MINIMUM_WIDTH_TO_DRAW` and `MINIMUM_HEIGHT_TO_DRAW` are inherited from `Plot`; no direct threshold assertion should be written without that class.

## Invalid/null/exceptional cases explicitly specified by this source

The following null inputs should produce `IllegalArgumentException`:

- `setOrientation(null)`
- `setAxisOffset(null)`
- `setDomainAxisLocation(0, null, ...)`
- `setRangeAxisLocation(0, null, ...)`
- all non-null-required gridline/baseline/crosshair stroke and paint setters
- `setQuadrantOrigin(null)`
- invalid quadrant index
- `addDomainMarker(..., null, ...)`
- `addRangeMarker(...)` does **not** explicitly validate null before calling `marker.addChangeListener(this)`; this would currently cause an NPE rather than the documented `IllegalArgumentException`. This is unrelated to Chart-4 and should not be treated as a Chart-4 expected behavior.
- `addAnnotation(null, ...)`
- `removeAnnotation(null, ...)`
- `removeRangeMarker(..., null, ...)`
- negative index in mapping methods
- invalid mapping-list contents
- invalid index in `getDomainAxisForDataset()` / `getRangeAxisForDataset()`.

## Reliability limitation for expected results

The supplied source alone does **not** establish exact numeric expected ranges for:

- `NumberAxis` auto-ranging;
- `LogAxis` auto-ranging;
- effects of axis margins;
- renderer-specific `findDomainBounds()` / `findRangeBounds()`;
- concrete renderer annotations.

Those expected values require either the relevant axis/renderer/dataset source, supplied existing test bodies, or a formal API specification. The reliable bug-regression oracle available here is primarily: **the null-renderer fallback path must not produce an NPE and must use dataset utilities for data bounds.**

---

# 5. Required constructors, dependencies, and external objects

## Minimum direct setup for `getDataRange()` regression coverage

A meaningful test needs:

1. An `XYPlot` instance:
   ```java
   new XYPlot(dataset, domainAxis, rangeAxis, renderer)
   ```

2. A concrete `XYDataset` with known finite values.

3. A concrete `ValueAxis` for each dimension:
   - likely `NumberAxis` for a baseline test;
   - `LogAxis` for coverage matching named triggering tests, provided valid logarithmic data are chosen.

4. A renderer configuration:
   - `null` renderer for the defect branch;
   - optionally a concrete `XYItemRenderer` for non-null renderer branch comparison.

5. Invocation of:
   - `plot.getDataRange(domainAxis)`;
   - `plot.getDataRange(rangeAxis)`;
   - or an axis auto-range configuration workflow that invokes `getDataRange()` indirectly.

## Setup for drawing tests

To test `draw(...)` or gridline-related execution:

- `Graphics2D`, typically from a `BufferedImage`;
- nontrivial `Rectangle2D` drawing area;
- `XYPlot` with axes;
- concrete renderer if gridlines/data rendering are to be exercised;
- optional `PlotRenderingInfo`, or `null` where the test is specifically validating the documented null-info path;
- optional `Point2D` anchor;
- `PlotState` may be `null`, per Javadoc.

## Setup for serialization tests

- A serializable `XYPlot` configuration.
- `ByteArrayOutputStream`, `ObjectOutputStream`, `ByteArrayInputStream`, `ObjectInputStream`.
- Serializable datasets, axes, renderers, annotations, paints, and strokes selected for the test.

## Setup for selection tests

Not required for Chart-4. If later tested, the region-selection path requires:

- a `SelectableXYDataset` or suitable `AbstractXYDataset` supporting selection state;
- a `GeneralPath`;
- data area;
- a `RenderingSource`.

---

# 6. JUnit version and build tool

- **JUnit:** `junit-4.12-hamcrest-1.3.jar`
  - Tests should use JUnit 4 conventions:
    - `@Test`
    - `org.junit.Assert`
    - optionally `@Test(expected = ...)` for explicit exception contracts.

- **Build tool:** **Defects4J project build**
  - The supplied project context identifies the Chart Defects4J build file as:
    `/root/defects4j/framework/projects/Chart/Chart.build.xml`
  - No Maven or Gradle configuration was supplied, so tests should be assumed to run through Defects4J’s Chart build/test mechanism rather than a standalone Maven/Gradle command.

---

# 7. Available test oracle

## Available sources of oracle evidence

1. **`XYPlot` Javadoc in the supplied source**
   - Explicit nullability and exception contracts.
   - Explicit semantics for mappings, axes, gridlines, drawing, and annotations.

2. **Bug metadata**
   - Bug ID: `Chart-4`.
   - Fixed revision: `2183`.
   - Modified source: only `org.jfree.chart.plot.XYPlot`.
   - Failure mode: `NullPointerException`.

3. **Named triggering tests**
   - Axis auto-range:
     - `LogAxisTests::testXYAutoRange1`
     - `LogAxisTests::testXYAutoRange2`
     - `NumberAxisTests::testXYAutoRange1`
     - `NumberAxisTests::testXYAutoRange2`
     - `ValueAxisTests::testAxisMargins`
   - Chart workflows:
     - serialization;
     - drawing with null rendering info;
     - replacing datasets;
     - setting series tooltip generators;
     - range gridline drawing.

These names establish affected workflows but do not provide their exact setup or assertions because their source bodies were not supplied.

## Strong regression oracle

The strongest directly supported oracle is:

> When `getDataRange()` processes a non-null dataset but `getRendererForDataset(dataset)` returns `null`, it must not throw `NullPointerException`.

The source itself shows intended fallback bounds computation with `DatasetUtilities.findDomainBounds(d)` or `DatasetUtilities.findRangeBounds(d)`. Therefore, the null-renderer path is not an invalid caller state; it is explicitly handled until the erroneous unconditional call to `r.getAnnotations()`.

## Oracle limitations

The supplied information is insufficient to determine:

- exact auto-range bounds, margins, and log-axis values;
- whether all annotations should be considered if no renderer exists;
- exact fixed-version behavior for null renderers with annotation handling;
- the original assertion logic in the listed triggering tests.

No bug report text, fixed diff, API documentation external to this source, or existing test implementations were supplied.

---

# 8. Behaviors related to Chart-4 that should be tested

## Core regression behavior

### A. Auto-range with a dataset and no renderer

Create a plot with:

- a non-null `XYDataset`;
- non-null domain and range `ValueAxis` instances configured for auto-range;
- `null` renderer.

Exercise the axis/plot data-range calculation path.

Assertions should establish at minimum:

- no `NullPointerException`;
- data bounds are obtainable through the fallback path;
- auto-range configuration completes.

This directly corresponds to the `NumberAxisTests`, `LogAxisTests`, and `ValueAxisTests` failures.

### B. Dataset replacement while the renderer is absent

Because `setDataset(...)` immediately invokes:

```java
datasetChanged(new DatasetChangeEvent(this, dataset));
```

and `datasetChanged(...)` calls:

```java
configureDomainAxes();
configureRangeAxes();
```

replacing a dataset can trigger auto-range and therefore `getDataRange()`.

Test:

1. Create plot with axes and null renderer.
2. Replace the dataset with a non-null valid dataset.
3. Verify the operation does not throw `NullPointerException`.

This corresponds to named `testReplaceDataset` failures.

### C. Chart creation/drawing with `PlotRenderingInfo == null`

`draw(...)` documents `info` as nullable. A chart created through factory methods may run auto-range before or during rendering. Test should ensure a valid configured chart can be drawn with `null` info without the Chart-4 NPE.

This corresponds to named `testDrawWithNullInfo` failures.

### D. Serialization workflow

A serialization round trip can invoke state restoration and later axis configuration/drawing. The supplied triggering test `JFreeChartTests::testSerialization4` failed with NPE, so a regression scenario should serialize and deserialize a relevant chart/plot configuration, then execute the workflow that triggers the data range calculation.

Exact serialization assertions beyond successful round-trip/equality cannot be reliably derived without the existing test body.

### E. Renderer absence and annotations

The defect is specifically caused after the null-renderer fallback. Test coverage should distinguish:

1. non-null dataset + null renderer;
2. non-null dataset + non-null renderer;
3. null or empty dataset;
4. renderer annotations only when a renderer exists.

A regression test must avoid assuming annotation behavior that the source does not define for a null renderer. It should simply verify no null-renderer dereference occurs.

### F. Range-gridline drawing

The listed `XYPlotTests::testDrawRangeGridlines` indicates the issue can surface in a draw-related path. The supplied `drawRangeGridlines(...)` already has an early return if `getRenderer() == null`, so a test should exercise the full scenario from the existing test or a valid equivalent chart setup rather than asserting that gridlines draw without a renderer.

---

# 9. Missing context required for compilable and meaningful tests

The supplied source is sufficient to identify the likely NPE defect and design a focused null-renderer regression test conceptually. However, the following missing context limits precision and compile certainty.

## Missing production/API context

1. **Source of existing triggering tests**
   - Their setup, assertions, helper methods, package declarations, and fixture conventions are absent.
   - These tests are the best available behavioral oracle but are only named.

2. **Source/API details of concrete dependencies**
   - `NumberAxis`, `LogAxis`, `ValueAxis`
   - concrete datasets, such as `XYSeriesCollection`
   - concrete renderers, such as `XYLineAndShapeRenderer`
   - `ChartFactory`, `JFreeChart`
   - `ObjectList`, `Range`, `DatasetUtilities`
   - `Plot` inherited behavior, including minimum drawing sizes and listener mechanics.

3. **Fixed revision diff**
   - Revision `2183` is identified but its patch content is not supplied.
   - Without it, one should not claim the exact intended implementation detail beyond the observed defective null dereference.

4. **Exact Chart project source layout and test source root**
   - The Defects4J build is identified, but no module/source-root paths or package test conventions were supplied.

5. **Java compiler/source level**
   - The source appears to predate modern Java idioms, but no explicit source/target compatibility setting is supplied.

## Consequences for test generation

- A compilable test can likely be generated using common JFreeChart classes, but their constructors and APIs should not be invented.
- Exact expected numerical auto-range assertions should not be generated until axis/dataset behavior or existing test code is available.
- The safest meaningful regression assertion is absence of `NullPointerException` when a valid dataset and axes are used with a null renderer, plus non-null/expected fallback range only when concrete dataset utility behavior is confirmed from supplied context.

## Conclusion

No JUnit class should be generated yet, as requested. The supplied evidence strongly supports a Chart-4 regression test centered on `XYPlot.getDataRange(ValueAxis)` with a non-null dataset and a null renderer, including indirect invocation through auto-ranging and dataset replacement. The exact numeric range expectations and the closest existing-test reproduction remain insufficiently specified without the omitted axis/dataset/test source context.