TARGETS: ReachingUsesJoinOp.apply merges reaching-use maps from predecessor CFG nodes.
TARGETS: addToUseIfLocal/removeFromUseIfLocal add/remove variable uses based on local scoping
conditions.
TARGETS: hasExceptionHandler checks whether a CFG node sits inside a try with a catch or finally
block.
ORACLES: Expected inlining outcome from FlowSensitiveInlineVariablesTest.testIssue794b assertion.
CASES: normal single-block: define, use, no exceptional path; reaching use present only at use.
CASES: boundary try-catch: define in try, use in catch; reaching use must not leak to post-try.
CASES: error reversed: define in try, use after try-catch; analysis erroneously adds use from catch.
CASES: conditional inline: var defined in if-block, used after if/else; join must conservatively
mark reach.
CASES: nested try-blocks with multiple exception handlers; join across handlers must correctly union
uses.
RISKS: CFG node types and full join semantics unknown; bug reproduction depends on internal graph
shape.