TARGETS: Variance.evaluate(values, weights) and weighted array-segment behavior implicated by trigger  
TARGETS: Variance.evaluate(values, begin, length); bias-correction setting may affect results  
ORACLES: Trigger asserts weighted segment variance 1.6644508338125354, not 0.31909161062727365  
CASES: Weighted nonempty segment; compare result with trigger’s stated expected value  
CASES: Segment boundaries, single-element segment, and bias-corrected versus uncorrected modes  
CASES: Invalid/null arrays, mismatched weights, invalid begin/length if API accepts weighted segments  
RISKS: Provided signatures omit a weighted begin/length overload despite the trigger name  
RISKS: No trigger input arrays/weights or implementation details are provided; avoid inferred numeric oracles