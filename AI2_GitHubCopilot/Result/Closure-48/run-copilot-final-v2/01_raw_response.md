TARGETS: TypedScopeCreator enum check: DiscoverEnumsAndTypedefs visitor must detect
non-object/non-enum initializer
TARGETS: defineSlot/defineName to report "enum initializer must be an object literal or an enum"
warning
TARGETS: Scope builders' handling of @enum JSDoc; check before assigning type
ORACLES: Warning message "enum initializer must be an object literal or an enum" (compiler constant)
ORACLES: TypeCheckTest::testIssue586 expects warning when enum init is non-object literal
CASES: Invalid init (function call, variable, number, string); valid object literal; valid enum
alias
CASES: Extern enum; enum without init; nested enum; init is non-literal expression
CASES: Enum redeclaration; enum inside function; init is another enum's property
RISKS: Only buggy API visible; full warning guards unknown; must use the provided TypeCheckTest
harness
RISKS: Some invalid patterns may not trigger w/o full check; context limited to Defects4J
Closure-48b