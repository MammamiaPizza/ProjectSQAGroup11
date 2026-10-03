TARGETS: InlineCostEstimator.getCost(Node root) — returns compiled-size estimate via
CompiledSizeEstimator
TARGETS: InlineCostEstimator.getCost(Node root, int costThreshhold) — caps result at threshold
TARGETS: private CompiledSizeEstimator extends CodeConsumer; its addIdentifier/addLiteral etc
determine cost
ORACLES: for a single identifier node, expected cost = ESTIMATED_IDENTIFIER_COST =
ESTIMATED_IDENTIFIER.length()
ORACLES: if threshold ≤0, cost=0 early exit; else cost = min(actual estimate, threshold)
CASES: single NAME node → ESTIMATED_IDENTIFIER_COST; two NAME nodes → 2 * ESTIMATED_IDENTIFIER_COST;
no double counting
CASES: threshold=0 → cost 0; threshold=ESTIMATED_IDENTIFIER_COST → exact; threshold > total → full
sum
CASES: empty script → cost 0; deep AST → sum of children costs; large tree does not overflow
RISKS: ESTIMATED_IDENTIFIER constant not visible; tests cannot assume its numeric value, only its
length or ratio
RISKS: cannot construct Node without Closure parser; tests need to parse JS snippets or manually
build AST tree