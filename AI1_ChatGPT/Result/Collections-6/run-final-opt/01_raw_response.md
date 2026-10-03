TARGETS: Flat3Map entrySet/keySet/values views and their iterators; mapIterator mutation behavior  
ORACLES: Trigger TestFlat3Map.testCollections261 expects 1 but receives 0  
CASES: Populate 1–3 entries; verify view sizes, iteration, contains, and remove results  
CASES: Exercise null/non-null keys and values through view removal and iterator removal  
CASES: Test transition at the 3-entry flat-storage boundary and after clear  
RISKS: Bug summary does not identify the exact failing method or asserted operation  
RISKS: No source/test body is provided; derive expectations only from existing trigger behavior