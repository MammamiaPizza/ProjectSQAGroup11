TARGETS: Line.revert(), Line.getOrigin(), Line.getDirection(), Line.contains(),
Line.distance(Vector3D), Line.getAbscissa(), Line.pointAt()
ORACLES: After revert, origin must equal original.getOrigin(); direction must be
original.getDirection().negate(); revert().revert() ~ original
ORACLES: For any point, original.getAbscissa(p) == -reverted.getAbscissa(p); original.contains(p) ==
reverted.contains(p); distance invariant
CASES: Normal revert of Line(Vector3D.ZERO, new Vector3D(1,0,0)) → origin ZERO, direction (-1,0,0);
contains (0.5,0,0) on both; abscissa flips sign
CASES: Boundary: line with very small direction (1e-8, 1e-8, 0); revert yields unit direction
negated; revert twice isSimilarTo original within epsilon
CASES: Error: revert after copy constructor preserves origin and direction reversal; distance to
same point identical before/after revert
RISKS: Bug may produce non-unit direction causing downstream errors; verify direction.getNorm()≈1;
use tolerance in assertions
RISKS: No access to internal state; rely on public APIs; floating-point residuals may cause false
failures if epsilon too tight