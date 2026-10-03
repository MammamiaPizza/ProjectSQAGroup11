TARGETS: createTableau (initial tableau layout), getSolution (objective value extraction), normalize
(constraint direction), getBasicRow (pivot row)
ORACLES: expected obj=1.0 from testMath272; manual LP solve; verify constraints satisfied after
solve
CASES: LP with artificial vars, maximize LP, minimize LP, single-constraint LP, redundant
constraints, zero-RHS constraints
RISKS: testMath272 LP data undisclosed; bug may lie in offset/index arithmetic or artificial-var
handling; requires SimplexSolver orchestration