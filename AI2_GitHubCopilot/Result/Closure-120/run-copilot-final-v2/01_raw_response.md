TARGETS: ReferenceCollectingCallback.visit tracks refs per Var; addReference; exitScope finalizes
collections; isBlockBoundary for block detection.
TARGETS: BasicBlock tracking via blockStack; enterScope/exitScope for scope changes;
isLhsOfForInExpression for special assignment.
ORACLES: testExternalIssue1053 expects InlineVariables to succeed; check reference well-definedness,
initializing declarations, assignment counts.
CASES: normal: variable declared, assigned, used; boundary: variable shadowed in nested scope;
for-in LHS assignment; global vs local.
CASES: variable reassigned after declaration (non-well-defined); multiple assignments; variable used
in conditional block (basic block boundary).
RISKS: bug specifics unknown; test expectation inferred from InlineVariables failure; need actual
test code for exact expected behavior.