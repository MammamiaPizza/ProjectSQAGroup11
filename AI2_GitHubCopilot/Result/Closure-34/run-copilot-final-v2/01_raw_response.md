TARGETS: CodeGenerator.addExpr(Node,int,Context) for deep ADD chains.
ORACLES: Output equals expected JS string; no StackOverflowError.
ORACLES: Compare against manually verified JavaScript code for moderate depth.
CASES: ADD chains: depth 0 (leaf), 10, 100, 500, 1000 (boundary for stack overflow).
CASES: Deep chains of SUB, MUL, BITOR, etc. to detect recurrence in other operators.
CASES: Mixed ADD and parenthesized subexpressions to test precedence insertion.
RISKS: Unknown Node hierarchy; building large AST may hit memory/time limits.
RISKS: Fix may be ADD-specific, leaving other binary ops vulnerable; test them.