TARGETS FinalMockCandidateFilter.filterCandidate/thenInject picks first remaining mock candidate.
TARGETS NameBasedCandidateFilter.filterCandidate delegates via next; selects mock(s) by field name.
TARGETS TypeBasedCandidateFilter.filterCandidate delegates via next; selects by field type.
TARGETS
PropertyAndSetterInjection.processInjection/initializeInjectMocksField/orderedInstanceFieldsFrom
drive candidate filtering.
ORACLES Trigger test shouldInsertFieldWithCorrectNameWhenMultipleTypesAvailable expects null when no
name match.
ORACLES Failure: expected null but got candidate2 means wrong candidate retained/injected.
CASES Exact field-name match among multiple same-type mocks.
CASES Multiple same-type, no name match -> no injection; type mismatch excluded; empty candidate
set.
CASES Single type-matching fallback; superclass fields via while(fieldClass != Object.class).
RISKS Only signatures supplied; internal chain order/fallback not fully specified; do not invent
extra APIs.