## 1. Test target and instantiation constraints

`AbstractCategoryItemRenderer` is an abstract class, so it cannot be instantiated directly.

Tests require either:

- A concrete production subclass available in the project, or
- A minimal test-only concrete subclass that implements every unimplemented method inherited from `CategoryItemRenderer` / `AbstractRenderer`.

The supplied source does not include the full `CategoryItemRenderer` interface or `AbstractRenderer`, so it is not possible to determine from this prompt alone which methods a minimal subclass must implement to compile.

Protected methods can be tested through a test subclass that exposes wrapper methods, provided that subclass can be compiled.

---

## 2. JUnit version and build tool

- **JUnit:** JUnit 4.12 with Hamcrest 1.3 (`junit-4.12-hamcrest-1.3.jar`)
- **Build tool:** Defects4J project build, using the Chart project Ant build configuration:
  - `/root/defects4j/framework/projects/Chart/Chart.build.xml`

The historical triggering test uses the older `junit.framework.AssertionFailedError`, indicating that the project may contain JUnit 3-style existing tests despite the supplied JUnit 4.12 dependency. New tests should not assume the existing test style without the project test-source context.

---

## 3. Test oracle available from the supplied context

### Explicit oracle

The supplied bug context is the strongest oracle:

- **Bug:** Chart-1 / bug report 983 / historical source comment “Fixed bug 2947660”
- **Triggering test:**  
  `org.jfree.chart.renderer.category.junit.AbstractCategoryItemRendererTests::test2947660`
- **Observed failure in Source Version Chart-1b:**  
  `expected:<1> but was:<0>`
- **Fixed revision:** 2266
- **Only modified production class:**  
  `org.jfree.chart.renderer.category.AbstractCategoryItemRenderer`

This directly indicates an expected legend-item collection size of `1` under the triggering scenario.

### API/Javadoc oracle

The class Javadoc provides contractual expectations for:

- permitted and forbidden `null` values;
- default values;
- listener-notification behavior;
- fallback behavior for generator lookup;
- range and marker behavior;
- legend visibility behavior;
- exception behavior in several methods.

### Limitations

The actual bug report content, fixed source revision, existing test source, production subclasses, and dependency implementations are **not supplied**. Therefore, expected values that depend on those implementations cannot always be determined reliably.

---

## 4. Bug-related behavior that must be tested

### Defect location

`getLegendItems()` contains this condition:

```java
CategoryDataset dataset = this.plot.getDataset(index);
if (dataset != null) {
    return result;
}
```

This returns an empty collection whenever a valid dataset exists. Immediately afterward, the code dereferences `dataset`:

```java
int seriesCount = dataset.getRowCount();
```

Thus, as written:

- with a non-null dataset, it returns an empty collection;
- with a null dataset, it proceeds and will throw `NullPointerException`.

This is inconsistent with the method Javadoc:

> “Returns a (possibly empty) collection of legend items for the series that this renderer is responsible for drawing.”

It is also inconsistent with the triggering failure: expected one legend item, got zero.

### Required regression tests

At minimum, the bug test must establish all prerequisites for a legend item:

1. A renderer assigned to a `CategoryPlot`.
2. The plot recognizes that renderer and returns a valid renderer dataset index.
3. A non-null `CategoryDataset` assigned at that index.
4. At least one row/series in that dataset.
5. The series is visible.
6. The series is visible in the legend.
7. A usable legend label generator is present. The constructor establishes a `StandardCategorySeriesLabelGenerator`, subject to normal construction.

Expected result:

- `getLegendItems()` returns a non-null collection containing one item for one eligible dataset series.
- The result must not be empty merely because the dataset is non-null.

Additional bug-relevant cases:

| Scenario | Expected behavior from source/Javadoc |
|---|---|
| Renderer has no plot | Return an empty, non-null `LegendItemCollection`. |
| Renderer has a plot but no dataset at its renderer index | Return an empty, non-null collection; it should not dereference a null dataset. The current source instead appears to dereference it. |
| One visible series, visible in legend | Exactly one legend item should be returned. This is the primary regression case. |
| Multiple eligible series, ascending row rendering order | Legend items should occur in ascending series-index order. |
| Multiple eligible series, descending row rendering order | Legend items should occur in descending series-index order. |
| Series not visible | No legend item, because `getLegendItem()` returns `null` for invisible series. |
| Series visible but hidden in legend | No legend item. |
| Optional legend tooltip/URL generator absent | Generated legend item tooltip/URL should be `null`, subject to `LegendItem` API. |
| Optional legend tooltip/URL generator configured | Its generated text should populate the legend item, subject to `LegendItem` accessors. |

The exact setup APIs for associating a renderer and dataset with `CategoryPlot` are not supplied, so compilable test setup cannot be specified with certainty from this prompt alone.

---

## 5. Public methods declared in the supplied class that should be tested

The following public methods are declared directly in the supplied target class.

### A. Construction, plot assignment, and basic state

| Method | Test focus |
|---|---|
| `getPassCount()` | Returns `1`. |
| `getPlot()` | Initially likely `null`; returns exactly the plot passed to `setPlot()`. |
| `setPlot(CategoryPlot)` | Accepts non-null plot; rejects `null` with `IllegalArgumentException`. |
| `getRowCount()` | Initially default transient value (`0`); updated by `initialise()`. |
| `getColumnCount()` | Initially default transient value (`0`); updated by `initialise()`. |
| `initialise(Graphics2D, Rectangle2D, CategoryPlot, CategoryDataset, PlotRenderingInfo)` | Sets plot; stores dataset row/column counts or zero for a null dataset; creates state; resolves selection state. |
| `getDrawingSupplier()` | Returns `null` without plot; otherwise delegates to the assigned plot. |

### B. Item-label generators

| Method | Test focus |
|---|---|
| `getItemLabelGenerator(int, int, boolean)` | Series generator takes precedence over base generator; returns base if series generator is absent; returns `null` if neither exists. Column and selected inputs are ignored by this implementation. |
| `getSeriesItemLabelGenerator(int)` | Returns the per-series generator or `null`. |
| `setSeriesItemLabelGenerator(int, CategoryItemLabelGenerator)` | Stores generator and notifies listeners. |
| `setSeriesItemLabelGenerator(int, CategoryItemLabelGenerator, boolean)` | Stores generator; notification branch based on `notify`. `null` generator is permitted. |
| `getBaseItemLabelGenerator()` | Returns configured base generator or `null`. |
| `setBaseItemLabelGenerator(CategoryItemLabelGenerator)` | Stores generator and notifies listeners. |
| `setBaseItemLabelGenerator(CategoryItemLabelGenerator, boolean)` | Stores `null` or non-null; notification branch based on `notify`. |

### C. Tooltip generators

| Method | Test focus |
|---|---|
| `getToolTipGenerator(int, int, boolean)` | Per-series generator takes precedence over base; `column` and `selected` are ignored by this implementation. |
| `getSeriesToolTipGenerator(int)` | Returns per-series generator or `null`. |
| `setSeriesToolTipGenerator(int, CategoryToolTipGenerator)` | Stores generator and notifies. |
| `setSeriesToolTipGenerator(int, CategoryToolTipGenerator, boolean)` | Stores nullable generator; optional notification. |
| `getBaseToolTipGenerator()` | Returns base generator or `null`. |
| `setBaseToolTipGenerator(CategoryToolTipGenerator)` | Stores generator and notifies. |
| `setBaseToolTipGenerator(CategoryToolTipGenerator, boolean)` | Stores nullable generator; optional notification. |

### D. URL generators

| Method | Test focus |
|---|---|
| `getURLGenerator(int, int, boolean)` | Per-series generator takes precedence; base fallback; `column` and `selected` are ignored. |
| `getSeriesURLGenerator(int)` | Returns per-series generator or `null`. |
| `setSeriesURLGenerator(int, CategoryURLGenerator)` | Stores generator and notifies. |
| `setSeriesURLGenerator(int, CategoryURLGenerator, boolean)` | Stores nullable generator; optional notification. |
| `getBaseURLGenerator()` | Returns base generator or `null`. |
| `setBaseURLGenerator(CategoryURLGenerator)` | Stores generator and notifies. |
| `setBaseURLGenerator(CategoryURLGenerator, boolean)` | Stores nullable generator; optional notification. |

### E. Annotations

| Method | Test focus |
|---|---|
| `addAnnotation(CategoryAnnotation)` | Adds to foreground layer and notifies; rejects null annotation through delegated overload. |
| `addAnnotation(CategoryAnnotation, Layer)` | Foreground branch; background branch; null annotation rejection; invalid/unknown layer branch. |
| `removeAnnotation(CategoryAnnotation)` | Removal result and listener notification. See potential contract issue below. |
| `removeAnnotations()` | Clears both annotation lists and notifies. |
| `drawAnnotations(Graphics2D, Rectangle2D, CategoryAxis, ValueAxis, Layer, PlotRenderingInfo)` | Draws foreground or background list based on layer; unknown layer branch throws `RuntimeException`. |

### F. Legend configuration and legend production

| Method | Test focus |
|---|---|
| `getLegendItemLabelGenerator()` | Constructor default is non-null; returns configured generator. |
| `setLegendItemLabelGenerator(CategorySeriesLabelGenerator)` | Rejects null with `IllegalArgumentException`; stores non-null generator; fires a change event. |
| `getLegendItemToolTipGenerator()` | Initially `null`; returns configured value. |
| `setLegendItemToolTipGenerator(CategorySeriesLabelGenerator)` | Permits null; stores value; fires change event. |
| `getLegendItemURLGenerator()` | Initially `null`; returns configured value. |
| `setLegendItemURLGenerator(CategorySeriesLabelGenerator)` | Permits null; stores value; fires change event. |
| `getLegendItem(int, int)` | No-plot branch; hidden-series branch; hidden-in-legend branch; normal construction of legend item and generated label/tooltip/URL. |
| `getLegendItems()` | Primary bug target; no-plot, null-dataset, ascending, descending, visible, invisible, and legend-hidden branches. |

### G. Dataset range and item positioning

| Method | Test focus |
|---|---|
| `findRangeBounds(CategoryDataset)` | Delegates with `includeInterval=false`; null dataset returns null; empty dataset behavior is delegated to `DatasetUtilities`. |
| `getItemMiddle(Comparable, Comparable, CategoryDataset, CategoryAxis, Rectangle2D, RectangleEdge)` | Delegates to `CategoryAxis.getCategoryMiddle()` using the column key and dataset column keys. |

The overloaded `findRangeBounds(CategoryDataset, boolean)` is protected. It should be tested through a subclass wrapper if visible-series filtering is in scope.

### H. Drawing delegation and grid/range lines

| Method | Test focus |
|---|---|
| `drawBackground(Graphics2D, CategoryPlot, Rectangle2D)` | Delegates to `plot.drawBackground()`. |
| `drawOutline(Graphics2D, CategoryPlot, Rectangle2D)` | Delegates to `plot.drawOutline()`. |
| `drawDomainLine(Graphics2D, CategoryPlot, Rectangle2D, double, Paint, Stroke)` | Rejects null paint/stroke; draws horizontal line for horizontal orientation and vertical line for vertical orientation. |
| `drawRangeLine(Graphics2D, CategoryPlot, ValueAxis, Rectangle2D, double, Paint, Stroke)` | Does nothing when value is outside axis range; draws orientation-dependent line when in range. Unlike `drawDomainLine()`, no explicit null checks for paint/stroke appear. |

### I. Markers

| Method | Test focus |
|---|---|
| `drawDomainMarker(Graphics2D, CategoryPlot, CategoryAxis, CategoryMarker, Rectangle2D)` | Missing category branch returns without drawing; line versus band marker; horizontal versus vertical orientation; label absent/present; restores original graphics composite. |
| `drawRangeMarker(Graphics2D, CategoryPlot, ValueAxis, Marker, Rectangle2D)` | `ValueMarker` branch; `IntervalMarker` branch; unsupported `Marker` subtype does nothing; out-of-range value/interval returns; orientation; marker labels; interval clipping; optional outlines; optional gradient transformation; composite restoration. |

### J. Equality, clone, and hash code

| Method | Test focus |
|---|---|
| `equals(Object)` | Reflexive equality; null/non-renderer inequality; equality and inequality for each local configuration field; inherited state participates through `super.equals()`. |
| `hashCode()` | Delegates to `super.hashCode()`. There is no supplied contract that equal renderers necessarily have a matching local-field-derived hash code, because this override does not incorporate this class’s fields. |
| `clone()` | Clones object lists; clones base generators only if `PublicCloneable`; throws `CloneNotSupportedException` for non-cloneable configured base generators; clones applicable legend generators; plot is shallow copied as stated by Javadoc. |

### K. Hotspots and hit testing

| Method | Test focus |
|---|---|
| `createHotSpotShape(...)` | Always throws `RuntimeException("Not implemented.")`. This is explicit behavior in this version. |
| `createHotSpotBounds(...)` | Creates a default `Rectangle` if result is null; returns null if dataset value is null; otherwise sets a 4×4 rectangle centered at transformed coordinates; reuses supplied result object. |
| `hitTest(...)` | Returns false when `createHotSpotBounds()` returns null; otherwise uses `Rectangle2D.contains(xx, yy)`. |

---

## 6. Protected methods with significant behavior

These are not directly public API methods, but should be covered indirectly or via a test subclass if feasible:

| Protected method | Relevant branches |
|---|---|
| `createState(PlotRenderingInfo)` | Builds visible-series array from `rowCount` and `isSeriesVisible(row)`. |
| `findRangeBounds(CategoryDataset, boolean)` | Null dataset; all series versus visible series only; interval flag forwarding. |
| `calculateDomainMarkerTextAnchorPoint(...)` | Horizontal/vertical adjustment ordering. |
| `calculateRangeMarkerTextAnchorPoint(...)` | Horizontal/vertical adjustment ordering. |
| `updateCrosshairValues(...)` | Null orientation rejection; null crosshair state no-op; range crosshair locked versus unlocked. |
| `drawItemLabel(...)` | No generator no-op; generator present; negative versus positive label position. |
| `getDomainAxis(CategoryPlot, CategoryDataset)` | Uses `plot.indexOf(dataset)` then `getDomainAxisForDataset(index)`. |
| `getRangeAxis(CategoryPlot, int)` | Uses indexed range axis when non-null; otherwise falls back to primary range axis. |
| `addEntity(...)` overloads | Null hotspot rejection in first overload; item entity creation disabled/enabled; supplied hotspot versus generated default ellipse; vertical/horizontal generated-shape coordinate behavior; optional tooltip and URL. |

---

## 7. Input types and documented valid ranges

### Index parameters

The following indexes are documented as zero-based:

- `row`
- `column`
- `series`
- `datasetIndex`
- range-axis `index`

The source itself does not validate most index bounds. Valid upper bounds depend on external structures:

| Input | Valid range cannot exceed |
|---|---|
| `row` / `series` | Dataset row count, or renderer’s configured per-series lists |
| `column` | Dataset column count |
| `datasetIndex` | Dataset index supported by `CategoryPlot` |
| range axis index | Axis index supported by `CategoryPlot` |

Negative-index behavior cannot be established from this source because it depends on `ObjectList`, `CategoryDataset`, `CategoryPlot`, and other external implementations.

### Nullable inputs explicitly documented

| Method / parameter | Null status from supplied source/Javadoc |
|---|---|
| `setPlot(plot)` | Null forbidden; explicit `IllegalArgumentException`. |
| Series/base item-label generators | Null permitted. |
| Series/base tooltip generators | Null permitted. |
| Series/base URL generators | Null permitted. |
| `addAnnotation(annotation)` | Null forbidden; explicit `IllegalArgumentException` in delegated overload. |
| `addAnnotation(annotation, layer)` annotation | Null forbidden; explicit `IllegalArgumentException`. |
| `addAnnotation(annotation, layer)` layer | Null forbidden according to Javadoc, but the implementation calls `layer.equals(...)`, so null produces `NullPointerException`, not a documented `IllegalArgumentException`. |
| Legend item label generator | Null forbidden; explicit `IllegalArgumentException`. |
| Legend item tooltip/URL generators | Null permitted. |
| `initialise(..., dataset, ...)` dataset | Null permitted. |
| `initialise(..., info)` info | Null permitted. |
| `findRangeBounds(dataset)` | Null permitted; returns null. |
| `updateCrosshairValues(..., crosshairState, ...)` | Null permitted; no-op. |
| `updateCrosshairValues(..., orientation)` | Null forbidden; explicit `IllegalArgumentException`. |
| `drawDomainLine(..., paint, ...)` | Null forbidden; explicit `IllegalArgumentException`. |
| `drawDomainLine(..., stroke)` | Null forbidden; explicit `IllegalArgumentException`. |
| `addEntity(..., hotspot, ...)` first overload | Null forbidden; explicit `IllegalArgumentException`. |
| `addEntity(..., hotspot, ..., entityX, entityY)` second overload | Null accepted and causes default hotspot generation. |
| `createHotSpotBounds(..., result)` | Null accepted; creates a `Rectangle`. |

For most graphics, plot, axis, dataset, and marker arguments, the Javadoc says “not null” in some methods but the implementation does not consistently perform explicit validation. Null behavior would usually be a downstream `NullPointerException` and is not necessarily a stable API contract.

---

## 8. Reachable branches and normal/boundary/invalid/exceptional cases

### Generator precedence branches

Applicable independently to item labels, tooltips, and URLs:

1. No series generator and no base generator → `null`.
2. Base generator only → base generator returned.
3. Series generator only → series generator returned.
4. Both configured → series generator returned.
5. Series generator reset to `null` after base configured → fallback to base.
6. `notify=true` versus `notify=false` for each setter overload.

### Renderer event notification branches

Generator setters and annotation/legend configuration methods call either:

- `notifyListeners(new RendererChangeEvent(this))`, or
- `fireChangeEvent()`.

Reliable listener-count tests require the listener-registration API from `AbstractRenderer`, which is not supplied. The notification calls are visible, but the exact listener interfaces and registration methods are not.

### Initialisation branches

`initialise()` has these relevant paths:

1. Non-null dataset:
   - record row and column counts;
   - create state;
   - selection state from `SelectableCategoryDataset`, if provided;
   - otherwise selection state possibly from `PlotRenderingInfo` → `ChartRenderingInfo` → `RenderingSource`.
2. Null dataset:
   - row count and column count set to zero;
   - creates state with no visible series;
   - may still attempt selection-source lookup if `info != null`.
3. `info == null`:
   - no rendering-source selection lookup.
4. `dataset` selectable but returns null selection state:
   - falls through to rendering-source lookup when info is supplied.
5. `dataset` not selectable:
   - rendering-source lookup path when info is supplied.

Potential exceptional path not safely specifiable:

```java
RenderingSource rs = cri.getRenderingSource();
selectionState = (CategoryDatasetSelectionState) rs.getSelectionState(dataset);
```

If `cri.getRenderingSource()` is null, this throws `NullPointerException`. The supplied source does not document whether a null rendering source is possible.

### Annotation branches

1. Foreground add.
2. Background add.
3. Null annotation → `IllegalArgumentException`.
4. Unrecognized non-null layer → `RuntimeException("Unknown layer.")`.
5. Null layer → `NullPointerException` due to `layer.equals(...)`.
6. Draw foreground annotations.
7. Draw background annotations.
8. Unknown draw layer → `RuntimeException("Unknown layer.")`.

### Potential annotation-removal contract issue

`removeAnnotation()` uses:

```java
boolean removed = this.foregroundAnnotations.remove(annotation);
removed = removed & this.backgroundAnnotations.remove(annotation);
```

Because `&` is used, the result is `true` only when the same annotation is removed from **both** foreground and background lists.

However, `addAnnotation()` adds an annotation to one layer only. Under ordinary use, removing a successfully added foreground-only or background-only annotation will return `false`, despite the Javadoc saying:

> “A boolean to indicate whether or not the annotation was successfully removed.”

A test can reliably observe this current behavior, but the intended expected result is ambiguous without fixed-version source or bug/specification specifically addressing it. The Javadoc suggests that `true` after successful removal from either layer would be the intended contract, but that should be treated as a potential defect hypothesis rather than an established bug oracle.

### Legend branches

1. No plot → empty collection.
2. Plot present, dataset absent → intended empty collection according to sensible contract; current code likely throws NPE.
3. Dataset present → should iterate series; current code returns empty immediately. This is the confirmed Chart-1 regression.
4. Ascending row rendering order.
5. Descending row rendering order.
6. Series visible/invisible.
7. Series included/excluded from legend.
8. Optional tooltip and URL legend generators absent/present.

### Range-finding branches

1. Null dataset → null.
2. `getDataBoundsIncludesVisibleSeriesOnly()` false → all series passed to `DatasetUtilities`.
3. Flag true → only visible row keys passed to `DatasetUtilities`.
4. No visible series → outcome depends on `DatasetUtilities.findRangeBounds()` implementation, which is not supplied.
5. `includeInterval` true/false.

### Grid-line branches

`drawDomainLine()`:

- null paint → `IllegalArgumentException`;
- null stroke → `IllegalArgumentException`;
- horizontal orientation → horizontal line at `y=value`;
- vertical orientation → vertical line at `x=value`.

`drawRangeLine()`:

- value outside range → no drawing;
- value at exact lower or upper range bound → range inclusion depends on `Range.contains()`, likely inclusive but that class implementation is not supplied;
- horizontal orientation → vertical line;
- vertical orientation → horizontal line.

### Marker branches

`drawDomainMarker()`:

- marker category does not exist → returns before modifying graphics state;
- `drawAsLine=true`;
- `drawAsLine=false`;
- horizontal / vertical orientation;
- label null / non-null;
- graphics composite restored after drawing.

`drawRangeMarker()`:

- `ValueMarker`, in range;
- `ValueMarker`, outside range;
- `IntervalMarker`, intersecting range;
- `IntervalMarker`, non-intersecting range;
- interval partially outside range, exercising clipping;
- horizontal / vertical orientation;
- plain paint / `GradientPaint`;
- gradient transformer null / non-null;
- outlines enabled / disabled;
- start and/or end inside axis range;
- label null / non-null;
- unsupported `Marker` subtype, which silently does nothing.

### Hotspot and hit-test branches

`createHotSpotBounds()`:

- supplied `result == null` → creates a `Rectangle`;
- supplied reusable rectangle → mutates and returns that object;
- null dataset value → returns null;
- non-null data value → returns a 4×4 rectangle centered at converted coordinates.

`hitTest()`:

- null value / null bounds → false;
- point within bounds → true;
- point outside bounds → false;
- boundary semantics depend on `Rectangle2D.contains()`, which normally excludes right/bottom edges.

---

## 9. Required dependencies and external objects

The following classes are needed for meaningful tests of the respective method groups.

| Test area | Required external objects/classes |
|---|---|
| Instantiation | Concrete renderer subclass or compilable test subclass |
| Plot assignment, legends, drawing supplier | `CategoryPlot`, potentially a configured renderer/dataset registration |
| Dataset tests | `CategoryDataset`; likely an in-project concrete dataset implementation |
| Selection-state initialisation | `SelectableCategoryDataset`, `CategoryDatasetSelectionState`, `PlotRenderingInfo`, `ChartRenderingInfo`, `RenderingSource` |
| Generator tests | Implementations or test doubles of `CategoryItemLabelGenerator`, `CategoryToolTipGenerator`, `CategoryURLGenerator`, `CategorySeriesLabelGenerator` |
| Change-notification tests | Renderer listener API and listener type from `AbstractRenderer` / event package |
| Graphics tests | `Graphics2D`, normally from a `BufferedImage`; `Rectangle2D`; paint and stroke instances |
| Domain/range-line tests | `CategoryPlot`, `ValueAxis`, potentially concrete axis implementations |
| Marker tests | `CategoryMarker`, `ValueMarker`, `IntervalMarker`, `CategoryAxis`, `ValueAxis`, `CategoryPlot` |
| Annotation tests | `CategoryAnnotation`, `Layer`, graphics/axis/plot setup |
| Entity tests | `EntityCollection`, `CategoryItemEntity`, dataset |
| Crosshair tests | `CategoryCrosshairState`, assigned plot, range-crosshair settings |
| Clone/equality tests | Generator implementations whose cloneability and equality behavior are known |
| Range bounds tests | Dataset with known values and visibility configuration inherited from `AbstractRenderer` |

The prompt supplies only type names and limited behavior of these dependencies, not constructors, configuration APIs, or test utilities.

---

## 10. Missing context that prevents fully reliable compilable tests

The following information is missing from the supplied prompt and is necessary to generate robust, compilable tests without inventing APIs:

1. **Concrete renderer availability**
   - The target is abstract.
   - The methods required by `CategoryItemRenderer` or inherited abstract methods are not shown.
   - Therefore, a minimal test subclass cannot be safely written from the supplied source alone.

2. **Concrete `CategoryDataset` implementation and constructors**
   - The source only references the `CategoryDataset` interface.
   - The available project dataset implementations and their construction APIs are not supplied.

3. **`CategoryPlot` construction/configuration API**
   - Regression testing `getLegendItems()` requires correctly registering a renderer and dataset so that:
     - `plot.getIndexOf(renderer)` returns the intended dataset index;
     - `plot.getDataset(index)` returns the intended dataset;
     - row rendering order can be configured.
   - Those APIs are not shown.

4. **Inherited visibility and entity configuration APIs**
   - Important branches depend on inherited methods such as:
     - `isSeriesVisible(int)`
     - `isSeriesVisibleInLegend(int)`
     - `getDataBoundsIncludesVisibleSeriesOnly()`
     - `getItemCreateEntity(...)`
     - `getDefaultEntityRadius()`
   - Their setters, defaults, and semantics are not supplied.

5. **Listener registration API**
   - Setter notification behavior is visible, but the listener registration methods/types inherited from `AbstractRenderer` are not supplied.

6. **Dependency behavior**
   - The behavior and constructors of `ObjectList`, `Range`, `CategoryAxis`, `ValueAxis`, `CategoryCrosshairState`, `RenderingSource`, `LegendItem`, and marker classes are not supplied.
   - Tests involving exact geometry, range inclusivity, generated labels, graphics calls, or selection state require these APIs.

7. **Existing triggering test source and fixed source**
   - The test method name and failure are supplied, but its setup and exact expected object assertions are not.
   - The fixed revision is identified but its actual change is not supplied.
   - The bug regression expectation of one legend item is clear; exact fixture construction is not.

---

## 11. Recommended priority for eventual test generation

### Highest priority: Chart-1 regression

1. `getLegendItems()` with:
   - assigned plot;
   - non-null dataset;
   - one visible, legend-visible series;
   - expected legend item count = `1`.

2. `getLegendItems()` with no plot:
   - expected non-null empty collection.

3. `getLegendItems()` with plot but null dataset:
   - expected non-null empty collection according to intended method contract; current source likely fails with `NullPointerException`.

4. Legend ordering:
   - ascending and descending row rendering order.

### High-value contract tests

- `setPlot(null)` exception.
- Generator precedence and null fallback for labels/tooltips/URLs.
- `initialise()` row/column count behavior with non-null and null dataset.
- `findRangeBounds(null)` returns null.
- `drawDomainLine()` null paint/stroke exceptions.
- `createHotSpotShape()` throws its documented/current `RuntimeException`.
- `createHotSpotBounds()` null data value and generated 4×4 bounds.
- `hitTest()` inside/outside/null-value cases.
- `getLegendItem()` no plot, invisible, hidden-in-legend, and normal cases.

### Lower priority / requires more dependency context

- Full graphics verification of marker drawing.
- Rendering-source selection-state lookup.
- Crosshair behavior.
- Clone behavior with `PublicCloneable` and non-cloneable generators.
- Annotation rendering and listener notifications.
- Exact `equals()` coverage across inherited renderer state.

No JUnit test class is generated here, as requested.