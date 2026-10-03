TARGETS: IRFactory AST transformation/dispatch for function statements, especially missing function names.
ORACLES: ParserTest::testUnnamedFunctionStatement assertion behavior and produced parser/IR result.
CASES: Named function statement as normal control; unnamed function statement as trigger input.
CASES: Verify transformation does not silently create an invalid named declaration/node shape.
RISKS: Exact expected diagnostic versus AST shape is not provided in the available context.
RISKS: IRFactory methods are private; testing likely must exercise parsing rather than direct calls.