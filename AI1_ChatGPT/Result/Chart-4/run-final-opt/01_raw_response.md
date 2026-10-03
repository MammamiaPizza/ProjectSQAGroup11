TARGETS: XYPlot construction and initialization used by chart factory methods and serialization.
TARGETS: Dataset/axis replacement, auto-range configuration, draw(), and range-gridline rendering.
ORACLES: Existing listed trigger tests define expected non-NPE behavior and chart/axis outcomes.
CASES: XY plots with NumberAxis or LogAxis and datasets requiring domain/range auto-range.
CASES: Axis margins, null ChartRenderingInfo in draw(), and range gridlines.
CASES: Replace dataset; set series tooltip generator; serialize/deserialize a chart containing XYPlot.
RISKS: Broad NPE failures suggest uninitialized XYPlot state shared across construction, drawing, and events.
RISKS: Context lacks stack traces/source bodies and a stated bug specification; avoid assumptions beyond triggers.