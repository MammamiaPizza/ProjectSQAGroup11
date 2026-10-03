TARGETS: Vector3D.crossProduct(Vector3D, Vector3D), especially cancellation-prone component computation.
ORACLES: Trigger test expects a cross-product component of 1.0, not 0.0.
CASES: Near-cancelling large-coordinate vectors yielding a small nonzero cross-product component.
CASES: Standard basis-vector cross products for component/sign orientation.
CASES: Parallel vectors should produce the zero vector.
RISKS: Exact input values and full expected vector from trigger test are not provided.
RISKS: No other program version/specification is available for deriving additional numerical tolerances.