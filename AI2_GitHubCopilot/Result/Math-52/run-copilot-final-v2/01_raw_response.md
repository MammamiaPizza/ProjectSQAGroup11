TARGETS: Rotation(Vector3D u1, Vector3D u2, Vector3D v1, Vector3D v2) constructor cross-product
logic, Rotation(double[][] m, double threshold) orthogonalization, getAngle() normalization of
quaternion components.
ORACLES: getAngle() must return finite double in [0,π]; Rotation.applyTo(Vector3D) must preserve
vector norm; distance(Rotation, Rotation) must be finite; IDENTITY.getAngle()==0.
CASES: aligned/antialigned vector pairs, near-parallel u1-u2 or v1-v2, nearly singular cross
products, threshold-edge matrices, zero-length input vectors, identity/near-identity rotations.
RISKS: NaN propagation from acos(quasi-singular) or from zero/zero in orthonormalization; no source
for expected 0.6228370359608201 derivation.