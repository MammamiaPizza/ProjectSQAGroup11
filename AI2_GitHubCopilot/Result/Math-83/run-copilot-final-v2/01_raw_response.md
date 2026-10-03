TARGETS: SimplexTableau.getSolution, createTableau, getBasicRow, initialize, normalize,
discardArtificialVariables, divideRow
ORACLES: Expected optimal value 6.9 from bug report MATH-286; feasible solution objective >= any
feasible if maximize
ORACLES: Tableau equality after known solve steps; null when no basic row; solution within epsilon
of constraints
CASES: Normal LP with mix of <=,=,>= constraints; LP requiring artificial variables; maximize and
minimize objectives
CASES: Boundary: no feasible solution (infeasible), unbounded LP; large coefficients; zero RHS;
all-slack initial basis
RISKS: Only trigger testMath286 provided; no SimplexTableau source; floating-point tolerance
required for comparisons