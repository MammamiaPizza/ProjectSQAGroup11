TARGETS: IRFactory.transform for ForEachLoop nodes; justTransform; parseDirectives; isDirective
handling "each".
TARGETS: newNode, newStringNode, transformTokenType used for for-each loop components.
ORACLES: ParserTest.testForEach expected Node type/children; AST structure per ECMA for-each spec.
CASES: Basic for-each with statement; nested for-each; for-each inside for; with JSDoc; label.
CASES: Boundary: empty body; variable declaration; destructuring; for-each as directive-like
keyword.
CASES: Error: missing body, missing "in", invalid iterator expression.
RISKS: Single trigger test may miss for-each with "in" vs "of" semantic differences.
RISKS: Bug could be confined to IRFactory.transform; other parse pathways (Block, Script) untested.
RISKS: Incomplete coverage of reserved keyword interactions (e.g., "each" with "let", "const").