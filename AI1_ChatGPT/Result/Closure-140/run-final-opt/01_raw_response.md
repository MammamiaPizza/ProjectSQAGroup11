TARGETS: Compiler.reportCodeChange() behavior during CrossModuleCodeMotion with empty modules
ORACLES: Existing trigger assertion: no reportCodeChange() call when compiler output/AST is unchanged
CASES: Empty JSModule alongside non-empty modules; run CrossModuleCodeMotion optimization
CASES: Empty-only module set and normal movable code across non-empty modules
CASES: Boundary: empty module before/after source modules in module dependency/order setup
RISKS: reportCodeChange() may be observable only through test compiler instrumentation, not public result APIs
RISKS: Context lacks exact module construction, pass options, and intended behavior beyond no-op change reporting