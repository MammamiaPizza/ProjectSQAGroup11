TARGETS: ReturnsEmptyValues.answer() handling of Comparable/compareTo return type
ORACLES: mock.compareTo(mock) must return 0 (self-comparison), not 1
CASES: self-comparison (same mock reference), cross-mock comparison, null comparison
RISKS: Bug 467 only affects Comparable return; other empty-value types unchanged