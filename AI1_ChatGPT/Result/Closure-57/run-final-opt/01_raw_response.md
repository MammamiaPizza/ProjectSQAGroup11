TARGETS: ClosureCodingConvention.extractClassNameIfRequire(Node node, Node parent)
ORACLES: Existing testRequire expects null; buggy result is "foo".
CASES: Reproduce the require AST shape exercised by testRequire.
CASES: Verify non-matching require context returns null, per trigger expectation.
RISKS: Node/parent AST construction details are not provided in this context.