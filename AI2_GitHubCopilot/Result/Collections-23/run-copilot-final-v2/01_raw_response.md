TARGETS: unmodifiableTrie factory, constructor, equals, hashCode, toString, entrySet, keySet,
values, put, remove, clear, putAll.
ORACLES: Bug test expects unmodifiableTrie on already-UnmodifiableTrie returns the same reference
(no double decoration).
CASES: Normal: wrap mutable trie; verify read delegation (get, containsKey, firstKey, nextKey,
etc.); put/remove/clear must throw UnsupportedOperationException.
CASES: Boundary: wrap an already-UnmodifiableTrie instance; assert returned reference is identical
to input; no new wrapper.
CASES: Boundary: wrap null trie (constructor/factory); handle or expect NullPointerException.
CASES: Error: on wrapped trie, call all mutators (put, putAll, remove, clear) and verify consistent
exception type.
ORACLES: Unmodifiable decorator contract: unmodifiableTrie should not re-wrap if input is already an
UnmodifiableTrie.
RISKS: Exact assertion details (assertSame vs assertNotSame) ambiguous from limited error message;
toString/equals/hashCode may be relevant.
RISKS: Other map view collections (entrySet, keySet, values) might not be wrapped as unmodifiable,
allowing indirect mutation.