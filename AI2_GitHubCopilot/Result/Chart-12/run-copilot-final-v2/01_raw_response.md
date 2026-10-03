TARGETS: MultiplePiePlot(), MultiplePiePlot(CategoryDataset), getAggregatedItemsPaint,
setAggregatedItemsPaint, getAggregatedItemsKey, getLimit, getDataExtractOrder, getPlotType,
getDataset, equals.
ORACLES: Bug 213 + testConstructor assert constructor defaults:
aggregatedItemsPaint=Color.lightGray, aggregatedItemsKey="Other",
dataExtractOrder=TableOrder.BY_COLUMN, limit=0.0.
CASES: no-arg MultiplePiePlot() default state; MultiplePiePlot(null) vs no-arg; constructor(dataset)
retains dataset via getDataset.
CASES: set/get aggregatedItemsPaint round-trip incl. null and non-null Paint; setAggregatedItemsKey;
setLimit boundary: 0.0, negative, large.
ORACLES: getPlotType expected "Multiple Pie Plot"; equals() symmetry/reflexivity for two default
plots; equals after modifying aggregatedItemsPaint/key.
RISKS: only testConstructor regression is visible; exact full assertion list undocumented, so avoid
asserting unrecognized defaults.
RISKS: getLegendItems/getPieChart with null dataset may NPE; guard or isolate these cases rather
than assuming behavior.