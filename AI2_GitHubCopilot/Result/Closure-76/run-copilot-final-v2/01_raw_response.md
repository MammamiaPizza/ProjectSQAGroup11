TARGETS: visit() handling of EXPR_RESULT nodes with nested assignments, side-effect detection for
RHS
TARGETS: isDeadAssignment logic for variables used only in conditionals or expressions with side
effects
ORACLES: process() test helper comparing output JS string to expected, based on stable test
infrastructure
ORACLES: assignments without side effects & no subsequent reads must be removed; assignments with
calls/property accesses preserved
CASES: x = (y = 1) where x,y unused; verify both removed (testInExpression2)
CASES: a = b = c() where only a used; b assignment has side-effect (call) so keep b = c()
CASES: dead assignment inside object literal ({p: x = foo()}), x unused; keep RHS call but drop
assignment
CASES: dead assignment used only in dead branch (if(false){ use(x); } x=1;), remove entire statement
CASES: for-loop increment i++ with i dead after loop; preservation depends on loop structure
RISKS: Exact removal rules depend on implementation details not visible; need to inspect failing
test source for precise expectations