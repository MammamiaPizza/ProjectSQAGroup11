TARGETS: DefaultBoxAndWhiskerCategoryDataset.getRangeLowerBound/getRangeUpperBound(boolean
includeInterval)
TARGETS: getRangeBounds(boolean includeInterval) and private updateBounds() bound recomputation
TARGETS: getMinRegularValue/getMaxRegularValue vs getMinOutlier/getMaxOutlier selection for range
ORACLES: Trigger test asserts getRangeBounds equals Range[8.5,9.6]; lower should reflect 8.5
(outlier) not 8.6
ORACLES: Expected results come from DefaultBoxAndWhiskerCategoryDatasetTests::testGetRangeBounds
assertion
CASES: includeInterval true and false for lower/upper bounds should produce identical range
CASES: Data with min/max outliers outside regular values (8.5/8.6) exercises lower-bound outlier
inclusion
CASES: Empty dataset getRangeBounds returns null; single row+column bound equals outlier extremes
CASES: Multiple rows/columns and post-add bound updates verify updateBounds aggregate state
RISKS: No source diff or test body; exact includeInterval versus outlier semantics inferred from
assertion only