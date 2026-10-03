@Test
public void testInitialGuessWithinFunctionValueAccuracy() throws Exception {
    UnivariateRealFunction f = new UnivariateRealFunction() {
        public double value(double x) {
            if (x == 2.0) return 0.05;
            return x * x - 2.0;
        }
    };
    BrentSolver solver = new BrentSolver(f);
    solver.setFunctionValueAccuracy(0.1);
    double root = solver.solve(0.0, 3.0, 2.0);
    Assert.assertEquals(2.0, root, 1E-10);
}

@Test
public void testMinEndpointWithinFunctionValueAccuracy() throws Exception {
    UnivariateRealFunction f = new UnivariateRealFunction() {
        public double value(double x) {
            if (x == 0.0) return 0.02;
            return x - 0.2; // f(0.5)=0.3, f(1)=0.8
        }
    };
    BrentSolver solver = new BrentSolver(f);
    solver.setFunctionValueAccuracy(0.05);
    double root = solver.solve(0.0, 1.0, 0.5);
    Assert.assertEquals(0.0, root, 1E-10);
}

@Test
public void testMaxEndpointWithinFunctionValueAccuracy() throws Exception {
    UnivariateRealFunction f = new UnivariateRealFunction() {
        public double value(double x) {
            if (x == 1.0) return 0.03;
            return x - 0.2; // f(0.0)=-0.2, f(0.5)=0.3
        }
    };
    BrentSolver solver = new BrentSolver(f);
    solver.setFunctionValueAccuracy(0.05);
    double root = solver.solve(0.0, 1.0, 0.5);
    Assert.assertEquals(1.0, root, 1E-10);
}

@Test(expected = IllegalArgumentException.class)
public void testEndpointsDoNotBracketRoot() throws Exception {
    UnivariateRealFunction f = new UnivariateRealFunction() {
        public double value(double x) {
            return x * x + 1.0;
        }
    };
    BrentSolver solver = new BrentSolver(f);
    solver.setFunctionValueAccuracy(1E-15);
    solver.solve(0.0, 5.0, 2.0);
}