TARGETS: Expression.iterate(EvalContext), iteratePointers(EvalContext), PointerIterator, ValueIterator behavior.
ORACLES: Existing ExtensionFunctionTest.testNodeSetReturn expected values: "Nested: Name 1", "Nested: Name 2".
CASES: Extension function node-set result iterates returned nodes as values, not pointer-path strings or nested collection.
CASES: Verify iterator order and exactly two values for test:nodeSet() in the trigger context.
CASES: Compare value iteration versus pointer iteration for a node-set-returning expression.
RISKS: Context setup, extension registration, and expected node values are only evidenced by the existing trigger test.
RISKS: No modified implementation details or broader null/empty/error behavior are provided.