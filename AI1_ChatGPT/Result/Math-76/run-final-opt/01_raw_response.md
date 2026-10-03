TARGETS: SingularValueDecompositionImpl.getSolver(): solve(double[]), solve(RealVector), solve(RealMatrix), getInverse().
ORACLES: Existing triggers testMath320A/B require zero residual where observed errors are 0.09336767546650937 and 3.2.
CASES: Reproduce testMath320A/B matrices/RHS; assert solver results satisfy expected zero residuals.
CASES: Check vector, array, matrix RHS paths and inverse consistency for the trigger decompositions.
RISKS: Context omits trigger inputs and exact residual expression; derive expected results only from supplied tests.