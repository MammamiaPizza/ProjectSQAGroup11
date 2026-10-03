TARGETS: SimplexTableau initialization/table creation and getSolution basis-variable extraction.
ORACLES: Existing SimplexSolverTest::testMath272 expects objective value 1.0, not 0.0.
CASES: Degenerate/duplicate basic-column tableau where a decision variable must retain its RHS value.
CASES: Normal LP solution extraction with basic and non-basic decision variables.
CASES: Constraint normalization for negative RHS and LEQ/GEQ/EQ relationships.
RISKS: Package-private class and protected methods require same-package test access/subclassing.
RISKS: Context lacks constructor signatures and exact MATH-272 LP constraints; do not infer them.