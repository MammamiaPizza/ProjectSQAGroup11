TARGETS: EqualPredicate.equalPredicate(T), equalPredicate(T,Equator<T>), constructors, evaluate(T), getValue()
ORACLES: Trigger TestEqualPredicate.objectFactoryUsesEqualsForTest is the sole stated expected-result source
CASES: Factory predicate evaluates true for an object equal to its stored test value
CASES: Factory predicate evaluates false for a non-equal object
CASES: Verify objectFactoryUsesEqualsForTest behavior specifically through equalPredicate(T).evaluate(T)
RISKS: Equality semantics for nulls and Equator overload are not specified in provided context
RISKS: No source/body or other-version comparison is available; avoid assuming exception behavior