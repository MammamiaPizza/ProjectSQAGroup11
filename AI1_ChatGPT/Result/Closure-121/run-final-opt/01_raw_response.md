TARGETS: InlineVariables.process; IdentifyConstants/Locals filtering; scope-level doInlinesForScope behavior.
TARGETS: Declaration/init/reference validation, alias candidates, stale-variable handling, declaration removal/value replacement.
ORACLES: Existing InlineVariablesTest::testExternalIssue1053 assertion is the only stated expected-result source.
CASES: Reproduce issue 1053 through compiler input exercising InlineVariables processing and compare test assertion outcome.
CASES: Normal inlining with valid declaration, initialization, and reference in a scope.
CASES: Boundary/error paths for invalid declaration/init/reference, l-values, forbidden vars, and blacklisted tree references.
RISKS: No source snippet, compiler options, input/output expectation, or assertion details are provided.
RISKS: Do not infer exact transformed JavaScript or access private helpers directly.