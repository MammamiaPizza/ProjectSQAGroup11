TARGETS: AnalyzePrototypeProperties.process/getAllNameInfo; CrossModuleMethodMotion.process and method movement.
ORACLES: Existing testIssue600, testIssue600b, and testIssue600e compiler-output assertions.
CASES: Reproduce Issue 600 inputs across modules; verify resulting method placement/output.
CASES: Cover prototype-property assignments and global-function/property reference analysis used for movement.
RISKS: Movement depends on declaration/reference module tracking and propagated NameInfo edges.
RISKS: API details and exact expected transformed code are unavailable; derive expectations only from trigger tests.