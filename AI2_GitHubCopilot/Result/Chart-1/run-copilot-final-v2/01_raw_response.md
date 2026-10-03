TARGETS: getRowCount(), getColumnCount() after rendering via createState() or plot render
ORACLES: count must equal dataset row/column count; test expects 1 but got 0 for row/col
CASES: dataset with 1 row, 1 col; multiple rows/cols; empty dataset (0,0); null dataset
RISKS: Only after rendering pass; internal state fields may not be set if render not invoked
correctly