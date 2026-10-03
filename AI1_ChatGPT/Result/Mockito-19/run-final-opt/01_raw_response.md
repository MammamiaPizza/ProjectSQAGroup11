TARGETS: PropertyAndSetterInjection candidate selection through TypeBased, NameBased, and Final filters.
ORACLES: Trigger expects correct-name mock injection when multiple mocks share a field type.
ORACLES: Trigger failure shows an unrelated field expected null but received candidate2.
CASES: Multiple same-type mocks; inject only the candidate whose mock name matches the target field name.
CASES: Unique type-compatible mock should remain eligible for property/setter injection.
CASES: Same-type candidates with no matching name should not select an arbitrary candidate.
CASES: Fields excluded by final/static filtering should not be injection targets.
RISKS: Candidate collection mutation/order may affect subsequent fields and cause wrong duplicate injection.
RISKS: Available context omits method bodies, mock naming rules, and OngoingInjecter behavior.