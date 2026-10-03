TARGETS: Same.matches(Object), especially null wanted/actual identity comparison.
TARGETS: Same.describeTo(Description) and private appendQuoting(Description) null handling.
ORACLES: Trigger expects AssertionError, not NullPointerException, when null is passed to same.
CASES: Same(null).matches(null); verify no NPE and identity-match result.
CASES: Same(null).matches(nonNull); Same(nonNull).matches(null); distinct equal objects vs same reference.
CASES: describeTo for null and non-null wanted; ensure description generation does not throw.
RISKS: Exact matcher return values and description text are not provided beyond trigger behavior.
RISKS: Private appendQuoting is testable only indirectly through describeTo.