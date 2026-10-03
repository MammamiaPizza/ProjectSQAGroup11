TARGETS: getDomainAxisIndex(CategoryAxis) and getRangeAxisIndex(ValueAxis) on CategoryPlot.  
ORACLES: Trigger tests are the only stated expected-result source; assertions currently fail.  
CASES: Set axis at index 0 and verify lookup returns 0 for the same domain/range axis instance.  
CASES: Set axes at multiple indexes; verify each instance resolves to its assigned index.  
CASES: Lookup an axis not installed in the plot; verify documented/observed sentinel only if available locally.  
CASES: Include sparse indexes and null axis slots to expose incorrect collection/index traversal.  
RISKS: No bug report or method bodies supplied; null, duplicate-instance, and sentinel expectations are unspecified.