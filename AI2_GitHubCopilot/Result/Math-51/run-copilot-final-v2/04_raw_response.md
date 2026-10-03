@Test
    public void testBelowSideReturnsWhenFunctionValueSmall() {
        RegulaFalsiSolver solver = new RegulaFalsiSolver();
        solver.solve(100, new UnivariateFunction() {
            @Override
            public double value(double x) { return x - 1.0; }
        }, 0.0, 3.0, 1.5, AllowedSolution.BELOW_SIDE);
        assertEquals(1.0, solver.getResult(), 1e-15);
    }

 @Test
 public void testAboveSideReturnsWhenFunctionValueSmall() {
     RegulaFalsiSolver solver = new RegulaFalsiSolver();
     solver.solve(100, new UnivariateFunction() {
         @Override
         public double value(double x) { return x - 1.0; }
     }, 0.0, 3.0, 1.5, AllowedSolution.ABOVE_SIDE);
     assertEquals(1.0, solver.getResult(), 1e-15);
 }

 @Test
 public void testOverloadedSolveWithAllowedSolution() {
     RegulaFalsiSolver solver = new RegulaFalsiSolver();
     solver.solve(100, new UnivariateFunction() {
         @Override
         public double value(double x) { return x - 2.0; }
     }, 0.0, 4.0, AllowedSolution.ANY_SIDE);
     assertEquals(2.0, solver.getResult(), 1e-15);
 }

 @Test
 public void testRegulaFalsiRegulaMethodAtRetainedEndpoint() {
     RegulaFalsiSolver solver = new RegulaFalsiSolver(1e-6, 1e-6);
     // cubic function ensures at least one retained endpoint (Regula Falsi branch)
     double root = solver.solve(100, new UnivariateFunction() {
         @Override
         public double value(double x) { return (x - 1.0) * (x - 1.0) * (x - 1.0); }
     }, -5.0, 2.0, AllowedSolution.ANY_SIDE, 1e-6);
     assertEquals(1.0, root, 1e-6);
 }