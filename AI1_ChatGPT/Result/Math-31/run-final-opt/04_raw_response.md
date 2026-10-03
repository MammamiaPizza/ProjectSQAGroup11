@org.junit.Test
public void testZeroInitialCoefficientIsReplacedBySmallValue() {
    org.apache.commons.math3.util.ContinuedFraction fraction =
        new org.apache.commons.math3.util.ContinuedFraction() {
            @Override
            protected double getA(int n, double x) {
                return n == 0 ? 0.0 : 1.0;
            }

            @Override
            protected double getB(int n, double x) {
                return n == 1 ? 1.0 : 0.0;
            }
        };

    org.junit.Assert.assertEquals(1.0, fraction.evaluate(0.5, 1.0e-12, 10), 0.0);
}

@org.junit.Test
public void testScalingHandlesOverflowInDenominatorOnly() {
    final double large = 1.0e200;
    org.apache.commons.math3.util.ContinuedFraction fraction =
        new org.apache.commons.math3.util.ContinuedFraction() {
            @Override
            protected double getA(int n, double x) {
                if (n == 0) {
                    return 2.0;
                }
                if (n == 1 || n == 2) {
                    return large;
                }
                if (n == 3) {
                    return (2.0 - 1.0 / large) / large;
                }
                return 1.0;
            }

            @Override
            protected double getB(int n, double x) {
                if (n == 2) {
                    return -large;
                }
                if (n == 3) {
                    return 1.0;
                }
                return 0.0;
            }
        };

    org.junit.Assert.assertEquals(1.0, fraction.evaluate(0.5, 1.0e-12, 10), 1.0e-12);
}