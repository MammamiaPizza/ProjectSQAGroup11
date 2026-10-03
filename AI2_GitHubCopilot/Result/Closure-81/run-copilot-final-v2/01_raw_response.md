TARGETS: IRFactory.TransformDispatcher handling FUNCTION token for unnamed statement.
TARGETS: IRFactory.transform method (dispatch to function node create).
ORACLES: Expected AST from ParserTest.testUnnamedFunctionStatement (e.g., FUNCTION node with empty
NAME).
CASES: Normal: "function() {}" as statement, verify node has no name.
RISKS: Named function statements and function expressions may be affected; other parser tests.