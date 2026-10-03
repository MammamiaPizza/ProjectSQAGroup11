TARGETS: CodeConsumer.append must preserve negative zero sign (e.g., -0.0, not 0)
ORACLES: testIssue582 expects var x=[-0.0] for an array containing negative zero
CASES: Negative zero literal, negative zero after operators, negative zero inside arrays/objects,
plain zero, -0 vs 0
RISKS: Only abstract class given; actual number formatting lives in subclass, context lacks concrete
implementation details