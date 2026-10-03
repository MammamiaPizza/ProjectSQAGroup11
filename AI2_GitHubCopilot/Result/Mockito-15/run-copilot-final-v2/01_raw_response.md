TARGETS: filterCandidate must select mock; thenInject must try property setter before direct field
access.
ORACLES: Existing test shouldInjectUsingPropertySetterIfAvailable validates setter receives mock,
not direct field set.
ORACLES: Spy on fieldInstance to verify setter invoked with correct mock; verify field reflection
not used.
CASES: Normal: setter exists + matching mock → inject via setter, not field.
CASES: Boundary: setter exists but no matching mock → no injection; no setter → fallback to field
reflection.
CASES: Error: setter param type mismatch with mock, null mocks/fieldInstance, setter throws
exception.
RISKS: Only filterCandidate/thenInject signatures known; internal chain and setter detection are
opaque.
RISKS: No source for default injection logic; assumptions based solely on bug summary and test name.