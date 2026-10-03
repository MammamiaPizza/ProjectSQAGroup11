TARGETS: StatisticalBarRenderer.draw() handling StatisticalCategoryDataset items with null mean or
null standard deviation.
ORACLES: Defects4J fixed version; tests expect no AssertionFailedError, bar skipped/drawn correctly
when mean or deviation is null.
CASES: Null mean vertical, null mean horizontal, null deviation vertical, null deviation horizontal,
both null, one null one valid, normal values.
RISKS: Limited context – only public API signatures; internal draw logic, dataset contract, and test
assertions unknown; missing edge-case details.