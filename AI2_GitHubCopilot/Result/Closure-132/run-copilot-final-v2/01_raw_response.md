TARGETS: tryReplaceUndefined (undefined-to-void-0 substitution); tryMinimizeCondition (condition
simplification)
TARGETS: tryFoldSimpleFunctionCall (typeof handling); tryMinimizeIf (if-condition folding)
ORACLES: Replace undefined with void 0 only when it refers to the global (unresolvable) undefined
value
ORACLES: Do not replace if undefined could be a shadowed local variable, parameter, or property
access
ORACLES: Verify optimized AST preserves original semantics; test with Node type/children assertions
CASES: Normal – if(x===undefined) with no local shadow → replaced with x===void 0
CASES: Boundary – local var undefined=42; if(x===undefined) → no replacement (keeps undefined)
CASES: Boundary – undefined as argument, parameter name, or obj.undefined → no replacement
CASES: Error – nested functions with outer shadow, inner comparison → check scoping, avoid false
replacement
RISKS: Only bug & method signatures provided; full test source not visible; must infer exact
expected AST