TARGETS: FastMath.max(float,float) and FastMath.min(float,float), especially operand ordering.
ORACLES: Trigger assertion establishes max(50.0f,-50.0f) must return 50.0f.
ORACLES: Compare float min/max results to expected ordering behavior; no alternate version available.
CASES: Positive/negative reversed pairs: max(50f,-50f), max(-50f,50f); analogous min pairs.
CASES: Equal values, zero with signed zero, and adjacent finite float values.
CASES: Float extremes Float.MAX_VALUE, -Float.MAX_VALUE, Float.MIN_VALUE, -Float.MIN_VALUE.
CASES: NaN and infinities only if existing FastMathTest specifies their expected behavior.
RISKS: Context identifies only the float min/max trigger; implementation and full test semantics are truncated.