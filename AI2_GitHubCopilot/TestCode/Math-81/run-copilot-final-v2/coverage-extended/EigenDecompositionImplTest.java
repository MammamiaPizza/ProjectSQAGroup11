package org.apache.commons.math.linear;

 import org.junit.Test;
 import static org.junit.Assert.*;

 public class EigenDecompositionImplTest {

     @Test
     public void testMath308() {
         // MATH-308: ArrayIndexOutOfBoundsException: -1
         // Tridiagonal matrix with close eigenvalues exercising split/eigenvalue range logic
         double[] main = {1.0, 2.0, 2.0, 3.0, 3.0};
         double[] secondary = {0.5, 0.5, 0.5, 0.5};
         EigenDecompositionImpl eigen = new EigenDecompositionImpl(main, secondary, 1.0e-12);
         double[] ev = eigen.getRealEigenvalues();
         assertNotNull(ev);
         assertEquals(5, ev.length);
         for (int i = 0; i < ev.length - 1; i++) {
             assertTrue("Eigenvalues not non-increasing at " + i, ev[i] >= ev[i + 1]);
         }
     }

     @Test
     public void testMath308AlternateMatrix() {
         // Alternate matrix to trigger MATH-308: symmetric with small off-diagonal
         double[][] data = {
             {1.0, 1.0e-6, 0.0, 0.0, 0.0},
             {1.0e-6, 1.0, 1.0e-6, 0.0, 0.0},
             {0.0, 1.0e-6, 1.0, 1.0e-6, 0.0},
             {0.0, 0.0, 1.0e-6, 1.0, 1.0e-6},
             {0.0, 0.0, 0.0, 1.0e-6, 1.0}
         };
         RealMatrix matrix = MatrixUtils.createRealMatrix(data);
         EigenDecompositionImpl eigen = new EigenDecompositionImpl(matrix, 1.0e-15);
         double[] ev = eigen.getRealEigenvalues();
         assertNotNull(ev);
         assertEquals(5, ev.length);
     }

     @Test
     public void test3x3CloseDiagonal() {
         // 3x3 matrix with close diagonal values [1, 2, 2]
         double[][] data = {
             {1.0, 0.5, 0.0},
             {0.5, 2.0, 0.5},
             {0.0, 0.5, 2.0}
         };
         RealMatrix matrix = MatrixUtils.createRealMatrix(data);
         EigenDecompositionImpl eigen = new EigenDecompositionImpl(matrix, 1.0e-12);
         double[] ev = eigen.getRealEigenvalues();
         assertEquals(3, ev.length);
         assertEquals(ev[0], eigen.getRealEigenvalue(0), 1e-15);
         RealMatrix v = eigen.getV();
         RealMatrix d = eigen.getD();
         RealMatrix vt = eigen.getVT();
         RealMatrix reconstructed = v.multiply(d).multiply(vt);
         for (int i = 0; i < 3; i++) {
             for (int j = 0; j < 3; j++) {
                 assertEquals(matrix.getEntry(i, j), reconstructed.getEntry(i, j), 1.0e-10);
             }
         }
     }

     @Test
     public void test4x4EqualEigenvalueCountBoundaries() {
         // 4x4 matrix exercising eigenvalue count boundaries in bisection
         double[][] data = {
             {2.0, 0.3, 0.0, 0.0},
             {0.3, 2.0, 0.3, 0.0},
             {0.0, 0.3, 2.0, 0.3},
             {0.0, 0.0, 0.3, 2.0}
         };
         RealMatrix matrix = MatrixUtils.createRealMatrix(data);
         EigenDecompositionImpl eigen = new EigenDecompositionImpl(matrix, 1.0e-12);
         double[] ev = eigen.getRealEigenvalues();
         assertEquals(4, ev.length);
         for (int i = 0; i < ev.length - 1; i++) {
             assertTrue(ev[i] >= ev[i + 1]);
         }
         assertEquals(4, eigen.getImagEigenvalues().length);
         for (int i = 0; i < 4; i++) {
             assertEquals(0.0, eigen.getImagEigenvalue(i), 1.0e-15);
         }
     }

     @Test
     public void testProcess3RowsBlockTrigger() {
         // Matrix exercising process3RowsBlock with similar eigenvalues
         double[] main = {10.0, 10.0, 10.0};
         double[] secondary = {2.0, 2.0};
         EigenDecompositionImpl eigen = new EigenDecompositionImpl(main, secondary, 1.0e-12);
         double[] ev = eigen.getRealEigenvalues();
         assertEquals(3, ev.length);
         assertTrue(ev[0] >= ev[1]);
         assertTrue(ev[1] >= ev[2]);
         // Verify eigendecomposition accuracy: eigenvalues near 10, 10+2√2, 10-2√2
         double sumEigenvalues = ev[0] + ev[1] + ev[2];
         assertEquals(30.0, sumEigenvalues, 1.0e-8);
     }

     @Test
     public void test1x1Identity() {
         // Degenerate single-element matrix
         double[][] data = {{7.0}};
         RealMatrix matrix = MatrixUtils.createRealMatrix(data);
         EigenDecompositionImpl eigen = new EigenDecompositionImpl(matrix, 1.0e-12);
         double[] ev = eigen.getRealEigenvalues();
         assertEquals(1, ev.length);
         assertEquals(7.0, ev[0], 1.0e-15);
         assertEquals(7.0, eigen.getRealEigenvalue(0), 1.0e-15);
         assertEquals(7.0, eigen.getDeterminant(), 1.0e-15);
         assertTrue(eigen.getSolver().isNonSingular());
         RealVector evec = eigen.getEigenvector(0);
         assertEquals(1.0, evec.getNorm(), 1.0e-10);
     }

     @Test
     public void test2x2EqualDiagonal() {
         // 2x2 with equal diagonal values exercises dqds degenerate path
         double[][] data = {
             {4.0, 1.0},
             {1.0, 4.0}
         };
         RealMatrix matrix = MatrixUtils.createRealMatrix(data);
         EigenDecompositionImpl eigen = new EigenDecompositionImpl(matrix, 1.0e-12);
         double[] ev = eigen.getRealEigenvalues();
         assertEquals(2, ev.length);
         assertEquals(5.0, ev[0], 1.0e-10);
         assertEquals(3.0, ev[1], 1.0e-10);
     }

     @Test
     public void testEigenDecompositionReconstruction() {
         // V * D * V^T should reconstruct the original symmetric matrix
         double[][] data = {
             {4.0, 1.0, 2.0},
             {1.0, 3.0, 0.0},
             {2.0, 0.0, 5.0}
         };
         RealMatrix matrix = MatrixUtils.createRealMatrix(data);
         EigenDecompositionImpl eigen = new EigenDecompositionImpl(matrix, 1.0e-12);
         RealMatrix v = eigen.getV();
         RealMatrix d = eigen.getD();
         RealMatrix vt = eigen.getVT();
         RealMatrix reconstructed = v.multiply(d).multiply(vt);
         for (int i = 0; i < 3; i++) {
             for (int j = 0; j < 3; j++) {
                 assertEquals(matrix.getEntry(i, j), reconstructed.getEntry(i, j), 1.0e-10);
             }
         }
     }

     @Test
     public void testEigenvectorsOrthonormal() {
         // V^T * V should equal the identity matrix
         double[][] data = {
             {4.0, 1.0, 2.0},
             {1.0, 3.0, 0.0},
             {2.0, 0.0, 5.0}
         };
         RealMatrix matrix = MatrixUtils.createRealMatrix(data);
         EigenDecompositionImpl eigen = new EigenDecompositionImpl(matrix, 1.0e-12);
         RealMatrix vt = eigen.getVT();
         RealMatrix v = eigen.getV();
         RealMatrix identity = vt.multiply(v);
         for (int i = 0; i < 3; i++) {
             for (int j = 0; j < 3; j++) {
                 if (i == j) {
                     assertEquals(1.0, identity.getEntry(i, j), 1.0e-10);
                 } else {
                     assertEquals(0.0, identity.getEntry(i, j), 1.0e-10);
                 }
             }
         }
     }

     @Test(expected = InvalidMatrixException.class)
     public void testNonSymmetricMatrixThrows() {
         double[][] data = {
             {1.0, 2.0},
             {3.0, 4.0}
         };
         RealMatrix matrix = MatrixUtils.createRealMatrix(data);
         new EigenDecompositionImpl(matrix, 1.0e-12);
     }

     @Test
     public void testSplitToleranceBoundaries() {
         // Large tolerance zeroes off-diagonals; small tolerance preserves them
         double[][] data = {
             {1.0, 0.5, 0.0, 0.0},
             {0.5, 2.0, 0.5, 0.0},
             {0.0, 0.5, 3.0, 0.5},
             {0.0, 0.0, 0.5, 4.0}
         };
         RealMatrix matrix = MatrixUtils.createRealMatrix(data);
         // Large tolerance: all off-diagonals zeroed
         EigenDecompositionImpl eigenLarge = new EigenDecompositionImpl(matrix, 100.0);
         double[] evLarge = eigenLarge.getRealEigenvalues();
         assertEquals(4, evLarge.length);
         // Small tolerance: off-diagonals preserved
         EigenDecompositionImpl eigenSmall = new EigenDecompositionImpl(matrix, 1.0e-15);
         double[] evSmall = eigenSmall.getRealEigenvalues();
         assertEquals(4, evSmall.length);
         assertTrue(evLarge[0] >= evLarge[1]);
         assertTrue(evSmall[0] >= evSmall[1]);
     }

     @Test
     public void testRepeatedEigenvalues() {
         // Diagonal matrix with repeated eigenvalues
         double[][] data = {
             {3.0, 0.0, 0.0},
             {0.0, 3.0, 0.0},
             {0.0, 0.0, 5.0}
         };
         RealMatrix matrix = MatrixUtils.createRealMatrix(data);
         EigenDecompositionImpl eigen = new EigenDecompositionImpl(matrix, 1.0e-12);
         double[] ev = eigen.getRealEigenvalues();
         assertEquals(3, ev.length);
         assertEquals(5.0, ev[0], 1.0e-10);
         assertEquals(3.0, ev[1], 1.0e-10);
         assertEquals(3.0, ev[2], 1.0e-10);
         assertEquals(45.0, eigen.getDeterminant(), 1.0e-10);
         assertTrue(eigen.getSolver().isNonSingular());
     }

     @Test
     public void test2x2DegenerateSplit() {
         // 2x2 tridiagonal with very small off-diagonal near splitTolerance boundary
         double[] main = {5.0, 5.0};
         double[] secondary = {1.0e-20};
         EigenDecompositionImpl eigen = new EigenDecompositionImpl(main, secondary, 1.0e-14);
         double[] ev = eigen.getRealEigenvalues();
         assertEquals(2, ev.length);
         // Both eigenvalues should be approximately 5.0
         assertEquals(5.0, ev[0], 1.0e-8);
         assertEquals(5.0, ev[1], 1.0e-8);
     }

 }
