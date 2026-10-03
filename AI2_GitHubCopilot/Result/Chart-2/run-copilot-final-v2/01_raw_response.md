TARGETS: DatasetUtilities.findRangeBounds(CategoryDataset/XYDataset) and delegates
iterateRangeBounds, iterateXYRangeBounds null handling.
TARGETS: Null y-values/values, zero-row/column, and isEmptyOrNull guarded empty paths returning null
Range.
ORACLES: Trigger tests testBug2849731_2/_3 assert no NPE and Range derived only from non-null data.
ORACLES: All-null category cells or XY y-values should yield null Range, not infinite/NaN bounds.
CASES: Single non-null value Range[v,v]; mixed null/valid; nulls among positive/negative values.
CASES: Fully null row/series; empty dataset with rowCount=0 or columnCount=0.
CASES: XYDataset non-interval path vs IntervalXYDataset start/end y-value path.
RISKS: Bug 959 exact assertions and fixed diff not provided in prompt.
RISKS: Null-dataset contract is unspecified (IllegalArgumentException vs null) - avoid assuming.
RISKS: NaN/Infinite value handling and expected invalid-argument behavior undocumented.