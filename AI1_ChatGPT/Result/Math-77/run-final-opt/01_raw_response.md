TARGETS: ArrayRealVector.dotProduct and OpenMapRealVector.dotProduct behavior implicated by basic-function triggers.
ORACLES: Trigger assertions require dot product/result value 6.0 for existing basic-function vector inputs.
CASES: Dense vectors with ordinary nonzero entries producing expected 6.0.
CASES: Sparse vectors with stored and implicit-zero entries producing expected 6.0.
CASES: Operand order and dense/sparse RealVector combinations, if dotProduct accepts both implementations.
RISKS: Reported wrong results differ by implementation (128.0 dense, -3.0 sparse).
RISKS: Available signatures/source are truncated; exact triggering input vectors and modified method bodies are unavailable.