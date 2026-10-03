TARGETS: DefaultKeyedValues.getIndex(), removeValue(int/key), and index rebuilding after removals/clear.  
TARGETS: DefaultKeyedValues2D.removeColumn(key/index), including column-key/index consistency and empty rows.  
ORACLES: Trigger assertions: absent key getIndex() returns -1; removing a value updates retained keys/values.  
ORACLES: Trigger exceptions: column removal and DefaultCategoryDataset path must not index into empty data.  
CASES: Add multiple keys, remove first/middle/last by index and key, then verify count, keys, values, indices.  
CASES: Remove sole value; verify empty state and absent-key index -1; repeat removal only if API behavior is established.  
CASES: 2D remove a populated column, sole column, and key across rows; verify column count/keys and row cleanup.  
RISKS: No bug report/source implementation provided; exception behavior for invalid indices/unknown keys is unspecified.