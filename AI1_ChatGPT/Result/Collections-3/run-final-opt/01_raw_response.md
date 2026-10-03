TARGETS: CollectionUtils.removeAll(Collection collection, Collection remove), especially duplicate handling.
ORACLES: Trigger testRemoveAll: expected result size 1, observed buggy result size 2.
CASES: Remove values from a collection with duplicates; verify every matching occurrence is excluded.
CASES: Normal no-match and mixed match/non-match inputs; preserve non-removed elements and multiplicity.
CASES: Empty collection/remove boundaries; null behavior is unspecified by supplied context.
RISKS: Only trigger assertion is provided; exact ordering, collection type, and null contract are unknown.