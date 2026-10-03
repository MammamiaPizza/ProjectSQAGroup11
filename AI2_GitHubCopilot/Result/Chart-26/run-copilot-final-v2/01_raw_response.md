TARGETS: Axis.draw(...) methods that accept ChartRenderingInfo; null-info code path.
ORACLES: No exception during draw(null); serializable after draw(null); existing tests.
CASES: Normal draw with non-null info; draw with null info (error); post-null serialization.
RISKS: Exact draw signatures not visible; rely on trigger-test expectations; missing fix version.