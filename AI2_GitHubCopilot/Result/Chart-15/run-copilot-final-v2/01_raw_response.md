TARGETS: PiePlot.draw(), setDataset(null), and null-dataset handling in PiePlot3D.
ORACLES: Null/empty dataset should not throw; consistent with JFreeChart null-handling conventions.
CASES: draw with null dataset, empty dataset, zero-value keys, null keys, single-value dataset.
RISKS: Limited API visible—constructor/getter/setter signatures only; actual draw() logic is
truncated.