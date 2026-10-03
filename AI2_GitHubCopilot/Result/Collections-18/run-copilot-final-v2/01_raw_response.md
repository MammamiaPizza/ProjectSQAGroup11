TARGETS: ListOrderedSet.retainAll(Collection<?>) must keep the wrapped Set and setOrder List
synchronized on retained elements.
TARGETS: asList() and iterator() must reflect post-retainAll surviving elements in original
insertion order.
TARGETS: get(int), remove(int), indexOf(Object) depend on setOrder after retainAll; verify they
match the retained set.
ORACLES: Expected results come from failing ListOrderedSetTest.testRetainAllCollections426 and the
retainAll contract.
ORACLES: ListOrderedSet invariant: setOrder lists each set element once and remains consistent after
retainAll.
CASES: retainAll with a mixed subset keeps survivors in original order and removes all non-member
elements.
CASES: Boundary: empty collection empties list and set; equal/superset argument retains all current
elements.
CASES: Duplicate entries in the argument collection must not duplicate elements in setOrder.
RISKS: Exact assertion/failure details are not provided; only the trigger test method and signature
are known.
RISKS: Cannot inspect patched source; avoid assuming cause beyond retainAll/setOrder inconsistency.