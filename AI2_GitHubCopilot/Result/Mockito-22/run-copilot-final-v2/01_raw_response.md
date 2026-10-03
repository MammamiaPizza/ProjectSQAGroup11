TARGETS: Equality.areEqual(Object,Object)
TARGETS: Equality.isArray(Object)
ORACLES: Standard Java equality: null-null true; null-nonnull false; Object.equals for non-arrays;
Arrays.equals for arrays
ORACLES: Array equality: same-type arrays with equal content true; different lengths false;
different elements false
CASES: both null, same object ref, different types, one null one non-null, one null one array, empty
arrays
CASES: arrays equal length/same elements, arrays diff length, arrays diff elements, primitive arrays
(int[], byte[], char[], etc.)
CASES: nested Object[] arrays, String[] arrays, arrays of boxed primitives (Integer[], etc.)
RISKS: areEqual throws RuntimeException on array arguments (Bug 484 trigger); verify no
RuntimeException for any array type
RISKS: isArray may misclassify arrays (e.g., primitive vs Object), causing ClassCastException or
unexpected false
RISKS: Deep/nested array equality may be missing, causing false negatives or StackOverflow on
self-referential arrays