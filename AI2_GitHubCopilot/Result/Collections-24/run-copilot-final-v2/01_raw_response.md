TARGETS: unmodifiableBoundedCollection(BoundedCollection) must preserve identity when already
unmodifiable
TARGETS: unmodifiableBoundedCollection(Collection) overload; private ctor; decorated(); iterator()
TARGETS: mutators add/addAll/clear/remove/removeAll/retainAll must reject changes
ORACLES: assertSame in testDecorateFactory after double decoration of same collection
ORACLES: UnsupportedOperationException assertions in testUnmodifiable for all mutators
ORACLES: expected full-collection equality/content shown in failure message
CASES: decorate already-unmodifiable collection again -> return same instance
CASES: decorate plain BoundedCollection -> contents, maxSize, isFull preserved
CASES: iterator().remove() and all mutators -> UOE, no mutation
RISKS: only failure text given; double-decoration semantics inferred, tests not inspectable