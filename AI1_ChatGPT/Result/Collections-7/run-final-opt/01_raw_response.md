TARGETS: ExtendedProperties.getKeys() and getKeys(String prefix) iterator traversal/order after property mutation.  
ORACLES: Trigger tests: key iteration must not throw NoSuchElementException; testKeySet4 expects "c", not "b".  
CASES: Add multiple keys, iterate getKeys() fully; verify hasNext()/next() consistency and no extra next().  
CASES: Prefix iteration with matching/nonmatching keys; verify returned keys exclude prefix and preserve listed-key behavior.  
CASES: Set/clear/re-add keys before iteration; check stale or duplicate key entries do not alter next key.  
RISKS: Hashtable enumeration order is unspecified; assert trigger-derived sequence only where keysAsListed defines it.  
RISKS: No source body or trigger-test setup provided; limit expectations to reported failures and public signatures.