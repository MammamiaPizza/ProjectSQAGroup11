TARGETS: SetUniqueList.addAll(int,Collection), especially duplicate filtering while preserving list/set uniqueness.  
ORACLES: Trigger testCollections304 expects resulting size 3, not 4; list uniqueness is implied by SetUniqueList.  
CASES: Indexed addAll with collection containing an existing element and new elements; verify size/order/no duplicates.  
CASES: Indexed addAll where input itself repeats values; verify each value is inserted at most once.  
CASES: Normal add/addAll/set/remove/clear should keep asSet() synchronized with list contents.  
RISKS: Index behavior/order for accepted elements and return value of addAll(int,Collection) need existing-test/spec evidence.  
RISKS: No source or full existing tests supplied; avoid assuming null, invalid-index, or iterator edge-case behavior.