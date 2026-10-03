TARGETS: Normalize.process(externs,root) entry; doStatementNormalizations; splitVarDeclarations;
moveNamedFunctions inside blocks; removeDuplicateDeclarations on scopes.
ORACLES: Expected transformed AST (e.g., no duplicate var, functions moved to top); no exceptions;
CompilerRunnerTest::testIssue115 expectations.
CASES: Input with duplicate var in same scope; function declarations inside if/while; labels;
empty/null root; single var statement.
RISKS: Trigger test name only; bug description absent; oracle must be inferred from testIssue115
behavior; limited API detail (can't inspect visit/Propogate).