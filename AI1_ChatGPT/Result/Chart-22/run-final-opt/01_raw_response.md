TARGETS: KeyedObjects2D getObject(key,key), removeObject, removeRow, removeColumn, key/index lookup methods.
ORACLES: Trigger outcomes expose retained/missing keys, counts, and returned values after mutation.
CASES: Add/set values across multiple rows/columns; retrieve by existing row/column keys.
CASES: Remove a value and verify resulting row/column counts and remaining key-addressable values.
CASES: Remove column by index and key; verify other columns/values remain addressable.
CASES: Remove row by key; verify valid-key removal does not produce negative-index access.
CASES: Lookup keys such as C2 after updates/removals; distinguish existing from absent keys.
RISKS: Current failures include UnknownKeyException, wrong count (expected 1, got 2), and index -1 access.
RISKS: setObject behavior is trigger-relevant, but its signature/contract is absent from supplied API context.