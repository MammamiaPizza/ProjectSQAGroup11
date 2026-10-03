TARGETS: removeAll(Collection collection, Collection remove) – cardinality-aware removal.
ORACLES: testRemoveAll failure shows expected size from cardinality subtraction. subtract(a,b)
javadoc likely same semantics.
CASES: Duplicates in source only; remove has extra copies; disjoint; identical; remove superset;
empty collections; null elements.
RISKS: Buggy impl likely delegates to Collection.removeAll, ignoring multiplicities. Test coverage
only basic removeAll; other methods may be correct.