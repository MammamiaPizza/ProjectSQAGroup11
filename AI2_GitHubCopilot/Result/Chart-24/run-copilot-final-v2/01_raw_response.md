TARGETS: GrayPaintScale.getPaint(double) – maps value to gray Color in [lower,upper]→[0,255]
ORACLES: For value in bounds, R=G=B = linear scaled int (clamp 0–255) verified via Color.getRed()
CASES: Default ctor bounds (likely 0,1): getPaint(0.0)→0, getPaint(1.0)→255, getPaint(0.5)→~127
CASES: Custom bounds (0,255)→identity map; (0,100)→mid~128; use extreme bounds +-1e9
CASES: Value at exact lowerBound→0, exact upperBound→255 (check rounding stable)
CASES: Out-of-range value (just below/above) – expected clamp to 0/255 or explicit exception
RISKS: Floating rounding (value==upperBound→fraction=1.0000000000000002 → gray=256) throws IAE; must
clamp
RISKS: lowerBound==upperBound: zero denominator → NaN/div-zero; document fallback (safe default
gray)
RISKS: Negative lowerBound with positive upperBound still maps correctly if clamp prevents negative
gray