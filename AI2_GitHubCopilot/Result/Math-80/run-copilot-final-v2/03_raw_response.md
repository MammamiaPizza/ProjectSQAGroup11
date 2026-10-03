package org.apache.commons.math.linear;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;

 import java.util.Arrays;

 import org.junit.Test;

 /**
  * JUnit tests for {@link EigenDecompositionImpl} focused on the MATH-318 bug
  * (incorrect eigenvalues) and invariants of the eigen decomposition.
  */
 public class EigenDecompositionImplTest {

     private static final double EPS = 1e-8;

     private RealMatrix symmetricMatrix(double[][] data) {
         return MatrixUtils.createRealMatrix(data);
     }

     private void assertVDVT(RealMatrix a, EigenDecompositionImpl ed, double tol) {
         RealMatrix v = ed.getV();
         RealMatrix d = ed.getD();
         RealMatrix vt = ed.getVT();
         RealMatrix vdvt = v.multiply(d).multiply(vt);
         int n = a.getRowDimension();
         for (int i = 0; i < n; i++) {
             for (int j = 0; j < n; j++) {
                 assertEquals("A[" + i + "][" + j + "] = V D V^T", a.getEntry(i, j),
 vdvt.getEntry(i, j), tol);
             }
         }
     }

     @Test(expected = InvalidMatrixException.class)
     public void testNonSymmetricMatrixThrows() {
         double[][] data = {{1, 2}, {3, 4}};
         RealMatrix m = symmetricMatrix(data);
         new EigenDecompositionImpl(m, 1e-12);
     }

     @Test
     public void test2x2Symmetric() {
         double[][] data = {{1, 2}, {2, 3}};
         RealMatrix a = symmetricMatrix(data);
         EigenDecompositionImpl ed = new EigenDecompositionImpl(a,1e-12);

         double[] ev = ed.getRealEigenvalues();
         assertEquals(2, ev.length);

         double traceA = a.getEntry(0, 0) + a.getEntry(1, 1);
         assertEquals(traceA, ev[0] + ev[1], EPS);

         double detA = a.getEntry(0, 0) * a.getEntry(1,1) - a.getEntry(0,1) * a.getEntry(1, 0);
         assertEquals(detA, ed.getDeterminant(), EPS);

         assertVDVT(a, ed, EPS);

         RealVector v0 = ed.getEigenvector(0);
         RealVector av0 = a.operate(v0);
         RealVector lambdaV0 = v0.mapMultiply(ed.getRealEigenvalue(0));
         assertEquals(0.0, av0.subtract(lambdaV0).getNorm(), EPS);
     }

     @Test
     public void test3x3Symmetric() {
         double[][] data = {{7, -2, 0}, {-2, 6, -2}, {0, -2, 5}};
         RealMatrix a = symmetricMatrix(data);
         EigenDecompositionImpl ed = new EigenDecompositionImpl(a, 1e-12);

         double[] ev = ed.getRealEigenvalues();
         assertEquals(3, ev.length);

         double trace = a.getEntry(0, 0) + a.getEntry(1, 1) + a.getEntry(2, 2);
         assertEquals(trace, ev[0] + ev[1] + ev[2], EPS);

         assertEquals(ed.getDeterminant(), ev[0] * ev[1] * ev[2], EPS);

         assertVDVT(a, ed, EPS);

         assertTrue(ed.getSolver().isNonSingular());
     }

     @Test
     public void testRepeatedEigenvalue() {
         double[][] data = {{3, 0, 0}, {0, 3, 0}, {0, 0, 5}};
         RealMatrix a = symmetricMatrix(data);
         EigenDecompositionImpl ed = new EigenDecompositionImpl(a, 1e-12);

         double[] ev = ed.getRealEigenvalues();
         Arrays.sort(ev);
         assertArrayEquals(new double[]{3, 3, 5}, ev, EPS);

         assertEquals(3 * 3 * 5, ed.getDeterminant(), EPS);
         assertVDVT(a, ed, EPS);
     }

     @Test
     public void testSingularMatrix() {
         // genuinely singular symmetric matrix: [[1,0,0],[0,0,0],[0,0,0]]
         double[][] data = {{1, 0, 0}, {0, 0, 0}, {0, 0, 0}};
         RealMatrix a = symmetricMatrix(data);
         EigenDecompositionImpl ed = new EigenDecompositionImpl(a, 1e-12);

         double[] ev = ed.getRealEigenvalues();
         assertEquals(3, ev.length);

         // determinant must be near zero
         assertEquals(0.0, ed.getDeterminant(), EPS);

         // at least one eigenvalue is zero
         boolean hasZero = false;
         for (double v : ev) {
             if (Math.abs(v) < EPS) {
                 hasZero = true;
                 break;
             }
         }
         assertTrue("one eigenvalue should be zero", hasZero);

         assertFalse(ed.getSolver().isNonSingular());
         assertVDVT(a, ed, EPS);
     }

     @Test
     public void testEigenvaluesNearZero() {
         double[][] data = {{1e-8, 1e-9}, {1e-9, 2e-8}};
         RealMatrix a = symmetricMatrix(data);
         EigenDecompositionImpl ed = new EigenDecompositionImpl(a, 1e-12);

         double[] ev = ed.getRealEigenvalues();
         assertEquals(2, ev.length);

         double trace = a.getEntry(0, 0) + a.getEntry(1, 1);
         assertEquals(trace, ev[0] + ev[1], EPS);

         RealMatrix v = ed.getV();
         RealMatrix d = ed.getD();
         RealMatrix vt = ed.getVT();
         RealMatrix vdvt = v.multiply(d).multiply(vt);
         assertEquals(a.getEntry(0, 0), vdvt.getEntry(0, 0), 1e-15);
         assertEquals(a.getEntry(0, 1), vdvt.getEntry(0, 1), 1e-15);
     }

     @Test
     public void testCloseEigenvalues5x5() {
         double[][] data = {
             {10, 2, 0, 0, 0},
             { 2, 9, 2, 0, 0},
             { 0, 2, 8, 2, 0},
             { 0, 0, 2, 7,2},
             { 0, 0, 0, 2,6}
         };
         RealMatrix a = symmetricMatrix(data);
         EigenDecompositionImpl ed = new EigenDecompositionImpl(a, 1e-14);

         double[] ev = ed.getRealEigenvalues();
         assertEquals(5, ev.length);

         double trace = 10 + 9 + 8 + 7 + 6;
         assertEquals(trace, sum(ev), EPS);

         assertVDVT(a, ed, EPS);

         for (int i = 0; i < 3; i++) {
             RealVector vi = ed.getEigenvector(i);
             RealVector avi = a.operate(vi);
             RealVector lambdaVi = vi.mapMultiply(ed.getRealEigenvalue(i));
             assertEquals(0.0, avi.subtract(lambdaVi).getNorm(), EPS);
         }
     }

     @Test
     public void testLargeMatrix6x6() {
         double[][] data = {
             {100,   5,   0,   0,   0,  0},
             {  5, 200,   5,   0,   0,  0},
             {  0,   5, 150,   5,   0,  0},
             {  0,   0,   5, 180,   5,  0},
             {  0,   0,   0,   5, 130,  5},
             {  0,   0,   0,   0,   5,170}
         };
         RealMatrix a = symmetricMatrix(data);
         EigenDecompositionImpl ed = new EigenDecompositionImpl(a, 1e-14);

         double[] ev = ed.getRealEigenvalues();
         assertEquals(6, ev.length);

         double trace = 100 + 200 + 150 + 180 + 130 + 170;
         assertEquals(trace, sum(ev), 1e-6);

         RealMatrix v = ed.getV();
         RealMatrix d = ed.getD();
         RealMatrix vt = ed.getVT();
         RealMatrix vdvt = v.multiply(d).multiply(vt);
         for (int i = 0; i < 6; i++) {
             for (int j = 0; j < 6; j++) {
                 double expected = a.getEntry(i, j);
                 double actual = vdvt.getEntry(i, j);
                 double tol = Math.max(1e-14, Math.abs(expected) * 1e-8);
                 assertEquals("A[" + i + "][" + j + "]", expected, actual, tol);
             }
         }
     }

     @Test
     public void testTridiagonalConstructor() {
         double[] main = {2, 3, 4};
         double[] secondary = {1, 2};
         EigenDecompositionImpl ed = new EigenDecompositionImpl(main, secondary, 1e-12);

         double[] ev = ed.getRealEigenvalues();
         assertEquals(3, ev.length);

         assertEquals(9.0, sum(ev), EPS);

         double[][] data = {{2, 1, 0}, {1, 3, 2}, {0, 2, 4}};
         RealMatrix a = symmetricMatrix(data);
         assertVDVT(a, ed, EPS);
     }

     @Test
     public void testGetDeterminantEqualsProductOfEigenvalues() {
         double[][] data = {{4, 1, 2}, {1, 3, 0}, {2, 0, 5}};
         RealMatrix a = symmetricMatrix(data);
         EigenDecompositionImpl ed = new EigenDecompositionImpl(a, 1e-12);

         double det = ed.getDeterminant();
         double product = 1.0;
         for (double lambda : ed.getRealEigenvalues()) {
             product *= lambda;
         }
         assertEquals(product, det, EPS);
     }

     @Test
     public void testVandVTTransposeConsistency() {
         double[][] data = {{1, 3, 5}, {3, 2, 4}, {5, 4, 6}};
         RealMatrix a = symmetricMatrix(data);
         EigenDecompositionImpl ed = new EigenDecompositionImpl(a, 1e-12);

         RealMatrix v = ed.getV();
         RealMatrix vt = ed.getVT();
         int n = a.getRowDimension();
         for (int i = 0; i < n; i++) {
             for (int j = 0; j < n; j++) {
                 assertEquals(v.getEntry(i, j), vt.getEntry(j, i), 1e-15);
             }
         }
     }

     @Test
     public void testNonSingularCondition() {
         double[][] data = {{1, 0}, {0, 0}};
         RealMatrix a = symmetricMatrix(data);
         EigenDecompositionImpl ed = new EigenDecompositionImpl(a, 1e-12);
         assertFalse(ed.getSolver().isNonSingular());
     }

     private static double sum(double[] arr) {
         double s = 0;
         for (double v : arr) s += v;
         return s;
     }

     private static void assertArrayEquals(double[] expected, double[] actual, double delta) {
         assertEquals("arrays have different lengths", expected.length, actual.length);
         for (int i = 0; i < expected.length; i++) {
             assertEquals("array element [" + i + "]", expected[i], actual[i], delta);
         }
     }
 }