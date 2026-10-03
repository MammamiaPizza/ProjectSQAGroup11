TARGETS: ValueInstantiator delegate/array-delegate capability and creation methods.
TARGETS: StdValueInstantiator configured array-delegate type/creator selection.
ORACLES: DelegatingArrayCreator1804Test::testDelegatingArray1804 must deserialize without abstract-type failure.
CASES: Delegating creator receiving JSON array for MyType; verify resulting instance/value.
CASES: Distinguish regular delegate from array-delegate creator when both capability paths exist.
CASES: Boundary: absent array-delegate creator/type reports non-creatable/null as defined by APIs.
RISKS: Context omits full trigger source and exact MyType fields/assertions; derive expected values only from trigger.