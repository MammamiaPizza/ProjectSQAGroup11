TARGETS: inlineAliasIfPossible, updateObjLitOrFunctionDeclaration,
collapseDeclarationOfNameAndDescendants in CollapseProperties
ORACLES: assertions in failing CollapsePropertiesTest methods; expected alias names and collapsed
property accesses
CASES: add property to child of uncollapsible function (local scope); alias at depth1 (1_1,1_2,1_3
variants)
CASES: add property to uncollapsible named ctor depth1; add property to uncollapsible function
depth1, depth2; alias at depth2
RISKS: test source unavailable; bug may affect only local scope and depth ≤2; no global scope or
deeper depth coverage