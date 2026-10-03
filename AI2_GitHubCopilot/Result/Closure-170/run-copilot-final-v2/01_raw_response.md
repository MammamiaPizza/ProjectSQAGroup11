TARGETS: canInline(), inlineVariable(), GatherCandiates (hook traversal), getDefCfgNode() for hook
assignments
ORACLES: testVarAssinInsideHookIssue965 expected behavior (var assign inside hook must not block
safe inlining)
CASES: hook with var assign in both branches (same RHS), one branch only, different RHS, nested
hooks, after-hook use
RISKS: limited to one bug scenario; hook analysis may interact with dead code, other inlining
phases; no JS engine ground truth