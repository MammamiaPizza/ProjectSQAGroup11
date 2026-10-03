TARGETS: injectMocksOnFields field ordering via supertypesLast comparator; ensure exact-type fields
precede ancestor-type fields.
TARGETS: injectMockCandidate type-based filtering and injection logic for mocks into fields.
ORACLES: After injection, exact-type field contains mock; ancestor-type field remains null.
ORACLES: Use assertSame(mock, field.get(testInstance)) and assertNull(otherField.get(testInstance)).
CASES: Class with two fields: exact type (e.g., Set) and supertype (e.g., Collection); inject Set
mock.
CASES: Only ancestor field exists (Collection); inject Set mock; verify injection into ancestor
field.
CASES: Multiple exact-type fields; verify first exact field gets mock (order maintained).
CASES: Mock type matches none; verify no exception or graceful no-op.
RISKS: Comparator may not account for interfaces; only class hierarchy ordering tested.
RISKS: Internal MockCandidateFilter logic may override ordering; test only verifies final outcome.