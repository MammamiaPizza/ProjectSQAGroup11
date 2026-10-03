TARGETS: Complex.add(Complex) – propagation of NaN arguments must return NaN
TARGETS: Complex.equals(Object) – must treat NaN components as equal to NaN
ORACLES: IEEE 754 NaN propagation rules; Javadoc for Complex.add
ORACLES: ComplexTest.testAddNaN expects result to equal Complex.NaN via equals
CASES: Complex.NaN.equals(Complex.NaN) must return true (NaN equality)
CASES: Complex.ZERO.add(Complex.NaN) must return a NaN that equals Complex.NaN
CASES: new Complex(1,2).add(Complex.NaN) must equal Complex.NaN
CASES: Complex.NaN.add(Complex.NaN) must equal Complex.NaN
RISKS: Double.NaN != Double.NaN breaks default equals; must override for NaN equality
RISKS: add may produce NaN before checking equals; isNaN() may be needed for assertions