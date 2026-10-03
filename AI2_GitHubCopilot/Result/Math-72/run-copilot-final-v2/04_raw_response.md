@Test
    public void testDeprecatedConstructorAndSolve() throws Exception {
        org.apache.commons.math.analysis.UnivariateRealFunction f =
            new org.apache.commons.math.analysis.UnivariateRealFunction() {
                public double value(double x) { return x - 2.0; }
            };
        BrentSolver solver = new BrentSolver(f);
        double result = solver.solve(0.0, 5.0);
        assertEquals(2.0, result, 1E-10);
    }

 @Test
 public void testInitialBracketsWithMaxReduceInterval() throws Exception {
     org.apache.commons.math.analysis.UnivariateRealFunction f =
         new org.apache.commons.math.analysis.UnivariateRealFunction() {
             public double value(double x) { return x - 5.0; }
         };
     BrentSolver solver = new BrentSolver(f);
     // min=0, max=10, initial=3 => yMin=-5, yMax=5, yInitial=-2
     // yInitial*yMax < 0 triggers interval reduction to [initial, max]
     double result = solver.solve(0.0, 10.0, 3.0);
     assertEquals(5.0, result, 1E-10);
 }

 @Test(expected = IllegalArgumentException.class)
 public void testNoBracketWithInitialThrowsException() throws Exception {
     org.apache.commons.math.analysis.UnivariateRealFunction f =
         new org.apache.commons.math.analysis.UnivariateRealFunction() {
             public double value(double x) { return x - 5.0; }
         };
     BrentSolver solver = new BrentSolver(f);
     // min=0, max=1 both negative => no bracket
     solver.solve(0.0, 1.0, 0.5);
 }

 @Test
 public void testEndpointCloseToZeroSameSignReturnsEndpoint() throws Exception {
     // f is positive and tiny at min, larger at max – no root in interval, but
     // sign>0 and |yMin| <= functionValueAccuracy causes the buggy solver to
     // return min as a “root”.
     org.apache.commons.math.analysis.UnivariateRealFunction f =
         new org.apache.commons.math.analysis.UnivariateRealFunction() {
             public double value(double x) { return x + 1e-10; }
         };
     BrentSolver solver = new BrentSolver(f);
     double result = solver.solve(0.0, 1.0);
     assertEquals(0.0, result, 1E-15);
 }