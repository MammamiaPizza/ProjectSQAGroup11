TARGETS: SimplexTableau tableau creation, constraint normalization, and solution extraction paths.
TARGETS: Column offsets/counts and basic-row detection affect simplex tableau interpretation.
ORACLES: Existing SimplexSolverTest::testMath781 assertion is the primary expected-result source.
ORACLES: Compare returned PointValuePair and feasibility only where the trigger test specifies them.
CASES: Reproduce the trigger optimization input through the public solver/tableau path.
CASES: Exercise equality, <=, and >= constraints if present in the trigger's tableau setup.
CASES: Cover artificial/slack-variable creation and phase-1 objective removal when applicable.
RISKS: SimplexTableau is package-private; tests may need same-package access or solver-level coverage.
RISKS: Trigger input and exact asserted values are absent, limiting precise independent expectations.