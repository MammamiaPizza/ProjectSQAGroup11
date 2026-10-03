TARGETS: parseDefineFlag handles quoted strings with embedded quotes (e.g., FOO="x'")
TARGETS: parseDefineFlag handles double quotes, escaped backslashes, empty values
TARGETS: RemoveConstantRValuesCallback.trySimplify preserves expressions with define-vars
ORACLES: define flag with ' or " must not throw RuntimeException; valid define added
ORACLES: constant sub-expressions using defines must not be removed when define present
CASES: FOO="x"" -> valid define; constant ref var a=FOO; expect not simplified to literal
CASES: FOO='value' (single quotes) input; ensure correct parsing
CASES: FOO= (empty value) -> define with empty string; constant expression uses FOO<>empty
CASES: multiple defines with mixed quotes; verify constant folding does not alter code
RISKS: No detailed spec for expected AST after constant removal when define strings contain
RISKS: Cannot inspect test assertions; must infer pass/fail based on bug description only