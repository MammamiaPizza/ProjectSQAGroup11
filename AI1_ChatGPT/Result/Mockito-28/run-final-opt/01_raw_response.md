TARGETS: injectMocksOnFields, injectMockCandidate, orderedInstanceFieldsFrom; superclass traversal and field order.  
ORACLES: Trigger assertion: exact-type field receives mock; ancestor-compatible field retains original Object.  
CASES: One mock assignable to both exact field type and ancestor field; verify injected once into best match.  
CASES: Fields across class hierarchy; ensure subclass/exact type is considered before ancestor-compatible field.  
CASES: Multiple eligible fields/mocks and no eligible mock; verify selection/non-injection via observable field state.  
RISKS: Private helpers require exercising injectMocksOnFields; candidate-filter behavior is indirect.  
RISKS: Context lacks source bodies and broader expected ordering/name-resolution semantics.