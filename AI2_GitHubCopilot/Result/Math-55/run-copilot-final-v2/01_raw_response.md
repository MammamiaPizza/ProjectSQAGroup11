TARGETS: crossProduct(Vector3D, Vector3D) – catastrophic cancellation for near-parallel vectors
causes magnitude to collapse to 0
ORACLES: expected cross-product magnitude: |u×v| = |u||v|·sin(angle) — test expects exactly 1.0 for
the given near-parallel pair
ORACLES: cross(v1,v2) must be orthogonal to v1 and v2; cross(v1,v2) = –cross(v2,v1); cross(v,v) =
ZERO
CASES: unit vectors separated by tiny angle (≈1e-8 rad) — verify nonzero cross magnitude
approximates sin(angle) rather than 0
CASES: orthogonal unit vectors, parallel vectors, vectors with large components whose product
differences nearly cancel
CASES: cross with ZERO, NaN, infinite components (if safe) — verify expected return/exception per
spec
RISKS: no access to the corrected implementation; must infer “stable” behaviour from geometry and
magnitude tolerance
RISKS: limited context — only crossProduct is implicated; may need relaxed epsilon for near-parallel
vectors (e.g., 1e-14 instead of 1e-9)
RISKS: divergence from Math-55b API: no additional stable method available; must test with existing
crossProduct signature only