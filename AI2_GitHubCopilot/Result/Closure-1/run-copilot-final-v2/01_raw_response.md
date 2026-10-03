TARGETS: RemoveUnusedVars.process, isRemovableVar, removeUnreferencedFunctionArgs,
CallSiteOptimizer.optimize/applyChanges
ORACLES: Compare AST after optimization: used globals present; unused vars removed; function args
kept if referenced; JS output vs expected
CASES: Global var used in inner function; param in closure; forward-declare type preserved; issue787
self-reference
CASES: Simple mode leaves unused params; debug flag keeps vars; mutual recursion; removeGlobal1/2/3
patterns
RISKS: Behavior depends on other passes' output; unknown exact spec; tests may break if compiler
internals change