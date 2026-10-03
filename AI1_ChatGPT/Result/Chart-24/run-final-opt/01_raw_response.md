TARGETS: GrayPaintScale.getPaint(double), constructors, bounds accessors; trigger is GrayPaintScaleTests::testGetPaint.  
ORACLES: getPaint must not cause Color RGB-range IllegalArgumentException for tested values; Paint result is expected source.  
CASES: default and explicit bounds; value at lower/upper bounds; values below lower and above upper.  
CASES: interior values that exercise grayscale RGB calculation, especially values producing out-of-range components.  
RISKS: Bug report/spec is UNKNOWN; exact expected Paint color mapping and clamping behavior are not provided.