TARGETS: GlobalNamespace reference collection/twin handling; JSDocInfoBuilder JSDoc annotation recording/build.
ORACLES: Existing trigger assertions: no JSC_USELESS_CODE for JSDoc comment input; collapse output/assertions.
CASES: JSDoc-only/commented expression at line 1; verify CheckSideEffects emits zero unexpected warnings.
CASES: Twin references that cancel child property collapsing; nested assignment and comma-operator expressions.
CASES: Global-name/property references across declarations, assignments, and nested/comma contexts.
RISKS: GlobalNamespace internals/private APIs limit direct unit access; prefer compiler-pass integration tests.
RISKS: Exact collapse behavior/input fixtures are not provided; derive expected results only from existing triggers.