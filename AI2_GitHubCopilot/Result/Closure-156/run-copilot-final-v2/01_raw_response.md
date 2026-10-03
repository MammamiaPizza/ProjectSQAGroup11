TARGETS: inlineAliasIfPossible, flattenReferencesTo, collapseDeclarationOfNameAndDescendants
ORACLES: testIssue389 expects aliased nested enum refs resolve correctly after collapse
ORACLES: testAliasedTopLevelEnum expects top-level enum alias remains valid post-collapse
CASES: alias of global enum constant; alias of namespace‑scoped enum; chain aliasing
CASES: boundary: alias used before declaration; alias after redefinition; empty alias path
RISKS: exact expected JS output not provided; Oracle only inferred from trigger names
RISKS: limited to black‑box input; cannot inspect bug‑fix diff or failing assertion messages
RISKS: must avoid inventing APIs; test inputs must be plausible for the given CollapseProperties
pass