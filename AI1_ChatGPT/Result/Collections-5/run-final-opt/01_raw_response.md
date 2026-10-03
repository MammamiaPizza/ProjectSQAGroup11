TARGETS: SetUniqueList.addAll(int, Collection), especially insertion index with duplicate filtering.  
ORACLES: Trigger TestSetUniqueList.testIntCollectionAddAll: first new element at index 0 is expected 2, not 1.  
CASES: Insert collection containing existing and new elements at index 0; verify retained new-element order.  
CASES: Insert at middle/end with duplicates before, between, and after new values.  
CASES: addAll(Collection) duplicate filtering and returned-change behavior.  
CASES: Empty input and input containing only existing elements.  
RISKS: Index must account for skipped duplicates while preserving requested insertion position.  
RISKS: Context lacks method bodies and exact exception/null/self-collection expectations.