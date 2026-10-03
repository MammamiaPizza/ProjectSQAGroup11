TARGETS: TypeValidator mismatch registration/reporting during type redefinition and forward-declared unresolved names.
ORACLES: Triggered tests define expected warning counts and absence/presence of JSC_DUP_VAR_DECLARATION.
CASES: Redefine enum-qualified a.A as constructor/function; verify diagnostics in normal and loose type checking.
CASES: Issue635b scenario: verify the expected warning is emitted.
RISKS: TypeValidator methods shown are private; test through compiler/type-check integration only.
RISKS: Context lacks method bodies and exact Issue635b source, so do not infer additional diagnostics.